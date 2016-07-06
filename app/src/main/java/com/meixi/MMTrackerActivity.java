package com.meixi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MMTrackerActivity extends Activity implements LocationListener {
    public static int ACTIVITY_COORDINATE = 0;
    public static int ACTIVITY_FILEBROWSER = 0;
    public static int ACTIVITY_MAPLIST = 0;
    public static int ACTIVITY_OVERLAYMANAGER = 0;
    public static int ACTIVITY_ROUTESTYLE = 0;
    public static int ACTIVITY_SETTINGS = 0;
    public static int ACTIVITY_TRACKDATA = 0;
    public static int ACTIVITY_TRACKSTYLE = 0;
    public static int ACTIVITY_WAYPOINTSTYLE = 0;
    public static double ARROW_MIN_SPEED_TO_SHOW = 0.0d;
    public static double ARROW_MIN_SPEED_TO_SWITCH_TO_COMPASS = 0.0d;
    public static int COMPASS_FILTER = 0;
    public static int CONTEXT_MENU_ACTIONS = 0;
    public static int CONTEXT_MENU_ACTIONS_SUBMENU_WP = 0;
    public static int CONTEXT_MENU_ROUTES = 0;
    public static int CONTEXT_MENU_TRACKS = 0;
    public static int CONTEXT_MENU_WAYPOINTS = 0;
    public static boolean DEBUG_GPS_SIMULATION = false;
    public static int DISTANCE_TO_TRACK_FOR_MENU = 0;
    public static int GRAPH_TYPE_HEIGHT = 0;
    public static int GRAPH_TYPE_SPEED = 0;
    public static int ID_DIALOG_SEARCHING = 0;
    public static int INITIAL_ZOOM_LEVEL = 0;
    public static int LARGER_SCALE = 0;
    public static int LOCATION_TYPE_GPS = 0;
    public static int LOCATION_TYPE_NETWORK = 0;
    public static int MAP_POSITION_INVALID = 0;
    public static int MIN_TIME_COMPASS_REFRESH = 0;
    public static int MIN_TRACK_RESOLUTION_PIXEL = 0;
    public static double NAVIGATION_LEG_TOLERANCE = 0.0d;
    public static double NAVIGATION_TARGET_DISTANCE = 0.0d;
    public static int NEW_POSITION_DEFAULT = 0;
    public static int NEW_POSITION_KEEP_OLD = 0;
    public static int NEW_POSITION_SET = 0;
    public static int NO_CHANGE_IN_ZOOM_LEVEL = 0;
    public static int POSITION_ARRAY_SIZE = 0;
    public static final String PREFS_NAME = "MyPrefsFile";
    public static int SMALLER_SCALE;
    public static int STATE_MAPCACHE_CHECK_DB;
    public static int STATE_MAPCACHE_CHECK_DIRS;
    public static int STATE_MAPCACHE_FINISHED;
    public static int STATE_MAPCACHE_READ_DB;
    public static int STATE_MAPCACHE_READ_DIRS;
    public static long TIMER_GPS_SIM_TICKS;
    public static long TIMER_MAIN_TICKS;
    public static long TIMER_MAPCACHE_TICKS;
    public static int TIME_TO_CONTEXT_MENU;
    public static long WAIT_FOR_SECOND_BACKBUTTON;
    public static String WHATSNEW_STRING;
    static Bitmap m_BitmapSmallGraph;
    static String m_CurrentMapName;
    static Route m_CurrentlyCreatedRoute;
    static NavigationTarget m_NavigationTarget;
    static int m_ScreenHeight;
    static int m_ScreenWidth;
    static Routepoint m_SelectedRoutepoint;
    static Track m_SelectedTrack;
    static Waypoint m_SelectedWaypoint;
    static ServiceConnection m_ServiceConn;
    static IBinder m_ServiceIBinder;
    static int m_SettingsGpsDataRate;
    static double m_SettingsGpsDistanceRate;
    static boolean m_SettingsKeepDisplayOn;
    static String m_SettingsMapPath;
    static double m_SettingsMinTrackResolution;
    static int m_SettingsOrientation;
    static int m_SettingsPositionType;
    static String m_SettingsRoutePath;
    static String m_SettingsTrackPath;
    static int m_SettingsUnitsAngles;
    static int m_SettingsUnitsDistances;
    static String m_SettingsWaypointPath;
    static Track m_TrackToDisplayData;
    static boolean m_bMmiListIsOnMap;
    static boolean m_bRequestAllRtRefresh;
    static boolean m_bRequestAllTrRefresh;
    static boolean m_bRequestAllWpRefresh;
    static boolean m_bRequestRouteCreation;
    static boolean m_bSettingsShowCompass;
    static boolean m_bTrackRecording;
    static boolean m_bTracksLoaded;
    static Canvas m_canvasSmallGraph;
    static double m_dCalculatedGpsAngle;
    static double m_dCalculatedGpsHeading;
    static double m_dCalculatedGpsSpeed;
    static double m_dCalculatedNavigationBearing;
    static double m_dGpsAltitude;
    static double m_dGpsBearing;
    static double m_dGpsSpeed;
    static double m_dRequestViewLat;
    static double m_dRequestViewLon;
    static int m_iScreenOrientation;
    static int m_iSettingsColorNewTrack;
    static int m_iSettingsMapRotation;
    static int m_iSettingsUseCompassForVector;
    static int m_iSettingsWidthNewTrack;
    static int m_iTimerCounter;
    static Track m_requestedTrRefresh;
    static Waypoint m_requestedWpRefresh;
    static String m_sLastLoadedMmiFile;
    static ArrayList<MapList> maps;
    static ArrayList<Route> routes;
    static ArrayList<Track> tracks;
    static ArrayList<Waypoint> waypoints;
    private Runnable TimerGpsSim_Tick;
    Timer TimerMain;
    private Runnable TimerMain_Tick;
    Timer TimerMapCache;
    private Runnable TimerMapCache_Tick;
    QctTile aTile;
    boolean bAvoidRotation;
    Location[] gps_list;
    private final SensorEventListener m_AccListener;
    private Sensor m_AccSensor;
    int m_ActiveContextMenu;
    private final SensorEventListener m_CompassListener;
    private Sensor m_CompassSensor;
    Cursor m_CursorCheckMapCache;
    GeoidCorrection m_GeoidTable;
    Track m_GpsSimulationTrack;
    long m_LastLocationMillisGps;
    long m_LastLocationMillisNetwork;
    private LocationManager m_LocationManager;
    MapView m_MapView;
    Date m_NavigationStartTime;
    int m_NavigationTargetIndex;
    double m_NavigationTotalDist;
    boolean m_PositionLock;
    SharedPreferences m_Prefs;
    QuickChartFile m_Qct;
    boolean m_ScreenLock;
    private SensorManager m_SensorManager;
    boolean m_SettingUseNetworkTriangulation;
    int m_SettingsArrowLength;
    boolean m_SettingsAutostartGpsLock;
    boolean m_SettingsAutostartTracking;
    String m_SettingsCurrentMap;
    boolean m_SettingsHideStatusbar;
    boolean m_SettingsInvertDPad;
    boolean m_SettingsKeepTrackingAlive;
    boolean m_SettingsLoadRtOnStartup;
    int m_SettingsLoadTrOnStartupCount;
    int m_SettingsLoadTrOnStartupDate;
    boolean m_SettingsLoadWpOnStartup;
    int m_SettingsNaviDisplay;
    int m_SettingsNightMode;
    boolean m_SettingsShowHeadingAndBearing;
    boolean m_SettingsShowMapscaleButtons;
    boolean m_SettingsShowPosition;
    boolean m_SettingsShowPositionWindow;
    boolean m_SettingsShowScale;
    boolean m_SettingsShowStausline;
    boolean m_SettingsShowTrackInfo;
    boolean m_SettingsShowZoombuttons;
    boolean m_SettingsUseClickEnterButton;
    boolean m_SettingsUseClickSearchButton;
    boolean m_SettingsUseGeoidCorrection;
    boolean m_SettingsUseLongClickSearchButton;
    boolean m_SettingsUseVolButtons;
    Timer m_TimerGpsSimulation;
    TileQueueManager m_Tqm;
    boolean m_bAccBasedLandscapeNormal;
    boolean m_bAppInPause;
    boolean m_bAutostartLockDone;
    boolean m_bChangeMapScale;
    boolean m_bChangingScreenOrientation;
    boolean m_bContextMenuActive;
    boolean m_bCreateFirstTrackpoint;
    boolean m_bGpsFix;
    boolean m_bIsRestart;
    boolean m_bMapCacheDbThreadFinished;
    boolean m_bNetworkFix;
    boolean m_bOverlayManagerRunning;
    boolean m_bPositionLockCheck;
    boolean m_bRefreshMap;
    boolean m_bSettingsAdaptZoomlevel;
    boolean m_bSettingsAskBeforeStopTracking;
    boolean m_bSettingsShowCompassHeading;
    boolean m_bSettingsShowETA;
    boolean m_bSettingsShowTrueNorth;
    boolean m_bShowDisclaimer;
    boolean m_bStandstillDetected;
    boolean m_bWaitForFullscreen;
    double m_dCompassCos;
    double m_dCompassSin;
    double m_dCosMapRotation;
    double m_dCosMapRotationNeg;
    double m_dGpsLatitude;
    double m_dGpsLongitude;
    double m_dOldArrowDeltaX;
    double m_dOldArrowDeltaY;
    double m_dSinMapRotation;
    double m_dSinMapRotationNeg;
    double m_dStoreLatForMaplist;
    double m_dStoreLongForMaplist;
    MapCacheDbAdapter m_dbMapCache;
    float m_fAccSensorX;
    float m_fCompassOrientationCorrection;
    float m_fCompassValue;
    float m_fGpsAccuracy;
    float m_fMagneticDeclination;
    float m_fMapRotation;
    int m_iBackButtonPressedTimer;
    int m_iCurrentTrackColor;
    int m_iDispPosX;
    int m_iDispPosY;
    int m_iDisplayDensity;
    int m_iDistanceTriggerIndex;
    int m_iGpsLockCenterX;
    int m_iGpsLockCenterY;
    int m_iGpsSimulationIndex;
    int m_iGpsType;
    int m_iLoadCountOld;
    int m_iMapCacheManagerState;
    int m_iMapRotationState;
    int m_iNewZoomLevel;
    int m_iRotateCenterX;
    int m_iRotateCenterY;
    int m_iSettingKeyPadDisplayMove;
    int m_iSettingsColorNaviArrowFill;
    int m_iSettingsColorNaviArrowLine;
    int m_iSettingsColorSpeedVector;
    int m_iSettingsColorTriangulationFill;
    int m_iSettingsCompassType;
    int m_iWhatsnewCounter;
    int m_iZoomLevel;
    long m_lDurationLastScreenRefresh;
    long m_lGpsOldTime;
    long m_lGpsSimulationTime;
    long m_lGpsTime;
    long m_lLastScreenRefresh;
    File[] m_sMapFilesArray;

    /* renamed from: com.meixi.MMTrackerActivity.1 */
    class C00241 implements SensorEventListener {
        C00241() {
        }

        public void onSensorChanged(SensorEvent event) {
            MMTrackerActivity.this.m_fAccSensorX = ((MMTrackerActivity.this.m_fAccSensorX * 5.0f) + event.values[0]) / 6.0f;
            if (MMTrackerActivity.this.m_fAccSensorX > 4.0f) {
                MMTrackerActivity.this.m_bAccBasedLandscapeNormal = true;
            }
            if (MMTrackerActivity.this.m_fAccSensorX < -4.0f) {
                MMTrackerActivity.this.m_bAccBasedLandscapeNormal = false;
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    /* renamed from: com.meixi.MMTrackerActivity.14 */
    class AnonymousClass14 implements OnClickListener {
        private final /* synthetic */ Activity val$a;
        private final /* synthetic */ boolean val$bUseOldRotCenterValues;
        private final /* synthetic */ String val$sMap;

        AnonymousClass14(String str, Activity activity, boolean z) {
            this.val$sMap = str;
            this.val$a = activity;
            this.val$bUseOldRotCenterValues = z;
        }

        public void onClick(DialogInterface dialog, int which) {
            if (this.val$sMap != MMTrackerActivity.m_CurrentMapName) {
                MMTrackerActivity.this.m_Qct.openMap(MMTrackerActivity.m_CurrentMapName, MMTrackerActivity.this.m_SettingsNightMode);
                MMTrackerActivity.this.m_MapView = null;
                MMTrackerActivity.this.m_MapView = new MapView(this.val$a, MMTrackerActivity.this.m_Qct, false, this.val$bUseOldRotCenterValues);
                MMTrackerActivity.this.setContentView(MMTrackerActivity.this.m_MapView);
                MMTrackerActivity.this.m_MapView.requestFocus();
                MMTrackerActivity.this.m_Tqm.UpdateQctAndClear(MMTrackerActivity.this.m_Qct);
                MMTrackerActivity.this.m_MapView.invalidateMapScreen(true);
                return;
            }
            MMTrackerActivity.this.ShowFileBrowser();
        }
    }

    /* renamed from: com.meixi.MMTrackerActivity.16 */
    class AnonymousClass16 implements OnClickListener {
        private final /* synthetic */ EditText val$input;
        private final /* synthetic */ Waypoint val$w;

        AnonymousClass16(EditText editText, Waypoint waypoint) {
            this.val$input = editText;
            this.val$w = waypoint;
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            if (this.val$input.getText().toString().trim().length() > 0) {
                this.val$w.m_sName = this.val$input.getText().toString().trim();
            }
            this.val$w.WriteGpx(MMTrackerActivity.m_SettingsWaypointPath + Tools.MakeProperFileName(this.val$w.m_sName) + ".gpx");
            if (MMTrackerActivity.this.m_Qct != null) {
                this.val$w.RefreshXY(MMTrackerActivity.this.m_Qct);
            }
            MMTrackerActivity.waypoints.add(this.val$w);
            if (MMTrackerActivity.this.m_MapView != null) {
                MMTrackerActivity.this.m_MapView.invalidateMapScreen(true);
            }
        }
    }

    /* renamed from: com.meixi.MMTrackerActivity.18 */
    class AnonymousClass18 implements OnClickListener {
        private final /* synthetic */ Track val$track;

        AnonymousClass18(Track track) {
            this.val$track = track;
        }

        public void onClick(DialogInterface dialog, int which) {
            if (this.val$track == TrackingService.m_RecordTrack) {
                MMTrackerActivity.this.StopTrackingService();
            }
            MMTrackerActivity.DeleteTrackCore(this.val$track);
            if (MMTrackerActivity.m_bTrackRecording) {
                MMTrackerActivity.this.StartTrackingService(false);
            }
            if (MMTrackerActivity.this.m_MapView != null) {
                MMTrackerActivity.this.m_MapView.invalidateMapScreen(true);
            }
        }
    }

    /* renamed from: com.meixi.MMTrackerActivity.2 */
    class C00252 implements SensorEventListener {
        C00252() {
        }

        public void onSensorChanged(SensorEvent event) {
            double dNewCompassCos = Math.cos(Math.toRadians((double) event.values[0]));
            double dNewCompassSin = Math.sin(Math.toRadians((double) event.values[0]));
            int iFilter = MMTrackerActivity.COMPASS_FILTER;
            if (MMTrackerActivity.m_iSettingsMapRotation == 1) {
                iFilter += 10;
            }
            MMTrackerActivity.this.m_dCompassCos = ((MMTrackerActivity.this.m_dCompassCos * ((double) iFilter)) + dNewCompassCos) / ((double) (iFilter + 1));
            MMTrackerActivity.this.m_dCompassSin = ((MMTrackerActivity.this.m_dCompassSin * ((double) iFilter)) + dNewCompassSin) / ((double) (iFilter + 1));
            MMTrackerActivity.this.m_fCompassValue = (float) Math.toDegrees(Math.atan2(MMTrackerActivity.this.m_dCompassSin, MMTrackerActivity.this.m_dCompassCos));
            if (MMTrackerActivity.this.m_bSettingsShowTrueNorth) {
                MMTrackerActivity mMTrackerActivity = MMTrackerActivity.this;
                mMTrackerActivity.m_fCompassValue -= MMTrackerActivity.this.m_fMagneticDeclination;
            }
            if (MMTrackerActivity.this.m_MapView != null) {
                if (MMTrackerActivity.m_iSettingsMapRotation == 1) {
                    MMTrackerActivity.this.CalcMapRotation();
                }
                long lTimeThreshold = Math.round(((double) MMTrackerActivity.this.m_lDurationLastScreenRefresh) * 1.2d);
                if (lTimeThreshold < ((long) MMTrackerActivity.MIN_TIME_COMPASS_REFRESH)) {
                    lTimeThreshold = (long) MMTrackerActivity.MIN_TIME_COMPASS_REFRESH;
                }
                if (MMTrackerActivity.m_iSettingsMapRotation == 1) {
                    lTimeThreshold = -1;
                }
                if (System.currentTimeMillis() - MMTrackerActivity.this.m_lLastScreenRefresh >= lTimeThreshold && !MMTrackerActivity.this.m_MapView.m_bOnDrawRunning) {
                    if (MMTrackerActivity.m_SettingsOrientation == 4) {
                        if (MMTrackerActivity.m_iScreenOrientation == 2) {
                            if (MMTrackerActivity.this.m_bAccBasedLandscapeNormal) {
                                MMTrackerActivity.this.m_fCompassOrientationCorrection = 90.0f;
                            } else {
                                MMTrackerActivity.this.m_fCompassOrientationCorrection = -90.0f;
                            }
                        } else if (MMTrackerActivity.m_iScreenOrientation == 1) {
                            MMTrackerActivity.this.m_fCompassOrientationCorrection = 0.0f;
                        }
                    }
                    MMTrackerActivity.this.m_MapView.SetSpeedVector(true, MMTrackerActivity.this.gps_list);
                    if (!MMTrackerActivity.this.m_MapView.m_bTouchMoveActive) {
                        MMTrackerActivity.this.m_MapView.invalidateMapScreen(false);
                    }
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    /* renamed from: com.meixi.MMTrackerActivity.20 */
    class AnonymousClass20 implements OnClickListener {
        private final /* synthetic */ Route val$route;

        AnonymousClass20(Route route) {
            this.val$route = route;
        }

        public void onClick(DialogInterface dialog, int which) {
            MMTrackerActivity.DeleteRouteCore(this.val$route);
            if (MMTrackerActivity.this.m_MapView != null) {
                MMTrackerActivity.this.m_MapView.invalidateMapScreen(true);
            }
        }
    }

    /* renamed from: com.meixi.MMTrackerActivity.22 */
    class AnonymousClass22 implements OnClickListener {
        private final /* synthetic */ Waypoint val$waypoint;

        AnonymousClass22(Waypoint waypoint) {
            this.val$waypoint = waypoint;
        }

        public void onClick(DialogInterface dialog, int which) {
            MMTrackerActivity.DeleteWaypointCore(this.val$waypoint);
            if (MMTrackerActivity.this.m_MapView != null) {
                MMTrackerActivity.this.m_MapView.invalidateMapScreen(true);
            }
        }
    }

    /* renamed from: com.meixi.MMTrackerActivity.3 */
    class C00263 implements Runnable {
        C00263() {
        }

        public void run() {
            if (MMTrackerActivity.DEBUG_GPS_SIMULATION && MMTrackerActivity.this.m_GpsSimulationTrack != null) {
                if (MMTrackerActivity.this.m_iGpsSimulationIndex < MMTrackerActivity.this.m_GpsSimulationTrack.trackpoints.size()) {
                    MMTrackerActivity mMTrackerActivity;
                    if (MMTrackerActivity.this.m_lGpsSimulationTime >= ((Trackpoint) MMTrackerActivity.this.m_GpsSimulationTrack.trackpoints.get(MMTrackerActivity.this.m_iGpsSimulationIndex)).m_lTime) {
                        Location loc = new Location("gps");
                        loc.setLatitude(((Trackpoint) MMTrackerActivity.this.m_GpsSimulationTrack.trackpoints.get(MMTrackerActivity.this.m_iGpsSimulationIndex)).m_dGpsLat);
                        loc.setLongitude(((Trackpoint) MMTrackerActivity.this.m_GpsSimulationTrack.trackpoints.get(MMTrackerActivity.this.m_iGpsSimulationIndex)).m_dGpsLong);
                        loc.setAltitude(((Trackpoint) MMTrackerActivity.this.m_GpsSimulationTrack.trackpoints.get(MMTrackerActivity.this.m_iGpsSimulationIndex)).m_dAltitude);
                        loc.setSpeed((float) ((Trackpoint) MMTrackerActivity.this.m_GpsSimulationTrack.trackpoints.get(MMTrackerActivity.this.m_iGpsSimulationIndex)).m_dVelocity);
                        MMTrackerActivity.this.onLocationChanged(loc);
                        mMTrackerActivity = MMTrackerActivity.this;
                        mMTrackerActivity.m_iGpsSimulationIndex++;
                    }
                    mMTrackerActivity = MMTrackerActivity.this;
                    mMTrackerActivity.m_lGpsSimulationTime += MMTrackerActivity.TIMER_GPS_SIM_TICKS;
                    return;
                }
                MMTrackerActivity.this.m_iGpsSimulationIndex = 0;
                MMTrackerActivity.this.m_lGpsSimulationTime = 0;
                MMTrackerActivity.this.m_GpsSimulationTrack = null;
            }
        }
    }

    /* renamed from: com.meixi.MMTrackerActivity.4 */
    class C00294 implements Runnable {

        /* renamed from: com.meixi.MMTrackerActivity.4.1 */
        class C00271 implements FilenameFilter {
            C00271() {
            }

            public boolean accept(File dir, String name) {
                return name.toUpperCase().endsWith(".QCT");
            }
        }

        /* renamed from: com.meixi.MMTrackerActivity.4.2 */
        class C00282 implements FilenameFilter {
            C00282() {
            }

            public boolean accept(File dir, String name) {
                return name.toUpperCase().endsWith(".QCT");
            }
        }

        C00294() {
        }

        public void run() {
            MMTrackerActivity.this.m_bMapCacheDbThreadFinished = false;
            if (MMTrackerActivity.this.m_iMapCacheManagerState == MMTrackerActivity.STATE_MAPCACHE_READ_DIRS) {
                if (MMTrackerActivity.m_bTracksLoaded) {
                    MMTrackerActivity.this.m_sMapFilesArray = Tools.listFilesAsArray(new File(MMTrackerActivity.m_SettingsMapPath), new C00271(), true);
                    if (MMTrackerActivity.this.m_sMapFilesArray == null) {
                        MMTrackerActivity.this.m_iMapCacheManagerState = MMTrackerActivity.STATE_MAPCACHE_READ_DB;
                    } else if (MMTrackerActivity.this.m_sMapFilesArray.length <= 0) {
                        MMTrackerActivity.this.m_iMapCacheManagerState = MMTrackerActivity.STATE_MAPCACHE_READ_DB;
                    } else {
                        MMTrackerActivity.this.m_iMapCacheManagerState = MMTrackerActivity.STATE_MAPCACHE_CHECK_DIRS;
                    }
                }
            } else if (MMTrackerActivity.this.m_iMapCacheManagerState == MMTrackerActivity.STATE_MAPCACHE_CHECK_DIRS) {
                boolean bNoFiles = true;
                if (MMTrackerActivity.this.m_sMapFilesArray != null && MMTrackerActivity.this.m_sMapFilesArray.length > 0) {
                    int iIndex = 0;
                    while (MMTrackerActivity.this.m_sMapFilesArray[iIndex] == null && iIndex < MMTrackerActivity.this.m_sMapFilesArray.length - 1) {
                        iIndex++;
                    }
                    if (iIndex < MMTrackerActivity.this.m_sMapFilesArray.length && MMTrackerActivity.this.m_sMapFilesArray[iIndex] != null) {
                        boolean bValidCursor;
                        bNoFiles = false;
                        Cursor cr = null;
                        if (MMTrackerActivity.this.m_dbMapCache.isOpen()) {
                            cr = MMTrackerActivity.this.m_dbMapCache.fetchCacheEntryByData(MMTrackerActivity.this.m_sMapFilesArray[iIndex].getName(), MMTrackerActivity.this.m_sMapFilesArray[iIndex].length(), MMTrackerActivity.this.m_sMapFilesArray[iIndex].lastModified());
                        }
                        if (cr == null) {
                            bValidCursor = false;
                        } else {
                            cr.moveToFirst();
                            bValidCursor = cr.getCount() > 0;
                        }
                        if (!(bValidCursor || MMTrackerActivity.this.m_Qct == null)) {
                            double[] bbox = MMTrackerActivity.this.m_Qct.GetBoundingBoxFromFile(MMTrackerActivity.this.m_sMapFilesArray[iIndex].getAbsolutePath());
                            if (MMTrackerActivity.this.m_dbMapCache.isOpen() && bbox != null) {
                                MMTrackerActivity.this.m_dbMapCache.createCacheEntry(MMTrackerActivity.this.m_sMapFilesArray[iIndex].getName(), MMTrackerActivity.this.m_sMapFilesArray[iIndex].length(), MMTrackerActivity.this.m_sMapFilesArray[iIndex].lastModified(), bbox[0], bbox[1], bbox[2], bbox[3]);
                            }
                        }
                        MMTrackerActivity.this.m_sMapFilesArray[iIndex] = null;
                    }
                }
                if (bNoFiles) {
                    MMTrackerActivity.this.m_iMapCacheManagerState = MMTrackerActivity.STATE_MAPCACHE_READ_DB;
                }
            } else if (MMTrackerActivity.this.m_iMapCacheManagerState == MMTrackerActivity.STATE_MAPCACHE_READ_DB) {
                MMTrackerActivity.this.m_CursorCheckMapCache = MMTrackerActivity.this.m_dbMapCache.fetchAllMapCaches();
                if (MMTrackerActivity.this.m_CursorCheckMapCache == null) {
                    MMTrackerActivity.this.m_iMapCacheManagerState = MMTrackerActivity.STATE_MAPCACHE_FINISHED;
                } else {
                    MMTrackerActivity.this.m_CursorCheckMapCache.moveToFirst();
                }
                File file = new File(MMTrackerActivity.m_SettingsMapPath);
                FilenameFilter c00282 = new C00282();
                MMTrackerActivity.this.m_sMapFilesArray = null;
                MMTrackerActivity.this.m_sMapFilesArray = Tools.listFilesAsArray(file, c00282, true);
                MMTrackerActivity.this.m_iMapCacheManagerState = MMTrackerActivity.STATE_MAPCACHE_CHECK_DB;
            } else if (MMTrackerActivity.this.m_iMapCacheManagerState == MMTrackerActivity.STATE_MAPCACHE_CHECK_DB) {
                if (MMTrackerActivity.this.m_CursorCheckMapCache.isClosed()) {
                    MMTrackerActivity.this.m_iMapCacheManagerState = MMTrackerActivity.STATE_MAPCACHE_FINISHED;
                } else if (MMTrackerActivity.this.m_CursorCheckMapCache.getPosition() < MMTrackerActivity.this.m_CursorCheckMapCache.getCount()) {
                    boolean bFound = false;
                    if (MMTrackerActivity.this.m_sMapFilesArray != null) {
                        for (File f : MMTrackerActivity.this.m_sMapFilesArray) {
                            if (MMTrackerActivity.this.m_dbMapCache.isOpen() && MMTrackerActivity.this.m_dbMapCache.CompareMapData(MMTrackerActivity.this.m_CursorCheckMapCache, f.getName(), f.length(), f.lastModified())) {
                                bFound = true;
                                break;
                            }
                        }
                    }
                    if (((bFound ? 0 : 1) & MMTrackerActivity.this.m_dbMapCache.isOpen()) != 0) {
                        long id = MMTrackerActivity.this.m_dbMapCache.getRowID(MMTrackerActivity.this.m_CursorCheckMapCache);
                        if (id >= 0) {
                            MMTrackerActivity.this.m_dbMapCache.deleteCacheEntry(id);
                        }
                    }
                    if (!MMTrackerActivity.this.m_CursorCheckMapCache.isClosed()) {
                        MMTrackerActivity.this.m_CursorCheckMapCache.moveToNext();
                    }
                } else {
                    MMTrackerActivity.this.m_iMapCacheManagerState = MMTrackerActivity.STATE_MAPCACHE_FINISHED;
                }
            }
            MMTrackerActivity.this.m_bMapCacheDbThreadFinished = true;
        }
    }

    /* renamed from: com.meixi.MMTrackerActivity.5 */
    class C00305 implements Runnable {
        C00305() {
        }

        public void run() {
            boolean z;
            if (!(MMTrackerActivity.this.m_Tqm == null || MMTrackerActivity.this.m_MapView == null)) {
                if ((MMTrackerActivity.this.m_Tqm.m_iLoadCount > 0 && !MMTrackerActivity.this.m_bWaitForFullscreen) || (MMTrackerActivity.this.m_iLoadCountOld != 0 && MMTrackerActivity.this.m_Tqm.m_iLoadCount == 0)) {
                    MMTrackerActivity.this.m_MapView.invalidateMapScreen(false);
                }
                MMTrackerActivity.this.m_iLoadCountOld = MMTrackerActivity.this.m_Tqm.m_iLoadCount;
            }
            if (MMTrackerActivity.m_bTracksLoaded) {
                if (TrackingService.m_bRecordingActive && MMTrackerActivity.m_ServiceIBinder == null && MMTrackerActivity.m_ServiceConn == null && MMTrackerActivity.tracks != null) {
                    MMTrackerActivity.this.StartTrackingService(true);
                    MMTrackerActivity.m_bTrackRecording = true;
                }
                if (TrackingService.m_bTrackReadyToTakeOver && !MMTrackerActivity.this.m_bOverlayManagerRunning) {
                    MMTrackerActivity.tracks.add(TrackingService.m_RecordTrack);
                    TrackingService.m_bTrackReadyToTakeOver = false;
                }
            }
            MMTrackerActivity.this.m_bGpsFix = SystemClock.elapsedRealtime() - MMTrackerActivity.this.m_LastLocationMillisGps < 3000;
            MMTrackerActivity mMTrackerActivity = MMTrackerActivity.this;
            if (SystemClock.elapsedRealtime() - MMTrackerActivity.this.m_LastLocationMillisNetwork < 60000) {
                z = true;
            } else {
                z = false;
            }
            mMTrackerActivity.m_bNetworkFix = z;
            if (MMTrackerActivity.m_iSettingsMapRotation == 2) {
                if (MMTrackerActivity.this.m_bGpsFix) {
                    MMTrackerActivity.this.CalcMapRotation();
                } else {
                    MMTrackerActivity mMTrackerActivity2 = MMTrackerActivity.this;
                    mMTrackerActivity = MMTrackerActivity.this;
                    MMTrackerActivity.this.m_fMapRotation = 0.0f;
                    double d = (double) null;
                    mMTrackerActivity.m_dSinMapRotationNeg = d;
                    mMTrackerActivity2.m_dSinMapRotation = d;
                    mMTrackerActivity2 = MMTrackerActivity.this;
                    MMTrackerActivity.this.m_dCosMapRotationNeg = 1.0d;
                    mMTrackerActivity2.m_dCosMapRotation = 1.0d;
                }
                if (!(MMTrackerActivity.this.m_MapView == null || MMTrackerActivity.this.m_MapView.m_bTouchMoveActive)) {
                    MMTrackerActivity.this.m_MapView.invalidateMapScreen(false);
                }
            }
            if (MMTrackerActivity.this.m_iBackButtonPressedTimer > 0) {
                mMTrackerActivity2 = MMTrackerActivity.this;
                mMTrackerActivity2.m_iBackButtonPressedTimer--;
            }
            if (MMTrackerActivity.m_bTracksLoaded) {
                try {
                    MMTrackerActivity.this.dismissDialog(MMTrackerActivity.ID_DIALOG_SEARCHING);
                } catch (IllegalArgumentException e) {
                }
                if (MMTrackerActivity.this.m_MapView != null) {
                    if (MMTrackerActivity.this.m_MapView.m_bTouchHold) {
                        MMTrackerActivity.m_iTimerCounter++;
                        if (MMTrackerActivity.m_iTimerCounter > MMTrackerActivity.TIME_TO_CONTEXT_MENU) {
                            Log.d("MM Tracker", "Long Click Timer - timer limit reached");
                            MMTrackerActivity.m_iTimerCounter = MMTrackerActivity.TIME_TO_CONTEXT_MENU + 1;
                        }
                        if (MMTrackerActivity.m_iTimerCounter == MMTrackerActivity.TIME_TO_CONTEXT_MENU) {
                            Log.d("MM Tracker", "Long Click Timer - call menu");
                            MMTrackerActivity.this.registerForContextMenu(MMTrackerActivity.this.m_MapView);
                            MMTrackerActivity.this.openContextMenu(MMTrackerActivity.this.m_MapView);
                        }
                    } else {
                        Log.d("MM Tracker", "Long Click Timer - reset timer");
                        MMTrackerActivity.m_iTimerCounter = 0;
                    }
                    if (MMTrackerActivity.this.m_bRefreshMap) {
                        MMTrackerActivity.this.m_MapView.invalidateMapScreen(true);
                        MMTrackerActivity.this.m_bRefreshMap = false;
                    }
                }
            }
        }
    }

    /* renamed from: com.meixi.MMTrackerActivity.6 */
    class C00316 extends TimerTask {
        C00316() {
        }

        public void run() {
            MMTrackerActivity.this.TimerMain_Method();
        }
    }

    /* renamed from: com.meixi.MMTrackerActivity.7 */
    class C00327 extends TimerTask {
        C00327() {
        }

        public void run() {
            MMTrackerActivity.this.TimerGpsSim_Method();
        }
    }

    /* renamed from: com.meixi.MMTrackerActivity.8 */
    class C00338 implements ServiceConnection {
        C00338() {
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            MMTrackerActivity.m_ServiceIBinder = service;
        }

        public void onServiceDisconnected(ComponentName arg0) {
            MMTrackerActivity.m_ServiceIBinder = null;
        }
    }

    /* renamed from: com.meixi.MMTrackerActivity.9 */
    class C00349 extends TimerTask {
        C00349() {
        }

        public void run() {
            MMTrackerActivity.this.TimerMapCache_Method();
        }
    }

    public interface ITrackingService {
        void restartRecording();

        void stopRecording();
    }

    public MMTrackerActivity() {
        this.m_Qct = null;
        this.m_ScreenLock = false;
        this.m_fGpsAccuracy = 0.0f;
        this.m_iCurrentTrackColor = 0;
        this.m_bGpsFix = false;
        this.m_bNetworkFix = false;
        this.m_NavigationTargetIndex = -1;
        this.m_NavigationTotalDist = 0.0d;
        this.m_NavigationStartTime = null;
        this.m_bRefreshMap = false;
        this.m_bAppInPause = false;
        this.m_bAutostartLockDone = false;
        this.m_iWhatsnewCounter = 0;
        this.m_iDistanceTriggerIndex = 0;
        this.m_bCreateFirstTrackpoint = true;
        this.m_fMagneticDeclination = 0.0f;
        this.m_fCompassOrientationCorrection = 0.0f;
        this.m_lDurationLastScreenRefresh = 0;
        this.m_bAccBasedLandscapeNormal = true;
        this.m_bOverlayManagerRunning = false;
        this.m_bWaitForFullscreen = false;
        this.m_dSinMapRotation = 0.0d;
        this.m_dCosMapRotation = 1.0d;
        this.m_dSinMapRotationNeg = 0.0d;
        this.m_dCosMapRotationNeg = 1.0d;
        this.m_iMapRotationState = 0;
        this.m_bChangeMapScale = false;
        this.m_bPositionLockCheck = false;
        this.m_bContextMenuActive = false;
        this.m_bStandstillDetected = false;
        this.m_bChangingScreenOrientation = false;
        this.bAvoidRotation = false;
        this.m_iNewZoomLevel = INITIAL_ZOOM_LEVEL;
        this.m_GpsSimulationTrack = null;
        this.m_lGpsSimulationTime = 0;
        this.m_iGpsSimulationIndex = 0;
        this.gps_list = new Location[POSITION_ARRAY_SIZE];
        this.m_AccListener = new C00241();
        this.m_CompassListener = new C00252();
        this.TimerGpsSim_Tick = new C00263();
        this.TimerMapCache_Tick = new C00294();
        this.TimerMain_Tick = new C00305();
    }

    static {
        WHATSNEW_STRING = "- Fixed crash on map load in Jellybean 4.2\n";
        DEBUG_GPS_SIMULATION = false;
        MAP_POSITION_INVALID = -99999;
        MIN_TRACK_RESOLUTION_PIXEL = 10;
        TIME_TO_CONTEXT_MENU = 8;
        MIN_TIME_COMPASS_REFRESH = 200;
        ACTIVITY_SETTINGS = 1;
        ACTIVITY_FILEBROWSER = 2;
        ACTIVITY_TRACKSTYLE = 3;
        ACTIVITY_ROUTESTYLE = 4;
        ACTIVITY_WAYPOINTSTYLE = 5;
        ACTIVITY_MAPLIST = 6;
        ACTIVITY_OVERLAYMANAGER = 7;
        ACTIVITY_COORDINATE = 8;
        ACTIVITY_TRACKDATA = 9;
        ID_DIALOG_SEARCHING = 777;
        CONTEXT_MENU_TRACKS = 1;
        CONTEXT_MENU_ROUTES = 2;
        CONTEXT_MENU_WAYPOINTS = 3;
        CONTEXT_MENU_ACTIONS = 4;
        CONTEXT_MENU_ACTIONS_SUBMENU_WP = 5;
        LARGER_SCALE = 1;
        SMALLER_SCALE = 2;
        NEW_POSITION_KEEP_OLD = 0;
        NEW_POSITION_DEFAULT = 1;
        NEW_POSITION_SET = 2;
        NO_CHANGE_IN_ZOOM_LEVEL = -999;
        DISTANCE_TO_TRACK_FOR_MENU = 40;
        INITIAL_ZOOM_LEVEL = 3;
        POSITION_ARRAY_SIZE = 6;
        NAVIGATION_TARGET_DISTANCE = 0.04d;
        NAVIGATION_LEG_TOLERANCE = 0.05d;
        ARROW_MIN_SPEED_TO_SHOW = 0.75d;
        ARROW_MIN_SPEED_TO_SWITCH_TO_COMPASS = 3.0d;
        TIMER_MAIN_TICKS = 100;
        TIMER_MAPCACHE_TICKS = 1000;
        TIMER_GPS_SIM_TICKS = 1000;
        WAIT_FOR_SECOND_BACKBUTTON = 2500;
        GRAPH_TYPE_SPEED = 1;
        GRAPH_TYPE_HEIGHT = 2;
        LOCATION_TYPE_GPS = 1;
        LOCATION_TYPE_NETWORK = 2;
        COMPASS_FILTER = 3;
        STATE_MAPCACHE_READ_DIRS = 1;
        STATE_MAPCACHE_CHECK_DIRS = 2;
        STATE_MAPCACHE_READ_DB = 3;
        STATE_MAPCACHE_CHECK_DB = 4;
        STATE_MAPCACHE_FINISHED = 5;
        m_dCalculatedGpsAngle = 0.0d;
        m_dCalculatedGpsSpeed = 0.0d;
        m_dCalculatedGpsHeading = Double.NaN;
        m_dCalculatedNavigationBearing = Double.NaN;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.m_bIsRestart = false;
        m_bTracksLoaded = false;
        m_bTrackRecording = false;
        m_bRequestAllWpRefresh = false;
        m_bRequestAllTrRefresh = false;
        m_bRequestAllRtRefresh = false;
        m_bMmiListIsOnMap = false;
        m_TrackToDisplayData = null;
        m_NavigationTarget = new NavigationTarget();
        tracks = new ArrayList();
        routes = new ArrayList();
        waypoints = new ArrayList();
        maps = new ArrayList();
        m_SelectedTrack = null;
        m_requestedWpRefresh = null;
        m_requestedTrRefresh = null;
        m_SettingsOrientation = 1;
        m_dGpsSpeed = 0.0d;
        m_dGpsAltitude = 0.0d;
        m_dGpsBearing = 0.0d;
        this.m_iGpsType = 0;
        m_CurrentlyCreatedRoute = null;
        m_BitmapSmallGraph = null;
        m_canvasSmallGraph = null;
        m_bRequestRouteCreation = false;
        m_ServiceConn = null;
        m_ServiceIBinder = null;
        this.m_GeoidTable = new GeoidCorrection(this);
        this.m_dbMapCache = new MapCacheDbAdapter(this);
        this.m_dbMapCache.open();
        this.m_bMapCacheDbThreadFinished = true;
        this.m_lGpsOldTime = 0;
        this.m_LocationManager = (LocationManager) getSystemService("location");
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.m_iDisplayDensity = metrics.densityDpi;
        m_ScreenHeight = metrics.heightPixels;
        m_ScreenWidth = metrics.widthPixels;
        this.m_iGpsLockCenterX = m_ScreenWidth / 2;
        this.m_iGpsLockCenterY = m_ScreenHeight / 2;
        this.m_iRotateCenterX = this.m_iGpsLockCenterX;
        this.m_iRotateCenterY = this.m_iGpsLockCenterY;
        requestWindowFeature(1);
        this.m_Prefs = getPreferences(0);
        m_CurrentMapName = this.m_Prefs.getString("CurrentMap", "");
        this.m_iDispPosX = this.m_Prefs.getInt("MapDisplayPosX", MAP_POSITION_INVALID);
        this.m_iDispPosY = this.m_Prefs.getInt("MapDisplayPosY", MAP_POSITION_INVALID);
        this.m_iZoomLevel = this.m_Prefs.getInt("ZoomLevel", INITIAL_ZOOM_LEVEL);
        this.m_bShowDisclaimer = this.m_Prefs.getBoolean("ShowDisclaimer", true);
        this.m_iWhatsnewCounter = this.m_Prefs.getInt("WhatsnewCounter", 0);
        m_sLastLoadedMmiFile = this.m_Prefs.getString("DafaultMmiFile", "");
        if (this.m_iZoomLevel > MapView.MAX_ZOOM_LEVELS - 1) {
            this.m_iZoomLevel = MapView.MAX_ZOOM_LEVELS - 1;
        }
        if (this.m_iZoomLevel < 0) {
            this.m_iZoomLevel = 0;
        }
        this.m_Tqm = new TileQueueManager(this.m_Qct, Math.round((float) (((m_ScreenHeight / 64) + 2) * ((m_ScreenWidth / 64) + 2))));
        m_iTimerCounter = 0;
        this.m_iBackButtonPressedTimer = 0;
        this.TimerMain = new Timer();
        this.TimerMain.schedule(new C00316(), 0, TIMER_MAIN_TICKS);
        if (DEBUG_GPS_SIMULATION) {
            this.m_TimerGpsSimulation = new Timer();
            this.m_TimerGpsSimulation.schedule(new C00327(), 0, TIMER_GPS_SIM_TICKS);
        }
    }

    private void StartTrackingService(boolean bBindWithoutStart) {
        Intent intent = new Intent(this, TrackingService.class);
        if (!bBindWithoutStart) {
            intent.putExtra("TrackPath", m_SettingsTrackPath);
            intent.putExtra("TrackColor", CalcColor(m_iSettingsColorNewTrack, -16777216));
            intent.putExtra("TrackWidth", m_iSettingsWidthNewTrack);
            intent.putExtra("GpsDistanceRate", m_SettingsGpsDistanceRate);
            intent.putExtra("GpsDataRate", m_SettingsGpsDataRate);
            intent.putExtra("UseGeoidCorrection", this.m_SettingsUseGeoidCorrection);
            startService(intent);
        }
        m_ServiceConn = new C00338();
        bindService(intent, m_ServiceConn, 1);
    }

    private void StopTrackingService() {
        Track t = TrackingService.m_RecordTrack;
        if (m_ServiceIBinder != null) {
            m_ServiceIBinder.stopRecording();
        }
        if (m_ServiceConn != null) {
            unbindService(m_ServiceConn);
            m_ServiceConn = null;
        }
        stopService(new Intent(this, TrackingService.class));
        if (t != null) {
            t.RefreshXY(this.m_Qct);
            t.m_bCacheVaild = false;
        }
        while (TrackingService.m_bRecordingActive) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
    }

    private void CalcMapRotation() {
        double dNewRotationSin = 0.0d;
        double dNewRotationCos = 1.0d;
        if (m_iSettingsMapRotation == 0 || this.m_MapView == null) {
            this.m_dSinMapRotationNeg = 0.0d;
            this.m_dSinMapRotation = 0.0d;
            this.m_dCosMapRotationNeg = 1.0d;
            this.m_dCosMapRotation = 1.0d;
            this.m_fMapRotation = 0.0f;
            return;
        }
        if (m_iSettingsMapRotation == 1) {
            dNewRotationSin = Math.sin(Math.toRadians((double) (-((this.m_fCompassValue + this.m_fCompassOrientationCorrection) + this.m_MapView.m_fTrueNorth))));
            dNewRotationCos = Math.cos(Math.toRadians((double) (-((this.m_fCompassValue + this.m_fCompassOrientationCorrection) + this.m_MapView.m_fTrueNorth))));
        }
        if (m_iSettingsMapRotation == 2) {
            dNewRotationSin = Math.sin(-(m_dCalculatedGpsAngle + Math.toRadians((double) this.m_MapView.m_fTrueNorth)));
            dNewRotationCos = Math.cos(-(m_dCalculatedGpsAngle + Math.toRadians((double) this.m_MapView.m_fTrueNorth)));
        }
        switch (this.m_iMapRotationState) {
            case 0:
                if (m_iSettingsMapRotation == 2) {
                    dNewRotationSin = ((this.m_dSinMapRotation * 3.0d) + dNewRotationSin) / 4.0d;
                    dNewRotationCos = ((this.m_dCosMapRotation * 3.0d) + dNewRotationCos) / 4.0d;
                }
                if (this.m_MapView.m_iTouchMode == MapView.TOUCH_MODE_ZOOM || this.m_MapView.m_bTouchMoveActive || this.m_MapView.m_bTouchHold || m_CurrentlyCreatedRoute != null || this.m_bContextMenuActive || this.bAvoidRotation) {
                    this.m_iMapRotationState = 1;
                    break;
                }
            case 1:
                dNewRotationSin = this.m_dSinMapRotation;
                dNewRotationCos = this.m_dCosMapRotation;
                if (!(this.m_MapView.m_iTouchMode != MapView.TOUCH_MODE_NONE || this.m_MapView.m_bTouchMoveActive || this.m_MapView.m_bTouchHold || m_CurrentlyCreatedRoute != null || this.m_bContextMenuActive || this.bAvoidRotation)) {
                    this.m_iMapRotationState = 2;
                    break;
                }
            case 2:
                if (m_iSettingsMapRotation == 1 || m_iSettingsMapRotation == 2) {
                    dNewRotationSin = ((this.m_dSinMapRotation * 3.0d) + dNewRotationSin) / 4.0d;
                    dNewRotationCos = ((this.m_dCosMapRotation * 3.0d) + dNewRotationCos) / 4.0d;
                }
                if (Math.abs(dNewRotationSin - this.m_dSinMapRotation) < 0.05d && Math.abs(dNewRotationCos - this.m_dCosMapRotation) < 0.05d) {
                    this.m_iMapRotationState = 0;
                    break;
                }
        }
        this.m_fMapRotation = (float) Math.atan2(dNewRotationSin, dNewRotationCos);
        this.m_dSinMapRotation = Math.sin((double) this.m_fMapRotation);
        this.m_dCosMapRotation = Math.cos((double) this.m_fMapRotation);
        this.m_dSinMapRotationNeg = Math.sin((double) (-this.m_fMapRotation));
        this.m_dCosMapRotationNeg = Math.cos((double) (-this.m_fMapRotation));
        this.m_fMapRotation = (float) Math.toDegrees((double) this.m_fMapRotation);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.m_bChangingScreenOrientation = true;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.m_MapView = null;
        this.m_iDisplayDensity = metrics.densityDpi;
        m_ScreenHeight = metrics.heightPixels;
        m_ScreenWidth = metrics.widthPixels;
        this.m_iGpsLockCenterX = m_ScreenWidth / 2;
        this.m_iGpsLockCenterY = m_ScreenHeight / 2;
        this.m_iRotateCenterX = this.m_iGpsLockCenterX;
        this.m_iRotateCenterY = this.m_iGpsLockCenterY;
        m_iScreenOrientation = newConfig.orientation;
        if (m_bTracksLoaded) {
            OpenMap(m_CurrentMapName, 0.0d, 0.0d, 0.0f, 0.0f, NEW_POSITION_KEEP_OLD, false, NO_CHANGE_IN_ZOOM_LEVEL, false);
        }
        this.m_bChangingScreenOrientation = false;
    }

    public void onRestart() {
        int i = 0;
        super.onRestart();
        this.m_bIsRestart = true;
        this.m_bAppInPause = false;
        if (this.m_MapView != null) {
            i = 1;
        }
        if ((i & m_bTracksLoaded) != 0) {
            this.m_MapView.invalidateMapScreen(true);
        }
    }

    public void onStart() {
        super.onStart();
        this.m_bAppInPause = false;
        getPrefs();
        setRequestedOrientation(m_SettingsOrientation);
        if (!this.m_bIsRestart) {
            this.m_Tqm.StartThread();
            m_bTrackRecording = this.m_SettingsAutostartTracking;
            if (m_bTrackRecording) {
                StartTrackingService(false);
            }
            if (CheckMapPathesForConmformity()) {
                this.m_iMapCacheManagerState = STATE_MAPCACHE_READ_DIRS;
                this.TimerMapCache = new Timer();
                this.TimerMapCache.schedule(new C00349(), 0, TIMER_MAPCACHE_TICKS);
            }
        }
        if (m_SettingsKeepDisplayOn) {
            getWindow().setFlags(128, 128);
        } else {
            getWindow().clearFlags(128);
        }
        if (this.m_SettingsHideStatusbar) {
            getWindow().setFlags(1024, 1024);
        } else {
            getWindow().clearFlags(1024);
        }
        if (!this.m_bIsRestart) {
            boolean bCheckDone = false;
            if (m_SettingsRoutePath == "" || m_SettingsTrackPath == "" || m_SettingsMapPath == "" || m_SettingsWaypointPath == "") {
                bCheckDone = true;
                ComplainAboutPathes();
            }
            OpenMap(m_CurrentMapName, 0.0d, 0.0d, 0.0f, 0.0f, NEW_POSITION_KEEP_OLD, false, NO_CHANGE_IN_ZOOM_LEVEL, false);
            if (m_SettingsRoutePath == "" && m_SettingsTrackPath == "" && m_SettingsWaypointPath == "") {
                m_bTracksLoaded = true;
            } else {
                if (m_SettingsOrientation == 4) {
                    setRequestedOrientation(1);
                } else {
                    setRequestedOrientation(m_SettingsOrientation);
                }
                showDialog(ID_DIALOG_SEARCHING);
                new Thread(new Runnable() {
                    public void run() {
                        Iterator it;
                        if (MMTrackerActivity.this.m_SettingsLoadTrOnStartupCount > 0 || MMTrackerActivity.this.m_SettingsLoadTrOnStartupDate > 0) {
                            MMTrackerActivity.this.LoadTracks(MMTrackerActivity.m_SettingsTrackPath, MMTrackerActivity.m_SettingsMinTrackResolution, (long) MMTrackerActivity.this.m_SettingsLoadTrOnStartupCount, (long) MMTrackerActivity.this.m_SettingsLoadTrOnStartupDate);
                            it = MMTrackerActivity.tracks.iterator();
                            while (it.hasNext()) {
                                Track track = (Track) it.next();
                                if (track != null) {
                                    if (MMTrackerActivity.this.m_Qct != null) {
                                        track.RefreshXY(MMTrackerActivity.this.m_Qct);
                                    }
                                    track.m_bCacheVaild = false;
                                }
                            }
                        }
                        if (MMTrackerActivity.this.m_SettingsLoadRtOnStartup) {
                            MMTrackerActivity.this.LoadRoutes(MMTrackerActivity.m_SettingsRoutePath);
                            it = MMTrackerActivity.routes.iterator();
                            while (it.hasNext()) {
                                Route route = (Route) it.next();
                                if (route != null) {
                                    if (MMTrackerActivity.this.m_Qct != null) {
                                        route.RefreshXY(MMTrackerActivity.this.m_Qct);
                                    }
                                    route.m_bCacheVaild = false;
                                }
                            }
                        }
                        if (MMTrackerActivity.this.m_SettingsLoadWpOnStartup) {
                            MMTrackerActivity.this.LoadWaypoints(MMTrackerActivity.m_SettingsWaypointPath);
                            it = MMTrackerActivity.waypoints.iterator();
                            while (it.hasNext()) {
                                Waypoint waypoint = (Waypoint) it.next();
                                if (waypoint != null) {
                                    if (MMTrackerActivity.this.m_Qct != null) {
                                        waypoint.RefreshXY(MMTrackerActivity.this.m_Qct);
                                    }
                                    waypoint.m_bCacheVaild = false;
                                }
                            }
                        }
                        MMTrackerActivity.this.removeDialog(MMTrackerActivity.ID_DIALOG_SEARCHING);
                        MMTrackerActivity.m_bTracksLoaded = true;
                        MMTrackerActivity.this.m_bRefreshMap = true;
                        MMTrackerActivity.this.setRequestedOrientation(MMTrackerActivity.m_SettingsOrientation);
                    }
                }).start();
            }
            if (!bCheckDone) {
                CheckTrackPath();
            }
            if (this.m_bShowDisclaimer) {
                ShowDisclaimerDialog();
            }
            try {
                PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                if (this.m_iWhatsnewCounter < pinfo.versionCode) {
                    this.m_iWhatsnewCounter = pinfo.versionCode;
                    ShowWhatsnewDialog();
                }
            } catch (Exception e) {
            }
        }
    }

    protected Dialog onCreateDialog(int id) {
        if (id != ID_DIALOG_SEARCHING) {
            return super.onCreateDialog(id);
        }
        ProgressDialog loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getString(C0047R.string.MmTrackerActivity_loading_tracks));
        loadingDialog.setIndeterminate(true);
        loadingDialog.setCancelable(false);
        return loadingDialog;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0047R.menu.main_menu, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        if (this.m_PositionLock || (this.m_SettingsAutostartGpsLock && !this.m_bAutostartLockDone)) {
            menu.findItem(C0047R.id.itemLockGps).setIcon(C0047R.drawable.menu_icon_gps_locked);
        } else {
            menu.findItem(C0047R.id.itemLockGps).setIcon(C0047R.drawable.menu_icon_gps_unlocked);
        }
        if (this.m_ScreenLock) {
            menu.findItem(C0047R.id.itemLockScreen).setIcon(C0047R.drawable.menu_icon_screen_locked);
        } else {
            menu.findItem(C0047R.id.itemLockScreen).setIcon(C0047R.drawable.menu_icon_screen_unlocked);
        }
        if (m_bTrackRecording) {
            menu.findItem(C0047R.id.itemStartTrack).setIcon(C0047R.drawable.menu_icon_track_stop);
        } else {
            menu.findItem(C0047R.id.itemStartTrack).setIcon(C0047R.drawable.menu_icon_track_start);
        }
        menu.findItem(C0047R.id.itemOverlay).setEnabled(m_CurrentlyCreatedRoute == null);
        boolean bLockIconEnabled = false;
        if (this.m_bGpsFix || this.m_bNetworkFix) {
            bLockIconEnabled = true;
        }
        if (this.m_MapView == null) {
            bLockIconEnabled = false;
        }
        if (this.m_PositionLock) {
            bLockIconEnabled = true;
        }
        if (this.m_SettingsAutostartGpsLock && !this.m_bAutostartLockDone) {
            bLockIconEnabled = true;
        }
        menu.findItem(C0047R.id.itemLockGps).setEnabled(bLockIconEnabled);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case C0047R.id.itemLoadMap:
                ShowFileBrowser();
                return true;
            case C0047R.id.itemOverlay:
                ShowOverlayManager();
                return true;
            case C0047R.id.itemSettings:
                ShowSettings();
                return true;
            case C0047R.id.itemLockGps:
                if (this.m_PositionLock || (this.m_SettingsAutostartGpsLock && !this.m_bAutostartLockDone)) {
                    if (this.m_MapView != null && this.m_PositionLock) {
                        this.m_MapView.DisableGpsLock(false);
                    }
                    this.m_PositionLock = false;
                    this.m_bAutostartLockDone = true;
                    if (this.m_MapView == null) {
                        return true;
                    }
                    this.m_MapView.invalidateMapScreen(true);
                    return true;
                } else if ((!this.m_bGpsFix && !this.m_bNetworkFix) || this.m_MapView == null) {
                    return true;
                } else {
                    this.m_PositionLock = true;
                    this.m_MapView.SetGpsLock(true, false);
                    return true;
                }
            case C0047R.id.itemLockScreen:
                if (this.m_ScreenLock) {
                    this.m_ScreenLock = false;
                    return true;
                }
                this.m_ScreenLock = true;
                return true;
            case C0047R.id.itemStartTrack:
                if (!m_bTrackRecording) {
                    m_bTrackRecording = true;
                    StartTrackingService(false);
                } else if (this.m_bSettingsAskBeforeStopTracking) {
                    AlertDialog alertDialog = new Builder(this).create();
                    alertDialog.setTitle("MM Tracker");
                    alertDialog.setMessage(getString(C0047R.string.MmTrackerActivity_ask_for_end_tracking));
                    alertDialog.setButton(getString(C0047R.string.yes), new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MMTrackerActivity.m_bTrackRecording = false;
                            MMTrackerActivity.this.StopTrackingService();
                        }
                    });
                    alertDialog.setButton2(getString(C0047R.string.no), new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    alertDialog.show();
                } else {
                    m_bTrackRecording = false;
                    StopTrackingService();
                }
                if (this.m_MapView == null) {
                    return true;
                }
                this.m_MapView.invalidateMapScreen(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean LoadTracks(String path, double dMinDistanceKm, long iLastFilesCount, long iLastFilesHours) {
        if (path == "") {
            return false;
        }
        File[] files;
        try {
            files = new File(path).listFiles(new CustomFileFilter("gpx", true));
        } catch (SecurityException e) {
            files = (File[]) null;
        }
        if (files == null) {
            return false;
        }
        if (!(iLastFilesCount == 0 && iLastFilesHours == 0)) {
            Arrays.sort(files, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    if (f1 == null || f2 == null) {
                        return 0;
                    }
                    return Long.valueOf(f2.lastModified()).compareTo(Long.valueOf(f1.lastModified()));
                }
            });
        }
        tracks.clear();
        for (File f : files) {
            if (!f.isDirectory()) {
                if (iLastFilesCount > 0 && iLastFilesCount != 999) {
                    iLastFilesCount--;
                    if (iLastFilesCount <= 0) {
                        return true;
                    }
                }
                boolean bTimeIsOk = true;
                if (iLastFilesHours > 0 && System.currentTimeMillis() - f.lastModified() > (3600 * iLastFilesHours) * 1000) {
                    bTimeIsOk = false;
                }
                if (bTimeIsOk) {
                    Tools.ReadGPXtoTracks(f.getAbsolutePath(), f.getName(), tracks, dMinDistanceKm, false, iLastFilesHours);
                }
            }
        }
        return true;
    }

    private boolean LoadRoutes(String path) {
        int i = 0;
        if (path == "") {
            return false;
        }
        File[] files = new File(path).listFiles(new CustomFileFilter("gpx", true));
        if (files == null) {
            return false;
        }
        routes.clear();
        int length = files.length;
        while (i < length) {
            File f = files[i];
            if (!f.isDirectory()) {
                Tools.ReadGPXtoRoutes(f.getAbsolutePath(), f.getName(), routes);
            }
            i++;
        }
        return true;
    }

    private boolean LoadWaypoints(String path) {
        int i = 0;
        if (path == "") {
            return false;
        }
        File[] files = new File(path).listFiles(new CustomFileFilter("gpx", true));
        if (files == null) {
            return false;
        }
        waypoints.clear();
        int length = files.length;
        while (i < length) {
            File f = files[i];
            if (!f.isDirectory()) {
                Tools.ReadGPXtoWaypoints(f.getAbsolutePath(), f.getName(), waypoints);
            }
            i++;
        }
        return true;
    }

    public void OpenMap(String sMap, double iNewLong, double iNewLat, float fShiftX, float fShiftY, int reset_position, boolean bMapWarning, int iDesiredZoomLevel, boolean bUseOldRotCenterValues) {
        boolean bAdaptPositionToZoomLevel = false;
        if (this.m_Qct != null) {
            this.m_Qct.closeMap();
        }
        this.m_Qct = null;
        this.m_Qct = new QuickChartFile();
        int iResult = -1;
        if (sMap != "") {
            iResult = this.m_Qct.openMap(sMap, this.m_SettingsNightMode);
        }
        if (iResult > 0) {
            m_CurrentMapName = sMap;
            if ((this.m_bGpsFix || this.m_bNetworkFix) && !this.m_Qct.IsInsideMap(this.m_dGpsLongitude, this.m_dGpsLatitude)) {
                WarnAndDisableGpsLock();
            }
            Iterator it = tracks.iterator();
            while (it.hasNext()) {
                Track track = (Track) it.next();
                track.RefreshXY(this.m_Qct);
                track.m_bCacheVaild = false;
            }
            it = routes.iterator();
            while (it.hasNext()) {
                Route route = (Route) it.next();
                route.RefreshXY(this.m_Qct);
                route.m_bCacheVaild = false;
            }
            it = waypoints.iterator();
            while (it.hasNext()) {
                Waypoint waypoint = (Waypoint) it.next();
                waypoint.RefreshXY(this.m_Qct);
                waypoint.m_bCacheVaild = false;
            }
            if (iDesiredZoomLevel != NO_CHANGE_IN_ZOOM_LEVEL) {
                this.m_iZoomLevel = iDesiredZoomLevel;
                bAdaptPositionToZoomLevel = true;
            }
            if (reset_position == NEW_POSITION_DEFAULT) {
                if (iDesiredZoomLevel == NO_CHANGE_IN_ZOOM_LEVEL) {
                    this.m_iZoomLevel = INITIAL_ZOOM_LEVEL;
                }
                this.m_iDispPosX = MAP_POSITION_INVALID;
                this.m_iDispPosY = MAP_POSITION_INVALID;
            }
            if (reset_position == NEW_POSITION_SET) {
                if (iDesiredZoomLevel == NO_CHANGE_IN_ZOOM_LEVEL) {
                    this.m_iZoomLevel = INITIAL_ZOOM_LEVEL;
                }
                this.m_iDispPosX = (int) Math.round((this.m_Qct.convertLongLatToX(iNewLong, iNewLat) * MapView.m_dZoomFactor[this.m_iZoomLevel]) - ((double) fShiftX));
                this.m_iDispPosY = (int) Math.round((this.m_Qct.convertLongLatToY(iNewLong, iNewLat) * MapView.m_dZoomFactor[this.m_iZoomLevel]) - ((double) fShiftY));
            }
        } else if (bMapWarning) {
            String s = new String();
            if (sMap == "") {
                s = getString(C0047R.string.MmTrackerActivity_no_map_defined);
            } else if (iResult == QuickChartFile.QCT_WRONG_FORMAT) {
                s = String.format("%s\n'%s'", new Object[]{getString(C0047R.string.MmTrackerActivity_map_has_wrong_format), sMap});
            } else {
                s = String.format("%s\n'%s'", new Object[]{getString(C0047R.string.MmTrackerActivity_could_not_load_map), sMap});
            }
            AlertDialog alertDialog = new Builder(this).create();
            alertDialog.setTitle("MM Tracker");
            alertDialog.setMessage(s);
            alertDialog.setButton("OK", new AnonymousClass14(sMap, this, bUseOldRotCenterValues));
            alertDialog.show();
        }
        this.m_MapView = null;
        this.m_MapView = new MapView(this, this.m_Qct, bAdaptPositionToZoomLevel, bUseOldRotCenterValues);
        setContentView(this.m_MapView);
        this.m_Tqm.UpdateQctAndClear(this.m_Qct);
        this.m_MapView.requestFocus();
    }

    protected void onResume() {
        super.onResume();
        this.m_bAppInPause = false;
        if (((this.m_MapView != null ? 1 : 0) & m_bTracksLoaded) != 0) {
            this.m_MapView.invalidateMapScreen(true);
        }
        this.m_SensorManager = (SensorManager) getSystemService("sensor");
        this.m_AccSensor = this.m_SensorManager.getDefaultSensor(1);
        this.m_SensorManager.registerListener(this.m_AccListener, this.m_AccSensor, 2);
        if (m_bSettingsShowCompass || m_iSettingsUseCompassForVector > 0 || m_iSettingsMapRotation == 1) {
            this.m_CompassSensor = this.m_SensorManager.getDefaultSensor(3);
            this.m_SensorManager.registerListener(this.m_CompassListener, this.m_CompassSensor, 2);
        }
        this.m_LocationManager.removeUpdates(this);
        if (this.m_LocationManager.getProvider("gps") != null) {
            this.m_LocationManager.requestLocationUpdates("gps", 0, 0.0f, this);
        }
        if (!this.m_SettingUseNetworkTriangulation) {
            this.m_bNetworkFix = false;
            this.m_iGpsType = 0;
        } else if (this.m_LocationManager.getProvider("network") != null) {
            this.m_LocationManager.requestLocationUpdates("network", 0, 0.0f, this);
            Location last_loc = this.m_LocationManager.getLastKnownLocation("network");
            if (last_loc != null) {
                onLocationChanged(last_loc);
            }
        }
    }

    protected void onPause() {
        super.onPause();
        this.m_bAppInPause = true;
        if (!(this.m_SettingsKeepTrackingAlive || m_bTrackRecording)) {
            this.m_LocationManager.removeUpdates(this);
        }
        if (this.m_SensorManager != null) {
            this.m_SensorManager.unregisterListener(this.m_CompassListener);
            this.m_SensorManager.unregisterListener(this.m_AccListener);
        }
        Editor ed = this.m_Prefs.edit();
        ed.putString("CurrentMap", m_CurrentMapName);
        ed.putInt("MapDisplayPosX", this.m_iDispPosX);
        ed.putInt("MapDisplayPosY", this.m_iDispPosY);
        ed.putInt("ZoomLevel", this.m_iZoomLevel);
        ed.putInt("WhatsnewCounter", this.m_iWhatsnewCounter);
        ed.putBoolean("ShowDisclaimer", this.m_bShowDisclaimer);
        ed.putString("DafaultMmiFile", m_sLastLoadedMmiFile);
        ed.commit();
    }

    protected void onDestroy() {
        super.onDestroy();
        this.m_Tqm.StopThread();
        if (this.m_SensorManager != null) {
            this.m_SensorManager.unregisterListener(this.m_CompassListener);
            this.m_SensorManager.unregisterListener(this.m_AccListener);
        }
        if (this.m_LocationManager != null) {
            this.m_LocationManager.removeUpdates(this);
        }
        if (this.TimerMain != null) {
            this.TimerMain.cancel();
        }
        if (this.TimerMapCache != null) {
            this.TimerMapCache.cancel();
        }
        if (this.m_TimerGpsSimulation != null) {
            this.m_TimerGpsSimulation.cancel();
        }
        if (this.m_Qct != null) {
            this.m_Qct.closeMap();
        }
        this.m_Tqm.RecycleBitmaps();
        while (!this.m_bMapCacheDbThreadFinished) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
        if (this.m_CursorCheckMapCache != null) {
            this.m_CursorCheckMapCache.close();
        }
        this.m_dbMapCache.close();
    }

    protected void onStop() {
        super.onStop();
        isFinishing();
    }

    private boolean CheckLastThreePositionsOutsideMap() {
        boolean bOutside = false;
        if (this.m_Qct == null || this.gps_list[POSITION_ARRAY_SIZE - 1] == null) {
            return false;
        }
        if (!this.m_Qct.IsPixelInsideMap(this.gps_list[POSITION_ARRAY_SIZE - 1].getLongitude(), this.gps_list[POSITION_ARRAY_SIZE - 1].getLatitude())) {
            if (this.gps_list[POSITION_ARRAY_SIZE - 2] == null) {
                bOutside = true;
            } else if (this.m_Qct.IsPixelInsideMap(this.gps_list[POSITION_ARRAY_SIZE - 2].getLongitude(), this.gps_list[POSITION_ARRAY_SIZE - 2].getLatitude())) {
                bOutside = true;
            } else if (!(this.gps_list[POSITION_ARRAY_SIZE - 3] == null || this.m_Qct.IsPixelInsideMap(this.gps_list[POSITION_ARRAY_SIZE - 3].getLongitude(), this.gps_list[POSITION_ARRAY_SIZE - 3].getLatitude()))) {
                bOutside = true;
            }
        }
        return bOutside;
    }

    public void onLocationChanged(Location location) {
        if (location != null) {
            if (location.getProvider().equals("network")) {
                this.m_LastLocationMillisNetwork = SystemClock.elapsedRealtime();
            }
            if (location.getProvider().equals("gps")) {
                this.m_LastLocationMillisGps = SystemClock.elapsedRealtime();
            }
            String sNewMap;
            if (location.getProvider().equals("network") && !this.m_bGpsFix) {
                this.m_dGpsLongitude = location.getLongitude();
                this.m_dGpsLatitude = location.getLatitude();
                m_dGpsAltitude = 0.0d;
                this.m_lGpsTime = location.getTime();
                m_dGpsSpeed = 0.0d;
                m_dGpsBearing = 0.0d;
                this.m_fGpsAccuracy = location.getAccuracy();
                this.m_iGpsType = LOCATION_TYPE_NETWORK;
                this.m_bNetworkFix = true;
                this.m_bStandstillDetected = true;
                GeomagneticField mfield = new GeomagneticField((float) this.m_dGpsLatitude, (float) this.m_dGpsLongitude, (float) m_dGpsAltitude, this.m_lGpsTime);
                if (mfield != null) {
                    this.m_fMagneticDeclination = mfield.getDeclination();
                }
                if (!(!m_bTracksLoaded || this.m_bAppInPause || this.m_MapView == null)) {
                    while (this.m_bChangeMapScale) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                        }
                    }
                    this.m_bPositionLockCheck = true;
                    if (!(this.m_Qct == null || !this.m_PositionLock || this.m_Qct.IsPixelInsideMap(this.m_dGpsLongitude, this.m_dGpsLatitude))) {
                        sNewMap = FindBestMapAtPosition(this.m_dGpsLongitude, this.m_dGpsLatitude);
                        if (sNewMap != "") {
                            OpenMap(sNewMap, 0.0d, 0.0d, 0.0f, 0.0f, NEW_POSITION_DEFAULT, true, this.m_iNewZoomLevel, true);
                        }
                    }
                    if (this.m_MapView.m_bInitDone) {
                        this.m_MapView.SetCurrentPosition(this.m_dGpsLongitude, this.m_dGpsLatitude, this.m_PositionLock, true);
                    }
                    if (!(!this.m_SettingsAutostartGpsLock || this.m_bAutostartLockDone || this.m_MapView == null)) {
                        this.m_bAutostartLockDone = true;
                        this.m_PositionLock = true;
                        this.m_MapView.SetGpsLock(true, false);
                    }
                    this.m_bPositionLockCheck = false;
                }
                if (m_bTracksLoaded && !this.m_bAppInPause && this.m_MapView != null) {
                    this.m_MapView.invalidateMapScreen(false);
                }
            } else if (location.getProvider().equals("gps")) {
                this.m_dGpsLongitude = location.getLongitude();
                this.m_dGpsLatitude = location.getLatitude();
                this.m_lGpsTime = location.getTime();
                m_dGpsSpeed = (double) location.getSpeed();
                m_dGpsBearing = (double) location.getBearing();
                double dAltTemp = location.getAltitude();
                this.m_iGpsType = LOCATION_TYPE_GPS;
                if (this.m_SettingsUseGeoidCorrection) {
                    m_dGpsAltitude = this.m_GeoidTable.Compensate(this.m_dGpsLatitude, this.m_dGpsLongitude, dAltTemp);
                } else {
                    m_dGpsAltitude = dAltTemp;
                }
                GeomagneticField geomagneticField = new GeomagneticField((float) this.m_dGpsLatitude, (float) this.m_dGpsLongitude, (float) m_dGpsAltitude, this.m_lGpsTime);
                if (geomagneticField != null) {
                    this.m_fMagneticDeclination = geomagneticField.getDeclination();
                }
                for (int i = 0; i < POSITION_ARRAY_SIZE - 1; i++) {
                    this.gps_list[i] = this.gps_list[i + 1];
                }
                this.gps_list[POSITION_ARRAY_SIZE - 1] = location;
                if (!(this.gps_list[POSITION_ARRAY_SIZE - 1] == null || this.gps_list[POSITION_ARRAY_SIZE - 2] == null)) {
                    boolean z = this.gps_list[POSITION_ARRAY_SIZE + -1].getSpeed() == 0.0f && this.gps_list[POSITION_ARRAY_SIZE - 2].getSpeed() == 0.0f;
                    this.m_bStandstillDetected = z;
                }
                CalcGpsAngleAndSpeed(this.gps_list);
                CalcBearingValues();
                if (this.m_MapView != null) {
                    this.m_MapView.SetSpeedVector(false, this.gps_list);
                }
                if (this.gps_list[POSITION_ARRAY_SIZE - 2] != null) {
                    if (this.m_NavigationStartTime == null) {
                        this.m_NavigationStartTime = new Date();
                    }
                    this.m_NavigationTotalDist += Tools.calcDistance(this.gps_list[POSITION_ARRAY_SIZE - 2].getLatitude(), this.gps_list[POSITION_ARRAY_SIZE - 2].getLongitude(), location.getLatitude(), location.getLongitude());
                }
                if (!(!m_bTracksLoaded || this.m_bAppInPause || this.m_MapView == null)) {
                    while (this.m_bChangeMapScale) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e2) {
                        }
                    }
                    this.m_bPositionLockCheck = true;
                    if (this.m_Qct != null && this.m_PositionLock && CheckLastThreePositionsOutsideMap()) {
                        sNewMap = FindBestMapAtPosition(this.m_dGpsLongitude, this.m_dGpsLatitude);
                        if (sNewMap != "") {
                            OpenMap(sNewMap, 0.0d, 0.0d, 0.0f, 0.0f, NEW_POSITION_DEFAULT, true, this.m_iNewZoomLevel, true);
                        }
                    }
                    if (this.m_MapView.m_bInitDone) {
                        this.m_MapView.SetCurrentPosition(this.m_dGpsLongitude, this.m_dGpsLatitude, this.m_PositionLock, true);
                    }
                    if (!(!this.m_SettingsAutostartGpsLock || this.m_bAutostartLockDone || this.m_MapView == null)) {
                        this.m_bAutostartLockDone = true;
                        this.m_PositionLock = true;
                        this.m_MapView.SetGpsLock(true, false);
                    }
                    this.m_bPositionLockCheck = false;
                }
                CheckNavigationTarget(this.m_dGpsLatitude, this.m_dGpsLongitude);
                if (m_bTracksLoaded && !this.m_bAppInPause && this.m_MapView != null) {
                    this.m_MapView.invalidateMapScreen(false);
                }
            }
        }
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void CalcGpsAngleAndSpeed(Location[] gps_list) {
        int iLastElement = POSITION_ARRAY_SIZE - 1;
        if (gps_list[0] == null || gps_list[iLastElement] == null) {
            m_dCalculatedGpsSpeed = 0.0d;
        } else if (gps_list[iLastElement].hasSpeed()) {
            m_dCalculatedGpsSpeed = (double) (((gps_list[0].getSpeed() + gps_list[iLastElement - 1].getSpeed()) + gps_list[iLastElement / 2].getSpeed()) / 3.0f);
        } else {
            m_dCalculatedGpsSpeed = Tools.calcDistance(gps_list[0].getLatitude(), gps_list[0].getLongitude(), gps_list[iLastElement].getLatitude(), gps_list[iLastElement].getLongitude());
            m_dCalculatedGpsSpeed /= (double) iLastElement;
            m_dCalculatedGpsSpeed *= 1000.0d;
        }
        if (this.m_Qct == null || gps_list[0] == null || gps_list[iLastElement] == null || m_dCalculatedGpsSpeed < ARROW_MIN_SPEED_TO_SHOW || this.m_bStandstillDetected) {
            m_dCalculatedGpsHeading = Double.NaN;
        } else {
            double dNewAngle;
            double dDeltaX = this.m_Qct.convertLongLatToX(gps_list[iLastElement].getLongitude(), gps_list[iLastElement].getLatitude()) - this.m_Qct.convertLongLatToX(gps_list[0].getLongitude(), gps_list[0].getLatitude());
            double dDeltaY = this.m_Qct.convertLongLatToY(gps_list[iLastElement].getLongitude(), gps_list[iLastElement].getLatitude()) - this.m_Qct.convertLongLatToY(gps_list[0].getLongitude(), gps_list[0].getLatitude());
            double dMeanDeltaX = (this.m_dOldArrowDeltaX + dDeltaX) / 2.0d;
            double dMeanDeltaY = (this.m_dOldArrowDeltaY + dDeltaY) / 2.0d;
            if (dMeanDeltaX != 0.0d) {
                dNewAngle = Math.atan(dMeanDeltaY / dMeanDeltaX);
            } else if (dMeanDeltaY < 0.0d) {
                dNewAngle = -1.5707963267948966d;
            } else {
                dNewAngle = 1.5707963267948966d;
            }
            if (dMeanDeltaX < 0.0d) {
                dNewAngle += 3.141592653589793d;
            }
            this.m_dOldArrowDeltaX = dDeltaX;
            this.m_dOldArrowDeltaY = dDeltaY;
            m_dCalculatedGpsAngle = Math.toRadians(90.0d) + dNewAngle;
            m_dCalculatedGpsHeading = Tools.CalcBearing(gps_list[0].getLatitude(), gps_list[0].getLongitude(), gps_list[iLastElement].getLatitude(), gps_list[iLastElement].getLongitude());
        }
        if (m_dCalculatedGpsHeading < 0.0d) {
            m_dCalculatedGpsHeading += 6.283185307179586d;
        }
    }

    public void CalcBearingValues() {
        if (m_NavigationTarget.m_Type != NavigationTarget.TARGET_TYPE_NONE) {
            m_dCalculatedNavigationBearing = Tools.CalcBearing(this.m_dGpsLatitude, this.m_dGpsLongitude, m_NavigationTarget.m_dGpsLat, m_NavigationTarget.m_dGpsLong);
        } else {
            m_dCalculatedNavigationBearing = Double.NaN;
        }
        if (m_dCalculatedNavigationBearing < 0.0d) {
            m_dCalculatedNavigationBearing += 6.283185307179586d;
        }
    }

    void LoadAllMapsAtPosition(double dLong, double dLat, boolean bShowActiveMap) {
        File[] files = Tools.listFilesAsArray(new File(m_SettingsMapPath), new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toUpperCase().endsWith(".QCT");
            }
        }, true);
        if (files != null) {
            maps.clear();
            for (File f : files) {
                if (!f.isDirectory()) {
                    boolean bValidCursor;
                    Cursor cr = this.m_dbMapCache.fetchCacheEntryByData(f.getName(), f.length(), f.lastModified());
                    if (cr == null) {
                        bValidCursor = false;
                    } else {
                        cr.moveToFirst();
                        bValidCursor = cr.getCount() > 0;
                    }
                    boolean bPointInBoundingBox = this.m_dbMapCache.TestPointInside(cr, dLat, dLong);
                    if (!bValidCursor || bPointInBoundingBox) {
                        if (!bValidCursor) {
                            double[] bbox = this.m_Qct.GetBoundingBoxFromFile(f.getAbsolutePath());
                            if (this.m_dbMapCache.isOpen() && bbox != null) {
                                this.m_dbMapCache.createCacheEntry(f.getName(), f.length(), f.lastModified(), bbox[0], bbox[1], bbox[2], bbox[3]);
                            }
                        }
                        if (this.m_Qct.IsInsideMapFile(f.getAbsolutePath(), dLong, dLat, 1)) {
                            boolean bActiveMap = m_CurrentMapName.equals(f.getAbsolutePath());
                            if (!bActiveMap || bShowActiveMap) {
                                maps.add(new MapList(f.getName(), f.getAbsolutePath(), this.m_Qct.getScaleMapFile(f.getAbsolutePath()), bActiveMap));
                            }
                        }
                    }
                }
            }
        }
    }

    String FindBestMapAtPosition(double dLong, double dLat) {
        if (this.m_Qct == null) {
            return "";
        }
        if (this.m_Qct.getScale() == 0.0d) {
            return "";
        }
        LoadAllMapsAtPosition(dLong, dLat, false);
        double dMaxError = 9.99999999E8d;
        MapList found_map = null;
        double dOldMapScale = this.m_Qct.getScale();
        Iterator it = maps.iterator();
        while (it.hasNext()) {
            MapList map = (MapList) it.next();
            if (map.m_dScale > 0.0d) {
                double dNewError;
                if (map.m_dScale > dOldMapScale) {
                    dNewError = map.m_dScale / dOldMapScale;
                } else {
                    dNewError = dOldMapScale / map.m_dScale;
                }
                dNewError = Math.abs(dNewError - 1.0d);
                if (dNewError < dMaxError) {
                    dMaxError = dNewError;
                    found_map = map;
                }
            }
        }
        this.m_iNewZoomLevel = INITIAL_ZOOM_LEVEL;
        if (found_map == null) {
            return "";
        }
        if (!(!this.m_bSettingsAdaptZoomlevel || found_map.m_dScale == 0.0d || this.m_MapView == null)) {
            this.m_iNewZoomLevel = this.m_MapView.FindZoomLevelFromScale((MapView.m_dZoomFactor[this.m_iZoomLevel] * dOldMapScale) / found_map.m_dScale, true);
        }
        return found_map.m_sMapPath;
    }

    public void LoadDifferentScaleMap(int iScaleType, double dLong, double dLat, float fScrollX, float fScrollY) {
        double dMaxScale = 0.0d;
        if (this.m_Qct != null) {
            while (this.m_bPositionLockCheck) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }
            this.m_bChangeMapScale = true;
            LoadAllMapsAtPosition(dLong, dLat, false);
            double dCurrentScale = this.m_Qct.getScale();
            int i = 0;
            while (i < maps.size()) {
                MapList map1 = (MapList) maps.get(i);
                if (iScaleType == LARGER_SCALE && map1.m_dScale <= dCurrentScale) {
                    maps.remove(map1);
                    i--;
                }
                if (iScaleType == SMALLER_SCALE && map1.m_dScale >= dCurrentScale) {
                    maps.remove(map1);
                    i--;
                }
                i++;
            }
            MapList found_map = null;
            if (iScaleType == LARGER_SCALE) {
                dMaxScale = 9999999.0d;
            }
            if (iScaleType == SMALLER_SCALE) {
                dMaxScale = 0.0d;
            }
            Iterator it = maps.iterator();
            while (it.hasNext()) {
                MapList map2 = (MapList) it.next();
                if (iScaleType == LARGER_SCALE && map2.m_dScale < dMaxScale) {
                    dMaxScale = map2.m_dScale;
                    found_map = map2;
                }
                if (iScaleType == SMALLER_SCALE && map2.m_dScale > dMaxScale) {
                    dMaxScale = map2.m_dScale;
                    found_map = map2;
                }
            }
            if (found_map != null) {
                this.m_iNewZoomLevel = INITIAL_ZOOM_LEVEL;
                if (!(found_map.m_dScale == 0.0d || this.m_MapView == null || !this.m_bSettingsAdaptZoomlevel)) {
                    this.m_iNewZoomLevel = this.m_MapView.FindZoomLevelFromScale((MapView.m_dZoomFactor[this.m_iZoomLevel] * dCurrentScale) / found_map.m_dScale, true);
                }
                OpenMap(found_map.m_sMapPath, dLong, dLat, fScrollX, fScrollY, NEW_POSITION_SET, true, this.m_iNewZoomLevel, true);
            } else if (this.m_MapView != null) {
                this.m_MapView.invalidateMapScreen(false);
            }
            this.m_bChangeMapScale = false;
        }
    }

    public void ShowMapsAtCursorList(double dLong, double dLat) {
        LoadAllMapsAtPosition(dLong, dLat, true);
        this.m_dStoreLongForMaplist = dLong;
        this.m_dStoreLatForMaplist = dLat;
        startActivityForResult(new Intent(getBaseContext(), MapListActivity.class), ACTIVITY_MAPLIST);
    }

    public void getPrefs() {
        int iTemp;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        m_SettingsMapPath = prefs.getString("editMapPath", "");
        try {
            if (m_SettingsMapPath.length() > 0 && m_SettingsMapPath.charAt(m_SettingsMapPath.length() - 1) != '/') {
                m_SettingsMapPath += "/";
            }
        } catch (Exception e) {
            m_SettingsMapPath = "";
        }
        m_SettingsTrackPath = prefs.getString("editTrackPath", "");
        try {
            if (m_SettingsTrackPath.length() > 0 && m_SettingsTrackPath.charAt(m_SettingsTrackPath.length() - 1) != '/') {
                m_SettingsTrackPath += "/";
            }
        } catch (Exception e2) {
            m_SettingsTrackPath = "";
        }
        m_SettingsRoutePath = prefs.getString("editRoutePath", "");
        try {
            if (m_SettingsRoutePath.length() > 0 && m_SettingsRoutePath.charAt(m_SettingsRoutePath.length() - 1) != '/') {
                m_SettingsRoutePath += "/";
            }
        } catch (Exception e3) {
            m_SettingsRoutePath = "";
        }
        m_SettingsWaypointPath = prefs.getString("editWaypointPath", "");
        try {
            if (m_SettingsWaypointPath.length() > 0 && m_SettingsWaypointPath.charAt(m_SettingsWaypointPath.length() - 1) != '/') {
                m_SettingsWaypointPath += "/";
            }
        } catch (Exception e4) {
            m_SettingsWaypointPath = "";
        }
        this.m_SettingsHideStatusbar = prefs.getBoolean("checkboxStatusBar", false);
        m_SettingsKeepDisplayOn = prefs.getBoolean("checkboxScreenOn", false);
        this.m_SettingsShowPosition = prefs.getBoolean("checkboxShowPosition", true);
        this.m_SettingsShowScale = prefs.getBoolean("checkboxShowScale", true);
        this.m_SettingsUseVolButtons = prefs.getBoolean("checkboxUseVolButtons", false);
        this.m_SettingsShowStausline = prefs.getBoolean("checkboxShowStatusline", true);
        this.m_SettingsShowTrackInfo = prefs.getBoolean("checkboxShowTrackInfo", false);
        this.m_SettingsShowZoombuttons = prefs.getBoolean("checkboxShowZoombuttons", true);
        this.m_SettingsShowMapscaleButtons = prefs.getBoolean("checkboxShowMapscaleButtons", true);
        this.m_SettingsUseLongClickSearchButton = prefs.getBoolean("checkboxUseSearchLongClick", false);
        this.m_SettingsUseClickSearchButton = prefs.getBoolean("checkboxUseSearchClick", true);
        this.m_SettingsAutostartTracking = prefs.getBoolean("checkboxAutostartTracking", false);
        this.m_SettingsAutostartGpsLock = prefs.getBoolean("checkboxAutostartGpsLock", false);
        this.m_SettingsUseGeoidCorrection = prefs.getBoolean("checkboxUseGeoidCorrection", true);
        this.m_SettingsUseClickEnterButton = prefs.getBoolean("checkboxUseCreateWpClick", false);
        this.m_SettingUseNetworkTriangulation = prefs.getBoolean("checkboxUseNetworkTriangulation", true);
        this.m_SettingsKeepTrackingAlive = prefs.getBoolean("checkboxGpsTrackingAlive", true);
        this.m_SettingsInvertDPad = prefs.getBoolean("checkboxInvertDPad", false);
        this.m_SettingsShowPositionWindow = prefs.getBoolean("checkboxShowPositionWindow", false);
        this.m_SettingsShowHeadingAndBearing = prefs.getBoolean("checkboxShowHeadingBearingWindow", false);
        this.m_SettingsLoadRtOnStartup = prefs.getBoolean("checkboxLoadRtOnStartup", true);
        this.m_SettingsLoadWpOnStartup = prefs.getBoolean("checkboxLoadWpOnStartup", true);
        m_bSettingsShowCompass = prefs.getBoolean("checkboxShowCompass", true);
        this.m_bSettingsShowTrueNorth = prefs.getBoolean("checkboxShowTrueNorth", true);
        this.m_bSettingsShowCompassHeading = prefs.getBoolean("checkboxShowCompassHeading", true);
        this.m_bSettingsAdaptZoomlevel = prefs.getBoolean("checkboxAdaptZoomLevel", true);
        this.m_bSettingsShowETA = prefs.getBoolean("checkboxShowETA", false);
        this.m_bSettingsAskBeforeStopTracking = prefs.getBoolean("checkboxAskBeforeStopTracking", false);
        try {
            this.m_SettingsNaviDisplay = Integer.parseInt(prefs.getString("listNaviDisplay", "3"));
        } catch (NumberFormatException e5) {
            this.m_SettingsNaviDisplay = 3;
        }
        try {
            m_SettingsUnitsDistances = Integer.parseInt(prefs.getString("listUnitsDistances", "0"));
        } catch (NumberFormatException e6) {
            m_SettingsUnitsDistances = 0;
        }
        try {
            m_SettingsUnitsAngles = Integer.parseInt(prefs.getString("listUnitsAngles", "0"));
        } catch (NumberFormatException e7) {
            m_SettingsUnitsAngles = 0;
        }
        try {
            this.m_SettingsArrowLength = Integer.parseInt(prefs.getString("listArrowLength", "30"));
        } catch (NumberFormatException e8) {
            this.m_SettingsArrowLength = 30;
        }
        try {
            m_SettingsGpsDataRate = Integer.parseInt(prefs.getString("listGpsRateNew", "1000"));
        } catch (NumberFormatException e9) {
            m_SettingsGpsDataRate = 1000;
        }
        try {
            m_SettingsGpsDistanceRate = (double) Integer.parseInt(prefs.getString("listGpsMinDistance", "0"));
        } catch (NumberFormatException e10) {
            m_SettingsGpsDistanceRate = 2.147483646E9d;
        }
        try {
            m_SettingsMinTrackResolution = Double.parseDouble(prefs.getString("listTrackMinDist", "0"));
        } catch (NumberFormatException e11) {
            m_SettingsMinTrackResolution = 0.0d;
        }
        try {
            this.m_iSettingKeyPadDisplayMove = Integer.parseInt(prefs.getString("listKeypadScrollDist", "0"));
        } catch (NumberFormatException e12) {
            this.m_iSettingKeyPadDisplayMove = 0;
        }
        try {
            this.m_SettingsNightMode = Integer.parseInt(prefs.getString("listNightMode", "0"));
        } catch (NumberFormatException e13) {
            this.m_SettingsNightMode = 0;
        }
        try {
            m_SettingsPositionType = Integer.parseInt(prefs.getString("listGrid", "0"));
        } catch (NumberFormatException e14) {
            m_SettingsPositionType = 0;
        }
        try {
            m_SettingsOrientation = Integer.parseInt(prefs.getString("listScreenOrientation", "1"));
        } catch (NumberFormatException e15) {
            m_SettingsOrientation = 1;
        }
        try {
            m_iSettingsUseCompassForVector = Integer.parseInt(prefs.getString("listUseCompassForVector", "0"));
        } catch (NumberFormatException e16) {
            m_iSettingsUseCompassForVector = 0;
        }
        try {
            this.m_iSettingsCompassType = Integer.parseInt(prefs.getString("listCompassType", "0"));
        } catch (NumberFormatException e17) {
            this.m_iSettingsCompassType = 0;
        }
        try {
            m_iSettingsMapRotation = Integer.parseInt(prefs.getString("listMapRotation", "0"));
        } catch (NumberFormatException e18) {
            m_iSettingsMapRotation = 0;
        }
        try {
            m_iSettingsColorNewTrack = (int) Long.parseLong(prefs.getString("listTrackDefaultColor", "0000FFFF"), 16);
        } catch (NumberFormatException e19) {
            m_iSettingsColorNewTrack = 65535;
        }
        try {
            m_iSettingsWidthNewTrack = Integer.parseInt(prefs.getString("listTrackDefaultWidth", "4"));
        } catch (NumberFormatException e20) {
            m_iSettingsWidthNewTrack = 4;
        }
        try {
            this.m_iSettingsColorSpeedVector = (int) Long.parseLong(prefs.getString("listSpeedVectorColor", "000000FF"), 16);
        } catch (NumberFormatException e21) {
            this.m_iSettingsColorSpeedVector = 255;
        }
        try {
            this.m_iSettingsColorNaviArrowLine = (int) Long.parseLong(prefs.getString("listNavArrowOutlineColor", "0000FF00"), 16);
        } catch (NumberFormatException e22) {
            this.m_iSettingsColorNaviArrowLine = 65280;
        }
        try {
            this.m_iSettingsColorNaviArrowFill = (int) Long.parseLong(prefs.getString("listNavArrowFillColor", "00FFFFFF"), 16);
        } catch (NumberFormatException e23) {
            this.m_iSettingsColorNaviArrowFill = 16777215;
        }
        try {
            this.m_iSettingsColorTriangulationFill = (int) Long.parseLong(prefs.getString("listNetworkFillColor", "000000FF"), 16);
        } catch (NumberFormatException e24) {
            this.m_iSettingsColorTriangulationFill = 255;
        }
        this.m_SettingsLoadTrOnStartupCount = 0;
        this.m_SettingsLoadTrOnStartupDate = 0;
        try {
            iTemp = Integer.parseInt(prefs.getString("listLoadTracksAtStartup", "0"));
        } catch (NumberFormatException e25) {
            iTemp = 0;
        }
        if (iTemp != 0) {
            if (iTemp < 1000) {
                this.m_SettingsLoadTrOnStartupCount = iTemp;
            } else {
                this.m_SettingsLoadTrOnStartupDate = iTemp - 1000;
            }
        }
        if (m_SettingsOrientation == 1) {
            this.m_fCompassOrientationCorrection = 0.0f;
        }
        if (m_SettingsOrientation == 0) {
            this.m_fCompassOrientationCorrection = 90.0f;
        }
        if (m_iSettingsMapRotation == 0) {
            this.m_fMapRotation = 0.0f;
            double d = (double) null;
            this.m_dSinMapRotation = d;
            this.m_dSinMapRotationNeg = d;
            this.m_dCosMapRotationNeg = 1.0d;
            this.m_dCosMapRotation = 1.0d;
        }
    }

    public static int CalcColor(int color, int opacity) {
        int[] color_table = new int[]{16711680, 65280, 255, 16776960, 16711935, 65535, 8323072, 32512, 127, 8355584, 8323199, 32639};
        if (color == 305419896) {
            return color_table[new Random(System.currentTimeMillis()).nextInt(11)] | opacity;
        }
        if (color == 286331153) {
            return 0;
        }
        return opacity | color;
    }

    public void ShowFileBrowser() {
        Intent i = new Intent(this, FileListActivity.class);
        i.putExtra("path", m_SettingsMapPath);
        i.putExtra("extension", "qct");
        i.putExtra("header", getString(C0047R.string.MmTrackerActivity_file_browser_header));
        i.putExtra("exclude", "");
        startActivityForResult(i, ACTIVITY_FILEBROWSER);
    }

    public void ShowOverlayManager() {
        this.m_bOverlayManagerRunning = true;
        Intent i = new Intent(this, OverlayManagerActivity.class);
        i.putExtra("units", m_SettingsUnitsDistances);
        i.putExtra("grid", m_SettingsPositionType);
        startActivityForResult(i, ACTIVITY_OVERLAYMANAGER);
    }

    public void ShowSettings() {
        startActivityForResult(new Intent(getBaseContext(), PrefsMainActivity.class), ACTIVITY_SETTINGS);
    }

    private void ShowTrackStyleSettings() {
        if (m_SelectedTrack != null) {
            Editor editor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
            editor.putString("textTrackName", m_SelectedTrack.m_sName);
            editor.putString("listTrackColor", String.format("%08X", new Object[]{Integer.valueOf(m_SelectedTrack.m_iColor & 16777215)}));
            editor.putString("listTrackWidth", String.format("%d", new Object[]{Integer.valueOf(Math.round(m_SelectedTrack.m_fWidth))}));
            editor.putString("listTrackOpacity", String.format("%d", new Object[]{Integer.valueOf(Tools.OpacityGranulation((m_SelectedTrack.m_iColor >> 24) & 255))}));
            editor.commit();
            startActivityForResult(new Intent(getBaseContext(), TrackStyleActivity.class), ACTIVITY_TRACKSTYLE);
        }
    }

    public void ShowRouteStyleSettings() {
        if (m_SelectedRoutepoint != null) {
            Editor editor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
            editor.putString("textRouteName", m_SelectedRoutepoint.m_ParentRoute.m_sName);
            editor.putString("listRouteColor", String.format("%08X", new Object[]{Integer.valueOf(m_SelectedRoutepoint.m_ParentRoute.m_iColor & 16777215)}));
            editor.putString("listRouteWidth", String.format("%d", new Object[]{Integer.valueOf(Math.round(m_SelectedRoutepoint.m_ParentRoute.m_fWidth))}));
            editor.putString("listRouteOpacity", String.format("%d", new Object[]{Integer.valueOf((m_SelectedRoutepoint.m_ParentRoute.m_iColor >> 24) & 255)}));
            editor.putBoolean("checkboxRouteLocked", m_SelectedRoutepoint.m_ParentRoute.m_bLocked);
            editor.commit();
            startActivityForResult(new Intent(getBaseContext(), RouteStyleActivity.class), ACTIVITY_ROUTESTYLE);
        }
    }

    public void ShowWaypointStyleSettings() {
        if (m_SelectedWaypoint != null) {
            Intent i = new Intent(this, WaypointStyleActivity.class);
            i.putExtra("textWaypointName", m_SelectedWaypoint.m_sName);
            i.putExtra("listWaypointColor", String.format("%08X", new Object[]{Integer.valueOf(m_SelectedWaypoint.m_iColor & 16777215)}));
            i.putExtra("listWaypointOpacity", String.format("%d", new Object[]{Integer.valueOf((m_SelectedWaypoint.m_iColor >> 24) & 255)}));
            i.putExtra("listWaypointIcon", m_SelectedWaypoint.m_sSymbol);
            i.putExtra("checkboxWaypointLabel", m_SelectedWaypoint.m_bShowLabel);
            i.putExtra("checkboxWaypointLocked", m_SelectedWaypoint.m_bLocked);
            i.putExtra("textWaypointDescription", m_SelectedWaypoint.m_sDesc);
            i.putExtra("waypoint_lat", m_SelectedWaypoint.m_dGpsLat);
            i.putExtra("waypoint_lon", m_SelectedWaypoint.m_dGpsLong);
            startActivityForResult(i, ACTIVITY_WAYPOINTSTYLE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String s;
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_SETTINGS) {
            getPrefs();
            if (TrackingService.m_bRecordingActive) {
                TrackingService.m_SettingsGpsDataRate = m_SettingsGpsDataRate;
                TrackingService.m_SettingsGpsDistanceRate = m_SettingsGpsDistanceRate;
                TrackingService.m_bSettingsGeoidCorrection = this.m_SettingsUseGeoidCorrection;
            }
            if (this.m_Qct != null) {
                this.m_Qct.setNightMode(this.m_SettingsNightMode);
                if (this.m_MapView != null) {
                    this.m_MapView.m_bDrawAll = true;
                    this.m_MapView.invalidateMapScreen(true);
                }
            }
        }
        if (!(requestCode != ACTIVITY_TRACKDATA || this.m_MapView == null || this.m_Qct == null || m_requestedTrRefresh == null)) {
            m_requestedTrRefresh.RefreshXY(this.m_Qct);
            m_requestedTrRefresh = null;
        }
        if (requestCode == ACTIVITY_OVERLAYMANAGER) {
            this.m_bOverlayManagerRunning = false;
            if (!(this.m_MapView == null || this.m_Qct == null)) {
                Iterator it;
                if (m_bRequestAllWpRefresh) {
                    it = waypoints.iterator();
                    while (it.hasNext()) {
                        ((Waypoint) it.next()).RefreshXY(this.m_Qct);
                    }
                    m_bRequestAllWpRefresh = false;
                }
                if (m_bRequestAllTrRefresh) {
                    it = tracks.iterator();
                    while (it.hasNext()) {
                        ((Track) it.next()).RefreshXY(this.m_Qct);
                    }
                    m_bRequestAllTrRefresh = false;
                }
                if (m_bRequestAllRtRefresh) {
                    it = routes.iterator();
                    while (it.hasNext()) {
                        ((Route) it.next()).RefreshXY(this.m_Qct);
                    }
                    m_bRequestAllRtRefresh = false;
                }
                if (m_requestedTrRefresh != null) {
                    m_requestedTrRefresh.RefreshXY(this.m_Qct);
                    m_requestedTrRefresh = null;
                }
                if (m_requestedWpRefresh != null) {
                    m_requestedWpRefresh.RefreshXY(this.m_Qct);
                    m_requestedWpRefresh = null;
                }
                if (m_bRequestRouteCreation) {
                    m_bRequestRouteCreation = false;
                    if (!(this.m_Qct == null || this.m_MapView == null)) {
                        Point p = this.m_MapView.convertScreenToMapCoordinates((float) (this.m_iRotateCenterX / 2), (float) (this.m_iRotateCenterY / 2));
                        CreateNewRoute(this.m_Qct.convertXYtoLongitude(p.x, p.y), this.m_Qct.convertXYtoLatitude(p.x, p.y));
                    }
                }
            }
            CheckForRequestAndJumpToPosition();
        }
        if (requestCode == ACTIVITY_COORDINATE && data != null) {
            double dLon = data.getDoubleExtra("result_lon", 0.0d);
            double dLat = data.getDoubleExtra("result_lat", 0.0d);
            String sWpName = data.getStringExtra("result_name");
            Boolean bJumpTo = Boolean.valueOf(data.getBooleanExtra("result_jump", false));
            Waypoint w = new Waypoint(dLon, dLat, Waypoint.WAYPOINT_COLOR_DEFAULT, "Circle");
            if (w != null) {
                w.m_sName = Tools.MakeProperFileName(sWpName).trim();
                w.m_bCacheVaild = false;
                w.m_bLocked = true;
                w.m_bShowLabel = true;
                w.WriteGpx(m_SettingsWaypointPath + Tools.MakeProperFileName(w.m_sName) + ".gpx");
                waypoints.add(w);
            }
            if (bJumpTo.booleanValue()) {
                m_dRequestViewLon = dLon;
                m_dRequestViewLat = dLat;
            }
            m_requestedWpRefresh = w;
            if (!(this.m_MapView == null || this.m_Qct == null || m_requestedWpRefresh == null)) {
                m_requestedWpRefresh.RefreshXY(this.m_Qct);
                m_requestedWpRefresh = null;
            }
            CheckForRequestAndJumpToPosition();
        }
        if (requestCode == ACTIVITY_MAPLIST && data != null && resultCode == 1) {
            this.m_iNewZoomLevel = INITIAL_ZOOM_LEVEL;
            if (!(this.m_Qct == null || this.m_MapView == null || !this.m_bSettingsAdaptZoomlevel)) {
                double dNewScale = this.m_Qct.getScaleMapFile(data.getStringExtra("map_path"));
                if (dNewScale != 0.0d) {
                    this.m_iNewZoomLevel = this.m_MapView.FindZoomLevelFromScale((this.m_Qct.getScale() * MapView.m_dZoomFactor[this.m_iZoomLevel]) / dNewScale, true);
                }
            }
            float tmsx = 25.0f;
            float tmsy = 25.0f;
            if (this.m_MapView != null) {
                tmsx = this.m_MapView.m_TouchMoveStartX;
                tmsy = this.m_MapView.m_TouchMoveStartY;
            }
            Intent intent = data;
            OpenMap(intent.getStringExtra("map_path"), this.m_dStoreLongForMaplist, this.m_dStoreLatForMaplist, tmsx, tmsy, NEW_POSITION_SET, true, this.m_iNewZoomLevel, true);
        }
        if (requestCode == ACTIVITY_TRACKSTYLE && m_SelectedTrack != null) {
            SharedPreferences prefs;
            boolean bNameChanged = false;
            prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            s = prefs.getString("listTrackColor", "FFFF0000");
            try {
                m_SelectedTrack.m_iColor = (int) Long.parseLong(s, 16);
            } catch (NumberFormatException e) {
                m_SelectedTrack.m_iColor = -65536;
            }
            s = prefs.getString("listTrackWidth", "4");
            try {
                m_SelectedTrack.m_fWidth = (float) Double.parseDouble(s);
            } catch (NumberFormatException e2) {
                m_SelectedTrack.m_fWidth = 4.0f;
            }
            s = prefs.getString("listTrackOpacity", "255");
            try {
                m_SelectedTrack.m_iColor = (m_SelectedTrack.m_iColor & 16777215) | (((int) Long.parseLong(s)) << 24);
            } catch (NumberFormatException e3) {
            }
            if (m_SelectedTrack.m_sName.compareToIgnoreCase(prefs.getString("textTrackName", "no_name")) != 0) {
                bNameChanged = true;
            }
            m_SelectedTrack.m_sName = prefs.getString("textTrackName", "no_name");
            if (!m_SelectedTrack.m_CreatedByMMTracker) {
                Toast.makeText(getBaseContext(), getString(C0047R.string.MmTrackerActivity_toast_tr_not_stored), 1).show();
            } else if (bNameChanged) {
                m_SelectedTrack.ReWriteGpx(getBaseContext(), new StringBuilder(String.valueOf(m_SelectedTrack.m_sName)).append(".gpx").toString());
            } else {
                m_SelectedTrack.ReWriteGpx(getBaseContext(), "");
            }
        }
        if (requestCode == ACTIVITY_ROUTESTYLE && m_SelectedRoutepoint != null) {
            prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            s = prefs.getString("listRouteColor", "FFFF0000");
            try {
                m_SelectedRoutepoint.m_ParentRoute.m_iColor = (int) Long.parseLong(s, 16);
            } catch (NumberFormatException e4) {
                m_SelectedRoutepoint.m_ParentRoute.m_iColor = -65536;
            }
            s = prefs.getString("listRouteWidth", "4");
            try {
                m_SelectedRoutepoint.m_ParentRoute.m_fWidth = (float) Double.parseDouble(s);
            } catch (NumberFormatException e5) {
                m_SelectedRoutepoint.m_ParentRoute.m_fWidth = 4.0f;
            }
            s = prefs.getString("listRouteOpacity", "255");
            try {
                m_SelectedRoutepoint.m_ParentRoute.m_iColor = (m_SelectedRoutepoint.m_ParentRoute.m_iColor & 16777215) | (((int) Long.parseLong(s)) << 24);
            } catch (NumberFormatException e6) {
            }
            m_SelectedRoutepoint.m_ParentRoute.m_sName = prefs.getString("textRouteName", "no_name");
            m_SelectedRoutepoint.m_ParentRoute.m_bLocked = prefs.getBoolean("checkboxRouteLocked", false);
            if (!m_SelectedRoutepoint.m_ParentRoute.m_CreatedByMMTracker || m_SelectedRoutepoint.m_ParentRoute.m_sFileName == "") {
                if (!m_SelectedRoutepoint.m_ParentRoute.m_bNoSaveWarningShown) {
                    Toast.makeText(getBaseContext(), getString(C0047R.string.MmTrackerActivity_toast_rt_not_stored), 1).show();
                    m_SelectedRoutepoint.m_ParentRoute.m_bNoSaveWarningShown = true;
                }
            } else if (m_CurrentlyCreatedRoute != m_SelectedRoutepoint.m_ParentRoute) {
                new File(m_SelectedRoutepoint.m_ParentRoute.m_sFileName).delete();
                m_SelectedRoutepoint.m_ParentRoute.WriteGPX(m_SettingsRoutePath + Tools.MakeProperFileName(m_SelectedRoutepoint.m_ParentRoute.m_sName) + ".gpx");
            }
        }
        if (!(requestCode != ACTIVITY_WAYPOINTSTYLE || m_SelectedWaypoint == null || data == null)) {
            s = data.getStringExtra("listWaypointColor");
            try {
                m_SelectedWaypoint.m_iColor = (int) Long.parseLong(s, 16);
            } catch (NumberFormatException e7) {
                m_SelectedWaypoint.m_iColor = -65536;
            }
            s = data.getStringExtra("listWaypointOpacity");
            try {
                m_SelectedWaypoint.m_iColor = (m_SelectedWaypoint.m_iColor & 16777215) | (((int) Long.parseLong(s)) << 24);
            } catch (NumberFormatException e8) {
            }
            m_SelectedWaypoint.m_sSymbol = data.getStringExtra("listWaypointIcon");
            m_SelectedWaypoint.m_iSymbol = m_SelectedWaypoint.CalcSymbolNumber();
            m_SelectedWaypoint.m_bShowLabel = data.getBooleanExtra("checkboxWaypointLabel", false);
            m_SelectedWaypoint.m_bLocked = data.getBooleanExtra("checkboxWaypointLocked", false);
            m_SelectedWaypoint.m_sName = data.getStringExtra("textWaypointName");
            m_SelectedWaypoint.m_sDesc = data.getStringExtra("textWaypointDescription");
            if (!(this.m_MapView == null || this.m_Qct == null || m_requestedWpRefresh == null)) {
                m_requestedWpRefresh.m_dGpsLat = data.getDoubleExtra("new_lat", m_requestedWpRefresh.m_dGpsLat);
                m_requestedWpRefresh.m_dGpsLong = data.getDoubleExtra("new_lon", m_requestedWpRefresh.m_dGpsLong);
                m_requestedWpRefresh.RefreshXY(this.m_Qct);
                m_dRequestViewLat = m_requestedWpRefresh.m_dGpsLat;
                m_dRequestViewLon = m_requestedWpRefresh.m_dGpsLong;
                m_requestedWpRefresh = null;
            }
            if (m_SelectedWaypoint.m_CreatedByMMTracker && m_SelectedWaypoint.m_sFileName != "" && m_SelectedWaypoint.m_bFromFile) {
                new File(m_SelectedWaypoint.m_sFileName).delete();
                m_SelectedWaypoint.WriteGpx(m_SettingsWaypointPath + Tools.MakeProperFileName(m_SelectedWaypoint.m_sName) + ".gpx");
            } else if (m_SelectedWaypoint.m_bFromFile) {
                Toast.makeText(getBaseContext(), getString(C0047R.string.MmTrackerActivity_toast_wp_not_stored), 1).show();
            }
            CheckForRequestAndJumpToPosition();
        }
        if (requestCode == ACTIVITY_FILEBROWSER && data != null) {
            if (resultCode == 1) {
                OpenMap(data.getStringExtra("file"), 0.0d, 0.0d, 0.0f, 0.0f, NEW_POSITION_DEFAULT, true, NO_CHANGE_IN_ZOOM_LEVEL, true);
            } else if (resultCode == 2) {
                ShowSettings();
            }
        }
    }

    private void CheckForRequestAndJumpToPosition() {
        if (m_dRequestViewLon != 9999.0d && m_dRequestViewLat != 9999.0d && m_bTracksLoaded) {
            if (!(this.m_MapView == null || this.m_Qct == null)) {
                WarnAndDisableGpsLock();
                if (this.m_Qct.IsPixelInsideMap(m_dRequestViewLon, m_dRequestViewLat)) {
                    this.m_MapView.PanMapToCenterPosition(m_dRequestViewLon, m_dRequestViewLat);
                } else {
                    String sNewMap = FindBestMapAtPosition(m_dRequestViewLon, m_dRequestViewLat);
                    if (sNewMap != "") {
                        OpenMap(sNewMap, m_dRequestViewLon, m_dRequestViewLat, (float) this.m_iGpsLockCenterX, (float) this.m_iGpsLockCenterY, NEW_POSITION_SET, true, this.m_iNewZoomLevel, false);
                    }
                }
            }
            m_dRequestViewLon = 9999.0d;
            m_dRequestViewLat = 9999.0d;
            if (this.m_MapView != null) {
                this.m_MapView.invalidateMapScreen(true);
            }
        }
    }

    private void WarnAndDisableGpsLock() {
        if (this.m_PositionLock) {
            Toast.makeText(getBaseContext(), getString(C0047R.string.MmTrackerActivity_toast_warn_disable_gpslock), 1).show();
            if (this.m_MapView != null) {
                this.m_MapView.DisableGpsLock(false);
            }
            this.m_PositionLock = false;
        }
    }

    public Track GetNearestTrack(float fX, float fY) {
        double dLatLongMin = 9999999.0d;
        Trackpoint selected_trackpoint = null;
        Track selected_track = null;
        double dDist = -99.0d;
        if (tracks.size() == 0) {
            return null;
        }
        if (this.m_MapView == null) {
            return null;
        }
        if (this.m_Qct == null) {
            return null;
        }
        Point pClick = this.m_MapView.convertScreenToMapCoordinates(fX, fY);
        double dLat = this.m_Qct.convertXYtoLatitude(pClick.x, pClick.y);
        double dLong = this.m_Qct.convertXYtoLongitude(pClick.x, pClick.y);
        Iterator it = tracks.iterator();
        while (it.hasNext()) {
            Track track = (Track) it.next();
            if (track.m_bVisible) {
                Iterator it2 = track.trackpoints.iterator();
                while (it2.hasNext()) {
                    Trackpoint trackpoint = (Trackpoint) it2.next();
                    if (Math.abs(dLat - trackpoint.m_dGpsLat) + Math.abs(dLong - trackpoint.m_dGpsLong) < dLatLongMin) {
                        dLatLongMin = Math.abs(dLat - trackpoint.m_dGpsLat) + Math.abs(dLong - trackpoint.m_dGpsLong);
                        selected_track = track;
                        selected_trackpoint = trackpoint;
                    }
                }
            }
        }
        if (selected_trackpoint != null) {
            double dTrackPixelX = this.m_Qct.convertLongLatToX(selected_trackpoint.m_dGpsLong, selected_trackpoint.m_dGpsLat);
            double dTrackPixelY = this.m_Qct.convertLongLatToY(selected_trackpoint.m_dGpsLong, selected_trackpoint.m_dGpsLat);
            dDist = Math.sqrt(((dTrackPixelX - ((double) pClick.x)) * (dTrackPixelX - ((double) pClick.x))) + ((dTrackPixelY - ((double) pClick.y)) * (dTrackPixelY - ((double) pClick.y))));
            if (this.m_MapView != null) {
                dDist *= this.m_MapView.getCurrentZoom();
            }
        }
        if (selected_track != null) {
            selected_track.m_dDummyDistance = dDist;
        }
        return selected_track;
    }

    public Routepoint GetNearestRoutepoint(float fX, float fY) {
        double dLatLongMin = 9999999.0d;
        Routepoint selected_routepoint = null;
        Route selected_route = null;
        double dDist = -99.0d;
        if (routes.size() == 0) {
            return null;
        }
        if (this.m_MapView == null) {
            return null;
        }
        if (this.m_Qct == null) {
            return null;
        }
        Point pClick = this.m_MapView.convertScreenToMapCoordinates(fX, fY);
        double dLat = this.m_Qct.convertXYtoLatitude(pClick.x, pClick.y);
        double dLong = this.m_Qct.convertXYtoLongitude(pClick.x, pClick.y);
        Iterator it = routes.iterator();
        while (it.hasNext()) {
            Route route = (Route) it.next();
            if (route.m_bVisible) {
                Iterator it2 = route.routepoints.iterator();
                while (it2.hasNext()) {
                    Routepoint routepoint = (Routepoint) it2.next();
                    if (Math.abs(dLat - routepoint.m_dGpsLat) + Math.abs(dLong - routepoint.m_dGpsLong) < dLatLongMin) {
                        dLatLongMin = Math.abs(dLat - routepoint.m_dGpsLat) + Math.abs(dLong - routepoint.m_dGpsLong);
                        selected_route = route;
                        selected_routepoint = routepoint;
                    }
                }
            }
        }
        if (selected_routepoint != null) {
            double dTrackPixelX = this.m_Qct.convertLongLatToX(selected_routepoint.m_dGpsLong, selected_routepoint.m_dGpsLat);
            double dTrackPixelY = this.m_Qct.convertLongLatToY(selected_routepoint.m_dGpsLong, selected_routepoint.m_dGpsLat);
            dDist = Math.sqrt(((dTrackPixelX - ((double) pClick.x)) * (dTrackPixelX - ((double) pClick.x))) + ((dTrackPixelY - ((double) pClick.y)) * (dTrackPixelY - ((double) pClick.y))));
            if (this.m_MapView != null) {
                dDist *= this.m_MapView.getCurrentZoom();
            }
        }
        if (selected_route != null) {
            selected_route.m_dDummyDistance = dDist;
        }
        return selected_routepoint;
    }

    public Waypoint GetNearestWaypoint(float fX, float fY) {
        double dLatLongMin = 9999999.0d;
        Waypoint selected_waypoint = null;
        if (waypoints.size() == 0) {
            return null;
        }
        if (this.m_Qct == null) {
            return null;
        }
        if (this.m_MapView == null) {
            return null;
        }
        Point pClick = this.m_MapView.convertScreenToMapCoordinates(fX, fY);
        double dLat = this.m_Qct.convertXYtoLatitude(pClick.x, pClick.y);
        double dLong = this.m_Qct.convertXYtoLongitude(pClick.x, pClick.y);
        Iterator it = waypoints.iterator();
        while (it.hasNext()) {
            Waypoint wp = (Waypoint) it.next();
            if (wp.m_bVisible) {
                if (Math.abs(dLat - wp.m_dGpsLat) + Math.abs(dLong - wp.m_dGpsLong) < dLatLongMin) {
                    dLatLongMin = Math.abs(dLat - wp.m_dGpsLat) + Math.abs(dLong - wp.m_dGpsLong);
                    selected_waypoint = wp;
                }
            }
        }
        if (selected_waypoint != null) {
            double dTrackPixelX = this.m_Qct.convertLongLatToX(selected_waypoint.m_dGpsLong, selected_waypoint.m_dGpsLat);
            double dTrackPixelY = this.m_Qct.convertLongLatToY(selected_waypoint.m_dGpsLong, selected_waypoint.m_dGpsLat);
            double dDist = Math.sqrt(((dTrackPixelX - ((double) pClick.x)) * (dTrackPixelX - ((double) pClick.x))) + ((dTrackPixelY - ((double) pClick.y)) * (dTrackPixelY - ((double) pClick.y))));
            if (this.m_MapView != null) {
                dDist *= this.m_MapView.getCurrentZoom();
            }
            selected_waypoint.m_dDummyDistance = dDist;
        }
        return selected_waypoint;
    }

    static void NavigateToWaypoint(Waypoint target_wp) {
        m_NavigationTarget.m_ParentRoute = null;
        m_NavigationTarget.m_Routepoint = null;
        m_NavigationTarget.m_Waypoint = target_wp;
        m_NavigationTarget.m_Type = NavigationTarget.TARGET_TYPE_WAYPOINT;
        m_NavigationTarget.m_dGpsLat = target_wp.m_dGpsLat;
        m_NavigationTarget.m_dGpsLong = target_wp.m_dGpsLong;
    }

    static void NavigateToRoutepoint(Routepoint target_rp) {
        if (m_NavigationTarget != null && target_rp != null) {
            m_NavigationTarget.m_ParentRoute = target_rp.m_ParentRoute;
            m_NavigationTarget.m_Routepoint = target_rp;
            m_NavigationTarget.m_Waypoint = null;
            m_NavigationTarget.m_Type = NavigationTarget.TARGET_TYPE_ROUTEPOINT;
            m_NavigationTarget.m_dGpsLat = target_rp.m_dGpsLat;
            m_NavigationTarget.m_dGpsLong = target_rp.m_dGpsLong;
            target_rp.m_ParentRoute.m_bCacheVaild = false;
        }
    }

    public void CheckNavigationTarget(double dLat, double dLon) {
        if (m_NavigationTarget.m_Type != NavigationTarget.TARGET_TYPE_NONE) {
            if (m_NavigationTarget.m_Type == NavigationTarget.TARGET_TYPE_ROUTEPOINT) {
                m_NavigationTarget.m_ParentRoute.CalcAllXteAtd(dLat, dLon);
                if (!m_NavigationTarget.m_Routepoint.m_bPointInLeg) {
                    Iterator it = m_NavigationTarget.m_ParentRoute.routepoints.iterator();
                    while (it.hasNext()) {
                        Routepoint rp = (Routepoint) it.next();
                        if (rp.m_bPointInLeg) {
                            NavigateToRoutepoint(rp);
                            if (this.m_MapView != null) {
                                this.m_MapView.invalidateMapScreen(false);
                            }
                        }
                    }
                }
                if (m_NavigationTarget.m_Routepoint.isFirstRoutepoint()) {
                    if (Tools.calcDistance(m_NavigationTarget.m_dGpsLat, m_NavigationTarget.m_dGpsLong, dLat, dLon) <= NAVIGATION_TARGET_DISTANCE) {
                        NavigationSwitchToNextRoutepoint();
                    }
                } else if (Math.abs(m_NavigationTarget.m_Routepoint.m_dATD - m_NavigationTarget.m_Routepoint.m_dLegLength) <= NAVIGATION_TARGET_DISTANCE && m_NavigationTarget.m_Routepoint.m_dATDinv <= NAVIGATION_TARGET_DISTANCE) {
                    NavigationSwitchToNextRoutepoint();
                }
            } else if (Tools.calcDistance(m_NavigationTarget.m_dGpsLat, m_NavigationTarget.m_dGpsLong, dLat, dLon) <= NAVIGATION_TARGET_DISTANCE) {
                m_NavigationTarget.m_Type = NavigationTarget.TARGET_TYPE_NONE;
            }
        }
    }

    void NavigationSwitchToNextRoutepoint() {
        Route r = m_NavigationTarget.m_ParentRoute;
        for (int i = 0; i < r.routepoints.size(); i++) {
            if (((Routepoint) r.routepoints.get(i)).equals(m_NavigationTarget.m_Routepoint)) {
                if (i < r.routepoints.size() - 1) {
                    NavigateToRoutepoint((Routepoint) r.routepoints.get(i + 1));
                    if (this.m_MapView != null) {
                        this.m_MapView.invalidateMapScreen(false);
                        return;
                    }
                    return;
                }
                m_NavigationTarget.m_Type = NavigationTarget.TARGET_TYPE_NONE;
            }
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (this.m_MapView != null) {
            Track l_SelectedTrack = null;
            Waypoint l_SelectedWaypoint = null;
            this.m_bContextMenuActive = true;
            Routepoint l_SelectedRoutepoint = GetNearestRoutepoint(this.m_MapView.m_TouchMoveStartX, this.m_MapView.m_TouchMoveStartY);
            if (m_CurrentlyCreatedRoute == null) {
                l_SelectedTrack = GetNearestTrack(this.m_MapView.m_TouchMoveStartX, this.m_MapView.m_TouchMoveStartY);
                l_SelectedWaypoint = GetNearestWaypoint(this.m_MapView.m_TouchMoveStartX, this.m_MapView.m_TouchMoveStartY);
            } else if (l_SelectedRoutepoint.m_ParentRoute != m_CurrentlyCreatedRoute) {
                l_SelectedRoutepoint = null;
            }
            if (l_SelectedWaypoint != null && l_SelectedWaypoint.m_dDummyDistance < ((double) DISTANCE_TO_TRACK_FOR_MENU)) {
                m_SelectedWaypoint = l_SelectedWaypoint;
                menu.setHeaderTitle("WP: " + l_SelectedWaypoint.m_sName);
                menu.add(0, 0, 1, getString(C0047R.string.MmTrackerActivity_context_wp_change_props));
                menu.add(1, 1, 1, getString(C0047R.string.MmTrackerActivity_context_wp_hide));
                menu.add(1, 2, 1, getString(C0047R.string.MmTrackerActivity_context_wp_delete));
                if (m_NavigationTarget.m_Type == NavigationTarget.TARGET_TYPE_WAYPOINT) {
                    if (l_SelectedWaypoint == m_NavigationTarget.m_Waypoint) {
                        menu.setGroupEnabled(1, false);
                    }
                    if (l_SelectedWaypoint == m_NavigationTarget.m_Waypoint) {
                        menu.add(0, 3, 1, getString(C0047R.string.MmTrackerActivity_context_wp_cancel_navigate));
                    } else {
                        menu.add(0, 3, 1, getString(C0047R.string.MmTrackerActivity_context_wp_navigate));
                    }
                } else {
                    menu.add(0, 3, 1, getString(C0047R.string.MmTrackerActivity_context_wp_navigate));
                }
                menu.add(0, 4, 1, getString(C0047R.string.MmTrackerActivity_context_wp_cancel));
                this.m_ActiveContextMenu = CONTEXT_MENU_WAYPOINTS;
            } else if (l_SelectedRoutepoint != null && l_SelectedRoutepoint.m_ParentRoute.m_dDummyDistance < ((double) DISTANCE_TO_TRACK_FOR_MENU)) {
                m_SelectedRoutepoint = l_SelectedRoutepoint;
                menu.setHeaderTitle("Route: " + l_SelectedRoutepoint.m_ParentRoute.m_sName);
                menu.add(0, 0, 1, getString(C0047R.string.MmTrackerActivity_context_rt_change_props));
                menu.add(0, 1, 1, getString(C0047R.string.MmTrackerActivity_context_rt_show_data));
                menu.add(1, 2, 1, getString(C0047R.string.MmTrackerActivity_context_rt_delete));
                menu.add(3, 3, 1, getString(C0047R.string.MmTrackerActivity_context_rt_add_point));
                menu.add(3, 4, 1, getString(C0047R.string.MmTrackerActivity_context_rt_del_point));
                if (m_NavigationTarget.m_Type == NavigationTarget.TARGET_TYPE_ROUTEPOINT) {
                    if (l_SelectedRoutepoint.m_ParentRoute == m_NavigationTarget.m_ParentRoute) {
                        menu.setGroupEnabled(1, false);
                        menu.setGroupEnabled(3, false);
                    }
                    if (l_SelectedRoutepoint == m_NavigationTarget.m_Routepoint) {
                        menu.add(2, 5, 1, getString(C0047R.string.MmTrackerActivity_context_rt_cancel_navigate));
                    } else {
                        menu.add(2, 5, 1, getString(C0047R.string.MmTrackerActivity_context_rt_navigate));
                    }
                } else {
                    menu.add(2, 5, 1, getString(C0047R.string.MmTrackerActivity_context_rt_navigate));
                }
                menu.add(1, 6, 1, getString(C0047R.string.MmTrackerActivity_context_rt_hide));
                menu.add(0, 7, 1, getString(C0047R.string.MmTrackerActivity_context_rt_cancel));
                if (m_CurrentlyCreatedRoute != null) {
                    menu.setGroupEnabled(1, false);
                    menu.setGroupEnabled(2, false);
                }
                this.m_ActiveContextMenu = CONTEXT_MENU_ROUTES;
            } else if (l_SelectedTrack == null || l_SelectedTrack.m_dDummyDistance >= ((double) DISTANCE_TO_TRACK_FOR_MENU)) {
                menu.setHeaderTitle(getString(C0047R.string.MmTrackerActivity_context_standard_Actions));
                menu.add(0, 0, 1, getString(C0047R.string.MmTrackerActivity_context_standard_maps_at_pos));
                menu.add(0, 1, 1, getString(C0047R.string.MmTrackerActivity_context_standard_maps_larger));
                menu.add(0, 2, 1, getString(C0047R.string.MmTrackerActivity_context_standard_maps_smaller));
                menu.add(1, 3, 1, getString(C0047R.string.MmTrackerActivity_context_standard_create_rt));
                SubMenu WpMenu = menu.addSubMenu(1, 4, 1, getString(C0047R.string.MmTrackerActivity_context_standard_create_wp));
                menu.add(0, 5, 1, getString(C0047R.string.MmTrackerActivity_context_standard_cancel));
                WpMenu.add(0, 0, 1, getString(C0047R.string.MmTrackerActivity_context_standard_create_wp_here));
                WpMenu.add(1, 1, 1, getString(C0047R.string.MmTrackerActivity_context_standard_create_wp_gps));
                WpMenu.add(0, 2, 1, getString(C0047R.string.MmTrackerActivity_context_standard_create_wp_manually));
                WpMenu.add(0, 3, 1, getString(C0047R.string.MmTrackerActivity_context_standard_cancel));
                if (m_CurrentlyCreatedRoute != null) {
                    menu.setGroupEnabled(1, false);
                }
                if (!this.m_bGpsFix) {
                    WpMenu.setGroupEnabled(1, false);
                }
                this.m_ActiveContextMenu = CONTEXT_MENU_ACTIONS;
            } else {
                m_SelectedTrack = l_SelectedTrack;
                menu.setHeaderTitle("Track: " + l_SelectedTrack.m_sName);
                menu.add(0, 0, 1, getString(C0047R.string.MmTrackerActivity_context_tr_change_props));
                menu.add(0, 1, 1, getString(C0047R.string.MmTrackerActivity_context_tr_show_data));
                menu.add(0, 2, 1, getString(C0047R.string.MmTrackerActivity_context_tr_hide));
                menu.add(0, 3, 1, getString(C0047R.string.MmTrackerActivity_context_tr_delete));
                menu.add(0, 4, 1, getString(C0047R.string.MmTrackerActivity_context_tr_delete_all));
                menu.add(0, 5, 1, getString(C0047R.string.MmTrackerActivity_context_tr_cancel));
                if (DEBUG_GPS_SIMULATION) {
                    menu.add(0, 6, 1, "Use for simulation");
                }
                this.m_ActiveContextMenu = CONTEXT_MENU_TRACKS;
            }
        }
    }

    public void onContextMenuClosed(Menu menu) {
        this.m_bContextMenuActive = false;
    }

    public boolean onContextItemSelected(MenuItem item) {
        this.m_bContextMenuActive = false;
        if (this.m_ActiveContextMenu != CONTEXT_MENU_ROUTES) {
            if (this.m_ActiveContextMenu != CONTEXT_MENU_WAYPOINTS) {
                if (this.m_ActiveContextMenu != CONTEXT_MENU_TRACKS) {
                    Point p;
                    if (this.m_ActiveContextMenu != CONTEXT_MENU_ACTIONS) {
                        if (this.m_ActiveContextMenu == CONTEXT_MENU_ACTIONS_SUBMENU_WP) {
                            if (this.m_Qct != null && this.m_MapView != null) {
                                p = this.m_MapView.convertScreenToMapCoordinates(this.m_MapView.m_TouchMoveStartX, this.m_MapView.m_TouchMoveStartY);
                                switch (item.getItemId()) {
                                    case 0:
                                        CreateNewWaypoint(this.m_Qct.convertXYtoLongitude(p.x, p.y), this.m_Qct.convertXYtoLatitude(p.x, p.y), true, false);
                                        return true;
                                    case 1:
                                        if (this.m_bGpsFix) {
                                            CreateNewWaypoint(this.m_dGpsLongitude, this.m_dGpsLatitude, true, true);
                                        }
                                        return true;
                                    case 2:
                                        Intent coordinateActivity = new Intent(getBaseContext(), CoordinateEntryActivity.class);
                                        coordinateActivity.putExtra("longitude", 0);
                                        coordinateActivity.putExtra("latitude", 0);
                                        coordinateActivity.putExtra("use_init_coords", false);
                                        coordinateActivity.putExtra("dialog_simple", false);
                                        startActivityForResult(coordinateActivity, ACTIVITY_COORDINATE);
                                        return true;
                                    default:
                                        break;
                                }
                            }
                            return true;
                        }
                    } else if (this.m_Qct != null && this.m_MapView != null) {
                        p = this.m_MapView.convertScreenToMapCoordinates(this.m_MapView.m_TouchMoveStartX, this.m_MapView.m_TouchMoveStartY);
                        switch (item.getItemId()) {
                            case 0:
                                ShowMapsAtCursorList(this.m_Qct.convertXYtoLongitude(p.x, p.y), this.m_Qct.convertXYtoLatitude(p.x, p.y));
                                return true;
                            case 1:
                                LoadDifferentScaleMap(LARGER_SCALE, this.m_Qct.convertXYtoLongitude(p.x, p.y), this.m_Qct.convertXYtoLatitude(p.x, p.y), this.m_MapView.m_TouchMoveStartX, this.m_MapView.m_TouchMoveStartY);
                                return true;
                            case 2:
                                LoadDifferentScaleMap(SMALLER_SCALE, this.m_Qct.convertXYtoLongitude(p.x, p.y), this.m_Qct.convertXYtoLatitude(p.x, p.y), this.m_MapView.m_TouchMoveStartX, this.m_MapView.m_TouchMoveStartY);
                                return true;
                            case 3:
                                CreateNewRoute(this.m_Qct.convertXYtoLongitude(p.x, p.y), this.m_Qct.convertXYtoLatitude(p.x, p.y));
                                return true;
                            case 4:
                                this.m_ActiveContextMenu = CONTEXT_MENU_ACTIONS_SUBMENU_WP;
                                return true;
                            default:
                                break;
                        }
                    } else {
                        return true;
                    }
                }
                switch (item.getItemId()) {
                    case 0:
                        ShowTrackStyleSettings();
                        if (this.m_MapView != null) {
                            this.m_MapView.invalidateMapScreen(true);
                        }
                        return true;
                    case 1:
                        ShowTrackDataActivity(m_SelectedTrack);
                        return true;
                    case 2:
                        m_SelectedTrack.m_bVisible = false;
                        if (!m_SelectedTrack.m_bActive && m_SelectedTrack.m_CreatedByMMTracker) {
                            m_SelectedTrack.ReWriteGpx(getBaseContext(), "");
                        }
                        if (this.m_MapView != null) {
                            this.m_MapView.invalidateMapScreen(true);
                        }
                        return true;
                    case 3:
                        DeleteTrack(m_SelectedTrack);
                        return true;
                    case 4:
                        DeleteAllTracks();
                        if (this.m_MapView != null) {
                            this.m_MapView.invalidateMapScreen(true);
                        }
                        return true;
                    case 5:
                        return true;
                    case 6:
                        if (DEBUG_GPS_SIMULATION) {
                            if (this.m_GpsSimulationTrack == null) {
                                this.m_LocationManager.removeUpdates(this);
                                this.m_GpsSimulationTrack = m_SelectedTrack;
                                this.m_iGpsSimulationIndex = 0;
                                this.m_lGpsSimulationTime = ((Trackpoint) this.m_GpsSimulationTrack.trackpoints.get(0)).m_lTime;
                            } else {
                                this.m_GpsSimulationTrack = null;
                                this.m_LocationManager.requestLocationUpdates("gps", 0, 0.0f, this);
                            }
                        }
                        return true;
                    default:
                        break;
                }
            }
            switch (item.getItemId()) {
                case 0:
                    ShowWaypointStyleSettings();
                    if (this.m_MapView != null) {
                        this.m_MapView.invalidateMapScreen(true);
                    }
                    return true;
                case 1:
                    m_SelectedWaypoint.m_bVisible = false;
                    if (m_SelectedWaypoint.m_CreatedByMMTracker) {
                        m_SelectedWaypoint.WriteGpx(m_SelectedWaypoint.m_sFileName);
                    }
                    if (this.m_MapView != null) {
                        this.m_MapView.invalidateMapScreen(true);
                    }
                    return true;
                case 2:
                    DeleteWaypoint(m_SelectedWaypoint);
                    return true;
                case 3:
                    if (m_NavigationTarget.m_Type == NavigationTarget.TARGET_TYPE_NONE) {
                        NavigateToWaypoint(m_SelectedWaypoint);
                    } else if (m_SelectedWaypoint == m_NavigationTarget.m_Waypoint) {
                        m_NavigationTarget.m_Type = NavigationTarget.TARGET_TYPE_NONE;
                    } else {
                        NavigateToWaypoint(m_SelectedWaypoint);
                    }
                    if (this.m_MapView != null) {
                        this.m_MapView.invalidateMapScreen(false);
                    }
                    return true;
                default:
                    break;
            }
        }
        switch (item.getItemId()) {
            case 0:
                ShowRouteStyleSettings();
                if (this.m_MapView != null) {
                    this.m_MapView.invalidateMapScreen(true);
                }
                return true;
            case 1:
                ShowRouteDataActivity(m_SelectedRoutepoint.m_ParentRoute);
                return true;
            case 2:
                DeleteRoute(m_SelectedRoutepoint.m_ParentRoute);
                return true;
            case 3:
                InsertAddRoutePoint(m_SelectedRoutepoint);
                if (this.m_MapView != null) {
                    this.m_MapView.invalidateMapScreen(false);
                }
                if (m_CurrentlyCreatedRoute == null) {
                    m_SelectedRoutepoint.m_ParentRoute.WriteGPX(m_SelectedRoutepoint.m_ParentRoute.m_sFileName);
                }
                return true;
            case 4:
                RemoveRoutePoint(m_SelectedRoutepoint);
                if (this.m_MapView != null) {
                    this.m_MapView.invalidateMapScreen(false);
                }
                if (m_CurrentlyCreatedRoute == null) {
                    m_SelectedRoutepoint.m_ParentRoute.WriteGPX(m_SelectedRoutepoint.m_ParentRoute.m_sFileName);
                }
                return true;
            case 5:
                if (m_NavigationTarget.m_Type == NavigationTarget.TARGET_TYPE_NONE) {
                    NavigateToRoutepoint(m_SelectedRoutepoint);
                } else if (m_SelectedRoutepoint == m_NavigationTarget.m_Routepoint) {
                    m_NavigationTarget.m_Type = NavigationTarget.TARGET_TYPE_NONE;
                } else {
                    NavigateToRoutepoint(m_SelectedRoutepoint);
                }
                if (this.m_MapView != null) {
                    this.m_MapView.invalidateMapScreen(false);
                }
                return true;
            case 6:
                m_SelectedRoutepoint.m_ParentRoute.m_bVisible = false;
                if (m_SelectedRoutepoint.m_ParentRoute.m_CreatedByMMTracker) {
                    m_SelectedRoutepoint.m_ParentRoute.WriteGPX(m_SelectedRoutepoint.m_ParentRoute.m_sFileName);
                }
                this.m_MapView.invalidateMapScreen(true);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    public void RemoveRoutePoint(Routepoint rp) {
        if (rp != null && rp.m_ParentRoute.routepoints.size() >= 2) {
            int iIndex = rp.m_ParentRoute.routepoints.indexOf(rp);
            if (iIndex >= 0 && iIndex < rp.m_ParentRoute.routepoints.size() && iIndex < rp.m_ParentRoute.chachepoints.size()) {
                rp.m_ParentRoute.routepoints.remove(iIndex);
                rp.m_ParentRoute.chachepoints.remove(iIndex);
                rp.m_ParentRoute.m_bCacheVaild = false;
            }
        }
    }

    public void InsertAddRoutePoint(Routepoint rp) {
        if (rp != null && rp.m_ParentRoute.routepoints.size() >= 2) {
            double dNewLat;
            double dNewLong;
            int iIndex = rp.m_ParentRoute.routepoints.indexOf(rp);
            if (rp != rp.m_ParentRoute.routepoints.get(rp.m_ParentRoute.routepoints.size() - 1)) {
                dNewLat = (rp.m_dGpsLat + ((Routepoint) rp.m_ParentRoute.routepoints.get(iIndex + 1)).m_dGpsLat) / 2.0d;
                dNewLong = (rp.m_dGpsLong + ((Routepoint) rp.m_ParentRoute.routepoints.get(iIndex + 1)).m_dGpsLong) / 2.0d;
            } else {
                dNewLat = (((rp.m_dGpsLat + ((Routepoint) rp.m_ParentRoute.routepoints.get(iIndex - 1)).m_dGpsLat) / 2.0d) - ((Routepoint) rp.m_ParentRoute.routepoints.get(iIndex - 1)).m_dGpsLat) + rp.m_dGpsLat;
                dNewLong = (((rp.m_dGpsLong + ((Routepoint) rp.m_ParentRoute.routepoints.get(iIndex - 1)).m_dGpsLong) / 2.0d) - ((Routepoint) rp.m_ParentRoute.routepoints.get(iIndex - 1)).m_dGpsLong) + rp.m_dGpsLong;
            }
            Routepoint new_rp = new Routepoint();
            new_rp.m_dGpsLat = dNewLat;
            new_rp.m_dGpsLong = dNewLong;
            new_rp.m_ParentRoute = rp.m_ParentRoute;
            if (this.m_Qct != null) {
                new_rp.RefreshXY(this.m_Qct);
            }
            rp.m_ParentRoute.routepoints.add(iIndex + 1, new_rp);
            rp.m_ParentRoute.m_bCacheVaild = false;
        }
    }

    public void CreateNewRoute(double dLon, double dLat) {
        Route r = new Route(m_SettingsRoutePath);
        if (r != null) {
            r.add(dLon, dLat, 0.0f, 0.0f, "");
            r.m_bCacheVaild = false;
            r.m_CreatedByMMTracker = true;
            if (this.m_Qct != null) {
                ((Routepoint) r.routepoints.get(0)).RefreshXY(this.m_Qct);
            }
            routes.add(r);
            m_CurrentlyCreatedRoute = r;
            m_SelectedRoutepoint = (Routepoint) r.routepoints.get(0);
            if (this.m_MapView != null) {
                this.m_MapView.invalidateMapScreen(true);
            }
        }
    }

    public void CreateNewWaypoint(double dLon, double dLat, boolean bAskForName, boolean bLocked) {
        Waypoint w = new Waypoint(dLon, dLat, Waypoint.WAYPOINT_COLOR_DEFAULT, "Circle");
        if (w != null) {
            if (this.m_Qct != null) {
                w.RefreshXY(this.m_Qct);
            }
            w.m_bCacheVaild = false;
            if (bAskForName) {
                Builder alert = new Builder(this);
                EditText input = new EditText(this);
                input.setText(w.m_sName);
                alert.setTitle(getString(C0047R.string.MmTrackerActivity_create_wp_name));
                alert.setView(input);
                alert.setPositiveButton("OK", new AnonymousClass16(input, w));
                alert.setNegativeButton(getString(C0047R.string.MmTrackerActivity_create_wp_cancel), new OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
                alert.show();
            } else {
                w.WriteGpx(m_SettingsWaypointPath + w.m_sName + ".gpx");
                if (this.m_Qct != null) {
                    w.RefreshXY(this.m_Qct);
                }
                waypoints.add(w);
                if (this.m_MapView != null) {
                    this.m_MapView.invalidateMapScreen(true);
                }
            }
            w.m_bLocked = bLocked;
        }
    }

    private void TimerMain_Method() {
        runOnUiThread(this.TimerMain_Tick);
    }

    private void TimerMapCache_Method() {
        runOnUiThread(this.TimerMapCache_Tick);
    }

    private void TimerGpsSim_Method() {
        runOnUiThread(this.TimerGpsSim_Tick);
    }

    static int getActiveTrackIndex() {
        if (tracks == null) {
            return -1;
        }
        Iterator it = tracks.iterator();
        while (it.hasNext()) {
            Track track = (Track) it.next();
            if (track.m_bActive) {
                return tracks.indexOf(track);
            }
        }
        return -1;
    }

    static boolean DeleteWaypointCore(Waypoint waypoint) {
        if (waypoint.m_sFileName != "" && waypoint.m_bFromFile) {
            new File(waypoint.m_sFileName).delete();
        }
        if (m_SelectedWaypoint == waypoint) {
            m_SelectedWaypoint = null;
        }
        waypoints.remove(waypoint);
        return true;
    }

    static boolean DeleteTrackCore(Track track) {
        if (track.m_sFileName != "") {
            new File(track.m_sFileName).delete();
        }
        if (m_SelectedTrack == track) {
            m_SelectedTrack = null;
        }
        tracks.remove(track);
        return true;
    }

    static boolean DeleteRouteCore(Route route) {
        if (route.m_sFileName != "") {
            new File(route.m_sFileName).delete();
        }
        if (m_SelectedRoutepoint != null && m_SelectedRoutepoint.m_ParentRoute == route) {
            m_SelectedRoutepoint = null;
        }
        routes.remove(route);
        return true;
    }

    private boolean DeleteTrack(Track track) {
        AlertDialog alertDialog = new Builder(this).create();
        alertDialog.setTitle("MM Tracker");
        alertDialog.setMessage(getString(C0047R.string.MmTrackerActivity_delete_tr_1) + track.m_sFileName + getString(C0047R.string.MmTrackerActivity_delete_tr_2) + track.m_sName + "'  ?");
        alertDialog.setButton(getString(C0047R.string.yes), new AnonymousClass18(track));
        alertDialog.setButton2(getString(C0047R.string.no), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
        return true;
    }

    boolean DeleteRoute(Route route) {
        AlertDialog alertDialog = new Builder(this).create();
        alertDialog.setTitle("MM Tracker");
        alertDialog.setMessage(getString(C0047R.string.MmTrackerActivity_delete_rt_1) + route.m_sFileName + getString(C0047R.string.MmTrackerActivity_delete_rt_2) + route.m_sName + "'  ?");
        alertDialog.setButton(getString(C0047R.string.yes), new AnonymousClass20(route));
        alertDialog.setButton2(getString(C0047R.string.no), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
        return true;
    }

    boolean DeleteWaypoint(Waypoint waypoint) {
        AlertDialog alertDialog = new Builder(this).create();
        alertDialog.setTitle("MM Tracker");
        if (waypoint.m_bFromFile) {
            alertDialog.setMessage(getString(C0047R.string.MmTrackerActivity_delete_wp_1) + waypoint.m_sFileName + getString(C0047R.string.MmTrackerActivity_delete_wp_2) + waypoint.m_sName + "'  ?");
        } else {
            alertDialog.setMessage(getString(C0047R.string.MmTrackerActivity_delete_wp_3) + waypoint.m_sName + "'  ?");
        }
        alertDialog.setButton(getString(C0047R.string.yes), new AnonymousClass22(waypoint));
        alertDialog.setButton2(getString(C0047R.string.no), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
        return true;
    }

    private boolean DeleteAllTracks() {
        AlertDialog alertDialog = new Builder(this).create();
        alertDialog.setTitle("MM Tracker");
        alertDialog.setMessage(getString(C0047R.string.MmTrackerActivity_delete_all_tr));
        alertDialog.setButton(getString(C0047R.string.yes), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                MMTrackerActivity.this.StopTrackingService();
                int i = 0;
                while (MMTrackerActivity.tracks.size() > 0) {
                    MMTrackerActivity.DeleteTrackCore((Track) MMTrackerActivity.tracks.get(i));
                    i = (i - 1) + 1;
                }
                if (MMTrackerActivity.m_bTrackRecording) {
                    MMTrackerActivity.this.StartTrackingService(false);
                }
                if (MMTrackerActivity.this.m_MapView != null) {
                    MMTrackerActivity.this.m_MapView.invalidateMapScreen(true);
                }
            }
        });
        alertDialog.setButton2(getString(C0047R.string.no), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
        return true;
    }

    private void ShowTrackDataActivity(Track track) {
        if (track != null) {
            Intent i = new Intent(this, TrackDataActivity.class);
            i.putExtra("index_unit", m_SettingsUnitsDistances);
            i.putExtra("reset_handles", true);
            m_TrackToDisplayData = track;
            startActivityForResult(i, ACTIVITY_TRACKDATA);
        }
    }

    private void ShowRouteDataActivity(Route route) {
        if (route != null) {
            Intent i = new Intent(this, RouteDataActivity.class);
            i.putExtra(MapCacheHelper.KEY_NAME, route.m_sName);
            i.putExtra("length", route.CalcLengthKm(null));
            i.putExtra("points", String.format("%d", new Object[]{Integer.valueOf(route.routepoints.size())}));
            i.putExtra("index_unit", m_SettingsUnitsDistances);
            startActivityForResult(i, 0);
        }
    }

    public static ArrayList<MapList> getMapList() {
        return maps;
    }

    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode != 84) {
            return super.onKeyLongPress(keyCode, event);
        }
        if (!this.m_SettingsUseLongClickSearchButton || !m_bTracksLoaded || this.m_MapView == null || this.m_Qct == null) {
            return true;
        }
        if (!this.m_bGpsFix && !this.m_bNetworkFix) {
            return true;
        }
        if (this.m_Qct.IsPixelInsideMap(this.m_dGpsLongitude, this.m_dGpsLatitude)) {
            this.m_MapView.SetGpsLock(true, false);
            return true;
        }
        String sNewMap = FindBestMapAtPosition(this.m_dGpsLongitude, this.m_dGpsLatitude);
        if (sNewMap == "") {
            return true;
        }
        OpenMap(sNewMap, this.m_dGpsLongitude, this.m_dGpsLatitude, (float) this.m_iGpsLockCenterX, (float) this.m_iGpsLockCenterY, NEW_POSITION_SET, true, this.m_iNewZoomLevel, false);
        return true;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() != 4) {
            if (this.m_SettingsUseVolButtons && this.m_MapView != null) {
                if (event.getKeyCode() == 24) {
                    if (event.getAction() == 0) {
                        this.m_MapView.getKeyDispatcherState().startTracking(event, this);
                        return true;
                    } else if (event.getAction() == 1) {
                        this.m_MapView.getKeyDispatcherState().handleUpEvent(event);
                        if (event.isTracking() && !event.isCanceled()) {
                            this.m_MapView.zoomPlus();
                            return true;
                        }
                    }
                }
                if (event.getKeyCode() == 25 && this.m_MapView != null) {
                    if (event.getAction() == 0) {
                        this.m_MapView.getKeyDispatcherState().startTracking(event, this);
                        return true;
                    } else if (event.getAction() == 1) {
                        this.m_MapView.getKeyDispatcherState().handleUpEvent(event);
                        if (event.isTracking() && !event.isCanceled()) {
                            this.m_MapView.zoomMinus();
                            return true;
                        }
                    }
                }
            }
            if (this.m_SettingsUseClickSearchButton && this.m_MapView != null && event.getKeyCode() == 84) {
                if (event.getAction() == 0 && event.getRepeatCount() == 0) {
                    this.m_MapView.getKeyDispatcherState().startTracking(event, this);
                    return true;
                } else if (event.getAction() == 1) {
                    this.m_MapView.getKeyDispatcherState().handleUpEvent(event);
                    if (event.isTracking() && !event.isCanceled()) {
                        if (m_SelectedTrack != null) {
                            ShowTrackDataActivity(m_SelectedTrack);
                            return true;
                        }
                        int iIndex = getActiveTrackIndex();
                        if (iIndex <= -1) {
                            return true;
                        }
                        ShowTrackDataActivity((Track) tracks.get(iIndex));
                        return true;
                    }
                }
            }
            if (this.m_SettingsUseClickEnterButton && this.m_MapView != null && event.getKeyCode() == 23) {
                if (event.getAction() == 0) {
                    this.m_MapView.getKeyDispatcherState().startTracking(event, this);
                    return true;
                } else if (event.getAction() == 1) {
                    this.m_MapView.getKeyDispatcherState().handleUpEvent(event);
                    if (event.isTracking() && !event.isCanceled()) {
                        if (!this.m_bGpsFix) {
                            return true;
                        }
                        CreateNewWaypoint(this.m_dGpsLongitude, this.m_dGpsLatitude, false, true);
                        return true;
                    }
                }
            }
            return super.dispatchKeyEvent(event);
        } else if (event.getAction() != 0 || event.getRepeatCount() != 0) {
            return event.getAction() == 1 ? true : true;
        } else {
            if (this.m_iBackButtonPressedTimer <= 0) {
                this.m_iBackButtonPressedTimer = Math.round((float) (WAIT_FOR_SECOND_BACKBUTTON / TIMER_MAIN_TICKS));
                Toast.makeText(getBaseContext(), getString(C0047R.string.MmTrackerActivity_toast_press_back_button), 0).show();
                return true;
            } else if (m_bTrackRecording && this.m_bSettingsAskBeforeStopTracking) {
                AlertDialog alertDialog = new Builder(this).create();
                alertDialog.setTitle("MM Tracker");
                alertDialog.setMessage(getString(C0047R.string.MmTrackerActivity_ask_for_end_tracking));
                alertDialog.setButton(getString(C0047R.string.yes), new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MMTrackerActivity.this.StopTrackingService();
                        MMTrackerActivity.this.finish();
                    }
                });
                alertDialog.setButton2(getString(C0047R.string.no), new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialog.show();
                return true;
            } else {
                StopTrackingService();
                finish();
                return true;
            }
        }
    }

    public boolean CheckMapPathesForConmformity() {
        if (!m_SettingsMapPath.equals("/") && !m_SettingsMapPath.toUpperCase().equals("/SDCARD/") && !m_SettingsMapPath.toUpperCase().equals("/REMOVABLE/")) {
            return true;
        }
        String sText = getString(C0047R.string.MmTrackerActivity_bad_map_dir) + "\n";
        AlertDialog alertDialog = new Builder(this).create();
        alertDialog.setTitle("MM Tracker");
        alertDialog.setMessage(sText);
        alertDialog.setButton(getString(C0047R.string.ok), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
        return false;
    }

    public void ComplainAboutPathes() {
        String sText = getString(C0047R.string.MmTrackerActivity_dir_not_set_hint) + "\n";
        if (m_SettingsMapPath == "") {
            sText = new StringBuilder(String.valueOf(sText)).append(getString(C0047R.string.MmTrackerActivity_dir_not_set_maps)).append("\n").toString();
        }
        if (m_SettingsTrackPath == "") {
            sText = new StringBuilder(String.valueOf(sText)).append(getString(C0047R.string.MmTrackerActivity_dir_not_set_tr)).append("\n").toString();
        }
        if (m_SettingsRoutePath == "") {
            sText = new StringBuilder(String.valueOf(sText)).append(getString(C0047R.string.MmTrackerActivity_dir_not_set_rt)).append("\n").toString();
        }
        if (m_SettingsWaypointPath == "") {
            sText = new StringBuilder(String.valueOf(sText)).append(getString(C0047R.string.MmTrackerActivity_dir_not_set_wp)).append("\n").toString();
        }
        sText = new StringBuilder(String.valueOf(sText)).append(getString(C0047R.string.MmTrackerActivity_dir_not_set_warning)).toString();
        AlertDialog alertDialog = new Builder(this).create();
        alertDialog.setTitle("MM Tracker");
        alertDialog.setMessage(sText);
        alertDialog.setButton(getString(C0047R.string.ok), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                MMTrackerActivity.this.ShowSettings();
            }
        });
        alertDialog.show();
    }

    public void ShowWhatsnewDialog() {
        String sVersion;
        try {
            sVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            sVersion = "";
        }
        String sText = "Changes in V" + sVersion + "\n\n" + WHATSNEW_STRING;
        AlertDialog alertDialog = new Builder(this).create();
        alertDialog.setTitle("MM Tracker");
        alertDialog.setMessage(sText);
        alertDialog.setButton(getString(C0047R.string.ok), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    public void ShowDisclaimerDialog() {
        String sText = getString(C0047R.string.MmTrackerActivity_disclaimer);
        AlertDialog alertDialog = new Builder(this).create();
        alertDialog.setTitle("MM Tracker");
        alertDialog.setMessage(sText);
        alertDialog.setButton(getString(C0047R.string.ok), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                MMTrackerActivity.this.m_bShowDisclaimer = false;
            }
        });
        alertDialog.show();
    }

    public void CheckTrackPath() {
        if (!new File(m_SettingsTrackPath).canWrite()) {
            AlertDialog alertDialog = new Builder(this).create();
            alertDialog.setTitle("MM Tracker");
            alertDialog.setMessage(getString(C0047R.string.MmTrackerActivity_track_dir_not_set_warning));
            alertDialog.setButton(getString(C0047R.string.ok), new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    MMTrackerActivity.this.ShowSettings();
                }
            });
            alertDialog.show();
        }
    }

    static void SetSelectedTrack(Track t) {
        m_SelectedTrack = t;
        m_SelectedWaypoint = null;
        m_SelectedRoutepoint = null;
    }

    static void SetSelectedRoutepoint(Routepoint r) {
        m_SelectedWaypoint = null;
        m_SelectedTrack = null;
        m_SelectedRoutepoint = r;
    }

    static void SetSelectedWaypoint(Waypoint w) {
        m_SelectedWaypoint = w;
        m_SelectedTrack = null;
        m_SelectedRoutepoint = null;
    }

    static Routepoint GetSelectedRoutepoint() {
        return m_SelectedRoutepoint;
    }

    static Track GetSelectedTrack() {
        return m_SelectedTrack;
    }

    static Waypoint GetSelectedWaypoint() {
        return m_SelectedWaypoint;
    }
}
