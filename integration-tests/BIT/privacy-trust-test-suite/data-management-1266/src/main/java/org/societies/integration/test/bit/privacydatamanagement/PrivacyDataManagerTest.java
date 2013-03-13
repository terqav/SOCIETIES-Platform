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
package org.societies.integration.test.bit.privacydatamanagement;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.identity.util.DataIdentifierFactory;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.DataWrapper;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.DataWrapperFactory;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.Name;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ConditionConstants;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class PrivacyDataManagerTest
{
	private static Logger LOG = LoggerFactory.getLogger(PrivacyDataManagerTest.class.getSimpleName());
	public static Integer testCaseNumber = 0;

	private DataIdentifier dataId;
	private DataIdentifier cisPublicDataId;
	private DataIdentifier cisPrivateDataId;
	private IIdentity myCssId;
	private IIdentity otherCssId;
	private IIdentity cisPublicId;
	private IIdentity cisPrivateId;
	private RequestorCis requestorCis;
	private RequestorService requestorService;
	private List<Action> actionsRead;
	private List<Condition> conditionsPublic;
	private List<Condition> conditionsMembersOnly;
	private List<Condition> conditionsPrivate;
	private RequestPolicy privacyPolicy;
	private RequestPolicy privacyPolicyMembersOnly;
	private RequestPolicy privacyPolicyPrivate;


	@Before
	public void setUp() throws Exception
	{
		LOG.info("[#"+testCaseNumber+"] "+getClass().getSimpleName()+"::setUp");
		// Dependency injection not ready
		if (!TestCase1266.isDepencyInjectionDone()) {
			throw new PrivacyException("[#"+testCaseNumber+"] [Dependency Injection] PrivacyDataManagerTest not ready");
		}
		// Data
		myCssId = TestCase1266.commManager.getIdManager().getThisNetworkNode();
		otherCssId =  TestCase1266.commManager.getIdManager().fromJid("othercss.societies.local");
		cisPublicId =  TestCase1266.commManager.getIdManager().fromJid("cis-public.societies.local");
		cisPrivateId =  TestCase1266.commManager.getIdManager().fromJid("cis-private.societies.local");
		requestorCis = getRequestorCis();
		requestorService = getRequestorService();
		// Data Id
		try {
			dataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CONTEXT+"://"+myCssId+"/ENTITY/person/1/ATTRIBUTE/name/13");
			cisPublicDataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CIS+"://"+cisPublicId+"/cis-member-list/");
			cisPrivateDataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CIS+"://"+cisPrivateId+"/cis-member-list/");
			LOG.info("Data id: "+dataId.getUri()+" (scheme: "+dataId.getScheme()+", type: "+dataId.getType()+")");
			LOG.info("Public Cis Data id: "+cisPublicDataId.getUri()+" (scheme: "+cisPublicDataId.getScheme()+", type: "+cisPublicDataId.getType()+")");
			LOG.info("Private Cis Data id: "+cisPrivateDataId.getUri()+" (scheme: "+cisPrivateDataId.getScheme()+", type: "+cisPrivateDataId.getType()+")");
		}
		catch (MalformedCtxIdentifierException e) {
			LOG.error("setUp(): DataId creation error "+e.getMessage()+"\n", e);
			fail("setUp(): DataId creation error "+e.getMessage());
		}

		// - Actions
		actionsRead = new ArrayList<Action>();
		actionsRead.add(new Action(ActionConstants.READ));

		// - Conditions
		conditionsPublic = new ArrayList<Condition>();
		conditionsPublic.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES, "1"));
		conditionsPublic.add(new Condition(ConditionConstants.STORE_IN_SECURE_STORAGE, "1"));

		conditionsMembersOnly = new ArrayList<Condition>();
		conditionsMembersOnly.add(new Condition(ConditionConstants.SHARE_WITH_CIS_MEMBERS_ONLY, "1"));
		conditionsMembersOnly.add(new Condition(ConditionConstants.STORE_IN_SECURE_STORAGE, "1"));

		conditionsPrivate = new ArrayList<Condition>();
		conditionsPrivate.add(new Condition(ConditionConstants.SHARE_WITH_CIS_OWNER_ONLY, "1"));
		conditionsPrivate.add(new Condition(ConditionConstants.STORE_IN_SECURE_STORAGE, "1"));

		// - Privacy Policy
		List<RequestItem> requestItems = new ArrayList<RequestItem>();
		requestItems.add(new RequestItem(new Resource(cisPublicDataId), actionsRead, conditionsPublic));
		List<RequestItem> requestItemsMembersOnly = new ArrayList<RequestItem>();
		requestItemsMembersOnly.add(new RequestItem(new Resource(cisPublicDataId), actionsRead, conditionsMembersOnly));
		List<RequestItem> requestItemsPrivate = new ArrayList<RequestItem>();
		requestItemsPrivate.add(new RequestItem(new Resource(cisPublicDataId), actionsRead, conditionsPrivate));

		privacyPolicy = new RequestPolicy(new RequestorCis(myCssId, cisPublicId), requestItems);
		privacyPolicyMembersOnly = new RequestPolicy(new RequestorCis(myCssId, cisPublicId), requestItemsMembersOnly);
		privacyPolicyPrivate = new RequestPolicy(new RequestorCis(myCssId, cisPublicId), requestItemsPrivate);
	}

	@After
	public void tearDown() throws Exception
	{
		LOG.info("[#"+testCaseNumber+"] "+getClass().getSimpleName()+"::tearDown");
	}

	/* --- CHECK PERMISSION CSS --- */

	@Test
	public void testCheckPermissionFirstTime()
	{
		String testTitle = new String("CheckPermission: retrieve a privacy for the first time");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		ResponseItem permission = null;
		try {
			permission = TestCase1266.privacyDataManager.checkPermission(requestorCis, dataId, actionsRead);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException] "+testTitle, e);
			fail("PrivacyException ("+e.getMessage()+") "+testTitle);
		}
		assertNotNull("No permission retrieved", permission);
		assertNotNull("No (real) permission retrieved", permission.getDecision());
		assertEquals("Bad permission retrieved", Decision.PERMIT.name(), permission.getDecision().name());
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManager#checkPermission(org.societies.api.internal.mock.DataIdentifier, IIdentity, IIdentity, org.societies.api.servicelifecycle.model.IServiceResourceIdentifier)}.
	 */
	@Test
	public void testCheckPermissionPreviouslyAdded()
	{
		String testTitle = new String("CheckPermission: retrieve a privacy two times");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		ResponseItem permission1 = null;
		ResponseItem permission2 = null;
		try {
			permission1 = TestCase1266.privacyDataManager.checkPermission(requestorCis, dataId, actionsRead);
			permission2 = TestCase1266.privacyDataManager.checkPermission(requestorCis, dataId, actionsRead);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException] "+testTitle, e);
			fail("PrivacyException ("+e.getMessage()+") "+testTitle);
		}
		assertNotNull("No permission retrieved", permission1);
		assertNotNull("No (real) permission retrieved", permission1.getDecision());
		assertEquals("Bad permission retrieved",  Decision.PERMIT.name(), permission1.getDecision().name());
		assertNotNull("No permission retrieved", permission2);
		assertNotNull("No (real) permission retrieved", permission2.getDecision());
		assertEquals("Bad permission retrieved", Decision.PERMIT.name(), permission2.getDecision().name());
		assertEquals("Two requests, not the same answer", permission1.toXMLString(), permission2.toXMLString());
	}

	/* --- CHECK PERMISSION CIS --- */

	@Test
	public void testCheckPermissionPublicCis()
	{
		String testTitle = new String("CheckPermission public Cis: retrieve a privacy for the first time");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		RequestPolicy privacyPolicyAdded = null;
		boolean privacyPolicyDeleted = false;
		ResponseItem permission = null;
		try {
			privacyPolicyAdded = TestCase1266.privacyPolicyManager.updatePrivacyPolicy(privacyPolicy);
			permission = TestCase1266.privacyDataManager.checkPermission(requestorService, cisPublicDataId, actionsRead);
			privacyPolicyDeleted = TestCase1266.privacyPolicyManager.deletePrivacyPolicy(privacyPolicy.getRequestor());
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException] "+testTitle, e);
			fail("PrivacyException ("+e.getMessage()+") "+testTitle);
		}
		assertNotNull("No privacy policy added", privacyPolicyAdded);
		assertEquals("Privacy policy added: not the good one", privacyPolicy.toXMLString(), privacyPolicyAdded.toXMLString());

		assertNotNull("No permission retrieved", permission);
		assertNotNull("No (real) permission retrieved", permission.getDecision());
		assertEquals("Bad permission retrieved", Decision.PERMIT.name(), permission.getDecision().name());

		assertTrue("Privacy policy not deleted", privacyPolicyDeleted);
	}

	@Test
	public void testCheckPermissionPublicCisPreviouslyAdded()
	{
		String testTitle = new String("CheckPermission public Cis: retrieve a privacy two times");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		RequestPolicy privacyPolicyAdded = null;
		boolean privacyPolicyDeleted = false;
		ResponseItem permission1 = null;
		ResponseItem permission2 = null;
		try {
			privacyPolicyAdded = TestCase1266.privacyPolicyManager.updatePrivacyPolicy(privacyPolicy);
			permission1 = TestCase1266.privacyDataManager.checkPermission(requestorService, cisPublicDataId, actionsRead);
			permission2 = TestCase1266.privacyDataManager.checkPermission(requestorService, cisPublicDataId, actionsRead);
			privacyPolicyDeleted = TestCase1266.privacyPolicyManager.deletePrivacyPolicy(privacyPolicy.getRequestor());
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException] "+testTitle, e);
			fail("PrivacyException ("+e.getMessage()+") "+testTitle);
		}
		assertNotNull("No privacy policy added", privacyPolicyAdded);
		assertEquals("Privacy policy added: not the good one", privacyPolicy.toXMLString(), privacyPolicyAdded.toXMLString());

		assertNotNull("No permission retrieved", permission1);
		assertNotNull("No (real) permission retrieved", permission1.getDecision());
		assertEquals("Bad permission retrieved",  Decision.PERMIT.name(), permission1.getDecision().name());
		assertNotNull("No permission retrieved", permission2);
		assertNotNull("No (real) permission retrieved", permission2.getDecision());
		assertEquals("Bad permission retrieved", Decision.PERMIT.name(), permission2.getDecision().name());
		assertEquals("Two requests, not the same answer", permission1.toXMLString(), permission2.toXMLString());

		assertTrue("Privacy policy not deleted", privacyPolicyDeleted);
	}

	@Test
	public void testCheckPermissionPrivateCis()
	{
		String testTitle = new String("CheckPermission Private Cis: retrieve a privacy for the first time");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		RequestPolicy privacyPolicyAdded = null;
		boolean privacyPolicyDeleted = false;
		ResponseItem permissionOther = null;
		ResponseItem permissionMe = null;
		try {
			privacyPolicyAdded = TestCase1266.privacyPolicyManager.updatePrivacyPolicy(privacyPolicyPrivate);
			LOG.info("[#"+testCaseNumber+"] Requested by: "+requestorService);
			permissionOther = TestCase1266.privacyDataManager.checkPermission(requestorService, cisPrivateDataId, actionsRead);
			LOG.info("[#"+testCaseNumber+"] Requested by me: "+myCssId.getJid());
			permissionMe = TestCase1266.privacyDataManager.checkPermission(new Requestor(myCssId), cisPrivateDataId, actionsRead);
			privacyPolicyDeleted = TestCase1266.privacyPolicyManager.deletePrivacyPolicy(privacyPolicyPrivate.getRequestor());
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException] "+testTitle, e);
			fail("PrivacyException ("+e.getMessage()+") "+testTitle);
		}
		assertNotNull("No privacy policy added", privacyPolicyAdded);
		assertEquals("Privacy policy added: not the good one", privacyPolicyPrivate.toXMLString(), privacyPolicyAdded.toXMLString());

		assertNotNull("No permission retrieved", permissionOther);
		assertNotNull("No (real) permission retrieved", permissionOther.getDecision());
		assertEquals("Bad permission retrieved", Decision.DENY.name(), permissionOther.getDecision().name());
		
		assertNotNull("No permission retrieved", permissionMe);
		assertNotNull("No (real) permission retrieved", permissionMe.getDecision());
		assertEquals("Bad permission retrieved", Decision.PERMIT.name(), permissionMe.getDecision().name());

		assertTrue("Privacy policy not deleted", privacyPolicyDeleted);
	}

	@Test
	public void testCheckPermissionPrivateCisPreviouslyAdded()
	{
		String testTitle = new String("CheckPermission Private Cis: retrieve a privacy two times");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		RequestPolicy privacyPolicyAdded = null;
		boolean privacyPolicyDeleted = false;
		ResponseItem permissionOther1 = null;
		ResponseItem permissionMe1 = null;
		ResponseItem permissionOther2 = null;
		ResponseItem permissionMe2 = null;
		try {
			privacyPolicyAdded = TestCase1266.privacyPolicyManager.updatePrivacyPolicy(privacyPolicyPrivate);
			permissionOther1 = TestCase1266.privacyDataManager.checkPermission(requestorService, cisPrivateDataId, actionsRead);
			permissionMe1 = TestCase1266.privacyDataManager.checkPermission(new Requestor(myCssId), cisPrivateDataId, actionsRead);
			permissionOther2 = TestCase1266.privacyDataManager.checkPermission(requestorService, cisPrivateDataId, actionsRead);
			permissionMe2 = TestCase1266.privacyDataManager.checkPermission(new Requestor(myCssId), cisPrivateDataId, actionsRead);
			privacyPolicyDeleted = TestCase1266.privacyPolicyManager.deletePrivacyPolicy(privacyPolicyPrivate.getRequestor());
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException] "+testTitle, e);
			fail("PrivacyException ("+e.getMessage()+") "+testTitle);
		}
		assertNotNull("No privacy policy added", privacyPolicyAdded);
		assertEquals("Privacy policy added: not the good one", privacyPolicyPrivate.toXMLString(), privacyPolicyAdded.toXMLString());

		assertNotNull("Other: No permission retrieved", permissionOther1);
		assertNotNull("Other: No (real) permission retrieved", permissionOther1.getDecision());
		assertEquals("Other: Bad permission retrieved",  Decision.DENY.name(), permissionOther1.getDecision().name());
		assertNotNull("Other: No permission retrieved", permissionOther2);
		assertNotNull("Other: No (real) permission retrieved", permissionOther2.getDecision());
		assertEquals("Other: Bad permission retrieved", Decision.DENY.name(), permissionOther2.getDecision().name());
		assertEquals("Other: Two requests, not the same answer", permissionOther1.toXMLString(), permissionOther2.toXMLString());
		
		assertNotNull("Me: No permission retrieved", permissionMe1);
		assertNotNull("Me: No (real) permission retrieved", permissionMe1.getDecision());
		assertEquals("Me: Bad permission retrieved",  Decision.PERMIT.name(), permissionMe1.getDecision().name());
		assertNotNull("Me: No permission retrieved", permissionMe2);
		assertNotNull("Me: No (real) permission retrieved", permissionMe2.getDecision());
		assertEquals("Me: Bad permission retrieved", Decision.PERMIT.name(), permissionMe2.getDecision().name());
		assertEquals("Me: Two requests, not the same answer", permissionMe1.toXMLString(), permissionMe2.toXMLString());

		assertTrue("Privacy policy not deleted", privacyPolicyDeleted);
	}


	/* --- OBFUSCATION --- */

	@Test
	public void testObfuscateData()
	{
		String testTitle = new String("ObfuscateData");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		IDataWrapper<Name> wrapper = DataWrapperFactory.getNameWrapper("Olivier", "Maridat");
		Future<IDataWrapper> obfuscatedDataWrapperAsync = null;
		IDataWrapper<Name> obfuscatedDataWrapper = null;
		try {
			obfuscatedDataWrapperAsync = TestCase1266.privacyDataManager.obfuscateData(requestorCis, wrapper);
			obfuscatedDataWrapper = obfuscatedDataWrapperAsync.get();
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException obfuscator error] "+testTitle, e);
			fail("PrivacyException obfuscator error ("+e.getMessage()+") "+testTitle);
		} catch (InterruptedException e) {
			LOG.error("[#"+testCaseNumber+"] [InterruptedException async error] "+testTitle, e);
			fail("InterruptedException async error ("+e.getMessage()+") "+testTitle);
		} catch (ExecutionException e) {
			LOG.error("[#"+testCaseNumber+"] [InterruptedException async exec error] "+testTitle, e);
			fail("InterruptedException async exec error ("+e.getMessage()+") "+testTitle);
		}

		// Verify
		LOG.debug("### Orginal name:\n"+wrapper.getData().toString());
		LOG.debug("### Obfuscated name:\n"+obfuscatedDataWrapper.getData().toString());
		assertNotNull("Obfuscated data null", obfuscatedDataWrapper);
	}

	@Test
	public void testHasObfuscatedVersion()
	{
		String testTitle = new String("HasObfuscatedVersion");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		IDataWrapper<Name> wrapper = new DataWrapper<Name>(dataId, null);
		LOG.info("[#"+testCaseNumber+"] "+wrapper);
		IDataWrapper actual = null;
		try {
			actual = TestCase1266.privacyDataManager.hasObfuscatedVersion(requestorCis, wrapper);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException obfuscator error] "+testTitle, e);
			fail("PrivacyException obfuscator error ("+e.getMessage()+") "+testTitle);
		}
		assertNotNull("Expected data id is not null", actual);
		assertEquals("Retrieved data id is not same as the first", dataId.getUri(), actual.getDataId().getUri());
	}



	/* ****************************
	 *            Tools           *
	 ******************************/

	private RequestorService getRequestorService() throws InvalidFormatException, URISyntaxException{
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		serviceId.setServiceInstanceIdentifier("css://"+otherCssId+"/HelloEarth");
		serviceId.setIdentifier(new URI("css://"+otherCssId+"/HelloEarth"));
		return new RequestorService(otherCssId, serviceId);
	}

	private RequestorCis getRequestorCis() throws InvalidFormatException{
		return new RequestorCis(otherCssId, cisPublicId);
	}
}