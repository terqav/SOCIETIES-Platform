/**
 * Copyright (c) 2011, SOCIETIES Consortium
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
package org.societies.privacytrust.privacyprotection.test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxIdentifierFactory;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.DataWrapperFactory;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.Name;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.model.PrivacyPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class PrivacyPermissionTest {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyPermissionTest.class.getSimpleName());
	
	public PrivacyPermission privacyPermission;
	public static List<Action> actions0;
	public static List<Action> actions1;
	public static List<Action> actions2;
	
	@BeforeClass
	public static void setUpClass() {
		actions0 = new ArrayList<Action>();
		
		actions1 = new ArrayList<Action>();
		actions1.add(new Action(ActionConstants.READ));
		
		actions2 = new ArrayList<Action>();
		actions2.add(new Action(ActionConstants.READ, true));
		actions2.add(new Action(ActionConstants.WRITE, false));
	}
	
	@Before
	public void setUp() {
		privacyPermission = new PrivacyPermission();
	}
	
	@After
	public void tearDown() {
		privacyPermission = null;
	}
	
	
	@Test
	public void testSet1Action() {
		String testTitle = new String("testSet1Action");
		LOG.info("[Test] "+testTitle);
		
		// -- 1 action in the list
		LOG.info("Expected actions:");
		for(Action action : actions1) {
			LOG.info(action.getActionType().name()+":"+action.isOptional());
		}
		privacyPermission.setActions(actions1);
		LOG.info("Setted actions: "+privacyPermission.getActions());
		List<Action> retrievedActions1 = privacyPermission.getActionsFromString();
		LOG.info("Retrieved actions: ");
		for(Action action : retrievedActions1) {
			LOG.info(action.getActionType().name()+":"+action.isOptional());
		}
		assertEquals("Expected same but are not", actions1, retrievedActions1);
	}
	
	@Test
	public void testSet2Actions() {
		String testTitle = new String("testSet2Actions");
		LOG.info("[Test] "+testTitle);
		
		// -- 1 action in the list
		LOG.info("Expected actions:");
		for(Action action : actions2) {
			LOG.info(action.getActionType().name()+":"+action.isOptional());
		}
		privacyPermission.setActions(actions2);
		LOG.info("Setted actions: "+privacyPermission.getActions());
		List<Action> retrievedActions2 = privacyPermission.getActionsFromString();
		LOG.info("Retrieved actions: ");
		for(Action action : retrievedActions2) {
			LOG.info(action.getActionType().name()+":"+action.isOptional());
		}
		assertEquals("Expected same but are not", actions2, retrievedActions2);
	}
	
	@Test
	public void testSet0Action() {
		String testTitle = new String("testSet0Action");
		LOG.info("[Test] "+testTitle);
		
		// -- 1 action in the list
		LOG.info("Expected actions:");
		for(Action action : actions0) {
			LOG.info(action.getActionType().name()+":"+action.isOptional());
		}
		privacyPermission.setActions(actions0);
		LOG.info("Setted actions: "+privacyPermission.getActions());
		List<Action> retrievedActions0 = privacyPermission.getActionsFromString();
		LOG.info("Retrieved actions: ");
		for(Action action : retrievedActions0) {
			LOG.info(action.getActionType().name()+":"+action.isOptional());
		}
		assertEquals("Expected same but are not", actions0, retrievedActions0);
	}
}
