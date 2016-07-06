package com.meixi;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class PrefsMainActivity extends PreferenceActivity {
    public static int ACTIVITY_COLORS;
    public static int ACTIVITY_COMPASS;
    public static int ACTIVITY_DIRS;
    public static int ACTIVITY_DISPLAY;
    public static int ACTIVITY_GPSTRACKS;
    public static int ACTIVITY_UNITS;
    public static int ACTIVITY_USERINTERFACE;

    /* renamed from: com.meixi.PrefsMainActivity.1 */
    class C00391 implements OnPreferenceClickListener {
        C00391() {
        }

        public boolean onPreferenceClick(Preference preference) {
            Toast.makeText(PrefsMainActivity.this.getBaseContext(), new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf("" + "MM Tracker (V1.10.4)\n\n")).append("This program can load and display QCT (Quick Chart) maps.\n\n").toString())).append("Please send any suggestions or report problems to:\n\nmmtracker@gmx.net\n\n").toString())).append("Check out\n\nhttp://sites.google.com/site/mmtrackerinfo\n\nfor further help and information.\n\n").toString())).append("Latest changes:\n").toString())).append(MMTrackerActivity.WHATSNEW_STRING).toString(), 1).show();
            return true;
        }
    }

    /* renamed from: com.meixi.PrefsMainActivity.2 */
    class C00402 implements OnPreferenceClickListener {
        C00402() {
        }

        public boolean onPreferenceClick(Preference preference) {
            PrefsMainActivity.this.ShowSubSettings(PrefsMainActivity.ACTIVITY_DIRS);
            return true;
        }
    }

    /* renamed from: com.meixi.PrefsMainActivity.3 */
    class C00413 implements OnPreferenceClickListener {
        C00413() {
        }

        public boolean onPreferenceClick(Preference preference) {
            PrefsMainActivity.this.ShowSubSettings(PrefsMainActivity.ACTIVITY_DISPLAY);
            return true;
        }
    }

    /* renamed from: com.meixi.PrefsMainActivity.4 */
    class C00424 implements OnPreferenceClickListener {
        C00424() {
        }

        public boolean onPreferenceClick(Preference preference) {
            PrefsMainActivity.this.ShowSubSettings(PrefsMainActivity.ACTIVITY_COLORS);
            return true;
        }
    }

    /* renamed from: com.meixi.PrefsMainActivity.5 */
    class C00435 implements OnPreferenceClickListener {
        C00435() {
        }

        public boolean onPreferenceClick(Preference preference) {
            PrefsMainActivity.this.ShowSubSettings(PrefsMainActivity.ACTIVITY_GPSTRACKS);
            return true;
        }
    }

    /* renamed from: com.meixi.PrefsMainActivity.6 */
    class C00446 implements OnPreferenceClickListener {
        C00446() {
        }

        public boolean onPreferenceClick(Preference preference) {
            PrefsMainActivity.this.ShowSubSettings(PrefsMainActivity.ACTIVITY_COMPASS);
            return true;
        }
    }

    /* renamed from: com.meixi.PrefsMainActivity.7 */
    class C00457 implements OnPreferenceClickListener {
        C00457() {
        }

        public boolean onPreferenceClick(Preference preference) {
            PrefsMainActivity.this.ShowSubSettings(PrefsMainActivity.ACTIVITY_UNITS);
            return true;
        }
    }

    /* renamed from: com.meixi.PrefsMainActivity.8 */
    class C00468 implements OnPreferenceClickListener {
        C00468() {
        }

        public boolean onPreferenceClick(Preference preference) {
            PrefsMainActivity.this.ShowSubSettings(PrefsMainActivity.ACTIVITY_USERINTERFACE);
            return true;
        }
    }

    static {
        ACTIVITY_DIRS = 1;
        ACTIVITY_DISPLAY = 2;
        ACTIVITY_GPSTRACKS = 3;
        ACTIVITY_UNITS = 4;
        ACTIVITY_USERINTERFACE = 5;
        ACTIVITY_COMPASS = 6;
        ACTIVITY_COLORS = 7;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(C0047R.xml.prefs_main);
        setRequestedOrientation(MMTrackerActivity.m_SettingsOrientation);
        findPreference("customPref").setOnPreferenceClickListener(new C00391());
        findPreference("prefsDirectories").setOnPreferenceClickListener(new C00402());
        findPreference("prefsDisplay").setOnPreferenceClickListener(new C00413());
        findPreference("prefsColors").setOnPreferenceClickListener(new C00424());
        findPreference("prefsGps").setOnPreferenceClickListener(new C00435());
        findPreference("prefsCompass").setOnPreferenceClickListener(new C00446());
        findPreference("prefsUnits").setOnPreferenceClickListener(new C00457());
        findPreference("prefsUserinterface").setOnPreferenceClickListener(new C00468());
    }

    public void ShowSubSettings(int iType) {
        if (iType == ACTIVITY_DIRS) {
            startActivityForResult(new Intent(this, PrefsDirsActivity.class), ACTIVITY_DIRS);
        }
        if (iType == ACTIVITY_DISPLAY) {
            startActivityForResult(new Intent(this, PrefsDisplayActivity.class), ACTIVITY_DISPLAY);
        }
        if (iType == ACTIVITY_COLORS) {
            startActivityForResult(new Intent(this, PrefsColorsActivity.class), ACTIVITY_COLORS);
        }
        if (iType == ACTIVITY_GPSTRACKS) {
            startActivityForResult(new Intent(this, PrefsGpsTracksActivity.class), ACTIVITY_GPSTRACKS);
        }
        if (iType == ACTIVITY_COMPASS) {
            startActivityForResult(new Intent(this, PrefsCompassActivity.class), ACTIVITY_COMPASS);
        }
        if (iType == ACTIVITY_UNITS) {
            startActivityForResult(new Intent(this, PrefsUnitsActivity.class), ACTIVITY_UNITS);
        }
        if (iType == ACTIVITY_USERINTERFACE) {
            startActivityForResult(new Intent(this, PrefsUserinterfaceActivity.class), ACTIVITY_USERINTERFACE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
        }
    }
}
