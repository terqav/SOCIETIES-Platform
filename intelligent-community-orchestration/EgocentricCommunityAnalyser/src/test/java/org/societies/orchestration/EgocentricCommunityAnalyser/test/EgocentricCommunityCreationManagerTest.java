/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske drÅ¾be in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOAÃ‡ÃƒO, SA (PTIN), IBM Corp., 
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

package org.societies.orchestration.EgocentricCommunityAnalyser.test;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.Calendar;

import org.junit.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

import org.societies.orchestration.EgocentricCommunityAnalyser.impl.EgocentricCommunityCreationManager;

import org.societies.api.identity.IIdentity;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;

import org.societies.api.internal.context.broker.ICtxBroker;

import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.internal.cis.management.ICisManager;
import org.societies.api.internal.cis.management.ICisRecord;

import org.societies.api.css.directory.ICssDirectory;
import org.societies.api.css.directory.ICssAdvertisementRecord;

import org.societies.api.css.management.ICssRecord;

/**
 * This is the test class for the Egocentric Community Creation Manager component
 * 
 * @author Fraser Blackmun
 * @version 1
 * 
 */

public class EgocentricCommunityCreationManagerTest {
	
	private EgocentricCommunityCreationManager egocentricCommunityCreationManager;
	private ICtxBroker userCtxBroker;
	//private IUserCtxDBMgr userCtxDBMgr;
	private CtxEntityIdentifier entityId;
	//private IUserCtxBrokerCallback userCtxBrokerCallback;
	private ICisManager cisManager;
	
	private ICssDirectory cssDirectory;
	
