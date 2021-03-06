/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske dru�be in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVA��O, SA (PTIN), IBM Corp., 
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
package org.societies.android.platform.context.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
//import org.societies.android.api.internal.personalisation.IPersonalisationManagerInternalAndroid;
import org.societies.android.api.internal.context.IInternalCtxClient;
//import org.societies.android.api.personalisation.IPersonalisationManagerAndroid;
import org.societies.android.api.context.CtxException;
import org.societies.android.api.context.ICtxClient;
import org.societies.android.api.comms.xmpp.Stanza;
import org.societies.android.api.comms.xmpp.XMPPError;
import org.societies.android.api.comms.xmpp.XMPPInfo;
import org.societies.android.api.comms.xmpp.CommunicationException;
import org.societies.android.api.comms.xmpp.ICommCallback;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.context.contextmanagement.CreateEntityBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerRequestBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerResponseBean;
import org.societies.api.schema.context.contextmanagement.BrokerMethodBean;
import org.societies.api.schema.context.model.CtxAssociationBean;
import org.societies.api.schema.context.model.CtxAttributeBean;
import org.societies.api.schema.context.model.CtxAttributeIdentifierBean;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.context.model.CtxEntityIdentifierBean;
import org.societies.api.schema.context.model.CtxIdentifierBean;
import org.societies.api.schema.context.model.CtxModelObjectBean;
import org.societies.api.schema.context.model.CtxModelTypeBean;
//import org.societies.api.schema.personalisation.mgmt.PersonalisationManagerBean;
//import org.societies.api.schema.personalisation.mgmt.PersonalisationMethodType;
//import org.societies.api.schema.personalisation.model.ActionBean;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

/**
 * @author pkosmides
 *
 */
public class ContextBrokerBase implements IInternalCtxClient{

	private static final List<String> ELEMENT_NAMES = Collections.unmodifiableList(Arrays.asList("ctxBrokerRequestBean","ctxBrokerResponseBean"));
	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList("http://societies.org/api/schema/context/model",
					"http://societies.org/api/schema/context/contextmanagement",
					"http://societies.org/api/schema/identity",
					"http://societies.org/api/schema/servicelifecycle/model"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList("org.societies.api.schema.context.model",
					"org.societies.api.schema.context.contextmanagement",
					"org.societies.api.schema.identity",
					"org.societies.api.schema.servicelifecycle.model"));



	//Logging tag
	private static final String LOG_TAG = ContextBrokerBase.class.getName();
	private final Context applicationContext;
	private final ClientCommunicationMgr commMgr;
	private final boolean restrictBroadcast;

	public ContextBrokerBase(Context applicationContext, ClientCommunicationMgr commMgr,
			boolean restrictBroadcast) {
		this.applicationContext = applicationContext;
		this.commMgr = commMgr;
		this.restrictBroadcast = restrictBroadcast;
	}

	@Override
	public CtxEntityBean createEntity(String client, RequestorBean requestor,
			String targetCss, String type) throws CtxException {
		Log.d(LOG_TAG, "CreateEntity called by client: " + client);
		
//		IIdentity toIdentity;
//		toIdentity = targetCss;
		
		try {
			IIdentityManager idm = this.commMgr.getIdManager();
			IIdentity toIdentity = idm.fromJid(targetCss);

			Stanza stanza = new Stanza(toIdentity);

			CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
			cbPacket.setMethod(BrokerMethodBean.CREATE_ENTITY);
	
			CreateEntityBean ctxBrokerCreateEntityBean = new CreateEntityBean();
	//		RequestorBean requestorBean = createRequestorBean(requestor);
			ctxBrokerCreateEntityBean.setRequestor(requestor);
	//		ctxBrokerCreateEntityBean.setTargetCss(toIdentity.getBareJid());
			ctxBrokerCreateEntityBean.setTargetCss(targetCss);
			ctxBrokerCreateEntityBean.setType(type);
		
			cbPacket.setCreateEntity(ctxBrokerCreateEntityBean);

			ICommCallback ctxBrokerCallback = new ContextBrokerCallback(client, IInternalCtxClient.CREATE_ENTITY); 

//		try {
//			commMgr.register(ELEMENT_NAMES, ctxBrokerCallback);
			commMgr.sendIQ(stanza, IQ.Type.GET, cbPacket, ctxBrokerCallback);
			Log.d(LOG_TAG, "Sending stanza");
		} catch (Exception e) {

			Log.e(LOG_TAG, "ERROR sending message: " + e.getMessage());
			//throw new CtxBrokerException("Could not create remote entity: "
			//		+ e.getLocalizedMessage(), e);
		} 
		return null;

	}

