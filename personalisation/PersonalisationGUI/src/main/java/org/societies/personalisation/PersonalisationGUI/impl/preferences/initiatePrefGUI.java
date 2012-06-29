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
package org.societies.personalisation.PersonalisationGUI.impl.preferences;




import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.preference.IUserPreferenceManagement;
import org.societies.api.osgi.event.IEventMgr;

/**
 * @scr.component name="PreferencesGUI" label="PreferencesGUI" immediate=true
 * 
 *  
 */
public class initiatePrefGUI {

	private GuiSelection gui;
	private IIdentityManager idMgr;
	private ICommManager commManager;
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	/**
	 * @scr.reference interface = "org.personalsmartspace.cm.broker.api.platform.ICtxBroker"
	 * cardinality="1..1" policy="static" bind="setCtxBroker" unbind="unsetCtxBroker"
	 * 
	 */
	private ICtxBroker broker;

	public void setCtxBroker(ICtxBroker broker){
		this.broker = broker;
	}

	public void getCtxBroker(ICtxBroker broker){
		this.broker = null;
	}


	private IUserPreferenceManagement prefMgr;

	public void setPrefMgr(IUserPreferenceManagement prefMgr){
		this.prefMgr = prefMgr;
	}

	public IUserPreferenceManagement getPrefMgr(){
		return this.prefMgr;
	}

	
	private IEventMgr evMgr;

	public void setEventMgr(IEventMgr evMgr){
		this.evMgr = evMgr;
	}

	public void getEventMgr(IEventMgr evMgr){
		this.evMgr = null;
	}
	
	/**
	 * @return the idMgr
	 */
	public IIdentityManager getIdMgr() {
		return idMgr;
	}

	/**
	 * @param idMgr the idMgr to set
	 */
	public void setIdMgr(IIdentityManager idMgr) {
		this.idMgr = idMgr;
	}

	public void initialiseGUI(){
		
		
		GuiSelectionPanel panel = new GuiSelectionPanel(this);
	
		
	}

	public void deactivate(ComponentContext cc){
		this.gui.setVisible(false);
		this.gui.dispose();
		this.gui = null;
	}


	/**
	 * @return the commManager
	 */
	public ICommManager getCommManager() {
		return commManager;
	}

	/**
	 * @param commManager the commManager to set
	 */
	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
		this.idMgr = commManager.getIdManager();
	}


}
