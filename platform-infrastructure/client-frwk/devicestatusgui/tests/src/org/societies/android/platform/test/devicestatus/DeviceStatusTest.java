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
package org.societies.android.platform.test.devicestatus;

import java.util.List;

import org.societies.android.platform.devicestatus.DeviceStatus;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * @author olivierm
 *
 */
public class DeviceStatusTest extends AndroidTestCase {
	DeviceStatus deviceStatus;
	
	public DeviceStatusTest() {
		super();
	}
	
	@Override
    protected void setUp() throws Exception {
		// Must be first statement in method
        super.setUp();
        deviceStatus = new DeviceStatus(getContext());
    }

	@Override
	protected void tearDown() throws Exception {
		// Must be last statement in method
		super.tearDown();
		deviceStatus = null;
	}

	/**
	 * Test method for {@link org.societies.android.platform.devicestatus.DeviceStatus#isInternetConnectivityOn(java.lang.String)}.
	 */
	@SmallTest
	public void testIsInternetConnectivityOn() {
		boolean expected = true;
		boolean actual = deviceStatus.isInternetConnectivityOn("don't care");
		assertEquals(expected, actual);
	}
	/**
	 * Test method for {@link org.societies.android.platform.devicestatus.DeviceStatus#getConnectivityProvidersStatus(java.lang.String)}.
	 */
	@SmallTest
	public void testGetConnectivityProvidersStatus() {
		List<?> actual = deviceStatus.getConnectivityProvidersStatus("don't care");
		assertNotNull(actual);
	}
	/**
	 * Test method for {@link org.societies.android.platform.devicestatus.DeviceStatus#getLocationProvidersStatus(java.lang.String)}.
	 */
	@SmallTest
	public void testGetLocationProvidersStatus() {
		List<?> actual = deviceStatus.getLocationProvidersStatus("don't care");
		assertNotNull(actual);
	}

}
