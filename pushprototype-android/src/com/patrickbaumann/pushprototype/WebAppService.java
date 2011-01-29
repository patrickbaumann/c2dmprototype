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


/**	
 * A service intended to interface with the pushprototypedjango applciation.
 */
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
            sendRegistrationId(intent.getStringExtra(REGISTRATION_ID));
        }		
    }

    /** 
     * Sends a registration ID received in response to a c2dm registration request
     * to the pushprototypedjango application. The address to which the id is sent is 
     * stored in shared preferences.
     * 
     * @param registrationId
     * @see Prefs
     */
    private void sendRegistrationId(String registrationId)
    {
        // Get the unique phone id
        String phoneid = Secure.getString(getContentResolver(), Secure.ANDROID_ID); 
        if(phoneid == null){phoneid = "emulator";} // emulator may return null with some APIs
        Log.e("PushPrototype", "UID of phone: " + phoneid);

        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();  
        nameValuePairs.add(new BasicNameValuePair("phoneid", phoneid));    
        nameValuePairs.add(new BasicNameValuePair("registrationid", registrationId));    

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // now let's try to post to the server.    	
        try {
            URI uri = new URI(prefs.getString("webapp_url", "http://192.168.2.111/")+"push/register/");
            HttpPost post = new HttpPost(uri);

            post.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8)); 

            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(post);

            Log.e("PushPrototype", response.getStatusLine().toString());

            // let's store the registrationId to prefs for the time being
            // TODO: find better way to save this (prefs seems misguided)
            prefs.edit().putString("registrationId", registrationId);

        } catch (Exception e) {
            // We may have caught uri parse, connectivity, etc. exception
            // just log it.
            Log.e("PushPrototype", e.getMessage());
        }
    }


}