	//@Test
	public void testNonExtensiveCreationCheck() {
		
		IIdentity ownerId = mock(IIdentity.class); //James Jents CSS
		CtxEntityIdentifier entityId = new CtxEntityIdentifier(ownerId.toString(), "James Jents", new Long(1));
    	
    	
		cisManager = mock(ICisManager.class);
		userCtxBroker = mock(ICtxBroker.class);
		
		
    	egocentricCommunityCreationManager = new EgocentricCommunityCreationManager(ownerId, "CSS");
		
    	
    	
    	//create some test context data for the CSS?
    	//ServiceResourceIdentifier userInterfacedService = new ServiceResourceIdentifier(/**new URI("Chat system")*/);
    	
    	Future <CtxEntity> theEntity = null;
    	try {
			if (userCtxBroker != null)
    		    theEntity = userCtxBroker.createEntity(ownerId.toString());
		} catch (CtxException e) {
			e.printStackTrace();
		}
    	//userCtxBroker.addAttribute(ownerIdContextEntity, CtxAttributeValueType.INDIVIDUAL, "friends", IUserCtxBrokerCallback);
    	
    	try {
			if (userCtxBroker != null)
    		    userCtxBroker.createAttribute(entityId, "proximity: donald, douglas");
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	egocentricCommunityCreationManager.setUserContextBroker(userCtxBroker);
    	egocentricCommunityCreationManager.setCisManager(cisManager);
    	
		//check user joined CISs before
		egocentricCommunityCreationManager.identifyCissToCreate("not extensive", null);
		//check and compare user joined CISs after
		boolean success = false;
		//if (cisManager.getCisList().size() > 0) success = true;
		
		String[] members = new String[1];
		members[0] = "James";
		
		//Assert.assertNull(cisManager.getCisList(new CisRecord(null, null, null, null, null, members, null, null)));
		
		//Assert.assertNull(cisManager.getCisList(new ICisRecord(null, null, null, null, null, members, null, null, null)));
	}
	
	//@Test
    public void testExtensiveCreationCheck() {
    	
    	IIdentity ownerId = mock(IIdentity.class); //James Jents CSS
    	IIdentity friend1Id = mock(IIdentity.class); //Friend 1 CSS
    	IIdentity friend2Id = mock(IIdentity.class); //Friend 2 CSS
    	
    	ICssAdvertisementRecord friend1Ad = mock(ICssAdvertisementRecord.class);
    	ICssAdvertisementRecord friend2Ad = mock(ICssAdvertisementRecord.class);
    	
    	cssDirectory.addCssAdvertisementRecord(friend1Ad);
    	cssDirectory.addCssAdvertisementRecord(friend2Ad);
    
        ICssRecord thisCss = mock(ICssRecord.class);
        //thisCss.addCssDirectory(cssDirectory);
    	
    	CtxEntityIdentifier entityId = new CtxEntityIdentifier(ownerId.toString(), "James Jents", new Long(1));
    	
    	
		cisManager = mock(ICisManager.class);
		userCtxBroker = mock(ICtxBroker.class);
		
		//when(cisManager.getCiss()).thenReturn(cisRecord);
		
    	egocentricCommunityCreationManager = new EgocentricCommunityCreationManager(ownerId, "CSS");
		
    	
    	
    	//create some test context data for the CSS?
    	//ServiceResourceIdentifier userInterfacedService = new ServiceResourceIdentifier(/**new URI("Chat system")*/);
    
    	Future <CtxEntity> theEntity = null;
    	try {
			theEntity = userCtxBroker.createEntity(ownerId.toString());
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	//userCtxBroker.addAttribute(ownerIdContextEntity, CtxAttributeValueType.INDIVIDUAL, "friends", IUserCtxBrokerCallback);
    	try {
    		userCtxBroker.createAttribute(entityId, "friends");
		} catch (CtxException e) {
			e.printStackTrace();
		}
    	
    	//create some test context data for the CSS?	
    	//ServiceResourceIdentifier userInterfacedService = new ServiceResourceIdentifier(/**new URI("Chat system")*/);
    	
    	//userCtxBroker = new ICtxBroker();
    	//userCtxBroker.createEntity("CSS User", IUserCtxBrokerCallback);
    	//userCtxBroker.addAttribute(ownerIdContextEntity, CtxAttributeValueType.INDIVIDUAL, "CSS proximity", IUserCtxBrokerCallback);
    	
    	//check user joined CISs before
		egocentricCommunityCreationManager.identifyCissToCreate("extensive", null);
		//check and compare user joined CISs after
		
		//Assert.assertNotNull(/**User's joined CISs*/);
		
		
		String[] members = new String[1];
		members[0] = "James";
		//Assert.assertNull(cisManager.getCisList(new ICisRecord(null, null, null, null, null, members, null, null, null)));
	}
    
    @Test
    public void testCreateCisForPeopleTemporarilyUsingServiceTogether() {
    	
    }
    
    @Test
    public void testNotCreateDuplicateCis() {
    	cisManager = mock(ICisManager.class);
    	ICisRecord cisRecord = mock(ICisRecord.class);
    	IIdentity ownerId = mock(IIdentity.class);
    	//cisManager.addCis(ownerId, cisRecord);
    	egocentricCommunityCreationManager = new EgocentricCommunityCreationManager(ownerId, "CSS");
		
    	egocentricCommunityCreationManager.setCisManager(cisManager);
    	
    	//egocentricCommunityCreationManager.identifyCissToCreate("extensive", new HashMap<IIdentity, String>());
        //ICisManager resultCisManager = egocentricCommunityCreationManager.getCisManager();
        int cisCount = 0;
       // for (int i = 0; i < resultCisManager.getCisList().size(); i++) {
       // 	if (resultCisManager.getCisList().get(i).getMembersList() == cisRecord.getMembersList() &&
       // 			resultCisManager.getCisList().get(i).getMembershipCriteria() == cisRecord.getMembershipCriteria() ) cisCount++;
        //}
        //Assert.assertTrue(cisCount == 1);
    }
    
    @Test
    public void testNotSuggestUndesiredCis() {
    	
    }
    
    public void setCtxBroker(ICtxBroker userCtxBroker){
		this.userCtxBroker = userCtxBroker;
	}
    
    /**public void setUserCtxBrokerCallback(IUserCtxBrokerCallback userCtxBrokerCallback) {
    	this.userCtxBrokerCallback = userCtxBrokerCallback;
    }*/
    
    public void callbackForUserCtxEntity(CtxEntityIdentifier contextEntityIdentifier) {
    	entityId = contextEntityIdentifier;
    }
    
    public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}
    
    public void contextCallback(CtxEntityIdentifier context) {
    	
    }
    
}