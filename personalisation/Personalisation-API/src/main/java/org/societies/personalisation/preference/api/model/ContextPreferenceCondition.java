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
package org.societies.personalisation.preference.api.model;

/**
 * @author Elizabeth
 * @version 1.0
 * @created 08-Nov-2011 14:02:56
 */
import java.io.Serializable;

import org.societies.api.context.model.CtxAttributeIdentifier;




public class ContextPreferenceCondition implements IPreferenceCondition, Serializable{

	private CtxAttributeIdentifier CtxIdentifier;
	//private String strCtxId;
	private String type;
	private String name;
	private String value;
	private OperatorConstants operator;


	public ContextPreferenceCondition(CtxAttributeIdentifier ctxId, OperatorConstants op, String val, String name){
		this.CtxIdentifier = ctxId;
		this.operator = op;
		this.value = val;
		this.type = "context";
		this.name = name;

	}
	
	public String getType(){
		return this.type;
	}


	public CtxAttributeIdentifier getCtxIdentifier(){
		return CtxIdentifier;
	}


	public void setCtxIdentifier(CtxAttributeIdentifier newVal){
		CtxIdentifier = newVal;
	}

	public String getname(){
		return name;
	}


	public void setname(String newVal){
		name = newVal;
	}

	public String getvalue(){
		return value;
	}


	public void setvalue(String newVal){
		value = newVal;
	}

	public OperatorConstants getoperator(){
		return operator;
	}

	public void setoperator(OperatorConstants op){
		operator = op;
	}
	
	public String toString(){
		
		return this.CtxIdentifier.getType()+this.operator+this.value;
	}
	
	public boolean equals(IPreferenceCondition pc){

		if (!(pc.getname().equals(this.name))){
			return false;
		}
		if (!(pc.getoperator().equals(this.operator))){
			return false;
		}
		if (!(pc.getCtxIdentifier().equals(this.CtxIdentifier))){
			return false;
		}
		if (!(pc.getType().equals(this.type))){
			return false;
		}
		if (!(pc.getvalue().equals(this.value))){
			return false;
		}
		
		return true;
	}
	public boolean equalsIgnoreValue(IPreferenceCondition pc){
		if (!(pc.getCtxIdentifier().equals(this.CtxIdentifier))){
			return false;
		}
		if (!(pc.getname().equals(this.name))){
			return false;
		}
		if (!(pc.getoperator().equals(this.operator))){
			return false;
		}
		if (!(pc.getCtxIdentifier().equals(this.CtxIdentifier))){
			return false;
		}
		if (!(pc.getType().equals(this.type))){
			return false;
		}
		
		return true;
	}
}