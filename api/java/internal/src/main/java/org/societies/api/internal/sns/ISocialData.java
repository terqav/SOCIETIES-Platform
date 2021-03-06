package org.societies.api.internal.sns;

import java.util.List;
import java.util.Map;

import org.societies.api.schema.sns.socialdata.model.SocialNetwork;
import org.societies.api.sns.ISocialDataExternal;

public interface ISocialData extends ISocialDataExternal{

	// verbs
	public static final String POST = "post";
	public static final String TAG = "tag";
	public static final String UPDATE = "update";
	public static final String LIKE = "like";
	public static final String SHARE = "share";
	public static final String MAKE_FRIEND = "make-friend";
	public static final String ATTEND = "attend";

	// Object types
	public static final String NOTE = "note";
	public static final String IMAGE = "image";
	public static final String PERSON = "person";
	public static final String BOOKMARK = "bookmark";
	public static final String COMMENT = "comment";
	public static final String EVENT = "event";
	public static final String QUESTION = "question";
	public static final String COLLECTION = "collection";
	public static final String PLACE = "places";
	public static final String CHECKIN = "checkin";	

	/**
     * Add a new social connector to fetch data from a specific Social network
     * @param socialConnector Interface of the specific connector
     * @throws Exception 
     */
    void addSocialConnector(ISocialConnector social) throws Exception;
    
    /**
     * Remove a social connector by his unique ID
     * @param connectorId
     * @throws Exception 
     */
    void removeSocialConnector(String connectorId) throws Exception;
    
    /**
     * Remove a social connector by his unique ID
     * @param connectorId
     * @throws Exception 
     */
    void removeSocialConnector(ISocialConnector connector) throws Exception;
    
    /**
     * Provide a list of the available social connector
     * @return List of Social Connector
     */
    List<ISocialConnector> getSocialConnectors();
    
    /**
     * Provide the list of Profiles coming from different social Networks
     * @return Object that should be cast as  org.apache.shindig.social.opensocial.model.Person;
     */
    List<?> getSocialProfiles();
    
    /**
     * Provide a list of Person Object extracted from all the connectors
     * @return List of Object that should be cast as  org.apache.shindig.social.opensocial.model.Person;
     */
    List<?> getSocialPeople();
    
    
    /**
     * Provide the list of activity generated by the users in the social network.
     * @return List of Object that should be cast as  org.apache.shindig.social.opensocial.model.ActivityEntry;
     */
    List<?> getSocialActivity();
    
    /**
     * Provide a list of Groups generated in the social network
     * @return List of Object that should be cast as  org.apache.shindig.social.opensocial.model.Group;
     */
    List<?> getSocialGroups();

    /**
     * Requeire the component to fetch from all active connector fetching data from the social networks
     */
    void updateSocialData();
    
    /**
     * Get last upated session
     */
    long getLastUpdate();
    
     /**
      * Generate an connector based on the name of the social Network and the parameters passed
      * @param socialNetworkName
      * @param params  to generate correctly the connector (Must be present the token)
      * @return the implementation of the spicific social connector castest as the interfeace ISocialConnector
      */
    ISocialConnector createConnector(SocialNetwork socialNetworkName, Map<String, String> params);
    
    
    /**
     * Check if the specific connector is available
     * @param connector Connetor instance
     * @return boolean
     */
    boolean isAvailable(ISocialConnector connector);
    
    
//    /**
//     * The Method post a message (String) to a specifc SocialNetwork
//     * @param socialNetworkName  the name of the Social Network [Facebook, Twitter, Foursquare]
//     * @param message the String to be posted
//     */
//    
//    void postMessage(ISocialConnector.SocialNetwork socialNetworkName, String message);
//    
//   /**
//    * This Method allow to post on a specific Social network more than a simple string, like events or checkin. 
//    * the data required are put in the MAP <key, value> in order to be correctly processed 
//    * 
//    * @param socialNetwork to send the data	
//    * @param data a MAP<String, Data> to instruct the connector to post the data on the SN
//    */
//    void postData(ISocialConnector.SocialNetwork socialNetwork, Map<String,?> data);
    
}