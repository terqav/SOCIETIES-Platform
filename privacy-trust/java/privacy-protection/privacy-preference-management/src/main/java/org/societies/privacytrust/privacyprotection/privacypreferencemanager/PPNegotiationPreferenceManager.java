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
package org.societies.privacytrust.privacyprotection.privacypreferencemanager;

import java.util.ArrayList;
import java.util.List;

import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation.PrivateContextCache;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.management.PrivatePreferenceCache;

/**
 * @author Eliza
 *
 */
public class PPNegotiationPreferenceManager {

	private PrivatePreferenceCache prefCache;
	private final PrivateContextCache contextCache;
	
	public PPNegotiationPreferenceManager(PrivatePreferenceCache prefCache, PrivateContextCache contextCache){
		this.prefCache = prefCache;
		this.contextCache = contextCache;
		
		
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#deletePPNPreference(java.lang.String)
	 */
	 
/*	public void deletePPNPreference(String contextType){
		PPNPreferenceDetailsBean details = new PPNPreferenceDetailsBean();
		Resource resource = new Resource();
		resource.setDataType(contextType);
		details.setResource(resource);
		this.prefCache.removePPNPreference(details);
	}*/
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#deletePPNPreference(java.lang.String, org.societies.api.context.model.CtxAttributeIdentifier)
	 */
/*	 
	public void deletePPNPreference(String contextType, CtxAttributeIdentifier affectedCtxID) {
		PPNPreferenceDetailsBean details = new PPNPreferenceDetailsBean(contextType);
		details.setAffectedDataId(affectedCtxID);
		this.prefCache.removePPNPreference(details);

	}*/

	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#deletePPNPreference(org.societies.api.identity.Requestor, java.lang.String, org.societies.api.context.model.CtxAttributeIdentifier)
	 */
	 
/*	public void deletePPNPreference(Requestor requestor, String contextType, CtxAttributeIdentifier affectedCtxID){
		PPNPreferenceDetailsBean details = new PPNPreferenceDetailsBean(contextType);
		details.setAffectedDataId(affectedCtxID);
		details.setRequestor(requestor);
		this.prefCache.removePPNPreference(details);
	}*/
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#deletePPNPreference(org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPreferenceDetailsBean)
	 */
	 
	public void deletePPNPreference(PPNPreferenceDetailsBean details) {
		this.prefCache.removePPNPreference(details);

	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#storePPNPreference(org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPreferenceDetailsBean, org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference)
	 */
	 
	public void storePPNPreference(PPNPreferenceDetailsBean details, IPrivacyPreference preference){

		PPNPrivacyPreferenceTreeModel model = new PPNPrivacyPreferenceTreeModel(details.getDataType(), preference);
		if (details.getAffectedDataId()!=null){
			model.setAffectedDataId(details.getAffectedDataId());
		}
		if (details.getRequestor()!=null){
			model.setRequestor(details.getRequestor());
		}
		this.logging.debug("REquest to add preference :\n"+details.toString());
		this.prefCache.addPPNPreference(details, model);
		this.privacyPCM.updatePreferences(details, preference);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#getPPNPreferenceDetails()
	 */
	 
	public List<PPNPreferenceDetailsBean> getPPNPreferenceDetails() {
		return this.prefCache.getPPNPreferenceDetails();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#getPPNPreferences(java.lang.String)
	 */
	 
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType) {
		return this.prefCache.getPPNPreferences(contextType);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#getPPNPreferences(java.lang.String, org.societies.api.context.model.CtxAttributeIdentifier)
	 */

	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType, CtxAttributeIdentifier ctxID) {
		return this.prefCache.getPPNPreferences(contextType, ctxID);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#getPPNPreferences(org.societies.api.identity.Requestor, java.lang.String)
	 */

	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(Requestor requestor, String contextType) {
		return this.prefCache.getPPNPreferences(contextType, requestor);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#getPPNPreferences(org.societies.api.identity.Requestor, java.lang.String, org.societies.api.context.model.CtxAttributeIdentifier)
	 */

	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(Requestor requestor, String contextType, CtxAttributeIdentifier ctxID) {
		return this.prefCache.getPPNPreferences(contextType, ctxID, requestor);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#getPPNPreference(org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPreferenceDetailsBean)
	 */

	public IPrivacyPreferenceTreeModel getPPNPreference(PPNPreferenceDetailsBean details) {
		return this.prefCache.getPPNPreference(details);
	}
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#evaluatePPNP(org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy)
	 */
	 
	public ResponsePolicy evaluatePPNP(RequestPolicy request){
		//TODO
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#evaluatePPNPreference(java.lang.String)
	 */

	public List<IPrivacyOutcome> evaluatePPNPreference(String contextType) {
		this.logging.debug("Request to evaluate Preferences referring to contextType: "+contextType);
		List<IPrivacyOutcome> outcomes = new ArrayList<IPrivacyOutcome>();
		List<IPrivacyPreferenceTreeModel> models = this.prefCache.getPPNPreferences(contextType);
		this.logging.debug("Found "+models.size()+" preferences referring contextType: "+contextType);
		for (IPrivacyPreferenceTreeModel model : models){
			IPrivacyOutcome outcome = this.evaluatePreference(model.getRootPreference());
			if (outcome!=null){
				outcomes.add(outcome);
			}
		}
		this.logging.debug("Number of applicable preferences: "+outcomes.size());
		return outcomes;
	}
	
	public IPrivacyOutcome evaluatePPNPreference(PPNPreferenceDetailsBean detail){
		IPrivacyPreferenceTreeModel model = this.prefCache.getPPNPreference(detail);
		if (model==null){
			this.logging.debug("Requested evaluation of PPN preference with details: "+detail.toString()+" but preference with these details does not exist");
			return null;
		}
		IPrivacyOutcome outcome = this.evaluatePreference(model.getRootPreference());
		
		return outcome;
	}

	
	

}
