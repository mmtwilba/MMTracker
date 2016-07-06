package com.meixi;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CoordinateEntryActivity extends Activity implements TextWatcher {
    Button bt1;
    CheckBox m_CheckboxShow;
    EditText m_EditOsgbCoord;
    EditText[] m_EditTextGrad;
    EditText m_EditTextName;
    RadioButton m_RadioButtonTypeDeg;
    RadioButton m_RadioButtonTypeOsgb;
    boolean m_bInitCoords;
    boolean m_bSimpleDialog;
    double m_dLatitude;
    double m_dLongitude;
    OsgbCoord osgb;

    /* renamed from: com.meixi.CoordinateEntryActivity.1 */
    class C00001 implements OnClickListener {
        C00001() {
        }

        public void onClick(View v) {
            CoordinateEntryActivity.this.m_EditOsgbCoord.setEnabled(false);
            for (int i = 0; i < 8; i++) {
                CoordinateEntryActivity.this.m_EditTextGrad[i].setEnabled(true);
            }
        }
    }

    /* renamed from: com.meixi.CoordinateEntryActivity.2 */
    class C00012 implements OnClickListener {
        C00012() {
        }

        public void onClick(View v) {
            CoordinateEntryActivity.this.m_EditOsgbCoord.setEnabled(true);
            for (int i = 0; i < 8; i++) {
                CoordinateEntryActivity.this.m_EditTextGrad[i].setEnabled(false);
            }
        }
    }

    /* renamed from: com.meixi.CoordinateEntryActivity.3 */
    class C00023 implements OnClickListener {
        C00023() {
        }

        public void onClick(View v) {
            double dLon = CoordinateEntryActivity.this.osgb.m_dLon;
            double dLat = CoordinateEntryActivity.this.osgb.m_dLat;
            Intent i = new Intent();
            i.putExtra("result_lon", dLon);
            i.putExtra("result_lat", dLat);
            if (CoordinateEntryActivity.this.m_bSimpleDialog) {
                i.putExtra("result_name", " ");
                i.putExtra("result_jump", false);
            } else {
                i.putExtra("result_name", CoordinateEntryActivity.this.m_EditTextName.getText().toString());
                i.putExtra("result_jump", CoordinateEntryActivity.this.m_CheckboxShow.isChecked());
            }
            CoordinateEntryActivity.this.setResult(1, i);
            CoordinateEntryActivity.this.finish();
        }
    }

    public CoordinateEntryActivity() {
        this.m_EditTextGrad = new EditText[8];
        this.m_dLongitude = 0.0d;
        this.m_dLatitude = 0.0d;
        this.m_bInitCoords = true;
        this.m_bSimpleDialog = true;
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        if (!(getIntent() == null || getIntent().getExtras() == null)) {
            this.m_dLongitude = getIntent().getExtras().getDouble("longitude");
            this.m_dLatitude = getIntent().getExtras().getDouble("latitude");
            this.m_bInitCoords = getIntent().getExtras().getBoolean("use_init_coords");
            this.m_bSimpleDialog = getIntent().getExtras().getBoolean("dialog_simple");
        }
        setRequestedOrientation(MMTrackerActivity.m_SettingsOrientation);
        if (this.m_bSimpleDialog) {
            setContentView(C0047R.layout.coordinate_entry_simple);
        } else {
            setContentView(C0047R.layout.coordinate_entry);
        }
        MMTrackerActivity.m_dRequestViewLon = 9999.0d;
        MMTrackerActivity.m_dRequestViewLat = 9999.0d;
        MMTrackerActivity.m_requestedWpRefresh = null;
        this.m_EditOsgbCoord = (EditText) findViewById(C0047R.id.editTextOsgb);
        this.m_EditOsgbCoord.addTextChangedListener(this);
        this.m_EditTextGrad[0] = (EditText) findViewById(C0047R.id.editTextDegLat);
        this.m_EditTextGrad[1] = (EditText) findViewById(C0047R.id.editTextMinLat);
        this.m_EditTextGrad[2] = (EditText) findViewById(C0047R.id.editTextSecLat);
        this.m_EditTextGrad[3] = (EditText) findViewById(C0047R.id.editTextDirLat);
        this.m_EditTextGrad[4] = (EditText) findViewById(C0047R.id.editTextDegLon);
        this.m_EditTextGrad[5] = (EditText) findViewById(C0047R.id.editTextMinLon);
        this.m_EditTextGrad[6] = (EditText) findViewById(C0047R.id.editTextSecLon);
        this.m_EditTextGrad[7] = (EditText) findViewById(C0047R.id.editTextDirLon);
        for (int i = 0; i < 8; i++) {
            this.m_EditTextGrad[i].addTextChangedListener(this);
        }
        this.m_RadioButtonTypeDeg = (RadioButton) findViewById(C0047R.id.radioButtonDeg);
        this.m_RadioButtonTypeOsgb = (RadioButton) findViewById(C0047R.id.radioButtonOsgb);
        if (!this.m_bSimpleDialog) {
            this.m_CheckboxShow = (CheckBox) findViewById(C0047R.id.CheckBoxGotoCoord);
            this.m_EditTextName = (EditText) findViewById(C0047R.id.editTextWpNameAtCoord);
            Date date = new Date();
            this.m_EditTextName.setText(new SimpleDateFormat("'WP_'yyMMdd'-'HHmmss").format(date));
        }
        this.bt1 = (Button) findViewById(C0047R.id.buttonCoordinateEntry);
        this.osgb = new OsgbCoord();
        this.m_RadioButtonTypeDeg.setOnClickListener(new C00001());
        this.m_RadioButtonTypeOsgb.setOnClickListener(new C00012());
        this.bt1.setOnClickListener(new C00023());
        if (this.m_bInitCoords) {
            FillTextBoxes(this.m_dLatitude, this.m_dLongitude);
        }
    }

    private void FillTextBoxes(double dLat, double dLon) {
        boolean bValid = true;
        if (MMTrackerActivity.m_SettingsPositionType == 5) {
            this.osgb.setPrecision(5);
            bValid = this.osgb.SetLatLonCoord(dLat, dLon, 0.0d);
        }
        RadioButton radioButton = this.m_RadioButtonTypeOsgb;
        boolean z = MMTrackerActivity.m_SettingsPositionType > 4 && bValid;
        radioButton.setChecked(z);
        radioButton = this.m_RadioButtonTypeDeg;
        z = MMTrackerActivity.m_SettingsPositionType <= 4 || !bValid;
        radioButton.setChecked(z);
        EditText editText = this.m_EditOsgbCoord;
        z = MMTrackerActivity.m_SettingsPositionType > 4 && bValid;
        editText.setEnabled(z);
        for (int i = 0; i < 8; i++) {
            editText = this.m_EditTextGrad[i];
            z = MMTrackerActivity.m_SettingsPositionType <= 4 || !bValid;
            editText.setEnabled(z);
        }
        if (MMTrackerActivity.m_SettingsPositionType == 0) {
            this.m_EditTextGrad[0].setText(String.format("%.5f", new Object[]{Double.valueOf(dLat)}).replace(",", "."));
            this.m_EditTextGrad[4].setText(String.format("%.5f", new Object[]{Double.valueOf(dLon)}).replace(",", "."));
            this.m_EditTextGrad[3].setText("N");
            this.m_EditTextGrad[7].setText("E");
        } else if (MMTrackerActivity.m_SettingsPositionType == 1) {
            this.m_EditTextGrad[0].setText(String.format("%.5f", new Object[]{Double.valueOf(Math.abs(dLat))}).replace(",", "."));
            this.m_EditTextGrad[4].setText(String.format("%.5f", new Object[]{Double.valueOf(Math.abs(dLon))}).replace(",", "."));
            if (dLat >= 0.0d) {
                this.m_EditTextGrad[3].setText("N");
            } else {
                this.m_EditTextGrad[3].setText("S");
            }
            if (dLon >= 0.0d) {
                this.m_EditTextGrad[3].setText("E");
            } else {
                this.m_EditTextGrad[3].setText("W");
            }
        } else if (MMTrackerActivity.m_SettingsPositionType == 2) {
            this.m_EditTextGrad[0].setText(String.format("%d", new Object[]{Long.valueOf(Math.round(Math.floor(Math.abs(dLat))))}));
            this.m_EditTextGrad[4].setText(String.format("%d", new Object[]{Long.valueOf(Math.round(Math.floor(Math.abs(dLon))))}));
            this.m_EditTextGrad[1].setText(String.format("%.3f", new Object[]{Double.valueOf(60.0d * (Math.abs(dLat) - Math.floor(Math.abs(dLat))))}).replace(",", "."));
            this.m_EditTextGrad[5].setText(String.format("%.3f", new Object[]{Double.valueOf(60.0d * (Math.abs(dLon) - Math.floor(Math.abs(dLon))))}).replace(",", "."));
            if (dLat >= 0.0d) {
                this.m_EditTextGrad[3].setText("N");
            } else {
                this.m_EditTextGrad[3].setText("S");
            }
            if (dLon >= 0.0d) {
                this.m_EditTextGrad[7].setText("E");
            } else {
                this.m_EditTextGrad[7].setText("W");
            }
        } else if (MMTrackerActivity.m_SettingsPositionType == 3 || !bValid) {
            this.m_EditTextGrad[0].setText(String.format("%d", new Object[]{Long.valueOf(Math.round(Math.floor(Math.abs(dLat))))}));
            this.m_EditTextGrad[4].setText(String.format("%d", new Object[]{Long.valueOf(Math.round(Math.floor(Math.abs(dLon))))}));
            double dLatMinutes = 60.0d * (Math.abs(dLat) - Math.floor(Math.abs(dLat)));
            this.m_EditTextGrad[1].setText(String.format("%d", new Object[]{Long.valueOf(Math.round(Math.floor(dLatMinutes)))}).replace(",", "."));
            double dLonMinutes = 60.0d * (Math.abs(dLon) - Math.floor(Math.abs(dLon)));
            this.m_EditTextGrad[5].setText(String.format("%d", new Object[]{Long.valueOf(Math.round(Math.floor(dLonMinutes)))}).replace(",", "."));
            this.m_EditTextGrad[2].setText(String.format("%.1f", new Object[]{Double.valueOf(60.0d * (dLatMinutes - Math.floor(dLatMinutes)))}).replace(",", "."));
            this.m_EditTextGrad[6].setText(String.format("%.1f", new Object[]{Double.valueOf(60.0d * (dLonMinutes - Math.floor(dLonMinutes)))}).replace(",", "."));
            if (dLat >= 0.0d) {
                this.m_EditTextGrad[3].setText("N");
            } else {
                this.m_EditTextGrad[3].setText("S");
            }
            if (dLon >= 0.0d) {
                this.m_EditTextGrad[7].setText("E");
            } else {
                this.m_EditTextGrad[7].setText("W");
            }
        } else if (MMTrackerActivity.m_SettingsPositionType == 5) {
            this.m_EditOsgbCoord.setText(this.osgb.GetOsgbAsText());
        }
    }

    private Location ConvertTextFieldsToCoordinate() {
        Location loc = new Location("dummy");
        double dMinuteLat = 0.0d;
        double dSecondLat = 0.0d;
        double dSignLat = 1.0d;
        double dMinuteLon = 0.0d;
        double dSecondLon = 0.0d;
        double dSignLon = 1.0d;
        if (!this.m_EditTextGrad[3].getText().toString().trim().toUpperCase().equals("N")) {
            if (!this.m_EditTextGrad[3].getText().toString().trim().toUpperCase().equals("S")) {
                if (!this.m_EditTextGrad[3].getText().toString().trim().equals("")) {
                    return null;
                }
            }
        }
        if (!this.m_EditTextGrad[7].getText().toString().trim().toUpperCase().equals("W")) {
            if (!this.m_EditTextGrad[7].getText().toString().trim().toUpperCase().equals("E")) {
                if (!this.m_EditTextGrad[7].getText().toString().trim().equals("")) {
                    return null;
                }
            }
        }
        try {
            double dGradLat = Double.parseDouble(this.m_EditTextGrad[0].getText().toString());
            if (this.m_EditTextGrad[1].getText().toString().trim().length() > 0) {
                try {
                    dMinuteLat = Double.parseDouble(this.m_EditTextGrad[1].getText().toString());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            if (this.m_EditTextGrad[2].getText().toString().trim().length() > 0) {
                try {
                    dSecondLat = Double.parseDouble(this.m_EditTextGrad[2].getText().toString());
                } catch (NumberFormatException e2) {
                    return null;
                }
            }
            try {
                double dGradLon = Double.parseDouble(this.m_EditTextGrad[4].getText().toString());
                if (this.m_EditTextGrad[5].getText().toString().trim().length() > 0) {
                    try {
                        dMinuteLon = Double.parseDouble(this.m_EditTextGrad[5].getText().toString());
                    } catch (NumberFormatException e3) {
                        return null;
                    }
                }
                if (this.m_EditTextGrad[6].getText().toString().trim().length() > 0) {
                    try {
                        dSecondLon = Double.parseDouble(this.m_EditTextGrad[6].getText().toString());
                    } catch (NumberFormatException e4) {
                        return null;
                    }
                }
                if (this.m_EditTextGrad[3].getText().toString().trim().toUpperCase().equals("S")) {
                    dSignLat = -1.0d;
                }
                if (this.m_EditTextGrad[7].getText().toString().trim().toUpperCase().equals("W")) {
                    dSignLon = -1.0d;
                }
                loc.setLatitude((((dMinuteLat / 60.0d) + dGradLat) + (dSecondLat / 3600.0d)) * dSignLat);
                loc.setLongitude((((dMinuteLon / 60.0d) + dGradLon) + (dSecondLon / 3600.0d)) * dSignLon);
                return loc;
            } catch (NumberFormatException e5) {
                return null;
            }
        } catch (NumberFormatException e6) {
            return null;
        }
    }

    public void afterTextChanged(Editable s) {
        Location loc;
        if (s == this.m_EditOsgbCoord.getText()) {
            if (this.osgb.ParseOsgbFromString(s.toString())) {
                this.m_EditOsgbCoord.setTextColor(-16716288);
                this.bt1.setEnabled(true);
                return;
            }
            loc = Tools.ConvertTextToLatLon(s.toString());
            if (loc != null) {
                this.osgb.SetLatLonCoord(loc.getLatitude(), loc.getLongitude(), 0.0d);
                this.osgb.setPrecision(5);
                this.m_EditOsgbCoord.setTextColor(-16716288);
                this.bt1.setEnabled(true);
                return;
            }
            this.m_EditOsgbCoord.setTextColor(-65536);
            this.bt1.setEnabled(false);
        } else if (s == this.m_EditTextGrad[0].getText() || s == this.m_EditTextGrad[1].getText() || s == this.m_EditTextGrad[2].getText() || s == this.m_EditTextGrad[3].getText() || s == this.m_EditTextGrad[4].getText() || s == this.m_EditTextGrad[5].getText() || s == this.m_EditTextGrad[6].getText() || s == this.m_EditTextGrad[7].getText()) {
            loc = ConvertTextFieldsToCoordinate();
            int i;
            if (loc != null) {
                this.osgb.SetLatLonCoord(loc.getLatitude(), loc.getLongitude(), 0.0d);
                this.osgb.setPrecision(5);
                for (i = 0; i < 8; i++) {
                    this.m_EditTextGrad[i].setTextColor(-16716288);
                }
                this.bt1.setEnabled(true);
                return;
            }
            for (i = 0; i < 8; i++) {
                this.m_EditTextGrad[i].setTextColor(-65536);
            }
            this.bt1.setEnabled(false);
        }
    }

    public void beforeTextChanged(CharSequence ch, int a, int b, int c) {
    }

    public void onTextChanged(CharSequence ch, int a, int b, int c) {
    }
}
