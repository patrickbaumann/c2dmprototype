package com.patrickbaumann.pushprototype;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
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