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
package org.societies.context.source.csscis.impl;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.activity.MarshaledActivity;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * This class is used to update user/community context based on CSS/CIS related
 * events (e.g. new CSS connections, CIS membership changes, etc).
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 1.0
 */
@Service
@Lazy(false)
public class CssCisCtxMonitor extends EventListener implements Subscriber {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CssCisCtxMonitor.class);
	
	private static final String[] EVENT_TYPES = { EventTypes.CSS_RECORD_EVENT,
		EventTypes.CSS_FRIENDED_EVENT, EventTypes.CIS_SUBS,	
		EventTypes.CIS_UNSUBS, EventTypes.CIS_CREATION, EventTypes.CIS_DELETION,
		EventTypes.CIS_RESTORE };
	
	private static final String VERB_CSS_JOINED = "joined";
	private static final String VERB_CSS_LEFT = "left";
			
	/** The internal Context Broker service. */
	@Autowired(required=true)
	private ICtxBroker ctxBroker;
	
	/** The Event Mgr service. */
	private IEventMgr eventMgr;
	
	/** The PubsubClient service reference. */
	@Autowired
	private PubsubClient pubsubClient;
	
	/** The Comm Mgr service. */
	private ICommManager commMgr;
	
	/** The executor service. */
	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	@Autowired(required=true)
	CssCisCtxMonitor(IEventMgr eventMgr, ICommManager commMgr) {
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
		
		this.eventMgr = eventMgr;
		this.commMgr = commMgr;
		if (LOG.isInfoEnabled())
			LOG.info("Registering for '" + Arrays.asList(EVENT_TYPES) + "' events");
		this.eventMgr.subscribeInternalEvent(this, EVENT_TYPES, null);
		// TODO unsubscribe when stopped?
	}

	/*
	 * @see org.societies.api.osgi.event.EventListener#handleExternalEvent(org.societies.api.osgi.event.CSSEvent)
	 */
	@Override
	public void handleExternalEvent(CSSEvent event) {
		
		if (LOG.isWarnEnabled())
			LOG.warn("Received unexpected external '" + event.geteventType() + "' event: " + event);
	}

	/*
	 * @see org.societies.api.osgi.event.EventListener#handleInternalEvent(org.societies.api.osgi.event.InternalEvent)
	 */
	@Override
	public void handleInternalEvent(InternalEvent event) {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Received internal event: " + eventToString(event));
		
		if (EventTypes.CSS_RECORD_EVENT.equals(event.geteventType())) {

			if (!(event.geteventInfo() instanceof CssRecord)) {

				LOG.error("Could not handle internal " + event.geteventType() + " event: " 
						+ "Expected event info of type " + CssRecord.class.getName()
						+ " but was " + event.geteventInfo().getClass());
				return;
			}

			final CssRecord cssRecord = (CssRecord) event.geteventInfo();
			this.executorService.execute(new CssRecordUpdateHandler(cssRecord));
			
		} else if (EventTypes.CSS_FRIENDED_EVENT.equals(event.geteventType())) {
			
			if (event.geteventSource() == null || event.geteventSource().isEmpty()) {
				LOG.error("Could not handle internal " + event.geteventType() + " event: " 
						+ "Expected non-empty event source but was "
						+ event.geteventSource());
				return;
			}
			
			if (!(event.geteventInfo() instanceof String && ((String) event.geteventInfo()).isEmpty())) {

				LOG.error("Could not handle internal " + event.geteventType() + " event: " 
						+ "Expected non-empty event info but was " 
						+ event.geteventInfo());
				return;
			}
			
			this.executorService.execute(new CssFriendedHandler(event.geteventSource(),
					(String) event.geteventInfo()));

		} else if (EventTypes.CIS_SUBS.equals(event.geteventType())
				|| EventTypes.CIS_UNSUBS.equals(event.geteventType())) {
			
			if (event.geteventSource() == null || event.geteventSource().length() == 0) {

				LOG.error("Could not handle internal " + event.geteventType() + " event: " 
						+ "Expected non-null or non-empty event source of type IIdentity JID String"
						+ " but was " + event.geteventSource());
				return;
			}
			
			if (!(event.geteventInfo() instanceof Community)) {

				LOG.error("Could not handle internal " + event.geteventType() + " event: " 
						+ "Expected event info of type " + Community.class.getName()
						+ " but was " + event.geteventInfo().getClass());
				return;
			}

			final Community cisRecord = (Community) event.geteventInfo();
			if (EventTypes.CIS_SUBS.equals(event.geteventType()))
				this.executorService.execute(new MyCssJoinedCisHandler(
						event.geteventSource(), cisRecord.getCommunityJid()));
			else //if (EventTypes.CIS_UNSUBS.equals(event.geteventType()))
				this.executorService.execute(new MyCssLeftCisHandler(
						event.geteventSource(), cisRecord.getCommunityJid()));
			
		} else if (EventTypes.CIS_CREATION.equals(event.geteventType())
				|| EventTypes.CIS_DELETION.equals(event.geteventType())
				|| EventTypes.CIS_RESTORE.equals(event.geteventType())) {
			
			if (event.geteventSource() == null || event.geteventSource().length() == 0) {

				LOG.error("Could not handle internal " + event.geteventType() + " event: " 
						+ "Expected non-null or non-empty event source of type IIdentity JID String"
						+ " but was " + event.geteventSource());
				return;
			}
			
			if (!(event.geteventInfo() instanceof Community)) {

				LOG.error("Could not handle internal " + event.geteventType() + " event: " 
						+ "Expected event info of type " + Community.class.getName()
						+ " but was " + event.geteventInfo().getClass());
				return;
			}
			
			if (EventTypes.CIS_CREATION.equals(event.geteventType()))
				this.executorService.execute(
						new CisCreatedHandler((Community) event.geteventInfo()));
			else if (EventTypes.CIS_DELETION.equals(event.geteventType()))
				this.executorService.execute(
						new CisRemovedHandler((Community) event.geteventInfo()));
			else // if (EventTypes.CIS_RESTORE.equals(event.geteventType()))
				this.executorService.execute(
						new CisRestoredHandler((Community) event.geteventInfo()));
			
		} else {
		
			if (LOG.isWarnEnabled())
				LOG.warn("Received unexpeted event of type '" + event.geteventType() + "'");
		}
	}
	
	/*
	 * @see org.societies.api.comm.xmpp.pubsub.Subscriber#pubsubEvent(org.societies.api.identity.IIdentity, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public void pubsubEvent(IIdentity pubsubService, String node,
			String itemId, Object item) {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Received CIS Activity Feed pubsub event: " + item);
		
		if (!(item instanceof MarshaledActivity)) {
			
			LOG.error("Could not handle CIS Activity Feed pubsub event: " 
					+ "Expected item of type " + MarshaledActivity.class.getName()
					+ " but was " + ((item != null) ? item.getClass() : "null"));
			return;
		}
		
		final String cssIdStr = ((MarshaledActivity) item).getActor();
		final String cisIdStr = ((MarshaledActivity) item).getObject();
		final String verb = ((MarshaledActivity) item).getVerb();
		if (verb.equals(VERB_CSS_JOINED)) {
			this.executorService.execute(
					new CssJoinedMyCisHandler(cssIdStr, cisIdStr));
		} else if (verb.equals(VERB_CSS_LEFT)) {
			this.executorService.execute(
					new CssLeftMyCisHandler(cssIdStr, cisIdStr));
		} else {
			if (LOG.isDebugEnabled())
				LOG.debug("Ignoring CIS Activity Feed pubsub event with verb '" + verb + "'");
		}
	}
	
	private class CssRecordUpdateHandler implements Runnable {
		
		private final CssRecord cssRecord;
		
		private CssRecordUpdateHandler(final CssRecord cssRecord) {
			
			this.cssRecord = cssRecord;
		}
		
		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {

			if (LOG.isInfoEnabled())
				LOG.info("Updated CSS record: " + cssRecord);
			
			final String cssIdStr = cssRecord.getCssIdentity();
			try {
				IIdentity cssId = commMgr.getIdManager().fromJid(cssIdStr);
				CtxEntityIdentifier ownerCtxId = 
						ctxBroker.retrieveIndividualEntity(cssId).get().getId();

				String value;

				// NAME
				value = cssRecord.getName();
				if (value != null && !value.isEmpty())
					updateCtxAttribute(ownerCtxId, CtxAttributeTypes.NAME, value);

				// EMAIL
				value = cssRecord.getEmailID();
				if (value != null && !value.isEmpty())
					updateCtxAttribute(ownerCtxId, CtxAttributeTypes.EMAIL, value);

				// ADDRESS_HOME_CITY
				value = cssRecord.getHomeLocation();
				if (value != null && !value.isEmpty())
					updateCtxAttribute(ownerCtxId, CtxAttributeTypes.ADDRESS_HOME_CITY, value);

			} catch (InvalidFormatException ife) {

				LOG.error("Invalid CSS IIdentity found in CSS record: " 
						+ ife.getLocalizedMessage(), ife);
			} catch (Exception e) {

				LOG.error("Failed to access context data: " 
						+ e.getLocalizedMessage(), e);
			}
		}
	}
	
	private class CssFriendedHandler implements Runnable {
		
		private final String myCssIdStr;
		private final String newFriendIdStr;
		
		private CssFriendedHandler(final String myCssIdStr, final String newFriendIdStr) {
			
			this.myCssIdStr = myCssIdStr;
			this.newFriendIdStr = newFriendIdStr;
		}
		
		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {

			if (LOG.isInfoEnabled())
				LOG.info("CSS '" + myCssIdStr + "' friended '" + newFriendIdStr + "'");
			
			try {
				final IIdentity myCssId = commMgr.getIdManager().fromJid(myCssIdStr);
				final IndividualCtxEntity myCssEnt = 
						ctxBroker.retrieveIndividualEntity(myCssId).get();

				final IIdentity newFriendId = commMgr.getIdManager().fromJid(newFriendIdStr);
				final CtxEntityIdentifier newFriendEntId =
						ctxBroker.retrieveIndividualEntityId(
								new Requestor(myCssId), newFriendId).get();
				
				final CtxAssociation isFriendsWithAssoc;
				if (myCssEnt.getAssociations(CtxAssociationTypes.IS_FRIENDS_WITH).isEmpty()) {
					isFriendsWithAssoc = ctxBroker.createAssociation(
							new Requestor(myCssId), myCssId, CtxAssociationTypes.IS_FRIENDS_WITH).get();
					isFriendsWithAssoc.setParentEntity(myCssEnt.getId());
				} else {
					isFriendsWithAssoc = (CtxAssociation) ctxBroker.retrieve(
							myCssEnt.getAssociations(CtxAssociationTypes.IS_FRIENDS_WITH).iterator().next()).get();
				}
				isFriendsWithAssoc.addChildEntity(newFriendEntId);
				ctxBroker.update(isFriendsWithAssoc);

			} catch (InvalidFormatException ife) {

				LOG.error("Invalid CSS IIdentity found in CSS record: " 
						+ ife.getLocalizedMessage(), ife);
			} catch (Exception e) {

				LOG.error("Failed to access context data: " 
						+ e.getLocalizedMessage(), e);
			}
		}
	}
	
	private class MyCssJoinedCisHandler implements Runnable {
		
		private final String myCssIdStr;
		
		private final String cisIdStr;
		
		private MyCssJoinedCisHandler(final String myCssIdStr, final String cisIdStr) {
			
			this.myCssIdStr = myCssIdStr;
			this.cisIdStr = cisIdStr;
		}
		
		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {

			if (LOG.isInfoEnabled())
				LOG.info("My CSS '" + this.myCssIdStr + "' joined CIS '" + this.cisIdStr + "'");
			
			try {
				final IIdentity myCssId = commMgr.getIdManager().fromJid(
						this.myCssIdStr);
				final IndividualCtxEntity myCssEnt = 
						ctxBroker.retrieveIndividualEntity(myCssId).get();
				if (myCssEnt == null) {
					LOG.error("Failed to retrieve the IndividualCtxEntity of my CSS '"
							+ myCssId + "'");
					return;
				}

				final IIdentity cisId = commMgr.getIdManager().fromJid(this.cisIdStr);
				final CtxEntityIdentifier cisEntId =
						ctxBroker.retrieveCommunityEntityId(
								new Requestor(myCssId), cisId).get();
				if (cisEntId == null) {
					LOG.error("Failed to retrieve the CommunityCtxEntity id of CIS '" 
							+ cisId + "'");
					return;
				}
				
				final CtxAssociation isMemberOfAssoc;
				if (myCssEnt.getAssociations(CtxAssociationTypes.IS_MEMBER_OF).isEmpty()) {
					isMemberOfAssoc = ctxBroker.createAssociation(
							new Requestor(myCssId), myCssId, CtxAssociationTypes.IS_MEMBER_OF).get();
					isMemberOfAssoc.setParentEntity(myCssEnt.getId());
				} else {
					isMemberOfAssoc = (CtxAssociation) ctxBroker.retrieve(
							myCssEnt.getAssociations(CtxAssociationTypes.IS_MEMBER_OF).iterator().next()).get();
				}
				isMemberOfAssoc.addChildEntity(cisEntId);
				ctxBroker.update(isMemberOfAssoc);

			} catch (InvalidFormatException ife) {

				LOG.error("Invalid CSS/CIS IIdentity: " 
						+ ife.getLocalizedMessage(), ife);
			} catch (Exception e) {

				LOG.error("Failed to access context data: " 
						+ e.getLocalizedMessage(), e);
			}
		}
	}
	
	private class MyCssLeftCisHandler implements Runnable {
		
		private final String myCssIdStr;
		
		private final String cisIdStr;
		
		private MyCssLeftCisHandler(final String myCssIdStr, final String cisIdStr) {
			
			this.myCssIdStr = myCssIdStr;
			this.cisIdStr = cisIdStr;
		}
		
		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {

			if (LOG.isInfoEnabled())
				LOG.info("My CSS '" + this.myCssIdStr + "' left CIS '" + this.cisIdStr + "'");
			
			try {
				final IIdentity myCssId = commMgr.getIdManager().fromJid(
						this.myCssIdStr);
				final IndividualCtxEntity myCssEnt = 
						ctxBroker.retrieveIndividualEntity(myCssId).get();
				if (myCssEnt == null) {
					LOG.error("Failed to retrieve the IndividualCtxEntity of my CSS '"
							+ myCssId + "'");
					return;
				}

				final IIdentity cisId = commMgr.getIdManager().fromJid(cisIdStr);
				final CtxEntityIdentifier cisEntId =
						ctxBroker.retrieveCommunityEntityId(
								new Requestor(myCssId), cisId).get();
				if (cisEntId == null) {
					LOG.error("Failed to retrieve the CommunityCtxEntity id of CIS '" 
							+ cisId + "'");
					return;
				}
				
				final CtxAssociation isMemberOfAssoc;
				if (!myCssEnt.getAssociations(CtxAssociationTypes.IS_MEMBER_OF).isEmpty()) {
					
					isMemberOfAssoc = (CtxAssociation) ctxBroker.retrieve(
							myCssEnt.getAssociations(CtxAssociationTypes.IS_MEMBER_OF).iterator().next()).get();
					isMemberOfAssoc.removeChildEntity(cisEntId);
					ctxBroker.update(isMemberOfAssoc);
				}

			} catch (InvalidFormatException ife) {

				LOG.error("Invalid CSS/CIS IIdentity: " 
						+ ife.getLocalizedMessage(), ife);
			} catch (Exception e) {

				LOG.error("Failed to access context data: " 
						+ e.getLocalizedMessage(), e);
			}
		}
	}

	private class CssJoinedMyCisHandler implements Runnable {
		
		private final String cssIdStr;
		
		private final String myCisIdStr;
		
		private CssJoinedMyCisHandler(String cssIdStr, String myCisIdStr) {
			
			this.cssIdStr = cssIdStr;
			this.myCisIdStr = myCisIdStr;
		}

		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			
			if (LOG.isInfoEnabled())
				LOG.info("CSS '" + this.cssIdStr + "' joined my CIS '" + this.myCisIdStr + "'");
			
			try {
				// Retrieve CommunityCtxEntity of CIS
				final IIdentity myCisId = commMgr.getIdManager().fromJid(this.myCisIdStr);
				final CtxEntityIdentifier cisEntityId = ctxBroker.retrieveCommunityEntityId(myCisId).get();
				if (cisEntityId == null) {
					LOG.error("Failed to retrieve the CommunityCtxEntity id of my CIS '"
							+ myCisId + "'");
				}
				final CommunityCtxEntity cisEntity = 
						(CommunityCtxEntity) ctxBroker.retrieve(cisEntityId).get();
				if (cisEntity == null) {
					LOG.error("Failed to retrieve CommunityCtxEntity with id '"
							+ cisEntityId + "'");
				}
				
				// Retrieve CIS owner
				// TODO do it the right way (TM)
				final IIdentity cisOwnerId = commMgr.getIdManager().fromJid(
						commMgr.getIdManager().getThisNetworkNode().getBareJid());
				
				// Retrieve IndividualCtxEntity identifier of new member (CSS) 
				final IIdentity cssId = commMgr.getIdManager().fromJid(this.cssIdStr);
				final CtxEntityIdentifier cssEntId = ctxBroker.retrieveIndividualEntityId(
						new RequestorCis(cisOwnerId, myCisId), cssId).get();
				if (cssEntId == null) {
					LOG.error("Failed to retrieve IndividualCtxEntity of new CIS member " + cssId);
					return;
				}
				
				if (LOG.isInfoEnabled())
					LOG.info("Adding member '" + cssEntId + "' to community '" + cisEntity.getId() + "'");
				final CtxAssociationIdentifier hasMembersAssocId = 
						cisEntity.getAssociations(CtxAssociationTypes.HAS_MEMBERS).iterator().next();
				final CtxAssociation hasMembersAssoc = 
						(CtxAssociation) ctxBroker.retrieve(hasMembersAssocId).get();
				hasMembersAssoc.addChildEntity(cssEntId);
				ctxBroker.update(hasMembersAssoc);				
				
			} catch (InvalidFormatException ife) {
				
				LOG.error("Failed to instantiate IIdentity from String representation: " 
						+ ife.getLocalizedMessage(), ife);
			} catch (Exception e) {
				
				LOG.error("Failed to access context data: " 
						+ e.getLocalizedMessage(), e);
			}
		}
	}
	
	private class CssLeftMyCisHandler implements Runnable {
		
		private final String cssIdStr;
		
		private final String myCisIdStr;
		
		private CssLeftMyCisHandler(String cssIdStr, String myCisIdStr) {
			
			this.cssIdStr = cssIdStr;
			this.myCisIdStr = myCisIdStr;
		}

		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			
			if (LOG.isInfoEnabled())
				LOG.info("CSS '" + this.cssIdStr + "' left my CIS '" + this.myCisIdStr + "'");
			
			try {
				// Retrieve CommunityCtxEntity of CIS
				final IIdentity myCisId = commMgr.getIdManager().fromJid(this.myCisIdStr);
				final CtxEntityIdentifier cisEntityId = ctxBroker.retrieveCommunityEntityId(myCisId).get();
				if (cisEntityId == null) {
					LOG.error("Failed to retrieve the CommunityCtxEntity id of my CIS '"
							+ myCisId + "'");
				}
				final CommunityCtxEntity cisEntity = 
						(CommunityCtxEntity) ctxBroker.retrieve(cisEntityId).get();
				if (cisEntity == null) {
					LOG.error("Failed to retrieve CommunityCtxEntity with id '"
							+ cisEntityId + "'");
				}
				
				// Retrieve CIS owner
				// TODO do it the right way (TM)
				final IIdentity cisOwnerId = commMgr.getIdManager().fromJid(
						commMgr.getIdManager().getThisNetworkNode().getBareJid());
				
				// Retrieve IndividualCtxEntity identifier of ex-member (CSS) 
				final IIdentity cssId = commMgr.getIdManager().fromJid(this.cssIdStr);
				final CtxEntityIdentifier cssEntId = ctxBroker.retrieveIndividualEntityId(
						new RequestorCis(cisOwnerId, myCisId), cssId).get();
				if (cssEntId == null) {
					LOG.error("Failed to retrieve IndividualCtxEntity of ex CIS member " + cisOwnerId);
					return;
				}
				
				if (LOG.isInfoEnabled())
					LOG.info("Removing member '" + cssEntId + "' from community '" + cisEntity.getId() + "'");
				final CtxAssociationIdentifier hasMembersAssocId = 
						cisEntity.getAssociations(CtxAssociationTypes.HAS_MEMBERS).iterator().next();
				final CtxAssociation hasMembersAssoc = 
						(CtxAssociation) ctxBroker.retrieve(hasMembersAssocId).get();
				hasMembersAssoc.removeChildEntity(cssEntId);
				ctxBroker.update(hasMembersAssoc);				
				
			} catch (InvalidFormatException ife) {
				
				LOG.error("Failed to instantiate IIdentity from String representation: " 
						+ ife.getLocalizedMessage(), ife);
			} catch (Exception e) {
				
				LOG.error("Failed to access context data: " 
						+ e.getLocalizedMessage(), e);
			}
		}
	}
	
	/**
	 * (1) Create community ctx entity
	 * (2) Create community ctx attributes 
	 * (3) Update community ctx entity HAS_MEMBERS association
	 * (4) Update community owner ctx entity IS_MEMBER_OF association
	 * (5) Subscribe for CIS Activity Feed to monitor membership changes
	 */
	private class CisCreatedHandler implements Runnable {
		
		private final Community cis;
		
		private CisCreatedHandler(Community cis) {
			
			this.cis = cis;
		}

		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			
			try {
				if (LOG.isInfoEnabled())
					LOG.info("CIS '" + this.cis.getCommunityJid() + "' created");
				// (1) Create community ctx entity
				final String cisIdStr = this.cis.getCommunityJid();
				final IIdentity cisId = commMgr.getIdManager().fromJid(cisIdStr);
				final CommunityCtxEntity cisEntity = ctxBroker.createCommunityEntity(cisId).get();
				
				// (2) Create community ctx attributes
				// NAME and ABOUT are extracted from the CIS record
				// NAME
				createCtxAttribute(cisEntity.getId(), CtxAttributeTypes.NAME, this.cis.getCommunityName());
				// ABOUT
				createCtxAttribute(cisEntity.getId(), CtxAttributeTypes.ABOUT, this.cis.getDescription());
				
				// TODO add attribute types defined in privacy policy, 
				// adding some attributes for testing purposes
				// INTERESTS
				createCtxAttribute(cisEntity.getId(), CtxAttributeTypes.INTERESTS, null);
				// LOCATION SYMBOLIC				
				createCtxAttribute(cisEntity.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC, null);
				// LOCATION COORDINATES				
				createCtxAttribute(cisEntity.getId(), CtxAttributeTypes.LOCATION_COORDINATES, null);
				// BOOKS				
				createCtxAttribute(cisEntity.getId(), CtxAttributeTypes.BOOKS, null);
				// MOVIES				
				createCtxAttribute(cisEntity.getId(), CtxAttributeTypes.MOVIES, null);
				//LANGUAGES 
				createCtxAttribute(cisEntity.getId(), CtxAttributeTypes.LANGUAGES , null);
				//FAVOURITE_QUOTES
				createCtxAttribute(cisEntity.getId(), CtxAttributeTypes.FAVOURITE_QUOTES , null);
				
				// (3) Update community ctx entity HAS_MEMBERS association
				final String cisOwnerIdStr = cis.getOwnerJid();
				final IIdentity cisOwnerId = commMgr.getIdManager().fromJid(cisOwnerIdStr);
				final IndividualCtxEntity cisOwnerEntity = ctxBroker.retrieveIndividualEntity(cisOwnerId).get();
				if (cisOwnerEntity == null) {
					LOG.error("Could not retrieve IndividualCtxEntity for CIS creator " + cisOwnerId);
					return;
				}
					
				if (LOG.isInfoEnabled())
					LOG.info("Adding member '" + cisOwnerEntity.getId() + "' to community '" + cisEntity.getId() + "'");
				final CtxAssociationIdentifier hasMembersAssocId = 
						cisEntity.getAssociations(CtxAssociationTypes.HAS_MEMBERS).iterator().next();
				final CtxAssociation hasMembersAssoc = 
						(CtxAssociation) ctxBroker.retrieve(hasMembersAssocId).get();
				hasMembersAssoc.addChildEntity(cisOwnerEntity.getId());
				ctxBroker.update(hasMembersAssoc);
				// TODO owning CSS ?
				// TODO administrating CSS ?
				// TODO membership criteria / bonds
				
				// (4) Update community owner ctx entity IS_MEMBER_OF association
				final CtxAssociation isMemberOfAssoc;
				if (cisOwnerEntity.getAssociations(CtxAssociationTypes.IS_MEMBER_OF).isEmpty()) {
					isMemberOfAssoc = ctxBroker.createAssociation(
							new Requestor(cisOwnerId), cisOwnerId, CtxAssociationTypes.IS_MEMBER_OF).get();
					isMemberOfAssoc.setParentEntity(cisOwnerEntity.getId());
				} else {
					isMemberOfAssoc = (CtxAssociation) ctxBroker.retrieve(
							cisOwnerEntity.getAssociations(CtxAssociationTypes.IS_MEMBER_OF).iterator().next()).get();
				}
				isMemberOfAssoc.addChildEntity(cisEntity.getId());
				ctxBroker.update(isMemberOfAssoc);
				
				// (5) Subscribe for CIS Activity Feed to monitor membership changes
				if (LOG.isInfoEnabled())
					LOG.info("Subscribing for the ActivityFeed of CIS '" + cisIdStr + "'");
				pubsubClient.subscriberSubscribe(cisOwnerId, cisIdStr, CssCisCtxMonitor.this);
				
			} catch (InvalidFormatException ife) {
				
				LOG.error("Invalid IIdentity found in CIS record: " 
						+ ife.getLocalizedMessage(), ife);
			} catch (CtxException ce) {
				
				LOG.error("Failed to access context data: " 
						+ ce.getLocalizedMessage(), ce);
			} catch (Exception e) {
				
				LOG.error("Failed to subscribe for CIS ActivityFeed: " 
						+ e.getLocalizedMessage(), e);
			}
		}
	}
	
	private class CisRemovedHandler implements Runnable {
		
		private final Community cis;
		
		private CisRemovedHandler(Community cis) {
			
			this.cis = cis;
		}

		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			
			if (LOG.isInfoEnabled())
				LOG.info("CIS '" + this.cis.getCommunityJid() + "' deleted");
			// TODO Auto-generated method stub
		}
	}
	
	/**
	 * (1) Subscribe for CIS Activity Feed to monitor membership changes
	 */
	private class CisRestoredHandler implements Runnable {
		
		private final Community cis;
		
		private CisRestoredHandler(Community cis) {
			
			this.cis = cis;
		}

		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			
			try {
				if (LOG.isInfoEnabled())
					LOG.info("CIS '" + this.cis.getCommunityJid() + "' restored");
				// (1) Subscribe for CIS Activity Feed to monitor membership changes
				final String cisIdStr = this.cis.getCommunityJid();
				final IIdentity cisId = commMgr.getIdManager().fromJid(cisIdStr);
				
				final String cisOwnerIdStr = cis.getOwnerJid();
				final IIdentity cisOwnerId = commMgr.getIdManager().fromJid(cisOwnerIdStr);
								
				if (LOG.isInfoEnabled())
					LOG.info("Subscribing for the ActivityFeed of CIS '" + cisId + "'");
				pubsubClient.subscriberSubscribe(cisOwnerId, cisIdStr, CssCisCtxMonitor.this);
				
			} catch (InvalidFormatException ife) {
				
				LOG.error("Invalid IIdentity found in CIS record: " 
						+ ife.getLocalizedMessage(), ife);
			
			} catch (Exception e) {
				
				LOG.error("Failed to subscribe for CIS ActivityFeed: " 
						+ e.getLocalizedMessage(), e);
			}
		}
	}
	
	private void updateCtxAttribute(CtxEntityIdentifier ownerCtxId, 
			String type, String value) throws Exception {

		if (LOG.isDebugEnabled())
			LOG.debug("Updating '" + type + "' of entity " + ownerCtxId + " with value '" + value + "'");

		final List<CtxIdentifier> ctxIds = 
				this.ctxBroker.lookup(ownerCtxId, CtxModelType.ATTRIBUTE, type).get();
		final CtxAttribute attr;
		if (!ctxIds.isEmpty())
			attr = (CtxAttribute) this.ctxBroker.retrieve(ctxIds.get(0)).get();
		else
			attr = this.ctxBroker.createAttribute(ownerCtxId, type).get();
			
		attr.setStringValue(value);
		attr.setValueType(CtxAttributeValueType.STRING);
		attr.getQuality().setOriginType(CtxOriginType.MANUALLY_SET);
		this.ctxBroker.update(attr);
	}
	
	private void createCtxAttribute(CtxEntityIdentifier ownerCtxId, 
			String type, String value) throws Exception {

		if (LOG.isDebugEnabled())
			LOG.debug("Creating '" + type + "' attribute under entity " + ownerCtxId + " with value '" + value + "'");
		
		final CtxAttribute attr = this.ctxBroker.createAttribute(ownerCtxId, type).get();
		
		if (value != null) {
			attr.setStringValue(value);
			attr.setValueType(CtxAttributeValueType.STRING);
			attr.getQuality().setOriginType(CtxOriginType.MANUALLY_SET);
			this.ctxBroker.update(attr);
		}
	}
	
	private String eventToString(final InternalEvent event) {
		
		final StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append("name=");
		sb.append(event.geteventName());
		sb.append(",");
		sb.append("type=");
		sb.append(event.geteventType());
		sb.append(",");
		sb.append("source=");
		sb.append(event.geteventSource());
		sb.append(",");
		sb.append("info=");
		sb.append(event.geteventInfo());
		sb.append("]");
		
		return sb.toString();
	}
}