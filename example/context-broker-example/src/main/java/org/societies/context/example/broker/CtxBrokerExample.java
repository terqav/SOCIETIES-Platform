/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druÅ¾be in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÃ‡ÃƒO, SA (PTIN), IBM Corp., 
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
package org.societies.context.example.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.*;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.schema.activity.MarshaledActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * This class provides examples for using the internal Context Broker in OSGi. 
 */
@Service
public class CtxBrokerExample implements Subscriber{

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CtxBrokerExample.class);

	/** The Internal Context Broker service reference. */
	private ICtxBroker internalCtxBroker;
	private ICommManager commMgrService;
	private ICisManager cisManager;
	//private IActivityFeed activityFeed;
	private PubsubClient pubsubClient;

	private IIdentity cisID;
	private IIdentity cssOwnerId;
	private IIdentity cssID1; 
	//private IIdentity cssID2;
	//private IIdentity cssID3;

	private CommunityCtxEntity communityEntity;
	private IndividualCtxEntity indiEnt1;
	private IndividualCtxEntity indiEnt2;
	private IndividualCtxEntity indiEnt3;

	private ICisOwned cisOwned  = null;

	private INetworkNode cssNodeId;
	
	
	//private ICSSLocalManager cssManager;
	
	String cssPassword = "password.societies.local";


	private CtxEntityIdentifier ctxEntityIdentifier = null;
	private CtxIdentifier ctxAttributeStringIdentifier = null;
	private CtxIdentifier ctxAttributeBinaryIdentifier = null;

	@Autowired(required=true)
	public CtxBrokerExample(ICtxBroker internalCtxBroker, ICommManager commMgr, ICisManager cisManager,PubsubClient pubsubClient) throws InvalidFormatException {

		LOG.info("*** CtxBrokerExample instantiated "+this.internalCtxBroker);

		this.internalCtxBroker = internalCtxBroker;
		LOG.info("*** CtxBroker service "+this.internalCtxBroker);

		this.commMgrService = commMgr;
		LOG.info("*** commMgrService instantiated "+this.commMgrService);

		this.cisManager = cisManager;
		LOG.info("*** cisManager instantiated "+this.cisManager);

		this.pubsubClient = pubsubClient;
		LOG.info("*** pubsubClient instantiated "+this.cisManager);

		//this.cssManager = cssManager;
		//LOG.info("*** cssManager instantiated "+this.cssManager);
		
		this.cssNodeId = commMgr.getIdManager().getThisNetworkNode();
		LOG.info("*** cssNodeId = " + this.cssNodeId);
		
		final String cssOwnerStr = this.cssNodeId.getBareJid();
		LOG.info( "cssOwnerStr "+ cssOwnerStr);
		this.cssOwnerId = commMgr.getIdManager().fromJid(cssOwnerStr);
		LOG.info("*** cssOwnerId = " + this.cssOwnerId.toString());
		LOG.info("  cssOwnerId id type: "+this.cssOwnerId.getType());

		this.cssID1 =  commMgr.getIdManager().fromJid("jane.societies.local");

		LOG.info( "this.cssID1 "+ this.cssID1);
		LOG.info( "this.cssID1.getType() "+ this.cssID1.getType());

		Hashtable<String,MembershipCriteria> cisCriteria = new Hashtable<String,MembershipCriteria>();

		try {
			// the cis creation will also create a community ctx Entity
			cisOwned = cisManager.createCis("testCIS", "cisType", cisCriteria, "nice CIS").get();
			LOG.info("*** cisOwned " +cisOwned);
			LOG.info("*** cisOwned.getCisId() " +cisOwned.getCisId());
			String cisIDString  = cisOwned.getCisId();

			this.cisID = commMgr.getIdManager().fromJid(cisIDString);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOG.info("*** cisManager this.cisID " +this.cisID.toString());
		LOG.info("*** cisManager this.cisID type " +this.cisID.getType());
		
		LOG.info("*** Starting  individual context examples...");
		this.retrieveIndividualEntity();
		this.retrieveCssNode();
		this.createContext();
		this.registerForContextChanges();
		this.retrieveContext();
		this.lookupContext();
		this.simpleCtxHistoryTest();
		this.tuplesCtxHistoryTest();
		//this.triggerInferenceTest();
	
		// community context tests
		LOG.info("*** Starting community context examples...");
		retrieveCommunityEntityBasedOnCisID();
		createCommunityEntAssociation();
		// create entity and association refering to community
		
		//this.createIndividualEntities();
		// includes context bond tests
	//	this.populateCommunityEntity();
		
	//	this.retrieveCommunityEntityBasedOnCisID();
	///	this.lookupCommunityEntAttributes();
		
		
	}


	
	private void createCommunityEntAssociation(){
		
		System.out.println(" createCommunityEntAssociation ");
		
		try {
			LOG.info("cisID "+cisID.getJid());
			
			this.retrieveCommunityEntityBasedOnCisID();
			
			CtxEntity entity = this.internalCtxBroker.createEntity(cisID, CtxEntityTypes.DEVICE).get();
			System.out.println(" CtxEntity refering to CIS created: "+entity.getId());
			
			CtxAssociation assoc = this.internalCtxBroker.createAssociation(cisID, CtxAssociationTypes.HAS_PARAMETERS).get();
			System.out.println(" CtxAssociation refering to CIS created: "+assoc.getId());
			
			assoc.addChildEntity(entity.getId());

			CtxEntityIdentifier ctxCommunityEntityIdentifier = this.internalCtxBroker.retrieveCommunityEntityId(this.cisID).get();
			LOG.info("ctxCommunityEntity  : " + ctxCommunityEntityIdentifier.toString());
			assoc.setParentEntity(ctxCommunityEntityIdentifier);
			
			CtxAssociation assocUpdated = (CtxAssociation) this.internalCtxBroker.update(assoc).get();
			System.out.println(" CtxAssociation refering to CIS child entities: "+assocUpdated.getChildEntities());
			LOG.info(" CtxAssociation refering to CIS child entities: "+assocUpdated.getChildEntities());
			//final IIdentity communityEntityId = this.commMgrService.getIdManager().fromJid(communityEntity.getOwnerId());

			
			
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
				
	}
	
	
	
	
	private void lookupCommunityEntAttributes(){

		CtxEntityIdentifier ctxCommunityEntityIdentifier;
		try {
			ctxCommunityEntityIdentifier = this.internalCtxBroker.retrieveCommunityEntityId(this.cisID).get();
			LOG.info("ctxCommunityEntity  : " + ctxCommunityEntityIdentifier.toString());

			List<CtxIdentifier> communityAttrIDList = this.internalCtxBroker.lookup(ctxCommunityEntityIdentifier, CtxModelType.ATTRIBUTE, "ctxCommunityAttribute").get();
			LOG.info("communityAttrIDList"+ communityAttrIDList);		

			if(communityAttrIDList.size()>0 ){
				CtxIdentifier communityAttrID = communityAttrIDList.get(0);
				LOG.info("communityAttrIDList"+ communityAttrID);
				CtxAttribute communityAttr = (CtxAttribute) this.internalCtxBroker.retrieve(communityAttrID).get();
				LOG.info("communityAttr value:"+communityAttr.getStringValue());

			}

			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	private void retrieveCommunityEntityBasedOnCisID(){
		try {
			CtxEntityIdentifier ctxCommunityEntityIdentifier = this.internalCtxBroker.retrieveCommunityEntityId(this.cisID).get();
			LOG.info("ctxCommunityEntity  : " + ctxCommunityEntityIdentifier.toString());

			CtxEntity communityEntity = (CtxEntity) this.internalCtxBroker.retrieve(ctxCommunityEntityIdentifier).get();

			final IIdentity communityEntityId = this.commMgrService.getIdManager().fromJid(communityEntity.getOwnerId());

			if (IdentityType.CIS.equals(communityEntityId.getType())){
				LOG.info("entity retrieved is a community entity with jid "+ communityEntityId.toString());

				//TODO add communityEntity.getAttributes(); code

			}

		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	private void populateCommunityEntity(){
		LOG.info("*** populateCommunityEntity");

		
		try {
			CtxEntityIdentifier ctxCommunityEntityIdentifier = this.internalCtxBroker.retrieveCommunityEntityId(this.cisID).get();
			
			this.communityEntity = (CommunityCtxEntity) this.internalCtxBroker.retrieve(ctxCommunityEntityIdentifier).get();
			
			Set<CtxAssociationIdentifier> assocIDSet = this.communityEntity.getAssociations(CtxAssociationTypes.HAS_MEMBERS);
			CtxAssociation assocHasMembers = null;
			if(assocIDSet.size()>0){
				List<CtxAssociationIdentifier> assocIdList = new ArrayList<CtxAssociationIdentifier>(assocIDSet);
				assocHasMembers = (CtxAssociation) this.internalCtxBroker.retrieve(assocIdList.get(0)).get();
			}
			
			LOG.info("community members (before adding new members) "+ assocHasMembers.getChildEntities());
			
			//add new members
			assocHasMembers.addChildEntity(this.indiEnt1.getId());
			assocHasMembers.addChildEntity(this.indiEnt2.getId());
			assocHasMembers.addChildEntity(this.indiEnt3.getId());
			
			assocHasMembers = (CtxAssociation) this.internalCtxBroker.update(assocHasMembers).get();
			
			LOG.info("community members (after adding new members) "+ assocHasMembers.getChildEntities());
			
			
			LOG.info(" BEFORE UPDATE communityEnt.getID():  " +this.communityEntity.getId());
			LOG.info(" BEFORE UPDATE communityEnt.getMembers():  " +this.communityEntity.getMembers());


			//	create and add bonds
			CtxAttributeBond attributeLocationBond = new CtxAttributeBond(CtxAttributeTypes.LOCATION_SYMBOLIC, CtxBondOriginType.MANUALLY_SET);
			attributeLocationBond.setMinValue("Athens_Greece");
			attributeLocationBond.setMaxValue("Athens_Greece");
			attributeLocationBond.setValueType(CtxAttributeValueType.STRING);
			LOG.info("locationBond created : " + attributeLocationBond.toString());
			CtxAttributeBond attributeAgeBond = new CtxAttributeBond(CtxAttributeTypes.WEIGHT, CtxBondOriginType.MANUALLY_SET);

			attributeLocationBond.setValueType(CtxAttributeValueType.INTEGER);
			attributeAgeBond.setMinValue(new Integer(18));
			attributeAgeBond.setMinValue(new Integer(20));

			this.communityEntity.addBond(attributeLocationBond);
			this.communityEntity.addBond(attributeAgeBond);


			this.internalCtxBroker.update(this.communityEntity).get();

			//CommunityCtxEntity communityEnt = (CommunityCtxEntity) this.internalCtxBroker.retrieve(this.communityEntity.getId()).get();
			//LOG.info(" AFTER UPDATE communityEnt id :  " +communityEnt.getId());
			//LOG.info(" AFTER UPDATE communityEnt.getMembers():  " +communityEnt.getMembers());

			Set<CtxBond> retrievedBonds = this.communityEntity.getBonds();
			LOG.info(" retrievedBonds " +retrievedBonds);
			for(CtxBond bond : retrievedBonds){
				LOG.info(" bond type : " +bond.getType());
				LOG.info(" bond modelType : " +bond.getModelType());
				LOG.info(" bond origin type : " +bond.getOriginType());
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private void createIndividualEntities(){
		LOG.info("*** createIndividualEntities ... to be added in a community");
		try {
			this.indiEnt1 = this.internalCtxBroker.createIndividualEntity(this.cssID1, CtxEntityTypes.PERSON).get();
			//this.indiEnt2 = this.internalCtxBroker.createIndividualEntity(this.cssID2, CtxEntityTypes.PERSON).get();
			//this.indiEnt3 = this.internalCtxBroker.createIndividualEntity(this.cssID3, CtxEntityTypes.PERSON).get();

			LOG.info("individual entity 1 "+this.indiEnt1);		
			LOG.info("individual entity 2 "+this.indiEnt2);	
			LOG.info("individual entity 3 "+this.indiEnt3);


			CtxAttribute individualAttr1 = this.internalCtxBroker.createAttribute(this.indiEnt1.getId() , CtxAttributeTypes.LOCATION_SYMBOLIC).get();
			CtxAttribute individualAttr2 = this.internalCtxBroker.createAttribute(this.indiEnt2.getId() , CtxAttributeTypes.LOCATION_SYMBOLIC).get();
			CtxAttribute individualAttr3 = this.internalCtxBroker.createAttribute(this.indiEnt3.getId() , CtxAttributeTypes.LOCATION_SYMBOLIC).get();

			individualAttr1.setStringValue("Athens_Greece");
			individualAttr2.setStringValue("Athens_Greece");
			individualAttr3.setStringValue("Athens_Greece");

			this.internalCtxBroker.update(individualAttr1);
			this.internalCtxBroker.update(individualAttr2);
			this.internalCtxBroker.update(individualAttr3);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

/*
	private void createCommunityEntity(){
		LOG.info("*** createCommunityContext");

		// create community ctx Entity
		try {
			LOG.info("this.cisID: "+ this.cisID);
			this.communityEntity = this.internalCtxBroker.createCommunityEntity(this.cisID).get();

			LOG.info("communityEntity: "+ communityEntity.getId());
			LOG.info("communityEntity type : "+ communityEntity.getType());

			CtxAttribute communityAttr = this.internalCtxBroker.createAttribute(communityEntity.getId(), "ctxCommunityAttribute").get();
			LOG.info("communityAttribute id " +communityAttr.getId() );
			LOG.info("communityAttribute owner id " +communityAttr.getOwnerId() );
			LOG.info("communityAttribute owner type " +communityAttr.getType() );

			communityAttr.setStringValue("communityValue");
			communityAttr = (CtxAttribute) this.internalCtxBroker.update(communityAttr).get();

			CtxAttribute retrievedCommunityAttr = (CtxAttribute) this.internalCtxBroker.retrieve(communityAttr.getId()).get();
			LOG.info("retrievedCommunityAttr id " +retrievedCommunityAttr.getId());
			LOG.info("retrievedCommunityAttr get string value " +retrievedCommunityAttr.getStringValue());

		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

*/



	private void retrieveIndividualEntity() {

		LOG.info("*** retrieveIndividualEntity");

		try {
			final IndividualCtxEntity operator = this.internalCtxBroker.retrieveIndividualEntity(this.cssOwnerId).get();
			LOG.info("*** CSS owner context entity id: " + operator.getId());

			CtxAttribute books = this.internalCtxBroker.createAttribute(operator.getId(), CtxAttributeTypes.BOOKS).get();
			books.setStringValue("Miserables");

			this.internalCtxBroker.update(books);

			final IndividualCtxEntity operator2 = this.internalCtxBroker.retrieveIndividualEntity(this.cssOwnerId).get();

			Set<CtxAttribute> attributesBooks = operator2.getAttributes(CtxAttributeTypes.BOOKS);
			if(attributesBooks.size()>0){
				for(CtxAttribute ctxAttr : attributesBooks){
					LOG.info("CtxAttribute books id:"+ctxAttr.getId() + "value"+ctxAttr.getStringValue() ) ;
				}	
			}

			Set<CtxAttribute> attributesAll = operator2.getAttributes();
			if(attributesAll.size()>0){
				for(CtxAttribute ctxAttr : attributesAll){
					LOG.info("ALL CtxAttribute  id:"+ctxAttr.getId()) ;
				}	
			}

			
		} catch (Exception e) {

			LOG.error("*** CM sucks: " + e.getLocalizedMessage(), e);
		}
	}

	private void retrieveCssNode() {

		LOG.info("*** retrieveCssNode");

		try {
			CtxEntity cssNodeEnt = this.internalCtxBroker.retrieveCssNode(this.cssNodeId).get();
			LOG.info("*** CSS node context entity id: " + cssNodeEnt.getId());

		} catch (Exception e) {

			LOG.error("*** CM sucks: " + e.getLocalizedMessage(), e);
		}
	}

	/**
	 * At this point a CtxEntity of type {@link CtxEntityTypes.DEVICE} is created with an attribute
	 * of type {@link CtxAttributeTypes.ID} with a string value "device1234".
	 */
	private void createContext(){

		LOG.info("*** createContext");

		//create ctxEntity of type "Device"
		CtxEntity ctxEntityDevice = null;
		try {
			ctxEntityDevice = this.internalCtxBroker.createEntity(CtxEntityTypes.DEVICE).get();


			//get the context identifier of the created entity (to be used at the next step)
			this.ctxEntityIdentifier = ctxEntityDevice.getId();

			//create ctxAttribute with a String value that it is assigned to the previously created ctxEntity
			Future<CtxAttribute> futureCtxAttrString = this.internalCtxBroker.createAttribute(this.ctxEntityIdentifier, CtxAttributeTypes.ID);
			// get the object of the created CtxAttribute
			CtxAttribute ctxAttributeString = (CtxAttribute) futureCtxAttrString.get();

			// by setting this flag to true the CtxAttribute values will be stored to Context History Database upon update
			ctxAttributeString.setHistoryRecorded(true);

			// set a string value to CtxAttribute
			ctxAttributeString.setStringValue("device1234");

			// with this update the attribute is stored in Context DB
			Future<CtxModelObject> futureAttrUpdated = this.internalCtxBroker.update(ctxAttributeString);

			// get the updated CtxAttribute object and identifier (to be used later for retrieval purposes)
			ctxAttributeString = (CtxAttribute) futureAttrUpdated.get();
			this.ctxAttributeStringIdentifier = ctxAttributeString.getId();


			//create a ctxAttribute with a Binary value that is assigned to the same CtxEntity
			Future<CtxAttribute> futureCtxAttrBinary = this.internalCtxBroker.createAttribute(ctxEntityDevice.getId(), "CustomData");
			CtxAttribute ctxAttrBinary = (CtxAttribute) futureCtxAttrBinary.get();

			// this is a mock blob class that contains the value "999"
			MockBlobClass blob = new MockBlobClass(999);
			byte[] blobBytes;
			try {
				blobBytes = SerialisationHelper.serialise(blob);

				Future<CtxAttribute> futureCtxAttrBinaryUpdated = this.internalCtxBroker.updateAttribute(ctxAttrBinary.getId(), blobBytes);
				ctxAttrBinary = (CtxAttribute) futureCtxAttrBinaryUpdated.get();

				this.ctxAttributeBinaryIdentifier = ctxAttrBinary.getId();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// at this point the ctxEntity of type CtxEntityTypes.DEVICE that is assigned with
			// a ctxAttribute of type CtxAttributeTypes.ID with a string value
			// and a ctxAttribute of type "CustomData" with a binary value

			// create Association of type ...
			CtxAssociation association_uses = this.internalCtxBroker.createAssociation(CtxAssociationTypes.USES_DEVICES).get();

			final IndividualCtxEntity operator = this.internalCtxBroker.retrieveIndividualEntity(this.cssOwnerId).get();
			association_uses.addChildEntity(ctxEntityDevice.getId());
			association_uses.setParentEntity(operator.getId());
			this.internalCtxBroker.update(association_uses);


		} catch (Exception e) {

			LOG.error("*** CM sucks: " + e.getLocalizedMessage(), e);
		}
	}

	/**
	 * This method demonstrates how to retrieve context data from the context database
	 */
	private void lookupContext() {

		LOG.info("*** lookupContext");
		try {
			List<CtxIdentifier> idsEntities =this.internalCtxBroker.lookup(CtxModelType.ENTITY, CtxEntityTypes.DEVICE).get();
			LOG.info("*** lookup results for Entity type: '" + CtxEntityTypes.DEVICE + "' " +idsEntities);

			List<CtxIdentifier> idsAttribute = this.internalCtxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.ID).get();
			LOG.info("*** lookup results for Attribute type: '" + CtxAttributeTypes.ID + "' " +idsAttribute);

			final IndividualCtxEntity operator = this.internalCtxBroker.retrieveIndividualEntity(this.cssOwnerId).get();


			//testing lookup(final CtxEntityIdentifier entityId,final CtxModelType modelType, final String type)

			List<CtxIdentifier> listAttribute = this.internalCtxBroker.lookup( operator.getId(), CtxModelType.ATTRIBUTE, CtxAttributeTypes.ID).get();

			if(listAttribute.size()>0){
				CtxAttributeIdentifier attrID = (CtxAttributeIdentifier) listAttribute.get(0);
				LOG.info("*** attributeID "+attrID);
				CtxAttribute ctxAttribute = (CtxAttribute) this.internalCtxBroker.retrieve(attrID).get();
				LOG.info("attribute type should be 'xcmanager.societies.local' and it is:"+ ctxAttribute.getStringValue());
			}

			List<CtxIdentifier> listAssociation = this.internalCtxBroker.lookup( operator.getId(), CtxModelType.ASSOCIATION, CtxAssociationTypes.USES_DEVICES).get();
			if(listAssociation.size()>0){
				CtxAssociationIdentifier assocID = (CtxAssociationIdentifier) listAssociation.get(0);
				LOG.info("*** associationID "+ assocID);
				CtxAssociation ctxAssociation = (CtxAssociation) this.internalCtxBroker.retrieve(assocID).get();
				LOG.info("association type should be 'USES_DEVICES' and it is:"+ ctxAssociation.getType());
			}


		} catch (Exception e) {

			LOG.error("*** CM sucks: " + e.getLocalizedMessage(), e);
		}
	}

	/**
	 * This method demonstrates how to retrieve context data from the context database
	 */
	private void retrieveContext() {

		LOG.info("*** retrieveContext");

		// if the CtxEntityID or CtxAttributeID is known the retrieval is performed by using the ctxBroker.retrieve(CtxIdentifier) method
		try {
			// retrieve ctxEntity
			// This retrieval is performed based on the known CtxEntity identifier
			// Retrieve is also possible to be performed based on the type of the CtxEntity. This will be demonstrated in a later example.

			CtxEntity retrievedCtxEntity = (CtxEntity) this.internalCtxBroker.retrieve(this.ctxEntityIdentifier).get();

			LOG.info("Retrieved ctxEntity id " +retrievedCtxEntity.getId()+ " of type: "+retrievedCtxEntity.getType());

			// retrieve the CtxAttribute contained in the CtxEntity with the string value
			// again the retrieval is based on an known identifier, it is possible to retrieve it based on type.This will be demonstrated in a later example.
			CtxAttribute retrievedCtxAttribute = (CtxAttribute) this.internalCtxBroker.retrieve(this.ctxAttributeStringIdentifier).get();

			LOG.info("Retrieved ctxAttribute id " +retrievedCtxAttribute.getId()+ " and value: "+retrievedCtxAttribute.getStringValue());

			// retrieve ctxAttribute with the binary value
			Future<CtxModelObject> ctxAttributeRetrievedBinaryFuture = this.internalCtxBroker.retrieve(this.ctxAttributeBinaryIdentifier);
			CtxAttribute ctxAttributeRetrievedBinary = (CtxAttribute) ctxAttributeRetrievedBinaryFuture.get();

			//deserialise object
			MockBlobClass retrievedBlob = (MockBlobClass) SerialisationHelper.deserialise(ctxAttributeRetrievedBinary.getBinaryValue(), this.getClass().getClassLoader());
			LOG.info("Retrieved ctxAttribute id " +ctxAttributeRetrievedBinary.getId()+ "and value: "+ retrievedBlob.toString());

		} catch (Exception e) {
			LOG.error("*** CM sucks: " + e.getLocalizedMessage(), e);
		}
	}

	/**
	 * This method demonstrates how to create and retrieve context history data
	 */
	private void simpleCtxHistoryTest() {

		LOG.info("*** simpleCtxHistoryTest");
		CtxAttribute ctxAttribute;

		final CtxEntity ctxEntity;
		// Create the attribute's scope

		try {
			ctxEntity = internalCtxBroker.createEntity("entType").get();

			// Create the attribute to be tested
			ctxAttribute = internalCtxBroker.createAttribute(ctxEntity.getId(), "attrType").get();
			ctxAttribute.setHistoryRecorded(true);

			ctxAttribute.setIntegerValue(100);
			ctxAttribute = (CtxAttribute) internalCtxBroker.update(ctxAttribute).get();

			ctxAttribute.setIntegerValue(200);
			ctxAttribute = (CtxAttribute) internalCtxBroker.update(ctxAttribute).get();

			ctxAttribute.setIntegerValue(300);
			ctxAttribute = (CtxAttribute) internalCtxBroker.update(ctxAttribute).get();

			List<CtxHistoryAttribute> history = internalCtxBroker.retrieveHistory(ctxAttribute.getId(), null, null).get();

			for(CtxHistoryAttribute hocAttr: history){
				System.out.println("history List id:"+hocAttr.getId()+" getLastMod:"+hocAttr.getLastModified() +" hocAttr value:"+hocAttr.getIntegerValue());		
			}

			//test createHistory methods
			CtxAttribute fakeAttribute = internalCtxBroker.createAttribute(ctxEntity.getId(), "historyAttribute").get();
			List<CtxHistoryAttribute> historyList = new ArrayList<CtxHistoryAttribute>();

			Date date = new Date();
			date.setTime(1000);
			CtxHistoryAttribute hocAttr1 = internalCtxBroker.createHistoryAttribute(fakeAttribute.getId(), date, "one", CtxAttributeValueType.STRING).get();
			date.setTime(2000);
			CtxHistoryAttribute hocAttr2 = internalCtxBroker.createHistoryAttribute(fakeAttribute.getId(), date, "two", CtxAttributeValueType.STRING).get();
			date.setTime(3000);
			CtxHistoryAttribute hocAttr3 = internalCtxBroker.createHistoryAttribute(fakeAttribute.getId(), date, "three", CtxAttributeValueType.STRING).get();
			historyList.add(hocAttr1);
			historyList.add(hocAttr2);
			historyList.add(hocAttr3);

			List<CtxHistoryAttribute> historyListRetrieved = internalCtxBroker.retrieveHistory(fakeAttribute.getId(), null, null).get();
			if(historyListRetrieved.equals(historyListRetrieved)) System.out.println("Succesfull Retrieval of created hoc Attributes");

			for (CtxHistoryAttribute ctxHistAttr : historyListRetrieved){
				System.out.println("Hoc attribute value:" +ctxHistAttr.getStringValue()+" time:"+ctxHistAttr.getLastModified().getTime());
			}

		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	/**
	 * This method demonstrates how to create and retrieve context history data in tuples, and how to update the tuple types.
	 */
	private void tuplesCtxHistoryTest() {

		LOG.info("*** tuplesCtxHistoryTest");

		final CtxEntity ctxEntity;
		CtxAttribute primaryAttribute;
		CtxAttribute escortingAttribute1;
		CtxAttribute escortingAttribute2;
		CtxAttribute escortingAttribute3;

		try {
			ctxEntity = (CtxEntity)internalCtxBroker.createEntity(CtxEntityTypes.ORGANISATION).get();

			// Create the attribute to be tested
			primaryAttribute = (CtxAttribute) internalCtxBroker.createAttribute(ctxEntity.getId(), CtxAttributeTypes.ACTION).get();
			primaryAttribute.setHistoryRecorded(true);
			primaryAttribute.setStringValue("TurnOn");
			primaryAttribute = (CtxAttribute) internalCtxBroker.update(primaryAttribute).get();
			
			escortingAttribute1 = (CtxAttribute)internalCtxBroker.createAttribute(ctxEntity.getId(), CtxAttributeTypes.LOCATION_COORDINATES).get();
			escortingAttribute1.setHistoryRecorded(true);
			escortingAttribute1 = (CtxAttribute)internalCtxBroker.update(escortingAttribute1).get();
			
			escortingAttribute2 = (CtxAttribute)internalCtxBroker.createAttribute(ctxEntity.getId(), CtxAttributeTypes.TEMPERATURE).get();
			escortingAttribute2.setHistoryRecorded(true);
			escortingAttribute2 = (CtxAttribute)internalCtxBroker.update(escortingAttribute2).get();
			
			escortingAttribute1 =  internalCtxBroker.updateAttribute(escortingAttribute1.getId(),(Serializable)"100,200").get();
			escortingAttribute2 =  internalCtxBroker.updateAttribute(escortingAttribute2.getId(),(Serializable)"HOT").get();

			List<CtxAttributeIdentifier> listOfEscortingAttributeIds = new ArrayList<CtxAttributeIdentifier>();
			listOfEscortingAttributeIds.add(escortingAttribute1.getId());
			listOfEscortingAttributeIds.add(escortingAttribute2.getId());

			LOG.info("set tuples");
			Boolean tuplesSet = internalCtxBroker.setHistoryTuples(primaryAttribute.getId(), listOfEscortingAttributeIds).get();	
			LOG.info("set tuples ok: "+tuplesSet);
			// this will store the first tuple set
		
			primaryAttribute = (CtxAttribute) internalCtxBroker.update(primaryAttribute).get();
			
			Date date = new Date();
			LOG.info("1 time "+date.getTime());
			Thread.sleep(1000);
			
			LOG.info("Store second set of hoc attribute tuples");
			escortingAttribute1 =  internalCtxBroker.updateAttribute(escortingAttribute1.getId(),(Serializable)"101,201").get();
			escortingAttribute2 =  internalCtxBroker.updateAttribute(escortingAttribute2.getId(),(Serializable)"WARM").get();
			primaryAttribute =  internalCtxBroker.updateAttribute(primaryAttribute.getId(),(Serializable)"Idle").get();
			Thread.sleep(1000);
			date = new Date();
			LOG.info("2 time "+date.getTime());
			
			LOG.info("Store third set of hoc attribute tuples");
			escortingAttribute1 =  internalCtxBroker.updateAttribute(escortingAttribute1.getId(),(Serializable)"102,202").get();
			escortingAttribute2 =  internalCtxBroker.updateAttribute(escortingAttribute2.getId(),(Serializable)"COLD").get();
			primaryAttribute =  internalCtxBroker.updateAttribute(primaryAttribute.getId(),(Serializable)"TurnOff").get();
			
			
			Thread.sleep(1000);
			date = new Date();
			LOG.info("3 time "+date.getTime());
			
			//primaryAttribute =  internalCtxBroker.updateAttribute(primaryAttribute.getId(),(Serializable)"forthValue").get();
			LOG.info("primary id "+primaryAttribute.getId());
			LOG.info("listOfEscortingAttributeIds id "+listOfEscortingAttributeIds);
			System.out.println("printHocTuples id based");
			Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> tupleResults = internalCtxBroker.retrieveHistoryTuples(primaryAttribute.getId(), listOfEscortingAttributeIds, null, null).get();
			LOG.info("tupleResults "+tupleResults);
			printHocTuples(tupleResults);
			
			System.out.println("printHocTuples type based");
			Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> tupleResults2 = internalCtxBroker.retrieveHistoryTuples(primaryAttribute.getType(), listOfEscortingAttributeIds, null, null).get();
			printHocTuples(tupleResults2);
			
			
			
			System.out.println("add new attribute in an existing tuple");
			
			escortingAttribute3 = (CtxAttribute) internalCtxBroker.createAttribute(ctxEntity.getId(),CtxAttributeTypes.BOOKS).get();
			//escortingAttribute3.setHistoryRecorded(true);
			List<CtxAttributeIdentifier> newlistOfEscortingAttributeIds = new ArrayList<CtxAttributeIdentifier>();
			newlistOfEscortingAttributeIds.add(escortingAttribute1.getId());
			newlistOfEscortingAttributeIds.add(escortingAttribute2.getId());
			newlistOfEscortingAttributeIds.add(escortingAttribute3.getId());
		
			
			internalCtxBroker.updateHistoryTuples(primaryAttribute.getId(), newlistOfEscortingAttributeIds);

			// newly add attribute doesn't contain any value yet, a null ref should be added in tuple
			escortingAttribute1 =  internalCtxBroker.updateAttribute(escortingAttribute1.getId(),(Serializable)"103,203").get();
			escortingAttribute2 =  internalCtxBroker.updateAttribute(escortingAttribute2.getId(),(Serializable)"FREEZING").get();
			//escortingAttribute3 =  internalCtxBroker.updateAttribute(escortingAttribute3.getId(),(Serializable)"escortingValue3_forthValue").get();
			primaryAttribute =  internalCtxBroker.updateAttribute(primaryAttribute.getId(),(Serializable)"MUTE").get();

			escortingAttribute1 =  internalCtxBroker.updateAttribute(escortingAttribute1.getId(),(Serializable)"104,204").get();
			escortingAttribute2 =  internalCtxBroker.updateAttribute(escortingAttribute2.getId(),(Serializable)"MELTING").get();
			escortingAttribute3 =  internalCtxBroker.updateAttribute(escortingAttribute3.getId(),(Serializable)"NICE_BOOK_TITLE").get();
			primaryAttribute =  internalCtxBroker.updateAttribute(primaryAttribute.getId(),(Serializable)"OFF").get();

			Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> updatedTupleResults = internalCtxBroker.retrieveHistoryTuples(primaryAttribute.getId(), listOfEscortingAttributeIds, null, null).get();
			printHocTuples(updatedTupleResults);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void printHocTuples(Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> tupleResults){

		int i = 0;
		System.out.println("printing hoc data size:"+ tupleResults.size());
		for (CtxHistoryAttribute primary : tupleResults.keySet()){
			String primaryValue = null;
			if (primary.getStringValue() != null) primaryValue =primary.getStringValue();
			String escValueTotal = "";
			for(CtxHistoryAttribute escortingAttr: tupleResults.get(primary)){
				String escValue = "";
				if (escortingAttr.getStringValue() != null )  escValue =escortingAttr.getStringValue();	
				escValueTotal = escValueTotal+" "+escValue; 
				//System.out.println("escValueTotal "+escValueTotal); 
			}
			System.out.println(i+ " primaryValue: "+primaryValue+ " escValues: "+escValueTotal);
			i++;
		}
	}	

	@SuppressWarnings("unused")
	private void triggerInferenceTest() {
		LOG.info("*** triggerInferenceTest");
		try {
			IndividualCtxEntity ownerEnt = this.internalCtxBroker.retrieveIndividualEntity(this.cssOwnerId).get();

			Set<CtxAttribute> locationSet = ownerEnt.getAttributes(CtxAttributeTypes.LOCATION_SYMBOLIC);
			List<CtxAttribute> locList = new ArrayList<CtxAttribute>(locationSet);
			CtxAttribute locAttr = null;
			if(locList.size()>0){
				locAttr = locList.get(0);
				System.out.println("trigger inference for attr: "+locAttr);

				locAttr = (CtxAttribute) this.internalCtxBroker.retrieve(locAttr.getId()).get();
				System.out.println("after inference " + locAttr);
			}

		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	/**
	 * This method demonstrates how to register for context change events in the context database
	 */
	private void registerForContextChanges() {

		LOG.info("*** registerForContextChanges");

		try {
			// 1a. Register listener by specifying the context attribute identifier
			this.internalCtxBroker.registerForChanges(new MyCtxChangeEventListener(), this.ctxAttributeStringIdentifier);

			// 1b. Register listener by specifying the context attribute scope and type
			this.internalCtxBroker.registerForChanges(new MyCtxChangeEventListener(), this.ctxEntityIdentifier, CtxAttributeTypes.ID);

			// 1c. Register listener by specifying the context attribute scope
			this.internalCtxBroker.registerForChanges(new MyCtxChangeEventListener(), this.ctxEntityIdentifier, null);

			// 2. Update attribute to see some event action
			this.internalCtxBroker.updateAttribute((CtxAttributeIdentifier) this.ctxAttributeStringIdentifier, "newDeviceIdValue");

		} catch (CtxException ce) {

			LOG.error("*** CM sucks " + ce.getLocalizedMessage(), ce);
		}
	}

	private class MyCtxChangeEventListener implements CtxChangeEventListener {

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onCreation(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onCreation(CtxChangeEvent event) {

			LOG.info(event.getId() + ": *** CREATED event ***");
		}

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onModification(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onModification(CtxChangeEvent event) {

			LOG.info(event.getId() + ": *** MODIFIED event ***");
		}

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onRemoval(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onRemoval(CtxChangeEvent event) {

			LOG.info(event.getId() + ": *** REMOVED event ***");
		}

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onUpdate(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onUpdate(CtxChangeEvent event) {

			LOG.info(event.getId() + ": *** UPDATED event ***");
		}
	}

	@Override
	public void pubsubEvent(IIdentity pubsubService, String node,
			String itemId, Object item) {

		if(item.getClass().equals(org.societies.api.schema.activity.MarshaledActivity.class)){
            MarshaledActivity a = (MarshaledActivity)item;
			LOG.info("*************************** pubsubevent with acitvity " + a.getActor() + " " +a.getVerb());
		}
	}

}