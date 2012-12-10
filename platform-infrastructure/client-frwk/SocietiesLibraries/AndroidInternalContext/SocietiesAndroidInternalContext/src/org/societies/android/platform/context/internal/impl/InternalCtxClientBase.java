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
package org.societies.android.platform.context.internal.impl;


//import java.net.URL;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.context.model.ACtxAssociation;
import org.societies.android.api.context.model.ACtxAssociationIdentifier;
import org.societies.android.api.context.model.ACtxAttribute;
import org.societies.android.api.context.model.ACtxAttributeIdentifier;
import org.societies.android.api.context.model.ACtxEntity;
import org.societies.android.api.context.model.ACtxEntityIdentifier;
import org.societies.android.api.context.model.ACtxIdentifier;
import org.societies.android.api.context.model.ACtxModelObject;
//import org.societies.android.api.context.ICtxClient;
import org.societies.android.api.internal.context.IInternalCtxClient;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxEntity;
import org.societies.android.api.context.model.CtxModelBeanTranslator;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.android.api.identity.ARequestor;
import org.societies.android.api.identity.ARequestorCis;
import org.societies.android.api.identity.ARequestorService;
import org.societies.android.api.servicelifecycle.AServiceResourceIdentifier;
import org.societies.api.schema.context.contextmanagement.BrokerMethodBean;
import org.societies.api.schema.context.contextmanagement.CreateEntityBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerRequestBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerResponseBean;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;
import org.societies.identity.IdentityManagerImpl;
import org.societies.utilities.DBC.Dbc;
//import org.societies.context.broker.api.CtxBrokerException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Describe your class here...
 *
 * @author pkosmides
 *
 */
public class InternalCtxClientBase implements IInternalCtxClient {

    //COMMS REQUIRED VARIABLES
	private static final List<String> ELEMENT_NAMES = Arrays.asList("requestorServiceBean", "requestorBean", "dataIdentifier", 
			"requestorCisBean", "dataIdentifierScheme", "ctxIdentifierBean", "ctxEntityIdentifierBean", 
			"ctxAttributeIdentifierBean", "ctxAssociationIdentifierBean", "ctxModelObjectBean", "ctxEntityBean", 
			"ctxAssociationBean", "ctxAttributeBean", "ctxQualityBean", "communityMemberCtxEntityBean", 
			"individualCtxEntityBean", "communityCtxEntityBean", "ctxBondBean", "ctxHistoryAttributeBean", 
			"ctxModelTypeBean", "ctxBondOriginTypeBean", "ctxAttributeValueTypeBean", "ctxOriginTypeBean", "ctxUIElement", 
			"ctxBrokerRequestBean", "ctxBrokerResponseBean", "createEntityBean", "createAttributeBean", "createAssociationBean", 
			"retrieveBean", "retrieveIndividualEntityIdBean", "retrieveCommunityEntityIdBean", "updateBean", 
			"updateAttributeBean", "removeBean", "lookupBean", "brokerMethodBean");

	  
	private final static List<String> NAME_SPACES = Arrays.asList(
			"http://societies.org/api/schema/identity",
			"http://societies.org/api/schema/context/model",
			"http://societies.org/api/schema/context/contextmanagement");
	private static final List<String> PACKAGES = Arrays.asList(
			"org.societies.api.schema.identity",
			"org.societies.api.schema.context.model",
			"org.societies.api.schema.context.contextmanagement");
	
//    private ClientCommunicationMgr commMgr;
    private Context applicationContext;
	private ClientCommunicationMgr ccm;
	private boolean restrictBroadcast;

    private static final String LOG_TAG = InternalCtxClientBase.class.getName();
//    private IBinder binder = null;
    
