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
package org.societies.privacytrust.privacyprotection.privacypreferencemanager.management;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPrivacyPreferenceTreeModelBean;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestorUtils;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyPreferenceTypeConstants;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.dobf.DObfPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IDSPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.util.preference.PrivacyPreferenceUtils;

public class PrivatePreferenceCache {

	
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private Registry registry;
	private PreferenceRetriever retriever;
	private PreferenceStorer storer;
	//the key refers to the ctxID of the (ppn) preference attribute (not the affected context attribute)
	private Hashtable<CtxAttributeIdentifier, PPNPrivacyPreferenceTreeModel> ppnCtxIDtoModel;
	//the key refers to the ctxID of the (ids) preference attribute (not the affected context attribute)
	private Hashtable<CtxAttributeIdentifier, IDSPrivacyPreferenceTreeModel> idsCtxIDtoModel;
	//the key refers to the ctxID of the (ids) preference attribute (not the affected context attribute)
	private Hashtable<CtxAttributeIdentifier, DObfPreferenceTreeModel> dobfCtxIDtoModel;
	//the key refers to the ctxID of the (accCtrl) preference attribute (not the affected context attribute)
	private Hashtable<CtxAttributeIdentifier, AccessControlPreferenceTreeModel> accCtrlCtxIDtoModel;
	
	private IIdentityManager idMgr;
	
	

	public PrivatePreferenceCache(ICtxBroker broker, IIdentityManager idMgr){
		this.idMgr = idMgr;
		this.ppnCtxIDtoModel = new Hashtable<CtxAttributeIdentifier,PPNPrivacyPreferenceTreeModel>();
		this.idsCtxIDtoModel = new Hashtable<CtxAttributeIdentifier, IDSPrivacyPreferenceTreeModel>();
		this.dobfCtxIDtoModel = new Hashtable<CtxAttributeIdentifier, DObfPreferenceTreeModel>();
		this.accCtrlCtxIDtoModel = new Hashtable<CtxAttributeIdentifier, AccessControlPreferenceTreeModel>();
		this.retriever = new PreferenceRetriever(broker, this.idMgr);
		this.storer = new PreferenceStorer(broker, idMgr);
		this.registry = retriever.retrieveRegistry();
		
	}
	
	
	public void addPPNPreference(PPNPreferenceDetailsBean details, PPNPrivacyPreferenceTreeModel model) throws PrivacyException{
		printCacheContentsOnScreen("Before update");
		this.logging.debug("REquest to add preference :\n"+details.toString());
		CtxAttributeIdentifier preferenceCtxID = this.registry.getPPNPreference(details);
	
		
		
			PPNPrivacyPreferenceTreeModelBean bean = PrivacyPreferenceUtils.toPPNPrivacyPreferenceTreeModelBean(model);
		
		
		if (preferenceCtxID==null){
			this.logging.debug("Registry did not contain preference details. Proceeding to add as new preference");
			String name = this.registry.getNameForNewPreference(PrivacyPreferenceTypeConstants.PRIVACY_POLICY_NEGOTIATION);
			preferenceCtxID = this.storer.storeNewPreference(bean, name);
			this.registry.addPPNPreference(details, preferenceCtxID);
			this.ppnCtxIDtoModel.put(preferenceCtxID, model);
			this.storer.storeRegistry(registry);
			this.logging.debug("Preference didn't exist. Created new context attribute");

		}else{
			this.logging.debug("Registry contained preference details. Proceeding to update existing preference");
			this.storer.storeExisting(preferenceCtxID, bean);
			this.ppnCtxIDtoModel.put(preferenceCtxID,  model);
			this.logging.debug("Preference existed and updated.");

		}
		printCacheContentsOnScreen("After update");
		
	}
	

