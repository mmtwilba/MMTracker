package com.meixi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.meixi.Tools.StringCoord;
import java.io.File;
import java.util.Collections;

public class WaypointManagerActivity extends Activity {
    private static int ACTIVITY_FILEBROWSER = 0;
    private static int ACTIVITY_WAYPOINTSTYLE = 0;
    private static final int BUTTON_STATE_NO_SELECTION = 0;
    private static final int BUTTON_STATE_ONE_SELECTED = 1;
    private static final int BUTTON_STATE_ONE_SELECTED_NAVIGATING = 3;
    private Button m_DeleteButton;
    private int m_Grid;
    private Button m_HideButton;
    private Button m_NaviButton;
    private Button m_PropsButton;
    private Waypoint m_SelectedWaypoint;
    private Button m_ViewButton;
    private WaypointOverlayAdapter m_WaypointAdapter;
    private boolean m_bSelectedIsNavigating;

    /* renamed from: com.meixi.WaypointManagerActivity.1 */
    class C00711 implements OnItemClickListener {
        C00711() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            WaypointManagerActivity.this.m_WaypointAdapter.setSelectedPosition(position);
            WaypointManagerActivity.this.SetHideButtonText(((Waypoint) WaypointManagerActivity.this.m_WaypointAdapter.getItem(WaypointManagerActivity.this.m_WaypointAdapter.selectedPos)).m_bVisible);
            WaypointManagerActivity.this.SetQuickinfo(position);
            WaypointManagerActivity.this.m_bSelectedIsNavigating = false;
            if (MMTrackerActivity.m_NavigationTarget.m_Type == NavigationTarget.TARGET_TYPE_WAYPOINT) {
                WaypointManagerActivity.this.m_bSelectedIsNavigating = MMTrackerActivity.m_NavigationTarget.m_Waypoint == WaypointManagerActivity.this.m_WaypointAdapter.getItem(WaypointManagerActivity.this.m_WaypointAdapter.selectedPos);
            }
            if (WaypointManagerActivity.this.m_bSelectedIsNavigating) {
                WaypointManagerActivity.this.EnableButtons(WaypointManagerActivity.BUTTON_STATE_ONE_SELECTED_NAVIGATING);
            } else {
                WaypointManagerActivity.this.EnableButtons(WaypointManagerActivity.BUTTON_STATE_ONE_SELECTED);
            }
        }
    }

    /* renamed from: com.meixi.WaypointManagerActivity.2 */
    class C00742 implements OnClickListener {
        private final /* synthetic */ Context val$c;
        private final /* synthetic */ ListView val$listview;

        /* renamed from: com.meixi.WaypointManagerActivity.2.1 */
        class C00721 implements DialogInterface.OnClickListener {
            private final /* synthetic */ ListView val$listview;

            C00721(ListView listView) {
                this.val$listview = listView;
            }

            public void onClick(DialogInterface dialog, int which) {
                int iStoredPos = WaypointManagerActivity.this.m_WaypointAdapter.selectedPos - 1;
                if (iStoredPos < 0) {
                    iStoredPos = WaypointManagerActivity.BUTTON_STATE_NO_SELECTION;
                }
                MMTrackerActivity.DeleteWaypointCore((Waypoint) WaypointManagerActivity.this.m_WaypointAdapter.getItem(WaypointManagerActivity.this.m_WaypointAdapter.selectedPos));
                if (iStoredPos <= WaypointManagerActivity.this.m_WaypointAdapter.getCount() - 1) {
                    WaypointManagerActivity.this.m_WaypointAdapter.setSelectedPosition(iStoredPos);
                    WaypointManagerActivity.this.SetQuickinfo(iStoredPos);
                    WaypointManagerActivity.this.SetHideButtonText(((Waypoint) WaypointManagerActivity.this.m_WaypointAdapter.getItem(iStoredPos)).m_bVisible);
                    this.val$listview.setSelection(iStoredPos);
                    WaypointManagerActivity.this.EnableButtons(WaypointManagerActivity.BUTTON_STATE_ONE_SELECTED);
                } else {
                    WaypointManagerActivity.this.EnableButtons(WaypointManagerActivity.BUTTON_STATE_NO_SELECTION);
                    WaypointManagerActivity.this.m_WaypointAdapter.selectedPos = -1;
                    WaypointManagerActivity.this.SetQuickinfo(-1);
                    this.val$listview.setAdapter(WaypointManagerActivity.this.m_WaypointAdapter);
                }
                this.val$listview.invalidate();
            }
        }

        /* renamed from: com.meixi.WaypointManagerActivity.2.2 */
        class C00732 implements DialogInterface.OnClickListener {
            C00732() {
            }

            public void onClick(DialogInterface dialog, int which) {
            }
        }

        C00742(Context context, ListView listView) {
            this.val$c = context;
            this.val$listview = listView;
        }

        public void onClick(View v) {
            AlertDialog alertDialog = new Builder(this.val$c).create();
            alertDialog.setTitle("MM Tracker");
            if (((Waypoint) WaypointManagerActivity.this.m_WaypointAdapter.getItem(WaypointManagerActivity.this.m_WaypointAdapter.selectedPos)).m_bFromFile) {
                alertDialog.setMessage(new StringBuilder(String.valueOf(WaypointManagerActivity.this.getString(C0047R.string.WaypointManagerActivity_delete_wp_1))).append(((Waypoint) WaypointManagerActivity.this.m_WaypointAdapter.getItem(WaypointManagerActivity.this.m_WaypointAdapter.selectedPos)).m_sFileName).append(WaypointManagerActivity.this.getString(C0047R.string.WaypointManagerActivity_delete_wp_2)).append(((Waypoint) WaypointManagerActivity.this.m_WaypointAdapter.getItem(WaypointManagerActivity.this.m_WaypointAdapter.selectedPos)).m_sName).append("'  ?").toString());
            } else {
                alertDialog.setMessage(new StringBuilder(String.valueOf(WaypointManagerActivity.this.getString(C0047R.string.WaypointManagerActivity_delete_wp_3))).append(((Waypoint) WaypointManagerActivity.this.m_WaypointAdapter.getItem(WaypointManagerActivity.this.m_WaypointAdapter.selectedPos)).m_sName).append("'  ?").toString());
            }
            alertDialog.setButton("Yes", new C00721(this.val$listview));
            alertDialog.setButton2("No", new C00732());
            alertDialog.show();
        }
    }

    /* renamed from: com.meixi.WaypointManagerActivity.3 */
    class C00753 implements OnClickListener {
        C00753() {
        }

        public void onClick(View v) {
            if (WaypointManagerActivity.this.m_WaypointAdapter.selectedPos >= 0) {
                Waypoint w = (Waypoint) WaypointManagerActivity.this.m_WaypointAdapter.getItem(WaypointManagerActivity.this.m_WaypointAdapter.selectedPos);
                if (w.m_bVisible) {
                    w.m_bVisible = false;
                } else {
                    w.m_bVisible = true;
                    w.m_bCacheVaild = false;
                }
                if (w.m_CreatedByMMTracker) {
                    w.WriteGpx(w.m_sFileName);
                }
                WaypointManagerActivity.this.SetHideButtonText(w.m_bVisible);
                WaypointManagerActivity.this.m_WaypointAdapter.updateViews();
            }
        }
    }

    /* renamed from: com.meixi.WaypointManagerActivity.4 */
    class C00764 implements OnClickListener {
        C00764() {
        }

        public void onClick(View v) {
            if (WaypointManagerActivity.this.m_WaypointAdapter.selectedPos >= 0) {
                WaypointManagerActivity.this.ShowWaypointStyleSettings((Waypoint) WaypointManagerActivity.this.m_WaypointAdapter.getItem(WaypointManagerActivity.this.m_WaypointAdapter.selectedPos));
            }
        }
    }

    /* renamed from: com.meixi.WaypointManagerActivity.5 */
    class C00775 implements OnClickListener {
        C00775() {
        }

        public void onClick(View v) {
            if (WaypointManagerActivity.this.m_WaypointAdapter.selectedPos >= 0) {
                MMTrackerActivity.m_dRequestViewLon = ((Waypoint) WaypointManagerActivity.this.m_WaypointAdapter.getItem(WaypointManagerActivity.this.m_WaypointAdapter.selectedPos)).m_dGpsLong;
                MMTrackerActivity.m_dRequestViewLat = ((Waypoint) WaypointManagerActivity.this.m_WaypointAdapter.getItem(WaypointManagerActivity.this.m_WaypointAdapter.selectedPos)).m_dGpsLat;
                MMTrackerActivity.SetSelectedWaypoint((Waypoint) WaypointManagerActivity.this.m_WaypointAdapter.getItem(WaypointManagerActivity.this.m_WaypointAdapter.selectedPos));
                WaypointManagerActivity.this.finish();
            }
        }
    }

    /* renamed from: com.meixi.WaypointManagerActivity.6 */
    class C00786 implements OnClickListener {
        C00786() {
        }

        public void onClick(View v) {
            if (WaypointManagerActivity.this.m_WaypointAdapter.selectedPos >= 0) {
                MMTrackerActivity.NavigateToWaypoint((Waypoint) WaypointManagerActivity.this.m_WaypointAdapter.getItem(WaypointManagerActivity.this.m_WaypointAdapter.selectedPos));
                WaypointManagerActivity.this.finish();
            }
        }
    }

    static {
        ACTIVITY_WAYPOINTSTYLE = BUTTON_STATE_NO_SELECTION;
        ACTIVITY_FILEBROWSER = BUTTON_STATE_ONE_SELECTED;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(MMTrackerActivity.m_SettingsOrientation);
        setContentView(C0047R.layout.waypoint_manager);
        this.m_Grid = getIntent().getExtras().getInt("grid");
        this.m_SelectedWaypoint = MMTrackerActivity.GetSelectedWaypoint();
        Collections.sort(MMTrackerActivity.waypoints);
        this.m_WaypointAdapter = new WaypointOverlayAdapter(this, BUTTON_STATE_NO_SELECTION, MMTrackerActivity.waypoints);
        this.m_WaypointAdapter.setNotifyOnChange(true);
        ListView listview = (ListView) findViewById(C0047R.id.ListViewWaypoints);
        listview.setAdapter(this.m_WaypointAdapter);
        this.m_DeleteButton = (Button) findViewById(C0047R.id.buttonWaypointDelete);
        this.m_HideButton = (Button) findViewById(C0047R.id.buttonWaypointHide);
        this.m_PropsButton = (Button) findViewById(C0047R.id.buttonWaypointProps);
        this.m_ViewButton = (Button) findViewById(C0047R.id.buttonWaypointView);
        this.m_NaviButton = (Button) findViewById(C0047R.id.buttonWaypointNavigate);
        EnableButtons(BUTTON_STATE_NO_SELECTION);
        if (this.m_SelectedWaypoint != null && this.m_WaypointAdapter.getPosition(this.m_SelectedWaypoint) >= 0) {
            this.m_WaypointAdapter.setSelectedPosition(this.m_WaypointAdapter.getPosition(this.m_SelectedWaypoint));
            SetQuickinfo(this.m_WaypointAdapter.getPosition(this.m_SelectedWaypoint));
            SetHideButtonText(((Waypoint) this.m_WaypointAdapter.getItem(this.m_WaypointAdapter.selectedPos)).m_bVisible);
            listview.setSelection(this.m_WaypointAdapter.getPosition(this.m_SelectedWaypoint));
            if (this.m_SelectedWaypoint == MMTrackerActivity.m_NavigationTarget.m_Waypoint) {
                EnableButtons(BUTTON_STATE_ONE_SELECTED_NAVIGATING);
            } else {
                EnableButtons(BUTTON_STATE_ONE_SELECTED);
            }
        }
        listview.setOnItemClickListener(new C00711());
        this.m_DeleteButton.setOnClickListener(new C00742(this, listview));
        this.m_HideButton.setOnClickListener(new C00753());
        this.m_PropsButton.setOnClickListener(new C00764());
        this.m_ViewButton.setOnClickListener(new C00775());
        this.m_NaviButton.setOnClickListener(new C00786());
    }

    void SetHideButtonText(boolean visible) {
        if (visible) {
            this.m_HideButton.setText(getString(C0047R.string.WaypointManagerActivity_hide));
        } else {
            this.m_HideButton.setText(getString(C0047R.string.WaypointManagerActivity_show));
        }
    }

    private void ShowWaypointStyleSettings(Waypoint SelectedWaypoint) {
        if (SelectedWaypoint != null) {
            this.m_SelectedWaypoint = SelectedWaypoint;
            Intent i = new Intent(this, WaypointStyleActivity.class);
            i.putExtra("textWaypointName", this.m_SelectedWaypoint.m_sName);
            Object[] objArr = new Object[BUTTON_STATE_ONE_SELECTED];
            objArr[BUTTON_STATE_NO_SELECTION] = Integer.valueOf(this.m_SelectedWaypoint.m_iColor & 16777215);
            i.putExtra("listWaypointColor", String.format("%08X", objArr));
            objArr = new Object[BUTTON_STATE_ONE_SELECTED];
            objArr[BUTTON_STATE_NO_SELECTION] = Integer.valueOf((this.m_SelectedWaypoint.m_iColor >> 24) & 255);
            i.putExtra("listWaypointOpacity", String.format("%d", objArr));
            i.putExtra("listWaypointIcon", this.m_SelectedWaypoint.m_sSymbol);
            i.putExtra("checkboxWaypointLabel", this.m_SelectedWaypoint.m_bShowLabel);
            i.putExtra("checkboxWaypointLocked", this.m_SelectedWaypoint.m_bLocked);
            i.putExtra("textWaypointDescription", this.m_SelectedWaypoint.m_sDesc);
            i.putExtra("waypoint_lat", this.m_SelectedWaypoint.m_dGpsLat);
            i.putExtra("waypoint_lon", this.m_SelectedWaypoint.m_dGpsLong);
            startActivityForResult(i, ACTIVITY_WAYPOINTSTYLE);
        }
    }

    void SetQuickinfo(int iIndex) {
        String s = "";
        if (iIndex > -1) {
            OsgbCoord osgb = new OsgbCoord();
            boolean bResult = osgb.SetLatLonCoord(((Waypoint) MMTrackerActivity.waypoints.get(iIndex)).m_dGpsLat, ((Waypoint) MMTrackerActivity.waypoints.get(iIndex)).m_dGpsLong, 0.0d);
            if (this.m_Grid != 5) {
                StringCoord c = Tools.CoordToString(((Waypoint) MMTrackerActivity.waypoints.get(iIndex)).m_dGpsLat, ((Waypoint) MMTrackerActivity.waypoints.get(iIndex)).m_dGpsLong, this.m_Grid, true);
                s = c.sLat + "   " + c.sLong;
            } else if (bResult) {
                s = osgb.GetOsgbAsText();
            } else {
                s = osgb.GetLatLonAsText();
            }
        }
        ((TextView) findViewById(C0047R.id.textWaypointProps)).setText(s);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!(requestCode != ACTIVITY_WAYPOINTSTYLE || this.m_SelectedWaypoint == null || data == null)) {
            String s = data.getStringExtra("listWaypointColor");
            try {
                this.m_SelectedWaypoint.m_iColor = (int) Long.parseLong(s, 16);
            } catch (NumberFormatException e) {
                this.m_SelectedWaypoint.m_iColor = -65536;
            }
            s = data.getStringExtra("listWaypointOpacity");
            try {
                this.m_SelectedWaypoint.m_iColor = (this.m_SelectedWaypoint.m_iColor & 16777215) | (((int) Long.parseLong(s)) << 24);
            } catch (NumberFormatException e2) {
            }
            this.m_SelectedWaypoint.m_sSymbol = data.getStringExtra("listWaypointIcon");
            this.m_SelectedWaypoint.m_iSymbol = this.m_SelectedWaypoint.CalcSymbolNumber();
            this.m_SelectedWaypoint.m_bShowLabel = data.getBooleanExtra("checkboxWaypointLabel", false);
            this.m_SelectedWaypoint.m_bLocked = data.getBooleanExtra("checkboxWaypointLocked", false);
            this.m_SelectedWaypoint.m_sName = data.getStringExtra("textWaypointName");
            this.m_SelectedWaypoint.m_sDesc = data.getStringExtra("textWaypointDescription");
            this.m_SelectedWaypoint.m_dGpsLat = data.getDoubleExtra("new_lat", this.m_SelectedWaypoint.m_dGpsLat);
            this.m_SelectedWaypoint.m_dGpsLong = data.getDoubleExtra("new_lon", this.m_SelectedWaypoint.m_dGpsLong);
            if (this.m_SelectedWaypoint.m_CreatedByMMTracker && this.m_SelectedWaypoint.m_sFileName != "" && this.m_SelectedWaypoint.m_bFromFile) {
                new File(this.m_SelectedWaypoint.m_sFileName).delete();
                this.m_SelectedWaypoint.WriteGpx(MMTrackerActivity.m_SettingsWaypointPath + Tools.MakeProperFileName(this.m_SelectedWaypoint.m_sName) + ".gpx");
            } else if (this.m_SelectedWaypoint.m_bFromFile) {
                Toast.makeText(getBaseContext(), getString(C0047R.string.MmTrackerActivity_toast_wp_not_stored), BUTTON_STATE_ONE_SELECTED).show();
            }
            if (this.m_SelectedWaypoint != null) {
                SetQuickinfo(this.m_WaypointAdapter.getPosition(this.m_SelectedWaypoint));
            }
            this.m_WaypointAdapter.updateViews();
            this.m_SelectedWaypoint = null;
        }
        if (requestCode == MMTrackerActivity.ACTIVITY_COORDINATE) {
            double dLon = 9999.0d;
            double dLat = 9999.0d;
            Boolean bJumpTo = Boolean.valueOf(false);
            if (data != null) {
                dLon = data.getDoubleExtra("result_lon", 9999.0d);
                dLat = data.getDoubleExtra("result_lat", 9999.0d);
                String sWpName = data.getStringExtra("result_name");
                bJumpTo = Boolean.valueOf(data.getBooleanExtra("result_jump", false));
                Waypoint w = new Waypoint(dLon, dLat, Waypoint.WAYPOINT_COLOR_DEFAULT, "Circle");
                if (w != null) {
                    w.m_sName = Tools.MakeProperFileName(sWpName).trim();
                    w.m_bCacheVaild = false;
                    w.m_bLocked = true;
                    w.m_bShowLabel = true;
                    w.WriteGpx(MMTrackerActivity.m_SettingsWaypointPath + Tools.MakeProperFileName(w.m_sName) + ".gpx");
                    MMTrackerActivity.waypoints.add(w);
                }
                MMTrackerActivity.m_requestedWpRefresh = w;
            }
            if (bJumpTo.booleanValue()) {
                MMTrackerActivity.m_dRequestViewLon = dLon;
                MMTrackerActivity.m_dRequestViewLat = dLat;
                finish();
            } else {
                this.m_WaypointAdapter.updateViews();
            }
        }
        if (requestCode == ACTIVITY_FILEBROWSER && data != null && resultCode == BUTTON_STATE_ONE_SELECTED) {
            String name;
            if (data.getStringExtra(MapCacheHelper.KEY_NAME).length() < 4) {
                name = new String("Loaded Waypoint");
            } else {
                name = data.getStringExtra(MapCacheHelper.KEY_NAME);
            }
            Tools.ReadGPXtoWaypoints(data.getStringExtra("file"), name.substring(BUTTON_STATE_NO_SELECTION, name.length() - 4), MMTrackerActivity.waypoints);
            this.m_WaypointAdapter.updateViews();
            MMTrackerActivity.m_bRequestAllWpRefresh = true;
        }
    }

    public void EnableButtons(int iState) {
        if (this.m_DeleteButton != null && this.m_HideButton != null && this.m_PropsButton != null && this.m_ViewButton != null) {
            switch (iState) {
                case BUTTON_STATE_NO_SELECTION /*0*/:
                    this.m_DeleteButton.setEnabled(false);
                    this.m_HideButton.setEnabled(false);
                    this.m_PropsButton.setEnabled(false);
                    this.m_ViewButton.setEnabled(false);
                    this.m_NaviButton.setEnabled(false);
                case BUTTON_STATE_ONE_SELECTED /*1*/:
                    this.m_DeleteButton.setEnabled(true);
                    this.m_HideButton.setEnabled(true);
                    this.m_PropsButton.setEnabled(true);
                    this.m_ViewButton.setEnabled(true);
                    this.m_NaviButton.setEnabled(true);
                case BUTTON_STATE_ONE_SELECTED_NAVIGATING /*3*/:
                    this.m_DeleteButton.setEnabled(false);
                    this.m_HideButton.setEnabled(false);
                    this.m_PropsButton.setEnabled(true);
                    this.m_ViewButton.setEnabled(true);
                    this.m_NaviButton.setEnabled(true);
                default:
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0047R.menu.waypoint_menu, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int i;
        Waypoint w;
        switch (item.getItemId()) {
            case C0047R.id.itemLoadWp:
                Intent inte = new Intent(this, FileListActivity.class);
                inte.putExtra("path", MMTrackerActivity.m_SettingsWaypointPath);
                inte.putExtra("extension", "gpx");
                inte.putExtra("header", getString(C0047R.string.WaypointManagerActivity_file_browser_header));
                inte.putExtra("exclude", "");
                startActivityForResult(inte, ACTIVITY_FILEBROWSER);
                return true;
            case C0047R.id.itemCreateWp:
                Intent coordinateActivity = new Intent(getBaseContext(), CoordinateEntryActivity.class);
                coordinateActivity.putExtra("longitude", BUTTON_STATE_NO_SELECTION);
                coordinateActivity.putExtra("latitude", BUTTON_STATE_NO_SELECTION);
                coordinateActivity.putExtra("use_init_coords", false);
                coordinateActivity.putExtra("dialog_simple", false);
                startActivityForResult(coordinateActivity, MMTrackerActivity.ACTIVITY_COORDINATE);
                return true;
            case C0047R.id.itemHideAllWp:
                for (i = BUTTON_STATE_NO_SELECTION; i < this.m_WaypointAdapter.getCount(); i += BUTTON_STATE_ONE_SELECTED) {
                    w = (Waypoint) this.m_WaypointAdapter.getItem(i);
                    if (w.m_bVisible) {
                        w.m_bVisible = false;
                        if (w.m_CreatedByMMTracker) {
                            w.WriteGpx(w.m_sFileName);
                        }
                    }
                }
                SetHideButtonText(false);
                this.m_WaypointAdapter.updateViews();
                return true;
            case C0047R.id.itemUnhideAllWp:
                for (i = BUTTON_STATE_NO_SELECTION; i < this.m_WaypointAdapter.getCount(); i += BUTTON_STATE_ONE_SELECTED) {
                    w = (Waypoint) this.m_WaypointAdapter.getItem(i);
                    if (!w.m_bVisible) {
                        w.m_bVisible = true;
                        w.m_bCacheVaild = false;
                        if (w.m_CreatedByMMTracker) {
                            w.WriteGpx(w.m_sFileName);
                        }
                    }
                }
                SetHideButtonText(true);
                this.m_WaypointAdapter.updateViews();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
