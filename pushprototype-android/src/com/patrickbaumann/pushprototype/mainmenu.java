package com.patrickbaumann.pushprototype;

import java.io.FileInputStream;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


public class mainmenu extends Activity {

    MediaRecorder recorder = new MediaRecorder();
    MediaPlayer player = new MediaPlayer();
    boolean isRecording = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        
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

    public void toggleRecord(View v)
    {
        if(isRecording)
        {
            recorder.stop();
            recorder.reset();
            isRecording = false;
            
                        

            try {
                FileInputStream f= new FileInputStream(getFilesDir().toString() + "/tempfile.mp4");
                
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
            recorder.setOutputFile(getFilesDir().toString() + "/tempfile.mp4");
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

    
}