    public InternalCtxClientBase(Context androidContext, ClientCommunicationMgr ccm, boolean restrictBroadcast0) {
    	Log.d(LOG_TAG, "InternalCtxClientBase created");
    	
		this.ccm = ccm;
    	this.applicationContext = applicationContext;
    	this.restrictBroadcast = restrictBroadcast;
	}

/*	public ACtxEntity createEntity(String client, ARequestor requestor,
			String targetCss, String type) throws CtxException {

		Log.d(LOG_TAG, "CreateEntity called by client: " + client);
		
		//Communications configuration
		ICommCallback ctxClientCallback = new CtxClientCallback(client, CREATE_ENTITY); 
		IIdentityManager idm = this.ccm.getIdManager();
		IIdentity toIdentity;
		
		try {
			toIdentity = idm.getCloudNode();
			//TODO use given targetCss
			
			CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
			cbPacket.setMethod(BrokerMethodBean.CREATE_ENTITY);

			CreateEntityBean ctxBrokerCreateEntityBean = new CreateEntityBean();
			RequestorBean requestorBean = createRequestorBean(requestor);
			ctxBrokerCreateEntityBean.setRequestor(requestorBean);
			ctxBrokerCreateEntityBean.setTargetCss(toIdentity.getBareJid());
			//		ctxBrokerCreateEntityBean.setTargetCss(toIdentity);
			ctxBrokerCreateEntityBean.setType(type);
	
			cbPacket.setCreateEntity(ctxBrokerCreateEntityBean);
		
			Stanza stanza = new Stanza(toIdentity);
		
			ccm.register(ELEMENT_NAMES, ctxClientCallback);
			ccm.sendIQ(stanza, IQ.Type.GET, cbPacket, ctxClientCallback);				
//			commMgr.register(ELEMENT_NAMES, ctxClientCallback);
//			commMgr.sendIQ(stanza, IQ.Type.GET, cbPacket, ctxClientCallback);
			Log.d(LOG_TAG, "Sending stanza");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(LOG_TAG, "ERROR sending message: " + e.getMessage());
			e.printStackTrace();
		}
		
		return null;
	}

	public ACtxAttribute createAttribute(String client, ARequestor requestor,
			ACtxEntityIdentifier scope, String type) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public ACtxAssociation createAssociation(String client,
			ARequestor requestor, IIdentity targetCss, String type)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ACtxIdentifier> lookup(String client, ARequestor requestor,
			IIdentity target, CtxModelType modelType, String type)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ACtxIdentifier> lookup(String client, ARequestor requestor,
			ACtxEntityIdentifier entityId, CtxModelType modelType, String type)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ACtxEntityIdentifier> lookupEntities(String client,
			ARequestor requestor, IIdentity targetCss, String entityType,
			String attribType, Serializable minAttribValue,
			Serializable maxAttribValue) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public ACtxModelObject remove(String client, ARequestor requestor,
			ACtxIdentifier identifier) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public ACtxModelObject retrieve(String client, ARequestor requestor,
			ACtxIdentifier identifier) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public ACtxEntityIdentifier retrieveIndividualEntityId(String client,
			ARequestor requestor, IIdentity cssId) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public ACtxEntityIdentifier retrieveCommunityEntityId(String client,
			ARequestor requestor, IIdentity cisId) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public ACtxModelObject update(String client, ARequestor requestor,
			ACtxModelObject object) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}
	
	private RequestorBean createRequestorBean(ARequestor requestor){
		if (requestor instanceof ARequestorCis){
			RequestorCisBean cisRequestorBean = new RequestorCisBean();
			cisRequestorBean.setRequestorId(requestor.getRequestorId());
			cisRequestorBean.setCisRequestorId(((ARequestorCis) requestor).getCisRequestorId());
			return cisRequestorBean;
		}else if (requestor instanceof ARequestorService){
			RequestorServiceBean serviceRequestorBean = new RequestorServiceBean();
			serviceRequestorBean.setRequestorId(requestor.getRequestorId());
			serviceRequestorBean.setRequestorServiceId(((ARequestorService) requestor).getRequestorServiceId());
			return serviceRequestorBean;
		}else{
			RequestorBean requestorBean = new RequestorBean();
			requestorBean.setRequestorId(requestor.getRequestorId());
			return requestorBean;
		}
	}*/

