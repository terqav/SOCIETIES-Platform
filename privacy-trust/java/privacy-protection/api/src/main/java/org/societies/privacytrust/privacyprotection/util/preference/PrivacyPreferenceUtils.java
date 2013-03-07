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
package org.societies.privacytrust.privacyprotection.util.preference;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxModelBeanTranslator;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RuleTarget;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.ContextPreferenceConditionBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPreferenceBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPrivacyPreferenceTreeModelBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IdentitySelectionPreferenceOutcomeBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.OperatorConstantsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPOutcomeBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPrivacyPreferenceTreeModelBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyConditionConstantsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyOutcomeConstantsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyPreferenceConditionBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyPreferenceTypeConstantsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.RuleTargetBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.TrustPreferenceConditionBean;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ConditionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestorUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.privacytrust.trust.model.MalformedTrustedEntityIdException;
import org.societies.api.privacytrust.trust.model.TrustModelBeanTranslator;
import org.societies.api.schema.context.model.CtxAttributeIdentifierBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ContextPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.TrustPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.OperatorConstants;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyOutcomeConstants;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyPreferenceTypeConstants;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IDSPreferenceDetails;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IDSPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IdentitySelectionPreferenceOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPreferenceDetails;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPrivacyPreferenceTreeModel;

/**
 * @author Eliza
 *
 */
public class PrivacyPreferenceUtils {

	
	
	/*
	 * FROM BEAN TO OBJECT METHODS
	 */
	
	
	public static PPNPrivacyPreferenceTreeModel toPPNPrivacyPreferenceTreeModel(PPNPrivacyPreferenceTreeModelBean bean, IIdentityManager idMgr) throws InvalidFormatException, URISyntaxException{

		PPNPreferenceDetails details = new PPNPreferenceDetails(bean.getDetails().getResource(), RequestorUtils.toRequestor(bean.getDetails().getRequestor(), idMgr), bean.getDetails().getAction());
		return new PPNPrivacyPreferenceTreeModel(details, toPPNPrivacyPreference(bean.getPref(), idMgr));
	}
	
	public static PrivacyPreference toPPNPrivacyPreference(PPNPreferenceBean bean, IIdentityManager idMgr) throws URISyntaxException{
		
		
		if (bean.getCondition()!=null){
			PrivacyPreference preference = new PrivacyPreference(toPrivacyPreferenceCondition(bean.getCondition()));
			List<PPNPreferenceBean> beans = bean.getChildren();
			
			for (PPNPreferenceBean b : beans){
				preference.add(toPPNPrivacyPreference(b, idMgr));
			}
			
			return preference;
		}
		
		if (bean.getOutcome()!=null){
			
			return new PrivacyPreference(toPPNOutcome(bean.getOutcome(), idMgr));
		}
		
		PrivacyPreference preference = new PrivacyPreference();
		List<PPNPreferenceBean> beans = bean.getChildren();
		
		for (PPNPreferenceBean b : beans){
			preference.add(toPPNPrivacyPreference(b, idMgr));
		}
		
		return preference;
		
		
	}

	private static IPrivacyOutcome toPPNOutcome(
			PPNPOutcomeBean bean, IIdentityManager idMgr) throws URISyntaxException {
			return new PPNPOutcome(toPrivacyOutcomeConstant(bean.getEffect()));
	
		
	}

	private static RuleTarget toRuleTarget(RuleTargetBean bean, IIdentityManager idMgr) {
		try {
			return new RuleTarget(RequestorUtils.toRequestors(bean.getSubjects(), idMgr), ResourceUtils.toResource(bean.getResource()), ActionUtils.toActions(bean.getActions()));
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new RuleTarget(new ArrayList<Requestor>(),ResourceUtils.toResource(bean.getResource()), ActionUtils.toActions(bean.getActions()));
		}
		
	}


	private static PrivacyOutcomeConstants toPrivacyOutcomeConstant(
			PrivacyOutcomeConstantsBean bean) {
		if (bean.compareTo(PrivacyOutcomeConstantsBean.ALLOW)==0){
			return PrivacyOutcomeConstants.ALLOW;
		}else{
			return PrivacyOutcomeConstants.BLOCK;
		}
	}

