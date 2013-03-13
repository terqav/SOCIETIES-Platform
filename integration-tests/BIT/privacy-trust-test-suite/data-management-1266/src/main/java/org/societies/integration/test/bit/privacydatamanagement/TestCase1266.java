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
package org.societies.integration.test.bit.privacydatamanagement;

/**
 * The test case 1266 aims to test the privacy data management
 * real usage, using the privacy preference manager.
 * 
 * @author Olivier Maridat (Trialog)
 *
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.integration.test.IntegrationTestCase;

public class TestCase1266 extends IntegrationTestCase {
	private static Logger LOG = LoggerFactory.getLogger(TestCase1266.class.getSimpleName());

	public static IPrivacyDataManager privacyDataManager;
	public static IPrivacyPolicyManager privacyPolicyManager;
	public static ICommManager commManager;
	public static ICisManager cisManager;
	
	
	public TestCase1266() {
		// Call the super constructor
		// with test case number
		// and test case classes to run
		super(1266, new Class[]{PrivacyDataManagerTest.class, CisDataAccessControlTest.class});
		PrivacyDataManagerTest.testCaseNumber = this.testCaseNumber;
		CisDataAccessControlTest.testCaseNumber = this.testCaseNumber;
	}
	
	
	/* -- Dependency injection --- */
	public void setPrivacyDataManager(IPrivacyDataManager privacyDataManager) {
		this.privacyDataManager = privacyDataManager;
		LOG.info("[#"+testCaseNumber+"] [DependencyInjection] IPrivacyDataManager injected");
	}
	public void setPrivacyPolicyManager(IPrivacyPolicyManager privacyPolicyManager) {
		this.privacyPolicyManager = privacyPolicyManager;
		LOG.info("[#"+testCaseNumber+"] [DependencyInjection] IPrivacyPolicyManager injected");
	}
	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
		LOG.info("[#"+testCaseNumber+"] [DependencyInjection] ICommManager injected");
	}
	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
		LOG.info("[#"+testCaseNumber+"] [DependencyInjection] ICisManager injected");
	}

	public static boolean isDepencyInjectionDone() {
		return isDepencyInjectionDone(0);
	}
	public static boolean isDepencyInjectionDone(int level) {
		if (null == commManager) {
			LOG.info("[Dependency Injection] Missing ICommManager");
			return false;
		}
		if (null == commManager.getIdManager()) {
			LOG.info("[Dependency Injection] Missing IIdentityManager");
			return false;
		}
		if (null == privacyDataManager) {
			LOG.info("[Dependency Injection] Missing IPrivacyDataManager");
			return false;
		}
		if (null == privacyPolicyManager) {
			LOG.info("[Dependency Injection] Missing IPrivacyPolicyManager");
			return false;
		}
		if (null == cisManager) {
			LOG.info("[Dependency Injection] Missing ICisManager");
			return false;
		}
		return true;
	}
}