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
package org.societies.privacytrust.privacyprotection.privacypolicy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestPolicyUtils;
import org.societies.privacytrust.privacyprotection.privacypolicy.registry.PrivacyPolicyRegistry;

/**
 * @author Elizabeth
 *
 */
public class PrivacyPolicyRegistryManager {

	private PrivacyPolicyRegistry policyRegistry;
	private ICtxBroker ctxBroker;

	private Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
	private IIdentity myPublicDPI;
	private IIdentityManager idm;

	public PrivacyPolicyRegistryManager(ICtxBroker ctxBroker, IIdentityManager idm){
		this.idm = idm;
		this.ctxBroker = ctxBroker;
		this.loadPolicies();
	}



	/**
	 * method to add a policy to the registry
	 * @param requestor	the service id of the service for which the policy is for
	 * @param policy	the policy document
	 */
	public void addPolicy (Requestor requestor, RequestPolicy policy){
		this.log("Request to add policy for : "+requestor.toString());
		if (this.policyRegistry==null){
			LOG.debug("Registry empty: loading privacy policies");
			this.loadPolicies();
		}

		// Global store
		CtxIdentifier id = this.storePolicyToDB(requestor, policy);
		this.policyRegistry.addPolicy(requestor,id);
		this.storePolicies(); 
		LOG.info("*** addPolicy Stored");
		//this.storePolicyToFile(policy);		
	}