    /* external methods */
	public ACtxEntity createEntity(String client, ARequestor requestor,
			String targetCss, String type) throws CtxException {

		Log.d(LOG_TAG, "CreateEntity called by client: " + client);
		
		//Communications configuration
//		ICommCallback ctxClientCallback = new CtxClientCallback(client, CREATE_ENTITY); 
		CtxClientCallback ctxClientCallback = new CtxClientCallback(client, CREATE_ENTITY);
		IIdentityManager idm = this.ccm.getIdManager();
		IIdentity toIdentity;
		
		try {
			if (idm.isMine(idm.fromJid(targetCss))) {
				toIdentity = idm.getCloudNode();
				//TODO use given targetCss
				
				CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
				cbPacket.setMethod(BrokerMethodBean.CREATE_ENTITY);
	
				CreateEntityBean ctxBrokerCreateEntityBean = new CreateEntityBean();
				RequestorBean requestorBean = createRequestorBean(requestor);
				ctxBrokerCreateEntityBean.setRequestor(requestorBean);
				ctxBrokerCreateEntityBean.setTargetCss(toIdentity.getBareJid());
				//		ctxBrokerCreateEntityBean.setTargetCss(toIdentity);
				ctxBrokerCreateEntityBean.setType(type);
		
				cbPacket.setCreateEntity(ctxBrokerCreateEntityBean);
			
				Stanza stanza = new Stanza(toIdentity);
			
				ccm.register(ELEMENT_NAMES, ctxClientCallback);
				ccm.sendIQ(stanza, IQ.Type.GET, cbPacket, ctxClientCallback);				
	//			commMgr.register(ELEMENT_NAMES, ctxClientCallback);
	//			commMgr.sendIQ(stanza, IQ.Type.GET, cbPacket, ctxClientCallback);
				Log.d(LOG_TAG, "Sending stanza");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(LOG_TAG, "ERROR sending message: " + e.getMessage());
			e.printStackTrace();
		}
		
		return null;
	}

	public ACtxAttribute createAttribute(String client, ARequestor requestor,
			ACtxEntityIdentifier scope, String type) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public ACtxAssociation createAssociation(String client,
			ARequestor requestor, String targetCss, String type)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ACtxIdentifier> lookup(String client, ARequestor requestor,
			String target, CtxModelType modelType, String type)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ACtxIdentifier> lookup(String client, ARequestor requestor,
			ACtxEntityIdentifier entityId, CtxModelType modelType, String type)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ACtxEntityIdentifier> lookupEntities(String client,
			ARequestor requestor, String targetCss, String entityType,
			String attribType, Serializable minAttribValue,
			Serializable maxAttribValue) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public ACtxModelObject remove(String client, ARequestor requestor,
			ACtxIdentifier identifier) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public ACtxModelObject retrieve(String client, ARequestor requestor,
			ACtxIdentifier identifier) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public ACtxEntityIdentifier retrieveIndividualEntityId(String client,
			ARequestor requestor, String cssId) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public ACtxEntityIdentifier retrieveCommunityEntityId(String client,
			ARequestor requestor, String cisId) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public ACtxModelObject update(String client, ARequestor requestor,
			ACtxModelObject object) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	/* internal methods */
	public ACtxAssociation createAssociation(String client, String type)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public ACtxAttribute createAttribute(String client,
			ACtxEntityIdentifier scope, String type) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public ACtxEntity createEntity(String client, String type)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ACtxEntityIdentifier> lookupEntities(String client,
			String entityType, String attribType, Serializable minAttribValue,
			Serializable maxAttribValue) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ACtxIdentifier> lookup(String client, CtxModelType modelType,
			String type) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public ACtxModelObject remove(String client, ACtxIdentifier identifier)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public ACtxModelObject retrieve(String client, ACtxIdentifier identifier)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public ACtxModelObject update(String client, ACtxModelObject identifier)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public ACtxAttribute updateAttribute(String client,
			ACtxAttributeIdentifier attributeId, Serializable value)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public ACtxAttribute updateAttribute(String client,
			ACtxAttributeIdentifier attributeId, Serializable value,
			String valueMetric) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	private RequestorBean createRequestorBean(ARequestor requestor){
		if (requestor instanceof ARequestorCis){
			RequestorCisBean cisRequestorBean = new RequestorCisBean();
			cisRequestorBean.setRequestorId(requestor.getRequestorId());
			cisRequestorBean.setCisRequestorId(((ARequestorCis) requestor).getCisRequestorId());
			return cisRequestorBean;
		}else if (requestor instanceof ARequestorService){
			RequestorServiceBean serviceRequestorBean = new RequestorServiceBean();
			serviceRequestorBean.setRequestorId(requestor.getRequestorId());
			serviceRequestorBean.setRequestorServiceId(((ARequestorService) requestor).getRequestorServiceId());
			return serviceRequestorBean;
		}else{
			RequestorBean requestorBean = new RequestorBean();
			requestorBean.setRequestorId(requestor.getRequestorId());
			return requestorBean;
		}
	}
	
/*	private RequestorBean toRequestorBean(ARequestor requestor) {

		if (requestor instanceof ARequestorService){
			RequestorServiceBean bean = new RequestorServiceBean();
			bean.setRequestorId(requestor.getRequestorId());
			bean.setRequestorServiceId(AServiceResourceIdentifier.convertAServiceResourceIdentifier(((ARequestorService) requestor).getRequestorServiceId()));
			return bean;
		}else if (requestor instanceof ARequestorCis){
			RequestorCisBean bean = new RequestorCisBean();
			bean.setRequestorId(requestor.getRequestorId());
			bean.setCisRequestorId(((ARequestorCis) requestor).getCisRequestorId());
			return bean;
		}

		RequestorBean bean = new RequestorBean();
		bean.setRequestorId(requestor.getRequestorId());


		return bean;
	}*/
	
	/**
	 * Callback required for Android Comms Manager
	 */
	private class CtxClientCallback implements ICommCallback {

		private String client;
		private String returnIntent;
		
		/**Constructor sets the calling client and Intent to be returned
		 * @param client
		 * @param returnIntent
		 */
		public CtxClientCallback(String client, String returnIntent) {
			this.client = client;
			this.returnIntent = returnIntent;
		}
		
		public List<String> getXMLNamespaces() {

			return NAME_SPACES;
		}

		public List<String> getJavaPackages() {

			return PACKAGES;
		}

		public void receiveResult(Stanza stanza, Object msgBean) {

			Log.d(LOG_TAG, "CtxClient Callback receiveResult");
			
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
							final ACtxEntity entity = CtxModelBeanTranslator.getInstance().fromCtxEntityBean(entityBean);
							//NOTIFY calling client
							intent.putExtra(IInternalCtxClient.CREATE_ENTITY, entity);
						}
					}catch (Exception e) {

						Log.e(LOG_TAG, "Could not handle result bean " + msgBean + ": "
								+ e.getLocalizedMessage(), e);
					}
				}
				