	public void addIDSPreference(IDSPreferenceDetailsBean details, IDSPrivacyPreferenceTreeModel model){
		printCacheContentsOnScreen("Before Update");
		this.logging.debug("REquest to add preference :\n"+details.toString());
		CtxAttributeIdentifier preferenceCtxID = this.registry.getIDSPreference(details);
		if (preferenceCtxID==null){
			this.logging.debug("Registry did not contain preference details. Proceeding to add as new preference");
			String name = this.registry.getNameForNewPreference(PrivacyPreferenceTypeConstants.IDENTITY_SELECTION);
			preferenceCtxID = this.storer.storeNewPreference(PrivacyPreferenceUtils.toIDSPreferenceTreeModelBean(model), name);
			if (preferenceCtxID!=null){
				this.registry.addIDSPreference(details, preferenceCtxID);
				this.idsCtxIDtoModel.put(preferenceCtxID, model);
				this.storer.storeRegistry(registry);
				this.logging.debug("Preference didn't exist. Created new context attribute");
			}else{
				this.logging.debug("Could not save IDS preference. Broker returned null ctx ID");
			}

		}else{
			this.logging.debug("Registry contained preference details. Proceeding to update existing preference");
			this.idsCtxIDtoModel.put(preferenceCtxID, model);
			this.storer.storeExisting(preferenceCtxID, PrivacyPreferenceUtils.toIDSPreferenceTreeModelBean(model));
			this.logging.debug("Preference existed and updated.");

		}
		printCacheContentsOnScreen("After Update");	
	}

	public void addDObfPreference(DObfPreferenceDetailsBean details, DObfPreferenceTreeModel model){
		printCacheContentsOnScreen("Before Update");
		this.logging.debug("REquest to add preference :\n"+details.toString());
		CtxAttributeIdentifier preferenceCtxID = this.registry.getDObfPreference(details);
		if (preferenceCtxID==null){
			this.logging.debug("Registry did not contain preference details. Proceeding to add as new preference");
			String name = this.registry.getNameForNewPreference(PrivacyPreferenceTypeConstants.DATA_OBFUSCATION);
			preferenceCtxID = this.storer.storeNewPreference(PrivacyPreferenceUtils.toDObfPrivacyPreferenceTreeModelBean(model), name);
			if (preferenceCtxID!=null){
				this.registry.addDObfPreference(details, preferenceCtxID);
				this.dobfCtxIDtoModel.put(preferenceCtxID, model);
				this.storer.storeRegistry(registry);
				this.logging.debug("Preference didn't exist. Created new context attribute");
			}else{
				this.logging.debug("Could not save IDS preference. Broker returned null ctx ID");
			}

		}else{
			this.logging.debug("Registry contained preference details. Proceeding to update existing preference");
			this.dobfCtxIDtoModel.put(preferenceCtxID, model);
			this.storer.storeExisting(preferenceCtxID, PrivacyPreferenceUtils.toDObfPrivacyPreferenceTreeModelBean(model));
			this.logging.debug("Preference existed and updated.");
		}
	}
		
	public void addAccCtrlPreference(AccessControlPreferenceDetailsBean details, AccessControlPreferenceTreeModel model){
		printCacheContentsOnScreen("Before Update");
		this.logging.debug("REquest to add preference :\n"+details.toString());
		CtxAttributeIdentifier preferenceCtxID = this.registry.getAccCtrlPreference(details);
		if (preferenceCtxID==null){
			this.logging.debug("Registry did not contain preference details. Proceeding to add as new preference");
			String name = this.registry.getNameForNewPreference(PrivacyPreferenceTypeConstants.ACCESS_CONTROL);
			preferenceCtxID = this.storer.storeNewPreference(PrivacyPreferenceUtils.toAccessControlPreferenceTreeModelBean(model), name);
			if (preferenceCtxID!=null){
				this.registry.addAccessCtrlPreference(details, preferenceCtxID);
				this.accCtrlCtxIDtoModel.put(preferenceCtxID, model);
				this.storer.storeRegistry(registry);
				this.logging.debug("Preference didn't exist. Created new context attribute");
			}else{
				this.logging.debug("Could not save IDS preference. Broker returned null ctx ID");
			}

		}else{
			this.logging.debug("Registry contained preference details. Proceeding to update existing preference");
			this.accCtrlCtxIDtoModel.put(preferenceCtxID, model);
			this.storer.storeExisting(preferenceCtxID, PrivacyPreferenceUtils.toAccessControlPreferenceTreeModelBean(model));
			this.logging.debug("Preference existed and updated.");
		}
	}
		
