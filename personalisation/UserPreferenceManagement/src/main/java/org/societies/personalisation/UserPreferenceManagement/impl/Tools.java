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
package org.societies.personalisation.UserPreferenceManagement.impl;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.preference.api.model.ContextPreferenceCondition;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceCondition;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;
import org.societies.personalisation.preference.api.model.OperatorConstants;
import org.societies.personalisation.preference.api.model.PreferenceTreeModel;



public class Tools {
	
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private final ICtxBroker broker;
	
	public Tools(ICtxBroker broker){
		this.broker = broker;
		
	}
	public String convertToKey(String serviceType, String serviceID, String preferenceName){
		return serviceType+":"+serviceID+":"+preferenceName;
	}
	public String convertToKey(String serviceType, ServiceResourceIdentifier serviceID, String preferenceName){
		return serviceType+":"+serviceID.toString()+":"+preferenceName;
	}
	public String convertTokey(String serviceType, ServiceResourceIdentifier serviceID){
		return serviceType+":"+serviceID.toString();
	}
	public IPreferenceTreeModel convertPreferenceObjToModel(IPreference p, String serviceType, ServiceResourceIdentifier serviceID, String preferenceName){
		if (null==p){
			log("preference object is null");
			return null;
		}
		log("constructing PreferenceTreeModel for preference: \n"+p.toString());
		IPreferenceTreeModel iptm = new PreferenceTreeModel(p);
		iptm.setServiceType(serviceType);
		iptm.setServiceID(serviceID);
		iptm.setPreferenceName(preferenceName);
		return iptm;
		
	}
	
	
/*	public IPreferenceTreeModel convertToIPreferenceTreeModel(DefaultTreeModel dtm){
		IPreference ptn = new PreferenceTreeNode();
		
		
		return null;
	}
	*/
	
	public IPreferenceCondition convertToPreferenceCondition(String str){
	
		String key = "";
		String value = "";
		OperatorConstants op;
		String[] a = this.split(str);
		if (null==a){
			return null;
		}
		
		key = a[0];
		op = OperatorConstants.EQUALS.fromString(a[1]);
		value = a[2];
		try{
			Future<List<CtxIdentifier>> futureL = broker.lookup(CtxModelType.ATTRIBUTE, key);
			List<CtxIdentifier> l = futureL.get(); 
			CtxAttributeIdentifier id = (CtxAttributeIdentifier) l.get(0);
			IPreferenceCondition con = new ContextPreferenceCondition(id, op, value, key);
			return con;
			
		}catch(CtxException ce){
			log("problem converting from String to IPreferenceCondition");
			return null;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private String[] split(String str){
		
		String[] ops = {"=",">","<","=>","=<","<=",">="};
		
		for (int i = 0; i< ops.length; i++){
			String[] a = str.split(ops[i]);
			if (a.length>1){
				return a;
			}
		}
		return null;
	}

	private void log(String message){
		this.logging.debug(this.getClass().getName()+" : "+message);
	}

}

