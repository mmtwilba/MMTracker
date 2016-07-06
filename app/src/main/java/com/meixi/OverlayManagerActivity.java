package com.meixi;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class OverlayManagerActivity extends TabActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(MMTrackerActivity.m_SettingsOrientation);
        setContentView(C0047R.layout.overlay_manager);
        int iUnits = getIntent().getExtras().getInt("units");
        int iGrid = getIntent().getExtras().getInt("grid");
        MMTrackerActivity.m_dRequestViewLon = 9999.0d;
        MMTrackerActivity.m_dRequestViewLat = 9999.0d;
        MMTrackerActivity.m_requestedWpRefresh = null;
        TabHost tabHost = getTabHost();
        Intent intent = new Intent().setClass(this, TrackManagerActivity.class);
        intent.putExtra("units", iUnits);
        tabHost.addTab(tabHost.newTabSpec("tracks").setIndicator(getString(C0047R.string.OverlayManagerActivity_tracks)).setContent(intent));
        intent = new Intent().setClass(this, RouteManagerActivity.class);
        intent.putExtra("units", iUnits);
        tabHost.addTab(tabHost.newTabSpec("routes").setIndicator(getString(C0047R.string.OverlayManagerActivity_routes)).setContent(intent));
        intent = new Intent().setClass(this, WaypointManagerActivity.class);
        intent.putExtra("grid", iGrid);
        tabHost.addTab(tabHost.newTabSpec("waypoints").setIndicator(getString(C0047R.string.OverlayManagerActivity_waypoints)).setContent(intent));
        tabHost.addTab(tabHost.newTabSpec("mmisearch").setIndicator(getString(C0047R.string.OverlayManagerActivity_search)).setContent(new Intent().setClass(this, MmiSearchActivity.class)));
        tabHost.getTabWidget().getChildAt(0).getLayoutParams().height = MMTrackerActivity.m_ScreenHeight / 12;
        tabHost.getTabWidget().getChildAt(1).getLayoutParams().height = MMTrackerActivity.m_ScreenHeight / 12;
        tabHost.getTabWidget().getChildAt(2).getLayoutParams().height = MMTrackerActivity.m_ScreenHeight / 12;
        tabHost.getTabWidget().getChildAt(3).getLayoutParams().height = MMTrackerActivity.m_ScreenHeight / 12;
        if (MMTrackerActivity.GetSelectedTrack() != null) {
            tabHost.setCurrentTab(0);
        } else if (MMTrackerActivity.GetSelectedRoutepoint() != null) {
            tabHost.setCurrentTab(1);
        } else if (MMTrackerActivity.GetSelectedWaypoint() != null) {
            tabHost.setCurrentTab(2);
        }
    }
}
