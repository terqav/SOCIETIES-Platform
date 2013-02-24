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
package org.societies.platform.TwitterConnector;

import org.societies.api.internal.sns.ISocialConnectorInternal;

public interface TwitterConnector extends ISocialConnectorInternal {

	public static final String PROTECTED_RESOURCE_URL = "https://api.twitter.com/1/statuses/update.json";
	public static final String ACCOUNT_VERIFICATION = "https://api.twitter.com/1/account/verify_credentials.json";
	public static final String GET_USERINFO_URL = "http://api.twitter.com/1/users/lookup.json?user_id=";
	public static final String GET_FRIENDS_URL = "https://api.twitter.com/1/friends/ids.json?user_id=";
	public static final String GET_FOLLOWERS_URL = "https://api.twitter.com/1/followers/ids.json?user_id=";
	public static final String GET_OTHER_PROFILE_URL = "https://api.twitter.com/1/users/lookup.json?user_id=";
	public static final String GET_TWEETS_URL = "http://api.twitter.com/1/statuses/home_timeline.json?count=200";
	public static final String POST_TWEET_URL = "https://api.twitter.com/1/statuses/update.json";
	
	
	public static final String TW_CLIENT_ID 		= "LjzBv5lvg673UYJ4xp3CYw";
	public static final String TW_CLIENT_SECRET	= "eHrtoM2A6mlix8L9kMacDi90IH46xoo6G5uceble6A";
	
	public static final String TW_CALLBACK_URL		= "http://127.0.0.1:8080/societies-test/doConnect.html?type=tw";
	
	
	/**
	 * @return
	 */
	String getUserFollowers();

	/**
	 * @param id
	 * @return
	 */
	String getOtherProfileString(String id);
}