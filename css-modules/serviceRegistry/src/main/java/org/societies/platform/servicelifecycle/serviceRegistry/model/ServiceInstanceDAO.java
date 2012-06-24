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
package org.societies.platform.servicelifecycle.serviceRegistry.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Describe your class here...
 *
 * @author solutanet
 *
 */
@Entity
@Table(name = "ServiceInstanceEntry")
public class ServiceInstanceDAO {
	
	private long id;
	private String fullJid;
	private String cssJid;
	private String XMPPNode;
	private ServiceImplementationDAO serviceImpl;
	
	/**
	 
	 * @param fullJid
	 * @param xMPPNode
	 * @param serviceImpl
	 */
	public ServiceInstanceDAO(String fullJid, String cssJid, String xMPPNode,
			ServiceImplementationDAO serviceImpl) {
		super();
		
		this.fullJid = fullJid;
		this.cssJid = cssJid;
		this.XMPPNode = xMPPNode;
		this.serviceImpl = serviceImpl;
	}
	
	/**
	 * 
	 */
	public ServiceInstanceDAO() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Column(name = "FullJid")
	public String getFullJid() {
		return fullJid;
	}
	public void setFullJid(String fullJid) {
		this.fullJid = fullJid;
	}
	@Column(name = "CssJid")
	public String getCssJid() {
		return cssJid;
	}
	public void setCssJid(String cssJid) {
		this.cssJid = cssJid;
	}
	@Column(name = "XMPPNode")
	public String getXMPPNode() {
		return XMPPNode;
	}
	public void setXMPPNode(String xMPPNode) {
		XMPPNode = xMPPNode;
	}
	@OneToOne(cascade=CascadeType.ALL)
	
	public ServiceImplementationDAO getServiceImpl() {
		return serviceImpl;
	}
	public void setServiceImpl(ServiceImplementationDAO serviceImpl) {
		this.serviceImpl = serviceImpl;
	}
	@Id
	@GeneratedValue
	@Column(name="id")
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	

	
	
}
