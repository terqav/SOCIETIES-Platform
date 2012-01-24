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
package org.societies.context.userHistory.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.context.api.user.history.IUserCtxHistoryCallback;
import org.societies.context.api.user.history.IUserCtxHistoryMgr;



public class UserContextHistoryManagement implements IUserCtxHistoryMgr {

	private final LinkedHashMap<CtxHistoryAttribute,Date> hocObjects;

	public UserContextHistoryManagement(){
		this.hocObjects =  new LinkedHashMap<CtxHistoryAttribute,Date>();
	}



	public void storeHoCAttribute(CtxAttribute ctxAttribute,Date date){
		CtxHistoryAttribute hocAttr = new CtxHistoryAttribute(ctxAttribute);
		this.hocObjects.put(hocAttr, date);	
	}


	@Override
	public void disableCtxRecording(IUserCtxHistoryCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enableCtxRecording(IUserCtxHistoryCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getHistoryTuplesID(
			CtxAttributeIdentifier primaryAttrIdentifier,IUserCtxHistoryCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerHistoryTuples(
			CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds,IUserCtxHistoryCallback callback) {
		// TODO Auto-generated method stub

	}
/*
	@Override
	public void registerHistoryTuples(
			CtxAttributeIdentifier primaryAttrIdentifier,
			CtxAttributeIdentifier listOfEscortingAttributeTypes) {
		// TODO Auto-generated method stub

	}
*/
	@Override
	public void removeHistory(CtxAttribute ctxAttribute, Date startDate,
			Date endDate,IUserCtxHistoryCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeHistory(String type, Date startDate, Date endDate,IUserCtxHistoryCallback callback) {
		// TODO Auto-generated method stub
		
	}


	public List<CtxHistoryAttribute> retrieveHistory(CtxAttributeIdentifier attrId) {

		List<CtxHistoryAttribute> results = new ArrayList<CtxHistoryAttribute>();
		for(CtxHistoryAttribute ctxHistoryAttribute : this.hocObjects.keySet()){
			if (ctxHistoryAttribute.getId().equals(attrId)){
				results.add(ctxHistoryAttribute);
			}
		}

		return results;
	}

	@Override
	public void retrieveHistory(CtxAttributeIdentifier attrId,
			Date startDate, Date endDate,IUserCtxHistoryCallback callback) {
		
		List<CtxHistoryAttribute> results = new ArrayList<CtxHistoryAttribute>();
		results = retrieveHistory(attrId);
	 
		callback.historyRetrievedDate(results);
	}

	@Override
	public void retrieveHistoryTuples(
			CtxAttributeIdentifier primaryAttrID,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds,
			Date startDate, Date endDate,IUserCtxHistoryCallback callback) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void retrieveHistory(CtxAttributeIdentifier attrId, int modificationIndex,IUserCtxHistoryCallback callback) {
		// TODO Auto-generated method stub
		
	}

}