	public IPrivacyPreferenceTreeModel getPPNPreference(PPNPreferenceDetailsBean details){
		
		this.logging.debug("Request for preference: \n"+details.toString());
		printCacheContentsOnScreen("No update");
		CtxAttributeIdentifier preferenceCtxID = this.registry.getPPNPreference(details);
		if (preferenceCtxID!=null){
			if (this.ppnCtxIDtoModel.containsKey(preferenceCtxID)){
				this.logging.debug("Found preference. returning object");
				return this.ppnCtxIDtoModel.get(preferenceCtxID);
			}else{
				this.logging.debug("Cache doesn't have preference. Will return obj if found in DB");
				return this.retrievePPNPFromDB(preferenceCtxID);
			}
			
		}else{
			this.logging.debug("NOt found preference, returning null");
			return null;
		}
	}
	
	public IPrivacyPreferenceTreeModel getIDSPreference(IDSPreferenceDetailsBean details){
		
		this.logging.debug("Request for preference: \n"+details.toString());
		printCacheContentsOnScreen("No update");
		
		CtxAttributeIdentifier preferenceCtxID = this.registry.getIDSPreference(details);
		if (preferenceCtxID!=null){
			if (this.idsCtxIDtoModel.containsKey(preferenceCtxID)){
				this.logging.debug("Found preference in registry and cache. returning object");
				return this.idsCtxIDtoModel.get(preferenceCtxID);
			}else{
				this.logging.debug("Found preference in registry. Cache doesn't have preference. Will return obj if found in DB");
				return this.retrieveIDSPFromDB(preferenceCtxID);
			}
			
		}else{
			this.logging.debug("ERROR251 - Not found preference in registry, returning null");
			return null;
		}
	}
	
	private IPrivacyPreferenceTreeModel getDObfPreference(DObfPreferenceDetailsBean details){
		
		this.logging.debug("Request for preference: \n"+details.toString());
		printCacheContentsOnScreen("No update");
		
		CtxAttributeIdentifier preferenceCtxID = this.registry.getDObfPreference(details);
		if (preferenceCtxID!=null){
			if (this.dobfCtxIDtoModel.containsKey(preferenceCtxID)){
				this.logging.debug("Found preference in registry and cache. returning object");
				return this.dobfCtxIDtoModel.get(preferenceCtxID);
			}else{
				this.logging.debug("Found preference in registry. Cache doesn't have preference. Will return obj if found in DB");
				return this.retrieveDObfPFromDB(preferenceCtxID);
			}
			
		}else{
			this.logging.debug("ERROR251 - Not found preference in registry, returning null");
			return null;
		}
	}
	
	private IPrivacyPreferenceTreeModel getAccCtrlPreference(AccessControlPreferenceDetailsBean details){
	
		this.logging.debug("Request for preference: \n"+details.toString());
		printCacheContentsOnScreen("No update");
		
		CtxAttributeIdentifier preferenceCtxID = this.registry.getAccCtrlPreference(details);
		if (preferenceCtxID!=null){
			if (this.accCtrlCtxIDtoModel.containsKey(preferenceCtxID)){
				this.logging.debug("Found preference in registry and cache. returning object");
				return this.accCtrlCtxIDtoModel.get(preferenceCtxID);
			}else{
				this.logging.debug("Found preference in registry. Cache doesn't have preference. Will return obj if found in DB");
				return this.retrieveAccCtrlPFromDB(preferenceCtxID);
			}
			
		}else{
			this.logging.debug("ERROR251 - Not found preference in registry, returning null");
			return null;
		}
	}
	private IPrivacyPreferenceTreeModel retrievePPNPFromDB(CtxAttributeIdentifier preferenceCtxID){
		this.logging.debug("Request to retrieve preference from DB");
		IPrivacyPreferenceTreeModel model = this.retriever.retrievePreference(preferenceCtxID);
		if (model!=null){
			this.logging.debug("Preference found. returning");
			this.ppnCtxIDtoModel.put(preferenceCtxID, (PPNPrivacyPreferenceTreeModel) model);
			return model;
		}else{
			this.logging.debug("Preference not found. returning null");
			return null;
		}
	}
	private IPrivacyPreferenceTreeModel retrieveIDSPFromDB(CtxAttributeIdentifier preferenceCtxID){
		this.logging.debug("Request to retrieve preference from DB");
		IPrivacyPreferenceTreeModel model = this.retriever.retrievePreference(preferenceCtxID);
		if (model!=null){
			this.logging.debug("Preference found. returning");
			this.idsCtxIDtoModel.put(preferenceCtxID, (IDSPrivacyPreferenceTreeModel) model);
			return model;
		}else{
			this.logging.debug("Preference not found. returning null");
			return null;
		}
	}
	
