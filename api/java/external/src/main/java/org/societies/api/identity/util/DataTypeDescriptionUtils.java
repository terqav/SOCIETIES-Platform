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
package org.societies.api.identity.util;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.schema.identity.DataTypeDescription;

/**
 * Utility class to manipulate DataTypeDescription classes
 * 
 * @author Olivier Maridat (Trialog)
 */
public class DataTypeDescriptionUtils {

	/**
	 * Create a data type description without friendly name: a computed one will be used
	 * @param dataTypeIdentifier
	 * @return The data type description
	 * @throws NullPointerException
	 * @see {@link CtxAttributeTypes}
	 */
	public static DataTypeDescription create(String dataTypeIdentifier)
			throws NullPointerException {
		return create(dataTypeIdentifier, null, "");
	}
	
	/**
	 * 
	 * @param dataTypeIdentifier
	 * @param dataTypeFriendlyName May be null
	 * @return
	 * @throws NullPointerException
	 * @see {@link CtxAttributeTypes}
	 */
	public static DataTypeDescription create(String dataTypeIdentifier, String dataTypeFriendlyName)
			throws NullPointerException {
		return create(dataTypeIdentifier, dataTypeFriendlyName, "");
	}

	/**
	 * 
	 * @param dataTypeIdentifier
	 * @param dataTypeFriendlyName May be null
	 * @param dataTypeFriendlyDescription May be null
	 * @return
	 * @throws NullPointerException
	 * @see {@link CtxAttributeTypes}
	 */
	public static DataTypeDescription create(String dataTypeIdentifier, String dataTypeFriendlyName, String dataTypeFriendlyDescription) 
			throws NullPointerException {
		// Verify
		if (null == dataTypeIdentifier || "".equals(dataTypeIdentifier)) {
			throw new NullPointerException("Data type identifier can't be null");
		}
		if (null == dataTypeFriendlyName || "".equals(dataTypeFriendlyName)) {
			dataTypeFriendlyName = computeFriendlyName(dataTypeIdentifier);
		}
		// Instantiate
		DataTypeDescription res = new DataTypeDescription();
		res.setName(dataTypeIdentifier);
		res.setFriendlyName(dataTypeFriendlyName);
		res.setFriendlyDescription(dataTypeFriendlyDescription);
		return res;
	}


	public static String toString(DataTypeDescription dataType) {
		StringBuilder sb = new StringBuilder();
		if (null != dataType) {
			sb.append("Data type \""+dataType.getName()+"\": "+dataType.getFriendlyName());
			if (null != dataType && !"".equals(dataType.getFriendlyDescription())) {
				sb.append(". Description: "+dataType.getFriendlyDescription());
			}
		}
		return sb.toString();
	}

	public boolean equals(DataTypeDescription o1, Object o2) {
		// -- Verify reference equality
		if (o2 == null) { return false; }
		if (o1 == o2) { return true; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		DataTypeDescription rhs = (DataTypeDescription) o2;
		return new EqualsBuilder()
		.append(o1.getName(), rhs.getName())
		.append(o1.getFriendlyName(), rhs.getFriendlyName())
		.append(o1.getFriendlyDescription(), rhs.getFriendlyDescription())
		.isEquals();
	}
	
	public static String computeFriendlyName(String dataTypeIdentifier) {
		return WordUtils.capitalizeFully(dataTypeIdentifier, new char[]{'_'}).replaceAll("_", " ");
	}
}
