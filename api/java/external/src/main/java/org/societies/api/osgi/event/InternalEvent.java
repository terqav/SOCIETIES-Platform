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
package org.societies.api.osgi.event;

import java.io.Serializable;

/**
 * The Class InternalEvent.
 *
 * @author pkuppuud
 */
public class InternalEvent implements Serializable {

	/** Default. */
	private static final long serialVersionUID = 1L;
	
	/** The event type. */
	private String eventType;
	
	/** The event name. */
	private String eventName;
	
	/** The event source. */
	private String eventSource;
	
	/** The event info. */
	private Serializable eventInfo;
	
	/**
	 * Constructor.
	 *
	 * @param eventType String for the type of event {@link EventTypes}
	 * @param eventName String for the name of event
	 * @param eventSource String for the source component or peer id
	 * @param eventInfo object for the event info. Must implement {@link Serializable}
	 */
	public InternalEvent(String eventType, String eventName, String eventSource, Serializable eventInfo){
		this.eventType = eventType;
		this.eventName = eventName;
		this.eventSource = eventSource;
		this.eventInfo = eventInfo;
	}

	/**
	 * Gets the event info.
	 *
	 * @return the event info
	 */
	public Object geteventInfo(){
		return eventInfo;
	}

	/**
	 * Gets the event source.
	 *
	 * @return the event source
	 */
	public String geteventSource(){
		return eventSource;
	}

	/**
	 * Gets the event name.
	 *
	 * @return the event name
	 */
	public String geteventName(){
		return eventName;
	}

	/**
	 * Gets the event type.
	 *
	 * @return the event type
	 */
	public String geteventType(){
		return eventType;
	}
}