	/**
	 * method to retrieve the policy of a given service
	 * @param requestor	the serviceid of the service for which the policy is for
	 * @return	the policy document for that service 
	 * @throws PrivacyException 
	 */
	public RequestPolicy getPolicy(Requestor requestor) throws PrivacyException{
		RequestPolicy policy = null;
		// -- Loading
		if (null == policyRegistry) {
			LOG.debug("Registry empty: loading privacy policies");
			this.loadPolicies();
		}
		
		// -- Verification
		if (null == requestor) {
			LOG.error("Requestor obj is null");
			return policy;
		}
		if (null == idm) {
			LOG.error("Identity manager obj is null");
			return policy;
		}

		// -- Retrieve privacy policy
		// Retrieve Context Id
		CtxIdentifier id = this.policyRegistry.getPolicyStorageID(requestor);
		if (null == id){
			LOG.warn("Requestor: "+requestor.toString()+" has not provided a privacy policy document");
			return policy;
		}
		// Retrieve in context
		try {
			CtxAttribute ctxAttr = (CtxAttribute) ctxBroker.retrieve(id).get();
			if (null == ctxAttr) {
				LOG.error("CtxAttr obj is null");
				return policy;
			}
			org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy tmpPolicy = (org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy) SerialisationHelper.deserialise(ctxAttr.getBinaryValue(), this.getClass().getClassLoader());
			if (null == tmpPolicy) {
				LOG.error("Can't deserialize the retrieved privacy policy: "+ctxAttr.getBinaryValue().toString());
				return policy;
			}
			policy = RequestPolicyUtils.toRequestPolicy(tmpPolicy, idm);
		} catch (Exception e) {
			throw new PrivacyException("Can't retrieve the privacy policy", e);
		}
		return policy;
	}
	/**
	 * method to delete the policy of a given service
	 * @param requestor	the serviceid of the service for which the policy is for
	 * @return	success of the operation 
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public boolean deletePolicy(Requestor requestor) throws InterruptedException, ExecutionException, CtxException{
		// -- No privacy policy in the registry
		if (null == policyRegistry){
			return true;
		}
		// -- Retrieve privacy policy id
		CtxIdentifier privacyPolicyId = policyRegistry.getPolicyStorageID(requestor);
		// No privacy policy
		if (null == privacyPolicyId) {
			return true;
		}
		// Remove it
		ctxBroker.remove(privacyPolicyId).get();
		policyRegistry.removePolicy(requestor);
		return true;
	}

	private CtxIdentifier storePolicyToDB(Requestor requestor, RequestPolicy policy){
		try {
			String name = "";
			if (requestor instanceof RequestorService){
				name = "policyOf"+((RequestorService) requestor).hashCode();
			}else if (requestor instanceof RequestorCis){
				name = "policyOf"+((RequestorCis) requestor).hashCode();
			}
			//TODO: The name might cause an error. We might need to provide different names for storing the policies as attributes in DB
			List<CtxIdentifier> ctxIDs = ctxBroker.lookup(CtxModelType.ATTRIBUTE, name).get();
			if (ctxIDs.size()==0){
				List<CtxIdentifier> entityIDs = ctxBroker.lookup(CtxModelType.ENTITY, CtxEntityTypes.PRIVACY_POLICY).get();
				if (entityIDs.size()==0){
					CtxEntity person = ctxBroker.retrieveCssOperator().get();
					if (person==null){
						this.log("ERROR in DB. Operator Entity doesn't exist");
						return null;
					}
					Set<CtxAssociationIdentifier> assocIDs = person.getAssociations(CtxAssociationTypes.HAS_PRIVACY_POLICIES);
					CtxAssociation assoc;
					if (assocIDs.size()==0){
						assoc = ctxBroker.createAssociation(CtxAssociationTypes.HAS_PRIVACY_POLICIES).get();
						assoc.setParentEntity(person.getId());
					}else{
						assoc = (CtxAssociation) ctxBroker.retrieve(assocIDs.iterator().next()).get();
					}
					CtxEntity policyEntity = ctxBroker.createEntity(CtxEntityTypes.PRIVACY_POLICY).get();
					assoc.addChildEntity(policyEntity.getId());
					ctxBroker.update(assoc);
					entityIDs.add(policyEntity.getId());

				}
				CtxAttribute ctxAttr = ctxBroker.createAttribute((CtxEntityIdentifier) entityIDs.get(0), name).get();
				ctxAttr.setBinaryValue(SerialisationHelper.serialise(RequestPolicyUtils.toRequestPolicyBean(policy)));
				ctxBroker.update(ctxAttr);
				this.log("Created attribute: "+ctxAttr.getType());
				return ctxAttr.getId();


			}else{
				CtxAttribute ctxAttr = (CtxAttribute) ctxBroker.retrieve(ctxIDs.get(0)).get();
				ctxAttr.setBinaryValue(SerialisationHelper.serialise(RequestPolicyUtils.toRequestPolicyBean(policy)));
				ctxBroker.update(ctxAttr);
				this.log("Updated attribute:"+ctxAttr.getType());
				return ctxAttr.getId();
			}
		} catch (CtxException e) {
			e.printStackTrace();
			return null;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * method to load the policies from context
	 */
	private void loadPolicies(){
		try {
			List<CtxIdentifier> attrList = ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.PRIVACY_POLICY_REGISTRY).get();
			if (null != attrList && !attrList.isEmpty()) {
					CtxIdentifier identifier = attrList.get(0);
					CtxAttribute attr = (CtxAttribute) ctxBroker.retrieve(identifier).get();
					this.policyRegistry = (PrivacyPolicyRegistry) SerialisationHelper.deserialise(attr.getBinaryValue(), this.getClass().getClassLoader());
			}
		} catch (Exception e) {
			LOG.warn("Error when retrieving privacy policies from context DB. Use empty registry.", e);
		}
		finally {
			if (null == policyRegistry) {
				this.policyRegistry = new PrivacyPolicyRegistry();
			}
		}

	}
	/**
	 * method to load the policies from the filesystem
	 *
	private void loadPoliciesFromFile(){
		File dir = new File("./servicePrivacyPolicies");
		if (dir.isDirectory()){
			File[] files = dir.listFiles(new Filter("xml"));
			XMLPolicyReader reader = new XMLPolicyReader(ctxBroker, idm);
			for (int i=0; i<files.length; i++){
				RequestPolicy request = reader.readPolicyFromFile(files[i]);
				if (request!=null){
					this.addPolicy(request.getRequestor(), request);

				}
			}

		}else{
			this.log("Directory: "+dir.toString()+" doesn't exist");
		}

	}*/

	/**
	 * method to set the new public dpi in the registry objects
	 */
	/*private void changeMyPublicDPIinMyServicePolicies(){
		if (this.policyRegistry!=null){
			this.policyRegistry.setPublicDPIinServiceID(this.myPublicDPI);
		}
	}*/

	/**
	 * method to store the policies currently in the registry in context
	 */
	private void storePolicies(){
		try {
			List<CtxIdentifier> attrList = ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.PRIVACY_POLICY_REGISTRY).get();
			if (attrList.size()>0){
				CtxIdentifier identifier = attrList.get(0);
				CtxAttribute attr = (CtxAttribute) ctxBroker.retrieve(identifier).get();
				attr.setBinaryValue(SerialisationHelper.serialise(this.policyRegistry));
				ctxBroker.update(attr);
				this.log("Stored service privacy policies");
			}else{
				CtxEntity operator = ctxBroker.retrieveCssOperator().get();
				CtxAttribute attr = ctxBroker.createAttribute(operator.getId(), CtxAttributeTypes.PRIVACY_POLICY_REGISTRY).get();
				attr.setBinaryValue(SerialisationHelper.serialise(this.policyRegistry));
				ctxBroker.update(attr);
				this.log("Created new Attribute: "+CtxAttributeTypes.PRIVACY_POLICY_REGISTRY+" and stored the service privacy policies");
			}
		} catch (CtxException e) {
			this.log("Error storing service privacy policies");
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	private void log(String message){
		this.LOG.info(message);
	}

	private void storePolicyToFile(RequestPolicy policy){
		try {
			String requestorName = "";
			Requestor requestor = policy.getRequestor();
			if (requestor instanceof RequestorService){
				requestorName = ((RequestorService) requestor).getRequestorServiceId().getIdentifier().toString();
			}else if (requestor instanceof RequestorCis){
				requestorName = ((RequestorCis) requestor).getCisRequestorId().getJid();
			}
			File directory = new File("./servicePrivacyPolicies/");
			if (!directory.exists()){
				boolean createdDir = directory.mkdir();
				this.LOG.debug("Created Directory "+directory.getCanonicalPath());
			}else{
				this.LOG.debug("Directory :"+directory.getCanonicalPath()+" already exists");
			}
			File file = new File("./servicePrivacyPolicies/"+requestorName+".xml");
			FileWriter fWriter = new FileWriter(file);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			BufferedWriter out = new BufferedWriter(bWriter);
			out.write(policy.toXMLString());
			out.close();
			this.LOG.debug("Stored privacy policy to file");
		} catch (IOException ioe) {
			log("Attempt to write service privacy policy to filesystem failed");
			ioe.printStackTrace();
		}
	}

	/**
	 * class that allows a filesystem directory to be filtered by a specific extension
	 * @author Elizabeth
	 *
	 */
	private class Filter implements FilenameFilter{

		protected String pattern;
		public Filter (String str) {
			pattern = str;
		}

		/* (non-Javadoc)
		 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
		 */
		@Override
		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith(pattern.toLowerCase());
		}

	}
}

