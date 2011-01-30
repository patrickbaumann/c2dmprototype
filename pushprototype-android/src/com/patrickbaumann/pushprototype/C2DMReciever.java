package com.patrickbaumann.pushprototype;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * This BroadcastReciever is filtered to only receive c2dm-related messages and delegate
 * them to the appropriate activity / service.
 */
public class C2DMReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")) {
            // we've received a registration response
            handleRegistration(context, intent);
        } else if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
            // we've received a push notification w/ paylaod
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
            Toast.makeText(context, "Registration Recieved: "+registration, Toast.LENGTH_SHORT).show();

            // the registration was successful, package an intent to send to the webappservice
            Intent registrationIntent = new Intent(context, WebAppService.class);
            registrationIntent.setAction(WebAppService.SEND_REGISTRATION_ID);
            registrationIntent.putExtra(WebAppService.REGISTRATION_ID, registration);
            context.startService(registrationIntent);  
        }
    }

    public void handleMessage(Context context, Intent intent)
    {
        // we've received a message, simply post to user via Toast
        String msg = "Message ID pushed: " + intent.getStringExtra("message");
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

        // let's initiate the downloading of the message!
        Intent messageIntent = new Intent(context, WebAppService.class);
        messageIntent.setAction(WebAppService.GET_MESSAGE);
        messageIntent.putExtra(WebAppService.MESSAGE_ID, intent.getStringExtra("message"));
        context.startService(messageIntent);  
    }
}
