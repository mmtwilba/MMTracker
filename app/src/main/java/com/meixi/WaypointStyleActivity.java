package com.meixi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import com.meixi.Tools.StringCoord;

public class WaypointStyleActivity extends Activity {
    public static int ACTIVITY_EDIT_COORDINATE;
    Button m_ButtonEdit;
    CheckBox m_CheckLocked;
    CheckBox m_CheckShowLabel;
    EditText m_EditTextWpCoord;
    EditText m_EditTextWpDesc;
    EditText m_EditTextWpName;
    Spinner m_SpinnerColor;
    Spinner m_SpinnerIcon;
    Spinner m_SpinnerOpacity;
    double m_dWpLat;
    double m_dWpLon;

    /* renamed from: com.meixi.WaypointStyleActivity.1 */
    class C00791 implements OnClickListener {
        C00791() {
        }

        public void onClick(View v) {
            Intent coordinateActivity = new Intent(WaypointStyleActivity.this.getBaseContext(), CoordinateEntryActivity.class);
            coordinateActivity.putExtra("latitude", WaypointStyleActivity.this.m_dWpLat);
            coordinateActivity.putExtra("longitude", WaypointStyleActivity.this.m_dWpLon);
            coordinateActivity.putExtra("use_init_coords", true);
            coordinateActivity.putExtra("dialog_simple", true);
            WaypointStyleActivity.this.startActivityForResult(coordinateActivity, WaypointStyleActivity.ACTIVITY_EDIT_COORDINATE);
        }
    }

    public WaypointStyleActivity() {
        this.m_dWpLat = 0.0d;
        this.m_dWpLon = 0.0d;
    }

    static {
        ACTIVITY_EDIT_COORDINATE = 1;
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setRequestedOrientation(MMTrackerActivity.m_SettingsOrientation);
        setContentView(C0047R.layout.waypoint_style);
        MMTrackerActivity.m_dRequestViewLon = 9999.0d;
        MMTrackerActivity.m_dRequestViewLat = 9999.0d;
        MMTrackerActivity.m_requestedWpRefresh = null;
        this.m_dWpLat = getIntent().getExtras().getDouble("waypoint_lat");
        this.m_dWpLon = getIntent().getExtras().getDouble("waypoint_lon");
        this.m_EditTextWpName = (EditText) findViewById(C0047R.id.editTextWpName);
        this.m_EditTextWpName.setText(getIntent().getExtras().getString("textWaypointName"));
        this.m_EditTextWpCoord = (EditText) findViewById(C0047R.id.editTextWpCoordinates);
        this.m_EditTextWpCoord.setText(RefreshCoordinateText());
        this.m_EditTextWpDesc = (EditText) findViewById(C0047R.id.editTextWpDescription);
        this.m_EditTextWpDesc.setText(getIntent().getExtras().getString("textWaypointDescription"));
        this.m_CheckLocked = (CheckBox) findViewById(C0047R.id.CheckBoxWpLock);
        this.m_CheckLocked.setChecked(getIntent().getExtras().getBoolean("checkboxWaypointLocked"));
        this.m_CheckShowLabel = (CheckBox) findViewById(C0047R.id.CheckBoxWpShowLabel);
        this.m_CheckShowLabel.setChecked(getIntent().getExtras().getBoolean("checkboxWaypointLabel"));
        this.m_SpinnerIcon = (Spinner) findViewById(C0047R.id.spinnerWpIcon);
        this.m_SpinnerIcon.setSelection(Tools.GetArrayIndexPosition(getResources().getStringArray(C0047R.array.listValuesWpIcon), getIntent().getExtras().getString("listWaypointIcon")));
        this.m_SpinnerColor = (Spinner) findViewById(C0047R.id.spinnerWpColor);
        this.m_SpinnerColor.setSelection(Tools.GetArrayIndexPosition(getResources().getStringArray(C0047R.array.listValuesTrackColor), getIntent().getExtras().getString("listWaypointColor")));
        this.m_SpinnerOpacity = (Spinner) findViewById(C0047R.id.spinnerWpOpacity);
        this.m_SpinnerOpacity.setSelection(Tools.GetArrayIndexPosition(getResources().getStringArray(C0047R.array.listValuesTrackOpacity), getIntent().getExtras().getString("listWaypointOpacity")));
        this.m_ButtonEdit = (Button) findViewById(C0047R.id.buttonCoordinateEdit);
        this.m_ButtonEdit.setOnClickListener(new C00791());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_EDIT_COORDINATE && data != null) {
            this.m_dWpLat = data.getDoubleExtra("result_lat", this.m_dWpLat);
            this.m_dWpLon = data.getDoubleExtra("result_lon", this.m_dWpLon);
            this.m_EditTextWpCoord.setText(RefreshCoordinateText());
            MMTrackerActivity.m_requestedWpRefresh = MMTrackerActivity.m_SelectedWaypoint;
        }
    }

    String RefreshCoordinateText() {
        if (MMTrackerActivity.m_SettingsPositionType == 5) {
            OsgbCoord OsgbTemp = new OsgbCoord();
            OsgbTemp.setPrecision(5);
            if (OsgbTemp.SetLatLonCoord(this.m_dWpLat, this.m_dWpLon, 0.0d)) {
                return OsgbTemp.GetOsgbAsText();
            }
            StringCoord c = Tools.CoordToString(this.m_dWpLat, this.m_dWpLon, 0, true);
            return c.sLat + "  " + c.sLong;
        }
        c = Tools.CoordToString(this.m_dWpLat, this.m_dWpLon, MMTrackerActivity.m_SettingsPositionType, true);
        return c.sLat + "  " + c.sLong;
    }

    public void finish() {
        Intent i = new Intent();
        i.putExtra("textWaypointName", this.m_EditTextWpName.getText().toString());
        i.putExtra("textWaypointDescription", this.m_EditTextWpDesc.getText().toString());
        i.putExtra("listWaypointColor", getResources().getStringArray(C0047R.array.listValuesTrackColor)[this.m_SpinnerColor.getSelectedItemPosition()]);
        i.putExtra("listWaypointOpacity", getResources().getStringArray(C0047R.array.listValuesTrackOpacity)[this.m_SpinnerOpacity.getSelectedItemPosition()]);
        i.putExtra("listWaypointIcon", getResources().getStringArray(C0047R.array.listValuesWpIcon)[this.m_SpinnerIcon.getSelectedItemPosition()]);
        i.putExtra("checkboxWaypointLocked", this.m_CheckLocked.isChecked());
        i.putExtra("checkboxWaypointLabel", this.m_CheckShowLabel.isChecked());
        i.putExtra("new_lat", this.m_dWpLat);
        i.putExtra("new_lon", this.m_dWpLon);
        setResult(-1, i);
        super.finish();
    }
}
