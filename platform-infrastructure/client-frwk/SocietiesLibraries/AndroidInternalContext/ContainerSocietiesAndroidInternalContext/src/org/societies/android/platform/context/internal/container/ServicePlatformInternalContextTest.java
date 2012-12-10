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
package org.societies.android.platform.context.internal.container;


//import org.societies.android.api.context.ICtxClient;
import org.societies.android.api.internal.context.IInternalCtxClient;
import org.societies.android.platform.context.internal.impl.InternalCtxClientBase;
import org.societies.android.platform.context.internal.impl.mocks.MockClientCommunicationMgr;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;
//import org.societies.comm.xmpp.client.impl.PubsubClientAndroid;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Describe your class here...
 *
 * @author pkosmides
 *
 */

public class ServicePlatformInternalContextTest extends Service {
	
    private static final String LOG_TAG = ServicePlatformInternalContextTest.class.getName();
    private IBinder binder = null;
    
    @Override
	public void onCreate () {
		this.binder = new TestPlatformInternalContextBinder();
		Log.d(LOG_TAG, "ServicePlatformInternalContextTest service starting");
	}

	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "ServicePlatformInternalContextLocal service terminating");
	}

	/**Create Binder object for local service invocation */
	public class TestPlatformInternalContextBinder extends Binder {
		
		public IInternalCtxClient getService() {
//			PubsubClientAndroid pubsubClient = createPubSubClientAndroid();
			ClientCommunicationMgr ccm = createClientCommunicationMgr();
			
			InternalCtxClientBase serviceBase = new InternalCtxClientBase(getApplicationContext(),ccm, false);

			return serviceBase;
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return this.binder;
	}
	
	/**
	 * Factory method to get instance of {@link ClientCommunicationMgr}
	 * @return ClientCommunicationMgr
	 */
	protected ClientCommunicationMgr createClientCommunicationMgr() {
		return new MockClientCommunicationMgr(getApplicationContext(), "emma", "societies.local");
	}
}
