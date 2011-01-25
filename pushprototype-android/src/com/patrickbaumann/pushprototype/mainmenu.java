package com.patrickbaumann.pushprototype;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;


public class mainmenu extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    public void launchSettings(View v)
    {
		startActivity(new Intent(this, Prefs.class));
    }
    
    public void launchLogin(View v)
    {
    	startActivity(new Intent(this, Prefs.class));
    }    

    public void register(View v)
    {
    	Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
    	registrationIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0)); // boilerplate
    	registrationIntent.putExtra("sender", "baumannpat@gmail.com");
    	startService(registrationIntent);
    }    
    
 
}