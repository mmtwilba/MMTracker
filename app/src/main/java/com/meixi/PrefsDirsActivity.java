package com.meixi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class PrefsDirsActivity extends PreferenceActivity {
    public static int ACTIVITY_DIRBROWSER_MAPS;
    public static int ACTIVITY_DIRBROWSER_ROUTES;
    public static int ACTIVITY_DIRBROWSER_TRACKS;
    public static int ACTIVITY_DIRBROWSER_WAYPOINTS;
    private String m_SettingsMapPath;
    private String m_SettingsRoutePath;
    private String m_SettingsTrackPath;
    private String m_SettingsWaypointPath;

    /* renamed from: com.meixi.PrefsDirsActivity.1 */
    class C00351 implements OnPreferenceClickListener {
        C00351() {
        }

        public boolean onPreferenceClick(Preference preference) {
            PrefsDirsActivity.this.ShowDirBrowser(PrefsDirsActivity.ACTIVITY_DIRBROWSER_MAPS);
            return true;
        }
    }

    /* renamed from: com.meixi.PrefsDirsActivity.2 */
    class C00362 implements OnPreferenceClickListener {
        C00362() {
        }

        public boolean onPreferenceClick(Preference preference) {
            PrefsDirsActivity.this.ShowDirBrowser(PrefsDirsActivity.ACTIVITY_DIRBROWSER_TRACKS);
            return true;
        }
    }

    /* renamed from: com.meixi.PrefsDirsActivity.3 */
    class C00373 implements OnPreferenceClickListener {
        C00373() {
        }

        public boolean onPreferenceClick(Preference preference) {
            PrefsDirsActivity.this.ShowDirBrowser(PrefsDirsActivity.ACTIVITY_DIRBROWSER_ROUTES);
            return true;
        }
    }

    /* renamed from: com.meixi.PrefsDirsActivity.4 */
    class C00384 implements OnPreferenceClickListener {
        C00384() {
        }

        public boolean onPreferenceClick(Preference preference) {
            PrefsDirsActivity.this.ShowDirBrowser(PrefsDirsActivity.ACTIVITY_DIRBROWSER_WAYPOINTS);
            return true;
        }
    }

    static {
        ACTIVITY_DIRBROWSER_MAPS = 1;
        ACTIVITY_DIRBROWSER_TRACKS = 2;
        ACTIVITY_DIRBROWSER_ROUTES = 3;
        ACTIVITY_DIRBROWSER_WAYPOINTS = 4;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(MMTrackerActivity.m_SettingsOrientation);
        addPreferencesFromResource(C0047R.xml.prefs_dirs);
        findPreference("customMapDir").setOnPreferenceClickListener(new C00351());
        findPreference("customTrackDir").setOnPreferenceClickListener(new C00362());
        findPreference("customRouteDir").setOnPreferenceClickListener(new C00373());
        findPreference("customWaypointDir").setOnPreferenceClickListener(new C00384());
    }

    public void ShowDirBrowser(int iType) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (iType == ACTIVITY_DIRBROWSER_MAPS) {
            this.m_SettingsMapPath = prefs.getString("editMapPath", "/");
            try {
                if (this.m_SettingsMapPath.length() <= 0) {
                    this.m_SettingsMapPath = "/";
                } else if (this.m_SettingsMapPath.charAt(this.m_SettingsMapPath.length() - 1) != '/') {
                    this.m_SettingsMapPath += "/";
                }
            } catch (Exception e) {
                this.m_SettingsMapPath = "/";
            }
            Intent i = new Intent(this, DirListActivity.class);
            i.putExtra("path", this.m_SettingsMapPath);
            i.putExtra("type", "Maps");
            i.putExtra("ignore_writeable", true);
            startActivityForResult(i, ACTIVITY_DIRBROWSER_MAPS);
        }
        if (iType == ACTIVITY_DIRBROWSER_TRACKS) {
            this.m_SettingsTrackPath = prefs.getString("editTrackPath", "/");
            try {
                if (this.m_SettingsTrackPath.length() <= 0) {
                    this.m_SettingsTrackPath = "/";
                } else if (this.m_SettingsTrackPath.charAt(this.m_SettingsTrackPath.length() - 1) != '/') {
                    this.m_SettingsTrackPath += "/";
                }
            } catch (Exception e2) {
                this.m_SettingsTrackPath = "/";
            }
            i = new Intent(this, DirListActivity.class);
            i.putExtra("path", this.m_SettingsTrackPath);
            i.putExtra("type", "Tracks");
            i.putExtra("ignore_writeable", false);
            startActivityForResult(i, ACTIVITY_DIRBROWSER_TRACKS);
        }
        if (iType == ACTIVITY_DIRBROWSER_ROUTES) {
            this.m_SettingsRoutePath = prefs.getString("editRoutePath", "/");
            try {
                if (this.m_SettingsRoutePath.length() <= 0) {
                    this.m_SettingsRoutePath = "/";
                } else if (this.m_SettingsRoutePath.charAt(this.m_SettingsRoutePath.length() - 1) != '/') {
                    this.m_SettingsRoutePath += "/";
                }
            } catch (Exception e3) {
                this.m_SettingsRoutePath = "/";
            }
            i = new Intent(this, DirListActivity.class);
            i.putExtra("path", this.m_SettingsRoutePath);
            i.putExtra("type", "Routes");
            i.putExtra("ignore_writeable", false);
            startActivityForResult(i, ACTIVITY_DIRBROWSER_ROUTES);
        }
        if (iType == ACTIVITY_DIRBROWSER_WAYPOINTS) {
            this.m_SettingsWaypointPath = prefs.getString("editWaypointPath", "/");
            try {
                if (this.m_SettingsWaypointPath.length() <= 0) {
                    this.m_SettingsWaypointPath = "/";
                } else if (this.m_SettingsWaypointPath.charAt(this.m_SettingsWaypointPath.length() - 1) != '/') {
                    this.m_SettingsWaypointPath += "/";
                }
            } catch (Exception e4) {
                this.m_SettingsWaypointPath = "/";
            }
            i = new Intent(this, DirListActivity.class);
            i.putExtra("path", this.m_SettingsWaypointPath);
            i.putExtra("type", "Waypoints");
            i.putExtra("ignore_writeable", false);
            startActivityForResult(i, ACTIVITY_DIRBROWSER_WAYPOINTS);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Editor ed = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
            if (requestCode == ACTIVITY_DIRBROWSER_MAPS) {
                this.m_SettingsMapPath = data.getStringExtra("selected_path");
                ed.putString("editMapPath", this.m_SettingsMapPath);
            }
            if (requestCode == ACTIVITY_DIRBROWSER_TRACKS) {
                this.m_SettingsTrackPath = data.getStringExtra("selected_path");
                ed.putString("editTrackPath", this.m_SettingsTrackPath);
            }
            if (requestCode == ACTIVITY_DIRBROWSER_ROUTES) {
                this.m_SettingsRoutePath = data.getStringExtra("selected_path");
                ed.putString("editRoutePath", this.m_SettingsRoutePath);
            }
            if (requestCode == ACTIVITY_DIRBROWSER_WAYPOINTS) {
                this.m_SettingsWaypointPath = data.getStringExtra("selected_path");
                ed.putString("editWaypointPath", this.m_SettingsWaypointPath);
            }
            ed.commit();
            return;
        }
        Toast.makeText(getBaseContext(), getString(C0047R.string.PrefsDirsActivity_toast_no_new_dir_defined), 0).show();
    }
}
