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
import java.util.Collections;

public class TrackManagerActivity extends Activity {
    private static int ACTIVITY_FILEBROWSER = 0;
    private static int ACTIVITY_TRACKDATA = 0;
    private static int ACTIVITY_TRACKSTYLE = 0;
    private static final int BUTTON_STATE_NO_SELECTION = 0;
    private static final int BUTTON_STATE_ONE_SELECTED = 1;
    private static final int BUTTON_STATE_ONE_SELECTED_NO_POINTS = 2;
    private Button m_DataButton;
    private Button m_DeleteButton;
    private Button m_HideButton;
    private Button m_PropsButton;
    private Track m_SelectedTrack;
    private TrackOverlayAdapter m_TrackAdapter;
    private Button m_ViewButton;
    private boolean m_bWasActive;
    private int m_iUnits;

    /* renamed from: com.meixi.TrackManagerActivity.1 */
    class C00631 implements OnItemClickListener {
        C00631() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            TrackManagerActivity.this.m_TrackAdapter.setSelectedPosition(position);
            TrackManagerActivity.this.SetHideButtonText(((Track) TrackManagerActivity.this.m_TrackAdapter.getItem(TrackManagerActivity.this.m_TrackAdapter.selectedPos)).m_bVisible);
            TrackManagerActivity.this.SetQuickinfo(position);
            if (((Track) TrackManagerActivity.this.m_TrackAdapter.getItem(TrackManagerActivity.this.m_TrackAdapter.selectedPos)).trackpoints.size() != 0) {
                TrackManagerActivity.this.EnableButtons(TrackManagerActivity.BUTTON_STATE_ONE_SELECTED);
            } else {
                TrackManagerActivity.this.EnableButtons(TrackManagerActivity.BUTTON_STATE_ONE_SELECTED_NO_POINTS);
            }
        }
    }

    /* renamed from: com.meixi.TrackManagerActivity.2 */
    class C00662 implements OnClickListener {
        private final /* synthetic */ Context val$c;
        private final /* synthetic */ ListView val$listview;

        /* renamed from: com.meixi.TrackManagerActivity.2.1 */
        class C00641 implements DialogInterface.OnClickListener {
            private final /* synthetic */ ListView val$listview;

            C00641(ListView listView) {
                this.val$listview = listView;
            }

            public void onClick(DialogInterface dialog, int which) {
                int iStoredPos = TrackManagerActivity.this.m_TrackAdapter.selectedPos - 1;
                if (iStoredPos < 0) {
                    iStoredPos = TrackManagerActivity.BUTTON_STATE_NO_SELECTION;
                }
                if (TrackManagerActivity.this.m_bWasActive) {
                    TrackManagerActivity.this.StopTrackingService();
                }
                MMTrackerActivity.DeleteTrackCore((Track) TrackManagerActivity.this.m_TrackAdapter.getItem(TrackManagerActivity.this.m_TrackAdapter.selectedPos));
                if (TrackManagerActivity.this.m_bWasActive) {
                    TrackManagerActivity.this.RestartTrackingService();
                }
                if (iStoredPos <= TrackManagerActivity.this.m_TrackAdapter.getCount() - 1) {
                    TrackManagerActivity.this.m_TrackAdapter.setSelectedPosition(iStoredPos);
                    TrackManagerActivity.this.SetQuickinfo(iStoredPos);
                    TrackManagerActivity.this.SetHideButtonText(((Track) TrackManagerActivity.this.m_TrackAdapter.getItem(iStoredPos)).m_bVisible);
                    this.val$listview.setSelection(iStoredPos);
                    TrackManagerActivity.this.EnableButtons(TrackManagerActivity.BUTTON_STATE_ONE_SELECTED);
                } else {
                    TrackManagerActivity.this.EnableButtons(TrackManagerActivity.BUTTON_STATE_NO_SELECTION);
                    TrackManagerActivity.this.m_TrackAdapter.selectedPos = -1;
                    TrackManagerActivity.this.SetQuickinfo(-1);
                    this.val$listview.setAdapter(TrackManagerActivity.this.m_TrackAdapter);
                }
                this.val$listview.invalidate();
            }
        }

        /* renamed from: com.meixi.TrackManagerActivity.2.2 */
        class C00652 implements DialogInterface.OnClickListener {
            C00652() {
            }

            public void onClick(DialogInterface dialog, int which) {
            }
        }

        C00662(Context context, ListView listView) {
            this.val$c = context;
            this.val$listview = listView;
        }

        public void onClick(View v) {
            AlertDialog alertDialog = new Builder(this.val$c).create();
            alertDialog.setTitle("MM Tracker");
            if (((Track) TrackManagerActivity.this.m_TrackAdapter.getItem(TrackManagerActivity.this.m_TrackAdapter.selectedPos)).m_bActive) {
                TrackManagerActivity.this.m_bWasActive = true;
                alertDialog.setMessage("Do you want to permanently delete the Track: '" + ((Track) TrackManagerActivity.this.m_TrackAdapter.getItem(TrackManagerActivity.this.m_TrackAdapter.selectedPos)).m_sName + "'  ?");
            } else {
                TrackManagerActivity.this.m_bWasActive = false;
                alertDialog.setMessage("Do you want to permanently delete the File: '" + ((Track) TrackManagerActivity.this.m_TrackAdapter.getItem(TrackManagerActivity.this.m_TrackAdapter.selectedPos)).m_sFileName + "' which contains the Track: '" + ((Track) TrackManagerActivity.this.m_TrackAdapter.getItem(TrackManagerActivity.this.m_TrackAdapter.selectedPos)).m_sName + "'  ?");
            }
            alertDialog.setButton("Yes", new C00641(this.val$listview));
            alertDialog.setButton2("No", new C00652());
            alertDialog.show();
        }
    }

    /* renamed from: com.meixi.TrackManagerActivity.3 */
    class C00673 implements OnClickListener {
        C00673() {
        }

        public void onClick(View v) {
            if (TrackManagerActivity.this.m_TrackAdapter.selectedPos >= 0) {
                if (((Track) TrackManagerActivity.this.m_TrackAdapter.getItem(TrackManagerActivity.this.m_TrackAdapter.selectedPos)).m_bVisible) {
                    ((Track) TrackManagerActivity.this.m_TrackAdapter.getItem(TrackManagerActivity.this.m_TrackAdapter.selectedPos)).m_bVisible = false;
                } else {
                    ((Track) TrackManagerActivity.this.m_TrackAdapter.getItem(TrackManagerActivity.this.m_TrackAdapter.selectedPos)).m_bVisible = true;
                    ((Track) TrackManagerActivity.this.m_TrackAdapter.getItem(TrackManagerActivity.this.m_TrackAdapter.selectedPos)).m_bCacheVaild = false;
                }
                if (!((Track) TrackManagerActivity.this.m_TrackAdapter.getItem(TrackManagerActivity.this.m_TrackAdapter.selectedPos)).m_bActive && ((Track) TrackManagerActivity.this.m_TrackAdapter.getItem(TrackManagerActivity.this.m_TrackAdapter.selectedPos)).m_CreatedByMMTracker) {
                    ((Track) TrackManagerActivity.this.m_TrackAdapter.getItem(TrackManagerActivity.this.m_TrackAdapter.selectedPos)).ReWriteGpx(TrackManagerActivity.this.getBaseContext(), "");
                }
                TrackManagerActivity.this.SetHideButtonText(((Track) TrackManagerActivity.this.m_TrackAdapter.getItem(TrackManagerActivity.this.m_TrackAdapter.selectedPos)).m_bVisible);
                TrackManagerActivity.this.m_TrackAdapter.updateViews();
            }
        }
    }

    /* renamed from: com.meixi.TrackManagerActivity.4 */
    class C00684 implements OnClickListener {
        C00684() {
        }

        public void onClick(View v) {
            if (TrackManagerActivity.this.m_TrackAdapter.selectedPos >= 0) {
                TrackManagerActivity.this.ShowTrackStyleSettings((Track) TrackManagerActivity.this.m_TrackAdapter.getItem(TrackManagerActivity.this.m_TrackAdapter.selectedPos));
            }
        }
    }

    /* renamed from: com.meixi.TrackManagerActivity.5 */
    class C00695 implements OnClickListener {
        C00695() {
        }

        public void onClick(View v) {
            if (TrackManagerActivity.this.m_TrackAdapter.selectedPos > -1 && ((Track) TrackManagerActivity.this.m_TrackAdapter.getItem(TrackManagerActivity.this.m_TrackAdapter.selectedPos)).trackpoints.size() > 0) {
                MMTrackerActivity.m_dRequestViewLon = ((Trackpoint) ((Track) TrackManagerActivity.this.m_TrackAdapter.getItem(TrackManagerActivity.this.m_TrackAdapter.selectedPos)).trackpoints.get(TrackManagerActivity.BUTTON_STATE_NO_SELECTION)).m_dGpsLong;
                MMTrackerActivity.m_dRequestViewLat = ((Trackpoint) ((Track) TrackManagerActivity.this.m_TrackAdapter.getItem(TrackManagerActivity.this.m_TrackAdapter.selectedPos)).trackpoints.get(TrackManagerActivity.BUTTON_STATE_NO_SELECTION)).m_dGpsLat;
                MMTrackerActivity.SetSelectedTrack((Track) TrackManagerActivity.this.m_TrackAdapter.getItem(TrackManagerActivity.this.m_TrackAdapter.selectedPos));
                TrackManagerActivity.this.finish();
            }
        }
    }

    /* renamed from: com.meixi.TrackManagerActivity.6 */
    class C00706 implements OnClickListener {
        C00706() {
        }

        public void onClick(View v) {
            if (TrackManagerActivity.this.m_TrackAdapter.selectedPos > -1) {
                Intent i = new Intent(TrackManagerActivity.this.getBaseContext(), TrackDataActivity.class);
                i.putExtra("index_unit", MMTrackerActivity.m_SettingsUnitsDistances);
                i.putExtra("reset_handles", true);
                MMTrackerActivity.m_TrackToDisplayData = (Track) TrackManagerActivity.this.m_TrackAdapter.getItem(TrackManagerActivity.this.m_TrackAdapter.selectedPos);
                TrackManagerActivity.this.startActivityForResult(i, TrackManagerActivity.ACTIVITY_TRACKDATA);
            }
        }
    }

    static {
        ACTIVITY_TRACKSTYLE = BUTTON_STATE_NO_SELECTION;
        ACTIVITY_FILEBROWSER = BUTTON_STATE_ONE_SELECTED;
        ACTIVITY_TRACKDATA = BUTTON_STATE_ONE_SELECTED_NO_POINTS;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(MMTrackerActivity.m_SettingsOrientation);
        setContentView(C0047R.layout.track_manager);
        this.m_iUnits = getIntent().getExtras().getInt("units");
        this.m_SelectedTrack = MMTrackerActivity.GetSelectedTrack();
        TrackingService.m_bNoTrackUpdateSemaphore = true;
        if (MMTrackerActivity.tracks != null && MMTrackerActivity.tracks.size() > BUTTON_STATE_ONE_SELECTED) {
            Collections.sort(MMTrackerActivity.tracks);
        }
        TrackingService.m_bNoTrackUpdateSemaphore = false;
        this.m_TrackAdapter = new TrackOverlayAdapter(this, BUTTON_STATE_NO_SELECTION, MMTrackerActivity.tracks);
        this.m_TrackAdapter.setNotifyOnChange(true);
        ListView listview = (ListView) findViewById(C0047R.id.ListViewTracks);
        listview.setAdapter(this.m_TrackAdapter);
        this.m_DeleteButton = (Button) findViewById(C0047R.id.buttonTrackDelete);
        this.m_HideButton = (Button) findViewById(C0047R.id.buttonTrackHide);
        this.m_PropsButton = (Button) findViewById(C0047R.id.buttonTrackProps);
        this.m_ViewButton = (Button) findViewById(C0047R.id.buttonTrackView);
        this.m_DataButton = (Button) findViewById(C0047R.id.buttonTrackData);
        EnableButtons(BUTTON_STATE_NO_SELECTION);
        if (this.m_SelectedTrack != null && this.m_TrackAdapter.getPosition(this.m_SelectedTrack) >= 0) {
            this.m_TrackAdapter.setSelectedPosition(this.m_TrackAdapter.getPosition(this.m_SelectedTrack));
            SetQuickinfo(this.m_TrackAdapter.getPosition(this.m_SelectedTrack));
            SetHideButtonText(((Track) this.m_TrackAdapter.getItem(this.m_TrackAdapter.selectedPos)).m_bVisible);
            listview.setSelection(this.m_TrackAdapter.getPosition(this.m_SelectedTrack));
            EnableButtons(BUTTON_STATE_ONE_SELECTED);
        }
        listview.setOnItemClickListener(new C00631());
        this.m_DeleteButton.setOnClickListener(new C00662(this, listview));
        this.m_HideButton.setOnClickListener(new C00673());
        this.m_PropsButton.setOnClickListener(new C00684());
        this.m_ViewButton.setOnClickListener(new C00695());
        this.m_DataButton.setOnClickListener(new C00706());
    }

    private void StopTrackingService() {
        if (MMTrackerActivity.m_ServiceIBinder != null) {
            MMTrackerActivity.m_ServiceIBinder.stopRecording();
        }
        do {
        } while (TrackingService.m_bRecordingActive);
    }

    private void RestartTrackingService() {
        if (MMTrackerActivity.m_ServiceIBinder != null) {
            MMTrackerActivity.m_ServiceIBinder.restartRecording();
        }
    }

    void SetHideButtonText(boolean visible) {
        if (visible) {
            this.m_HideButton.setText(getString(C0047R.string.TrackManagerActivity_hide));
        } else {
            this.m_HideButton.setText(getString(C0047R.string.TrackManagerActivity_show));
        }
    }

    private void ShowTrackStyleSettings(Track SelectedTrack) {
        if (SelectedTrack != null) {
            this.m_SelectedTrack = SelectedTrack;
            Editor editor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
            editor.putString("textTrackName", this.m_SelectedTrack.m_sName);
            Object[] objArr = new Object[BUTTON_STATE_ONE_SELECTED];
            objArr[BUTTON_STATE_NO_SELECTION] = Integer.valueOf(SelectedTrack.m_iColor & 16777215);
            editor.putString("listTrackColor", String.format("%08X", objArr));
            objArr = new Object[BUTTON_STATE_ONE_SELECTED];
            objArr[BUTTON_STATE_NO_SELECTION] = Integer.valueOf(Math.round(SelectedTrack.m_fWidth));
            editor.putString("listTrackWidth", String.format("%d", objArr));
            objArr = new Object[BUTTON_STATE_ONE_SELECTED];
            objArr[BUTTON_STATE_NO_SELECTION] = Integer.valueOf(Tools.OpacityGranulation((this.m_SelectedTrack.m_iColor >> 24) & 255));
            editor.putString("listTrackOpacity", String.format("%d", objArr));
            editor.commit();
            startActivityForResult(new Intent(getBaseContext(), TrackStyleActivity.class), ACTIVITY_TRACKSTYLE);
        }
    }

    void SetQuickinfo(int iIndex) {
        String s;
        if (iIndex > -1) {
            int iCount = ((Track) MMTrackerActivity.tracks.get(iIndex)).trackpoints.size();
            double dLen = ((Track) MMTrackerActivity.tracks.get(iIndex)).CalcLengthKm();
            s = String.format("Points: %d   Length: %.2f %s", new Object[]{Integer.valueOf(iCount), Double.valueOf(Tools.m_dUnitDistanceFactor[this.m_iUnits] * dLen), Tools.m_sUnitDistance[this.m_iUnits]});
        } else {
            s = "";
        }
        ((TextView) findViewById(C0047R.id.textTrackProps)).setText(s);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_TRACKSTYLE && this.m_SelectedTrack != null) {
            boolean bNameChanged = false;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String s = prefs.getString("listTrackColor", "FFFF0000");
            try {
                this.m_SelectedTrack.m_iColor = (int) Long.parseLong(s, 16);
            } catch (NumberFormatException e) {
                this.m_SelectedTrack.m_iColor = -65536;
            }
            s = prefs.getString("listTrackWidth", "4");
            try {
                this.m_SelectedTrack.m_fWidth = (float) Double.parseDouble(s);
            } catch (NumberFormatException e2) {
                this.m_SelectedTrack.m_fWidth = 4.0f;
            }
            s = prefs.getString("listTrackOpacity", "255");
            try {
                this.m_SelectedTrack.m_iColor = (this.m_SelectedTrack.m_iColor & 16777215) | (((int) Long.parseLong(s)) << 24);
            } catch (NumberFormatException e3) {
            }
            if (this.m_SelectedTrack.m_sName.compareToIgnoreCase(prefs.getString("textTrackName", "no_name")) != 0) {
                bNameChanged = true;
            }
            this.m_SelectedTrack.m_sName = prefs.getString("textTrackName", "no_name");
            if (!this.m_SelectedTrack.m_CreatedByMMTracker) {
                Toast.makeText(getBaseContext(), "Track properties not stored permanently, as this track was not generated by MM Tracker.", BUTTON_STATE_ONE_SELECTED).show();
            } else if (bNameChanged) {
                this.m_SelectedTrack.ReWriteGpx(getBaseContext(), new StringBuilder(String.valueOf(this.m_SelectedTrack.m_sName)).append(".gpx").toString());
            } else {
                this.m_SelectedTrack.ReWriteGpx(getBaseContext(), "");
            }
            this.m_TrackAdapter.updateViews();
            this.m_SelectedTrack = null;
        }
        if (requestCode == ACTIVITY_FILEBROWSER && data != null && resultCode == BUTTON_STATE_ONE_SELECTED) {
            String name;
            if (data.getStringExtra(MapCacheHelper.KEY_NAME).length() < 4) {
                name = new String("Loaded Track");
            } else {
                name = data.getStringExtra(MapCacheHelper.KEY_NAME);
            }
            Tools.ReadGPXtoTracks(data.getStringExtra("file"), name.substring(BUTTON_STATE_NO_SELECTION, name.length() - 4), MMTrackerActivity.tracks, MMTrackerActivity.m_SettingsMinTrackResolution, false, 0);
            this.m_TrackAdapter.updateViews();
            MMTrackerActivity.m_bRequestAllTrRefresh = true;
        }
    }

    public void EnableButtons(int iState) {
        if (this.m_DeleteButton != null && this.m_HideButton != null && this.m_PropsButton != null && this.m_ViewButton != null && this.m_DataButton != null) {
            switch (iState) {
                case BUTTON_STATE_NO_SELECTION /*0*/:
                    this.m_DeleteButton.setEnabled(false);
                    this.m_HideButton.setEnabled(false);
                    this.m_PropsButton.setEnabled(false);
                    this.m_ViewButton.setEnabled(false);
                    this.m_DataButton.setEnabled(false);
                case BUTTON_STATE_ONE_SELECTED /*1*/:
                    this.m_DeleteButton.setEnabled(true);
                    this.m_HideButton.setEnabled(true);
                    this.m_PropsButton.setEnabled(true);
                    this.m_ViewButton.setEnabled(true);
                    this.m_DataButton.setEnabled(true);
                case BUTTON_STATE_ONE_SELECTED_NO_POINTS /*2*/:
                    this.m_DeleteButton.setEnabled(true);
                    this.m_HideButton.setEnabled(true);
                    this.m_PropsButton.setEnabled(true);
                    this.m_ViewButton.setEnabled(false);
                    this.m_DataButton.setEnabled(true);
                default:
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0047R.menu.track_menu, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int i;
        Track t;
        switch (item.getItemId()) {
            case C0047R.id.itemLoadTr:
                Intent inte = new Intent(this, FileListActivity.class);
                inte.putExtra("path", MMTrackerActivity.m_SettingsTrackPath);
                inte.putExtra("extension", "gpx");
                inte.putExtra("header", getString(C0047R.string.TrackManagerActivity_file_browser_header));
                if (TrackingService.m_RecordTrack == null || !MMTrackerActivity.m_bTrackRecording) {
                    inte.putExtra("exclude", "");
                } else {
                    inte.putExtra("exclude", TrackingService.m_RecordTrack.m_sFileName);
                }
                startActivityForResult(inte, ACTIVITY_FILEBROWSER);
                return true;
            case C0047R.id.itemHideAllTr:
                for (i = BUTTON_STATE_NO_SELECTION; i < this.m_TrackAdapter.getCount(); i += BUTTON_STATE_ONE_SELECTED) {
                    t = (Track) this.m_TrackAdapter.getItem(i);
                    if (t.m_bVisible) {
                        t.m_bVisible = false;
                        if (t.m_CreatedByMMTracker && !t.m_bActive) {
                            t.ReWriteGpx(getBaseContext(), "");
                        }
                    }
                }
                SetHideButtonText(false);
                this.m_TrackAdapter.updateViews();
                return true;
            case C0047R.id.itemUnhideAllTr:
                for (i = BUTTON_STATE_NO_SELECTION; i < this.m_TrackAdapter.getCount(); i += BUTTON_STATE_ONE_SELECTED) {
                    t = (Track) this.m_TrackAdapter.getItem(i);
                    if (!t.m_bVisible) {
                        t.m_bVisible = true;
                        t.m_bCacheVaild = false;
                        if (t.m_CreatedByMMTracker && !t.m_bActive) {
                            t.ReWriteGpx(getBaseContext(), "");
                        }
                    }
                }
                SetHideButtonText(true);
                this.m_TrackAdapter.updateViews();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
