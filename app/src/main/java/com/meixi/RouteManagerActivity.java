package com.meixi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import java.io.File;
import java.util.Collections;

public class RouteManagerActivity extends Activity {
    private static int ACTIVITY_FILEBROWSER = 0;
    private static int ACTIVITY_ROUTESTYLE = 0;
    private static final int BUTTON_STATE_NO_SELECTION = 0;
    private static final int BUTTON_STATE_ONE_SELECTED = 1;
    private static final int BUTTON_STATE_ONE_SELECTED_NAVIGATING = 3;
    private static final int BUTTON_STATE_ONE_SELECTED_NO_POINTS = 2;
    private Button m_DeleteButton;
    private Button m_HideButton;
    private Button m_NaviButton;
    private Button m_PropsButton;
    private RouteOverlayAdapter m_RouteAdapter;
    private Routepoint m_SelectedRoutepoint;
    private Button m_ViewButton;
    private boolean m_bSelectedIsNavigating;
    private int m_iUnits;

    /* renamed from: com.meixi.RouteManagerActivity.1 */
    class C00481 implements OnItemClickListener {
        C00481() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            RouteManagerActivity.this.m_RouteAdapter.setSelectedPosition(position);
            RouteManagerActivity.this.SetHideButtonText(((Route) RouteManagerActivity.this.m_RouteAdapter.getItem(RouteManagerActivity.this.m_RouteAdapter.selectedPos)).m_bVisible);
            RouteManagerActivity.this.SetQuickinfo(position);
            RouteManagerActivity.this.m_bSelectedIsNavigating = false;
            if (MMTrackerActivity.m_NavigationTarget.m_Type == NavigationTarget.TARGET_TYPE_ROUTEPOINT) {
                RouteManagerActivity.this.m_bSelectedIsNavigating = MMTrackerActivity.m_NavigationTarget.m_ParentRoute == RouteManagerActivity.this.m_RouteAdapter.getItem(RouteManagerActivity.this.m_RouteAdapter.selectedPos);
            }
            if (((Route) RouteManagerActivity.this.m_RouteAdapter.getItem(RouteManagerActivity.this.m_RouteAdapter.selectedPos)).routepoints.size() == 0) {
                RouteManagerActivity.this.EnableButtons(RouteManagerActivity.BUTTON_STATE_ONE_SELECTED_NO_POINTS);
            } else if (RouteManagerActivity.this.m_bSelectedIsNavigating) {
                RouteManagerActivity.this.EnableButtons(RouteManagerActivity.BUTTON_STATE_ONE_SELECTED_NAVIGATING);
            } else {
                RouteManagerActivity.this.EnableButtons(RouteManagerActivity.BUTTON_STATE_ONE_SELECTED);
            }
        }
    }

    /* renamed from: com.meixi.RouteManagerActivity.2 */
    class C00512 implements OnClickListener {
        private final /* synthetic */ Context val$c;
        private final /* synthetic */ ListView val$listview;

        /* renamed from: com.meixi.RouteManagerActivity.2.1 */
        class C00491 implements DialogInterface.OnClickListener {
            private final /* synthetic */ ListView val$listview;

            C00491(ListView listView) {
                this.val$listview = listView;
            }

            public void onClick(DialogInterface dialog, int which) {
                int iStoredPos = RouteManagerActivity.this.m_RouteAdapter.selectedPos - 1;
                if (iStoredPos < 0) {
                    iStoredPos = RouteManagerActivity.BUTTON_STATE_NO_SELECTION;
                }
                MMTrackerActivity.DeleteRouteCore((Route) RouteManagerActivity.this.m_RouteAdapter.getItem(RouteManagerActivity.this.m_RouteAdapter.selectedPos));
                if (iStoredPos <= RouteManagerActivity.this.m_RouteAdapter.getCount() - 1) {
                    RouteManagerActivity.this.m_RouteAdapter.setSelectedPosition(iStoredPos);
                    RouteManagerActivity.this.SetQuickinfo(iStoredPos);
                    RouteManagerActivity.this.SetHideButtonText(((Route) RouteManagerActivity.this.m_RouteAdapter.getItem(iStoredPos)).m_bVisible);
                    this.val$listview.setSelection(iStoredPos);
                    RouteManagerActivity.this.EnableButtons(RouteManagerActivity.BUTTON_STATE_ONE_SELECTED);
                } else {
                    RouteManagerActivity.this.EnableButtons(RouteManagerActivity.BUTTON_STATE_NO_SELECTION);
                    RouteManagerActivity.this.m_RouteAdapter.selectedPos = -1;
                    RouteManagerActivity.this.SetQuickinfo(-1);
                    this.val$listview.setAdapter(RouteManagerActivity.this.m_RouteAdapter);
                }
                this.val$listview.invalidate();
            }
        }

        /* renamed from: com.meixi.RouteManagerActivity.2.2 */
        class C00502 implements DialogInterface.OnClickListener {
            C00502() {
            }

            public void onClick(DialogInterface dialog, int which) {
            }
        }

        C00512(Context context, ListView listView) {
            this.val$c = context;
            this.val$listview = listView;
        }

        public void onClick(View v) {
            AlertDialog alertDialog = new Builder(this.val$c).create();
            alertDialog.setTitle("MM Tracker");
            alertDialog.setMessage("Do you want to permanently delete the File: '" + ((Route) RouteManagerActivity.this.m_RouteAdapter.getItem(RouteManagerActivity.this.m_RouteAdapter.selectedPos)).m_sFileName + "' which contains the Route: '" + ((Route) RouteManagerActivity.this.m_RouteAdapter.getItem(RouteManagerActivity.this.m_RouteAdapter.selectedPos)).m_sName + "'  ?");
            alertDialog.setButton("Yes", new C00491(this.val$listview));
            alertDialog.setButton2("No", new C00502());
            alertDialog.show();
        }
    }

    /* renamed from: com.meixi.RouteManagerActivity.3 */
    class C00523 implements OnClickListener {
        C00523() {
        }

        public void onClick(View v) {
            if (RouteManagerActivity.this.m_RouteAdapter.selectedPos >= 0) {
                if (((Route) RouteManagerActivity.this.m_RouteAdapter.getItem(RouteManagerActivity.this.m_RouteAdapter.selectedPos)).m_bVisible) {
                    ((Route) RouteManagerActivity.this.m_RouteAdapter.getItem(RouteManagerActivity.this.m_RouteAdapter.selectedPos)).m_bVisible = false;
                } else {
                    ((Route) RouteManagerActivity.this.m_RouteAdapter.getItem(RouteManagerActivity.this.m_RouteAdapter.selectedPos)).m_bVisible = true;
                    ((Route) RouteManagerActivity.this.m_RouteAdapter.getItem(RouteManagerActivity.this.m_RouteAdapter.selectedPos)).m_bCacheVaild = false;
                }
                if (((Route) RouteManagerActivity.this.m_RouteAdapter.getItem(RouteManagerActivity.this.m_RouteAdapter.selectedPos)).m_CreatedByMMTracker) {
                    ((Route) RouteManagerActivity.this.m_RouteAdapter.getItem(RouteManagerActivity.this.m_RouteAdapter.selectedPos)).WriteGPX(((Route) RouteManagerActivity.this.m_RouteAdapter.getItem(RouteManagerActivity.this.m_RouteAdapter.selectedPos)).m_sFileName);
                }
                RouteManagerActivity.this.SetHideButtonText(((Route) RouteManagerActivity.this.m_RouteAdapter.getItem(RouteManagerActivity.this.m_RouteAdapter.selectedPos)).m_bVisible);
                RouteManagerActivity.this.m_RouteAdapter.updateViews();
            }
        }
    }

    /* renamed from: com.meixi.RouteManagerActivity.4 */
    class C00534 implements OnClickListener {
        C00534() {
        }

        public void onClick(View v) {
            if (RouteManagerActivity.this.m_RouteAdapter.selectedPos >= 0) {
                RouteManagerActivity.this.ShowRouteStyleSettings((Route) RouteManagerActivity.this.m_RouteAdapter.getItem(RouteManagerActivity.this.m_RouteAdapter.selectedPos));
            }
        }
    }

    /* renamed from: com.meixi.RouteManagerActivity.5 */
    class C00545 implements OnClickListener {
        C00545() {
        }

        public void onClick(View v) {
            if (RouteManagerActivity.this.m_RouteAdapter.selectedPos > -1 && ((Route) RouteManagerActivity.this.m_RouteAdapter.getItem(RouteManagerActivity.this.m_RouteAdapter.selectedPos)).routepoints.size() > 0) {
                MMTrackerActivity.m_dRequestViewLon = ((Routepoint) ((Route) RouteManagerActivity.this.m_RouteAdapter.getItem(RouteManagerActivity.this.m_RouteAdapter.selectedPos)).routepoints.get(RouteManagerActivity.BUTTON_STATE_NO_SELECTION)).m_dGpsLong;
                MMTrackerActivity.m_dRequestViewLat = ((Routepoint) ((Route) RouteManagerActivity.this.m_RouteAdapter.getItem(RouteManagerActivity.this.m_RouteAdapter.selectedPos)).routepoints.get(RouteManagerActivity.BUTTON_STATE_NO_SELECTION)).m_dGpsLat;
                MMTrackerActivity.SetSelectedRoutepoint((Routepoint) ((Route) RouteManagerActivity.this.m_RouteAdapter.getItem(RouteManagerActivity.this.m_RouteAdapter.selectedPos)).routepoints.get(RouteManagerActivity.BUTTON_STATE_NO_SELECTION));
                RouteManagerActivity.this.finish();
            }
        }
    }

    /* renamed from: com.meixi.RouteManagerActivity.6 */
    class C00556 implements OnClickListener {
        C00556() {
        }

        public void onClick(View v) {
            if (RouteManagerActivity.this.m_RouteAdapter.selectedPos >= 0) {
                MMTrackerActivity.NavigateToRoutepoint((Routepoint) ((Route) RouteManagerActivity.this.m_RouteAdapter.getItem(RouteManagerActivity.this.m_RouteAdapter.selectedPos)).routepoints.get(RouteManagerActivity.BUTTON_STATE_NO_SELECTION));
                RouteManagerActivity.this.finish();
            }
        }
    }

    static {
        ACTIVITY_ROUTESTYLE = BUTTON_STATE_NO_SELECTION;
        ACTIVITY_FILEBROWSER = BUTTON_STATE_ONE_SELECTED;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(MMTrackerActivity.m_SettingsOrientation);
        setContentView(C0047R.layout.route_manager);
        this.m_iUnits = getIntent().getExtras().getInt("units");
        this.m_SelectedRoutepoint = MMTrackerActivity.GetSelectedRoutepoint();
        MMTrackerActivity.m_bRequestRouteCreation = false;
        Collections.sort(MMTrackerActivity.routes);
        this.m_RouteAdapter = new RouteOverlayAdapter(this, BUTTON_STATE_NO_SELECTION, MMTrackerActivity.routes);
        this.m_RouteAdapter.setNotifyOnChange(true);
        ListView listview = (ListView) findViewById(C0047R.id.ListViewRoutes);
        listview.setAdapter(this.m_RouteAdapter);
        this.m_DeleteButton = (Button) findViewById(C0047R.id.buttonRouteDelete);
        this.m_HideButton = (Button) findViewById(C0047R.id.buttonRouteHide);
        this.m_PropsButton = (Button) findViewById(C0047R.id.buttonRouteProps);
        this.m_ViewButton = (Button) findViewById(C0047R.id.buttonRouteView);
        this.m_NaviButton = (Button) findViewById(C0047R.id.buttonRouteNavigate);
        EnableButtons(BUTTON_STATE_NO_SELECTION);
        if (this.m_SelectedRoutepoint != null && this.m_RouteAdapter.getPosition(this.m_SelectedRoutepoint.m_ParentRoute) >= 0) {
            this.m_RouteAdapter.setSelectedPosition(this.m_RouteAdapter.getPosition(this.m_SelectedRoutepoint.m_ParentRoute));
            SetQuickinfo(this.m_RouteAdapter.getPosition(this.m_SelectedRoutepoint.m_ParentRoute));
            SetHideButtonText(((Route) this.m_RouteAdapter.getItem(this.m_RouteAdapter.selectedPos)).m_bVisible);
            listview.setSelection(this.m_RouteAdapter.getPosition(this.m_SelectedRoutepoint.m_ParentRoute));
            EnableButtons(BUTTON_STATE_ONE_SELECTED);
        }
        listview.setOnItemClickListener(new C00481());
        this.m_DeleteButton.setOnClickListener(new C00512(this, listview));
        this.m_HideButton.setOnClickListener(new C00523());
        this.m_PropsButton.setOnClickListener(new C00534());
        this.m_ViewButton.setOnClickListener(new C00545());
        this.m_NaviButton.setOnClickListener(new C00556());
    }

    void SetHideButtonText(boolean visible) {
        if (visible) {
            this.m_HideButton.setText(getString(C0047R.string.RouteManagerActivity_hide));
        } else {
            this.m_HideButton.setText(getString(C0047R.string.RouteManagerActivity_show));
        }
    }

    private void ShowRouteStyleSettings(Route SelectedRoute) {
        if (SelectedRoute != null) {
            this.m_SelectedRoutepoint = SelectedRoute.get(BUTTON_STATE_NO_SELECTION);
            Editor editor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
            editor.putString("textRouteName", this.m_SelectedRoutepoint.m_ParentRoute.m_sName);
            Object[] objArr = new Object[BUTTON_STATE_ONE_SELECTED];
            objArr[BUTTON_STATE_NO_SELECTION] = Integer.valueOf(this.m_SelectedRoutepoint.m_ParentRoute.m_iColor & 16777215);
            editor.putString("listRouteColor", String.format("%08X", objArr));
            objArr = new Object[BUTTON_STATE_ONE_SELECTED];
            objArr[BUTTON_STATE_NO_SELECTION] = Integer.valueOf(Math.round(this.m_SelectedRoutepoint.m_ParentRoute.m_fWidth));
            editor.putString("listRouteWidth", String.format("%d", objArr));
            objArr = new Object[BUTTON_STATE_ONE_SELECTED];
            objArr[BUTTON_STATE_NO_SELECTION] = Integer.valueOf((this.m_SelectedRoutepoint.m_ParentRoute.m_iColor >> 24) & 255);
            editor.putString("listRouteOpacity", String.format("%d", objArr));
            editor.putBoolean("checkboxRouteLocked", this.m_SelectedRoutepoint.m_ParentRoute.m_bLocked);
            editor.commit();
            startActivityForResult(new Intent(getBaseContext(), RouteStyleActivity.class), ACTIVITY_ROUTESTYLE);
        }
    }

    void SetQuickinfo(int iIndex) {
        String s;
        if (iIndex > -1) {
            int iCount = ((Route) MMTrackerActivity.routes.get(iIndex)).routepoints.size();
            double dLen = 0.0d;
            if (iCount > 0) {
                dLen = ((Route) MMTrackerActivity.routes.get(iIndex)).CalcLengthKm((Routepoint) ((Route) MMTrackerActivity.routes.get(iIndex)).routepoints.get(BUTTON_STATE_NO_SELECTION));
            }
            Object[] objArr = new Object[BUTTON_STATE_ONE_SELECTED_NAVIGATING];
            objArr[BUTTON_STATE_NO_SELECTION] = Integer.valueOf(iCount);
            objArr[BUTTON_STATE_ONE_SELECTED] = Double.valueOf(Tools.m_dUnitDistanceFactor[this.m_iUnits] * dLen);
            objArr[BUTTON_STATE_ONE_SELECTED_NO_POINTS] = Tools.m_sUnitDistance[this.m_iUnits];
            s = String.format("Points: %d   Length: %.2f %s", objArr);
        } else {
            s = "";
        }
        ((TextView) findViewById(C0047R.id.textRouteProps)).setText(s);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_ROUTESTYLE && this.m_SelectedRoutepoint != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String s = prefs.getString("listRouteColor", "FFFF0000");
            try {
                this.m_SelectedRoutepoint.m_ParentRoute.m_iColor = (int) Long.parseLong(s, 16);
            } catch (NumberFormatException e) {
                this.m_SelectedRoutepoint.m_ParentRoute.m_iColor = -65536;
            }
            s = prefs.getString("listRouteWidth", "4");
            try {
                this.m_SelectedRoutepoint.m_ParentRoute.m_fWidth = (float) Double.parseDouble(s);
            } catch (NumberFormatException e2) {
                this.m_SelectedRoutepoint.m_ParentRoute.m_fWidth = 4.0f;
            }
            s = prefs.getString("listRouteOpacity", "255");
            try {
                this.m_SelectedRoutepoint.m_ParentRoute.m_iColor = (this.m_SelectedRoutepoint.m_ParentRoute.m_iColor & 16777215) | (((int) Long.parseLong(s)) << 24);
            } catch (NumberFormatException e3) {
            }
            this.m_SelectedRoutepoint.m_ParentRoute.m_sName = prefs.getString("textRouteName", "no_name");
            this.m_SelectedRoutepoint.m_ParentRoute.m_bLocked = prefs.getBoolean("checkboxRouteLocked", false);
            if (this.m_SelectedRoutepoint.m_ParentRoute.m_CreatedByMMTracker && this.m_SelectedRoutepoint.m_ParentRoute.m_sFileName != "") {
                new File(this.m_SelectedRoutepoint.m_ParentRoute.m_sFileName).delete();
                this.m_SelectedRoutepoint.m_ParentRoute.WriteGPX(MMTrackerActivity.m_SettingsRoutePath + Tools.MakeProperFileName(this.m_SelectedRoutepoint.m_ParentRoute.m_sName) + ".gpx");
            } else if (!this.m_SelectedRoutepoint.m_ParentRoute.m_bNoSaveWarningShown) {
                Toast.makeText(getBaseContext(), getString(C0047R.string.MmTrackerActivity_toast_rt_not_stored), BUTTON_STATE_ONE_SELECTED).show();
                this.m_SelectedRoutepoint.m_ParentRoute.m_bNoSaveWarningShown = true;
            }
            this.m_RouteAdapter.updateViews();
            this.m_SelectedRoutepoint = null;
        }
        if (requestCode == ACTIVITY_FILEBROWSER && data != null && resultCode == BUTTON_STATE_ONE_SELECTED) {
            String name;
            if (data.getStringExtra(MapCacheHelper.KEY_NAME).length() < 4) {
                name = new String("Loaded Route");
            } else {
                name = data.getStringExtra(MapCacheHelper.KEY_NAME);
            }
            Tools.ReadGPXtoRoutes(data.getStringExtra("file"), name.substring(BUTTON_STATE_NO_SELECTION, name.length() - 4), MMTrackerActivity.routes);
            this.m_RouteAdapter.updateViews();
            MMTrackerActivity.m_bRequestAllRtRefresh = true;
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
                case BUTTON_STATE_ONE_SELECTED_NO_POINTS /*2*/:
                    this.m_DeleteButton.setEnabled(true);
                    this.m_HideButton.setEnabled(true);
                    this.m_PropsButton.setEnabled(true);
                    this.m_ViewButton.setEnabled(false);
                    this.m_NaviButton.setEnabled(false);
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
        getMenuInflater().inflate(C0047R.menu.route_menu, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int i;
        Route r;
        switch (item.getItemId()) {
            case C0047R.id.itemLoadRt:
                Intent inte = new Intent(this, FileListActivity.class);
                inte.putExtra("path", MMTrackerActivity.m_SettingsRoutePath);
                inte.putExtra("extension", "gpx");
                inte.putExtra("header", getString(C0047R.string.RouteManagerActivity_file_browser_header));
                inte.putExtra("exclude", "");
                startActivityForResult(inte, ACTIVITY_FILEBROWSER);
                return true;
            case C0047R.id.itemCreateRt:
                MMTrackerActivity.m_bRequestRouteCreation = true;
                finish();
                return true;
            case C0047R.id.itemHideAllRt:
                for (i = BUTTON_STATE_NO_SELECTION; i < this.m_RouteAdapter.getCount(); i += BUTTON_STATE_ONE_SELECTED) {
                    r = (Route) this.m_RouteAdapter.getItem(i);
                    if (r.m_bVisible) {
                        r.m_bVisible = false;
                        if (r.m_CreatedByMMTracker) {
                            r.WriteGPX(r.m_sFileName);
                        }
                    }
                }
                SetHideButtonText(false);
                this.m_RouteAdapter.updateViews();
                return true;
            case C0047R.id.itemUnhideAllRt:
                for (i = BUTTON_STATE_NO_SELECTION; i < this.m_RouteAdapter.getCount(); i += BUTTON_STATE_ONE_SELECTED) {
                    r = (Route) this.m_RouteAdapter.getItem(i);
                    if (!r.m_bVisible) {
                        r.m_bVisible = true;
                        r.m_bCacheVaild = false;
                        if (r.m_CreatedByMMTracker) {
                            r.WriteGPX(r.m_sFileName);
                        }
                    }
                }
                SetHideButtonText(true);
                this.m_RouteAdapter.updateViews();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
