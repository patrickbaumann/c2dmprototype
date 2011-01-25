package com.patrickbaumann.pushprototype;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;

public class WebAppService extends IntentService {
	public static final String SEND_REGISTRATION_ID = "send_reg_id";
	public static final String REGISTRATION_ID = "registration_id";
	
	public WebAppService()
	{
		super("WebAppService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		handleIntent(intent);
	}
	
	public void handleIntent(Intent intent)
	{
		if(intent.getAction().equals(SEND_REGISTRATION_ID))
		{
			sendRegistrationId(intent.getStringExtra(REGISTRATION_ID), intent);
		}		
	}
	
	public void sendRegistrationId(String registrationId, Intent intent)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

    	List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();  
        
        String phoneid = Secure.getString(getContentResolver(), Secure.ANDROID_ID); 
        Log.e("PushPrototeyp", phoneid);
        if(phoneid == null){phoneid = "emulator";}            
        nameValuePairs.add(new BasicNameValuePair("phoneid", phoneid));    
    	nameValuePairs.add(new BasicNameValuePair("registrationid", registrationId));    
    	
    	try {
			URI uri = new URI(prefs.getString("webapp_url", "http://192.168.2.111/")+"push/register/");
			HttpPost post = new HttpPost(uri);
	        post.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8)); 
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpResponse response = httpclient.execute(post);
	        Log.e("PushPrototype", response.getStatusLine().toString());
			prefs.edit().putString("registrationId", registrationId);
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("PushPrototype", e.getMessage());
		}
	}


}
