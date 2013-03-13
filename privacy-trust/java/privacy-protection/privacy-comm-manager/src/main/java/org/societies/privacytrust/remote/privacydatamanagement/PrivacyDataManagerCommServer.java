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

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
package org.societies.privacytrust.remote.privacydatamanagement;

import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxIdentifierFactory;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.util.DataIdentifierFactory;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.DataWrapperFactory;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.api.internal.privacytrust.privacyprotection.util.remote.Util;
import org.societies.api.internal.schema.privacytrust.model.dataobfuscation.DataWrapper;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.MethodType;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.PrivacyDataManagerBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.PrivacyDataManagerBeanResult;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponseItemUtils;
import org.societies.api.schema.identity.DataIdentifier;


public class PrivacyDataManagerCommServer {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyDataManagerCommServer.class);

	private ICommManager commManager;
	private IPrivacyDataManager privacyDataManager;
	
	
	public PrivacyDataManagerCommServer() {
	}

	
	public Object getQuery(Stanza stanza, PrivacyDataManagerBean bean){
		PrivacyDataManagerBeanResult beanResult = new PrivacyDataManagerBeanResult();
		boolean ack = true;

		// -- Check Permission
		if (bean.getMethod().equals(MethodType.CHECK_PERMISSION)) {
			LOG.info("getQuery(): CheckPermission remote called");
			beanResult.setMethod(MethodType.CHECK_PERMISSION);
			ack = checkPermission(bean, beanResult);
			LOG.info("getQuery(): CheckPermission remote response sending");
		}

		// -- Obfuscate Data
		else if (bean.getMethod().equals(MethodType.OBFUSCATE_DATA)) {
			LOG.info("getQuery(): ObfuscateData remote called");
			beanResult.setMethod(MethodType.OBFUSCATE_DATA);
			ack = obfuscateData(bean, beanResult);
			LOG.info("getQuery(): ObfuscateData remote response sending");
		}
		else {
			LOG.info("getQuery(): Unknown method "+bean.getMethod().name());
			beanResult.setAckMessage("Error Unknown method "+bean.getMethod().name());
		}

		beanResult.setAck(ack);
		return beanResult;
	}
	
	private boolean checkPermission(PrivacyDataManagerBean bean, PrivacyDataManagerBeanResult beanResult) {
		try {
			Requestor requestor = Util.getRequestorFromBean(bean.getRequestor(), commManager);
			List<Action> actions = ActionUtils.toActions(bean.getActions());
			DataIdentifier dataId = DataIdentifierFactory.fromUri(bean.getDataIdUri());
			ResponseItem permission = privacyDataManager.checkPermission(requestor, dataId, actions);
			beanResult.setPermission(ResponseItemUtils.toResponseItemBean(permission));
		} catch (MalformedCtxIdentifierException e) {
			beanResult.setAckMessage("Error MalformedCtxIdentifierException: "+e.getMessage());
			return false;
		}
		catch (PrivacyException e) {
			beanResult.setAckMessage("Error PrivacyException: "+e.getMessage());
			return false;
		}
		return true;
	}
	
	private boolean obfuscateData(PrivacyDataManagerBean bean, PrivacyDataManagerBeanResult beanResult) {
		try {
			Requestor requestor = Util.getRequestorFromBean(bean.getRequestor(), commManager);
			DataWrapper dataWrapper = bean.getDataWrapper();
//			Future<IDataWrapper> obfuscatedDataWrapperAsync = privacyDataManager.obfuscateData(requestor, dataWrapper);
			beanResult.setAckMessage("Sorry, the obfuscation is available, but not remotely yet.");
			return false;
		} catch (Exception e) {
			beanResult.setAckMessage("Error Exception: "+e.getMessage());
			return false;
		}
//		catch (PrivacyException e) {
//			beanResult.setAckMessage("Error PrivacyException: "+e.getMessage());
//			return false;
//		} catch (InvalidFormatException e) {
//			beanResult.setAckMessage("Error InvalidFormatException: "+e.getMessage());
//			return false;
//		}
//		return true;
	}
	
	
	
	// -- Dependency Injection

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
		LOG.info("[DepencyInjection] CommManager injected");
	}
	public void setPrivacyDataManager(IPrivacyDataManager privacyDataManager) {
		this.privacyDataManager = privacyDataManager;
		LOG.info("[DepencyInjection] PrivacyDataManager injected");
	}
}
