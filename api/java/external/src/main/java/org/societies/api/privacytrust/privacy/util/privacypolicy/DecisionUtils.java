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
package org.societies.api.privacytrust.privacy.util.privacypolicy;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Decision;

/**
 * Tool class to manage conversion between Java type and Bean XMLschema generated type
 * @author Olivier Maridat (Trialog)
 */
public class DecisionUtils {
	
	public static Decision toDecision(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision decisionBean)
	{
		if (null == decisionBean) {
			return null;
		}
		return Decision.valueOf(decisionBean.name());
	}
	public static List<Decision> toDecisions(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision> decisionBeans)
	{
		if (null == decisionBeans) {
			return null;
		}
		List<Decision> decisions = new ArrayList<Decision>();
		for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision decisionBean : decisionBeans) {
			decisions.add(DecisionUtils.toDecision(decisionBean));
		}
		return decisions;
	}
	
	public static org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision toDecisionBean(Decision decision)
	{
		if (null == decision) {
			return null;
		}
		return org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision.valueOf(decision.name());
	}
	public static List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision> toDecisionBeans(List<Decision> decisions)
	{
		if (null == decisions) {
			return null;
		}
		List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision> decisionBeans = new ArrayList<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision>();
		for(Decision decision : decisions) {
			decisionBeans.add(DecisionUtils.toDecisionBean(decision));
		}
		return decisionBeans;
	}
	
	public static String toXmlString(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision decision){
		StringBuilder sb = new StringBuilder();
		if (null != decision) {
			sb.append("\n<Decision>\n");
			sb.append("\t<Attribute AttributeId=\"Decision\" DataType=\""+decision.getClass().getName()+"\">\n");
			sb.append("\t\t<AttributeValue>"+decision.name()+"</AttributeValue>\n");
			sb.append("\t</Attribute>\n");
			sb.append("</Decision>");
		}
		return sb.toString();
	}
	
	public static boolean equals(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision o1, Object o2) {
		// -- Verify reference equality
		if (o2 == null) { return false; }
		if (o1 == o2) { return true; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision rhs = (org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision) o2;
		return new EqualsBuilder()
		.append(o1.name(), rhs.name())
		.isEquals();
	}
}