	private IPrivacyPreferenceTreeModel retrieveDObfPFromDB(CtxAttributeIdentifier preferenceCtxID){
		this.logging.debug("Request to retrieve preference from DB");
		IPrivacyPreferenceTreeModel model = this.retriever.retrievePreference(preferenceCtxID);
		if (model!=null){
			this.logging.debug("Preference found. returning");
			this.dobfCtxIDtoModel.put(preferenceCtxID, (DObfPreferenceTreeModel) model);
			return model;
		}else{
			this.logging.debug("Preference not found. returning null");
			return null;
		}
	}
	
	private IPrivacyPreferenceTreeModel retrieveAccCtrlPFromDB(CtxAttributeIdentifier preferenceCtxID){
		this.logging.debug("Request to retrieve preference from DB");
		IPrivacyPreferenceTreeModel model = this.retriever.retrievePreference(preferenceCtxID);
		if (model!=null){
			this.logging.debug("Preference found. returning");
			this.accCtrlCtxIDtoModel.put(preferenceCtxID, (AccessControlPreferenceTreeModel) model);
			return model;
		}else{
			this.logging.debug("Preference not found. returning null");
			return null;
		}
	}
	private IPrivacyPreferenceTreeModel findPPNPreference(CtxAttributeIdentifier preferenceCtxID){
		if (this.ppnCtxIDtoModel.containsKey(preferenceCtxID)){
			return this.ppnCtxIDtoModel.get(preferenceCtxID);
		}else{
			return this.retrievePPNPFromDB(preferenceCtxID);
		}
	}
	private IPrivacyPreferenceTreeModel findIDSPreference(CtxAttributeIdentifier preferenceCtxID){
		if (this.idsCtxIDtoModel.containsKey(preferenceCtxID)){
			return this.idsCtxIDtoModel.get(preferenceCtxID);
		}else{
			return this.retrieveIDSPFromDB(preferenceCtxID);
		}
	}
	
	private IPrivacyPreferenceTreeModel findDObfPreference(CtxAttributeIdentifier preferenceCtxID){
		if (this.dobfCtxIDtoModel.containsKey(preferenceCtxID)){
			return this.dobfCtxIDtoModel.get(preferenceCtxID);
		}else{
			return this.retrieveDObfPFromDB(preferenceCtxID);
		}
	}
	
	private IPrivacyPreferenceTreeModel findAccCtrlPreference(CtxAttributeIdentifier preferenceCtxID){
		if (this.accCtrlCtxIDtoModel.containsKey(preferenceCtxID)){
			return this.accCtrlCtxIDtoModel.get(preferenceCtxID);
		}else{
			return this.retrieveAccCtrlPFromDB(preferenceCtxID);
		}
	}
/*	public IPrivacyPreferenceTreeModel getPPNPreference(String contextType, CtxAttributeIdentifier affectedCtxID, Requestor requestor){
		PPNPreferenceDetails details = new PPNPreferenceDetails(contextType);
		details.setAffectedDataId(affectedCtxID);
		details.setRequestor(requestor);
		return this.getPPNPreference(details);
	}*/
	
