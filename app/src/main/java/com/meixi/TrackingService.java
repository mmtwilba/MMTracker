package com.meixi;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import com.meixi.MMTrackerActivity.ITrackingService;
import java.util.ArrayList;

public class TrackingService extends Service implements LocationListener {
    public static Track m_RecordTrack;
    public static int m_SettingsGpsDataRate;
    public static double m_SettingsGpsDistanceRate;
    public static boolean m_bNoTrackUpdateSemaphore;
    public static boolean m_bRecordingActive;
    public static boolean m_bSettingsGeoidCorrection;
    public static boolean m_bTrackDataUpdateHasToWait;
    public static boolean m_bTrackDataUpdateInProgress;
    public static boolean m_bTrackReadyToTakeOver;
    public static boolean m_bTriggerTrackDataUpdate;
    GeoidCorrection m_GeoidTable;
    LocationListener m_LocationListener;
    LocationManager m_LocationManager;
    SharedPreferences m_Prefs;
    String m_SettingsTrackPath;
    boolean m_bCreateFirstTrackpoint;
    int m_iDistanceTriggerIndex;
    int m_iRestartedTrackingActive;
    int m_iTrackColor;
    int m_iTrackWidth;
    long m_lGpsOldTime;
    private MyServiceBinder myServiceBinder;

    public class MyServiceBinder extends Binder implements ITrackingService {
        public void stopRecording() {
            if (TrackingService.m_RecordTrack != null) {
                TrackingService.m_RecordTrack.CloseGPX(TrackingService.this.getApplicationContext());
                TrackingService.m_RecordTrack.m_bActive = false;
            }
            TrackingService.m_bRecordingActive = false;
            Editor ed = TrackingService.this.m_Prefs.edit();
            ed.putInt("ServicePrefTrackingActive", 0);
            ed.commit();
            if (!(TrackingService.this.m_LocationManager == null || TrackingService.this.m_LocationListener == null)) {
                TrackingService.this.m_LocationManager.removeUpdates(TrackingService.this.m_LocationListener);
            }
            TrackingService.m_RecordTrack = null;
        }

        public void restartRecording() {
            TrackingService.m_bRecordingActive = true;
            TrackingService.this.m_bCreateFirstTrackpoint = true;
            Editor ed = TrackingService.this.m_Prefs.edit();
            ed.putInt("ServicePrefTrackingActive", 1);
            ed.commit();
            if (TrackingService.this.m_LocationManager.getProvider("gps") != null) {
                TrackingService.this.m_LocationManager.requestLocationUpdates("gps", 0, 0.0f, TrackingService.this.m_LocationListener);
            }
        }
    }

    public TrackingService() {
        this.myServiceBinder = new MyServiceBinder();
    }

    public void onCreate() {
        this.m_Prefs = PreferenceManager.getDefaultSharedPreferences(this);
        this.m_LocationListener = this;
        m_bRecordingActive = false;
        m_RecordTrack = null;
        m_bTrackReadyToTakeOver = false;
        m_bTrackDataUpdateHasToWait = false;
        m_bTriggerTrackDataUpdate = false;
        m_bTrackDataUpdateInProgress = false;
        m_bNoTrackUpdateSemaphore = false;
        m_bSettingsGeoidCorrection = false;
        this.m_GeoidTable = new GeoidCorrection(this);
        this.m_LocationManager = (LocationManager) getSystemService("location");
    }

