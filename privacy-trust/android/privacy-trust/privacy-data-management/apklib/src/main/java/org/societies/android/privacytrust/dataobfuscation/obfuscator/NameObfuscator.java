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
package org.societies.android.privacytrust.dataobfuscation.obfuscator;

import org.societies.android.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.internal.schema.privacytrust.model.dataobfuscation.DataWrapper;
import org.societies.api.internal.schema.privacytrust.model.dataobfuscation.Name;
import org.societies.api.internal.schema.privacytrust.model.dataobfuscation.ObfuscationLevelType;

/**
 * Obfuscator for name
 *
 * @author Olivier Maridat (Trialog)
 *
 */
public class NameObfuscator extends DataObfuscator<DataWrapper> {
	/**
	 * @param data
	 */
	public NameObfuscator(DataWrapper data) {
		super(data);
		available = true;
		obfuscationLevelType = ObfuscationLevelType.DISCRETE;
		stepNumber = 4;
		dataType = Name.class;
	}


	/*
	 * (non-Javadoc)
	 * @see org.societies.android.api.internal.privacytrust.model.dataobfuscation.obfuscator.IDataObfuscator#obfuscateData(double)
	 */
	public DataWrapper obfuscateData(double obfuscationLevel)
			throws PrivacyException {
		Name name = (Name) dataWrapper.getData();
		// -- Verify
		if (null == name.getFirstName()) {
			name.setFirstName("");
		}
		if (null == name.getLastName()) {
			name.setLastName("");
		}

		// -- Obfuscate
		// 0: nothing
		Name obfuscatedName = new Name();
		if (obfuscationLevel <= 0) {
			obfuscatedName.setFirstName("");
			obfuscatedName.setLastName("");
		}
		// 1: first letters
		else if (obfuscationLevel > 0 && obfuscationLevel <= 1/stepNumber) {
			obfuscatedName.setFirstName((name.getFirstName() != "" ? name.getFirstName().substring(0, 1)+"." : ""));
			obfuscatedName.setLastName((name.getFirstName() != "" ? name.getLastName().substring(0, 1)+"." : ""));
		}
		// 2: firstname only
		else if (obfuscationLevel > 1/stepNumber && obfuscationLevel <= 2/stepNumber) {
			obfuscatedName.setFirstName(name.getFirstName());
			obfuscatedName.setLastName("");
		}
		// 3: lastname only
		else if (obfuscationLevel > 2/stepNumber && obfuscationLevel <= 3/stepNumber) {
			obfuscatedName.setFirstName("");
			obfuscatedName.setLastName(name.getLastName());
		}
		// 4: everything
		else if (obfuscationLevel >= 1) {
			obfuscatedName = name;
		}
		DataWrapper obfuscatedWrapper = new DataWrapper();
		obfuscatedWrapper.setData(obfuscatedName);
		return obfuscatedWrapper;
	}

}
