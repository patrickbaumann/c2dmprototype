package com.patrickbaumann.pushprototype;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class C2DMReciever extends BroadcastReceiver {
		
	@Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")) {
            handleRegistration(context, intent);
        } else if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
            handleMessage(context, intent);
         }
     }

    private void handleRegistration(Context context, Intent intent) {
        String registration = intent.getStringExtra("registration_id"); 
        if (intent.getStringExtra("error") != null) {
        	Toast.makeText(context, "Registration ERROR", Toast.LENGTH_SHORT).show();
        
        } else if (intent.getStringExtra("unregistered") != null) {
        	Toast.makeText(context, "Registration not active", Toast.LENGTH_SHORT).show();

        } else if (registration != null) {
        	Intent registrationIntent = new Intent(context, WebAppService.class);
        	registrationIntent.setAction(WebAppService.SEND_REGISTRATION_ID);
        	registrationIntent.putExtra(WebAppService.REGISTRATION_ID, registration);
        	Toast.makeText(context, "Registration Recieved"+registration, Toast.LENGTH_SHORT).show();
        	context.startService(registrationIntent);  
        }
    }
    
    public void handleMessage(Context context, Intent intent)
    {
    	String msg = "Message received: " + intent.getStringExtra("message");
    	Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
       

}
