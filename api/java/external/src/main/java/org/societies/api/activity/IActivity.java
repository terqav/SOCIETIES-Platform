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
package org.societies.api.activity;

import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;


/**
 * This interface is mainly a container for passing the parameter
 * Activity into the {@link IActivityFeed} calls. This interface
 * only provides getters and setters to the activity container.
 *
 * To illustrate the usage of this interface, I will use an example througout the documentation:
 * "Jack says 'hi' to Jane" (and this activity was published 00:00:00 01 january 1970 UTC)
 *
 * For more information of the standard this interface represents a subset of, see:
 * http://activitystrea.ms/specs/json/1.0/
 * 
 * @author      Babak.Farshchian@sintef.no
 * @author      Bjørn Magnus
 * @author      Thomas Vilarinho
 * 
 */


@SocietiesExternalInterface(type = SocietiesInterfaceType.PROVIDED)
public interface IActivity {
    /**
     * This method returns the ID of the activity.
     * This ID is not of any use outside the internal functions in activityfeed, it is autogenerated per activityfeed database table.
     *
     * e.g. "129317741200"
     *
     * @return {@link Long} the ID.
     */
	public Long getId();
    /**
     * This method returns the verb of the activity
     *
     * e.g. "says"
     *
     * @return {@link String} the ID.
     */
	public String getVerb();

    /**
     * This methods lets you set the verb of the activity
     *
     * e.g. "says"
     *
     * @param {@link String} verb
     */
	public void setVerb(String verb);

    /**
     * This method returns the actor of the activity
     *
     * e.g. "Jack"
     *
     * @return {@link String}
     */
	public String getActor();

    /**
     * This method lets you set the actor of the activity
     *
     * e.g. "Jack"
     *
     * @param {@link String} actor
     */
	public void setActor(String actor);

    /**
     * This method returns the object of the activity
     *
     * e.g. "hi"
     *
     * @return {@link String}
     */
	public String getObject();

    /**
     * This method lets you set the object of the activity
     *
     * e.g. "hi"
     *
     * @param {@link String} object
     */
	public void setObject(String object);

    /**
     * This method returns the target of the activity
     *
     * e.g. "Jane"
     *
     * @return {@link String}
     */
	public String getTarget();

    /**
     * This method lets you set the target of the activity
     *
     * e.g. "Jane"
     *
     * @param {@link String} target
     */
	public void setTarget(String target);

    /**
     * This method returns the time of publication of the activity in ms since unix epoch represented as a string.
     *
     * e.g. "0"
     *
     * @return {@link String}
     */
	public String getPublished();

    /**
     * This method is not of any practical use for API/3rd party service developers, this method is only for use within the activityfeed implementation, and may be refactored at any moment of time.
     * This method lets you set the time of publication of the activity in ms since unix epoch represented as a string.
     *
     * e.g. "0"
     *
     * @return {@link String}
     */
    @Deprecated
	public void setPublished(String published);
}
