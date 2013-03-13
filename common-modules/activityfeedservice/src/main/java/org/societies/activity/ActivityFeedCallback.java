package org.societies.activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.activity.IActivityFeedCallback;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.schema.activityfeed.MarshaledActivityFeed;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ActivityFeedCallback implements ICommCallback {

	
	private final static List<String> NAMESPACES = Collections
			.unmodifiableList( Arrays.asList("http://societies.org/api/schema/activityfeed"));
//								"http://societies.org/api/schema/activity",
						//  		"http://societies.org/api/schema/cis/community"));
			//.singletonList("http://societies.org/api/schema/cis/manager");
	private final static List<String> PACKAGES = Collections
			//.singletonList("org.societies.api.schema.cis.manager");
			.unmodifiableList( Arrays.asList("org.societies.api.schema.activityfeed"));
//					"org.societies.api.schema.activity",
					//"org.societies.api.schema.cis.community"));
	
	
	//private IActivityFeedCallback sourceCallback = null;
	//MAP TO STORE THE ALL THE CLIENT CONNECTIONS
	 private final Map<String, IActivityFeedCallback> clients = new HashMap<String, IActivityFeedCallback>();

	
	private static Logger LOG = LoggerFactory.getLogger(ActivityFeedCallback.class);
	
	public ActivityFeedCallback (String clientId, IActivityFeedCallback sourceCallback) {
		clients.put(clientId, sourceCallback);
	}
	
	/**Returns the correct client callback for this request
	 * @param requestID the id of the initiating request
	 * @return
	 * @throws UnavailableException
	 */
	private IActivityFeedCallback getRequestingClient(String requestID) {
		IActivityFeedCallback requestingClient = (IActivityFeedCallback) clients.get(requestID);
		clients.remove(requestID);
		return requestingClient;
	}
	
	@Override
	public List<String> getXMLNamespaces() {
		// TODO Auto-generated method stub
		return NAMESPACES;
	}

	@Override
	public List<String> getJavaPackages() {
		// TODO Auto-generated method stub
		return PACKAGES;
	}

	@Override
	public void receiveResult(Stanza stanza, Object payload) {
		LOG.info("receive result received");
		// community namespace
		if (payload instanceof MarshaledActivityFeed) {
			LOG.info("Callback with result");
            MarshaledActivityFeed a = (MarshaledActivityFeed) payload ;
			
			if(a.getDeleteActivityResponse() != null)
				LOG.info("Delete Activity Response received and equal to " + a.getDeleteActivityResponse().isResult());
			
			if(a.getAddActivityResponse() != null)
				LOG.info("Add Activity Response received and equal to " + a.getAddActivityResponse().isResult());
			
			if(a.getCleanUpActivityFeedResponse() != null)
				LOG.info("CleanUp Activity Feed Response received and equal to " + a.getCleanUpActivityFeedResponse().getResult());	
			
			if(a.getGetActivitiesResponse() !=null)
				LOG.info("Get Activities Response received");
			
		}
		

		// return callback for all cases
		IActivityFeedCallback callback = this.getRequestingClient(stanza.getId());
		callback.receiveResult((MarshaledActivityFeed)payload);
		//this.sourceCallback.receiveResult((MarshaledActivityFeed)payload);

	}

	@Override
	public void receiveError(Stanza stanza, XMPPError error) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveItems(Stanza stanza, String node, List<String> items) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		// TODO Auto-generated method stub

	}

}
