package org.societies.platform.socialdata.service;

import org.json.JSONObject;



public class JsonToSocialDataService {

	 /**
	   * The DB
	   */
	  private JSONObject db;
	  
	  /**
	   * Allows access to the underlying json db.
	   *
	   * @return a reference to the json db
	   */
	  public JSONObject getDb() {
	    return db;
	  }
	  
	  /**
	   * override the json database
	   * @param db a {@link org.json.JSONObject}.
	   */
	  public void setDb(JSONObject db) {
	    this.db = db;
	  }
	  
	  public static void main(String[]args){
//		  System.out.println("Convert JSON to SocialDATA");
//		  String access_token = "AAAFPIhZAkC90BAJy6bV7hnRJcBs3VZAmr4mtSrdJpszhXO6ZAwNdQfSZAZCDx3VLQql84NefBBp11IrnZCUFGP9H731m4K0RoZCMzQbvZCIcZAAZDZD";
//		  ISocialConnector c = new FacebookConnectorImpl(access_token, null);
//		  
//		  JsonToSocialDataService service = new JsonToSocialDataService();
//		  try {
//			
//			service.setDb(new JSONObject(c.getSocialData(FacebookConnector.ME)));
//			PersonConverterFromFacebook parser = new PersonConverterFromFacebook();
//			
//			Person p = parser.load(service.getDb());
//			
//			System.out.println("abaut_me:" + p.getAboutMe());
//			System.out.println("JSON:" + service.getDb().toString(0));
//			
//		  } catch (JSONException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		  
	  }
	  
}
