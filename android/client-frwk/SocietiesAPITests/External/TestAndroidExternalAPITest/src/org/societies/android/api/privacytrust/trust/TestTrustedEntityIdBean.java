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
package org.societies.android.api.privacytrust.trust;

import android.os.Parcel;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityTypeBean;

public class TestTrustedEntityIdBean extends AndroidTestCase {
	
	private static final String TRUSTED_ENTITY_ID = "foo.societies.local";
	
	protected void setUp() throws Exception {
		
		super.setUp();
	}

	protected void tearDown() throws Exception {

		super.tearDown();
	}

	@MediumTest
	public void testParcelable() {

		final TrustedEntityIdBean teidBean = new TrustedEntityIdBean();
		assertNotNull(teidBean);
		teidBean.setEntityId(TRUSTED_ENTITY_ID);
		assertEquals(TRUSTED_ENTITY_ID, teidBean.getEntityId());
		teidBean.setEntityType(TrustedEntityTypeBean.CSS);
		assertEquals(TrustedEntityTypeBean.CSS, teidBean.getEntityType());
		
		assertEquals(0, teidBean.describeContents());

		Parcel teidBeanToParcel = Parcel.obtain();
		teidBean.writeToParcel(teidBeanToParcel, 0);
		//done writing, now reset parcel for reading
		teidBeanToParcel.setDataPosition(0);
		//finish round trip

		final TrustedEntityIdBean teidBeanFromParcel = 
				TrustedEntityIdBean.CREATOR.createFromParcel(teidBeanToParcel);

		assertNotNull(teidBeanFromParcel);
		assertEquals(teidBean.getEntityId(), teidBeanFromParcel.getEntityId());
		assertEquals(teidBean.getEntityType(), teidBeanFromParcel.getEntityType());
	}
}