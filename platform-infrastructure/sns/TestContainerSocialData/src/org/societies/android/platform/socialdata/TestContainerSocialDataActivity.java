package org.societies.android.platform.socialdata;

import org.societies.android.api.internal.sns.ISocialData;
import org.societies.android.platform.socialdata.SocialData;
import org.societies.android.platform.socialdata.SocialData.LocalBinder;
import org.societies.api.internal.sns.ISocialConnector.SocialNetwork;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

public class TestContainerSocialDataActivity extends Activity {

	private static final String LOG_TAG = TestContainerSocialDataActivity.class.getName();
	
    private ISocialData socialData;
    private boolean socialDataConnected = false;
    private TextView text;  
    private TestTask testTask;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        text = (TextView) findViewById(R.id.text);
        
        testTask = new TestTask(this);        
        
        //REGISTER BROADCAST
//        IntentFilter intentFilter = new IntentFilter() ;
//        intentFilter.addAction(SocialTokenManager.GET_TOKEN);        
//        this.getApplicationContext().registerReceiver(new bReceiver(), intentFilter);
        
        //CREATE INTENT FOR SERVICE AND BIND
        Intent intentSocialData = new Intent(this.getApplicationContext(), SocialData.class);
        this.getApplicationContext().bindService(intentSocialData, socialDataConnection, Context.BIND_AUTO_CREATE);
        
        Log.d(LOG_TAG, "Test in progress...");
		text.setText("Test in progress...");
    }
    
    /**
     * IServiceDiscovery service connection
     */
    private ServiceConnection socialDataConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to service");

        	try {
	        	//GET LOCAL BINDER
	            LocalBinder binder = (LocalBinder) service;
	
	            //OBTAIN SERVICE API
	            socialData = (ISocialData) binder.getService();
	            socialDataConnected = true;
	            	        	
	            testTask.execute();
        	} catch (Exception ex) {
        		Log.d(LOG_TAG, "Error binding to service: " + ex.getMessage());
        	}
        }
        
        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from service");
        	socialDataConnected = false;
        }
    };
    
    private class TestTask extends AsyncTask<Void, Void, Void> {
    	
    	private Context context;
    	
    	public TestTask(Context context) {
    		this.context = context;    		
    	}
    	
    	protected Void doInBackground(Void... args) {
    		testAddConnector();
    		
    		return null;
    	}    	
    }

    private void testAddConnector() {
    	
		long testValidity = 1000;
		socialData.addSocialConnector("TestContainerSocialDataActivity", SocialNetwork.Facebook, "testToken", testValidity);		
    }
    
//    private class bReceiver extends BroadcastReceiver  {
//		
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			Log.d(LOG_TAG, intent.getAction());
//			
//			if (intent.getAction().equals(SocialTokenManager.GET_TOKEN)) {
//				String token = intent.getStringExtra(SocialTokenManager.INTENT_RETURN_KEY);
//				Log.d(LOG_TAG, "Token="+token);
//				text.setText("Token="+token);
//			}
//		}
//	};
}