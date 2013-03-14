package org.societies.activity;

import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeedCallback;
import org.societies.api.activity.IActivityFeedManager;
import org.societies.api.schema.activityfeed.AddActivityResponse;
import org.societies.api.schema.activityfeed.CleanUpActivityFeedResponse;

import java.util.concurrent.Callable;

    /**
     * Created with IntelliJ IDEA.
     * User: epic
     * Date: 3/13/13
     * Time: 12:49
     * To change this template use File | Settings | File Templates.
     */
    public class ActivityFeedWorker implements Callable<Void>  {
        final IActivityFeedCallback c;
        final ActivityFeed activityFeed;
        public ActivityFeedWorker(final IActivityFeedCallback c, final ActivityFeed activityFeed) {
            this.c = c;
            this.activityFeed = activityFeed;
        }

        @Override
        public Void call() throws Exception {

            return null;
        }
    public void asyncAddActivity(final IActivity activity){
        Thread t = new Thread(new Runnable(){

            @Override
            public void run() {
                org.societies.api.schema.activityfeed.MarshaledActivityFeed result = new org.societies.api.schema.activityfeed.MarshaledActivityFeed();
                AddActivityResponse r = new AddActivityResponse();

                activityFeed.addActivity(activity);
                r.setResult(true); //TODO. add a return on the activity feed method


                result.setAddActivityResponse(r);
                c.receiveResult(result);
            }
        });
        t.start();

    }
        public void asyncCleanupFeed(){
            org.societies.api.schema.activityfeed.MarshaledActivityFeed result = new org.societies.api.schema.activityfeed.MarshaledActivityFeed();
            CleanUpActivityFeedResponse r = new CleanUpActivityFeedResponse();

            r.setResult(this.cleanupFeed(criteria));


            result.setCleanUpActivityFeedResponse(r);
            c.receiveResult(result);
        }
}
