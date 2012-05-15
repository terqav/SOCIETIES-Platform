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
package org.societies.personalisation.CRISTUserIntentDiscovery.test;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.societies.personalisation.CRIST.api.CRISTUserIntentDiscovery.ICRISTUserIntentDiscovery;
import org.societies.personalisation.CRISTUserIntentDiscovery.impl.CRISTUserIntentDiscovery;
import org.societies.personalisation.CRISTUserIntentTaskManager.impl.MockHistoryData;

/**
 * Describe your class here...
 *
 * @author Zhu WANG
 *
 */
public class CRISTUserIntentDiscoveryTesting {

	/**
	 * @param args
	 */
	private static ICRISTUserIntentDiscovery cristDiscovery = new CRISTUserIntentDiscovery();
	private static ArrayList<String> registeredContext = new ArrayList<String>();
	private static ArrayList<MockHistoryData> historyList = new ArrayList<MockHistoryData>();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("CRIST UI Discovery Testing");
		initiateHistoryData();
		cristDiscovery.enableCRISTUIDiscovery(true);		
		@SuppressWarnings("unchecked")
		LinkedHashMap<String, Integer> intentModel = cristDiscovery.generateNewCRISTUIModel(historyList);
		if (intentModel == null) {
			System.out.println("The result is NULL.");
		} else {
			System.out.println(intentModel.toString());
		}
	}

	private static void initiateHistoryData(){
		ArrayList<String> historyAction = new ArrayList<String>();
		ArrayList<String> historySituation = new ArrayList<String>();
		ArrayList<ArrayList<String>> historyContext = new ArrayList<ArrayList<String>>();
		// Assuming that the following sensors are available: light, sound,
		// temperature, gps
		registeredContext.add("Light");
		registeredContext.add("Sound");
		registeredContext.add("Temperature");
		registeredContext.add("GPS");

		ArrayList<String> historyContextClique = new ArrayList<String>();

		// Mock history data
		historyAction.add("Turn on MP3 Player");
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);

		historyAction.add("Turn down volume");
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);

		historyAction.add("Switch songs");
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);

		historyAction.add("Turn off MP3 Player");
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);

		historyAction.add("Start the GPS navigator");
		historySituation.add("Outdoor");
		historyContextClique.add("120");
		historyContextClique.add("60");
		historyContextClique.add("15");
		historyContextClique.add("55,1.33");
		historyContext.add(historyContextClique);

		historyAction.add("Input a location name");
		historySituation.add("Outdoor");
		historyContextClique.add("120");
		historyContextClique.add("60");
		historyContextClique.add("15");
		historyContextClique.add("55,1.33");
		historyContext.add(historyContextClique);

		historyAction.add("Close the GPS navigator");
		historySituation.add("Shopping Mall");
		historyContextClique.add("100");
		historyContextClique.add("80");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);

		historyAction.add("Turn on MP3 Player");
		historySituation.add("Outdoor");
		historyContextClique.add("120");
		historyContextClique.add("60");
		historyContextClique.add("15");
		historyContextClique.add("55,1.33");
		historyContext.add(historyContextClique);

		historyAction.add("Turn up volume");
		historySituation.add("Outdoor");
		historyContextClique.add("120");
		historyContextClique.add("60");
		historyContextClique.add("15");
		historyContextClique.add("55,1.33");
		historyContext.add(historyContextClique);

		historyAction.add("Switch songs");
		historySituation.add("Outdoor");
		historyContextClique.add("120");
		historyContextClique.add("60");
		historyContextClique.add("15");
		historyContextClique.add("55,1.33");
		historyContext.add(historyContextClique);

		historyAction.add("Turn off MP3 Player");
		historySituation.add("Office");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("26");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);

		historyAction.add("Start the GPS navigator");
		historySituation.add("Outdoor");
		historyContextClique.add("120");
		historyContextClique.add("60");
		historyContextClique.add("15");
		historyContextClique.add("55,1.33");
		historyContext.add(historyContextClique);

		historyAction.add("Input a location name");
		historySituation.add("Outdoor");
		historyContextClique.add("120");
		historyContextClique.add("60");
		historyContextClique.add("15");
		historyContextClique.add("55,1.33");
		historyContext.add(historyContextClique);

		historyAction.add("Close the GPS navigator");
		historySituation.add("Shopping Mall");
		historyContextClique.add("100");
		historyContextClique.add("80");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);

		historyAction.add("Turn on MP3 Player");
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);

		historyAction.add("Turn down volume");
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);

		historyAction.add("Switch songs");
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);

		historyAction.add("Turn off MP3 Player");
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);

		historyAction.add("Turn on MP3 Player");
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);

		historyAction.add("Turn down volume");
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);

		for (int i = 0; i < historyAction.size(); i++) {
			MockHistoryData currentHisData = new MockHistoryData(
					historyAction.get(i), historySituation.get(i),
					historyContext.get(i));
			historyList.add(currentHisData);
		}
	}
}
