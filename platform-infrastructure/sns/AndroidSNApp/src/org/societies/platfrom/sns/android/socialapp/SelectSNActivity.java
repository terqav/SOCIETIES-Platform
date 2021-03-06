package org.societies.platfrom.sns.android.socialapp;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class SelectSNActivity extends Activity {
	
	
	
	
	
	
	Button fb, tw, fq, lk;
   
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ssolib_main);
        
        
       	fb = (Button)findViewById(R.id.fb_connector);
       	tw = (Button)findViewById(R.id.tw_connector);
       	fq = (Button)findViewById(R.id.fq_connector);
        lk = (Button)findViewById(R.id.lk_connector);
        
        fb.setOnClickListener(listener);
        fq.setOnClickListener(listener);
        tw.setOnClickListener(listener);
        lk.setOnClickListener(listener);
                
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.v(Constants.DEBUG_TAG, "onResume");
		super.onResume();		
	}
	
	
	
	OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			switch(v.getId()){
				
			   case R.id.fb_connector:
					openBrowser(Constants.FB_URL, Constants.FB_CODE);
					break;
				
				case R.id.fq_connector: openBrowser(Constants.FQ_URL, Constants.FQ_CODE);
						break;
				
				case R.id.tw_connector: openBrowser(Constants.TW_URL, Constants.TW_CODE);
					break;
					
					
				case R.id.lk_connector: openBrowser(Constants.LK_URL, Constants.LK_CODE);
					break;
				}
			
		}
	};
	
	
	private void openBrowser(String uri, int requestCode){
		Intent openB = new Intent(this, WebActivity.class);
		openB.putExtra(Constants.SSO_URL, uri);
		startActivityForResult(openB, requestCode);
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	
		// TODO Auto-generated method stub
		Log.v(Constants.DEBUG_TAG, "onActivityResult: " + resultCode);
		//super.onActivityResult(requestCode, resultCode, data);
		
		
			
			String token = data.getStringExtra(Constants.ACCESS_TOKEN);
			if (requestCode == Constants.FB_CODE){
				
				openDialog(token, "Facebook");
				
			}
			else if(requestCode == Constants.TW_CODE){
				openDialog(token, "Twitter");
			}
			else if(requestCode == Constants.FQ_CODE){
				openDialog(token, "Foursquare");
			}
			
			else if(requestCode == Constants.LK_CODE){
				openDialog(token, "Linkedin");
			}
		
	}
	
	
	
	private void openDialog(String value, String sn){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(value)
			   .setTitle(sn)
		       .setCancelable(false)
		       .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   Toast.makeText(SelectSNActivity.this, "Not Implementes", Toast.LENGTH_LONG).show();
		           }
		       })
		       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       }).create().show();
		
		
	}
	
	
}