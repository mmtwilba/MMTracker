package com.meixi;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class RouteDataActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(MMTrackerActivity.m_SettingsOrientation);
        setContentView(C0047R.layout.route_data);
        int iIndex = getIntent().getExtras().getInt("index_unit");
        String sLen = String.format("%.2f %s", new Object[]{Double.valueOf(getIntent().getExtras().getDouble("length") * Tools.m_dUnitDistanceFactor[iIndex]), Tools.m_sUnitDistance[iIndex]});
        ((TextView) findViewById(C0047R.id.text_route_name)).setText(getIntent().getExtras().getString(MapCacheHelper.KEY_NAME));
        ((TextView) findViewById(C0047R.id.text_route_length)).setText(sLen);
        ((TextView) findViewById(C0047R.id.text_route_points)).setText(getIntent().getExtras().getString("points"));
    }
}
