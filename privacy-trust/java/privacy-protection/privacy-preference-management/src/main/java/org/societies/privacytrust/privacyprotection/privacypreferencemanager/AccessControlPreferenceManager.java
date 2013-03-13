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

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RuleTarget;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyOutcomeConstantsBean;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyOutcomeConstants;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation.PreferenceEvaluator;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation.PrivateContextCache;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.management.PrivatePreferenceCache;

/**
 * @author Eliza
 *
 */
public class AccessControlPreferenceManager {

	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private final PrivatePreferenceCache prefCache;
	private final PrivateContextCache contextCache;
	private final IUserFeedback userFeedback;
	private final ITrustBroker trustBroker;

	public AccessControlPreferenceManager(PrivatePreferenceCache prefCache, PrivateContextCache contextCache, IUserFeedback userFeedback, ITrustBroker trustBroker){
		this.prefCache = prefCache;
		this.contextCache = contextCache;
		this.userFeedback = userFeedback;
		this.trustBroker = trustBroker;
		
	}
	
	/* 
	 ******* PRIVATE METHODS BELOW ***************
	 */
	
	
	private ResponseItem checkPreferenceForAccessControl(IPrivacyPreferenceTreeModel model, Requestor requestor, DataIdentifier dataId, List<Condition> conditions, List<Action> actions){
		this.logging.debug("Evaluating preference");
		IPrivacyOutcome outcome = this.evaluatePreference(model.getRootPreference());
		
		String actionList = "";
		for (Action a : actions){
			actionList = actionList.concat(a.toString());
		}
		if (null==outcome){
			this.logging.debug("Evaluation returned no result. Asking the user: "+dataId.getType());
			
			String allow = "Allow";
			String deny = "Deny";
			
			
			List<String> response = new ArrayList<String>();
			
			String proposalText = requestor.getRequestorId().toString()+" is requesting access to: \n"
					+ "resource:"+dataId.getType()+"\n("+dataId.getUri()+")\nto perform a "+actionList+" operation.";
			try {
				response = this.userFeedback.getExplicitFB(ExpProposalType.ACKNACK, new ExpProposalContent(proposalText, new String[]{allow,deny})).get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (response.contains(allow)){
				this.askToStoreDecision(requestor, dataId, conditions, actions,  PrivacyOutcomeConstants.ALLOW);
				return this.createResponseItem(requestor, dataId, actions, conditions, Decision.PERMIT);
			}else{
				this.askToStoreDecision(requestor, dataId, conditions, actions, PrivacyOutcomeConstants.BLOCK);
				return this.createResponseItem(requestor, dataId, actions, conditions, Decision.DENY);
			}
			/*int n = myMessageBox.showConfirmDialog(requestor.getRequestorId().toString()+" is requesting access to: \n"
					+ "resource:"+dataId.getType()+"\n("+dataId.getUri()+")\nto perform a "+actionList+" operation. \nAllow?", "Access request", JOptionPane.YES_NO_OPTION);
			if (n==JOptionPane.YES_OPTION){
				this.askToStoreDecision(requestor, dataId, conditions, actions,  PrivacyOutcomeConstants.ALLOW);
				return this.createResponseItem(requestor, dataId, actions, conditions, Decision.PERMIT);
			}else{
				this.askToStoreDecision(requestor, dataId, conditions, actions, PrivacyOutcomeConstants.BLOCK);
				return this.createResponseItem(requestor, dataId, actions, conditions, Decision.DENY);
			}*/
		}else{
			if (((AccessControlOutcome) outcome).getEffect()==PrivacyOutcomeConstantsBean.ALLOW){
				this.logging.debug("Returning PERMIT decision for resource: "+dataId.getUri());
				return this.createResponseItem(requestor, dataId, actions, conditions, Decision.PERMIT);
			}
			this.logging.debug("Returning DENY decision for resource: "+dataId.getUri());
			return this.createResponseItem(requestor, dataId, actions, conditions, Decision.DENY);
		}
	}
	
	private IPrivacyOutcome evaluatePreference(IPrivacyPreference privPref){
		PreferenceEvaluator ppE = new PreferenceEvaluator(this.contextCache, trustBroker);
		Hashtable<IPrivacyOutcome, List<CtxIdentifier>> results = ppE.evaluatePreference(privPref);
		Enumeration<IPrivacyOutcome> outcomes = results.keys();
		if (outcomes.hasMoreElements()){
			return outcomes.nextElement();
		}

		return null;

	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#checkPermission(org.societies.api.identity.Requestor, org.societies.api.context.model.CtxAttributeIdentifier, java.util.List)
	 */
	public ResponseItem checkPermission(Requestor requestor, DataIdentifier dataId, List<Action> actions) throws PrivacyException{
		
		if (null==dataId){
			this.logging.debug("requested permission for null CtxIdentifier. returning : null");
			return null;
			
		}
		this.logging.debug("checkPermission: \nRequestor: "+requestor.toString()+"\nctxId: "+dataId.getUri()+"\n and actions...");
		
		String actionList = "";
		for (Action a : actions){
			actionList = actionList.concat(a.toString());
		}
		List<Condition> conditions = new ArrayList<Condition>();
		AccessControlPreferenceDetailsBean details = new AccessControlPreferenceDetailsBean(dataId.getType());
		details.setAffectedDataId(dataId);
		details.setRequestor(requestor);
		IPrivacyPreferenceTreeModel model = prefCache.getPPNPreference(details);
		if (model!=null){
			this.logging.debug("Preference for specific request found");
			return this.checkPreferenceForAccessControl(model, requestor, dataId, conditions, actions);
		}

		this.logging.debug("Preference for specific request NOT found");
		details = new AccessControlPreferenceDetailsBean(dataId.getType());
		details.setRequestor(requestor);
		model = this.prefCache.getPPNPreference(details);
		if (model!=null){
			this.logging.debug("Preference found specific to type and requestor but not for ctxId");
			return this.checkPreferenceForAccessControl(model, requestor, dataId, conditions, actions);
		}		

		details = new AccessControlPreferenceDetailsBean(dataId.getType());
		details.setAffectedDataId(dataId);
		model = this.prefCache.getPPNPreference(details);
		if (model!=null){
			this.logging.debug("Preference found specific to ctxId but not for requestor");
			return this.checkPreferenceForAccessControl(model, requestor, dataId, conditions, actions);
		}

		details = new AccessControlPreferenceDetailsBean(dataId.getType());
		model = this.prefCache.getPPNPreference(details);
		if (model!=null){
			this.logging.debug("Preference found specific to type  but not for ctxId or requestor");
			return this.checkPreferenceForAccessControl(model, requestor, dataId, conditions, actions);
		}
		
		String allow  = "Allow";
		String deny = "Deny";
		String proposalText = requestor.getRequestorId().toString()+" is requesting access to: \n"
				+ "resource:"+dataId.getType()+"\n("+dataId.getUri()+")\nto perform a "+actionList+" operation. \nAllow?";
		List<String> response = new ArrayList<String>();
		try {
			response = this.userFeedback.getExplicitFB(ExpProposalType.ACKNACK, new ExpProposalContent(proposalText, new String[]{allow,deny})).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/* int n = myMessageBox.showConfirmDialog(requestor.getRequestorId().toString()+" is requesting access to: \n"
				+ "resource:"+dataId.getType()+"\n("+dataId.getUri()+")\nto perform a "+actionList+" operation. \nAllow?", "Access request", JOptionPane.YES_NO_OPTION);
		
		if (n==JOptionPane.YES_OPTION){
			this.askToStoreDecision(requestor, dataId, conditions, actions, PrivacyOutcomeConstants.ALLOW);
			return this.createResponseItem(requestor, dataId, actions, conditions, Decision.PERMIT);
		}else{
			this.askToStoreDecision(requestor, dataId, conditions, actions, PrivacyOutcomeConstants.BLOCK);
			return this.createResponseItem(requestor, dataId, actions, conditions, Decision.DENY);
		}*/
		
		if (response.contains(allow))
		{
			this.askToStoreDecision(requestor, dataId, conditions, actions, PrivacyOutcomeConstants.ALLOW);
			return this.createResponseItem(requestor, dataId, actions, conditions, Decision.PERMIT);
		}else{
			this.askToStoreDecision(requestor, dataId, conditions, actions, PrivacyOutcomeConstants.BLOCK);
			return this.createResponseItem(requestor, dataId, actions, conditions, Decision.DENY);
		}
	}
	
	private void askToStoreDecision(Requestor requestor, DataIdentifier dataId, List<Condition> conditions,List<Action> actions,  PrivacyOutcomeConstants decision){
		
		//int n = myMessageBox.showConfirmDialog("Do you want to store this decision permanently?", "Access request", JOptionPane.YES_NO_OPTION);
		//if (n==JOptionPane.YES_OPTION){
			
			Resource r = new Resource(dataId);
			List<Requestor> requestors = new ArrayList<Requestor>();
			requestors.add(requestor);
			RuleTarget ruleTarget = new RuleTarget(requestors, r, actions);
			try {
				PPNPOutcome outcome = new PPNPOutcome(decision, ruleTarget, new ArrayList<Condition>());
				PrivacyPreference pref = new PrivacyPreference(outcome); 
				PPNPrivacyPreferenceTreeModel model = new PPNPrivacyPreferenceTreeModel(dataId.getType(), pref);
				model.setAffectedDataId(dataId);
				model.setRequestor(requestor);
				AccessControlPreferenceDetailsBean detailsBean = new AccessControlPreferenceDetailsBean();
				detailsBean.setRequestor(value)
				detailsBean.setAffectedDataId(dataId);
				detailsBean.setRequestor(requestor);
				this.prefCache.addPPNPreference(detailsBean, model);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//}
	}
	
	private ResponseItem createResponseItem(Requestor requestor, DataIdentifier dataId, List<Action> actions, List<Condition> conditions, Decision decision){
		
	/*
	 * sfina: otan to request einai gia create, prosthetoume WRITE, DELETE kai READ
	 * sfina2: otan to request einai gia write, prosthetoume READ
	 */
	actions = this.adjustActions(actions);
	/*
	 * telos sfinas
	 */
		RequestItem reqItem = new RequestItem(new Resource(dataId), actions, conditions);

		ResponseItem respItem = new ResponseItem(reqItem, decision);
		
		
		return respItem;
	}
	
	private List<Action> adjustActions(List<Action> actions){
		boolean hasCreate = false;
		boolean hasWrite = false;
		boolean hasDelete = false;
		for (Action a: actions){
			if (a.getActionType().equals(ActionConstants.CREATE)){
				hasCreate = true;
			}
			if (a.getActionType().equals(ActionConstants.WRITE)){
				hasWrite = true;
			}
			
			if (a.getActionType().equals(ActionConstants.DELETE)){
				hasDelete = true;
			}
		}
		if (hasCreate){
			actions = new ArrayList<Action>();
			actions.add(new Action(ActionConstants.CREATE));
			actions.add(new Action(ActionConstants.WRITE));
			actions.add(new Action(ActionConstants.READ));
			actions.add(new Action(ActionConstants.DELETE));
		} else 	if (hasWrite){
			actions = new ArrayList<Action>();
			actions.add(new Action(ActionConstants.WRITE));
			actions.add(new Action(ActionConstants.READ));
			
			if (hasDelete){
				actions.add(new Action(ActionConstants.DELETE));
			}
		}
		return actions;
	}
}