	public IPrivacyPreferenceTreeModel getIDSPreference(IIdentity affectedIdentity, Requestor requestor){
		IDSPreferenceDetailsBean details = new IDSPreferenceDetailsBean();
		details.setAffectedIdentity(affectedIdentity.getJid());
		details.setRequestor(RequestorUtils.toRequestorBean(requestor));
		return this.getIDSPreference(details);
	}
	
	
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType){
		this.logging.debug("Request for preferences for context type: \n"+contextType);
		List<IPrivacyPreferenceTreeModel> modelList = new ArrayList<IPrivacyPreferenceTreeModel>();
		List<CtxAttributeIdentifier> preferenceCtxIDs = this.registry.getPPNPreferences(contextType);
		
		for (CtxAttributeIdentifier ctxID : preferenceCtxIDs){
			IPrivacyPreferenceTreeModel model = this.findPPNPreference(ctxID);
			if (model!=null){
				modelList.add(model);
			}
		}
		
		this.logging.debug("Found "+modelList.size()+" preferences for contextType: "+contextType);
		return modelList;
	}
	
	public List<IPrivacyPreferenceTreeModel> getIDSPreferences(IIdentity affectedDPI){
		this.logging.debug("Request for IDS preferences for DPI: \n"+affectedDPI.toString());
		List<IPrivacyPreferenceTreeModel> modelList = new ArrayList<IPrivacyPreferenceTreeModel>();
		List<CtxAttributeIdentifier> preferenceCtxIDs = this.registry.getIDSPreferences(affectedDPI);
		
		for (CtxAttributeIdentifier ctxID : preferenceCtxIDs){
			IPrivacyPreferenceTreeModel model = this.findIDSPreference(ctxID);
			if (model!=null){
				modelList.add(model);
			}
		}
		
		this.logging.debug("Found "+modelList.size()+" IDS preferences for dpi: "+affectedDPI.toString());
		return modelList;
	}
	
	
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType, CtxAttributeIdentifier affectedCtxID){
		this.logging.debug("Request for preferences for context type: \n"+contextType+" and affectedCtxID: "+affectedCtxID.toString());
		List<IPrivacyPreferenceTreeModel> modelList = new ArrayList<IPrivacyPreferenceTreeModel>();
		List<CtxAttributeIdentifier> preferenceCtxIDs = this.registry.getPPNPreferences(contextType, affectedCtxID);
		
		for (CtxAttributeIdentifier ctxID : preferenceCtxIDs){
			IPrivacyPreferenceTreeModel model = this.findPPNPreference(ctxID);
			if (model!=null){
				modelList.add(model);
			}
		}
		
		this.logging.debug("Found "+modelList.size()+" preferences for contextType: "+contextType+" and affectedCtxID: "+affectedCtxID.toString());

		return modelList;
	}
	