    public IBinder onBind(Intent intent) {
        return this.myServiceBinder;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        Notification notification = new Notification(C0047R.drawable.statusbar_icon_tracking, getText(C0047R.string.TrackingService_TickerMessage), System.currentTimeMillis());
        notification.setLatestEventInfo(this, getText(C0047R.string.TrackingService_NotificationTitle), getText(C0047R.string.TrackingService_NotificationText), PendingIntent.getActivity(this, 0, new Intent(this, TrackingService.class), 0));
        startForeground(16274836, notification);
        if (intent != null) {
            this.m_SettingsTrackPath = intent.getStringExtra("TrackPath");
            this.m_iTrackColor = intent.getIntExtra("TrackColor", MMTrackerActivity.CalcColor(MMTrackerActivity.m_iSettingsColorNewTrack, -16777216));
            this.m_iTrackWidth = intent.getIntExtra("TrackWidth", Track.TRACK_WIDTH_DEFAULT);
            m_SettingsGpsDistanceRate = intent.getDoubleExtra("GpsDistanceRate", 0.0d);
            m_SettingsGpsDataRate = intent.getIntExtra("GpsDataRate", 1000);
            m_bSettingsGeoidCorrection = intent.getBooleanExtra("UseGeoidCorrection", false);
            Editor ed = this.m_Prefs.edit();
            ed.putString("ServicePrefTrackFile", "");
            ed.putInt("ServicePrefTrackColor", this.m_iTrackColor);
            ed.putInt("ServicePrefTrackWidth", this.m_iTrackWidth);
            ed.putInt("ServicePrefTrackingActive", 1);
            ed.putInt("ServicePrefGpsDataRate", m_SettingsGpsDataRate);
            ed.putFloat("ServicePrefGpsDistanceRate", (float) m_SettingsGpsDistanceRate);
            ed.putBoolean("ServicePrefUseGeoidCorrection", m_bSettingsGeoidCorrection);
            ed.commit();
        } else {
            String SettingsTrackFile = this.m_Prefs.getString("ServicePrefTrackFile", "");
            this.m_iTrackColor = this.m_Prefs.getInt("ServicePrefTrackColor", MMTrackerActivity.CalcColor(MMTrackerActivity.m_iSettingsColorNewTrack, -16777216));
            this.m_iTrackWidth = this.m_Prefs.getInt("ServicePrefTrackWidth", Track.TRACK_WIDTH_DEFAULT);
            this.m_iRestartedTrackingActive = this.m_Prefs.getInt("ServicePrefTrackingActive", 0);
            m_SettingsGpsDataRate = this.m_Prefs.getInt("ServicePrefGpsDataRate", 1000);
            m_SettingsGpsDistanceRate = (double) this.m_Prefs.getFloat("ServicePrefGpsDistanceRate", 0.0f);
            m_bSettingsGeoidCorrection = this.m_Prefs.getBoolean("ServicePrefUseGeoidCorrection", false);
            if (this.m_iRestartedTrackingActive == 1) {
                ArrayList<Track> tlist = new ArrayList();
                Tools.ReadGPXtoTracks(SettingsTrackFile, "", tlist, 0.0d, true, 0);
                if (tlist.size() > 0) {
                    m_RecordTrack = (Track) tlist.get(0);
                }
                if (m_RecordTrack != null) {
                    m_RecordTrack.m_CreatedByMMTracker = true;
                    m_RecordTrack.m_AllDetailsAvailable = true;
                    m_RecordTrack.m_bActive = true;
                    m_RecordTrack.m_iColor = this.m_iTrackColor;
                    m_RecordTrack.m_fWidth = (float) this.m_iTrackWidth;
                    m_RecordTrack.m_bWriteStarted = true;
                    m_bTrackReadyToTakeOver = true;
                }
            } else {
                stopSelf();
            }
        }
        if (this.m_LocationManager.getProvider("gps") != null) {
            this.m_LocationManager.requestLocationUpdates("gps", 0, 0.0f, this);
        }
        m_bRecordingActive = true;
        this.m_bCreateFirstTrackpoint = true;
        return 1;
    }

    public void onDestroy() {
    }

    public void onLocationChanged(Location location) {
        if (location != null && m_bRecordingActive && !m_bNoTrackUpdateSemaphore) {
            double dGpsAltitude;
            if (m_bSettingsGeoidCorrection) {
                dGpsAltitude = this.m_GeoidTable.Compensate(location.getLatitude(), location.getLongitude(), location.getAltitude());
            } else {
                dGpsAltitude = location.getAltitude();
            }
            boolean bDistanceTrigger = false;
            if (m_RecordTrack != null) {
                double dDistance = m_RecordTrack.CalcLengthKm(this.m_iDistanceTriggerIndex);
                if (m_RecordTrack.trackpoints.size() > 0) {
                    dDistance += Tools.calcDistance(((Trackpoint) m_RecordTrack.trackpoints.get(m_RecordTrack.trackpoints.size() - 1)).m_dGpsLat, ((Trackpoint) m_RecordTrack.trackpoints.get(m_RecordTrack.trackpoints.size() - 1)).m_dGpsLong, location.getLatitude(), location.getLongitude());
                } else {
                    dDistance = 0.0d;
                }
                if (dDistance >= m_SettingsGpsDistanceRate / 1000.0d) {
                    bDistanceTrigger = true;
                }
            }
            boolean bTimeTrigger = false;
            if (location.getTime() - this.m_lGpsOldTime >= ((long) m_SettingsGpsDataRate) && m_SettingsGpsDataRate != 9999999) {
                bTimeTrigger = true;
            }
            if (bTimeTrigger || bDistanceTrigger || this.m_bCreateFirstTrackpoint) {
                if (m_RecordTrack == null) {
                    m_RecordTrack = new Track(this.m_SettingsTrackPath);
                    m_RecordTrack.m_CreatedByMMTracker = true;
                    m_RecordTrack.m_AllDetailsAvailable = true;
                    m_RecordTrack.m_bActive = true;
                    m_RecordTrack.m_iColor = this.m_iTrackColor;
                    m_RecordTrack.m_fWidth = (float) this.m_iTrackWidth;
                    m_bTrackReadyToTakeOver = true;
                    Editor ed = this.m_Prefs.edit();
                    ed.putString("ServicePrefTrackFile", m_RecordTrack.m_sFileName);
                    ed.commit();
                }
                m_bTrackDataUpdateHasToWait = true;
                while (m_bTrackDataUpdateInProgress) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                }
                m_RecordTrack.add(location.getLongitude(), location.getLatitude(), 0.0f, 0.0f, dGpsAltitude, location.getTime(), (double) location.getSpeed());
                if (bDistanceTrigger) {
                    this.m_iDistanceTriggerIndex = m_RecordTrack.trackpoints.size() - 1;
                }
                m_bTrackDataUpdateHasToWait = false;
                this.m_bCreateFirstTrackpoint = false;
                if (location.hasSpeed()) {
                    m_RecordTrack.m_bHasRealSpeedValues = true;
                }
                m_RecordTrack.WriteLastTrackpointToGPX(getApplicationContext());
                this.m_lGpsOldTime = location.getTime();
                m_bTriggerTrackDataUpdate = true;
            }
        }
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
