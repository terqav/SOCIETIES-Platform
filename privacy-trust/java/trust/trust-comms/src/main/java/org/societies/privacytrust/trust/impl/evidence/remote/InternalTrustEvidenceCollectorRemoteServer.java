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
package org.societies.privacytrust.trust.impl.evidence.remote;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.StanzaError;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector;
import org.societies.api.internal.schema.privacytrust.trust.evidence.collector.AddDirectEvidenceRequestBean;
import org.societies.api.internal.schema.privacytrust.trust.evidence.collector.AddIndirectEvidenceRequestBean;
import org.societies.api.internal.schema.privacytrust.trust.evidence.collector.MethodName;
import org.societies.api.internal.schema.privacytrust.trust.evidence.collector.TrustEvidenceCollectorRequestBean;
import org.societies.api.internal.schema.privacytrust.trust.evidence.collector.TrustEvidenceCollectorResponseBean;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.MalformedTrustedEntityIdException;
import org.societies.api.privacytrust.trust.model.TrustModelBeanTranslator;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.3
 */
@Service
public class InternalTrustEvidenceCollectorRemoteServer implements IFeatureServer {
	
	/** The logging facility. */
	private static Logger LOG = LoggerFactory.getLogger(InternalTrustEvidenceCollectorRemoteServer.class);
	
	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList(
					"http://societies.org/api/schema/privacytrust/trust/model",
					"http://societies.org/api/internal/schema/privacytrust/trust/evidence/collector"));
	
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList(
					"org.societies.api.schema.privacytrust.trust.model",
					"org.societies.api.internal.schema.privacytrust.trust.evidence.collector"));
	
	/** The Trust Evidence Collector service reference. */
	@Autowired(required=true)
	private ITrustEvidenceCollector internalTrustEvidenceCollector;
	
	InternalTrustEvidenceCollectorRemoteServer() {
		
		if (LOG.isInfoEnabled())
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
		
		if (!(bean instanceof TrustEvidenceCollectorRequestBean))
			throw new IllegalArgumentException("bean is not instance of TrustEvidenceCollectorRequestBean");
		
		final TrustEvidenceCollectorRequestBean requestBean = (TrustEvidenceCollectorRequestBean) bean;
		final TrustEvidenceCollectorResponseBean responseBean = new TrustEvidenceCollectorResponseBean();
		
		if (LOG.isDebugEnabled())
			LOG.debug("getQuery(stanza=" + this.stanzaToString(stanza)
					+ ",methodName=" + requestBean.getMethodName() + ")");
		
		if (MethodName.ADD_DIRECT_EVIDENCE.equals(requestBean.getMethodName())) {
			
			final AddDirectEvidenceRequestBean addEvidenceRequestBean =
					requestBean.getAddDirectEvidence();
			if (addEvidenceRequestBean == null)
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addDirectEvidence request: "
						+ "AddDirectEvidenceRequestBean can't be null");
			
			try {
				// 1. subjectId
				final TrustedEntityId subjectId = TrustModelBeanTranslator.getInstance().
						fromTrustedEntityIdBean(addEvidenceRequestBean.getSubjectId());
				// 2. objectId
				final TrustedEntityId objectId = TrustModelBeanTranslator.getInstance().
						fromTrustedEntityIdBean(addEvidenceRequestBean.getObjectId());
				// 3. type
				final TrustEvidenceType type = TrustModelBeanTranslator.getInstance().
						fromTrustEvidenceTypeBean(addEvidenceRequestBean.getType());
				// 4. timestamp
				final Date timestamp = addEvidenceRequestBean.getTimestamp();
				// 5. info
				final Serializable info;
				if (addEvidenceRequestBean.getInfo() != null)
					info = deserialise(addEvidenceRequestBean.getInfo(), this.getClass().getClassLoader());
				else
					info = null;
				
				if (LOG.isDebugEnabled())
					LOG.debug("addDirectTrustEvidence(subjectId=" + subjectId
							+ ",objectId=" + objectId + ",type=" + type 
							+ ",timestamp=" + timestamp	+ ",info=" + info + ")");
				this.internalTrustEvidenceCollector.addDirectEvidence(subjectId,
						objectId, type, timestamp, info);
				
				responseBean.setMethodName(MethodName.ADD_DIRECT_EVIDENCE);
				
			} catch (MalformedTrustedEntityIdException mteide) {
				
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addDirectEvidence request: "
						+ "Invalid teid: " + mteide.getLocalizedMessage(), mteide);
			} catch (IOException ioe) {
				
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addDirectEvidence request: "
						+ "Could not deserialise info: " + ioe.getLocalizedMessage(), ioe);
			} catch (ClassNotFoundException cnfe) {
				
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addDirectEvidence request: "
						+ "Could not deserialise info: " + cnfe.getLocalizedMessage(), cnfe);
			} catch (TrustException te) {
				
				throw new XMPPError(StanzaError.internal_server_error,
						te.getLocalizedMessage(), te);
			}
			
		} else if (MethodName.ADD_INDIRECT_EVIDENCE.equals(requestBean.getMethodName())) {
			
			final AddIndirectEvidenceRequestBean addEvidenceRequestBean =
					requestBean.getAddIndirectEvidence();
			if (addEvidenceRequestBean == null)
				throw new XMPPError(StanzaError.bad_request,
						"Invalid TrustEvidenceCollector remote addIndirectEvidence request: "
						+ "AddIndirectEvidenceRequestBean can't be null");
			
			try {
				// 1. subjectId
				final TrustedEntityId subjectId = TrustModelBeanTranslator.getInstance().
						fromTrustedEntityIdBean(addEvidenceRequestBean.getSubjectId()); 
				// 2. objectId
				final TrustedEntityId objectId = TrustModelBeanTranslator.getInstance().
						fromTrustedEntityIdBean(addEvidenceRequestBean.getObjectId());
				// 3. type
				final TrustEvidenceType type = TrustModelBeanTranslator.getInstance().
						fromTrustEvidenceTypeBean(addEvidenceRequestBean.getType());
				// 4. timestamp
				final Date timestamp = addEvidenceRequestBean.getTimestamp();
				// 5. info
				final Serializable info;
				if (addEvidenceRequestBean.getInfo() != null)
					info = deserialise(addEvidenceRequestBean.getInfo(), this.getClass().getClassLoader());
				else
					info = null;
				// 6. sourceId
				final TrustedEntityId sourceId = TrustModelBeanTranslator.getInstance().
						fromTrustedEntityIdBean(addEvidenceRequestBean.getSourceId());
	
				if (LOG.isDebugEnabled())
					LOG.debug("addIndirectTrustEvidence(subjectId=" + subjectId
							+ ",objectId=" + objectId + ",type=" + type 
							+ ",timestamp="	+ timestamp	+ ",info=" + info 
							+ ",sourceId" + sourceId + ")");				
				this.internalTrustEvidenceCollector.addIndirectEvidence(subjectId, 
						objectId, type, timestamp, info, sourceId);
				
				responseBean.setMethodName(MethodName.ADD_INDIRECT_EVIDENCE);
				
			} catch (MalformedTrustedEntityIdException mteide) {
				
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addIndirectEvidence request: "
						+ "Invalid teid: " + mteide.getLocalizedMessage(), mteide);
			} catch (IOException ioe) {
				
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addIndirectEvidence request: "
						+ "Could not deserialise info: " + ioe.getLocalizedMessage(), ioe);
			} catch (ClassNotFoundException cnfe) {
				
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addIndirectEvidence request: "
						+ "Could not deserialise info: " + cnfe.getLocalizedMessage(), cnfe);
			} catch (TrustException te) {
				
				throw new XMPPError(StanzaError.internal_server_error,
						te.getLocalizedMessage(), te);
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
	
	private String stanzaToString(Stanza stanza) {
		
		final StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append("id=" + stanza.getId());
		sb.append(",");
		sb.append("from=" + stanza.getFrom());
		sb.append(",");
		sb.append("to=" + stanza.getTo());
		sb.append("]");
		
		return sb.toString();
	}
	
	/**
	 * Deserialises an object from the specified byte array
	 * 
	 * @param objectData
	 *            the object to deserialise
	 * @param classLoader
	 *            the <code>ClassLoader</code> to use for deserialisation
	 * @return the deserialised object
	 * @throws IOException if the deserialisation of the specified byte array fails
	 * @throws ClassNotFoundException if the class of the deserialised object cannot be found
	 */
	private static Serializable deserialise(byte[] objectData,
			ClassLoader classLoader) throws IOException, ClassNotFoundException {

		final Serializable result;
		CustomObjectInputStream ois = null;
		try {
			ois = new CustomObjectInputStream(
					new ByteArrayInputStream(objectData), classLoader);

			result = (Serializable) ois.readObject();
		} finally {
			if (ois != null)
				ois.close();
		}

		return result;
	}
	
	/**
	 * Credits go to jboss/hibernate for the inspiration
	 */
	private static final class CustomObjectInputStream extends ObjectInputStream {

		// The ClassLoader to use for deserialisation
		private ClassLoader classLoader;

		public CustomObjectInputStream(InputStream is, ClassLoader cl)
				throws IOException {
			super(is);
			this.classLoader = cl;
		}

		protected Class<?> resolveClass(ObjectStreamClass clazz)
				throws IOException, ClassNotFoundException {

			String className = clazz.getName();
			Class<?> resolvedClass = null;

			if (LOG.isDebugEnabled())
				LOG.debug("Attempting to resolve class " + className);
			try {
				resolvedClass = this.classLoader.loadClass(className);
				if (LOG.isDebugEnabled())
					LOG.debug(className	+ " resolved through specified class loader");
			} catch (ClassNotFoundException e) {
				if (LOG.isDebugEnabled())
					LOG.debug("Asking parent class to resolve " + className);
				resolvedClass = super.resolveClass(clazz);
			}

			return resolvedClass;
		}
	}
}