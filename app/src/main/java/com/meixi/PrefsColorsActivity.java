package com.meixi;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PrefsColorsActivity extends PreferenceActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(MMTrackerActivity.m_SettingsOrientation);
        addPreferencesFromResource(C0047R.xml.prefs_colors);
    }
}