	@Override
	public CtxAttributeBean createAttribute(String client,
			RequestorBean requestor, CtxEntityIdentifierBean scope, String type)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CtxAssociationBean createAssociation(String client,
			RequestorBean requestor, String targetCss, String type)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CtxIdentifierBean> lookup(String client,
			RequestorBean requestor, String target, CtxModelTypeBean modelType,
			String type) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CtxIdentifierBean> lookup(String client,
			RequestorBean requestor, CtxEntityIdentifierBean entityId,
			CtxModelTypeBean modelType, String type) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CtxEntityIdentifierBean> lookupEntities(String client,
			RequestorBean requestor, String targetCss, String entityType,
			String attribType, Serializable minAttribValue,
			Serializable maxAttribValue) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CtxModelObjectBean remove(String client, RequestorBean requestor,
			CtxIdentifierBean identifier) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CtxModelObjectBean retrieve(String client, RequestorBean requestor,
			CtxIdentifierBean identifier) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CtxEntityIdentifierBean retrieveIndividualEntityId(String client,
			RequestorBean requestor, String cssId) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CtxEntityIdentifierBean retrieveCommunityEntityId(String client,
			RequestorBean requestor, String cisId) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CtxModelObjectBean update(String client, RequestorBean requestor,
			CtxModelObjectBean object) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CtxAssociationBean createAssociation(String client, String type)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CtxAttributeBean createAttribute(String client,
			CtxEntityIdentifierBean scope, String type) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CtxEntityBean createEntity(String client, String type)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CtxEntityIdentifierBean> lookupEntities(String client,
			String entityType, String attribType, Serializable minAttribValue,
			Serializable maxAttribValue) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CtxIdentifierBean> lookup(String client,
			CtxModelTypeBean modelType, String type) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CtxModelObjectBean remove(String client, CtxIdentifierBean identifier)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CtxModelObjectBean retrieve(String client,
			CtxIdentifierBean identifier) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CtxModelObjectBean update(String client,
			CtxModelObjectBean identifier) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CtxAttributeBean updateAttribute(String client,
			CtxAttributeIdentifierBean attributeId, Serializable value)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CtxAttributeBean updateAttribute(String client,
			CtxAttributeIdentifierBean attributeId, Serializable value,
			String valueMetric) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Callback required for Android Comms Manager
	 */
	private class ContextBrokerCallback implements ICommCallback {

		private String client;
		private String returnIntent;
		
		/**Constructor sets the calling client and Intent to be returned
		 * @param client
		 * @param returnIntent
		 */
		public ContextBrokerCallback(String client, String returnIntent) {
			this.client = client;
			this.returnIntent = returnIntent;
		}
		
		public List<String> getXMLNamespaces() {

			return NAMESPACES;
		}

		public List<String> getJavaPackages() {

			return PACKAGES;
		}

		public void receiveResult(Stanza stanza, Object msgBean) {

			Log.d(LOG_TAG, "ContextBroker Callback receiveResult");
			
			if (client != null) {
				Intent intent = new Intent(returnIntent);
				
				Log.d(LOG_TAG, "Return Stanza: " + stanza.toString());
				if (msgBean==null)
					Log.d(LOG_TAG, "msgBean is null");
				
				if (msgBean instanceof CtxBrokerResponseBean) {
					
					Log.d(LOG_TAG, "receiveResult CtxBrokerRespose");
					
					final CtxBrokerResponseBean payload = (CtxBrokerResponseBean) msgBean;
					final BrokerMethodBean method = payload.getMethod();
					try {
						switch (method) {
						
						case CREATE_ENTITY:
							
							Log.i(LOG_TAG, "inside receiveResult CREATE ENTITY");
							if (payload.getCreateEntityBeanResult() == null) {
								Log.e(LOG_TAG, "Could not handle result bean: CtxBrokerResponseBean.getCreateEntityBeanResult() is null");
								return;
							}
							final CtxEntityBean entityBean = payload.getCreateEntityBeanResult();
//							final ACtxEntity entity = CtxModelBeanTranslator.getInstance().fromCtxEntityBean(entityBean);
							//NOTIFY calling client
							intent.putExtra(INTENT_RETURN_VALUE_KEY, (Parcelable) entityBean);
							if (restrictBroadcast) {
								intent.setPackage(client);
							}
							ContextBrokerBase.this.applicationContext.sendBroadcast(intent);
							Log.d(LOG_TAG, "SendBroadcast intent with ctxEntity object");
//							ContextBrokerBase.this.commMgr.unregister(ELEMENT_NAMES, NAMESPACES, ContextBrokerCallback());
							
							break;
						}
					}catch (Exception e) {

						Log.e(LOG_TAG, "Could not handle result bean " + msgBean + ": "
								+ e.getLocalizedMessage(), e);
					}
				}
				
//				intent.setPackage(client);
//				CtxClientBase.this.androidContext.sendBroadcast(intent);
//				CtxClientBase.this.commMgr.unregister(ELEMENT_NAMES, this);
			}
		}

		public void receiveError(Stanza stanza, XMPPError error) {

			Log.d(LOG_TAG, "CtxClient Callback receiveError: " + error.getMessage());
		}

		public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {

			Log.d(LOG_TAG, "CtxClient Callback receiveInfo");
		}

		public void receiveItems(Stanza stanza, String node, List<String> items) {

			Log.d(LOG_TAG, "CtxClient Callback receiveItems");
		}

		public void receiveMessage(Stanza stanza, Object payload) {

			Log.d(LOG_TAG, "CtxClient Callback receiveMessage");
		}
	}

}
