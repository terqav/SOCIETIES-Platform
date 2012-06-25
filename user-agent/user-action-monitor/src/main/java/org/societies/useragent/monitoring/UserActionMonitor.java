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

package org.societies.useragent.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.useragent.monitoring.UIMEvent;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.useragent.monitoring.IUserActionMonitor;

public class UserActionMonitor implements IUserActionMonitor{

	private static Logger LOG = LoggerFactory.getLogger(UserActionMonitor.class);
	private boolean cloud;
	private ICtxBroker ctxBroker;
	private IEventMgr eventMgr;
	private ICommManager commsMgr;
	private ContextCommunicator ctxComm;
	String myDeviceID;

	@Override
	public void monitor(IIdentity owner, IAction action) {
		LOG.info("UAM - Received user action!");
		LOG.info("action ServiceId: "+action.getServiceID().toString());
		LOG.info("action serviceType: "+action.getServiceType());
		LOG.info("action parameterName: "+action.getparameterName());
		LOG.info("action value: "+action.getvalue());

		//save action in context - IIdentity (Person) > ServiceId > paramName
		//create new entities and attributes if necessary
		ctxComm.updateHistory(owner, action);

		//update interactionDevice if NOT on cloud node
		if(!cloud){  //CHANGE
			ctxComm.updateUID(owner, myDeviceID);
		}

		//send local event
		UIMEvent payload = new UIMEvent(owner, action);
		InternalEvent event = new InternalEvent(EventTypes.UIM_EVENT, "newaction", "org/societies/useragent/monitoring", payload);
		try {
			eventMgr.publishInternalEvent(event);
		} catch (EMSException e) {
			e.printStackTrace();
		}
	}

	public void initialiseUserActionMonitor(){
		System.out.println("Initialising user action monitor!");
		ctxComm = new ContextCommunicator(ctxBroker);

		//get device type from CSS Manager
		IdentityType nodeType = commsMgr.getIdManager().getThisNetworkNode().getType();
		
		//get myDeviceID from comms Mgr
		myDeviceID = commsMgr.getIdManager().getThisNetworkNode().getJid();
	}

	public void setCtxBroker(ICtxBroker broker){
		this.ctxBroker = broker;
	}

	public void setEventMgr(IEventMgr eventMgr){
		this.eventMgr = eventMgr;
	}
	
	public void setCommsMgr(ICommManager commsMgr){
		this.commsMgr = commsMgr;
	}
	
}
