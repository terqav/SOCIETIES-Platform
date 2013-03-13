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
package org.societies.privacytrust.trust.impl.broker.remote;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.StanzaError;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.schema.privacytrust.trust.broker.MethodName;
import org.societies.api.schema.privacytrust.trust.broker.RetrieveTrustBrokerRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.RetrieveTrustBrokerResponseBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustBrokerRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustBrokerResponseBean;
import org.societies.api.privacytrust.trust.ITrustBroker;
import org.societies.api.privacytrust.trust.model.MalformedTrustedEntityIdException;
import org.societies.api.privacytrust.trust.model.TrustModelBeanTranslator;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.5
 */
@Service
public class TrustBrokerRemoteServer implements IFeatureServer {
	
	/** The logging facility. */
	private static Logger LOG = LoggerFactory.getLogger(TrustBrokerRemoteServer.class);
	
	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList(
					"http://societies.org/api/schema/privacytrust/trust/model",
					"http://societies.org/api/schema/privacytrust/trust/broker"));
	
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList(
					"org.societies.api.schema.privacytrust.trust.model",
					"org.societies.api.schema.privacytrust.trust.broker"));
	
	/** The Trust Broker service reference. */
	@Autowired(required=true)
	private ITrustBroker trustBroker;
	
	TrustBrokerRemoteServer() {
		
		LOG.info(this.getClass() + " instantiated");
	}
	
	/*
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#getJavaPackages()
	 */
	@Override
	public List<String> getJavaPackages() {
		
		return PACKAGES;
	}
	
	/*
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#getXMLNamespaces()
	 */
	@Override
	public List<String> getXMLNamespaces() {
		
		return NAMESPACES;
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#getQuery(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object getQuery(Stanza stanza, Object bean) throws XMPPError {
		
		if (!(bean instanceof TrustBrokerRequestBean))
			throw new IllegalArgumentException("bean is not instance of TrustBrokerRequestBean");
		
		final TrustBrokerRequestBean requestBean = (TrustBrokerRequestBean) bean;
		final TrustBrokerResponseBean responseBean = new TrustBrokerResponseBean();
		
		if (MethodName.RETRIEVE.equals(requestBean.getMethodName())) {
			
			final RetrieveTrustBrokerRequestBean retrieveRequestBean =
					requestBean.getRetrieve();
			if (retrieveRequestBean == null)
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustBroker remote retrieve request: "
						+ "RetrieveTrustBrokerRequestBean can't be null");
			
			try {
				final TrustedEntityId trustorId = TrustModelBeanTranslator.getInstance().
						fromTrustedEntityIdBean(retrieveRequestBean.getTrustorId());
				final TrustedEntityId trusteeId = TrustModelBeanTranslator.getInstance().
						fromTrustedEntityIdBean(retrieveRequestBean.getTrusteeId());
				final Double result = this.trustBroker.retrieveTrust(
						trustorId, trusteeId).get();
				
				final RetrieveTrustBrokerResponseBean retrieveResponseBean = 
						new RetrieveTrustBrokerResponseBean(); 
				if (result != null)
					retrieveResponseBean.setResult(result);
				responseBean.setMethodName(MethodName.RETRIEVE);
				responseBean.setRetrieve(retrieveResponseBean);
				
			} catch (MalformedTrustedEntityIdException mteide) {
				
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustBroker remote retrieve request: "
						+ mteide.getLocalizedMessage(), mteide);
			} catch (Exception e) {
				
				throw new XMPPError(StanzaError.internal_server_error, 
						"Could not retrieve trust value: "
						+ e.getLocalizedMessage(), e);
			} 
			
		} else {
			
			throw new XMPPError(StanzaError.unexpected_request, 
					"Unsupported TrustBroker remote request method: " + requestBean.getMethodName());
		}
		
		return responseBean;
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#receiveMessage(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveMessage(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#setQuery(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object setQuery(Stanza arg0, Object arg1) throws XMPPError {
		// TODO Auto-generated method stub
		return null;
	}
}