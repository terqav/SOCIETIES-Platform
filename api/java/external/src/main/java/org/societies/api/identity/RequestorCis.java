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
package org.societies.api.identity;

import java.io.Serializable;

/**
 * This class is used to represent a CIS requesting resources
 *
 * @author Nicolas, Olivier, Eliza
 *
 */
public class RequestorCis extends Requestor implements Serializable {
	private static final long serialVersionUID = 4071746215222120394L;
	private final IIdentity cisRequestorId;
	
	
	/**
	 * Create a CIS requestor from the CSS and CIS identities
	 * @param requestorId Identity of the CSS ownerof the CIS
	 * @param cisRequestorId CIS identity
	 */
	public RequestorCis(IIdentity requestorId, IIdentity cisRequestorId) {
		super(requestorId);
		this.cisRequestorId = cisRequestorId;
	}

	
	/**
	 * Identity of the CIS requestor
	 * @return the CIS identity
	 */
	public IIdentity getCisRequestorId() {
		return cisRequestorId;
	}
	
	
	/* *************************
	 *         Tools           *
	 ************************* */

	/*
	 * (non-Javadoc)
	 * @see org.societies.api.identity.Requestor#toString()
	 */
	@Override
	public String toString() {
		return super.toString()+", [CIS Identity: "+cisRequestorId.getJid()+"]";
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.api.identity.Requestor#toXMLString()
	 */
	@Override
	public String toXMLString() {
		String cisIdType = new String("CisId");
		StringBuilder str = new StringBuilder(super.toXMLString());
		str.append("\n\t<Attribute AttributeId=\""+cisIdType+"\"");
		str.append("\n\t\t\tDataType=\""+IIdentity.class.getCanonicalName()+"\">");
		str.append("\n\t\t<AttributeValue>");
		str.append(cisRequestorId.getJid());
		str.append("</AttributeValue>");
		str.append("\n\t</Attribute>");
		return str.toString();
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1; //super.hashCode();
		result = prime * result
				+ ((cisRequestorId == null) ? 0 : cisRequestorId.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof RequestorCis)) {
			return false;
		}
		RequestorCis other = (RequestorCis) obj;
		if (cisRequestorId == null) {
			if (other.cisRequestorId != null) {
				return false;
			}
		} else if (!cisRequestorId.equals(other.cisRequestorId)) {
			return false;
		}
		return true;
	}
}
