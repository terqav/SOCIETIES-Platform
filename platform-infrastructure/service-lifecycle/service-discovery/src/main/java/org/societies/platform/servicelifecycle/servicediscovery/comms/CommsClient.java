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
package org.societies.platform.servicelifecycle.servicediscovery.comms;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.servicelifecycle.IServiceDiscoveryCallback;
import org.societies.api.internal.servicelifecycle.IServiceDiscoveryRemote;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.springframework.scheduling.annotation.Async;

import org.societies.api.schema.servicelifecycle.servicediscovery.MethodName;
import org.societies.api.schema.servicelifecycle.servicediscovery.ServiceDiscoveryMsgBean;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;


/**
 * Comms Client that initiates the remote communication for the Service Lifecycle
 *
 * @author aleckey
 * @author Maria Mannion
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public class CommsClient implements IServiceDiscoveryRemote, ICommCallback{
	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			  Arrays.asList("http://societies.org/api/schema/servicelifecycle/model",
					  		"http://societies.org/api/schema/servicelifecycle/servicediscovery"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			  Arrays.asList("org.societies.api.schema.servicelifecycle.model",
							"org.societies.api.schema.servicelifecycle.servicediscovery"));
	
	//PRIVATE VARIABLES
	private ICommManager commMngr;
	private static Logger LOG = LoggerFactory.getLogger(CommsClient.class);
	
	public void setCommMngr(ICommManager commMngr) {
		this.commMngr = commMngr;
	}
	
	public ICommManager getCommMngr() {
		return commMngr;
	}
	public CommsClient() {	}

	public void InitService() {
		//REGISTER OUR ServiceManager WITH THE XMPP Communication Manager
		
		if(LOG.isDebugEnabled())
			LOG.debug("Registering the Service Discovery SLM Communication Manager with the XMPP Communication Manager");
		
		try {
			getCommMngr().register(this); 
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	@Async
	public void getServices(IIdentity node, IServiceDiscoveryCallback serviceDiscoveryCallback) {
		

		if(LOG.isDebugEnabled()) LOG.debug("SLM CommsClient: getServices called");
		
		Stanza stanza = new Stanza(node);

		//SETUP CALC CLIENT RETURN STUFF
		CommsClientCallback callback = new CommsClientCallback(stanza.getId(), serviceDiscoveryCallback);

		//CREATE MESSAGE BEAN
		ServiceDiscoveryMsgBean bean = new ServiceDiscoveryMsgBean();
		bean.setMethod(MethodName.GET_LOCAL_SERVICES);
		try {
			//SEND INFORMATION QUERY - RESPONSE WILL BE IN "callback"
			getCommMngr().sendIQGet(stanza, bean, callback);
			
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};
	}
	
	@Override
	@Async
	public void getService(ServiceResourceIdentifier serviceId, IIdentity node,
			IServiceDiscoveryCallback serviceDiscoveryCallback) {
		
		if(LOG.isDebugEnabled()) LOG.debug("SLM CommsClient: getService called");
		
		Stanza stanza = new Stanza(node);

		//SETUP CALC CLIENT RETURN STUFF
		CommsClientCallback callback = new CommsClientCallback(stanza.getId(), serviceDiscoveryCallback);

		//CREATE MESSAGE BEAN
		ServiceDiscoveryMsgBean bean = new ServiceDiscoveryMsgBean();
		bean.setMethod(MethodName.GET_SERVICE);
		bean.setServiceId(serviceId);
		
		try {
			//SEND INFORMATION QUERY - RESPONSE WILL BE IN "callback"
			getCommMngr().sendIQGet(stanza, bean, callback);
			
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};
		
	}

	@Override
	@Async
	public void searchService(Service filter, IIdentity node,
			IServiceDiscoveryCallback serviceDiscoveryCallback) {

		if(LOG.isDebugEnabled()) LOG.debug("SLM CommsClient: searchService called");
		
		Stanza stanza = new Stanza(node);

		//SETUP CALC CLIENT RETURN STUFF
		CommsClientCallback callback = new CommsClientCallback(stanza.getId(), serviceDiscoveryCallback);

		//CREATE MESSAGE BEAN
		ServiceDiscoveryMsgBean bean = new ServiceDiscoveryMsgBean();
		bean.setMethod(MethodName.GET_SERVICE);
		bean.setService(filter);
		
		try {
			//SEND INFORMATION QUERY - RESPONSE WILL BE IN "callback"
			getCommMngr().sendIQGet(stanza, bean, callback);
			
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};
		
	}


	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#getJavaPackages() */
	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#getXMLNamespaces() */
	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}

	@Override
	public void receiveError(Stanza arg0, XMPPError arg1) { }

	@Override
	public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) { }

	@Override
	public void receiveMessage(Stanza arg0, Object arg1) { }

	@Override
	public void receiveResult(Stanza arg0, Object arg1) { }

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveItems(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, java.util.List)
	 */
	@Override
	public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
		// TODO Auto-generated method stub
	}


}
