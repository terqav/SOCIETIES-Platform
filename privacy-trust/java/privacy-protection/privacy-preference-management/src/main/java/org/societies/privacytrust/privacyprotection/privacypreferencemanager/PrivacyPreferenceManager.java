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

import java.util.List;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreement;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.dobf.DObfOutcome;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation.PrivateContextCache;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.management.PrivatePreferenceCache;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.monitoring.PrivacyPreferenceConditionMonitor;

/**
 * @author Elizabeth
 *
 */

public class PrivacyPreferenceManager implements IPrivacyPreferenceManager{
	
	private PrivatePreferenceCache prefCache;
	private PrivateContextCache contextCache;
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	private ICtxBroker ctxBroker;

	private ITrustBroker trustBroker;
	
	private PrivacyPreferenceConditionMonitor privacyPCM;
	
	private IPrivacyDataManagerInternal privacyDataManagerInternal;
	
	private IIdentityManager idm;
	
	private ICommManager commsMgr;
	
	private boolean test = false;
	
	private MessageBox myMessageBox;
	
	private IUserFeedback userFeedback;
	
	
	public PrivacyPreferenceManager(){
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        UIManager.put("ClassLoader", ClassLoader.getSystemClassLoader());
		if (this.myMessageBox==null){
			myMessageBox = new MessageBox();
		}
	}
	

	public void initialisePrivacyPreferenceManager(ICtxBroker ctxBroker, ITrustBroker trustBroker){
		this.setCtxBroker(ctxBroker);
		this.trustBroker = trustBroker;
		this.privacyPCM = new PrivacyPreferenceConditionMonitor(ctxBroker, this, privacyDataManagerInternal, idm);
		prefCache = new PrivatePreferenceCache(ctxBroker, this.idm);
		contextCache = new PrivateContextCache(ctxBroker);
		if (this.myMessageBox==null){
			myMessageBox = new MessageBox();
		}
	}
	
	public void initialisePrivacyPreferenceManager(){
		prefCache = new PrivatePreferenceCache(ctxBroker, this.idm);
		contextCache = new PrivateContextCache(ctxBroker);
		this.privacyPCM = new PrivacyPreferenceConditionMonitor(ctxBroker, this, getprivacyDataManagerInternal(), idm);
		contextCache = new PrivateContextCache(ctxBroker);
		if (this.myMessageBox==null){
			myMessageBox = new MessageBox();
		}
	}
	
