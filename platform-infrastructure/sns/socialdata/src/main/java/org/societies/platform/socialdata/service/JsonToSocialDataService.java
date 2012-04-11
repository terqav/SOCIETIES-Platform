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
	  
//	  public static void main(String[]args){
//		  //System.out.println("Convert JSON to SocialDATA");
//		  String access_token = "AAAFs43XOj3IBAGbtrA2I7cibWs8YD1ODGr7JiqXl0ZCJ4DBkeXKeSsth9r2EbRGj6jh1eBIhUAkIZBNs1nKOJU1Ys81xKxUqZAC13DwBAZDZD";
//		  ISocialConnector c = new FacebookConnectorImpl(access_token, null);
//		  
//		  
//		  try {
//			
//			String data = c.getUserActivities();
//			
//			int index=0;
//			ActivityConverterFromFacebook parser = new ActivityConverterFromFacebook();
//			
//			List<ActivityEntry> list = parser.load(data);
//			Iterator<ActivityEntry> it = list.iterator();
//			while (it.hasNext()){
//				ActivityEntry entry = it.next();
//				System.out.println("-- "+entry.getActor().getDisplayName() + " made a  "+entry.getVerb() + " ? " + entry.getContent());
//				index++;
//			}
//		
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		  
//	  }
	  
}
