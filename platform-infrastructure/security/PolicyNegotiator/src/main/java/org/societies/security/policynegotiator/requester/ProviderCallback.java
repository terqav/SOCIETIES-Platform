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
package org.societies.security.policynegotiator.requester;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.schema.security.policynegotiator.MethodType;
import org.societies.api.internal.schema.security.policynegotiator.SlaBean;
import org.societies.api.internal.security.policynegotiator.INegotiationProvider;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderCallback;
import org.societies.security.policynegotiator.sla.SLA;
import org.societies.security.policynegotiator.xml.Xml;
import org.societies.security.policynegotiator.xml.XmlException;

/**
 * This class receives results from async invocations of {@link INegotiationProvider}
 *
 * @author Mitja Vardjan
 *
 */
public class ProviderCallback implements INegotiationProviderCallback {

	private static Logger LOG = LoggerFactory.getLogger(ProviderCallback.class);

	private NegotiationRequester requester;
	private MethodType method;
	
	public ProviderCallback(NegotiationRequester requester, MethodType method) {
		this.requester = requester;
		this.method = method;
		LOG.debug("ProviderCallback({})", method);
	}

//	@Override
//	public void onGetPolicyOptions(int sessionId, String sops) {
//		// TODO Auto-generated method stub
//	}

//	@Override
//	public void onAcceptPolicyAndGetSla(int sessionId, String policy) {
//		// TODO Auto-generated method stub
//	}
	
	/* (non-Javadoc)
	 * @see org.societies.api.internal.security.policynegotiator.INegotiationProviderCallback
	 * #receiveExamplesResult(java.lang.Object)
	 */
	@Override
	public void receiveResult(SlaBean result) {
		
		LOG.debug("receiveResult(): method = ", method);
		
		int sessionId = result.getSessionId();
		
		switch(method) {
		case GET_POLICY_OPTIONS:
			if (result.isSuccess()) {
				String sop;
				sop = result.getSla();
				try {
					String selectedSop = selectSopOption(sop);
					// TODO: use real identity when it can be gathered from other components
					sop = requester.getSignatureMgr().signXml(sop, selectedSop, "identity");
					requester.getGroupMgr().acceptPolicyAndGetSla(
							sessionId,
							sop,
							false,
							new ProviderCallback(requester, MethodType.ACCEPT_POLICY_AND_GET_SLA));
				} catch (XmlException e) {
					LOG.warn("receiveResult(): session {}: ", sessionId, e);
				}
			}
			break;
		case ACCEPT_POLICY_AND_GET_SLA:
			if (result.isSuccess()) {
				String sla;
				sla = result.getSla();
				if (requester.getSignatureMgr().verify(sla)) {
					LOG.info("receiveResult(): session = {}, final SLA reached.", sessionId);
					LOG.debug("receiveResult(): session = {}, final SLA: {}", sessionId, sla);
					// TODO: store the SLA when secure services are implemented
				}
				else {
					LOG.warn("receiveResult(): session = {}, final SLA invalid!", sessionId);
				}
			}
			break;
		case REJECT:
			// No need for action.
			// After more tests, the method could be changed back to void to save some bandwidth.
			LOG.debug("receiveResult(): session = {}, reject success = ", sessionId, result.isSuccess());
			break;
		}
	}
	
	private String selectSopOption(String sopString) throws XmlException {
		
		Xml xml = new Xml(sopString);
		SLA sop = new SLA(xml);
		String[] sopName = sop.getSopNames();
		String[] providerName = new String[sopName.length];
		String[] sopContent = new String[sopName.length];
		
		for (int k = 0; k < sopName.length; k++) {
			providerName[k] = sop.getProviderName(sopName[k]);
			sopContent[k] = sop.getSopContent(sopName[k]);
			LOG.debug("selectSopOption(): SOP = {}, provider = {}", sopName[k], providerName[k]);
		}

		return sopName[0];  // TODO: display all options in GUI and return what user has chosen
	}
}