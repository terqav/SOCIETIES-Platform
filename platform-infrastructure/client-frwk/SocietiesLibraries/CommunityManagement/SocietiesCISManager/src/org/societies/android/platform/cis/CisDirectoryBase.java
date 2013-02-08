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
package org.societies.android.platform.cis;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.cis.directory.ICisDirectory;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.xmpp.CommunicationException;
import org.societies.android.api.comms.xmpp.ICommCallback;
import org.societies.android.api.comms.xmpp.Stanza;
import org.societies.android.api.comms.xmpp.XMPPError;
import org.societies.android.api.comms.xmpp.XMPPInfo;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.api.identity.IIdentity;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.schema.cis.directory.CisDirectoryBean;
import org.societies.api.schema.cis.directory.CisDirectoryBeanResult;
import org.societies.api.schema.cis.directory.MethodType;
import org.societies.utilities.DBC.Dbc;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class CisDirectoryBase implements ICisDirectory {
	//LOGGING TAG
	private static final String LOG_TAG = CisDirectoryBase.class.getName();
	
	//COMMS REQUIRED VARIABLES
	private static final List<String> ELEMENT_NAMES = Arrays.asList("cisDirectoryBean", "cisDirectoryBeanResult");
	private static final List<String> NAME_SPACES = Collections.unmodifiableList(Arrays.asList("http://societies.org/api/schema/cis/directory"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(Arrays.asList("org.societies.api.schema.cis.directory"));
    
	private ClientCommunicationMgr commMgr;
    private Context androidContext;
    private boolean connectedToComms = false;
    private boolean registeredNamespaces = false;
    
    /**
     * Default constructor
     */
    public CisDirectoryBase(Context androidContext) {
    	Log.d(LOG_TAG, "Object created");
    	
    	this.androidContext = androidContext;    	
		try {
			//INSTANTIATE COMMS MANAGER
			this.commMgr = new ClientCommunicationMgr(androidContext, true);
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
        }
    }
    
    /**
	 * @param client
	 */
	private void broadcastNotLoggedIn(final String client) {
		if (client != null) {
			Intent intent = new Intent(IMethodCallback.INTENT_NOTLOGGEDIN_EXCEPTION);
			intent.setPackage(client);
			CisDirectoryBase.this.androidContext.sendBroadcast(intent);
		}
	}
	
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ICisDirectory METHODS >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	/* @see org.societies.android.api.cis.directory.ICisDirectory#findAllCisAdvertisementRecords(java.lang.String) */
	public CisAdvertisementRecord[] findAllCisAdvertisementRecords(final String client) {
        Log.d(LOG_TAG, "findAllCisAdvertisementRecords called by client: " + client);
		
        if (!connectedToComms) {
        	//NOT CONNECTED TO COMMS SERVICE YET
        	Log.d(LOG_TAG, "findAllCisAdvertisementRecords connecting to comms");
	        this.commMgr.bindCommsService(new IMethodCallback() {
	
				@Override
				public void returnAction(boolean resultFlag) {
					Log.d(LOG_TAG, "Connected to comms: " + resultFlag);
					if (resultFlag) { 
						AsyncDirFunctions methodAsync = new AsyncDirFunctions();
						String params[] = {client, ICisDirectory.FIND_ALL_CIS, ""};
						methodAsync.execute(params);		
						connectedToComms = true;
					}
					else // NOT LOGGED IN
						broadcastNotLoggedIn(client);
				}
	
				@Override
				public void returnAction(String result) {
				}
			});
        } else {
        	//ALREADY CONNECTED TO COMMS SERVICE
        	AsyncDirFunctions methodAsync = new AsyncDirFunctions();
			String params[] = {client, ICisDirectory.FIND_ALL_CIS, ""};
			methodAsync.execute(params);
        }
		
		return null;
	}

	/* @see org.societies.android.api.cis.directory.ICisDirectory#findForAllCis(java.lang.String, java.lang.String) */
	public CisAdvertisementRecord[] findForAllCis(final String client, final String filter) {       
        Log.d(LOG_TAG, "findForAllCis called by client: " + client);
		
        if (!connectedToComms) {
        	//NOT CONNECTED TO COMMS SERVICE YET
        	Log.d(LOG_TAG, "findForAllCis connecting to comms");
	        this.commMgr.bindCommsService(new IMethodCallback() {
				@Override
				public void returnAction(boolean resultFlag) {
					Log.d(LOG_TAG, "Connected to comms: " + resultFlag);
					if (resultFlag) { 
						AsyncDirFunctions methodAsync = new AsyncDirFunctions();
						String params[] = {client, ICisDirectory.FILTER_CIS, filter};
						methodAsync.execute(params);
						connectedToComms = true;
					}
					else // NOT LOGGED IN
						broadcastNotLoggedIn(client);
				}
				
				@Override
				public void returnAction(String result) {
				}
			});
        } else {
        	//ALREADY CONNECTED TO COMMS SERVICE
        	AsyncDirFunctions methodAsync = new AsyncDirFunctions();
			String params[] = {client, ICisDirectory.FILTER_CIS, filter};
			methodAsync.execute(params);
		}
		
		return null;
	}

	/* @see org.societies.android.api.cis.directory.ICisDirectory#searchByID(java.lang.String, java.lang.String) */
	public CisAdvertisementRecord searchByID(final String client, final String cis_id) {
		Log.d(LOG_TAG, "searchByID called by client: " + client);
		
		if (!connectedToComms) {
        	//NOT CONNECTED TO COMMS SERVICE YET
        	Log.d(LOG_TAG, "findForAllCis connecting to comms");
	        this.commMgr.bindCommsService(new IMethodCallback() {
				@Override
				public void returnAction(boolean resultFlag) {
					Log.d(LOG_TAG, "Connected to comms: " + resultFlag);
					if (resultFlag) { 
						AsyncDirFunctions methodAsync = new AsyncDirFunctions();
						String params[] = {client, ICisDirectory.FIND_CIS_ID, cis_id};
						methodAsync.execute(params);
						connectedToComms = true;
					}
					else // NOT LOGGED IN
						broadcastNotLoggedIn(client);
				}
				
				@Override
				public void returnAction(String result) {
				}
			});
        } else {
        	//ALREADY CONNECTED TO COMMS SERVICE
        	AsyncDirFunctions methodAsync = new AsyncDirFunctions();
    		String params[] = {client, ICisDirectory.FIND_CIS_ID, cis_id};
    		methodAsync.execute(params);
		}
		
		return null;
	}

	/**
	 * AsyncTask classes required to carry out threaded tasks. These classes should be used where it is estimated that 
	 * the task length is unknown or potentially long. While direct usage of the Communications components for remote 
	 * method invocation is an explicitly asynchronous operation, other usage is not and the use of these types of classes
	 * is encouraged. Remember, Android Not Responding (ANR) exceptions will be invoked if the main app thread is abused
	 * and the app will be closed down by Android very soon after.
	 * 
	 * Although the result of an AsyncTask can be obtained by using <AsyncTask Object>.get() it's not a good idea as 
	 * it will effectively block the parent method until the result is delivered back and so render the use if the AsyncTask
	 * class ineffective. Use Intents as an asynchronous callback mechanism.
	 */

	/**
	 * This class carries out the GetFriendRequests method call asynchronously
	 */
	private class AsyncDirFunctions extends AsyncTask<String, Void, String[]> {
		
		@Override
		protected String[] doInBackground(String... params) {
			Dbc.require("At least one parameter must be supplied", params.length >= 1);
			Log.d(LOG_TAG, "AsyncFriendRequests - doInBackground");
			
			//PARAMETERS
			String client = params[0];
			String method = params[1];
			String filterCis = params[2];
			//RETURN OBJECT
			String results[] = new String[1];
			results[0] = client;
			//MESSAGE BEAN
			final CisDirectoryBean messageBean = new CisDirectoryBean();
			if (method.equals(ICisDirectory.FIND_CIS_ID)) {
				messageBean.setMethod(MethodType.SEARCH_BY_ID);
				messageBean.setFilter(filterCis);
			} 
			else if (method.equals(ICisDirectory.FILTER_CIS)) {
				messageBean.setMethod(MethodType.FIND_FOR_ALL_CIS);
				CisAdvertisementRecord advert = new CisAdvertisementRecord();
				advert.setName(filterCis);
				messageBean.setCisA(advert);
			} 
			else {
				messageBean.setMethod(MethodType.FIND_ALL_CIS_ADVERTISEMENT_RECORDS);
			}
			//COMMS CONFIG
			try {
				IIdentity toID = commMgr.getIdManager().getDomainAuthorityNode();
				final ICommCallback cisCallback = new CisDirectoryCallback(client, method);
				final Stanza stanza = new Stanza(toID);
	        
				//only need to register once
				if (!CisDirectoryBase.this.registeredNamespaces) {
					CisDirectoryBase.this.registeredNamespaces = true;
					
		        	commMgr.register(ELEMENT_NAMES, new ICommCallback() {
						
						@Override
						public void receiveResult(Stanza arg1, Object result) {
							boolean status = (Boolean) result;
							if (status) {
					        	try {
									commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback);
								} catch (CommunicationException e) {
									// TODO Auto-generated catch block
									Log.e(LOG_TAG, "Error sending XMPP message", e);
								}
							}
						}
						
						@Override
						public void receiveMessage(Stanza stanza, Object payload) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void receiveItems(Stanza stanza, String node, List<String> items) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void receiveError(Stanza stanza, XMPPError error) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public List<String> getXMLNamespaces() {
							// TODO Auto-generated method stub
							return NAME_SPACES;
						}
						
						@Override
						public List<String> getJavaPackages() {
							// TODO Auto-generated method stub
							return PACKAGES;
						}
					});
				} else {
		        	try {
						commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback);
					} catch (CommunicationException e) {
						// TODO Auto-generated catch block
						Log.e(LOG_TAG, "Error sending XMPP message", e);
					}
				}
					
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "ERROR sending message: " + e.getMessage());
	        }
			return results;
		}

		@Override
		protected void onPostExecute(String results []) {
			Log.d(LOG_TAG, "DomainRegistration - onPostExecute");
	    }
	}

	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> COMMS CALLBACK >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	/**
	 * Callback required for Android Comms Manager
	 */
	private class CisDirectoryCallback implements ICommCallback {
		private String returnIntent;
		private String client;
		
		/**Constructor sets the calling client and Intent to be returned
		 * @param client
		 * @param returnIntent
		 */
		public CisDirectoryCallback(String client, String returnIntent) {
			this.client = client;
			this.returnIntent = returnIntent;
		}

		public List<String> getXMLNamespaces() {
			return NAME_SPACES;
		}

		public List<String> getJavaPackages() {
			return PACKAGES;
		}

		public void receiveError(Stanza arg0, XMPPError err) {
			Log.d(LOG_TAG, "Callback receiveError:" + err.getMessage());			
		}

		public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
			Log.d(LOG_TAG, "Callback receiveInfo");
		}

		public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
			Log.d(LOG_TAG, "Callback receiveItems");
		}

		public void receiveMessage(Stanza arg0, Object arg1) {
			Log.d(LOG_TAG, "Callback receiveMessage");	
		}

		public void receiveResult(Stanza returnStanza, Object msgBean) {
			Log.d(LOG_TAG, "Callback receiveResult");
			
			if (client != null) {
				Intent intent = new Intent(returnIntent);
				
				//TODO: Investigate why stanza is not returning
				
//				Log.d(LOG_TAG, ">>>>>Return Stanza: " + returnStanza.toString());
				if (msgBean==null) Log.d(LOG_TAG, ">>>>msgBean is null");
				// --------- cisDirectoryBeanResult Bean ---------
				if (msgBean instanceof CisDirectoryBeanResult) {
					Log.d(LOG_TAG, "CisDirectoryBeanResult Result!");
					CisDirectoryBeanResult dirResult = (CisDirectoryBeanResult) msgBean;
					List<CisAdvertisementRecord> listReturned = dirResult.getResultCis();
					//CONVERT TO PARCEL BEANS
					//Parcelable returnArray[] = new Parcelable[listReturned.size()];
					//for (int i=0; i<listReturned.size(); i++) {
					//	ACisAdvertisementRecord record = ACisAdvertisementRecord.convertCisAdvertRecord(listReturned.get(i)); 
					//	returnArray[i] = record;
					//	Log.d(LOG_TAG, "Added record: " + record.getId());
					//}
					 CisAdvertisementRecord returnArray[] = listReturned.toArray(new CisAdvertisementRecord[listReturned.size()]);
					
					//NOTIFY CALLING CLIENT
						intent.putExtra(ICisDirectory.INTENT_RETURN_VALUE, returnArray);
					
					//TODO: investigate why wrong client is being used
//					intent.setPackage(client);
					CisDirectoryBase.this.androidContext.sendBroadcast(intent);
					//CisDirectoryBase.this.commMgr.unregister(ELEMENT_NAMES, this);
				}
			}
		}
	}//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> END COMMS CALLBACK >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
}
