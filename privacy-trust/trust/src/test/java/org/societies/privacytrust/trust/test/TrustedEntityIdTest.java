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
package org.societies.privacytrust.trust.test;

import static org.junit.Assert.*;

import java.net.URISyntaxException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.societies.privacytrust.trust.api.model.TrustedEntityId;
import org.societies.privacytrust.trust.api.model.TrustedEntityType;

/**
 * Tests the {@link TrustedEntityId} class.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.3
 */
public class TrustedEntityIdTest {

	private static final TrustedEntityType ENTITY_TYPE = TrustedEntityType.CSS;
	private static final String ENTITY_ID = "foo";
	
	private TrustedEntityId teid;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		this.teid = new TrustedEntityId(ENTITY_TYPE, ENTITY_ID);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.api.model.TrustedEntityId#getEntityType()}.
	 * @throws URISyntaxException 
	 */
	@Test
	public void testGetEntityType() throws URISyntaxException {
		
		assertNotNull(this.teid.getEntityType());
		assertEquals(ENTITY_TYPE, this.teid.getEntityType());
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.api.model.TrustedEntityId#getEntityId()}.
	 * @throws URISyntaxException 
	 */
	@Test
	public void testGetEntityId() throws URISyntaxException {
		
		assertNotNull(this.teid.getEntityId());
		assertEquals(ENTITY_ID, this.teid.getEntityId());
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.api.model.TrustedEntityId#getUri()}.
	 */
	@Test
	public void testGetUri() {
		
		assertNotNull(this.teid.getUri());
		assertEquals(TrustedEntityId.URI_SCHEME, this.teid.getUri().getScheme());
		// TODO use regex
		assertTrue(this.teid.getUri().getSchemeSpecificPart().startsWith(TrustedEntityId.URN_NID));
		// assertTrue(this.teid.getUri().getSchemeSpecificPart().contains(ENTITY_TYPE));
		assertTrue(this.teid.getUri().getSchemeSpecificPart().endsWith(ENTITY_ID));
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.api.model.TrustedEntityId#toString()}.
	 */
	@Test
	public void testToString() {
		
		assertNotNull(this.teid.toString());
		assertEquals(TrustedEntityId.URI_SCHEME 
				+ TrustedEntityId.URN_DELIM 
				+ TrustedEntityId.URN_NID 
				+ TrustedEntityId.URN_DELIM
				+ ENTITY_TYPE
				+ TrustedEntityId.URN_DELIM
				+ ENTITY_ID, this.teid.toString());
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.api.model.TrustedEntityId#equals()}.
	 * @throws URISyntaxException 
	 */
	@Test
	public void testEquals() throws URISyntaxException {
		
		final TrustedEntityId sameTeid = new TrustedEntityId(ENTITY_TYPE, ENTITY_ID);
		assertTrue(this.teid.equals(sameTeid));
		
		final TrustedEntityId differentTeid1 = new TrustedEntityId(ENTITY_TYPE, "bar");
		assertFalse(this.teid.equals(differentTeid1));
		
		final TrustedEntityId differentTeid2 = new TrustedEntityId(TrustedEntityType.DVP, ENTITY_ID);
		assertFalse(this.teid.equals(differentTeid2));
	}
}