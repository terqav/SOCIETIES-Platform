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
package org.societies.privacytrust.trust.impl.evidence.repo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.privacytrust.trust.model.TrustedEntityId;
import org.societies.privacytrust.trust.api.evidence.model.IDirectTrustEvidence;
import org.societies.privacytrust.trust.api.evidence.model.IIndirectTrustEvidence;
import org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence;
import org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository;
import org.societies.privacytrust.trust.api.evidence.repo.TrustEvidenceRepositoryException;
import org.societies.privacytrust.trust.impl.evidence.repo.model.DirectTrustOpinion;
import org.societies.privacytrust.trust.impl.evidence.repo.model.TrustEvidence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.8
 */
@Repository
public class TrustEvidenceRepository implements ITrustEvidenceRepository {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(TrustEvidenceRepository.class);
	
	@Autowired
	private SessionFactory sessionFactory;
	
	TrustEvidenceRepository() {
		
		LOG.info(this.getClass() + " instantiated");
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository#addEvidence(org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence)
	 */
	@Override
	public void addEvidence(ITrustEvidence evidence)
			throws TrustEvidenceRepositoryException {
		
		if (evidence == null)
			throw new NullPointerException("evidence can't be null");
		
		final Session session = sessionFactory.openSession();
		final Transaction transaction = session.beginTransaction();
		try {
			if (LOG.isDebugEnabled())
				LOG.debug("Adding trust evidence " + evidence + " to the Trust Evidence Repository...");
	
			session.save(evidence);
			session.flush();
			transaction.commit();
		} catch (Exception e) {
			LOG.warn("Rolling back transaction for trust evidence " + evidence);
			transaction.rollback();
			throw new TrustEvidenceRepositoryException("Could not add evidence " + evidence, e);
		} finally {
			if (session != null)
				session.close();
		}
	}

	/*
	 * @see org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository#retrieveAllDirectEvidence(org.societies.api.internal.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public Set<IDirectTrustEvidence> retrieveAllDirectEvidence(
			TrustedEntityId teid) throws TrustEvidenceRepositoryException {
		
		if (teid == null)
			throw new NullPointerException("teid can't be null");
		
		final Set<IDirectTrustEvidence> result = new HashSet<IDirectTrustEvidence>();
		result.addAll(this.retrieve(teid, DirectTrustOpinion.class));
		
		return result;
	}

	/*
	 * @see org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository#retrieveDirectEvidence(org.societies.api.internal.privacytrust.trust.model.TrustedEntityId, java.util.Date, java.util.Date)
	 */
	@Override
	public Set<IDirectTrustEvidence> retrieveDirectEvidence(
			TrustedEntityId teid, Date startDate, Date endDate)
			throws TrustEvidenceRepositoryException {
		// TODO Auto-generated method stub
		if (teid == null)
			throw new NullPointerException("teid can't be null");
		
		return null;
	}

	/*
	 * @see org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository#retrieveAllIndirectEvidence(org.societies.api.internal.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public Set<IIndirectTrustEvidence> retrieveAllIndirectEvidence(
			TrustedEntityId teid) throws TrustEvidenceRepositoryException {
		// TODO Auto-generated method stub
		if (teid == null)
			throw new NullPointerException("teid can't be null");
		
		return null;
	}

	/*
	 * @see org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository#retrieveIndirectEvidence(org.societies.api.internal.privacytrust.trust.model.TrustedEntityId, java.util.Date, java.util.Date)
	 */
	@Override
	public Set<IIndirectTrustEvidence> retrieveIndirectEvidence(
			TrustedEntityId teid, Date startDate, Date endDate)
			throws TrustEvidenceRepositoryException {
		// TODO Auto-generated method stub
		if (teid == null)
			throw new NullPointerException("teid can't be null");
		
		return null;
	}

	/*
	 * @see org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository#removeAllDirectEvidence(org.societies.api.internal.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public Set<IDirectTrustEvidence> removeAllDirectEvidence(
			TrustedEntityId teid) throws TrustEvidenceRepositoryException {
		// TODO Auto-generated method stub
		if (teid == null)
			throw new NullPointerException("teid can't be null");
		
		return null;
	}

	/*
	 * @see org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository#removeDirectEvidence(org.societies.api.internal.privacytrust.trust.model.TrustedEntityId, java.util.Date, java.util.Date)
	 */
	@Override
	public Set<IDirectTrustEvidence> removeDirectEvidence(TrustedEntityId teid,
			Date startDate, Date endDate)
			throws TrustEvidenceRepositoryException {
		// TODO Auto-generated method stub
		if (teid == null)
			throw new NullPointerException("teid can't be null");
		
		return null;
	}

	/*
	 * @see org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository#removeAllIndirectEvidence(org.societies.api.internal.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public Set<IIndirectTrustEvidence> removeAllIndirectEvidence(
			TrustedEntityId teid) throws TrustEvidenceRepositoryException {
		// TODO Auto-generated method stub
		if (teid == null)
			throw new NullPointerException("teid can't be null");
		
		return null;
	}

	/*
	 * @see org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository#removeIndirectEvidence(org.societies.api.internal.privacytrust.trust.model.TrustedEntityId, java.util.Date, java.util.Date)
	 */
	@Override
	public Set<IIndirectTrustEvidence> removeIndirectEvidence(
			TrustedEntityId teid, Date startDate, Date endDate)
			throws TrustEvidenceRepositoryException {
		// TODO Auto-generated method stub
		if (teid == null)
			throw new NullPointerException("teid can't be null");
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private <T extends TrustEvidence> Set<T> retrieve(
			final TrustedEntityId teid, final Class<T> evidenceClass) throws TrustEvidenceRepositoryException {
		
		final Set<T> result = new HashSet<T>();
		
		final Session session = sessionFactory.openSession();
		result.addAll(session.createCriteria(evidenceClass)
			.add(Restrictions.eq("teid", teid))
			.list());
		if (session != null)
			session.close();
			
		return result;
	}
}