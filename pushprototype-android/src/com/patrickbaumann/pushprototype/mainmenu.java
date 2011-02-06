package com.patrickbaumann.pushprototype;

import java.io.FileInputStream;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


public class mainmenu extends Activity {

    public class GpsLocationListener implements LocationListener
    {
        @Override
        public void onLocationChanged(Location location) {
            lat = location.getLatitude();
            lon = location.getLongitude();
        }
        @Override
        public void onProviderDisabled(String provider) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }
    
    MediaRecorder recorder = new MediaRecorder();
    MediaPlayer player = new MediaPlayer();
    boolean isRecording = false;
    double lat = 0;
    double lon = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        LocationManager locManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new GpsLocationListener());
    }

    @Override
    protected void onStart() {
        super.onStart();
        
    }
    
    public void launchSettings(View v)
    {
        startActivity(new Intent(this, Prefs.class));
    }

    public void register(View v)
    {
        Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
        registrationIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0)); // boilerplate
        registrationIntent.putExtra("sender", getString(R.string.sender_address));
        startService(registrationIntent);
    }
    
    public void login(View v)
    {
        // login to the django server, package an intent to send to the webappservice
        Intent loginIntent = new Intent(this, WebAppService.class);
        loginIntent.setAction(WebAppService.WEBAPP_LOGIN);
        loginIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0)); // boilerplate
        loginIntent.putExtra("user", getString(R.string.webapp_user));
        loginIntent.putExtra("password", getString(R.string.webapp_password));
        startService(loginIntent);
    }
    
    public void sendAudio(View v)
    {
        // the registration was successful, package an intent to send to the webappservice
        Intent registrationIntent = new Intent(this, WebAppService.class);
        registrationIntent.setAction(WebAppService.SEND_AUDIO);
        registrationIntent.putExtra(WebAppService.AUDIO_FILE_NAME, tempFileName());
        registrationIntent.putExtra(WebAppService.LATITUDE, this.lat);
        registrationIntent.putExtra(WebAppService.LONGITUDE, this.lon);
        startService(registrationIntent);         
    }
    
    public void toggleRecord(View v)
    {
        if(isRecording)
        {
            recorder.stop();
            recorder.reset();
            isRecording = false;
            
                        

            try {
                FileInputStream f= new FileInputStream(tempFileName());
                
                player.setDataSource(f.getFD());
                player.prepare();
                player.start();
                Toast.makeText(v.getContext(), "Stopped!", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e)
            {
                Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                player.reset();
            }
        }
        else
        {
            player.stop();
            player.reset();

            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            recorder.setOutputFile(tempFileName());
            try {
                recorder.prepare();
                recorder.start();
                isRecording = true;
                Toast.makeText(v.getContext(), "Recording!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                recorder.stop();
                recorder.reset();
            }
        }
    }

    public String tempFileName() {
        return getFilesDir().toString() + "/tempfile.mp4";
    }

    
}