/*	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType, CtxAttributeIdentifier affectedCtxID, Requestor requestor){
		this.logging.debug("Request for preferences for context type: \n"+contextType+" and affectedCtxID: "+affectedCtxID.toString());
		List<IPrivacyPreferenceTreeModel> modelList = new ArrayList<IPrivacyPreferenceTreeModel>();
		List<CtxAttributeIdentifier> preferenceCtxIDs = this.registry.getPPNPreferences(contextType, affectedCtxID, requestor);
		
		for (CtxAttributeIdentifier ctxID : preferenceCtxIDs){
			IPrivacyPreferenceTreeModel model = this.findPPNPreference(ctxID);
			if (model!=null){
				modelList.add(model);
			}
		}
		
		this.logging.debug("Found "+modelList.size()+" preferences for contextType: "+contextType+" and affectedCtxID: "+affectedCtxID.toString());

		return modelList;
	}*/
	
	
	public List<IPrivacyPreferenceTreeModel> getIDSPreferences(IIdentity affectedDPI, Requestor requestor){
		this.logging.debug("Request for IDS preferences for dpi: \n"+affectedDPI.toString()+", requestor: "+requestor.toString());
		List<IPrivacyPreferenceTreeModel> modelList = new ArrayList<IPrivacyPreferenceTreeModel>();
		List<CtxAttributeIdentifier> preferenceCtxIDs = this.registry.getIDSPreferences(affectedDPI, requestor);
		
		for (CtxAttributeIdentifier ctxID : preferenceCtxIDs){
			IPrivacyPreferenceTreeModel model = this.findIDSPreference(ctxID);
			if (model!=null){
				modelList.add(model);
			}
		}
		
		this.logging.debug("Found "+modelList.size()+" IDS preferences for dpi: \n"+affectedDPI.toString()+", providerDPI: "+requestor.toString());

		return modelList;
	}
	
	
	
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType, Requestor requestor){
		this.logging.debug("Request for preferences for context type: \n"+contextType+" and requestor: "+requestor.toString());

		List<IPrivacyPreferenceTreeModel> modelList = new ArrayList<IPrivacyPreferenceTreeModel>();
		List<CtxAttributeIdentifier> preferenceCtxIDs = this.registry.getPPNPreferences(contextType, requestor);
		
		for (CtxAttributeIdentifier ctxID : preferenceCtxIDs){
			IPrivacyPreferenceTreeModel model = this.findPPNPreference(ctxID);
			if (model!=null){
				modelList.add(model);
			}
		}
		this.logging.debug("Found "+modelList.size()+" preferences for contextType: "+contextType+" and requestor: "+requestor.toString());

		return modelList;
	}
	
	/*public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType, IIdentity dpi, ServiceResourceIdentifier serviceID){
		this.logging.debug("Request for preferences for context type: \n"+contextType+" and requestorDPI: "+dpi.toString());

		List<IPrivacyPreferenceTreeModel> modelList = new ArrayList<IPrivacyPreferenceTreeModel>();
		List<CtxAttributeIdentifier> preferenceCtxIDs = this.registry.getPPNPreferences(contextType, requestor);
		
		for (CtxAttributeIdentifier ctxID : preferenceCtxIDs){
			IPrivacyPreferenceTreeModel model = this.findPPNPreference(ctxID);
			if (model!=null){
				modelList.add(model);
			}
		}
		this.logging.debug("Found "+modelList.size()+" preferences for contextType: "+contextType+" and requestorDPI: "+dpi.toString());

		return modelList;
	}*/
	public void removePPNPreference(PPNPreferenceDetailsBean details){
		this.logging.debug("Request to remove preference : \n"+details.toString());
		CtxAttributeIdentifier preferenceCtxID = this.registry.getPPNPreference(details);
		if (preferenceCtxID!=null){
			this.storer.deletePreference(preferenceCtxID);
			if (this.ppnCtxIDtoModel.containsKey(preferenceCtxID)){
				
				logging.debug("Deleting preference details:"+details.toString());
				this.ppnCtxIDtoModel.remove(preferenceCtxID);
				this.registry.removePPNPreference(details);
				this.storer.storeRegistry(registry);
			}
		}else{
			this.logging.debug("Registry did not contain preference details: "+details.toString());
		}
	}
	
	public void removeIDSPreference(IDSPreferenceDetailsBean details){
		this.logging.debug("Request to remove preference : \n"+details.toString());
		CtxAttributeIdentifier preferenceCtxID = this.registry.getIDSPreference(details);
		if (preferenceCtxID!=null){
			this.storer.deletePreference(preferenceCtxID);
			if (this.idsCtxIDtoModel.containsKey(preferenceCtxID)){
				this.idsCtxIDtoModel.remove(preferenceCtxID);
				this.registry.removeIDSPreference(details);
				this.storer.storeRegistry(registry);
			}
		}else{
			this.logging.debug("Preference Details not found in registry: "+details.toString());
		}
	}
	
	public void removeDObfPreference(DObfPreferenceDetailsBean details){
		this.logging.debug("Request to remove preference : \n"+details.toString());
		CtxAttributeIdentifier preferenceCtxID = this.registry.getDObfPreference(details);
		if (preferenceCtxID!=null){
			this.storer.deletePreference(preferenceCtxID);
			if (this.dobfCtxIDtoModel.containsKey(preferenceCtxID)){
				this.dobfCtxIDtoModel.remove(preferenceCtxID);
				this.registry.removeDObfPreference(details);
				this.storer.storeRegistry(registry);
			}
		}else{
			this.logging.debug("Preference Details not found in registry: "+details.toString());
		}
	}
	
	public void removeAccCtrlPreference(AccessControlPreferenceDetailsBean details){
		this.logging.debug("Request to remove preference : \n"+details.toString());
		CtxAttributeIdentifier preferenceCtxID = this.registry.getAccCtrlPreference(details);
		if (preferenceCtxID!=null){
			this.storer.deletePreference(preferenceCtxID);
			if (this.accCtrlCtxIDtoModel.containsKey(preferenceCtxID)){
				this.accCtrlCtxIDtoModel.remove(preferenceCtxID);
				this.registry.removeAccCtrlPreference(details);
				this.storer.storeRegistry(registry);
			}
		}else{
			this.logging.debug("Preference Details not found in registry: "+details.toString());
		}
	}
	private void printCacheContentsOnScreen(String string){
	
		this.logging.debug("*********CACHE CONTENTS START "+string+"**************");
		this.logging.debug(this.registry.toString());
		this.logging.debug("*********CACHE CONTENTS END "+string+"**************");
	}

	
	public List<PPNPreferenceDetailsBean> getPPNPreferenceDetails(){
		return this.registry.getPPNPreferenceDetails();
	}
	
	public List<IDSPreferenceDetailsBean> getIDSPreferenceDetails(){
		return this.registry.getIDSPreferenceDetails();	
	}
	/*
	 * OLD METHODS
	 */




	public List<DObfPreferenceDetailsBean> getDObfPreferences() {
		return this.registry.getDObfPreferenceDetails();
	}



}


