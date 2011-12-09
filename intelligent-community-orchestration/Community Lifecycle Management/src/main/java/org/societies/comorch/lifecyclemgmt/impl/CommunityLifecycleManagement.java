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

package org.societies.comorch.lifecyclemgmt.impl;

import org.societies.context.broker.api.IUserCtxBroker;
import org.societies.context.broker.api.ICommunityCtxBroker;

/**
 * This is the class for the Community Lifecycle Management component
 * 
 * Superclass of the three community lifecycle manager components, delegates CIS
 * lifecycle orchestration work to them.
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

public class CommunityLifecycleManagement {
	
	private AutomaticCommunityCreationManager autoCreationManager;
	private AutomaticCommunityConfigurationManager autoConfigurationManager;
	private AutomaticCommunityDeletionManager autoDeletionManager;
	
	private Css linkedCss;
	private EntityIdentifier dpi;
	
    private CisRecord linkedCis;
    
    private Domain linkedDomain;
	
	/*
     * Constructor for CommunityLifecycleManagement
     * 
	 * Description: The constructor creates the CommunityLifecycleManagement
	 *              component either on a given CSS, or abstractly at a domain/cloud-level.
	 * Parameters: 
	 * 				
	 */
	
	public CommunityLifecycleManagement(Css linkedCss, EntityIdentifier dpi) {
		this.linkedCss = linkedCss;
		this.dpi = dpi;
	}
	
	/*
     * Constructor for CommunityLifecycleManagement
     * 
	 * Description: The constructor creates the CommunityLifecycleManagement
	 *              component either on a given CSS, or abstractly at a domain/cloud-level.
	 * Parameters: 
	 * 				
	 */
	
	public CommunityLifecycleManagement(Domain linkedDomain) {
		this.linkedDomain = linkedDomain;
	}
	
	/*
     * Constructor for CommunityLifecycleManagement
     * 
	 * Description: The constructor creates the CommunityLifecycleManagement
	 *              component either on a given CSS, or abstractly at a domain/cloud-level.
	 * Parameters: 
	 * 				
	 */
	
	public CommunityLifecycleManagement(CisRecord linkedCis) {
		this.linkedCis = linkedCis;
	}
	
	/**
	 * User Interface method - trigger creation of CIS following UI request
	 */
	
	public void createCiss() {
		
	}
	
	/**
	 * User Interface method - trigger configuration of CIS following UI request.
	 */
	
	public void configureCiss() {
		
	}
	
	/**
	 * User Interface method - trigger deletion of CIS following UI request.
	 */
	
	public void deleteCiss() {
		
	}
	
	public void processPreviousLongTimeCycle() {
		autoCreationManager.determinCissToCreate("extensive");
		autoConfigurationManager.determineCissToConfigure("extensive");
		autoDeletionManager.determineCissToDelete("extensive");
	}
	
	public void processPreviousShortTimeCycle() {
		autoCreationManager.determineCissToCreate("not extensive");
		autoConfigurationManager.determineCissToConfigure("not extensive");
	}
	
	public void loop() {
		
		new Thread.start() {
			while (true) {
				Thread.sleep(10000);
				processPreviousShortTimeCycle;
		    }
		};
		
		new Thread.start() {
			while (true) {
				Thread.sleep(220000000);
				processPreviousLongTimeCycle;
		    }
		};
		
	}
	
	public void stimulusForCommunityCreationDetected() {
		autoCreationManager.determineCissToCreate();
	}
	
	public void stimulusForCommunityDeletionDetected() {
		autoDeletionManager.determineCissToDelete();
		
	}
}