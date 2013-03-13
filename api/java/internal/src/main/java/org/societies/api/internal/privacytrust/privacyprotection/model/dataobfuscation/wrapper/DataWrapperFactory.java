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
package org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.SimpleDataIdentifier;
import org.societies.api.schema.activity.MarshaledActivity;
import org.societies.api.schema.identity.DataIdentifierScheme;


/**
 * Utilities to instantiate DataWrapper for data obfuscation
 * @state skeleton 
 * @author olivierm
 * @date 14 oct. 2011
 */
public class DataWrapperFactory {
	// -- CONTEXT ATTRIBUTE
	/**
	 * To select automatically the relevant DataWrapper
	 * for this ContextAttribute
	 * 
	 * @param contextData
	 * @return A relevant DataWrapper
	 * @throw RuntimeException
	 */
	public static IDataWrapper selectDataWrapper(CtxAttribute contextData) throws RuntimeException{
		if (null == contextData || null == contextData.getId() || null == contextData.getId().getType()) {
			return null;
		}
		if (CtxAttributeTypes.LOCATION_COORDINATES.equals(contextData.getId().getType())) {
			return getLocationCoordinatesWrapper(contextData.getDoubleValue(), 0, 0);
		}
		return null;
	}


	// -- GEOLOCATION

	/**
	 * To get a LocationCoordinatesWrapper
	 * The persistence is disabled by default, the obfuscated geolocation will not
	 * be stored after obfuscation.
	 * @param latitude Latitude
	 * @param longitude Longitude
	 * @param accuracy Accuracy in meters
	 * @return A LocationCoordinatesWrapper
	 */
	public static IDataWrapper<LocationCoordinates> getLocationCoordinatesWrapper(double latitude, double longitude, double accuracy) {
		SimpleDataIdentifier dataId = new SimpleDataIdentifier();
		dataId.setScheme(DataIdentifierScheme.CONTEXT);
		dataId.setType(CtxAttributeTypes.LOCATION_COORDINATES);
		LocationCoordinates data = new LocationCoordinates(latitude, longitude, accuracy);
		return new DataWrapper<LocationCoordinates>(dataId, data);
	}

	/**
	 * To get the Postal location wrapper
	 * @param logicalName
	 * @param streetNumber
	 * @param streetName
	 * @param district
	 * @param town
	 * @param postalCode
	 * @param department
	 * @param region
	 * @param country
	 * @param continent
	 * @param planet
	 * @return
	 */
	public static IDataWrapper<PostalLocation> getPostalLocationWrapper(String logicalName, String streetNumber,
			String streetName, String district, String town, String postalCode,
			String department, String region, String country, String continent,
			String planet) {
		SimpleDataIdentifier dataId = new SimpleDataIdentifier();
		dataId.setScheme(DataIdentifierScheme.CONTEXT);
		dataId.setType(CtxAttributeTypes.LOCATION_SYMBOLIC);
		PostalLocation data = new PostalLocation(logicalName, streetNumber, streetName, district, town, postalCode, department, region, country, continent, planet);
		return new DataWrapper<PostalLocation>(dataId, data);
	}


	// -- NAME

	/**
	 * To get a NameWrapper
	 * The persistence is disabled by default, the obfuscated name will not
	 * @param firstName
	 * @param lastName
	 * @return the NameWrapper
	 */
	public static IDataWrapper<Name> getNameWrapper(String firstName, String lastName) {
		SimpleDataIdentifier dataId = new SimpleDataIdentifier();
		dataId.setScheme(DataIdentifierScheme.CONTEXT);
		dataId.setType(CtxAttributeTypes.NAME);
		Name data = new Name(firstName, lastName);
		return new DataWrapper<Name>(dataId, data);
	}


	// -- TEMPERATURE

	/**
	 * To get a TemperatureWrapper
	 * The persistence is disabled by default
	 * @param degree Degree in number value
	 * @return the Temperature wrapper
	 */
	public static IDataWrapper<Temperature> getTemperatureWrapper(double degree) {
		SimpleDataIdentifier dataId = new SimpleDataIdentifier();
		dataId.setScheme(DataIdentifierScheme.CONTEXT);
		dataId.setType(CtxAttributeTypes.TEMPERATURE);
		Temperature data = new Temperature(degree);
		return new DataWrapper<Temperature>(dataId, data);
	}
	/**
	 * To get a TemperatureWrapper
	 * The persistence is disabled by default
	 * @param degree Degree in string value @see {@value org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.Temperature#heatString}
	 * @return the Temperature wrapper
	 */
	public static IDataWrapper<Temperature> getTemperatureWrapper(String degree) {
		SimpleDataIdentifier dataId = new SimpleDataIdentifier();
		dataId.setScheme(DataIdentifierScheme.CONTEXT);
		dataId.setType(CtxAttributeTypes.TEMPERATURE);
		Temperature data = new Temperature(degree);
		return new DataWrapper<Temperature>(dataId, data);
	}


	// -- ACTION

	/**
	 * To get a ActivityWrapper
	 * @param actor
	 * @param verb
	 * @param object
	 * @return
	 */
	public static IDataWrapper<MarshaledActivity> getActivityWrapper(String actor, String verb, String object) {
		SimpleDataIdentifier dataId = new SimpleDataIdentifier();
		dataId.setScheme(DataIdentifierScheme.CONTEXT);
		dataId.setType(CtxAttributeTypes.ACTION);
        MarshaledActivity data = new MarshaledActivity();
		data.setActor(actor);
		data.setVerb(verb);
		data.setObject(object);
		return new DataWrapper<MarshaledActivity>(dataId, data);
	}


	// -- STATUS

	/**
	 * 
	 * @param status
	 * @return
	 */
	public static IDataWrapper<Status> getStatusWrapper(String status) {
		SimpleDataIdentifier dataId = new SimpleDataIdentifier();
		dataId.setScheme(DataIdentifierScheme.CONTEXT);
		dataId.setType(CtxAttributeTypes.STATUS);
		Status data = new Status(status);
		return new DataWrapper<Status>(dataId, data);
	}
}