	/**
	 * @return the ctxBroker
	 */
	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	/**
	 * @param ctxBroker the ctxBroker to set
	 */
	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}

	/**
	 * @return the trustBroker
	 */
	public ITrustBroker getTrustBroker() {
		return trustBroker;
	}
	/**
	 * @param trustBroker the trustBroker to set
	 */
	public void setTrustBroker(ITrustBroker trustBroker) {
		this.trustBroker = trustBroker;
	}
	


	

	


	/**
	 * @return the privacyDataManager
	 */
	public IPrivacyDataManagerInternal getprivacyDataManagerInternal() {
		return privacyDataManagerInternal;
	}


	/**
	 * @param privacyDataManagerInternal the privacyDataManager to set
	 */
	public void setprivacyDataManagerInternal(IPrivacyDataManagerInternal privacyDataManagerInternal) {
		this.privacyDataManagerInternal = privacyDataManagerInternal;
	}





	/**
	 * @return the test
	 */
	public boolean isTest() {
		return test;
	}


	/**
	 * @param test the test to set
	 */
	public void setTest(boolean test) {
		this.test = test;
	}


	/**
	 * @return the myMessageBox
	 */
	public MessageBox getMyMessageBox() {
		return myMessageBox;
	}


	/**
	 * @param myMessageBox the myMessageBox to set
	 */
	public void setMyMessageBox(MessageBox myMessageBox) {
		this.myMessageBox = myMessageBox;
	}


	/**
	 * @return the commsMgr
	 */
	public ICommManager getCommsMgr() {
		return commsMgr;
	}


	/**
	 * @param commsMgr the commsMgr to set
	 */
	public void setCommsMgr(ICommManager commsMgr) {
		this.commsMgr = commsMgr;
		this.idm = commsMgr.getIdManager();
	}

	
	public PrivacyPreferenceConditionMonitor getPCM(){
		return privacyPCM;
	}
	
	public PrivateContextCache getContextCache(){
		return this.contextCache;
	}


	/**
	 * @return the userFeedback
	 */
	public IUserFeedback getUserFeedback() {
		return userFeedback;
	}


	/**
	 * @param userFeedback the userFeedback to set
	 */
	public void setUserFeedback(IUserFeedback userFeedback) {
		this.userFeedback = userFeedback;
	}

	private AccessControlPreferenceManager getAccessControlPreferenceManager(){
		AccessControlPreferenceManager  accCtrlMgr = new AccessControlPreferenceManager(prefCache, contextCache, userFeedback, trustBroker);
		
		return accCtrlMgr;
	}
	
	private PPNegotiationPreferenceManager getPPNegotiationPreferenceManager(){
		PPNegotiationPreferenceManager ppnMgr = new PPNegotiationPreferenceManager(prefCache, contextCache);
		return ppnMgr;
	}
	

	private DObfPrivacyPreferenceManager getDObfPreferenceManager(){
		DObfPrivacyPreferenceManager dobfMgr = new DObfPrivacyPreferenceManager(prefCache, contextCache);
		return dobfMgr;
	}
	
	private IDSPreferenceManager getIDSPreferenceManager(){
		IDSPreferenceManager idsMgr = new IDSPreferenceManager(prefCache, contextCache);
		return idsMgr;
	}
	@Override
	public ResponseItem checkPermission(Requestor requestor, DataIdentifier dataId,
			List<Action> actions) throws PrivacyException {
		// TODO Auto-generated method stub
		AccessControlPreferenceManager  accCtrlMgr = getAccessControlPreferenceManager();
		return accCtrlMgr.checkPermission(requestor, dataId, actions);
	}


	@Override
	public void deleteIDSPreference(IDSPreferenceDetailsBean details) {
		IDSPreferenceManager idsMgr = getIDSPreferenceManager();
		idsMgr.deleteIDSPreference(details);
	}


	@Override
	public void deleteIDSPreference(IIdentity affectedIdentity) {
		IDSPreferenceManager idsMgr = getIDSPreferenceManager();
		idsMgr.deleteIDSPreference(affectedIdentity);
		
	}


	@Override
	public void deleteIDSPreference(Requestor requestor, IIdentity affectedIdentity) {
		IDSPreferenceManager idsMgr = getIDSPreferenceManager();
		idsMgr.deleteIDSPreference(requestor, affectedIdentity);
	}


	@Override
	public void deletePPNPreference(String dataType) {
		PPNegotiationPreferenceManager ppnMgr = getPPNegotiationPreferenceManager();
		ppnMgr.deletePPNPreference(dataType);
	}


	@Override
	public void deletePPNPreference(PPNPreferenceDetailsBean details) {
		PPNegotiationPreferenceManager ppnMgr = getPPNegotiationPreferenceManager();
		ppnMgr.deletePPNPreference(details);
	}


	@Override
	public void deletePPNPreference(String dataType, CtxAttributeIdentifier ctxID) {
		PPNegotiationPreferenceManager ppnMgr = getPPNegotiationPreferenceManager();
		ppnMgr.deletePPNPreference(dataType, ctxID);
	}


	@Override
	public void deletePPNPreference(Requestor requestor, String dataType,
			CtxAttributeIdentifier ctxID) {
		PPNegotiationPreferenceManager ppnMgr = getPPNegotiationPreferenceManager();
		ppnMgr.deletePPNPreference(requestor, dataType, ctxID);
	}


	@Override
	public DObfOutcome evaluateDObfOutcome(CtxIdentifier ctxID) {
		DObfPrivacyPreferenceManager dobfMgr = getDObfPreferenceManager();
		return dobfMgr.evaluateDObfOutcome(ctxID);
	}


	@Override
	public DObfOutcome evaluateDObfPreference(Requestor requestor, String dataType) {
		DObfPrivacyPreferenceManager dobfMgr = getDObfPreferenceManager();
		return dobfMgr.evaluateDObfPreference(requestor, dataType);
	}


	@Override
	public IIdentity evaluateIDSPreference(IDSPreferenceDetailsBean details) {
		IDSPreferenceManager idsMgr = getIDSPreferenceManager();
		return idsMgr.evaluateIDSPreference(details);
	}


	@Override
	public IIdentity evaluateIDSPreferences(IAgreement agreement,
			List<IIdentity> identities) {
		IDSPreferenceManager idsMgr = getIDSPreferenceManager();
		return idsMgr.evaluateIDSPreferences(agreement, identities);
	}


	@Override
	public IIdentity evaluateIdSPreference(Requestor requestor) {
		IDSPreferenceManager idsMgr = getIDSPreferenceManager();
		return idsMgr.evaluateIdSPreference(requestor);
	}


	@Override
	public ResponsePolicy evaluatePPNP(RequestPolicy policy) {
		PPNegotiationPreferenceManager ppnMgr = getPPNegotiationPreferenceManager();
		return ppnMgr.evaluatePPNP(policy);
	}


	@Override
	public List<IPrivacyOutcome> evaluatePPNPreference(String contextType) {
		PPNegotiationPreferenceManager ppnMgr = getPPNegotiationPreferenceManager();
		return ppnMgr.evaluatePPNPreference(contextType);
	}


	@Override
	public IPrivacyPreferenceTreeModel getDObfPreference(
			DObfPreferenceDetailsBean details) {
		DObfPrivacyPreferenceManager dobfMgr = getDObfPreferenceManager();
		return dobfMgr.getDObfPreference(details);
	}


	@Override
	public List<DObfPreferenceDetailsBean> getDObfPreferences() {
		DObfPrivacyPreferenceManager dobfMgr = getDObfPreferenceManager();
		return dobfMgr.getDObfPreferences();
	}


	@Override
	public IPrivacyPreferenceTreeModel getIDSPreference(
			IDSPreferenceDetailsBean details) {
		IDSPreferenceManager idsMgr = getIDSPreferenceManager();
		
		return idsMgr.getIDSPreference(details);
	}


	@Override
	public List<IDSPreferenceDetailsBean> getIDSPreferenceDetails() {
		IDSPreferenceManager idsMgr = getIDSPreferenceManager();
		
		return idsMgr.getIDSPreferenceDetails();
	}


	@Override
	public List<IPrivacyPreferenceTreeModel> getIDSPreferences(IIdentity affectedIdentity) {
		IDSPreferenceManager idsMgr = getIDSPreferenceManager();
		
		return idsMgr.getIDSPreferences(affectedIdentity);
	}


	@Override
	public List<IPrivacyPreferenceTreeModel> getIDSPreferences(Requestor requestor,
			IIdentity affectedIdentity) {
		IDSPreferenceManager idsMgr = getIDSPreferenceManager();
		return idsMgr.getIDSPreferences(requestor, affectedIdentity);
	}


	@Override
	public IPrivacyPreferenceTreeModel getPPNPreference(
			PPNPreferenceDetailsBean details) {
		PPNegotiationPreferenceManager ppnMgr = getPPNegotiationPreferenceManager();
		return ppnMgr.getPPNPreference(details);
	}


	@Override
	public List<PPNPreferenceDetailsBean> getPPNPreferenceDetails() {
		PPNegotiationPreferenceManager ppnMgr = getPPNegotiationPreferenceManager();
		return ppnMgr.getPPNPreferenceDetails();
	}


	@Override
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType) {
		PPNegotiationPreferenceManager ppnMgr = getPPNegotiationPreferenceManager();
		return ppnMgr.getPPNPreferences(contextType);
	}


	@Override
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType,
			CtxAttributeIdentifier ctxID) {
		PPNegotiationPreferenceManager ppnMgr = getPPNegotiationPreferenceManager();

		return ppnMgr.getPPNPreferences(contextType, ctxID);
	}


	@Override
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(Requestor requestor,
			String contextType) {
		PPNegotiationPreferenceManager ppnMgr = getPPNegotiationPreferenceManager();
		return ppnMgr.getPPNPreferences(requestor, contextType);
	}


	@Override
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(Requestor requestor,
			String contextType, CtxAttributeIdentifier ctxID) {
		// TODO Auto-generated method stub
		PPNegotiationPreferenceManager ppnMgr = getPPNegotiationPreferenceManager();

		return ppnMgr.getPPNPreferences(requestor, contextType, ctxID);
	}


	@Override
	public void storeIDSPreference(IDSPreferenceDetailsBean details,
			IPrivacyPreference preference) {
		IDSPreferenceManager idsMgr = getIDSPreferenceManager();
		idsMgr.storeIDSPreference(details, preference);
		
	}


	@Override
	public void storePPNPreference(PPNPreferenceDetailsBean details,
			IPrivacyPreference preference) {
		PPNegotiationPreferenceManager ppnMgr = getPPNegotiationPreferenceManager();
		ppnMgr.storePPNPreference(details, preference);
		
	}

}

