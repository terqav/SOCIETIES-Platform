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
package org.societies.privacytrust.privacyprotection.assessment.log;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.ChannelType;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.IPrivacyLogAppender;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.LogEntry;

/**
 * 
 *
 * @author Mitja Vardjan
 *
 */
public class PrivacyLogAppender implements IPrivacyLogAppender {

	private static Logger LOG = LoggerFactory.getLogger(PrivacyLogAppender.class);

	/* (non-Javadoc)
	 * @see IPrivacyLogAppender#log(LogEntry)
	 */
	@Override
	public boolean log(LogEntry entry) {
		
		LOG.debug("log()");

		return true;
	}

	/* (non-Javadoc)
	 * @see IPrivacyLogAppender#logCommsFw(IIdentity, IIdentity, Object)
	 */
	@Override
	public boolean logCommsFw(IIdentity sender, IIdentity receiver, Object payload) {
		
		LOG.debug("logCommsFw()");

		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		LOG.debug("stackTrace length = {}", stackTrace.length);
		if (stackTrace != null) {
			for (StackTraceElement st : stackTrace) {
				LOG.debug(" ");
				LOG.debug("  ClassName : {}", st.getClassName());
				//LOG.debug("  FileName  : {}", st.getFileName());
				//LOG.debug("  MethodName: {}", st.getMethodName());
			}
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see IPrivacyLogAppender#logSN(String, Date, boolean, IIdentity, IIdentity, ChannelType)
	 */
	@Override
	public boolean logSN(String dataType, Date time, boolean sentToGroup, IIdentity sender,
			IIdentity receiver, ChannelType channelId) {
		
		LOG.debug("logSN()");

		return true;
	}

}
