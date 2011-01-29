package com.patrickbaumann.pushprototype;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Prefs extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefaultSettings(R.layout.settings);
        addPreferencesFromResource(R.layout.settings);

    }

    private void setDefaultSettings(int settings) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(null == prefs.getString("webapp_url", null))
            prefs.edit().putString("webapp_url", "http://192.168.2.112:8000");
    }
}
