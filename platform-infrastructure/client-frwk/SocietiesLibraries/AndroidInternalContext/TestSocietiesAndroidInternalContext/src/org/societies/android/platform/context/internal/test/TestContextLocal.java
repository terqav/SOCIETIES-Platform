/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druûbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVA«√O, SA (PTIN), IBM Corp., 
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

package org.societies.android.platform.context.internal.test;

import java.net.URI;
import java.net.URISyntaxException;

//import org.societies.android.api.context.ICtxClient;
import org.societies.android.api.internal.context.IInternalCtxClient;
import org.societies.android.api.context.model.ACtxEntity;
import org.societies.android.api.identity.ARequestor;
import org.societies.android.api.identity.ARequestorService;
import org.societies.android.api.servicelifecycle.AServiceResourceIdentifier;
import org.societies.android.platform.context.internal.container.ServicePlatformInternalContextTest;
import org.societies.android.platform.context.internal.container.ServicePlatformInternalContextTest.TestPlatformInternalContextBinder;
//import org.societies.android.platform.ctxclientcontainer.ServicePlatformContextTest;
//import org.societies.android.platform.ctxclientcontainer.ServicePlatformContextTest.TestPlatformContextBinder;
import org.societies.api.identity.IIdentity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;


public class TestContextLocal extends ServiceTestCase <ServicePlatformInternalContextTest> {
	//Logging tag
	private static final String LOG_TAG = TestContextLocal.class.getName();
//	private static final String CLIENT_PARAM_1 = "org.societies.android.platform.events.test";
//	private static final String CLIENT_PARAM_2 = "org.societies.android.platform.events.test.alternate";
//	private static final String CLIENT_PARAM_3 = "org.societies.android.platform.events.test.other";
//	private static final String CLIENT_PARAM_1 = "org.societies.android.platform.ctxclient.test";
	private static final String CLIENT_ID = "org.societies.android.platform.context.internal.test";
//	private static final String INTENT_FILTER = "org.societies.android.css.manager";
	private static final int SLEEP_DELAY = 5000;

//	private ARequestor requestor;
	private Boolean receivedResult = false;
	
	public TestContextLocal() {
		super(ServicePlatformInternalContextTest.class);
	}
	
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }


    /**
     * The name 'test preconditions' is a convention to signal that if this
     * test doesn't pass, the test case was not set up properly and it might
     * explain any and all failures in other tests.  This is not guaranteed
     * to run before other tests, as JUnit uses reflection to find the tests.
     */
    @MediumTest
    public void testPreconditions() {
    }
    
    @SuppressWarnings("null")
	@MediumTest
	public void testCreateEntity() throws Exception {
        
		BroadcastReceiver receiver = setupBroadcastReceiver();

		Intent eventsIntent = new Intent(getContext(), ServicePlatformInternalContextTest.class);
		TestPlatformInternalContextBinder binder = (TestPlatformInternalContextBinder) bindService(eventsIntent);
    	assertNotNull(binder);
    	
    	IInternalCtxClient eventService = (IInternalCtxClient) binder.getService();
    	
//		ARequestor requestor = new Requestor(mockIdentityLocal);
//    	ARequestor requestor = null;
//    	requestor.setRequestorId("myFooIIdentity@societies.local");
		AServiceResourceIdentifier serviceID = new AServiceResourceIdentifier();
		serviceID.setIdentifier(new URI("http://service.societies.local"));
		serviceID.setServiceInstanceIdentifier("service_1232");
		ARequestorService requestorService = new ARequestorService("service@societies.local", serviceID);

    	
//		final ACtxEntity ctxEntity = eventService.createEntity(CLIENT_ID, requestor, "myFooIIdentity@societies.local", "DEVICE");
		eventService.createEntity(CLIENT_ID, requestorService, "myFooIIdentity@societies.local", "DEVICE");

    	
				//ctxBroker.createEntity(
				//requestor, mockIdentityLocal, CtxEntityTypes.DEVICE).get();
//		assertNotNull(ctxEntity);
//		assertNotNull(ctxEntity.getId());
//		assertEquals(mockIdentityLocal.toString(), ctxEntity.getOwnerId());
//		assertEquals("DEVICE", ctxEntity.getType());
    	
//    	eventService.subscribeToAllEvents(CLIENT_PARAM_1);
    	Thread.sleep(SLEEP_DELAY);

//    	eventService.unSubscribeFromAllEvents(CLIENT_PARAM_1);
//    	Thread.sleep(SLEEP_DELAY);

    	getContext().unregisterReceiver(receiver);
    	
	}

