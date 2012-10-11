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
package org.societies.security.policynegotiator.provider;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.domainauthority.IClientJarServerCallback;
import org.societies.api.internal.domainauthority.IClientJarServerRemote;
import org.societies.api.internal.domainauthority.UrlPath;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderRemote;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderSLMCallback;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderServiceMgmt;
import org.societies.api.internal.security.policynegotiator.NegotiationException;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.security.digsig.DigsigException;
import org.societies.api.security.digsig.ISignatureMgr;

/**
 * 
 *
 * @author Mitja Vardjan
 *
 */
public class ProviderServiceMgr implements INegotiationProviderServiceMgmt {

	private static Logger LOG = LoggerFactory.getLogger(INegotiationProviderServiceMgmt.class);

	private IClientJarServerRemote clientJarServer;
	private ISignatureMgr signatureMgr;
	private INegotiationProviderRemote groupMgr;

	private HashMap<String, Service> services = new HashMap<String, Service>();

	public ProviderServiceMgr() {
		LOG.info("ProviderServiceMgr");
	}
	
	public IClientJarServerRemote getClientJarServer() {
		return clientJarServer;
	}
	public void setClientJarServer(IClientJarServerRemote clientJarServer) {
		LOG.debug("setClientJarServer()");
		this.clientJarServer = clientJarServer;
	}
	public ISignatureMgr getSignatureMgr() {
		return signatureMgr;
	}
	public void setSignatureMgr(ISignatureMgr signatureMgr) {
		LOG.debug("setSignatureMgr()");
		this.signatureMgr = signatureMgr;
	}
	public INegotiationProviderRemote getGroupMgr() {
		return groupMgr;
	}
	public void setGroupMgr(INegotiationProviderRemote groupMgr) {
		LOG.debug("setGroupMgr()");
		this.groupMgr = groupMgr;
	}

	@Override
	public void addService(ServiceResourceIdentifier serviceId, String slaXml, URI fileServer,
			List<String> files, INegotiationProviderSLMCallback callback) throws NegotiationException {
		
		IIdentity provider = groupMgr.getIdMgr().getThisNetworkNode();
		String signature;
		String dataToSign;
		String strippedFilePath;
		
		String idStr = serviceId.getIdentifier().toString();
		Service s = new Service(idStr, slaXml, fileServer, files);

		if (files != null && files.size() > 0) {
			dataToSign = serviceId.getIdentifier().toASCIIString();
	
			for (int k = 0; k < files.size(); k++) {
				if (files.get(k).startsWith("/")) {
					strippedFilePath = files.get(k).replaceFirst("/", "");
					files.set(k, strippedFilePath);
				}
				dataToSign += files.get(k);
			}
	
			try {
				signature = signatureMgr.sign(dataToSign, provider);
			} catch (DigsigException e) {
				throw new NegotiationException(e);
			}
			IClientJarServerCallback cb = new ClientJarServerCallback(callback);
			//this.clientJarServer.shareFiles(serviceId.getIdentifier(), provider, signature, files);  // local OSGi call
			this.clientJarServer.shareFiles(groupMgr.getIdMgr().getDomainAuthorityNode(),
					serviceId.getIdentifier(), provider, signature, files, cb);
			services.put(idStr, s);
		}
		else {
			services.put(idStr, s);
			callback.notifySuccess();
		}
	}

	@Override
	public void addService(ServiceResourceIdentifier serviceId, String slaXml, URI fileServer,
			URI[] fileUris, INegotiationProviderSLMCallback callback) throws NegotiationException {
		
		List<String> files = new ArrayList<String>();
		
		// TODO: upload the files to REST server
		LOG.warn("Automatic file upload is not supported yet. You still have to manually place " +
				"the files (e.g. service client jar) to the domain authority node.");
		
		for (URI f : fileUris) {
			files.add(f.getPath());
		}

		addService(serviceId, slaXml, fileServer, files, callback);
	}

	@Override
	public void addService(ServiceResourceIdentifier serviceId, String slaXml, URI clientJarServer,
			String clientJarFilePath, INegotiationProviderSLMCallback callback) throws NegotiationException {
		
		List<String> files = new ArrayList<String>();
		files.add(clientJarFilePath);
		addService(serviceId, slaXml, clientJarServer, files, callback);
	}
	
	@Override
	public void removeService(ServiceResourceIdentifier serviceId) {
		
		String idStr = serviceId.getIdentifier().toString();
		
		services.remove(idStr);
	}

	protected HashMap<String, Service> getServices() {
		return services;
	}
	
	protected Service getService(String id) {
		
		Service s = services.get(id);
		
		if (s == null) {
			LOG.warn("getService({}): service not found", id);
		}
		
		return s;
	}

	/**
	 * 
	 * @param serviceId
	 * @return
	 * @throws NegotiationException When service is not found
	 */
	protected List<URI> getSignedUris(String serviceId) throws NegotiationException {

		List <URI> uri = new ArrayList<URI>();
		String uriStr;
		String host;
		String sig;
		List <String> filePath;
		Service s = getService(serviceId);
		
		if (s == null) {
			throw new NegotiationException("Service " + serviceId + " not found");
		}

		host = s.getFileServerHost().toString();
		filePath = s.getFiles();
		
		for (int k = 0; k < filePath.size(); k++) {
			try {
				sig = signatureMgr.sign(filePath.get(k), groupMgr.getIdMgr().getThisNetworkNode());
			} catch (DigsigException e) {
				LOG.error("Failed to sign file " + filePath.get(k) + " of service " + serviceId, e);
				throw new NegotiationException(e);
			}
			uriStr = host + UrlPath.BASE + UrlPath.PATH + "/" + filePath.get(k).replaceAll(".*/", "") +
					"?" + UrlPath.URL_PARAM_FILE + "=" + filePath.get(k) +
					"&" + UrlPath.URL_PARAM_SERVICE_ID + "=" + serviceId +
					"&" + UrlPath.URL_PARAM_SIGNATURE + "=" + sig;
			
			try {
				uri.add(new URI(uriStr));
			} catch (URISyntaxException e) {
				throw new NegotiationException(e);
			}
		}
		return uri;
	}

	/**
	 * 
	 * @param id
	 * @return
	 * @throws NegotiationException When service is not found
	 */
	protected String getSlaXmlOptions(String id) throws NegotiationException {
		
		Service s = getService(id);
		
		if (s != null) {
			return s.getSlaXmlOptions();
		}
		else {
			throw new NegotiationException("Service " + id + " not found");
		}
	}
}