				intent.setPackage(client);
				InternalCtxClientBase.this.applicationContext.sendBroadcast(intent);
//				CtxClientBase.this.commMgr.unregister(ELEMENT_NAMES, this);
				InternalCtxClientBase.this.ccm.unregister(ELEMENT_NAMES, this);
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
	
	/**
	 * This class carries out the createEntity method call asynchronously
	 */
/*	private class AsyncCreateEntity extends AsyncTask<Object, Void, String[]> {
		
		@Override
		/**
		 * Carry out compute task 
		 
		protected String[] doInBackground(Object... params) {
			Dbc.require("At least four parameter must be supplied", params.length >= 4);
			Log.d(LOG_TAG, "DomainRegistration - doInBackground");
			
			//PARAMETERS
			String client = (String)params[0];
			ARequestor requestor = (ARequestor) params[1];
//			IIdentity targetCss = (IIdentity) params[2];
			String targetCss = (String) params[2];
			String type = (String) params[3];
			//RETURN OBJECT
			String results[] = new String[1];
			results[0] = client;
			//MESSAGE BEAN
			IIdentity toIdentity;
//			String toIdentity;
//			toIdentity = targetCss;

			IIdentityManager idm = ccm.getIdManager();
			toIdentity = idm.getCloudNode();

			
			CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
			cbPacket.setMethod(BrokerMethodBean.CREATE_ENTITY);

			CreateEntityBean ctxBrokerCreateEntityBean = new CreateEntityBean();
			RequestorBean requestorBean = createRequestorBean(requestor);
			ctxBrokerCreateEntityBean.setRequestor(requestorBean);
			ctxBrokerCreateEntityBean.setTargetCss(toIdentity.getBareJid());
//			ctxBrokerCreateEntityBean.setTargetCss(toIdentity);
			ctxBrokerCreateEntityBean.setType(type);
		
			cbPacket.setCreateEntity(ctxBrokerCreateEntityBean);

			
			//Communications configuration
			ICommCallback ctxClientCallback = new CtxClientCallback(client, CREATE_ENTITY); 
			Stanza stanza = new Stanza(toIdentity);
			
			try {
				ccm.register(ELEMENT_NAMES, ctxClientCallback);
				ccm.sendIQ(stanza, IQ.Type.GET, cbPacket, ctxClientCallback);				
//				commMgr.register(ELEMENT_NAMES, ctxClientCallback);
//				commMgr.sendIQ(stanza, IQ.Type.GET, cbPacket, ctxClientCallback);
				Log.d(LOG_TAG, "Sending stanza");
			} catch (Exception e) {

				Log.e(LOG_TAG, "ERROR sending message: " + e.getMessage());
				//throw new CtxBrokerException("Could not create remote entity: "
				//		+ e.getLocalizedMessage(), e);
			} 
			return results;
		}
*/		
		/**Handle the communication of the result*/
/*		@Override
		protected void onPostExecute(String results []) {
			Log.d(LOG_TAG, "DomainRegistration - onPostExecute");
	    }
	}*/
	
    /**
     * Assign connection parameters (must happen after successful XMPP login)
     */
/*    private void assignConnectionParameters() {
		//Get the Cloud destination
    	this.cloudCommsDestination = this.ccm.getIdManager().getCloudNode().getJid();
		Log.d(LOG_TAG, "Cloud Node: " + this.cloudCommsDestination);

//    	this.domainCommsDestination = this.ccm.getIdManager().getDomainAuthorityNode().getJid();
 //   	Log.d(LOG_TAG, "Domain Authority Node: " + this.domainCommsDestination);
    			
    	try {
			this.cloudNodeIdentity  = IdentityManagerImpl.staticfromJid(this.cloudCommsDestination);
			Log.d(LOG_TAG, "Cloud node identity: " + this.cloudNodeIdentity);
			
//			this.domainNodeIdentity = IdentityManagerImpl.staticfromJid(this.domainCommsDestination);
//			Log.d(LOG_TAG, "Domain node identity: " + this.cloudNodeIdentity);
			
		} catch (InvalidFormatException e) {
			Log.e(LOG_TAG, "Unable to get CSS Node identity", e);
			throw new RuntimeException(e);
		}     
    }
*/

}