	private static IPrivacyPreferenceCondition toPrivacyPreferenceCondition(
			PrivacyPreferenceConditionBean bean) {
		if (bean.getType().compareTo(PrivacyConditionConstantsBean.CONTEXT) ==0){
			return toContextPreferenceCondition((ContextPreferenceConditionBean) bean);
		}else 
			return toTrustPreferenceCondition((TrustPreferenceConditionBean) bean);
	}

	private static IPrivacyPreferenceCondition toTrustPreferenceCondition(
			TrustPreferenceConditionBean bean) {
		// TODO Auto-generated method stub
		try {
			return new TrustPreferenceCondition(TrustModelBeanTranslator.getInstance().fromTrustedEntityIdBean(bean.getTrustId()), bean.getValue());
		} catch (MalformedTrustedEntityIdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private static IPrivacyPreferenceCondition toContextPreferenceCondition(
			ContextPreferenceConditionBean bean) {
		// TODO Auto-generated method stub
		try {
			return new ContextPreferenceCondition((CtxAttributeIdentifier) CtxModelBeanTranslator.getInstance().fromCtxIdentifierBean(bean.getCtxID()), toOperator(bean.getOperator()), bean.getValue());
		} catch (MalformedCtxIdentifierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private static OperatorConstants toOperator(OperatorConstantsBean bean) {
		if (bean.compareTo(OperatorConstantsBean.EQUALS)==0){
			return OperatorConstants.EQUALS;
		}
		
		if (bean.compareTo(OperatorConstantsBean.GREATER_OR_EQUAL_THAN)==0){
			return OperatorConstants.GREATER_OR_EQUAL_THAN;
		}
		
		if (bean.compareTo(OperatorConstantsBean.GREATER_THAN)==0){
			return OperatorConstants.GREATER_THAN;
		}
		
		if (bean.compareTo(OperatorConstantsBean.LESS_OR_EQUAL_THAN)==0){
			return OperatorConstants.LESS_OR_EQUAL_THAN;
		}
		
		return OperatorConstants.LESS_THAN;
	}
	
	
	public static IDSPrivacyPreferenceTreeModel toIDSPrivacyPreferenceTreeModel(IDSPrivacyPreferenceTreeModelBean bean, IIdentityManager idMgr) throws InvalidFormatException{

			IDSPreferenceDetails details = new IDSPreferenceDetails(idMgr.fromJid(bean.getDetails().getAffectedIdentity()));
			details.setRequestor(RequestorUtils.toRequestor(bean.getDetails().getRequestor(), idMgr));
			return new IDSPrivacyPreferenceTreeModel(details , toIDSPrivacyPreference(bean.getPref(), idMgr));
			

	}
	
	public static PrivacyPreference toIDSPrivacyPreference(IDSPreferenceBean bean, IIdentityManager idMgr) throws InvalidFormatException{
		if (bean.getCondition()!=null){
			PrivacyPreference preference = new PrivacyPreference(toPrivacyPreferenceCondition(bean.getCondition()));
			List<IDSPreferenceBean> beans = bean.getChildren();
			
			for (IDSPreferenceBean b : beans){
				preference.add(toIDSPrivacyPreference(b, idMgr));
			}
			
			return preference;
		}
		
		if (bean.getOutcome()!=null){
			return new PrivacyPreference(toIDSOutcome(bean.getOutcome(), idMgr));
		}
		
		PrivacyPreference preference = new PrivacyPreference();
		List<IDSPreferenceBean> beans = bean.getChildren();
		
		for (IDSPreferenceBean b : beans){
			preference.add(toIDSPrivacyPreference(b, idMgr));
		}
		
		return preference;
		
		
	}

	private static IdentitySelectionPreferenceOutcome toIDSOutcome(
			IdentitySelectionPreferenceOutcomeBean bean, IIdentityManager idMgr) throws InvalidFormatException {
		IdentitySelectionPreferenceOutcome outcome = new IdentitySelectionPreferenceOutcome();
		
		
			outcome.setIdentity(idMgr.fromJid(bean.getUserIdentity()));
			outcome.setShouldUseIdentity(bean.isShouldUseIdentity());
		

		
		return outcome;
	}
	
	
	/*
	 * FROM OBJECT TO BEAN METHODS
	 */
	
	public static PPNPrivacyPreferenceTreeModelBean toPPNPrivacyPreferenceTreeModelBean(PPNPrivacyPreferenceTreeModel model){
		PPNPrivacyPreferenceTreeModelBean bean = new PPNPrivacyPreferenceTreeModelBean();
		PPNPreferenceDetailsBean detailsBean = new PPNPreferenceDetailsBean();
		detailsBean.setAction(model.getDetails().getAction());
		detailsBean.setRequestor(RequestorUtils.toRequestorBean(model.getDetails().getRequestor()));
		detailsBean.setResource(model.getDetails().getResource());
		bean.setDetails(detailsBean);
		bean.setPref(toPPNPreferenceBean(model.getRootPreference()));
		
		return bean;
	}

	private static PPNPreferenceBean toPPNPreferenceBean(
			IPrivacyPreference rootPreference) {
		PPNPreferenceBean bean = new PPNPreferenceBean();
		
		
		if  (rootPreference.isLeaf()){
			bean.setOutcome(toPPNPOutcomeBean((PPNPOutcome) rootPreference.getOutcome()));
			return bean;	
		}
		
		if (rootPreference.isBranch()){
			if (rootPreference.getCondition()!=null){
				bean.setCondition(toConditionBean(rootPreference.getCondition()));
			}
		}
		
	
		List<PPNPreferenceBean> beans = new ArrayList<PPNPreferenceBean>();
		
		Enumeration<PrivacyPreference> children = rootPreference.children();
		while (children.hasMoreElements()){
			beans.add(toPPNPreferenceBean(children.nextElement()));
		}
		
		bean.setChildren(beans);
		return bean;
		
	}

	private static PrivacyPreferenceConditionBean toConditionBean(
		IPrivacyPreferenceCondition condition) {
		if (condition instanceof ContextPreferenceCondition){
				return toContextPreferenceConditionBean((ContextPreferenceCondition) condition);
			}
			
		return toTrustPreferenceConditionBean((TrustPreferenceCondition) condition);
	}

	private static TrustPreferenceConditionBean toTrustPreferenceConditionBean(
			TrustPreferenceCondition condition) {
		TrustPreferenceConditionBean bean = new TrustPreferenceConditionBean();
		bean.setTrustId(TrustModelBeanTranslator.getInstance().fromTrustedEntityId(condition.getTrustId()));
		bean.setType(PrivacyConditionConstantsBean.TRUST);
		bean.setValue(condition.getTrustThreshold());
		return bean;
	}

	private static ContextPreferenceConditionBean toContextPreferenceConditionBean(
			ContextPreferenceCondition condition) {
		ContextPreferenceConditionBean bean = new ContextPreferenceConditionBean();
		bean.setCtxID((CtxAttributeIdentifierBean) CtxModelBeanTranslator.getInstance().fromCtxIdentifier(condition.getCtxIdentifier()));
		bean.setOperator(toOperatorConstantsBean(condition.getOperator()));
		bean.setType(PrivacyConditionConstantsBean.CONTEXT);
		bean.setValue(condition.getValue());
		
		return bean;
	}

	private static OperatorConstantsBean toOperatorConstantsBean(
			OperatorConstants operator) {
		if (operator.compareTo(OperatorConstants.EQUALS)==0){
			return OperatorConstantsBean.EQUALS;
		}
		
		if (operator.compareTo(OperatorConstants.GREATER_OR_EQUAL_THAN)==0){
			return OperatorConstantsBean.GREATER_OR_EQUAL_THAN;	
		}
		
		if (operator.compareTo(OperatorConstants.GREATER_THAN)==0){
			return OperatorConstantsBean.GREATER_THAN;
		}
		
		if (operator.compareTo(OperatorConstants.LESS_OR_EQUAL_THAN)==0){
			return OperatorConstantsBean.LESS_OR_EQUAL_THAN;
		}
		
		return OperatorConstantsBean.LESS_THAN;
		
	}

	private static PPNPOutcomeBean toPPNPOutcomeBean(PPNPOutcome outcome) {
		PPNPOutcomeBean bean = new PPNPOutcomeBean();
		bean.setConfidenceLevel(outcome.getConfidenceLevel());
		bean.setEffect(toPrivacyOutcomeConstantsBean(outcome.getEffect()));
		
		
		return bean;
	}

	private static RuleTargetBean toRuleTargetBean(RuleTarget ruleTarget) {
		RuleTargetBean bean = new RuleTargetBean();
		
		
		bean.setActions(ActionUtils.toActionBeans(ruleTarget.getActions()));
		
		bean.setResource(ResourceUtils.toResourceBean(ruleTarget.getResource()));
		
		bean.setSubjects(RequestorUtils.toRequestorBeans(ruleTarget.getRequestors()));
		
		return bean;
	}

	private static PrivacyPreferenceTypeConstantsBean toPrivacyPreferenceTypeConstantsBean(
			PrivacyPreferenceTypeConstants outcomeType) {
		if (outcomeType.compareTo(PrivacyPreferenceTypeConstants.PRIVACY_POLICY_NEGOTIATION)==0){
			return PrivacyPreferenceTypeConstantsBean.PPNP;
		}
		
		if (outcomeType.compareTo(PrivacyPreferenceTypeConstants.DATA_OBFUSCATION)==0){
			return PrivacyPreferenceTypeConstantsBean.DOBF;
		}
		
		return PrivacyPreferenceTypeConstantsBean.IDS;
	}

	private static PrivacyOutcomeConstantsBean toPrivacyOutcomeConstantsBean(
			PrivacyOutcomeConstants effect) {
		
		if (effect.compareTo(PrivacyOutcomeConstants.ALLOW)==0){
			return PrivacyOutcomeConstantsBean.ALLOW;
		}
		
		return PrivacyOutcomeConstantsBean.BLOCK;
	}
	
	
	
	public static IDSPrivacyPreferenceTreeModelBean toIDSPreferenceTreeModelBean(IDSPrivacyPreferenceTreeModel model){
		IDSPrivacyPreferenceTreeModelBean bean = new IDSPrivacyPreferenceTreeModelBean();
		IDSPreferenceDetailsBean detailsBean = new IDSPreferenceDetailsBean();
		detailsBean.setAffectedIdentity(model.getDetails().getAffectedDPI().getJid());
		detailsBean.setRequestor(RequestorUtils.toRequestorBean(model.getDetails().getRequestor()));
		bean.setDetails(detailsBean);
		bean.setPref(toIDSPrivacyPreferenceBean(model.getRootPreference()));
		
		
		return bean;
	}

	private static IDSPreferenceBean toIDSPrivacyPreferenceBean(
			IPrivacyPreference rootPreference) {
		
		if (rootPreference.isLeaf()){
			IDSPreferenceBean bean = new IDSPreferenceBean();
			bean.setOutcome(toIdentitySelectionPreferenceOutcomeBean((IdentitySelectionPreferenceOutcome) rootPreference.getOutcome()));
			return bean;
		}
		IDSPreferenceBean bean = new IDSPreferenceBean();
		if (rootPreference.getCondition()!=null){
			bean.setCondition(toConditionBean(rootPreference.getCondition()));
		}
		
		ArrayList<IDSPreferenceBean> beans = new ArrayList<IDSPreferenceBean>();
		
		Enumeration<PrivacyPreference> children = rootPreference.children();
		
		while(children.hasMoreElements()){
			beans.add(toIDSPrivacyPreferenceBean(children.nextElement()));
		}
		
		bean.setChildren(beans);
		return bean;
	}

	private static IdentitySelectionPreferenceOutcomeBean toIdentitySelectionPreferenceOutcomeBean(
			IdentitySelectionPreferenceOutcome outcome) {
		IdentitySelectionPreferenceOutcomeBean bean = new IdentitySelectionPreferenceOutcomeBean();
		bean.setShouldUseIdentity(outcome.isShouldUseIdentity());
		bean.setUserIdentity(outcome.getIdentity().getJid());
		
		return bean;
	}
	
	
}
