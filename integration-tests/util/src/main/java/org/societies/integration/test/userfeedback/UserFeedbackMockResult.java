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
package org.societies.integration.test.userfeedback;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for a mock user feedback result
 * @author Olivier Maridat (Trialog)
 */
public class UserFeedbackMockResult {
	public static final int InfinitelyUsable = -255;
	private int nbOfUsage;
	private List<String> result;
	private List<Integer> resultIndexes;

	/**
	 * Instantiate a user feedback result that will be used infinitely
	 * @param result Result values
	 */
	public UserFeedbackMockResult(String... result) {
		this(InfinitelyUsable, result);
	}
	/**
	 * Instantiate a user feedback result that will be used nbOfUsage time
	 * @param nbOfUsage Nb of time to use theses values as a user feedback result
	 * @param result Result values
	 */
	public UserFeedbackMockResult(int nbOfUsage, String... result) {
		super();
		this.nbOfUsage = nbOfUsage;
		this.resultIndexes = new ArrayList<Integer>();
		this.result = new ArrayList<String>();
		for(String resultItem : result) {
			this.result.add(resultItem);
		}
	}
	/**
	 * Instantiate a user feedback result that will be used nbOfUsage time
	 * @param nbOfUsage Nb of time to use theses values as a user feedback result
	 * @param result Result index values
	 */
	public UserFeedbackMockResult(Integer... resultIndexes) {
		super();
		this.nbOfUsage = InfinitelyUsable;
		this.result = new ArrayList<String>();
		this.resultIndexes = new ArrayList<Integer>();
		for(Integer resultIndex : resultIndexes) {
			this.resultIndexes.add(resultIndex);
		}
	}

	
	/**
	 * Retrieve the number of time these values can be used as user feedback result
	 * @return number of time these values can be used as user feedback result
	 */
	public int getNbOfUsage() {
		return nbOfUsage;
	}
	/**
	 * Modify the number of time these values can be used as user feedback result
	 * @param nb number of time these values can be used as user feedback result
	 */
	public void setNbOfusage(int nb) {
		this.nbOfUsage = nb;
	}
	/**
	 * Increment the number of time these values can be used as user feedback result
	 * @param incr Nb to add to nbOfUsage (e.g. -1)
	 */
	public void incrNbOfusage(int incr) {
		this.nbOfUsage += incr;
	}
	/**
	 * To know if these values are still usable as user feedback result
	 * @return True if usable, false otherwise
	 */
	public boolean isUsable() {
		return InfinitelyUsable == nbOfUsage || nbOfUsage > 0;
	}
	/**
	 * Retrieve the list of values to use as user feedback result 
	 * @return list of values to use as user feedback result
	 */
	public List<String> getResult() {
		return result;
	}
	/**
	 * Modify the list of values to use as user feedback result 
	 * @param result list of values to use as user feedback result
	 */
	public void setResult(List<String> result) {
		if (null == this.result) {
			this.result = new ArrayList<String>();
		}
		this.result = result;
	}
	/**
	 * Add a value to the list of values to use as user feedback result 
	 * @param value value to add
	 */
	public void addResult(String value) {
		if (null == this.result) {
			this.result = new ArrayList<String>();
		}
		this.result.add(value);
	}
	/**
	 * Retrieve the list of indexes of the proposed user feedback options. They will be used to retrieved values to use as user feedback result
	 * @return list of indexes of the proposed user feedback options
	 */
	public List<Integer> getResultIndexes() {
		return resultIndexes;
	}
	/**
	 * Modify the list of indexes of the proposed user feedback options. They will be used to retrieved values to use as user feedback result
	 * @return list of indexes of the proposed user feedback options
	 */
	public void setResultIndexes(List<Integer> resultIndexes) {
		if (null == this.resultIndexes) {
			this.resultIndexes = new ArrayList<Integer>();
		}
		this.resultIndexes = resultIndexes;
	}
	/**
	 * Add a value to the list of indexes of the proposed user feedback options. They will be used to retrieved values to use as user feedback result
	 * @param value value to add
	 */
	public void addResultIndexes(Integer value) {
		if (null == this.resultIndexes) {
			this.resultIndexes = new ArrayList<Integer>();
		}
		this.resultIndexes.add(value);
	}
}