/*
    @MediumTest
	public void testSubscribeToAllEvents() throws Exception {
        
		BroadcastReceiver receiver = setupBroadcastReceiver();

		Intent eventsIntent = new Intent(getContext(), ServicePlatformEventsTest.class);
		TestPlatformEventsBinder binder = (TestPlatformEventsBinder) bindService(eventsIntent);
    	assertNotNull(binder);
    	
    	IAndroidSocietiesEvents eventService = (IAndroidSocietiesEvents) binder.getService();
    	eventService.subscribeToAllEvents(CLIENT_PARAM_1);
    	Thread.sleep(SLEEP_DELAY);

    	eventService.unSubscribeFromAllEvents(CLIENT_PARAM_1);
    	Thread.sleep(SLEEP_DELAY);

    	getContext().unregisterReceiver(receiver);
    	
	}
	
    @MediumTest
	public void testSubscribeToOneEvent() throws Exception {
        
		BroadcastReceiver receiver = setupBroadcastReceiver();

		Intent eventsIntent = new Intent(getContext(), ServicePlatformEventsTest.class);
		TestPlatformEventsBinder binder = (TestPlatformEventsBinder) bindService(eventsIntent);
    	assertNotNull(binder);
    	
    	IAndroidSocietiesEvents eventService = (IAndroidSocietiesEvents) binder.getService();
    	eventService.subscribeToEvent(CLIENT_PARAM_1, IAndroidSocietiesEvents.societiesEvents[0]);
    	Thread.sleep(SLEEP_DELAY);
    	
    	eventService.unSubscribeFromEvent(CLIENT_PARAM_1, IAndroidSocietiesEvents.societiesEvents[0]);
    	Thread.sleep(SLEEP_DELAY);
    	
       	getContext().unregisterReceiver(receiver);

	}
	@MediumTest
	public void testSubscribeToEvents() throws Exception {
		
		BroadcastReceiver receiver = setupBroadcastReceiver();

		Intent eventsIntent = new Intent(getContext(), ServicePlatformEventsTest.class);
		TestPlatformEventsBinder binder = (TestPlatformEventsBinder) bindService(eventsIntent);
    	assertNotNull(binder);
    	
    	IAndroidSocietiesEvents eventService = (IAndroidSocietiesEvents) binder.getService();
    	eventService.subscribeToEvents(CLIENT_PARAM_1, INTENT_FILTER);
    	Thread.sleep(SLEEP_DELAY);
    	
    	eventService.unSubscribeFromEvents(CLIENT_PARAM_1, INTENT_FILTER);
    	Thread.sleep(SLEEP_DELAY);
    	
       	getContext().unregisterReceiver(receiver);
	}
    @MediumTest
	public void testTwoClients() throws Exception {
        
		BroadcastReceiver receiver = setupBroadcastReceiver();

		Intent eventsIntent = new Intent(getContext(), ServicePlatformEventsTest.class);
		TestPlatformEventsBinder binder = (TestPlatformEventsBinder) bindService(eventsIntent);
    	assertNotNull(binder);
    	
    	IAndroidSocietiesEvents eventService = (IAndroidSocietiesEvents) binder.getService();
    	eventService.subscribeToAllEvents(CLIENT_PARAM_1);
    	Thread.sleep(SLEEP_DELAY);

    	eventService.subscribeToEvents(CLIENT_PARAM_2, INTENT_FILTER);
    	Thread.sleep(SLEEP_DELAY);

    	eventService.subscribeToEvent(CLIENT_PARAM_3, IAndroidSocietiesEvents.societiesEvents[0]);
    	Thread.sleep(SLEEP_DELAY);

    	eventService.unSubscribeFromAllEvents(CLIENT_PARAM_1);
    	Thread.sleep(SLEEP_DELAY);

    	eventService.unSubscribeFromEvents(CLIENT_PARAM_2, INTENT_FILTER);
    	Thread.sleep(SLEEP_DELAY);
    	
    	eventService.unSubscribeFromEvent(CLIENT_PARAM_3, IAndroidSocietiesEvents.societiesEvents[0]);
    	Thread.sleep(SLEEP_DELAY);

    	getContext().unregisterReceiver(receiver);
    	
	}*/

    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class MainReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			TestContextLocal.this.receivedResult = true;
			assertNotNull(intent.getParcelableExtra(IInternalCtxClient.INTENT_RETURN_VALUE_KEY));
			if (intent.getAction().equals(IInternalCtxClient.CREATE_ENTITY)) {
				assertEquals(0, intent.getIntExtra(IInternalCtxClient.INTENT_RETURN_VALUE_KEY, 0));
			}
/*			if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_ALL_EVENTS)) {
				assertEquals(6, intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, 0));
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENT)) {
				assertEquals(1, intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, 0));
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENTS)) {
				assertEquals(2, intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, 0));
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_ALL_EVENTS)) {
				assertEquals(0, intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, 0));
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENT)) {
				assertEquals(0, intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, 0));
			} else if (intent.getAction().equals(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENTS)) {
				assertEquals(0, intent.getIntExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, 0));
			}*/
			
			Log.d(LOG_TAG, "OnReceive finished");
		}
    }
    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupBroadcastReceiver() {
    	BroadcastReceiver receiver = null;
    	
        Log.d(LOG_TAG, "Set up broadcast receiver");
        
        receiver = new MainReceiver();
        getContext().registerReceiver(receiver, createTestIntentFilter());    	
        Log.d(LOG_TAG, "Register broadcast receiver");

        return receiver;
    }
    
    /**
     * Create an alternate broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupAlternateBroadcastReceiver() {
    	BroadcastReceiver receiver = null;
    	
        Log.d(LOG_TAG, "Set up broadcast receiver");
        
        receiver = new MainReceiver();
        getContext().registerReceiver(receiver, createTestIntentFilter());    	
        Log.d(LOG_TAG, "Register broadcast receiver");

        return receiver;
    }
    
    /**
     * Create a suitable intent filter
     * @return IntentFilter
     */
    private IntentFilter createTestIntentFilter() {
    	//register broadcast receiver to receive SocietiesEvents return values 
        IntentFilter intentFilter = new IntentFilter();
        
        intentFilter.addAction(IInternalCtxClient.CREATE_ENTITY);
/*        intentFilter.addAction(IAndroidSocietiesEvents.SUBSCRIBE_TO_ALL_EVENTS);
        intentFilter.addAction(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENT);
        intentFilter.addAction(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENTS);
        intentFilter.addAction(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_ALL_EVENTS);
        intentFilter.addAction(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENT);
        intentFilter.addAction(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENTS);
        intentFilter.addAction(IAndroidSocietiesEvents.PUBLISH_EVENT);
        intentFilter.addAction(IAndroidSocietiesEvents.NUM_EVENT_LISTENERS);*/
        
        return intentFilter;

    }
}
