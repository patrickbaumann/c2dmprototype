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
            prefs.edit().putString("webapp_url", getString(R.string.webapp_url));
        if(null == prefs.getString("webapp_user", null))
            prefs.edit().putString("webapp_user", getString(R.string.webapp_user));
        if(null == prefs.getString("webapp_password", null))
            prefs.edit().putString("webapp_password", getString(R.string.webapp_password));
    }
}
