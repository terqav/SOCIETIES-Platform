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
package org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.societies.api.identity.IIdentity;

/**
 * High-level interface for Privacy Assessment.
 *
 * @author Mitja Vardjan
 *
 */
public interface IAssessment {

	/**
	 * Get time interval after which the Privacy Assessment automatically
	 * evaluates past events and performs assessment.
	 * 
	 * @return seconds period in seconds, negative value for no auto update
	 */
	public int getAutoPeriod();

	/**
	 * Set time interval after which the Privacy Assessment will automatically
	 * evaluate past events and perform assessment.
	 * 
	 * @param seconds period in seconds, negative value for no auto update
	 */
	public void setAutoPeriod(int seconds);
	
	/**
	 * Perform all assessments and update assessment values. May take a long time.
	 */
	public void assessAllNow();
	
	/**
	 * Get a-posteriori assessment for all sender {@link IIdentity} values.
	 */
	public HashMap<IIdentity, AssessmentResultIIdentity> getAssessmentAllIds();
	
	/**
	 * Get a-posteriori assessment for all sender classes.
	 */
	public HashMap<String, AssessmentResultClassName> getAssessmentAllClasses();

	/**
	 * Get a-posteriori assessment for a particular sender {@link IIdentity}.
	 * 
	 * @param sender sender identity that was self-reported by the sender
	 * 
	 * @return privacy assessment for given sender
	 */
	public AssessmentResultIIdentity getAssessment(IIdentity sender);

	/**
	 * Get a-posteriori assessment for a particular sender class name.
	 * 
	 * @param sender class name of the sender as determined by the Privacy Assessment
	 * 
	 * @return privacy assessment for given sender
	 */
	public AssessmentResultClassName getAssessment(String senderClassName);
	
	/**
	 * Development only.
	 * TODO: remove from API and implementation
	 * 
	 * @return number of all recorded data transmission events
	 */
	public long getNumDataTransmissionEvents();
	
	/**
	 * @return All identities that have requested access to local data
	 */
	public List<IIdentity> getDataAccessRequestors();
	
	/**
	 * @return Names of all classes that have requested access to local data
	 */
	public List<String> getDataAccessRequestorClasses();

	/**
	 * @return number of all recorded data access events
	 */
	public long getNumDataAccessEvents();

	/**
	 * Get number of events in certain time period where given requestor accessed local data.
	 * 
	 * @param requestor Identity of the requestor (the one who requested data access)
	 * @param start Match only events after this time
	 * @param end Match only events before this time
	 * @return All events where requestor matches
	 */
	public int getNumDataAccessEvents(IIdentity requestor, Date start, Date end);

	/**
	 * Get number of events in certain time period where given requestor accessed local data.
	 * 
	 * @param requestorClass Class name of the requestor (the one who requested data access)
	 * @param start Match only events after this time
	 * @param end Match only events before this time
	 * @return All events where requestor matches
	 */
	public int getNumDataAccessEvents(String requestorClass, Date start, Date end);

	/**
	 * Get number of local data access events in certain time period, grouped by requestor identity.
	 * 
	 * @param start Match only events after this time
	 * @param end Match only events before this time
	 * @return Number of data access events for each requestor class name
	 */
	public Map<IIdentity, Integer> getNumDataAccessEventsForAllIdentities(Date start, Date end);

	/**
	 * Get number of local data access events in certain time period, grouped by requestor class name.
	 * 
	 * @param start Match only events after this time
	 * @param end Match only events before this time
	 * @return Number of data access events for each requestor identity
	 */
	public Map<String, Integer> getNumDataAccessEventsForAllClasses(Date start, Date end);
	
	/**
	 * @return All identities the messages have been sent to.
	 */
	public List<IIdentity> getDataTransmissionReceivers();

	/**
	 * Get number of events in certain time period where data has been sent to given receiver.
	 * 
	 * @param receiver Identity of the receiver (the one data has been sent to)
	 * @param start Match only events after this time
	 * @param end Match only events before this time
	 * @return All events where receiver matches
	 */
	public int getNumDataTransmissionEvents(IIdentity receiver, Date start, Date end);
	
	/**
	 * Get number of events in certain time period where data has been sent to any receiver.
	 * Events are grouped by receiver identity.
	 * 
	 * @param start Match only events after this time
	 * @param end Match only events before this time
	 * @return Number of data transmission events for each receiver
	 */
	public Map<IIdentity, Integer> getNumDataTransmissionEventsForAllReceivers(Date start, Date end);
}
