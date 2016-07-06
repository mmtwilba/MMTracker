package com.meixi;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TrackDataActivity extends Activity {
    final Handler mHandler;
    final Runnable mUpdateData;
    final Runnable mUpdateResults;
    View m_MainView;
    ArrayList<Trackpoint> m_NewTrackpoints;
    int m_StoreTrackPointsCount;
    Track m_TrackToDisplay;
    boolean m_bAllDataRead;
    boolean m_bDataThreadRunning;
    boolean m_bResetHandles;
    boolean m_bThreadFinished;
    boolean m_bWaitForUI;
    int m_iProgressBarWidth;
    int m_iReadDataCount;
    int m_iTotalLoadedData;
    int m_iUnitsIndex;
    String m_sCurrentFileName;
    View vw1;

    /* renamed from: com.meixi.TrackDataActivity.1 */
    class C00571 implements Runnable {
        C00571() {
        }

        public void run() {
            if (TrackDataActivity.this.m_bAllDataRead) {
                TrackDataActivity.this.m_TrackToDisplay.clear();
                TrackDataActivity.this.m_TrackToDisplay.setTrackpoints(TrackDataActivity.this.m_NewTrackpoints);
            }
            if (TrackDataActivity.this.m_iReadDataCount % 250 == 0 || TrackDataActivity.this.m_bAllDataRead) {
                TrackDataActivity.this.DisplayTrackData(TrackDataActivity.this.m_bAllDataRead, true, false, TrackDataActivity.this.m_NewTrackpoints);
                TrackDataActivity.this.SetProgress(TrackDataActivity.this.m_iReadDataCount, TrackDataActivity.this.m_StoreTrackPointsCount);
            }
            TrackDataActivity.this.m_bWaitForUI = false;
        }
    }

    /* renamed from: com.meixi.TrackDataActivity.2 */
    class C00582 implements Runnable {
        C00582() {
        }

        public void run() {
            TrackingService.m_bTrackDataUpdateInProgress = true;
            while (TrackingService.m_bTrackDataUpdateHasToWait) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }
            TrackDataActivity.this.DisplayTrackData(true, false, true, TrackDataActivity.this.m_TrackToDisplay.trackpoints);
            TrackDataActivity.this.m_bWaitForUI = false;
            TrackingService.m_bTrackDataUpdateInProgress = false;
        }
    }

    /* renamed from: com.meixi.TrackDataActivity.3 */
    class C00593 implements Runnable {
        C00593() {
        }

        public void run() {
            TrackDataActivity.this.m_bThreadFinished = false;
            while (TrackDataActivity.this.m_bDataThreadRunning) {
                if (!(TrackDataActivity.this.m_bAllDataRead || TrackDataActivity.this.m_bWaitForUI)) {
                    if (TrackDataActivity.this.m_TrackToDisplay != null) {
                        if (Tools.ReReadOneLineOfGPXtrack(TrackDataActivity.this.m_TrackToDisplay, TrackDataActivity.this.m_NewTrackpoints, MMTrackerActivity.m_SettingsMinTrackResolution) != Tools.LOAD_RESULT_OK) {
                            TrackDataActivity.this.m_bAllDataRead = true;
                            TrackDataActivity.this.m_bDataThreadRunning = false;
                            TrackDataActivity.this.m_TrackToDisplay.m_AllDetailsAvailable = true;
                        } else {
                            TrackDataActivity trackDataActivity = TrackDataActivity.this;
                            trackDataActivity.m_iReadDataCount++;
                        }
                    }
                    TrackDataActivity.this.m_bWaitForUI = true;
                    TrackDataActivity.this.mHandler.post(TrackDataActivity.this.mUpdateResults);
                }
            }
            TrackDataActivity.this.m_bThreadFinished = true;
        }
    }

    /* renamed from: com.meixi.TrackDataActivity.4 */
    class C00604 implements Runnable {
        C00604() {
        }

        public void run() {
            while (TrackDataActivity.this.m_bDataThreadRunning) {
                if (!TrackDataActivity.this.m_bWaitForUI && TrackingService.m_bTriggerTrackDataUpdate) {
                    TrackingService.m_bTriggerTrackDataUpdate = false;
                    TrackDataActivity.this.m_bWaitForUI = true;
                    if (!TrackDataActivity.this.mHandler.post(TrackDataActivity.this.mUpdateData)) {
                        TrackDataActivity.this.m_bWaitForUI = false;
                    }
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public TrackDataActivity() {
        this.m_bWaitForUI = false;
        this.m_bDataThreadRunning = false;
        this.m_iTotalLoadedData = 0;
        this.m_bThreadFinished = true;
        this.m_bAllDataRead = false;
        this.m_MainView = null;
        this.m_bResetHandles = false;
        this.m_NewTrackpoints = new ArrayList();
        this.mHandler = new Handler();
        this.mUpdateResults = new C00571();
        this.mUpdateData = new C00582();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(MMTrackerActivity.m_SettingsOrientation);
        this.m_TrackToDisplay = MMTrackerActivity.m_TrackToDisplayData;
        if (this.m_TrackToDisplay == null) {
            this.vw1 = null;
            setContentView(C0047R.layout.track_data);
        } else if (this.m_TrackToDisplay.m_bActive) {
            this.vw1 = null;
            setContentView(C0047R.layout.track_data_current);
        } else {
            setContentView(C0047R.layout.track_data);
            this.vw1 = findViewById(C0047R.id.viewTrackLoadProgressBar);
        }
        this.m_MainView = findViewById(C0047R.id.TrackDataView);
        this.m_iUnitsIndex = getIntent().getExtras().getInt("index_unit");
        this.m_bResetHandles = getIntent().getExtras().getBoolean("reset_handles");
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.m_iProgressBarWidth = metrics.widthPixels;
        SetProgress(0, 100);
        if (this.m_TrackToDisplay == null) {
            return;
        }
        if (this.m_TrackToDisplay.m_bActive || this.m_TrackToDisplay.m_AllDetailsAvailable) {
            DisplayTrackData(true, false, false, this.m_TrackToDisplay.trackpoints);
            if (this.m_TrackToDisplay.m_bActive) {
                this.m_bDataThreadRunning = true;
                StartRealtimeThread();
                return;
            }
            return;
        }
        this.m_StoreTrackPointsCount = this.m_TrackToDisplay.trackpoints.size();
        if (Tools.OpenGPXforReReadTrack(this.m_TrackToDisplay) == Tools.LOAD_RESULT_OK) {
            this.m_bDataThreadRunning = true;
            this.m_iReadDataCount = 0;
            this.m_bAllDataRead = false;
            this.m_bWaitForUI = false;
            MMTrackerActivity.m_requestedTrRefresh = this.m_TrackToDisplay;
            StartDataThread();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.m_iProgressBarWidth = metrics.widthPixels;
    }

    public void onStart() {
        super.onStart();
        if (MMTrackerActivity.m_SettingsKeepDisplayOn) {
            getWindow().setFlags(128, 128);
        } else {
            getWindow().clearFlags(128);
        }
    }

    private void DisplayTrackData(boolean bFinal, boolean bCalcSpeed, boolean bCurrent, ArrayList<Trackpoint> tpoints) {
        Date date = new Date();
        if (this.m_TrackToDisplay != null) {
            long lDelta;
            if (bFinal) {
                if (bCalcSpeed && !this.m_TrackToDisplay.m_bHasRealSpeedValues) {
                    this.m_TrackToDisplay.CreateSpeedValues();
                }
                ((TextView) findViewById(C0047R.id.TrackMaxVel)).setText(String.format("%.1f %s", new Object[]{Double.valueOf(this.m_TrackToDisplay.CalcMaxSpeed() * Tools.m_dUnitSpeedFactor[this.m_iUnitsIndex]), Tools.m_sUnitSpeed[this.m_iUnitsIndex]}));
                ((TextView) findViewById(C0047R.id.TrackMinVel)).setText(String.format("%.1f %s", new Object[]{Double.valueOf(this.m_TrackToDisplay.CalcMinSpeed() * Tools.m_dUnitSpeedFactor[this.m_iUnitsIndex]), Tools.m_sUnitSpeed[this.m_iUnitsIndex]}));
                ((TextView) findViewById(C0047R.id.TrackMeanVel)).setText(String.format("%.1f %s", new Object[]{Double.valueOf(this.m_TrackToDisplay.CalcMeanSpeed() * Tools.m_dUnitSpeedFactor[this.m_iUnitsIndex]), Tools.m_sUnitSpeed[this.m_iUnitsIndex]}));
                ((TextView) findViewById(C0047R.id.TrackMeanMoveVel)).setText(String.format("%.1f %s", new Object[]{Double.valueOf(this.m_TrackToDisplay.CalcMeanMovementSpeed() * Tools.m_dUnitSpeedFactor[this.m_iUnitsIndex]), Tools.m_sUnitSpeed[this.m_iUnitsIndex]}));
                ((TextView) findViewById(C0047R.id.TrackLength)).setText(String.format("%.2f %s", new Object[]{Double.valueOf(this.m_TrackToDisplay.CalcLengthKm() * Tools.m_dUnitDistanceFactor[this.m_iUnitsIndex]), Tools.m_sUnitDistance[this.m_iUnitsIndex]}));
                ((TextView) findViewById(C0047R.id.TrackMaxHeight)).setText(String.format("%.1f %s", new Object[]{Double.valueOf(this.m_TrackToDisplay.CalcMaxHeight() * Tools.m_dUnitHeightFactor[this.m_iUnitsIndex]), Tools.m_sUnitHeight[this.m_iUnitsIndex]}));
                ((TextView) findViewById(C0047R.id.TrackMinHeight)).setText(String.format("%.1f %s", new Object[]{Double.valueOf(this.m_TrackToDisplay.CalcMinHeight() * Tools.m_dUnitHeightFactor[this.m_iUnitsIndex]), Tools.m_sUnitHeight[this.m_iUnitsIndex]}));
            }
            ((TextView) findViewById(C0047R.id.text_track_name)).setText(this.m_TrackToDisplay.m_sName);
            ((TextView) findViewById(C0047R.id.TrackCount)).setText(String.format("%d", new Object[]{Integer.valueOf(tpoints.size())}));
            if (tpoints.size() > 0) {
                date.setTime(((Trackpoint) tpoints.get(0)).m_lTime);
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                ((TextView) findViewById(C0047R.id.TrackStartDate)).setText(String.format("%s", new Object[]{sdf.format(date)}));
                sdf = new SimpleDateFormat("HH:mm:ss");
                ((TextView) findViewById(C0047R.id.TrackStartTime)).setText(String.format("%s", new Object[]{sdf.format(date)}));
                date.setTime(((Trackpoint) tpoints.get(tpoints.size() - 1)).m_lTime);
                sdf = new SimpleDateFormat("dd.MM.yyyy");
                ((TextView) findViewById(C0047R.id.TrackEndDate)).setText(String.format("%s", new Object[]{sdf.format(date)}));
                sdf = new SimpleDateFormat("HH:mm:ss");
                ((TextView) findViewById(C0047R.id.TrackEndTime)).setText(String.format("%s", new Object[]{sdf.format(date)}));
                lDelta = (long) Math.round((float) ((((Trackpoint) tpoints.get(tpoints.size() - 1)).m_lTime - ((Trackpoint) tpoints.get(0)).m_lTime) / 1000));
                ((TextView) findViewById(C0047R.id.TrackDurationTotal)).setText(String.format("%d:%02d:%02d", new Object[]{Long.valueOf(lDelta / 3600), Long.valueOf((lDelta % 3600) / 60), Long.valueOf((lDelta % 3600) % 60)}));
            }
            lDelta = this.m_TrackToDisplay.CalcMovingTime();
            ((TextView) findViewById(C0047R.id.TrackDurationMove)).setText(String.format("%d:%02d:%02d", new Object[]{Long.valueOf(lDelta / 3600), Long.valueOf((lDelta % 3600) / 60), Long.valueOf((lDelta % 3600) % 60)}));
            if (bCurrent) {
                ((TextView) findViewById(C0047R.id.TrackCurentVel)).setText(String.format("%.1f %s", new Object[]{Double.valueOf(MMTrackerActivity.m_dGpsSpeed * Tools.m_dUnitSpeedFactor[this.m_iUnitsIndex]), Tools.m_sUnitSpeed[this.m_iUnitsIndex]}));
                ((TextView) findViewById(C0047R.id.TrackCurrentHeight)).setText(String.format("%.1f %s", new Object[]{Double.valueOf(MMTrackerActivity.m_dGpsAltitude * Tools.m_dUnitHeightFactor[this.m_iUnitsIndex]), Tools.m_sUnitHeight[this.m_iUnitsIndex]}));
                ((TextView) findViewById(C0047R.id.TrackCurentBearing)).setText(String.format("%.1f\u00b0", new Object[]{Double.valueOf(MMTrackerActivity.m_dGpsBearing)}));
            }
        }
    }

    private void SetProgress(int count, int base) {
        if (this.vw1 != null && base > 0 && count >= 0) {
            this.vw1.getLayoutParams().width = (int) Math.round((((double) this.m_iProgressBarWidth) * ((double) count)) / ((double) base));
            this.vw1.requestLayout();
        }
    }

    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            this.m_bWaitForUI = false;
            this.m_bDataThreadRunning = false;
            TrackingService.m_bTrackDataUpdateInProgress = false;
        }
    }

    private void StartDataThread() {
        new Thread(new C00593()).start();
    }

    private void StartRealtimeThread() {
        new Thread(new C00604()).start();
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == 84 && this.m_MainView != null) {
            if (event.getAction() == 0 && event.getRepeatCount() == 0) {
                this.m_MainView.getKeyDispatcherState().startTracking(event, this);
                return true;
            } else if (event.getAction() == 1) {
                this.m_MainView.getKeyDispatcherState().handleUpEvent(event);
                if (event.isTracking() && !event.isCanceled()) {
                    StartGraph(MMTrackerActivity.GRAPH_TYPE_SPEED);
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void StartGraph(int iType) {
        if (this.m_TrackToDisplay != null && this.m_TrackToDisplay.m_AllDetailsAvailable) {
            this.m_bWaitForUI = false;
            this.m_bDataThreadRunning = false;
            TrackingService.m_bTrackDataUpdateInProgress = false;
            Intent i = new Intent(getBaseContext(), TrackGraphActivity.class);
            i.putExtra("type", iType);
            i.putExtra("reset_handles", this.m_bResetHandles);
            this.m_bResetHandles = false;
            startActivity(i);
            finish();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0047R.menu.track_data_menu, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(C0047R.id.itemTrackDataStatistics).setEnabled(false);
        if (this.m_TrackToDisplay == null) {
            menu.findItem(C0047R.id.itemTrackDataAltitude).setEnabled(false);
            menu.findItem(C0047R.id.itemTrackDataVelocity).setEnabled(false);
        } else if (this.m_TrackToDisplay.m_AllDetailsAvailable) {
            menu.findItem(C0047R.id.itemTrackDataAltitude).setEnabled(true);
            menu.findItem(C0047R.id.itemTrackDataVelocity).setEnabled(true);
        } else {
            menu.findItem(C0047R.id.itemTrackDataAltitude).setEnabled(false);
            menu.findItem(C0047R.id.itemTrackDataVelocity).setEnabled(false);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case C0047R.id.itemTrackDataStatistics:
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
