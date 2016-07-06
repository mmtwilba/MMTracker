package com.meixi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

public class TrackGraphActivity extends Activity {
    final Handler mHandler;
    final Runnable mUpdateData;
    private TrackGraphView m_GraphView;
    SharedPreferences m_Prefs;
    Track m_TrackToDisplay;
    boolean m_bDataThreadRunning;
    boolean m_bWaitForUI;
    public int m_iDisplayDensity;
    public int m_iGraphType;

    /* renamed from: com.meixi.TrackGraphActivity.1 */
    class C00611 implements Runnable {
        C00611() {
        }

        public void run() {
            TrackingService.m_bTrackDataUpdateInProgress = true;
            while (TrackingService.m_bTrackDataUpdateHasToWait) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }
            TrackGraphActivity.this.m_GraphView.Init(false);
            TrackGraphActivity.this.m_GraphView.FullInvalidate();
            TrackGraphActivity.this.m_bWaitForUI = false;
            TrackingService.m_bTrackDataUpdateInProgress = false;
        }
    }

    /* renamed from: com.meixi.TrackGraphActivity.2 */
    class C00622 implements Runnable {
        C00622() {
        }

        public void run() {
            while (TrackGraphActivity.this.m_bDataThreadRunning) {
                if (!TrackGraphActivity.this.m_bWaitForUI && TrackingService.m_bTriggerTrackDataUpdate) {
                    TrackingService.m_bTriggerTrackDataUpdate = false;
                    TrackGraphActivity.this.m_bWaitForUI = true;
                    if (!TrackGraphActivity.this.mHandler.post(TrackGraphActivity.this.mUpdateData)) {
                        TrackGraphActivity.this.m_bWaitForUI = false;
                    }
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public TrackGraphActivity() {
        this.m_bWaitForUI = false;
        this.m_bDataThreadRunning = false;
        this.m_TrackToDisplay = null;
        this.mHandler = new Handler();
        this.mUpdateData = new C00611();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(MMTrackerActivity.m_SettingsOrientation);
        this.m_TrackToDisplay = MMTrackerActivity.m_TrackToDisplayData;
        this.m_Prefs = getPreferences(0);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.m_iDisplayDensity = metrics.densityDpi;
        this.m_GraphView = null;
        if (getIntent().getExtras().getBoolean("reset_handles")) {
            this.m_GraphView = new TrackGraphView(this, 0.0d, 100.0d);
        } else {
            this.m_GraphView = new TrackGraphView(this, (double) this.m_Prefs.getFloat("HandleLeftPos", 0.0f), (double) this.m_Prefs.getFloat("HandleRightPos", 100.0f));
        }
        setContentView(this.m_GraphView);
        this.m_iGraphType = getIntent().getExtras().getInt("type");
        this.m_GraphView.requestFocus();
        StartDisplayThread();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.m_bWaitForUI = false;
        this.m_bDataThreadRunning = false;
        TrackingService.m_bTrackDataUpdateInProgress = false;
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
        }
        StartDisplayThread();
    }

    public void onStart() {
        super.onStart();
        if (MMTrackerActivity.m_SettingsKeepDisplayOn) {
            getWindow().setFlags(128, 128);
        } else {
            getWindow().clearFlags(128);
        }
    }

    private void StartDisplayThread() {
        if (this.m_TrackToDisplay != null && this.m_TrackToDisplay.m_bActive && this.m_TrackToDisplay.m_AllDetailsAvailable) {
            this.m_bDataThreadRunning = true;
            StartRealtimeThread();
        }
    }

    protected void onPause() {
        super.onPause();
        if (this.m_GraphView != null) {
            Editor ed = this.m_Prefs.edit();
            ed.putFloat("HandleLeftPos", (float) this.m_GraphView.m_dHandleLeftPos);
            ed.putFloat("HandleRightPos", (float) this.m_GraphView.m_dHandleRightPos);
            ed.commit();
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == 84) {
            if (event.getAction() == 0 && event.getRepeatCount() == 0) {
                this.m_GraphView.getKeyDispatcherState().startTracking(event, this);
                return true;
            } else if (event.getAction() == 1) {
                this.m_GraphView.getKeyDispatcherState().handleUpEvent(event);
                if (event.isTracking() && !event.isCanceled()) {
                    if (this.m_iGraphType == MMTrackerActivity.GRAPH_TYPE_SPEED) {
                        StartGraph(MMTrackerActivity.GRAPH_TYPE_HEIGHT);
                        return true;
                    }
                    StartStatistics();
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            this.m_bWaitForUI = false;
            this.m_bDataThreadRunning = false;
            TrackingService.m_bTrackDataUpdateInProgress = false;
        }
    }

    private void StartRealtimeThread() {
        new Thread(new C00622()).start();
    }

    private void StartGraph(int iType) {
        if (this.m_TrackToDisplay.m_AllDetailsAvailable) {
            this.m_bWaitForUI = false;
            this.m_bDataThreadRunning = false;
            TrackingService.m_bTrackDataUpdateInProgress = false;
            Intent i = new Intent(getBaseContext(), TrackGraphActivity.class);
            i.putExtra("type", iType);
            i.putExtra("reset_handles", false);
            startActivity(i);
            finish();
        }
    }

    private void StartStatistics() {
        Intent i = new Intent(this, TrackDataActivity.class);
        i.putExtra("index_unit", MMTrackerActivity.m_SettingsUnitsDistances);
        i.putExtra("reset_handles", false);
        startActivity(i);
        finish();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0047R.menu.track_data_menu, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        if (this.m_iGraphType == MMTrackerActivity.GRAPH_TYPE_SPEED) {
            menu.findItem(C0047R.id.itemTrackDataVelocity).setEnabled(false);
        }
        if (this.m_iGraphType == MMTrackerActivity.GRAPH_TYPE_HEIGHT) {
            menu.findItem(C0047R.id.itemTrackDataAltitude).setEnabled(false);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case C0047R.id.itemTrackDataStatistics:
                StartStatistics();
                return true;
            case C0047R.id.itemTrackDataVelocity:
                StartGraph(MMTrackerActivity.GRAPH_TYPE_SPEED);
                return true;
            case C0047R.id.itemTrackDataAltitude:
                StartGraph(MMTrackerActivity.GRAPH_TYPE_HEIGHT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
