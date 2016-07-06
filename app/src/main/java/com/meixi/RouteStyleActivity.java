package com.meixi;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class RouteStyleActivity extends PreferenceActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(MMTrackerActivity.m_SettingsOrientation);
        addPreferencesFromResource(C0047R.xml.route_style);
    }
}
