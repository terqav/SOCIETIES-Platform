/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druÅ¾be in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÃ‡ÃƒO, SA (PTIN), IBM Corp., 
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
package org.societies.platform.servicelifecycle.servicecontrol;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.jar.JarFile;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.css.devicemgmt.model.DeviceMgmtConstants;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderSLMCallback;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderServiceMgmt;
import org.societies.api.internal.servicelifecycle.IServiceControl;
import org.societies.api.internal.servicelifecycle.ServiceControlException;
import org.societies.api.internal.servicelifecycle.ServiceMgmtInternalEvent;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceNotFoundException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRegistrationException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRetrieveException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceSharingNotificationException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceUpdateException;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceImplementation;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.model.ServiceStatus;
import org.societies.api.schema.servicelifecycle.model.ServiceInstance;
import org.societies.api.schema.servicelifecycle.model.ServiceType;
import org.societies.api.services.ServiceMgmtEventType;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.osgi.util.OsgiListenerUtils;
import org.springframework.scheduling.annotation.Async;

/**
 * 
 * This class implements the Service Registry Listener, that registers and unregisters services
 * 
 * @author pkuppuud
 * @author mmanniox
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 * 
 */

public class ServiceRegistryListener implements BundleContextAware,
		ServiceListener, BundleListener {

	private BundleContext bctx;
	private static Logger log = LoggerFactory.getLogger(ServiceRegistryListener.class);
	private IServiceRegistry serviceReg;
	private ICommManager commMngr;
	private INegotiationProviderServiceMgmt negotiationProvider;
	private IServiceControl serviceControl;
	private IPrivacyPolicyManager privacyManager;
	private IEventMgr eventMgr;

	public IEventMgr getEventMgr(){
		return eventMgr;
	}
	
	public void setEventMgr(IEventMgr eventMgr){
		this.eventMgr=eventMgr;
	}
	
	public IPrivacyPolicyManager getPrivacyManager(){
		return privacyManager;
	}
	
	public void setPrivacyManager(IPrivacyPolicyManager privacyManager){
		this.privacyManager = privacyManager;
	}
	
	public IServiceControl getServiceControl(){
		return serviceControl;
	}
	
	public void setServiceControl(IServiceControl serviceControl){
		this.serviceControl = serviceControl;
	}
	
	  public INegotiationProviderServiceMgmt getNegotiationProvider(){
		return negotiationProvider;
	}
	
	public void setNegotiationProvider(INegotiationProviderServiceMgmt negotiationProvider){
		this.negotiationProvider = negotiationProvider;
	}
	
	public IServiceRegistry getServiceReg() {
		return serviceReg;
	}

	public void setServiceReg(IServiceRegistry serviceReg) {
		this.serviceReg = serviceReg;
	}

	public ICommManager getCommMngr() {
		return commMngr;
	}
	
	public void setCommMngr(ICommManager commMngr) {
		this.commMngr = commMngr;
	}
	
	public ServiceRegistryListener() {
		log.info("Service RegistryListener Bean Instantiated");
	}


	
	public void registerListener() {
		
		Filter fltr = null;
		
		if(log.isDebugEnabled()) 
			log.debug("Registering Listener!");
		
		try {
			fltr = this.bctx.createFilter("(TargetPlatform=SOCIETIES)");
			log.info("Service Filter Registered");
		} catch (InvalidSyntaxException e) {
			log.error("Error creating Service Listener Filter");
			e.printStackTrace();
		}
		OsgiListenerUtils.addServiceListener(this.bctx, this, fltr);
		
		log.info("Bundle Listener Registered");
		this.bctx.addBundleListener(this);
		
		getServiceControl().cleanAfterRestart();
		
	}

	public void unRegisterListener() {
		log.info("Service Management unregistering service listener");
		OsgiListenerUtils.removeServiceListener(this.bctx, this);
		log.info("Service Management unregistering bundle listener");
		this.bctx.removeBundleListener(this);
	}

	@Override
	public void setBundleContext(BundleContext ctx) {
		this.bctx = ctx;
	}

	@Override
	public void serviceChanged(ServiceEvent event) {

		if(log.isDebugEnabled())
			log.debug("Service Listener event received");

		Service service = getServiceFromOSGIEvent(event);
		
		if(service == null){
			log.warn("Couldn't get information from service!");
			return;
		}
		
		Bundle serBndl = event.getServiceReference().getBundle();
		
		List<Service> serviceList = new ArrayList<Service>();

		switch (event.getType()) {

		case ServiceEvent.MODIFIED:
			if(log.isDebugEnabled()) log.debug("Service Modification");

			/*serviceList.add(service);
			this.getServiceReg().unregisterServiceList(serviceList);
			this.getServiceReg().registerServiceList(serviceList);
			*/
			try {
				this.getServiceReg().updateRegisteredService(service);
			} catch (ServiceUpdateException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
			
		case ServiceEvent.REGISTERED:
			
			if(log.isDebugEnabled()) log.debug("Service Registering...");			
			//service.setServiceIdentifier(ServiceModelUtils.generateServiceResourceIdentifier(service, serBndl));
			
			if(log.isDebugEnabled())
				log.debug("Service Identifier generated: " + service.getServiceIdentifier().getIdentifier().toString()+"_"+service.getServiceIdentifier().getServiceInstanceIdentifier());
			
			serviceList.add(service);
			
			try {
				if(log.isDebugEnabled()) log.debug("First, checking if the service already exists...");
			
				Service existService = this.getServiceReg().retrieveService(service.getServiceIdentifier());
				
				if(existService == null){

					if(ServiceModelUtils.isServiceOurs(service, getCommMngr()) && !service.getServiceType().equals(ServiceType.THIRD_PARTY_CLIENT) && !service.getServiceType().equals(ServiceType.DEVICE)){
						if(!updateSecurityPolicy(service,serBndl) || !updatePrivacyPolicy(service,serBndl)){
							log.warn("Adding security and privacy failed!");
							return;
						}
					}
					
					if(log.isDebugEnabled()) log.debug("Registering Service: " + service.getServiceName());
					this.getServiceReg().registerServiceList(serviceList);
					
					if(!service.getServiceType().equals(ServiceType.THIRD_PARTY_CLIENT)){
						sendEvent(ServiceMgmtEventType.NEW_SERVICE,service,serBndl);
						sendEvent(ServiceMgmtEventType.SERVICE_STARTED,service,serBndl);
					}
					
				} else{
					if(log.isDebugEnabled()) log.debug(service.getServiceName() + " already exists, setting status to STARTED");
					if(existService.getServiceStatus()==ServiceStatus.STARTED){
						if(log.isDebugEnabled())
							log.debug("This is a restart! We need to update everything!");
						if(ServiceModelUtils.isServiceOurs(service, getCommMngr()) && !service.getServiceType().equals(ServiceType.THIRD_PARTY_CLIENT) && !service.getServiceType().equals(ServiceType.DEVICE)){
							if(!updateSecurityPolicy(service,serBndl) || !updatePrivacyPolicy(service,serBndl)){
								log.warn("Adding security and privacy failed!");
								return;
							}
						} else{
							if(log.isDebugEnabled())
								log.debug("It's a restart, but this service doesn't need security & privacy updates");
						}
						sendEvent(ServiceMgmtEventType.NEW_SERVICE,service,serBndl);
						
					} else{
						if(log.isDebugEnabled())
							log.debug("Just restarting the service, no need to update stuff yet.");
					}
					
					this.getServiceReg().changeStatusOfService(service.getServiceIdentifier(), ServiceStatus.STARTED);
					sendEvent(ServiceMgmtEventType.SERVICE_STARTED,service,serBndl);

				}
					
				//The service is now registered, so we update the hashmap
				if(ServiceControl.installingBundle(serBndl.getBundleId())){
					if(log.isDebugEnabled())
						log.debug("ServiceControl is installing the bundle, so we need to tell it it's done");
					ServiceControl.serviceInstalled(serBndl.getBundleId(), service);
				}
				
				
			} catch (Exception e) {
				log.error("Error while persisting service meta data");
				e.printStackTrace();
			}
			break;
			
		case ServiceEvent.UNREGISTERING:
			
			if(!service.getServiceType().equals(ServiceType.DEVICE)){
				
				if(log.isDebugEnabled()) log.debug("Service Unregistered, so we set it to stopped but do not remove from registry");			
				//service.setServiceIdentifier(ServiceModelUtils.generateServiceResourceIdentifier(service, serBndl));
				
				try {
					this.getServiceReg().changeStatusOfService(service.getServiceIdentifier(), ServiceStatus.STOPPED);
					sendEvent(ServiceMgmtEventType.SERVICE_STOPPED,service,serBndl);

				} catch (ServiceNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				if(log.isDebugEnabled()) log.debug("It's a DEVICE, so we need to remove it.");
				
				List<Service> servicesToRemove = new ArrayList<Service>();
				servicesToRemove.add(service);
							
				try {
					
					if(log.isDebugEnabled()) log.debug("Checking if service is shared with any CIS, and removing that");
					List<String> cisSharedList = getServiceReg().retrieveCISSharedService(service.getServiceIdentifier());
					
					if(!cisSharedList.isEmpty()){
						for(String cisShared: cisSharedList){
							if(log.isDebugEnabled()) log.debug("Removing sharing to CIS: " + cisShared);
							try {
								getServiceControl().unshareService(service, cisShared);
							} catch (ServiceControlException e) {
								e.printStackTrace();
								log.error("Couldn't unshare from that CIS!");
							}
						}
					} else{
						if(log.isDebugEnabled()) log.debug("Service not shared with any CIS!");
					}
					
					if(log.isDebugEnabled())
						log.debug("Removing the shared service from the policy provider!");
					
					getNegotiationProvider().removeService(service.getServiceIdentifier());
				
					if(log.isDebugEnabled()) log.debug("Removing service: " + service.getServiceName() + " from SOCIETIES Registry");

					getServiceReg().unregisterServiceList(servicesToRemove);
					log.info("Service " + service.getServiceName() + " has been uninstalled");

					sendEvent(ServiceMgmtEventType.SERVICE_STOPPED,service,serBndl);
					sendEvent(ServiceMgmtEventType.SERVICE_REMOVED,service,serBndl);
					
				} catch (ServiceRegistrationException e) {
					e.printStackTrace();
					log.error("Exception while unregistering service: " + e.getMessage());
				}
			}
			
			//The service is now registered, so we update the hashmap
			if(ServiceControl.installingBundle(serBndl.getBundleId())){
				if(log.isDebugEnabled())
					log.debug("ServiceControl is stopping the bundle, so we need to tell it it's done");
				ServiceControl.serviceInstalled(serBndl.getBundleId(), service);
			}
			
		}
	}

	@Override
	public void bundleChanged(BundleEvent event) {
				
		if( event.getType() != BundleEvent.UNINSTALLED ){
			return;
		}
		
		if(log.isDebugEnabled())
			log.debug("Bundle Uninstalled Event arrived!");
		// Now we search for services in the registry corresponding to this bundle.
		Service serviceToRemove = getServiceFromBundle(event.getBundle());
		
		if(serviceToRemove == null){
			if(log.isDebugEnabled())
				log.debug("It was not a SOCIETIES-related bundle, ignoring event!");
			return;
		}
		
		if(log.isDebugEnabled())
			log.debug("Uninstalled bundle that had service: " + serviceToRemove.getServiceEndpoint());
				
		List<Service> servicesToRemove = new ArrayList<Service>();
		servicesToRemove.add(serviceToRemove);
					
		try {
			
			if(log.isDebugEnabled()) log.debug("Checking if service is shared with any CIS, and removing that");
			List<String> cisSharedList = getServiceReg().retrieveCISSharedService(serviceToRemove.getServiceIdentifier());
			
			if(!cisSharedList.isEmpty()){
				for(String cisShared: cisSharedList){
					if(log.isDebugEnabled()) log.debug("Removing sharing to CIS: " + cisShared);
					try {
						getServiceControl().unshareService(serviceToRemove, cisShared);
					} catch (ServiceControlException e) {
						e.printStackTrace();
						log.error("Couldn't unshare from that CIS!");
					}
				}
			} else{
				if(log.isDebugEnabled()) log.debug("Service not shared with any CIS!");
			}
			
			if(log.isDebugEnabled())
					log.debug("Removing the shared service from the policy provider!");
			getNegotiationProvider().removeService(serviceToRemove.getServiceIdentifier());
			
			
			if(log.isDebugEnabled())
				log.debug("Removing the privacy policy for the service!");
			IIdentity myNode = getCommMngr().getIdManager().getThisNetworkNode();
			RequestorService requestService = new RequestorService(myNode , serviceToRemove.getServiceIdentifier());

			try{
				getPrivacyManager().deletePrivacyPolicy(requestService);
			} catch (PrivacyException e) {
				log.error("Exception while removing privacy policy: " + e.getMessage());
				e.printStackTrace();
			}
			
			if(log.isDebugEnabled()) log.debug("Removing service: " + serviceToRemove.getServiceName() + " from SOCIETIES Registry");

			getServiceReg().unregisterServiceList(servicesToRemove);
			log.info("Service " + serviceToRemove.getServiceName() + " has been uninstalled");
			
			sendEvent(ServiceMgmtEventType.SERVICE_REMOVED,serviceToRemove,event.getBundle());
						
		} catch (ServiceRegistrationException e) {
			e.printStackTrace();
			log.error("Exception while unregistering service: " + e.getMessage());
		}
		
		//The service is now registered, so we update the hashmap
		if(ServiceControl.uninstallingBundle(event.getBundle().getBundleId())){
			if(log.isDebugEnabled())
				log.debug("ServiceControl is uninstalling the bundle, so we need to tell it it's done");
			ServiceControl.serviceUninstalled(event.getBundle().getBundleId(), serviceToRemove);
		}
		
	}
	
	
	/**
	 * This method is used to obtain the Service that is exposed by given Bundle
	 * 
	 * @param The Bundle that exposes this service
	 * @return The Service object whose bundle we wish to find
	 */
	private Service getServiceFromBundle(Bundle bundle) {
		
		if(log.isDebugEnabled()) log.debug("Obtaining Service that corresponds to a bundle: " + bundle.getSymbolicName());
		
		// Preparing the search filter		
		Service filter = ServiceModelUtils.generateEmptyFilter();
		filter.getServiceIdentifier().setServiceInstanceIdentifier(bundle.getSymbolicName());
		filter.setServiceLocation(bundle.getLocation());
		//filter.getServiceInstance().getServiceImpl().setServiceVersion(bundle.getVersion().toString());
		
		List<Service> listServices;
		try {
			listServices = getServiceReg().findServices(filter);
		} catch (ServiceRetrieveException e) {
			log.error("Exception while searching for services:" + e.getMessage());
			e.printStackTrace();
			return null;
		}
		

		if(listServices == null || listServices.isEmpty()){
			if(log.isDebugEnabled()) log.debug("Couldn't find any services that fulfill the criteria");
			return null;
		} 
		
		Service result = null;

		for(Service service: listServices){
			String bundleSymbolic = service.getServiceIdentifier().getServiceInstanceIdentifier();
			
			if(bundleSymbolic.equals(bundle.getSymbolicName())){
				result = service;
				break;
			}
		}
		
		 if(log.isDebugEnabled() && result != null) 
			 log.debug("The service corresponding to bundle " + bundle.getSymbolicName() + " is "+ result.getServiceName() );
			
		// Finally, we return
		 return result;
		 
	}
	
	private boolean updateSecurityPolicy(Service service, Bundle bundle){
		
		try{
							
			if(log.isDebugEnabled())
				log.debug("Adding the shared service to the policy provider!");
				
			String slaXml = null;
			URI clientJar = null;
			INegotiationProviderSLMCallback callback = new ServiceNegotiationCallback();
			
			if(log.isDebugEnabled())
				log.debug("serviceClient: "+ service.getServiceInstance().getServiceImpl().getServiceClient());
			
			if(service.getServiceType().equals(ServiceType.THIRD_PARTY_WEB))
				clientJar= new URI("http://www.societies.org/webapp/webservice.test");
			else{
				if(service.getServiceInstance().getServiceImpl().getServiceClient() != null)
					clientJar= new URI(service.getServiceInstance().getServiceImpl().getServiceClient());
			}
	
			URI clientHost = null;
			if(clientJar!=null){
				if(clientJar.getPort()!= -1)
					clientHost = new URI("http://" + clientJar.getHost() +":"+ clientJar.getPort());
				else
					clientHost = new URI("http://" + clientJar.getHost() );
	
				if(log.isDebugEnabled())
					log.debug("With the path: " + clientJar.getPath() + " on host " + clientHost);
			
				getNegotiationProvider().addService(service.getServiceIdentifier(), slaXml, clientHost, clientJar.getPath(), callback);
			} else{
				getNegotiationProvider().addService(service.getServiceIdentifier(), slaXml, clientHost, new ArrayList<String>(), callback);
			}
			//addService(service.getServiceIdentifier(), clientHost, clientJar.getPath());	
			
		} catch(Exception ex){
			log.error("Error while adding security policy for Service: " + ex.getMessage());
			ex.printStackTrace();
			return false;
		}
		
		return true;
	}
		
	private boolean updatePrivacyPolicy(Service service, Bundle bundle){
			
		try{
			if(log.isDebugEnabled())
				log.debug("Adding privacy policy for " + service.getServiceName());
			
			//First we check if we have a privacy policy online
			String privacyLocation = service.getPrivacyPolicy();
			String privacyPolicy = null;
				
			privacyLocation = null;
			if(privacyLocation != null){
				if(log.isDebugEnabled())
					log.debug("Privacy Policy url is supplied, trying to use that.");
					
				privacyPolicy = new Scanner( new URL(privacyLocation).openStream(), "UTF-8").useDelimiter("\\A").next();
			} else{
	
				if(log.isDebugEnabled())
					log.debug("Privacy Policy url not supplied, looking inside bundle jar: " + bundle.getLocation());

				String privacyPath = bundle.getLocation();
				int index = privacyPath.indexOf('@');	
				privacyLocation = privacyPath.substring(index+1);
				
				File bundleFile = new File(new URI(privacyLocation));
					
				if(bundleFile.isDirectory()){
					if(log.isDebugEnabled())
						log.debug("OSGI expanded .jar, getting privacy-policy directly");
				
					privacyLocation = privacyLocation + "/privacy-policy.xml";
					privacyPolicy = new Scanner( new URL(privacyLocation).openStream(), "UTF-8").useDelimiter("\\A").next();
					
				} else 
					if(bundleFile.isFile()) {
						if(log.isDebugEnabled())
							log.debug("OSGI didn't expand .jar, getting privacy-policy from inside.");
						
						JarFile jarFile = new JarFile(bundleFile);	
						privacyPolicy = new Scanner( jarFile.getInputStream(jarFile.getEntry("privacy-policy.xml")) ).useDelimiter("\\A").next();
					
					} else{
						if(log.isDebugEnabled())
							log.debug("Couldn't get privacy-policy from jar!");
					}
			}

			if(privacyPolicy == null){
				if(log.isDebugEnabled())
					log.debug("Couldn't get privacy policy from jar, aborting process!");
				return false;
			}
				
			IIdentity myNode = getCommMngr().getIdManager().getThisNetworkNode();
			RequestorService requestService = new RequestorService(myNode, service.getServiceIdentifier());
			RequestPolicy policyResult = getPrivacyManager().updatePrivacyPolicy(privacyPolicy, requestService);
			
			if(policyResult == null){
				if(log.isDebugEnabled())
					log.debug("Error adding privacyPolicy to Privacy Manager!");
				return true;
			} else{
				if(log.isDebugEnabled())
					log.debug("Added privacyPolicy to Privacy Manager!");
				return true;
			}
			
						
		} catch(Exception ex){
			log.error("Exception while trying to update Privacy Policy!");
			ex.printStackTrace();
			return false;
		}

	}
	
	private Service getServiceFromOSGIEvent(ServiceEvent event){
		
		Bundle serBndl = event.getServiceReference().getBundle();
		String propKeys[] = event.getServiceReference().getPropertyKeys();

		if(log.isDebugEnabled()){
			for (String key : propKeys) {
				log.debug("Property Key: " + key);
				Object value = event.getServiceReference().getProperty(key);
				log.debug("Property value: " + value);
				// serviceMeteData.put(key, value);
			}
		
			log.debug("Bundle Id: " + serBndl.getBundleId() + " Bundle State: "
				+ serBndl.getState() + "  Bundle Symbolic Name: "
				+ serBndl.getSymbolicName());
		}
		
		Service service = (Service) event.getServiceReference().getProperty(
				"ServiceMetaModel");
		
		if(service==null || (!(service instanceof Service) )){
			if(log.isDebugEnabled()) log.debug("**Service MetadataModel object is null**");
			return null;
		}
			
		//TODO DEAL WITH THIS
		service.setServiceEndpoint(commMngr.getIdManager().getThisNetworkNode().getJid()  + "/" +  service.getServiceName().replaceAll(" ", ""));

		//TODO: Do this properly!
		ServiceInstance si = new ServiceInstance();
		
		INetworkNode myNode = commMngr.getIdManager().getThisNetworkNode();
		
		if(service.getServiceType().equals(ServiceType.DEVICE)){
			String nodeId = (String)event.getServiceReference().getProperty(DeviceMgmtConstants.DEVICE_NODE_ID);
			
			if(log.isDebugEnabled())
				log.debug("This is device... : " + nodeId);
				
			try{
				INetworkNode serviceNode = getCommMngr().getIdManager().fromFullJid(nodeId);
				
				si.setFullJid(serviceNode.getJid());
				si.setCssJid(serviceNode.getBareJid());
				si.setParentJid(serviceNode.getBareJid()); //This is later changed!
				si.setXMPPNode(serviceNode.getNodeIdentifier());
			
			}catch(Exception ex){
				ex.printStackTrace();
				log.warn("Exception in IdManager, doing alternate solution!");
				si.setFullJid(nodeId);
				si.setCssJid(nodeId);
				si.setParentJid(nodeId); //This is later changed!
				si.setXMPPNode(myNode.getNodeIdentifier());
			}
			
		} else{
			si.setFullJid(myNode.getJid());
			si.setCssJid(myNode.getBareJid());
			si.setParentJid(myNode.getBareJid()); //This is later changed!
			si.setXMPPNode(myNode.getNodeIdentifier());
			service.setServiceLocation(serBndl.getLocation());
		}
		
		ServiceImplementation servImpl = new ServiceImplementation();
		servImpl.setServiceVersion((String)event.getServiceReference().getProperty("Bundle-Version"));
		
		if(service.getServiceType().equals(ServiceType.DEVICE))
			servImpl.setServiceNameSpace("device."+service.getServiceName()+"."+myNode.getBareJid());
		else
			servImpl.setServiceNameSpace(serBndl.getSymbolicName());
		
		servImpl.setServiceProvider((String) event.getServiceReference().getProperty("ServiceProvider"));
		servImpl.setServiceClient((String) event.getServiceReference().getProperty("ServiceClient"));
		
		si.setServiceImpl(servImpl);
		service.setServiceInstance(si);
		
		if(log.isDebugEnabled()){
			log.debug("**Service MetadataModel Data Read**");
			log.debug("Service Name: "+service.getServiceName());
			log.debug("Service Description: "+service.getServiceDescription());
			log.debug("Service type: "+service.getServiceType().toString());
			log.debug("Service Location: "+service.getServiceLocation());
			log.debug("Service Endpoint: "+service.getServiceEndpoint());
			log.debug("Service PrivacyPolicy: "+service.getPrivacyPolicy());
			log.debug("Service SecurityPolicy: "+service.getSecurityPolicy());
			log.debug("Service SecurityPolicy: "+service.getContextSource());
			log.debug("Service Provider: "+service.getServiceInstance().getServiceImpl().getServiceProvider());
			log.debug("Service Namespace: "+service.getServiceInstance().getServiceImpl().getServiceNameSpace());
			log.debug("Service ServiceClient: "+service.getServiceInstance().getServiceImpl().getServiceClient());
			log.debug("Service Version: "+service.getServiceInstance().getServiceImpl().getServiceVersion());
			log.debug("Service XMPPNode: "+service.getServiceInstance().getXMPPNode());
			log.debug("Service FullJid: "+service.getServiceInstance().getFullJid());
			log.debug("Service CssJid: "+service.getServiceInstance().getCssJid());
		}
		
		service.setServiceStatus(ServiceStatus.STARTED);
		
		String deviceId = null;
		deviceId = (String) event.getServiceReference().getProperty("DeviceId");
		if(service.getServiceType().equals(ServiceType.DEVICE) && deviceId == null){
			log.warn("Service Type is DEVICE but no device Id. Aborting");
			return null;
		}
		
		if(!service.getServiceType().equals(ServiceType.DEVICE))
			service.setServiceIdentifier(ServiceModelUtils.generateServiceResourceIdentifier(service, serBndl));
		else
			service.setServiceIdentifier(ServiceModelUtils.generateServiceResourceIdentifierForDevice(service, deviceId));
		
		si.setParentIdentifier(service.getServiceIdentifier());
		service.setServiceInstance(si);
		
		return service;		
	}
	
	private void sendEvent(ServiceMgmtEventType eventType, Service service, Bundle bundle){
		
		if(log.isDebugEnabled())
			log.debug("Sending event of type: " + eventType + " for service " + ServiceModelUtils.serviceResourceIdentifierToString(service.getServiceIdentifier()));
		
		ServiceMgmtInternalEvent serviceEvent = new ServiceMgmtInternalEvent();
		serviceEvent.setEventType(eventType);
		serviceEvent.setServiceType(service.getServiceType());
		serviceEvent.setServiceId(service.getServiceIdentifier());
		if(bundle != null){
			serviceEvent.setBundleId(bundle.getBundleId());
			serviceEvent.setBundleSymbolName(bundle.getSymbolicName());
		} else{
			serviceEvent.setBundleId(-1);
			serviceEvent.setBundleSymbolName(null);
		}

		InternalEvent internalEvent = new InternalEvent(EventTypes.SERVICE_LIFECYCLE_EVENT, eventType.toString(), "org/societies/servicelifecycle", serviceEvent);
		
		try {
			getEventMgr().publishInternalEvent(internalEvent);
		} catch (EMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("Error sending event!");
		}
	}
	
}
