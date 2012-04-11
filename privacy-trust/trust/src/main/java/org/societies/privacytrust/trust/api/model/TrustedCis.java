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
package org.societies.privacytrust.trust.api.model;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * This class represents trusted CISs. A <code>TrustedCis</code> object is
 * referenced by its {@link TrustedEntityId}, while the associated {@link Trust}
 * value objects express the trustworthiness of this community, i.e. direct, 
 * indirect and user-perceived. Each trusted CIS is assigned a set of 
 * {@link TrustedCss} objects, which represent its members.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.1
 */
@Entity
@Table(name="t_communities")
public class TrustedCis extends TrustedEntity {

	private static final long serialVersionUID = -438368876927927076L;
	
	/** The members of this trusted CIS. */
	private Set<TrustedCss> members = new CopyOnWriteArraySet<TrustedCss>();
	
	/* TODO The services shared by this trusted CIS. */
	// private Set<TrustedService> services = new CopyOnWriteArraySet<TrustedService>();

	/**
	 * @param trustor
	 * @param teid
	 */
	public TrustedCis(TrustedEntityId trustor, TrustedEntityId teid) {
		
		super(trustor, teid);
	}

	/**
	 * Returns a set containing the members of this community.
	 * 
	 * @return a set containing the members of this community.
	 */
	public Set<TrustedCss> getMembers() {
		
		return this.members;
	}
	
	/**
	 * Adds the specified trusted individual to the members of this community.
	 * 
	 * @param member
	 *            the trusted individual to add to the members of this community
	 * @since 0.0.3
	 */
	public void addMember(final TrustedCss member) {
		
		if (!this.members.contains(member))
			this.members.add(member);
		
		if (!member.getCommunities().contains(this))
			member.getCommunities().add(this);
	}
	
	/**
	 * Removes the specified trusted individual from the members of this community.
	 * 
	 * @param member
	 *            the trusted individual to remove from the members of this community
	 * @since 0.0.3
	 */
	public void removeMember(final TrustedCss member) {
		
		if (this.members.contains(member))
			this.members.remove(member);
		
		if (member.getCommunities().contains(this))
			member.getCommunities().remove(this);
	}

	/*
	 * TODO			
	 *
	public Set<TrustedService> getServices() {
		return this.services;
	}

	/**
	 * 
	 * @param s
	 *
	public Set<TrustedService> getServices(String serviceType) {
		return null;
	}*/
}