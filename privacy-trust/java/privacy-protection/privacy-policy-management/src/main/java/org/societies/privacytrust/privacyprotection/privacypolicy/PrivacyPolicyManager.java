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
package org.societies.privacytrust.privacyprotection.privacypolicy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyPolicyTypeConstants;
import org.societies.privacytrust.privacyprotection.privacypolicy.reader.XMLPolicyReader;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author Olivier Maridat (Trialog)
 * @date 5 déc. 2011
 */
public class PrivacyPolicyManager implements IPrivacyPolicyManager {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyPolicyManager.class.getName());

	ICommManager commManager;
	ICtxBroker ctxBroker;
	public PrivacyPolicyRegistryManager policyRegistryManager;


	public void init() {
		policyRegistryManager = new PrivacyPolicyRegistryManager(ctxBroker, commManager.getIdManager());
	}


	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager#getPrivacyPolicy(org.societies.api.identity.Requestor)
	 */
	@Override
	public RequestPolicy getPrivacyPolicy(Requestor requestor)
			throws PrivacyException {
		// -- Verify
		if (null == requestor || null == requestor.getRequestorId()) {
			throw new PrivacyException("Not enought information to search a privacy policy. Requestor needed.");
		}
		// Dependency injection not ready
		if (!isDepencyInjectionDone()) {
			throw new PrivacyException("[Dependency Injection] PrivacyPolicyManager not ready");
		}

		// -- Search
		RequestPolicy privacyPolicy = policyRegistryManager.getPolicy(requestor);
		return privacyPolicy;
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager#getPrivacyPolicyFromLocation(java.lang.String)
	 */
	@Override
	public String getPrivacyPolicyFromLocation(String location) throws PrivacyException {
		return getPrivacyPolicyFromLocation(location, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager#getPrivacyPolicyFromLocation(java.lang.String, java.util.Map)
	 */
	@Override
	public String getPrivacyPolicyFromLocation(String location, Map<String, String> options) throws PrivacyException {
		// -- Read options (and create default options)
		String encodingField = "encoding";
		if (null == options) {
			options = new HashMap<String, String>();
		}
		if (!options.containsKey(encodingField)) {
			options.put(encodingField, "UTF-8");
		}

		// -- Retrieve the privacy policy file
		URL url;
		String privacyPolicy = null;
		try {
			url = new URL(location);
			InputStream privacyPolicyStream = url.openStream();
			privacyPolicy = IOUtils.toString(privacyPolicyStream, options.get(encodingField));
		} catch (MalformedURLException e) {
			throw new PrivacyException("Can't find the privacy policy file: \""+location+"", e);
		} catch (IOException e) {
			throw new PrivacyException("Can't read the privacy policy file: \""+location+"", e);
		}
		return privacyPolicy;
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager#updatePrivacyPolicy(org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy)
	 */
	@Override
	public RequestPolicy updatePrivacyPolicy(RequestPolicy privacyPolicy)
			throws PrivacyException {
		// -- Verify
		if (null == privacyPolicy) {
			throw new PrivacyException("The privacy policy to update is empty.");
		}
		if (null == privacyPolicy.getRequestor() || null == privacyPolicy.getRequestor().getRequestorId()) {
			throw new PrivacyException("Not enought information to update a privacy policy. Requestor needed.");
		}
		// Dependency injection not ready
		if (!isDepencyInjectionDone()) {
			throw new PrivacyException("[Dependency Injection] PrivacyPolicyManager not ready");
		}

		// -- Add
		policyRegistryManager.addPolicy(privacyPolicy.getRequestor(), privacyPolicy);
		return privacyPolicy;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager#updatePrivacyPolicy(java.lang.String, org.societies.api.identity.Requestor)
	 */
	@Override
	public RequestPolicy updatePrivacyPolicy(String privacyPolicyXml, Requestor requestor) throws PrivacyException {
		// Retrieve the privacy policy
		RequestPolicy privacyPolicy = fromXMLString(privacyPolicyXml);
		if (null == privacyPolicy) {
			throw new PrivacyException("This XML formatted string of the privacy policy can not be parsed as a privacy policy.");
		}
		// Fill the requestor id
		privacyPolicy.setRequestor(requestor);
		// Create / Store it
		return updatePrivacyPolicy(privacyPolicy);
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager#deletePrivacyPolicy(org.societies.api.identity.Requestor)
	 */
	@Override
	public boolean deletePrivacyPolicy(Requestor requestor) throws PrivacyException {
		// -- Verify
		if (null == requestor || null == requestor.getRequestorId()) {
			throw new PrivacyException("Not enought information to search a privacy policy. Requestor needed.");
		}
		// Dependency injection not ready
		if (!isDepencyInjectionDone()) {
			throw new PrivacyException("[Dependency Injection] PrivacyPolicyManager not ready");
		}

		// -- Delete
		try {
			policyRegistryManager.deletePolicy(requestor);
		} catch (InterruptedException e) {
			LOG.error("[Error deletePrivacyPolicy] Can't delete privacy policy.", e);
			return false;
		} catch (ExecutionException e) {
			LOG.error("[Error deletePrivacyPolicy] Can't delete privacy policy.", e);
			return false;
		} catch (CtxException e) {
			LOG.error("[Error deletePrivacyPolicy] Can't delete privacy policy. Error in the context.", e);
			return false;
		}
		return true;
	}

	/*
	 * Try to infer a privacy policy a configuration map
	 * At the moment it only returns an empty privacy policy
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager#inferPrivacyPolicy(org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.PrivacyPolicyTypeConstants, java.util.Map)
	 */
	@Override
	public RequestPolicy inferPrivacyPolicy(PrivacyPolicyTypeConstants privacyPolicyType,
			Map configuration) throws PrivacyException {
		List<RequestItem> requests = new ArrayList<RequestItem>();
		RequestPolicy privacyPolicy = new RequestPolicy(requests);
		return privacyPolicy;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager#toXMLString(org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy)
	 */
	@Override
	public String toXMLString(RequestPolicy privacyPolicy) {
		String encoding = "UTF-8";
		if (null == privacyPolicy) {
			return "<?xml version=\"1.0\" encoding=\""+encoding+"\"?><RequestPolicy></RequestPolicy>";
		}
		String privacyPolicyXml = privacyPolicy.toXMLString();
		// Fill XML header if necessary
		if (!privacyPolicyXml.startsWith("<?xml")) {
			privacyPolicyXml = "<?xml version=\"1.0\" encoding=\""+encoding+"\"?>\n"+privacyPolicyXml;
		}
		return privacyPolicyXml;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager#fromXMLString(java.lang.String)
	 */
	@Override
	public RequestPolicy fromXMLString(String privacyPolicy) throws PrivacyException {
		// -- Verify
		// Empty privacy policy
		if (null == privacyPolicy || privacyPolicy.equals("")) {
			LOG.debug("Empty privacy policy. Return a null java object.");
			return null;
		}
		// Fill XML header if necessary
		String encoding = "UTF-8";
		if (!privacyPolicy.startsWith("<?xml")) {
			privacyPolicy = "<?xml version=\"1.0\" encoding=\""+encoding+"\"?>\n"+privacyPolicy;
		}
		// If only contains the XML header: empty privacy policy
		if (privacyPolicy.endsWith("?>")) {
			LOG.debug("Empty privacy policy. Return a null java object.");
			return null;
		}
		// Dependency injection not ready
		if (!isDepencyInjectionDone(1)) {
			throw new PrivacyException("[Dependency Injection] PrivacyPolicyManager not ready");
		}

		// -- Convert Xml to Java
		RequestPolicy result = null;
		XMLPolicyReader xmlPolicyReader = new XMLPolicyReader(ctxBroker, commManager.getIdManager());
		try {
			// -- Create XMLDocument version of the privacy policy
			DocumentBuilder xmlDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document privacyPolicyDocument = xmlDocumentBuilder.parse(new ByteArrayInputStream(privacyPolicy.getBytes(encoding)));
			// -- Transform XML Privacy Policy to Java Privacy Policy
			result = xmlPolicyReader.readPolicyFromFile(privacyPolicyDocument);
		} catch (ParserConfigurationException e) {
			LOG.error("[Error fromXMLString] Can't parse the privacy policy.", e);
		} catch (SAXException e) {
			LOG.error("[Error fromXMLString] Can't parse the privacy policy. SAX error.", e);
		} catch (IOException e) {
			LOG.error("[Error fromXMLString] Can't parse the privacy policy. IO error.", e);
		}
		return result;
	}


	// -- Dependency Injection
	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
		LOG.info("[DependencyInjection] ICommManager injected");
	}

	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
		LOG.info("[DependencyInjection] ICtxBroker injected");
	}

	private boolean isDepencyInjectionDone() {
		return isDepencyInjectionDone(0);
	}
	private boolean isDepencyInjectionDone(int level) {
		if (level == 0) {

			if (null == policyRegistryManager) {
				LOG.error("[Manual Dependency Injection] Missing PolicyRegistryManager");
				return false;
			}
		}
		if (level == 0 || level == 1) {
			if (null == ctxBroker) {
				LOG.error("[Dependency Injection] Missing ICtxBorker");
				return false;
			}
			if (null == commManager) {
				LOG.error("[Dependency Injection] Missing ICommManager");
				return false;
			}
			if (null == commManager.getIdManager()) {
				LOG.error("[Dependency Injection] Missing IIdentityManager");
				return false;
			}
		}
		return true;
	}
}
