package com.patrickbaumann.pushprototype;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;



/**	
 * A service intended to interface with the pushprototypedjango applciation.
 */
public class WebAppService extends IntentService {
    public static final String SEND_REGISTRATION_ID = "send_reg_id";
    public static final String REGISTRATION_ID = "registration_id";

    public static final String SEND_AUDIO = "send_audio_message";
    public static final String AUDIO_FILE_NAME = "audio_file";
    
    
    // need a handler class for posting Toast messages as the spawning
    // threads are being destroyed before the message can be shown
    public Handler toastHandler;
    
    private class ToastText implements Runnable{
        private String text;
        public ToastText(String message)
        {
            text = message;
        }
        @Override
        public void run() {
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
        }
        
    }
    
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        toastHandler = new Handler(); // creating handler on main thread so
                                      // that Toast is called on main thread

    }
    
    
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
        else if(intent.getAction().equals(SEND_AUDIO))
        {
            sendAudio(intent.getStringExtra(AUDIO_FILE_NAME));
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

        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();  
        nameValuePairs.add(new BasicNameValuePair("phoneid", getPhoneId()));    
        nameValuePairs.add(new BasicNameValuePair("registrationid", registrationId));    

        // now let's try to post to the server.    	
        try {
            URI uri = new URI(getWebAppUrl()+"push/register/");
            HttpPost post = new HttpPost(uri);

            post.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8)); 

            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(post);
            
            verifyHttpResponseOk(response);
        } catch (Exception e) {
            toastHandler.post(new ToastText(e.getMessage()));
            Log.e("PushPrototype", e.getMessage());
        }
    }

    private void verifyHttpResponseOk(HttpResponse response) throws Exception {
        if(response.getStatusLine().getStatusCode() != 200)
        {
            throw new Exception(response.getStatusLine().toString());
        }
        Log.e("PushPrototype", response.getStatusLine().toString());
    }

    private String getWebAppUrl() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString("webapp_url", "http://192.168.2.111/");
    }

    private String getPhoneId() {
        String phoneid = Secure.getString(getContentResolver(), Secure.ANDROID_ID); 
        if(phoneid == null){phoneid = "emulator";} // emulator may return null with some APIs
        Log.e("PushPrototype", "UID of phone: " + phoneid);
        return phoneid;
    }
    
    private void sendAudio(String audioFileName)
    {
        // let's try to post to the server.     
        try {
            URI uri = new URI(getWebAppUrl()+"push/message/");
            HttpPost post = new HttpPost(uri);
            
            List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();  
            nameValuePairs.add(new BasicNameValuePair("phoneid", getPhoneId()));    
            
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            StringBody phoneid = new StringBody(getPhoneId());
            entity.addPart("phoneid", phoneid);
            
            File audioFile = new File(audioFileName);
            FileBody audioBin = new FileBody(audioFile);
            entity.addPart("audio", audioBin);
            
            post.setEntity(entity);
            
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(post);

            verifyHttpResponseOk(response);
            toastHandler.post(new ToastText("Audio successfully sent!"));
            
        
        } catch (Exception e) {
            toastHandler.post(new ToastText(e.getMessage()));
            Log.e("PushPrototype", e.getMessage());
        }
    }
}
