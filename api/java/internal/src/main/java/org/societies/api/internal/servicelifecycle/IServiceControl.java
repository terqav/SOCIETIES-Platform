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
package org.societies.api.internal.servicelifecycle;

import java.net.URL;
import java.util.concurrent.Future;

import org.societies.api.identity.IIdentity;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResult;
import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;

/**
 * 
 * The interface class for the Service Control component. It permits a caller to tell the SLM to
 * start a service, to stop a service, to install a new service and to uninstall a service.
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 */
@SocietiesExternalInterface(type = SocietiesInterfaceType.PROVIDED)
public interface IServiceControl {
		
	/**
	 * This method starts the service that is identified by the </code>ServiceResourceIdentifier</code>
	 * 
	 * @param serviceId unique service identifier
	 * @return the result of the operation
	 */
	
	public Future<ServiceControlResult> startService(ServiceResourceIdentifier serviceId) throws ServiceControlException;

	
	/**
	 * This method stops the service running in the container that is identified by the </code>ServiceResourceIdentifier</code>
	 * 
	 * @param serviceId unique service identifier
	 * @return the result of the operation
	 */
	public Future<ServiceControlResult> stopService(ServiceResourceIdentifier serviceId) throws ServiceControlException;

	/**
	 * This shares a Service with a given CSS or CIS
	 * 
	 * @param service The Service to be shared
	 * @param jid The jid of the node we are sharing the service with
	 * @return the result of the operation
	 */
	public Future<ServiceControlResult> shareService(Service service, String jid) throws ServiceControlException;

	/**
	 * This shares a Service with a given CSS or CIS
	 * 
	 * @param service The Service to be shared
	 * @param node The node we are sharing the service with
	 * @return the result of the operation
	 */
	public Future<ServiceControlResult> shareService(Service service, IIdentity node) throws ServiceControlException;

	/**
	 * This method installs a shared service into the container
	 * 
	 * @param service the Remote Service to install
	 * @return the result of the operation
	 */
	public Future<ServiceControlResult> installService(Service service) throws ServiceControlException;

	/**
	 * This method installs a new service into the container present on a given node
	 * 
	 * @param service the Remote Service to install
	 * @param node The node where we wish to install the service
	 * @return the result of the operation
	 */
	public Future<ServiceControlResult> installService(Service service, IIdentity node) throws ServiceControlException;

	/**
	 * This method installs a new service into the container present on a given node, given by the jid
	 * 
	 * @param service the Remote Service to install
	 * @param jid The node where we wish to install the service
	 * @return the result of the operation
	 */
	public Future<ServiceControlResult> installService(Service service, String jid) throws ServiceControlException;

	/**
	 * This method installs a new service into the container
	 * 
	 * @param bundleLocation the URL of the bundle to install
	 * @return the result of the operation
	 */
	public Future<ServiceControlResult> installService(URL bundleLocation) throws ServiceControlException;

	/**
	 * This method installs a new service into the container present on a given node
	 * 
	 * @param bundleLocation the URL of the bundle to install
	 * @param node The node where we wish to install the service
	 * @return the result of the operation
	 */
	public Future<ServiceControlResult> installService(URL bundleLocation, IIdentity node) throws ServiceControlException;

	/**
	 * This method installs a new service into the container present on a given node, given by the jid
	 * 
	 * @param bundleLocation the URL of the bundle to install
	 * @param jid The node where we wish to install the service
	 * @return the result of the operation
	 */
	public Future<ServiceControlResult> installService(URL bundleLocation, String jid) throws ServiceControlException;

	/**
	 * This method removes a service from the container.
	 * 
	 * @param serviceId unique service identifier
	 * @return the result of the operation
	 */
	public Future<ServiceControlResult> uninstallService(ServiceResourceIdentifier serviceId) throws ServiceControlException;

	/**
	 * This unshares a Service with a given CSS or CIS
	 * 
	 * @param service The Service to be unshared
	 * @param jid The jid of the node we are unsharing the service with
	 * @return the result of the operation
	 */
	public Future<ServiceControlResult> unshareService(Service service, String jid) throws ServiceControlException;

	/**
	 * This unshares a Service with a given CSS or CIS
	 * 
	 * @param service The Service to be unshared
	 * @param node The node we are unsharing the service with
	 * @return the result of the operation
	 */
	public Future<ServiceControlResult> unshareService(Service service, IIdentity node) throws ServiceControlException;
	
	public void cleanAfterRestart();
	
}
