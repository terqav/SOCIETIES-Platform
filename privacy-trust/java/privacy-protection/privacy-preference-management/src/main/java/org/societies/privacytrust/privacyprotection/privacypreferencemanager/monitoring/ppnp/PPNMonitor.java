/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.privacytrust.privacyprotection.privacypreferencemanager.monitoring.ppnp;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JOptionPane;

import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPreferenceDetails;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyOutcomeConstants;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation.PreferenceConditionExtractor;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class PPNMonitor implements CtxChangeEventListener {

	private final PrivacyPreferenceManager privPrefMgr;
	Hashtable<CtxIdentifier, List<PPNPreferenceDetails>> monitoring = new Hashtable<CtxIdentifier, List<PPNPreferenceDetails>>();
	private final ICtxBroker ctxBroker;
	private final IPrivacyDataManagerInternal privDataManager;
	private final IIdentity userIdentity;

	public PPNMonitor(IIdentity userIdentity, PrivacyPreferenceManager privPrefMgr, ICtxBroker ctxBroker, IPrivacyDataManagerInternal privacyDataManager) {
		this.privPrefMgr = privPrefMgr;
		this.ctxBroker = ctxBroker;
		this.privDataManager = privacyDataManager;
		this.userIdentity = userIdentity;
		loadPreferenceDetails();
		registerForEvents();
		
	}

	private void registerForEvents() {
		Enumeration<CtxIdentifier> ctxIdEnum = monitoring.keys();
		
		while(ctxIdEnum.hasMoreElements()){
			try {
				ctxBroker.registerForChanges(this, ctxIdEnum.nextElement());
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	private void loadPreferenceDetails(){
		List<PPNPreferenceDetails> details = privPrefMgr.getPPNPreferenceDetails();

		for (PPNPreferenceDetails detail: details){
			PreferenceConditionExtractor extractor = new PreferenceConditionExtractor();
			IPrivacyPreferenceTreeModel model = privPrefMgr.getPPNPreference(detail);
			if (null!=model){
				List<CtxIdentifier> ctxIds = extractor.extractConditions(model);
				for (CtxIdentifier ctxId: ctxIds){
					if (monitoring.containsKey(ctxId)){
						monitoring.get(ctxId).add(detail);
					}else{
						ArrayList<PPNPreferenceDetails> list = new ArrayList<PPNPreferenceDetails>();
						list.add(detail);
						monitoring.put(ctxId, list);

					}
				}
			}

		}
	}

	
	private ResponseItem createResponseItem(PPNPreferenceDetails detail,
			PPNPOutcome outcome) {
		
		RequestItem requestItem = new RequestItem(
				outcome.getRuleTarget().getResource(),
				outcome.getRuleTarget().getActions(),
				outcome.getConditions()); 
		if (outcome.getEffect().equals(PrivacyOutcomeConstants.ALLOW)){
			return new ResponseItem(requestItem, Decision.PERMIT);
		}else{
			return new ResponseItem(requestItem, Decision.DENY);
		}
	}
	
	/**
	 *  CtxChangeEventListener methods
	 */

	@Override
	public void onModification(CtxChangeEvent event) {
		CtxIdentifier ctxId = event.getId();
		
		if (this.monitoring.containsKey(ctxId)){
			List<PPNPreferenceDetails> details = this.monitoring.get(ctxId);
			for (PPNPreferenceDetails detail : details){
				IPrivacyOutcome outcome = this.privPrefMgr.evaluatePPNPreference(detail);
				if (null!=outcome){
					if (outcome instanceof PPNPOutcome){
						ResponseItem responseItem = this.createResponseItem(detail, (PPNPOutcome) outcome);
						List<Requestor> requestors = ((PPNPOutcome) outcome).getRuleTarget().getRequestors();
						for (Requestor requestor : requestors){
							try {
								this.privDataManager.updatePermission(requestor, responseItem);
							} catch (PrivacyException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
					}
				}
			}
		
		
		}
		
	}


	@Override
	public void onCreation(CtxChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onRemoval(CtxChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdate(CtxChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void updateDetails(PPNPreferenceDetails detail,
			IPrivacyPreference preference) {
		PreferenceConditionExtractor extractor = new PreferenceConditionExtractor();

		List<CtxIdentifier> ctxIds = extractor.extractConditions(preference);
		for (CtxIdentifier ctxId: ctxIds){
			if (monitoring.containsKey(ctxId)){
				monitoring.get(ctxId).add(detail);
			}else{
				ArrayList<PPNPreferenceDetails> list = new ArrayList<PPNPreferenceDetails>();
				list.add(detail);
				monitoring.put(ctxId, list);

			}
		}
		
	}

}
