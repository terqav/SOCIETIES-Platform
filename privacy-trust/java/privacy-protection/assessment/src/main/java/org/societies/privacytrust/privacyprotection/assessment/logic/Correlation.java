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
package org.societies.privacytrust.privacyprotection.assessment.logic;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentResultClassName;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentResultIIdentity;
import org.societies.privacytrust.privacyprotection.assessment.logger.DataEvent;

/**
 * Worker class.
 * Correlations between a single data access event and a single data transmission event,
 * calculated for every data transmission event.
 *
 * @author Mitja Vardjan
 *
 */
public class Correlation {

	private static Logger LOG = LoggerFactory.getLogger(Correlation.class);

	private List<DataEvent> dataAccess;
	private List<DataEvent> dataTransmission;
	
	private CorrelationInData correlationInData;
	private CorrelationInTime correlationInTime;
	
	private List<AssessmentResultClassName> assessmentResultClassName = new ArrayList<AssessmentResultClassName>();
	private List<AssessmentResultIIdentity> assessmentResultIIdentity = new ArrayList<AssessmentResultIIdentity>();
	
	public Correlation(List<DataEvent> dataAccess, List<DataEvent> dataTransmission) {
		this.dataAccess = dataAccess;
		this.dataTransmission = dataTransmission;
		correlationInData = new CorrelationInData();
		correlationInTime = new CorrelationInTime();
	}
	
	/**
	 * Perform all calculations. May take a long time.
	 */
	public void run() {
		
		LOG.info("run()");
		
		long size1;
		long size2;
		long time1;
		long time2;
		double corr12;
		
		for (DataEvent tr : dataTransmission) {
			
			size2 = tr.getDataSize();
			time2 = tr.getTime();
			
			for (DataEvent ac : dataAccess) {
				
				size1 = ac.getDataSize();
				time1 = ac.getTime();
				
				corr12 = correlation(size2 - size1, time2 - time1);
			}
		}
	}
	
	/**
	 * Correlation between a single data transmission and a single data access.
	 *
	 * @param deltaSize difference in data size
	 * @param dt difference in time
	 * 
	 * @return correlation value, combined from correlation based on time and
	 * correlation based on data
	 */
	private double correlation(long deltaSize, long dt) {
		
		double cData;
		double cTime;
		
		cData = correlationInData.correlation(deltaSize);
		cTime = correlationInTime.correlation(dt);
		
		return cData * cTime;
	}
}
