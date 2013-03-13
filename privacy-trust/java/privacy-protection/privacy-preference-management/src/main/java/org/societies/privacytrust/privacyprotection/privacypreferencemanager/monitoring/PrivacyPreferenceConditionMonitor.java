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
package org.societies.privacytrust.privacyprotection.privacypreferencemanager.monitoring;

import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceConditionMonitor;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPreferenceDetails;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.monitoring.dobf.DObfMonitor;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.monitoring.ids.IDSMonitor;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.monitoring.ppnp.PPNMonitor;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class PrivacyPreferenceConditionMonitor implements IPrivacyPreferenceConditionMonitor{

	private ICtxBroker ctxBroker;
	private PPNMonitor ppnMonitor;
	private DObfMonitor dobfMonitor;
	private IDSMonitor idsMonitor;
	private PrivacyPreferenceManager privPrefMgr;
	private IIdentityManager idm;
	private IPrivacyDataManagerInternal privacyDataManager;
	private IIdentity userIdentity;
	
	public PrivacyPreferenceConditionMonitor(ICtxBroker ctxBroker, PrivacyPreferenceManager privPrefMgr, IPrivacyDataManagerInternal privDataManager, IIdentityManager idm){
		this.ctxBroker = ctxBroker;
		this.privacyDataManager = privDataManager;
		this.idm = idm;
		userIdentity = idm.getThisNetworkNode();
		ppnMonitor = new PPNMonitor(userIdentity, privPrefMgr, ctxBroker, privacyDataManager);
		dobfMonitor = new DObfMonitor(userIdentity, privPrefMgr, ctxBroker);
		idsMonitor = new IDSMonitor(userIdentity, ctxBroker, privPrefMgr);
		
		
	}
	
	
	@Override
	public void contextEventReceived(CtxAttributeIdentifier arg0, IIdentity arg1) {
		// TODO Auto-generated method stub
		
	}


	public void updatePreferences(PPNPreferenceDetails details,
			IPrivacyPreference preference) {
		this.ppnMonitor.updateDetails(details, preference);
		
	}


	/**
	 * @return the ppnMonitor
	 */
	public PPNMonitor getPpnMonitor() {
		return ppnMonitor;
	}


	/**
	 * @param ppnMonitor the ppnMonitor to set
	 */
	public void setPpnMonitor(PPNMonitor ppnMonitor) {
		this.ppnMonitor = ppnMonitor;
	}

}
