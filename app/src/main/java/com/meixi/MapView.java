package com.meixi;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Location;
import android.os.Handler;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.meixi.Tools.StringCoord;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class MapView extends View {
    public static int BUTTON_END_NAVIGATION;
    public static int BUTTON_ROUTE_CANCEL;
    public static int BUTTON_ROUTE_FINISH;
    public static int BUTTON_ROUTE_REMOVE;
    public static int BUTTON_SCALE_LARGE;
    public static int BUTTON_SCALE_SMALL;
    public static int BUTTON_ZOOM_MINUS;
    public static int BUTTON_ZOOM_PLUS;
    public static int MAP_COORDINATES;
    public static int MAX_ZOOM_LEVELS;
    public static int PATH_ARROW_LENGTH;
    public static int SCREEN_COORDINATES;
    public static int TOUCH_MODE_DRAG;
    public static int TOUCH_MODE_NONE;
    public static int TOUCH_MODE_ZOOM;
    public static int TOUCH_MOVE_MAP;
    public static int TOUCH_MOVE_POSITION;
    public static int TOUCH_MOVE_ROUTEPOINT;
    public static int TOUCH_MOVE_WAYPOINT;
    public static int ZOOM_LEVEL_SCALE_1;
    public static double[][] m_dUnitsFactor;
    public static double[] m_dUnitsFactorAngle;
    public static double[] m_dZoomFactor;
    public static int[] m_sUnitsAngleLength;
    public static String[] m_sUnitsAngleName;
    public static String[][] m_sUnitsName;
    Point m_ArrowTargetPoint;
    Context m_Context;
    boolean m_DisplayLongLat;
    boolean m_DisplayRpData;
    boolean m_DisplayWpData;
    final Handler m_Handler;
    OsgbCoord m_OsgbTemp;
    MMTrackerActivity m_Parent;
    QuickChartFile m_Qct;
    Rect m_RectButtonEndNavigation;
    Rect m_RectButtonMapCoarse;
    Rect m_RectButtonMapFine;
    Rect m_RectButtonRtCancel;
    Rect m_RectButtonRtFinish;
    Rect m_RectButtonRtRemove;
    Rect m_RectButtonZoomMinus;
    Rect m_RectButtonZoomPlus;
    Routepoint m_SelectedTouchMoveRoutepoint;
    Waypoint m_SelectedTouchMoveWaypoint;
    int m_TouchMoveDispPosStartX;
    int m_TouchMoveDispPosStartY;
    float m_TouchMoveStartX;
    float m_TouchMoveStartY;
    long m_TouchTimerStart;
    final Runnable m_UpdateEndNavigation;
    final Runnable m_UpdateRouteCancel;
    final Runnable m_UpdateRouteFinish;
    final Runnable m_UpdateRouteRemove;
    final Runnable m_UpdateScaleLarge;
    final Runnable m_UpdateScaleSmall;
    final Runnable m_UpdateZoomMinus;
    final Runnable m_UpdateZoomPlus;
    boolean m_bAdaptPositionToZoomLevel;
    boolean m_bDrawAll;
    boolean m_bFirstTime;
    boolean m_bInitDone;
    boolean m_bOnDrawRunning;
    boolean m_bTouchHold;
    boolean m_bTouchMoveActive;
    boolean m_bTouchMoveTargetMoved;
    boolean m_bUseOldCenterValues;
    Bitmap m_bmButtonEndNavigationNormal;
    Bitmap m_bmButtonEndNavigationPressed;
    Bitmap m_bmButtonRtCancelNormal;
    Bitmap m_bmButtonRtCancelPressed;
    Bitmap m_bmButtonRtFinishNormal;
    Bitmap m_bmButtonRtFinishPressed;
    Bitmap m_bmButtonRtRemoveNormal;
    Bitmap m_bmButtonRtRemovePressed;
    Bitmap m_bmCrosshairActive;
    Bitmap m_bmCrosshairNetwork;
    Bitmap m_bmMapCoarse;
    Bitmap m_bmMapCoarseActive;
    Bitmap m_bmMapFine;
    Bitmap m_bmMapFineActive;
    Bitmap m_bmMiniGpsLocked;
    Bitmap m_bmMiniGpsUnlocked;
    Bitmap m_bmMiniScreenLocked;
    Bitmap m_bmMiniScreenUnlocked;
    Bitmap m_bmMiniTrackRunning;
    Bitmap m_bmMiniTrackStopped;
    Bitmap[] m_bmWpIcon;
    Bitmap m_bmWpIconBaseActive;
    Bitmap m_bmWpIconBasePassive;
    Bitmap m_bmZoomMinus;
    Bitmap m_bmZoomMinusActive;
    Bitmap m_bmZoomPlus;
    Bitmap m_bmZoomPlusActive;
    double m_dArrowAngle;
    double m_dArrowLength_km_s;
    double m_dPinchMidPointX;
    double m_dPinchMidPointY;
    double m_dPinchOldZoomFactor;
    double m_dPinchScale;
    double m_dPinchStartDistance;
    double m_dTouchMoveStartLat;
    double m_dTouchMoveStartLon;
    float m_fCompassRadius;
    float m_fTrueNorth;
    int m_iCurrentActiveButton;
    int m_iCurrentTouchMove;
    int m_iDecimate;
    int m_iDispPosOldX;
    int m_iDispPosOldY;
    int m_iDispSizeX;
    int m_iDispSizeY;
    int m_iGpsX;
    int m_iGpsY;
    int m_iTileSize;
    int m_iTouchMode;
    int m_iVirtualDispSizeX;
    int m_iVirtualDispSizeY;
    int m_iWindowLastYPos;
    int m_iZoomCenterX;
    int m_iZoomCenterY;
    Path m_pathCompassArrow;
    Path m_pathLetterN;
    Path m_pathLine;
    Path m_pathNaviArrow;
    Path m_pathWaypointArrow;
    Canvas m_vbnCanvas;
    Canvas m_vboCanvas;

    /* renamed from: com.meixi.MapView.1 */
    class C00081 implements Runnable {
        C00081() {
        }

        public void run() {
            MapView.this.zoomPlus();
        }
    }

    /* renamed from: com.meixi.MapView.2 */
    class C00092 implements Runnable {
        C00092() {
        }

        public void run() {
            MapView.this.zoomMinus();
        }
    }

    /* renamed from: com.meixi.MapView.3 */
    class C00103 implements Runnable {
        C00103() {
        }

        public void run() {
            MapView.this.scaleSmall();
        }
    }

    /* renamed from: com.meixi.MapView.4 */
    class C00114 implements Runnable {
        C00114() {
        }

        public void run() {
            MapView.this.scaleLarge();
        }
    }

    /* renamed from: com.meixi.MapView.5 */
    class C00125 implements Runnable {
        C00125() {
        }

        public void run() {
            MapView.this.buttonRouteCancel();
        }
    }

    /* renamed from: com.meixi.MapView.6 */
    class C00136 implements Runnable {
        C00136() {
        }

        public void run() {
            MapView.this.buttonRouteFinish();
        }
    }

    /* renamed from: com.meixi.MapView.7 */
    class C00147 implements Runnable {
        C00147() {
        }

        public void run() {
            MapView.this.buttonRouteRemove();
        }
    }

    /* renamed from: com.meixi.MapView.8 */
    class C00158 implements Runnable {
        C00158() {
        }

        public void run() {
            MapView.this.buttonEndNavigation();
        }
    }

    /* renamed from: com.meixi.MapView.9 */
    class C00169 implements OnClickListener {
        C00169() {
        }

        public void onClick(DialogInterface dialog, int which) {
            MMTrackerActivity.routes.remove(MMTrackerActivity.m_CurrentlyCreatedRoute);
            MMTrackerActivity.m_CurrentlyCreatedRoute = null;
        }
    }

    static {
        PATH_ARROW_LENGTH = 11;
        TOUCH_MOVE_MAP = 1;
        TOUCH_MOVE_WAYPOINT = 2;
        TOUCH_MOVE_ROUTEPOINT = 3;
        TOUCH_MOVE_POSITION = 4;
        BUTTON_ZOOM_PLUS = 1;
        BUTTON_ZOOM_MINUS = 2;
        BUTTON_SCALE_SMALL = 3;
        BUTTON_SCALE_LARGE = 4;
        BUTTON_ROUTE_FINISH = 5;
        BUTTON_ROUTE_CANCEL = 6;
        BUTTON_ROUTE_REMOVE = 7;
        BUTTON_END_NAVIGATION = 8;
        TOUCH_MODE_NONE = 0;
        TOUCH_MODE_DRAG = 1;
        TOUCH_MODE_ZOOM = 2;
        MAX_ZOOM_LEVELS = 11;
        ZOOM_LEVEL_SCALE_1 = 3;
        MAP_COORDINATES = 1;
        SCREEN_COORDINATES = 2;
        m_dZoomFactor = new double[]{0.125d, 0.25d, 0.5d, 1.0d, 1.5d, 2.0d, 2.5d, 3.0d, 3.5d, 4.0d, 4.5d};
        r0 = new String[4][];
        r0[0] = new String[]{"cm", "cm", "m", "m", "m", "km", "km", "km", "km"};
        r0[1] = new String[]{"mi", "mi", "mi", "mi", "mi", "mi", "mi", "mi", "mi"};
        r0[2] = new String[]{"mi", "mi", "mi", "mi", "mi", "mi", "mi", "mi", "mi"};
        r0[3] = new String[]{"nmi", "nmi", "nmi", "nmi", "nmi", "nmi", "nmi", "nmi", "nmi"};
        m_sUnitsName = r0;
        m_dUnitsFactor = new double[][]{new double[]{1.0d, 10.0d, 1.0d, 10.0d, 100.0d, 1.0d, 10.0d, 100.0d, 1000.0d}, new double[]{1.0E-5d, 1.0E-4d, 0.001d, 0.01d, 0.1d, 1.0d, 10.0d, 100.0d, 1000.0d}, new double[]{1.0E-5d, 1.0E-4d, 0.001d, 0.01d, 0.1d, 1.0d, 10.0d, 100.0d, 1000.0d}, new double[]{1.0E-5d, 1.0E-4d, 0.001d, 0.01d, 0.1d, 1.0d, 10.0d, 100.0d, 1000.0d}};
        m_sUnitsAngleName = new String[]{"\u00b0", ""};
        m_dUnitsFactorAngle = new double[]{1.0d, 17.777777777d};
        m_sUnitsAngleLength = new int[]{3, 4};
    }

    public MapView(Context context, QuickChartFile f, boolean bAdaptPositionToZoomLevel, boolean bUseOldCenterValues) {
        super(context);
        this.m_TouchMoveStartX = 25.0f;
        this.m_TouchMoveStartY = 25.0f;
        this.m_SelectedTouchMoveWaypoint = null;
        this.m_SelectedTouchMoveRoutepoint = null;
        this.m_DisplayLongLat = false;
        this.m_DisplayRpData = false;
        this.m_DisplayWpData = false;
        this.m_bDrawAll = true;
        this.m_iDecimate = 1;
        this.m_ArrowTargetPoint = new Point();
        this.m_bTouchHold = false;
        this.m_pathNaviArrow = new Path();
        this.m_pathWaypointArrow = new Path();
        this.m_pathCompassArrow = new Path();
        this.m_pathLetterN = new Path();
        this.m_pathLine = new Path();
        this.m_bInitDone = false;
        this.m_Handler = new Handler();
        this.m_UpdateZoomPlus = new C00081();
        this.m_UpdateZoomMinus = new C00092();
        this.m_UpdateScaleSmall = new C00103();
        this.m_UpdateScaleLarge = new C00114();
        this.m_UpdateRouteCancel = new C00125();
        this.m_UpdateRouteFinish = new C00136();
        this.m_UpdateRouteRemove = new C00147();
        this.m_UpdateEndNavigation = new C00158();
        this.m_Qct = f;
        this.m_Context = context;
        this.m_Parent = (MMTrackerActivity) context;
        this.m_bFirstTime = true;
        this.m_bUseOldCenterValues = bUseOldCenterValues;
        this.m_bAdaptPositionToZoomLevel = bAdaptPositionToZoomLevel;
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    private void InitAll(boolean bFirstTime) {
        int iLargeSide;
        this.m_bInitDone = false;
        Log.d("MM_TRACKER", "Start InitAll()");
        this.m_iDispSizeX = getWidth();
        this.m_iDispSizeY = getHeight();
        this.m_iZoomCenterX = this.m_Parent.m_iGpsLockCenterX;
        this.m_iZoomCenterY = this.m_Parent.m_iGpsLockCenterY;
        if (this.m_iDispSizeY > this.m_iDispSizeX) {
            iLargeSide = this.m_iDispSizeY;
        } else {
            iLargeSide = this.m_iDispSizeX;
        }
        double dCompassRelevantSize = (double) iLargeSide;
        this.m_fCompassRadius = (float) (0.3333333333333333d * ((double) this.m_Parent.m_iDisplayDensity));
        if (((double) this.m_fCompassRadius) / dCompassRelevantSize > 0.07d) {
            this.m_fCompassRadius = (float) (0.07d * dCompassRelevantSize);
        }
        Log.d("MM_TRACKER", "Set Zoom Level");
        SetZoomLevel(this.m_Parent.m_iZoomLevel);
        this.m_OsgbTemp = new OsgbCoord();
        this.m_OsgbTemp.setPrecision(5);
        Log.d("MM_TRACKER", "Calc Midpoint of Map");
        if (this.m_Parent.m_iDispPosX == MMTrackerActivity.MAP_POSITION_INVALID || this.m_Parent.m_iDispPosY == MMTrackerActivity.MAP_POSITION_INVALID) {
            this.m_Parent.m_iDispPosX = ((this.m_Qct.getWidthTiles() * this.m_iTileSize) / 2) - (this.m_iDispSizeX / 2);
            this.m_Parent.m_iDispPosY = ((this.m_Qct.getHeightTiles() * this.m_iTileSize) / 2) - (this.m_iDispSizeY / 2);
        }
        Log.d("MM_TRACKER", "Load bitmaps for Buttons and icons");
        if (bFirstTime) {
            this.m_bmWpIcon = new Bitmap[2];
            this.m_bmWpIcon[0] = BitmapFactory.decodeResource(getResources(), C0047R.drawable.wp_icon_traditional_cache);
            this.m_bmWpIcon[1] = BitmapFactory.decodeResource(getResources(), C0047R.drawable.wp_icon_multi_cache);
            this.m_bmWpIconBasePassive = BitmapFactory.decodeResource(getResources(), C0047R.drawable.wp_icon_base);
            this.m_bmWpIconBaseActive = BitmapFactory.decodeResource(getResources(), C0047R.drawable.wp_icon_base_active);
            this.m_bmZoomPlus = BitmapFactory.decodeResource(getResources(), C0047R.drawable.zoom_plus);
            this.m_bmZoomPlusActive = BitmapFactory.decodeResource(getResources(), C0047R.drawable.zoom_plus_active);
            this.m_bmZoomMinus = BitmapFactory.decodeResource(getResources(), C0047R.drawable.zoom_minus);
            this.m_bmZoomMinusActive = BitmapFactory.decodeResource(getResources(), C0047R.drawable.zoom_minus_active);
            this.m_bmMapFine = BitmapFactory.decodeResource(getResources(), C0047R.drawable.map_change_fine);
            this.m_bmMapFineActive = BitmapFactory.decodeResource(getResources(), C0047R.drawable.map_change_fine_active);
            this.m_bmMapCoarse = BitmapFactory.decodeResource(getResources(), C0047R.drawable.map_change_coarse);
            this.m_bmMapCoarseActive = BitmapFactory.decodeResource(getResources(), C0047R.drawable.map_change_coarse_active);
            this.m_bmMiniGpsLocked = BitmapFactory.decodeResource(getResources(), C0047R.drawable.menu_icon_gps_locked_mini);
            this.m_bmMiniGpsUnlocked = BitmapFactory.decodeResource(getResources(), C0047R.drawable.menu_icon_gps_unlocked_mini);
            this.m_bmMiniScreenLocked = BitmapFactory.decodeResource(getResources(), C0047R.drawable.menu_icon_screen_locked_mini);
            this.m_bmMiniScreenUnlocked = BitmapFactory.decodeResource(getResources(), C0047R.drawable.menu_icon_screen_unlocked_mini);
            this.m_bmMiniTrackRunning = BitmapFactory.decodeResource(getResources(), C0047R.drawable.menu_icon_track_start_mini);
            this.m_bmMiniTrackStopped = BitmapFactory.decodeResource(getResources(), C0047R.drawable.menu_icon_track_stop_mini);
            this.m_bmCrosshairActive = BitmapFactory.decodeResource(getResources(), C0047R.drawable.crosshair_active);
            this.m_bmCrosshairNetwork = BitmapFactory.decodeResource(getResources(), C0047R.drawable.crosshair_network);
            this.m_bmButtonRtCancelNormal = BitmapFactory.decodeResource(getResources(), C0047R.drawable.button_route_cancel);
            this.m_bmButtonRtCancelPressed = BitmapFactory.decodeResource(getResources(), C0047R.drawable.button_route_cancel_pressed);
            this.m_bmButtonRtFinishNormal = BitmapFactory.decodeResource(getResources(), C0047R.drawable.button_route_finished);
            this.m_bmButtonRtFinishPressed = BitmapFactory.decodeResource(getResources(), C0047R.drawable.button_route_finished_pressed);
            this.m_bmButtonRtRemoveNormal = BitmapFactory.decodeResource(getResources(), C0047R.drawable.button_route_delete);
            this.m_bmButtonRtRemovePressed = BitmapFactory.decodeResource(getResources(), C0047R.drawable.button_route_delete_pressed);
            this.m_bmButtonEndNavigationNormal = BitmapFactory.decodeResource(getResources(), C0047R.drawable.button_route_cancel);
            this.m_bmButtonEndNavigationPressed = BitmapFactory.decodeResource(getResources(), C0047R.drawable.button_route_cancel_pressed);
        }
        Log.d("MM_TRACKER", "Setup positions for Buttons and icons");
        int left = (this.m_bmZoomPlus.getWidth() / 3) + this.m_bmZoomPlus.getWidth();
        this.m_RectButtonZoomPlus = new Rect(this.m_iDispSizeX - left, (this.m_iDispSizeY / 16) * 7, (this.m_iDispSizeX - left) + this.m_bmZoomPlus.getWidth(), ((this.m_iDispSizeY / 16) * 7) + this.m_bmZoomPlus.getHeight());
        left = (this.m_bmZoomMinus.getWidth() / 3) + this.m_bmZoomMinus.getWidth();
        this.m_RectButtonZoomMinus = new Rect(this.m_iDispSizeX - left, (int) Math.round(((double) ((this.m_iDispSizeY / 16) * 7)) + (((double) this.m_bmZoomMinus.getHeight()) * 1.8d)), (this.m_iDispSizeX - left) + this.m_bmZoomMinus.getWidth(), (int) Math.round(((double) ((this.m_iDispSizeY / 16) * 7)) + (((double) this.m_bmZoomMinus.getHeight()) * 2.8d)));
        left = this.m_bmMapFine.getWidth() / 3;
        this.m_RectButtonMapFine = new Rect(left, (this.m_iDispSizeY / 16) * 7, this.m_bmMapFine.getWidth() + left, ((this.m_iDispSizeY / 16) * 7) + this.m_bmMapFine.getHeight());
        left = this.m_bmMapCoarse.getWidth() / 3;
        this.m_RectButtonMapCoarse = new Rect(left, (int) Math.round(((double) ((this.m_iDispSizeY / 16) * 7)) + (((double) this.m_bmMapCoarse.getHeight()) * 1.8d)), this.m_bmMapCoarse.getWidth() + left, (int) Math.round(((double) ((this.m_iDispSizeY / 16) * 7)) + (((double) this.m_bmMapCoarse.getHeight()) * 2.8d)));
        this.m_RectButtonRtFinish = new Rect(0, 0, this.m_bmButtonRtFinishNormal.getWidth(), this.m_bmButtonRtFinishNormal.getHeight());
        this.m_RectButtonRtFinish.offsetTo(this.m_bmButtonRtFinishNormal.getHeight() / 10, this.m_bmButtonRtFinishNormal.getHeight() / 10);
        this.m_RectButtonRtCancel = new Rect(0, 0, this.m_bmButtonRtCancelNormal.getWidth(), this.m_bmButtonRtCancelNormal.getHeight());
        this.m_RectButtonRtCancel.offsetTo((this.m_iDispSizeX - (this.m_bmButtonRtFinishNormal.getHeight() / 10)) - this.m_bmButtonRtFinishNormal.getWidth(), this.m_bmButtonRtFinishNormal.getHeight() / 10);
        this.m_RectButtonRtRemove = new Rect(0, 0, this.m_bmButtonRtRemoveNormal.getWidth(), this.m_bmButtonRtRemoveNormal.getHeight());
        this.m_RectButtonRtRemove.offsetTo((this.m_iDispSizeX / 2) - (this.m_bmButtonRtFinishNormal.getWidth() / 2), this.m_bmButtonRtFinishNormal.getHeight() / 10);
        this.m_RectButtonEndNavigation = new Rect(0, 0, this.m_bmButtonEndNavigationNormal.getWidth(), this.m_bmButtonEndNavigationNormal.getHeight());
        this.m_RectButtonEndNavigation.offsetTo(-100, -100);
        Log.d("MM_TRACKER", "Define Pathes");
        DefineArrowPath();
        DefineWaypointArrow();
        DefineCompassArrow();
        DefineLetterN();
        DefineLine();
        Log.d("MM_TRACKER", "Set Current Position");
        SetCurrentPosition(this.m_Parent.m_dGpsLongitude, this.m_Parent.m_dGpsLatitude, this.m_Parent.m_PositionLock, false);
        if (this.m_Parent.m_PositionLock) {
            Log.d("MM_TRACKER", "Set GPS Lock");
            SetGpsLock(false, this.m_bUseOldCenterValues);
        }
        Log.d("MM_TRACKER", "End InitAll()");
        this.m_bInitDone = true;
    }

    private void DefineArrowPath() {
        this.m_pathNaviArrow.reset();
        this.m_pathNaviArrow.moveTo(-15.0f, 40.0f);
        this.m_pathNaviArrow.lineTo(15.0f, 40.0f);
        this.m_pathNaviArrow.lineTo(15.0f, 0.0f);
        this.m_pathNaviArrow.lineTo(25.0f, 0.0f);
        this.m_pathNaviArrow.lineTo(0.0f, -40.0f);
        this.m_pathNaviArrow.lineTo(-25.0f, 0.0f);
        this.m_pathNaviArrow.lineTo(-15.0f, 0.0f);
        this.m_pathNaviArrow.lineTo(-15.0f, 40.0f);
    }

    private void DefineWaypointArrow() {
        this.m_pathWaypointArrow.reset();
        this.m_pathWaypointArrow.moveTo(0.0f, 0.0f);
        this.m_pathWaypointArrow.lineTo((float) (-PATH_ARROW_LENGTH), 4.0f);
        this.m_pathWaypointArrow.lineTo((float) (-PATH_ARROW_LENGTH), -4.0f);
        this.m_pathWaypointArrow.lineTo(0.0f, 0.0f);
    }

    private void DefineCompassArrow() {
        this.m_pathCompassArrow.reset();
        this.m_pathCompassArrow.moveTo(0.0f, -30.0f);
        this.m_pathCompassArrow.lineTo(10.0f, 30.0f);
        this.m_pathCompassArrow.lineTo(0.0f, 25.0f);
        this.m_pathCompassArrow.lineTo(-10.0f, 30.0f);
        this.m_pathCompassArrow.lineTo(0.0f, -30.0f);
    }

    private void DefineLetterN() {
        this.m_pathLetterN.reset();
        this.m_pathLetterN.moveTo(-3.0f, 5.0f);
        this.m_pathLetterN.lineTo(-3.0f, -5.0f);
        this.m_pathLetterN.lineTo(3.0f, 5.0f);
        this.m_pathLetterN.lineTo(3.0f, -5.0f);
    }

    private void DefineLine() {
        this.m_pathLine.reset();
        this.m_pathLine.moveTo(0.0f, 5.0f);
        this.m_pathLine.lineTo(0.0f, -5.0f);
    }

    int FindZoomLevelFromScale(double dScale, boolean bLimit) {
        int iNewZoomLevel = ZOOM_LEVEL_SCALE_1;
        double dError = 9999999.0d;
        for (int i = 0; i < MAX_ZOOM_LEVELS; i++) {
            if (Math.abs(m_dZoomFactor[i] - dScale) < dError) {
                dError = Math.abs(m_dZoomFactor[i] - dScale);
                iNewZoomLevel = i;
            }
        }
        if (iNewZoomLevel >= ZOOM_LEVEL_SCALE_1 - 1 || !bLimit) {
            return iNewZoomLevel;
        }
        return ZOOM_LEVEL_SCALE_1 - 1;
    }

    private boolean SetZoomLevel(int iZoom) {
        if (iZoom >= MAX_ZOOM_LEVELS || iZoom < 0) {
            return false;
        }
        this.m_Parent.m_iZoomLevel = iZoom;
        if (m_dZoomFactor[iZoom] >= 1.0d) {
            this.m_iVirtualDispSizeX = (int) Math.round(((double) this.m_iDispSizeX) / m_dZoomFactor[iZoom]);
            this.m_iVirtualDispSizeY = (int) Math.round(((double) this.m_iDispSizeY) / m_dZoomFactor[iZoom]);
            this.m_iDecimate = 1;
            this.m_iTileSize = 64;
        } else {
            this.m_iVirtualDispSizeX = this.m_iDispSizeX;
            this.m_iVirtualDispSizeY = this.m_iDispSizeY;
            this.m_iDecimate = (int) Math.round(1.0d / m_dZoomFactor[iZoom]);
            this.m_iTileSize = 64 / this.m_iDecimate;
        }
        return true;
    }

    private void touch_start(float x, float y) {
        this.m_TouchMoveStartX = x;
        this.m_TouchMoveStartY = y;
        this.m_TouchMoveDispPosStartX = this.m_Parent.m_iDispPosX;
        this.m_TouchMoveDispPosStartY = this.m_Parent.m_iDispPosY;
        this.m_bTouchMoveActive = false;
        Log.d("MM Tracker", "TouchHold = true (1)");
        this.m_bTouchHold = true;
        if (this.m_Qct != null) {
            Point pClick = convertScreenToMapCoordinates(this.m_TouchMoveStartX, this.m_TouchMoveStartY);
            this.m_dTouchMoveStartLat = this.m_Qct.convertXYtoLatitude(pClick.x, pClick.y);
            this.m_dTouchMoveStartLon = this.m_Qct.convertXYtoLongitude(pClick.x, pClick.y);
        }
        if (!(this.m_RectButtonZoomPlus == null || this.m_RectButtonZoomMinus == null || this.m_RectButtonMapCoarse == null || this.m_RectButtonMapFine == null || (!this.m_RectButtonZoomPlus.contains(Math.round(this.m_TouchMoveStartX), Math.round(this.m_TouchMoveStartY)) && !this.m_RectButtonZoomMinus.contains(Math.round(this.m_TouchMoveStartX), Math.round(this.m_TouchMoveStartY)) && !this.m_RectButtonMapCoarse.contains(Math.round(this.m_TouchMoveStartX), Math.round(this.m_TouchMoveStartY)) && !this.m_RectButtonMapFine.contains(Math.round(this.m_TouchMoveStartX), Math.round(this.m_TouchMoveStartY))))) {
            Log.d("MM Tracker", "TouchHold = false (2)");
            this.m_bTouchHold = false;
        }
        if (!(MMTrackerActivity.m_CurrentlyCreatedRoute == null || this.m_RectButtonRtFinish == null || this.m_RectButtonRtCancel == null || this.m_RectButtonRtRemove == null || (!this.m_RectButtonRtFinish.contains(Math.round(this.m_TouchMoveStartX), Math.round(this.m_TouchMoveStartY)) && !this.m_RectButtonRtCancel.contains(Math.round(this.m_TouchMoveStartX), Math.round(this.m_TouchMoveStartY)) && !this.m_RectButtonRtRemove.contains(Math.round(this.m_TouchMoveStartX), Math.round(this.m_TouchMoveStartY))))) {
            Log.d("MM Tracker", "TouchHold = false (3)");
            this.m_bTouchHold = false;
        }
        this.m_iCurrentTouchMove = TOUCH_MOVE_MAP;
        if (MMTrackerActivity.m_bTracksLoaded) {
            this.m_SelectedTouchMoveWaypoint = null;
            this.m_SelectedTouchMoveRoutepoint = null;
            this.m_SelectedTouchMoveWaypoint = this.m_Parent.GetNearestWaypoint(this.m_TouchMoveStartX, this.m_TouchMoveStartY);
            if (this.m_SelectedTouchMoveWaypoint != null) {
                if (this.m_SelectedTouchMoveWaypoint.m_bLocked) {
                    this.m_SelectedTouchMoveWaypoint = null;
                } else if (this.m_SelectedTouchMoveWaypoint.m_dDummyDistance >= ((double) MMTrackerActivity.DISTANCE_TO_TRACK_FOR_MENU) || MMTrackerActivity.m_SelectedWaypoint != this.m_SelectedTouchMoveWaypoint) {
                    this.m_SelectedTouchMoveWaypoint = null;
                } else {
                    this.m_iCurrentTouchMove = TOUCH_MOVE_WAYPOINT;
                    this.m_bTouchMoveTargetMoved = false;
                }
            }
            if (this.m_SelectedTouchMoveWaypoint == null) {
                this.m_SelectedTouchMoveRoutepoint = this.m_Parent.GetNearestRoutepoint(this.m_TouchMoveStartX, this.m_TouchMoveStartY);
                if (this.m_SelectedTouchMoveRoutepoint != null && MMTrackerActivity.m_SelectedRoutepoint != null) {
                    if (this.m_SelectedTouchMoveRoutepoint.m_ParentRoute.m_bLocked) {
                        this.m_SelectedTouchMoveRoutepoint = null;
                    } else if (this.m_SelectedTouchMoveRoutepoint.m_ParentRoute.m_dDummyDistance >= ((double) MMTrackerActivity.DISTANCE_TO_TRACK_FOR_MENU) || MMTrackerActivity.m_SelectedRoutepoint.m_ParentRoute != this.m_SelectedTouchMoveRoutepoint.m_ParentRoute) {
                        this.m_SelectedTouchMoveRoutepoint = null;
                    } else {
                        this.m_iCurrentTouchMove = TOUCH_MOVE_ROUTEPOINT;
                        this.m_bTouchMoveTargetMoved = false;
                    }
                }
            }
        }
    }

    private void touch_move(float x, float y) {
        if (Math.abs(x - this.m_TouchMoveStartX) > 18.0f || Math.abs(y - this.m_TouchMoveStartY) > 18.0f || this.m_bTouchMoveActive) {
            Point p;
            NavigationTarget n;
            this.m_bTouchMoveActive = true;
            Log.d("MM Tracker", "TouchHold = false (3)");
            this.m_bTouchHold = false;
            if (this.m_iCurrentTouchMove == TOUCH_MOVE_MAP) {
                if (MMTrackerActivity.m_iSettingsMapRotation != 0) {
                    DoublePoint dp = RotatePointNeg((double) this.m_TouchMoveStartX, (double) this.m_TouchMoveStartY, (double) x, (double) y);
                    this.m_Parent.m_iDispPosX = (int) Math.round(((double) ((float) this.m_TouchMoveDispPosStartX)) - (((double) x) - dp.f0x));
                    this.m_Parent.m_iDispPosY = (int) Math.round(((double) ((float) this.m_TouchMoveDispPosStartY)) - (((double) y) - dp.f1y));
                } else {
                    this.m_Parent.m_iDispPosX = Math.round(((float) this.m_TouchMoveDispPosStartX) - (x - this.m_TouchMoveStartX));
                    this.m_Parent.m_iDispPosY = Math.round(((float) this.m_TouchMoveDispPosStartY) - (y - this.m_TouchMoveStartY));
                }
            }
            if (this.m_iCurrentTouchMove == TOUCH_MOVE_WAYPOINT) {
                Waypoint w = this.m_SelectedTouchMoveWaypoint;
                if (w != null) {
                    p = convertScreenToMapCoordinates(x, y);
                    w.m_dGpsLong = this.m_Qct.convertXYtoLongitude(p.x, p.y);
                    w.m_dGpsLat = this.m_Qct.convertXYtoLatitude(p.x, p.y);
                    w.m_fX = (float) p.x;
                    w.m_fY = (float) p.y;
                    w.m_bCacheVaild = false;
                    this.m_bTouchMoveTargetMoved = true;
                    n = MMTrackerActivity.m_NavigationTarget;
                    if (n.m_Type == NavigationTarget.TARGET_TYPE_WAYPOINT) {
                        this.m_Parent.CalcBearingValues();
                        if (n.m_Waypoint == w) {
                            n.m_dGpsLat = w.m_dGpsLat;
                            n.m_dGpsLong = w.m_dGpsLong;
                        }
                    }
                }
            }
            if (this.m_iCurrentTouchMove == TOUCH_MOVE_ROUTEPOINT) {
                Routepoint r = this.m_SelectedTouchMoveRoutepoint;
                if (r != null) {
                    p = convertScreenToMapCoordinates(x, y);
                    r.m_dGpsLong = this.m_Qct.convertXYtoLongitude(p.x, p.y);
                    r.m_dGpsLat = this.m_Qct.convertXYtoLatitude(p.x, p.y);
                    r.m_fX = (float) p.x;
                    r.m_fY = (float) p.y;
                    r.m_ParentRoute.m_bCacheVaild = false;
                    this.m_bTouchMoveTargetMoved = true;
                    r.m_ParentRoute.UpdateAreal();
                    n = MMTrackerActivity.m_NavigationTarget;
                    if (n.m_Type == NavigationTarget.TARGET_TYPE_ROUTEPOINT) {
                        this.m_Parent.CalcBearingValues();
                        if (n.m_Routepoint == r) {
                            n.m_dGpsLat = r.m_dGpsLat;
                            n.m_dGpsLong = r.m_dGpsLong;
                        }
                    }
                }
            }
        }
    }

    private void touch_up() {
        Log.d("MM Tracker", "TouchHold = false (4)");
        this.m_bTouchHold = false;
        if (this.m_iCurrentTouchMove == TOUCH_MOVE_WAYPOINT && this.m_SelectedTouchMoveWaypoint != null && this.m_bTouchMoveTargetMoved) {
            if (this.m_SelectedTouchMoveWaypoint.m_CreatedByMMTracker) {
                this.m_SelectedTouchMoveWaypoint.WriteGpx(this.m_SelectedTouchMoveWaypoint.m_sFileName);
            } else {
                Toast.makeText(this.m_Context, this.m_Parent.getText(C0047R.string.MapView_toast_wp_not_stored), 0).show();
            }
        }
        if (MMTrackerActivity.m_CurrentlyCreatedRoute == null && this.m_iCurrentTouchMove == TOUCH_MOVE_ROUTEPOINT && this.m_SelectedTouchMoveRoutepoint != null && this.m_bTouchMoveTargetMoved) {
            if (this.m_SelectedTouchMoveRoutepoint.m_ParentRoute.m_CreatedByMMTracker) {
                this.m_SelectedTouchMoveRoutepoint.m_ParentRoute.WriteGPX(this.m_SelectedTouchMoveRoutepoint.m_ParentRoute.m_sFileName);
            } else if (!this.m_SelectedTouchMoveRoutepoint.m_ParentRoute.m_bNoSaveWarningShown) {
                Toast.makeText(this.m_Context, this.m_Parent.getText(C0047R.string.MapView_toast_rt_not_stored), 0).show();
                this.m_SelectedTouchMoveRoutepoint.m_ParentRoute.m_bNoSaveWarningShown = true;
            }
        }
        this.m_iCurrentTouchMove = 0;
        if (this.m_Parent.m_PositionLock && (this.m_Parent.m_bGpsFix || this.m_Parent.m_bNetworkFix)) {
            SetMapRotationCenter(this.m_iGpsX, this.m_iGpsY, MAP_COORDINATES);
        } else {
            SetMapRotationCenter(this.m_Parent.m_iGpsLockCenterX, this.m_Parent.m_iGpsLockCenterY, SCREEN_COORDINATES);
        }
    }

    private double spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return Math.sqrt((double) ((x * x) + (y * y)));
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.m_Parent.m_ScreenLock) {
            return true;
        }
        float x = event.getX();
        float y = event.getY();
        Log.d("MM_TRACKER_TOUCH", "MotionEvent =" + event.getAction());
        switch (event.getAction() & 255) {
            case 0:
                this.m_iTouchMode = TOUCH_MODE_DRAG;
                touch_start(x, y);
                this.m_TouchTimerStart = System.currentTimeMillis();
                break;
            case 1:
            case 6:
                if (this.m_iTouchMode == TOUCH_MODE_DRAG) {
                    touch_up();
                    if (!this.m_bTouchMoveActive && System.currentTimeMillis() - this.m_TouchTimerStart < ((long) (MMTrackerActivity.TIME_TO_CONTEXT_MENU * 100))) {
                        EvaluateKlick();
                    }
                } else if (this.m_iTouchMode == TOUCH_MODE_ZOOM) {
                    if (this.m_dPinchScale != 1.0d) {
                        int zoomlevel = FindZoomLevelFromScale(this.m_dPinchOldZoomFactor * this.m_dPinchScale, false);
                        if (SetZoomLevel(zoomlevel)) {
                            ExecuteZoom(zoomlevel, this.m_dPinchOldZoomFactor, (int) this.m_dPinchMidPointX, (int) this.m_dPinchMidPointY);
                        }
                    }
                    this.m_DisplayLongLat = false;
                    this.m_iCurrentTouchMove = 0;
                }
                this.m_iTouchMode = TOUCH_MODE_NONE;
                this.m_bTouchMoveActive = false;
                invalidateMapScreen(false);
                break;
            case 2:
                if (this.m_iTouchMode == TOUCH_MODE_DRAG) {
                    touch_move(x, y);
                    this.m_DisplayLongLat = false;
                    this.m_DisplayRpData = false;
                    this.m_DisplayWpData = false;
                } else if (this.m_iTouchMode == TOUCH_MODE_ZOOM) {
                    double newDist = spacing(event);
                    if (newDist > 10.0d) {
                        this.m_dPinchScale = newDist / this.m_dPinchStartDistance;
                        if (this.m_dPinchOldZoomFactor * this.m_dPinchScale < m_dZoomFactor[0]) {
                            this.m_dPinchScale = m_dZoomFactor[0] / this.m_dPinchOldZoomFactor;
                        }
                        if (this.m_dPinchOldZoomFactor * this.m_dPinchScale > m_dZoomFactor[MAX_ZOOM_LEVELS - 1]) {
                            this.m_dPinchScale = m_dZoomFactor[MAX_ZOOM_LEVELS - 1] / this.m_dPinchOldZoomFactor;
                        }
                    }
                }
                invalidateMapScreen(false);
                break;
            case 5:
                this.m_dPinchStartDistance = spacing(event);
                if (this.m_dPinchStartDistance > 10.0d && (this.m_iCurrentTouchMove == 0 || this.m_iCurrentTouchMove == TOUCH_MOVE_MAP)) {
                    this.m_dPinchMidPointX = (double) ((event.getX(0) + event.getX(1)) / 2.0f);
                    this.m_dPinchMidPointY = (double) ((event.getY(0) + event.getY(1)) / 2.0f);
                    this.m_iTouchMode = TOUCH_MODE_ZOOM;
                    this.m_dPinchOldZoomFactor = m_dZoomFactor[this.m_Parent.m_iZoomLevel];
                    this.m_bTouchHold = false;
                    this.m_dPinchScale = 1.0d;
                    break;
                }
        }
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (this.m_Parent.m_iSettingKeyPadDisplayMove != 0) {
            MMTrackerActivity mMTrackerActivity;
            int iShift = this.m_Parent.m_iSettingKeyPadDisplayMove;
            if (this.m_Parent.m_SettingsInvertDPad) {
                iShift *= -1;
            }
            if (keyCode == 21 && event.getRepeatCount() == 0) {
                mMTrackerActivity = this.m_Parent;
                mMTrackerActivity.m_iDispPosX = (int) (((double) mMTrackerActivity.m_iDispPosX) - (this.m_Parent.m_dCosMapRotation * ((double) iShift)));
                mMTrackerActivity = this.m_Parent;
                mMTrackerActivity.m_iDispPosY = (int) (((double) mMTrackerActivity.m_iDispPosY) + (this.m_Parent.m_dSinMapRotation * ((double) iShift)));
            }
            if (keyCode == 22 && event.getRepeatCount() == 0) {
                mMTrackerActivity = this.m_Parent;
                mMTrackerActivity.m_iDispPosX = (int) (((double) mMTrackerActivity.m_iDispPosX) + (this.m_Parent.m_dCosMapRotation * ((double) iShift)));
                mMTrackerActivity = this.m_Parent;
                mMTrackerActivity.m_iDispPosY = (int) (((double) mMTrackerActivity.m_iDispPosY) - (this.m_Parent.m_dSinMapRotation * ((double) iShift)));
            }
            if (keyCode == 19 && event.getRepeatCount() == 0) {
                mMTrackerActivity = this.m_Parent;
                mMTrackerActivity.m_iDispPosX = (int) (((double) mMTrackerActivity.m_iDispPosX) - (this.m_Parent.m_dSinMapRotation * ((double) iShift)));
                mMTrackerActivity = this.m_Parent;
                mMTrackerActivity.m_iDispPosY = (int) (((double) mMTrackerActivity.m_iDispPosY) - (this.m_Parent.m_dCosMapRotation * ((double) iShift)));
            }
            if (keyCode == 20 && event.getRepeatCount() == 0) {
                mMTrackerActivity = this.m_Parent;
                mMTrackerActivity.m_iDispPosX = (int) (((double) mMTrackerActivity.m_iDispPosX) + (this.m_Parent.m_dSinMapRotation * ((double) iShift)));
                mMTrackerActivity = this.m_Parent;
                mMTrackerActivity.m_iDispPosY = (int) (((double) mMTrackerActivity.m_iDispPosY) + (this.m_Parent.m_dCosMapRotation * ((double) iShift)));
            }
            if (this.m_Parent.m_PositionLock && (this.m_Parent.m_bGpsFix || this.m_Parent.m_bNetworkFix)) {
                SetMapRotationCenter(this.m_iGpsX, this.m_iGpsY, MAP_COORDINATES);
            } else {
                SetMapRotationCenter(this.m_Parent.m_iGpsLockCenterX, this.m_Parent.m_iGpsLockCenterY, SCREEN_COORDINATES);
            }
            invalidateMapScreen(false);
        }
        return super.onKeyDown(keyCode, event);
    }

    private void ExecuteZoom(int zoomlevel, double zoom_old, int iScreenCenterX, int iScreenCenterY) {
        double zoom_new = m_dZoomFactor[zoomlevel];
        if (zoom_new != zoom_old) {
            this.m_iGpsX = (int) Math.round((((double) this.m_iGpsX) / zoom_old) * zoom_new);
            this.m_iGpsY = (int) Math.round((((double) this.m_iGpsY) / zoom_old) * zoom_new);
            DoublePoint p = RotatePointNeg((double) iScreenCenterX, (double) iScreenCenterY, (double) this.m_Parent.m_iRotateCenterX, (double) this.m_Parent.m_iRotateCenterY);
            iScreenCenterX = (int) Math.round(p.f0x);
            iScreenCenterY = (int) Math.round(p.f1y);
            double x = ((double) (this.m_Parent.m_iDispPosX + iScreenCenterX)) / zoom_old;
            double y = ((double) (this.m_Parent.m_iDispPosY + iScreenCenterY)) / zoom_old;
            this.m_Parent.m_iDispPosX = ((int) Math.round(x * zoom_new)) - iScreenCenterX;
            this.m_Parent.m_iDispPosY = ((int) Math.round(y * zoom_new)) - iScreenCenterY;
            if (this.m_Parent.m_PositionLock && (this.m_Parent.m_bGpsFix || this.m_Parent.m_bNetworkFix)) {
                SetMapRotationCenter(this.m_iGpsX, this.m_iGpsY, MAP_COORDINATES);
            } else {
                SetMapRotationCenter(this.m_Parent.m_iGpsLockCenterX, this.m_Parent.m_iGpsLockCenterY, SCREEN_COORDINATES);
            }
            Iterator it = MMTrackerActivity.tracks.iterator();
            while (it.hasNext()) {
                ((Track) it.next()).m_bCacheVaild = false;
            }
            it = MMTrackerActivity.routes.iterator();
            while (it.hasNext()) {
                ((Route) it.next()).m_bCacheVaild = false;
            }
            it = MMTrackerActivity.waypoints.iterator();
            while (it.hasNext()) {
                ((Waypoint) it.next()).m_bCacheVaild = false;
            }
            this.m_Parent.m_Tqm.UpdateQctAndClear(this.m_Qct);
            invalidateMapScreen(true);
        }
    }

    public void zoomPlus() {
        int zoomlevel = this.m_Parent.m_iZoomLevel + 1;
        if (SetZoomLevel(zoomlevel)) {
            ExecuteZoom(zoomlevel, m_dZoomFactor[zoomlevel - 1], this.m_iZoomCenterX, this.m_iZoomCenterY);
        } else {
            invalidateMapScreen(true);
        }
    }

    public void zoomMinus() {
        int zoomlevel = this.m_Parent.m_iZoomLevel - 1;
        if (SetZoomLevel(zoomlevel)) {
            ExecuteZoom(zoomlevel, m_dZoomFactor[zoomlevel + 1], this.m_iZoomCenterX, this.m_iZoomCenterY);
        } else {
            invalidateMapScreen(true);
        }
    }

    public void scaleSmall() {
        Point p = convertScreenToMapCoordinates((float) this.m_iZoomCenterX, (float) this.m_iZoomCenterY);
        this.m_Parent.LoadDifferentScaleMap(MMTrackerActivity.SMALLER_SCALE, this.m_Qct.convertXYtoLongitude(p.x, p.y), this.m_Qct.convertXYtoLatitude(p.x, p.y), (float) this.m_iZoomCenterX, (float) this.m_iZoomCenterY);
    }

    public void scaleLarge() {
        Point p = convertScreenToMapCoordinates((float) this.m_iZoomCenterX, (float) this.m_iZoomCenterY);
        this.m_Parent.LoadDifferentScaleMap(MMTrackerActivity.LARGER_SCALE, this.m_Qct.convertXYtoLongitude(p.x, p.y), this.m_Qct.convertXYtoLatitude(p.x, p.y), (float) this.m_iZoomCenterX, (float) this.m_iZoomCenterY);
    }

    public void buttonRouteCancel() {
        if (MMTrackerActivity.m_CurrentlyCreatedRoute != null) {
            AlertDialog alertDialog = new Builder(this.m_Parent).create();
            alertDialog.setTitle("MM Tracker");
            alertDialog.setMessage(this.m_Parent.getString(C0047R.string.MapView_ask_route_cancel));
            alertDialog.setButton(this.m_Parent.getString(C0047R.string.yes), new C00169());
            alertDialog.setButton2(this.m_Parent.getString(C0047R.string.no), new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            alertDialog.show();
            invalidateMapScreen(true);
        }
    }

    public void buttonRouteFinish() {
        if (MMTrackerActivity.m_CurrentlyCreatedRoute != null) {
            if (MMTrackerActivity.m_CurrentlyCreatedRoute.routepoints.size() > 1) {
                MMTrackerActivity.m_CurrentlyCreatedRoute.WriteGPX(MMTrackerActivity.m_CurrentlyCreatedRoute.m_sFileName);
            } else {
                MMTrackerActivity.routes.remove(MMTrackerActivity.m_CurrentlyCreatedRoute);
            }
            MMTrackerActivity.m_CurrentlyCreatedRoute = null;
            invalidateMapScreen(true);
        }
    }

    public void buttonRouteRemove() {
        if (MMTrackerActivity.m_CurrentlyCreatedRoute != null && MMTrackerActivity.m_CurrentlyCreatedRoute.routepoints.size() > 1) {
            MMTrackerActivity.m_CurrentlyCreatedRoute.routepoints.remove(MMTrackerActivity.m_CurrentlyCreatedRoute.routepoints.size() - 1);
            MMTrackerActivity.m_CurrentlyCreatedRoute.chachepoints.remove(MMTrackerActivity.m_CurrentlyCreatedRoute.chachepoints.size() - 1);
            invalidateMapScreen(true);
        }
    }

    public void buttonEndNavigation() {
        MMTrackerActivity.m_NavigationTarget.m_Type = NavigationTarget.TARGET_TYPE_NONE;
        invalidateMapScreen(true);
    }

    private void EvaluateKlick() {
        boolean bSelectionWp = false;
        boolean bSelectionTr = false;
        boolean bSelectionRt = false;
        boolean bObjectWasSelected = false;
        if (this.m_RectButtonZoomPlus != null && this.m_RectButtonZoomMinus != null && this.m_RectButtonMapCoarse != null && this.m_RectButtonMapFine != null) {
            if (!(MMTrackerActivity.m_SelectedRoutepoint == null && MMTrackerActivity.m_SelectedWaypoint == null && MMTrackerActivity.m_SelectedTrack == null)) {
                bObjectWasSelected = true;
            }
            if (this.m_RectButtonZoomPlus.contains(Math.round(this.m_TouchMoveStartX), Math.round(this.m_TouchMoveStartY)) && this.m_Parent.m_SettingsShowZoombuttons) {
                Log.d("MM Tracker", "TouchHold = false (5)");
                this.m_bTouchHold = false;
                this.m_iCurrentActiveButton = BUTTON_ZOOM_PLUS;
                invalidateMapScreen(true);
                this.m_Handler.post(this.m_UpdateZoomPlus);
            } else if (this.m_RectButtonZoomMinus.contains(Math.round(this.m_TouchMoveStartX), Math.round(this.m_TouchMoveStartY)) && this.m_Parent.m_SettingsShowZoombuttons) {
                this.m_bTouchHold = false;
                this.m_iCurrentActiveButton = BUTTON_ZOOM_MINUS;
                invalidateMapScreen(true);
                this.m_Handler.post(this.m_UpdateZoomMinus);
            } else if (this.m_RectButtonMapCoarse.contains(Math.round(this.m_TouchMoveStartX), Math.round(this.m_TouchMoveStartY)) && this.m_Parent.m_SettingsShowMapscaleButtons) {
                this.m_bTouchHold = false;
                this.m_iCurrentActiveButton = BUTTON_SCALE_SMALL;
                invalidateMapScreen(true);
                this.m_Handler.post(this.m_UpdateScaleSmall);
            } else if (this.m_RectButtonMapFine.contains(Math.round(this.m_TouchMoveStartX), Math.round(this.m_TouchMoveStartY)) && this.m_Parent.m_SettingsShowMapscaleButtons) {
                this.m_bTouchHold = false;
                this.m_iCurrentActiveButton = BUTTON_SCALE_LARGE;
                invalidateMapScreen(true);
                this.m_Handler.post(this.m_UpdateScaleLarge);
            } else if (this.m_RectButtonRtCancel.contains(Math.round(this.m_TouchMoveStartX), Math.round(this.m_TouchMoveStartY)) && MMTrackerActivity.m_CurrentlyCreatedRoute != null) {
                this.m_bTouchHold = false;
                this.m_iCurrentActiveButton = BUTTON_ROUTE_CANCEL;
                invalidateMapScreen(true);
                this.m_Handler.post(this.m_UpdateRouteCancel);
            } else if (this.m_RectButtonRtFinish.contains(Math.round(this.m_TouchMoveStartX), Math.round(this.m_TouchMoveStartY)) && MMTrackerActivity.m_CurrentlyCreatedRoute != null) {
                this.m_bTouchHold = false;
                this.m_iCurrentActiveButton = BUTTON_ROUTE_FINISH;
                invalidateMapScreen(true);
                this.m_Handler.post(this.m_UpdateRouteFinish);
            } else if (this.m_RectButtonRtRemove.contains(Math.round(this.m_TouchMoveStartX), Math.round(this.m_TouchMoveStartY)) && MMTrackerActivity.m_CurrentlyCreatedRoute != null) {
                this.m_bTouchHold = false;
                this.m_iCurrentActiveButton = BUTTON_ROUTE_REMOVE;
                invalidateMapScreen(true);
                this.m_Handler.post(this.m_UpdateRouteRemove);
            } else if (!this.m_RectButtonEndNavigation.contains(Math.round(this.m_TouchMoveStartX), Math.round(this.m_TouchMoveStartY)) || MMTrackerActivity.m_NavigationTarget.m_Type == NavigationTarget.TARGET_TYPE_NONE) {
                if (MMTrackerActivity.m_CurrentlyCreatedRoute == null) {
                    Waypoint wp = MMTrackerActivity.m_SelectedWaypoint;
                    Waypoint GetNearestWaypoint = this.m_Parent.GetNearestWaypoint(this.m_TouchMoveStartX, this.m_TouchMoveStartY);
                    MMTrackerActivity.m_SelectedWaypoint = GetNearestWaypoint;
                    if (GetNearestWaypoint != null) {
                        if (MMTrackerActivity.m_SelectedWaypoint.m_dDummyDistance >= ((double) MMTrackerActivity.DISTANCE_TO_TRACK_FOR_MENU)) {
                            MMTrackerActivity.m_SelectedWaypoint = null;
                        } else if (wp != MMTrackerActivity.m_SelectedWaypoint) {
                            bSelectionWp = true;
                            MMTrackerActivity.m_SelectedTrack = null;
                            MMTrackerActivity.m_SelectedRoutepoint = null;
                        } else {
                            this.m_DisplayWpData = true;
                            this.m_DisplayRpData = false;
                            this.m_DisplayLongLat = false;
                        }
                    }
                    if (!bSelectionWp) {
                        Route rt = null;
                        if (MMTrackerActivity.m_SelectedRoutepoint != null) {
                            rt = MMTrackerActivity.m_SelectedRoutepoint.m_ParentRoute;
                        }
                        Routepoint GetNearestRoutepoint = this.m_Parent.GetNearestRoutepoint(this.m_TouchMoveStartX, this.m_TouchMoveStartY);
                        MMTrackerActivity.m_SelectedRoutepoint = GetNearestRoutepoint;
                        if (GetNearestRoutepoint != null) {
                            if (MMTrackerActivity.m_SelectedRoutepoint.m_ParentRoute.m_dDummyDistance >= ((double) MMTrackerActivity.DISTANCE_TO_TRACK_FOR_MENU)) {
                                MMTrackerActivity.m_SelectedRoutepoint = null;
                            } else if (rt != MMTrackerActivity.m_SelectedRoutepoint.m_ParentRoute) {
                                bSelectionRt = true;
                                MMTrackerActivity.m_SelectedWaypoint = null;
                                MMTrackerActivity.m_SelectedTrack = null;
                            } else {
                                this.m_DisplayRpData = true;
                                this.m_DisplayWpData = false;
                                this.m_DisplayLongLat = false;
                            }
                        }
                    }
                    if (!(bSelectionWp || bSelectionRt)) {
                        Track GetNearestTrack = this.m_Parent.GetNearestTrack(this.m_TouchMoveStartX, this.m_TouchMoveStartY);
                        MMTrackerActivity.m_SelectedTrack = GetNearestTrack;
                        if (GetNearestTrack != null) {
                            if (MMTrackerActivity.m_SelectedTrack.m_dDummyDistance < ((double) MMTrackerActivity.DISTANCE_TO_TRACK_FOR_MENU)) {
                                bSelectionTr = true;
                                MMTrackerActivity.m_SelectedWaypoint = null;
                                MMTrackerActivity.m_SelectedRoutepoint = null;
                            } else {
                                MMTrackerActivity.m_SelectedTrack = null;
                            }
                        }
                    }
                } else if (this.m_Qct != null) {
                    Point p = convertScreenToMapCoordinates(this.m_TouchMoveStartX, this.m_TouchMoveStartY);
                    MMTrackerActivity.m_CurrentlyCreatedRoute.add(this.m_Qct.convertXYtoLongitude(p.x, p.y), this.m_Qct.convertXYtoLatitude(p.x, p.y), 0.0f, 0.0f, "");
                    ((Routepoint) MMTrackerActivity.m_CurrentlyCreatedRoute.routepoints.get(MMTrackerActivity.m_CurrentlyCreatedRoute.routepoints.size() - 1)).RefreshXY(this.m_Qct);
                }
                if (bSelectionWp || bSelectionTr || bSelectionRt || MMTrackerActivity.m_CurrentlyCreatedRoute != null) {
                    invalidateMapScreen(true);
                    return;
                }
                if (!(!this.m_Parent.m_SettingsShowPosition || this.m_Qct == null || this.m_DisplayWpData || this.m_DisplayRpData || bObjectWasSelected)) {
                    this.m_DisplayLongLat = true;
                    this.m_DisplayWpData = false;
                    this.m_DisplayRpData = false;
                }
                MMTrackerActivity.m_SelectedTrack = null;
                if (!this.m_DisplayWpData) {
                    MMTrackerActivity.m_SelectedWaypoint = null;
                }
                if (!this.m_DisplayRpData) {
                    MMTrackerActivity.m_SelectedRoutepoint = null;
                }
            } else {
                this.m_bTouchHold = false;
                this.m_iCurrentActiveButton = BUTTON_END_NAVIGATION;
                invalidateMapScreen(true);
                this.m_Handler.post(this.m_UpdateEndNavigation);
            }
        }
    }

    public void invalidateMapScreen(boolean bFullScreen) {
        int iCountTime;
        double dZoom = m_dZoomFactor[this.m_Parent.m_iZoomLevel];
        if (dZoom > 1.0d) {
            this.m_Parent.m_Tqm.UpdateTileList(this.m_iVirtualDispSizeX, this.m_iVirtualDispSizeY, (int) Math.round(((double) this.m_Parent.m_iDispPosX) / dZoom), (int) Math.round(((double) this.m_Parent.m_iDispPosY) / dZoom), (double) this.m_Parent.m_fMapRotation, this.m_iDecimate, (int) Math.round(((double) this.m_Parent.m_iRotateCenterX) / dZoom), (int) Math.round(((double) this.m_Parent.m_iRotateCenterY) / dZoom));
        } else {
            this.m_Parent.m_Tqm.UpdateTileList(this.m_iVirtualDispSizeX, this.m_iVirtualDispSizeY, this.m_Parent.m_iDispPosX, this.m_Parent.m_iDispPosY, (double) this.m_Parent.m_fMapRotation, this.m_iDecimate, this.m_Parent.m_iRotateCenterX, this.m_Parent.m_iRotateCenterY);
        }
        if (bFullScreen) {
            this.m_Parent.m_bWaitForFullscreen = true;
            iCountTime = 0;
            while (this.m_Parent.m_Tqm.m_iLoadCount > 0 && iCountTime < 800) {
                iCountTime++;
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }
            this.m_Parent.m_bWaitForFullscreen = false;
        }
        iCountTime = 0;
        while (this.m_bOnDrawRunning && !this.m_bFirstTime && iCountTime < 1000) {
            iCountTime++;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e2) {
            }
        }
        invalidate();
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.m_bOnDrawRunning = true;
        this.m_Parent.m_lLastScreenRefresh = System.currentTimeMillis();
        Paint pText;
        if (this.m_Qct == null ? false : this.m_Qct.m_bFileOpen) {
            int i;
            pText = new Paint();
            Paint pRect = new Paint();
            Paint pMap = new Paint();
            if (!(!this.m_bFirstTime && getWidth() == this.m_iDispSizeX && getHeight() == this.m_iDispSizeY)) {
                InitAll(this.m_bFirstTime);
                this.m_bFirstTime = false;
                this.m_bDrawAll = true;
            }
            int l_iDispPosX = this.m_Parent.m_iDispPosX;
            int l_iDispPosY = this.m_Parent.m_iDispPosY;
            Log.d("MM Tracker - OnDraw", "Prepare paints");
            pText.setColor(Color.argb(255, 0, 0, 0));
            pText.setTextSize((float) (this.m_Parent.m_iDisplayDensity / 10));
            pText.setFakeBoldText(true);
            pText.setAntiAlias(true);
            pRect.setColor(Color.argb(192, 255, 255, 0));
            pRect.setAntiAlias(true);
            pMap.setAntiAlias(false);
            double dZoom = m_dZoomFactor[this.m_Parent.m_iZoomLevel];
            if (this.m_bDrawAll) {
                if (dZoom > 1.0d) {
                    this.m_Parent.m_Tqm.UpdateTileList(this.m_iVirtualDispSizeX, this.m_iVirtualDispSizeY, (int) Math.round(((double) this.m_Parent.m_iDispPosX) / dZoom), (int) Math.round(((double) this.m_Parent.m_iDispPosY) / dZoom), (double) this.m_Parent.m_fMapRotation, this.m_iDecimate, (int) Math.round(((double) this.m_Parent.m_iRotateCenterX) / dZoom), (int) Math.round(((double) this.m_Parent.m_iRotateCenterY) / dZoom));
                } else {
                    this.m_Parent.m_Tqm.UpdateTileList(this.m_iVirtualDispSizeX, this.m_iVirtualDispSizeY, this.m_Parent.m_iDispPosX, this.m_Parent.m_iDispPosY, (double) this.m_Parent.m_fMapRotation, this.m_iDecimate, this.m_Parent.m_iRotateCenterX, this.m_Parent.m_iRotateCenterY);
                }
                this.m_Parent.m_bWaitForFullscreen = true;
                while (this.m_Parent.m_Tqm.m_iLoadCount > 0) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                }
                this.m_Parent.m_bWaitForFullscreen = false;
            }
            canvas.drawARGB(255, 255, 255, 255);
            Matrix matrix = new Matrix();
            if (dZoom <= 1.0d) {
                i = 0;
                while (i < this.m_Parent.m_Tqm.m_TileQueue.length) {
                    if (this.m_Parent.m_Tqm.m_TileQueue[i].m_bTileInUse && !this.m_Parent.m_Tqm.m_TileQueue[i].m_bNeedsToBeLoaded) {
                        if (MMTrackerActivity.m_iSettingsMapRotation != 0 || this.m_iTouchMode == TOUCH_MODE_ZOOM) {
                            matrix.reset();
                            matrix.setTranslate((float) this.m_Parent.m_Tqm.m_TileQueue[i].m_iScreenX, (float) this.m_Parent.m_Tqm.m_TileQueue[i].m_iScreenY);
                            matrix.postRotate((float) this.m_Parent.m_Tqm.m_TileQueue[i].m_dAngle, (float) this.m_Parent.m_iRotateCenterX, (float) this.m_Parent.m_iRotateCenterY);
                            if (this.m_iTouchMode == TOUCH_MODE_ZOOM) {
                                matrix.postScale((float) this.m_dPinchScale, (float) this.m_dPinchScale);
                                matrix.postTranslate((float) ((1.0d - this.m_dPinchScale) * this.m_dPinchMidPointX), (float) ((1.0d - this.m_dPinchScale) * this.m_dPinchMidPointY));
                            }
                            canvas.drawBitmap(this.m_Parent.m_Tqm.m_TileQueue[i].m_Bitmap.m_Bitmap, matrix, pMap);
                        } else {
                            canvas.drawBitmap(this.m_Parent.m_Tqm.m_TileQueue[i].m_Bitmap.m_Bitmap, (float) this.m_Parent.m_Tqm.m_TileQueue[i].m_iScreenX, (float) this.m_Parent.m_Tqm.m_TileQueue[i].m_iScreenY, null);
                        }
                    }
                    i++;
                }
            } else {
                i = 0;
                while (i < this.m_Parent.m_Tqm.m_TileQueue.length) {
                    if (this.m_Parent.m_Tqm.m_TileQueue[i].m_bTileInUse && !this.m_Parent.m_Tqm.m_TileQueue[i].m_bNeedsToBeLoaded) {
                        matrix.reset();
                        matrix.setTranslate((float) this.m_Parent.m_Tqm.m_TileQueue[i].m_iScreenX, (float) this.m_Parent.m_Tqm.m_TileQueue[i].m_iScreenY);
                        matrix.postScale((float) dZoom, (float) dZoom);
                        if (MMTrackerActivity.m_iSettingsMapRotation != 0) {
                            matrix.postRotate((float) this.m_Parent.m_Tqm.m_TileQueue[i].m_dAngle, (float) this.m_Parent.m_iRotateCenterX, (float) this.m_Parent.m_iRotateCenterY);
                        }
                        if (this.m_iTouchMode == TOUCH_MODE_ZOOM) {
                            matrix.postScale((float) this.m_dPinchScale, (float) this.m_dPinchScale);
                            matrix.postTranslate((float) ((1.0d - this.m_dPinchScale) * this.m_dPinchMidPointX), (float) ((1.0d - this.m_dPinchScale) * this.m_dPinchMidPointY));
                        }
                        canvas.drawBitmap(this.m_Parent.m_Tqm.m_TileQueue[i].m_Bitmap.m_Bitmap, matrix, pMap);
                    }
                    i++;
                }
            }
            if (this.m_bDrawAll) {
                this.m_bDrawAll = false;
            }
            if (this.m_iTouchMode != TOUCH_MODE_ZOOM) {
                Log.d("MM Tracker - OnDraw", "Draw all Overlay");
                Rect MapRect = CalcMaxRectFromScreen();
                DrawAllRoutes(canvas, dZoom, MapRect);
                DrawAllTracks(canvas, dZoom, MapRect);
                DrawAllWaypoints(canvas, dZoom, MapRect);
            }
            CalcTrueNorthOfMap();
            if (this.m_iTouchMode != TOUCH_MODE_ZOOM) {
                DoublePoint dp;
                if (this.m_Parent.m_bGpsFix) {
                    if (this.m_Parent.m_SettingsArrowLength > 0) {
                        DrawDirectionArrow(canvas);
                    }
                    if (MMTrackerActivity.m_iSettingsMapRotation != 0) {
                        dp = RotatePoint((double) (this.m_iGpsX - l_iDispPosX), (double) (this.m_iGpsY - l_iDispPosY), (double) this.m_Parent.m_iRotateCenterX, (double) this.m_Parent.m_iRotateCenterY);
                        canvas.drawBitmap(this.m_bmCrosshairActive, ((float) dp.f0x) - ((float) (this.m_bmCrosshairActive.getWidth() / 2)), ((float) dp.f1y) - ((float) (this.m_bmCrosshairActive.getHeight() / 2)), null);
                    } else {
                        canvas.drawBitmap(this.m_bmCrosshairActive, (((float) this.m_iGpsX) - ((float) l_iDispPosX)) - ((float) (this.m_bmCrosshairActive.getWidth() / 2)), (((float) this.m_iGpsY) - ((float) l_iDispPosY)) - ((float) (this.m_bmCrosshairActive.getHeight() / 2)), null);
                    }
                } else if (this.m_Parent.m_iGpsType == MMTrackerActivity.LOCATION_TYPE_NETWORK) {
                    DrawAccuracyCircle(canvas);
                    if (this.m_Parent.m_SettingsArrowLength > 0 && MMTrackerActivity.m_iSettingsUseCompassForVector != 0) {
                        DrawDirectionArrow(canvas);
                    }
                    if (MMTrackerActivity.m_iSettingsMapRotation != 0) {
                        dp = RotatePoint((double) (this.m_iGpsX - l_iDispPosX), (double) (this.m_iGpsY - l_iDispPosY), (double) this.m_Parent.m_iRotateCenterX, (double) this.m_Parent.m_iRotateCenterY);
                        canvas.drawBitmap(this.m_bmCrosshairNetwork, ((float) dp.f0x) - ((float) (this.m_bmCrosshairNetwork.getWidth() / 2)), ((float) dp.f1y) - ((float) (this.m_bmCrosshairNetwork.getHeight() / 2)), null);
                    } else {
                        canvas.drawBitmap(this.m_bmCrosshairNetwork, (((float) this.m_iGpsX) - ((float) l_iDispPosX)) - ((float) (this.m_bmCrosshairNetwork.getWidth() / 2)), (((float) this.m_iGpsY) - ((float) l_iDispPosY)) - ((float) (this.m_bmCrosshairNetwork.getHeight() / 2)), null);
                    }
                }
            }
            if (this.m_Parent.m_SettingsShowScale) {
                DrawScale(canvas);
            }
            if (MMTrackerActivity.m_CurrentlyCreatedRoute == null) {
                this.m_iWindowLastYPos = 0;
                this.m_iWindowLastYPos = DrawStatusLine(canvas, MMTrackerActivity.m_dGpsSpeed, MMTrackerActivity.m_dGpsAltitude);
                if (this.m_Parent.m_SettingsShowPositionWindow) {
                    if (MMTrackerActivity.m_SettingsPositionType != 5) {
                        this.m_iWindowLastYPos = DrawLatLonPositionLine(canvas, MMTrackerActivity.m_SettingsPositionType, this.m_Parent.m_dGpsLatitude, this.m_Parent.m_dGpsLongitude);
                    } else if (this.m_OsgbTemp.SetLatLonCoord(this.m_Parent.m_dGpsLatitude, this.m_Parent.m_dGpsLongitude, 0.0d) || !(this.m_Parent.m_bGpsFix || this.m_Parent.m_bNetworkFix)) {
                        this.m_iWindowLastYPos = DrawOSGridPositionLine(canvas, this.m_Parent.m_dGpsLatitude, this.m_Parent.m_dGpsLongitude);
                    } else {
                        this.m_iWindowLastYPos = DrawLatLonPositionLine(canvas, 0, this.m_Parent.m_dGpsLatitude, this.m_Parent.m_dGpsLongitude);
                    }
                }
                if (this.m_Parent.m_SettingsShowHeadingAndBearing) {
                    this.m_iWindowLastYPos = DrawHeadingAndBearing(canvas, MMTrackerActivity.m_dCalculatedGpsHeading, MMTrackerActivity.m_dCalculatedNavigationBearing);
                }
                if (this.m_Parent.m_SettingsShowTrackInfo) {
                    Track track = MMTrackerActivity.m_SelectedTrack;
                    Route route = null;
                    if (MMTrackerActivity.m_SelectedRoutepoint != null) {
                        route = MMTrackerActivity.m_SelectedRoutepoint.m_ParentRoute;
                    }
                    if (track == null && route == null && this.m_Parent != null) {
                        i = MMTrackerActivity.getActiveTrackIndex();
                        if (i > -1) {
                            track = (Track) MMTrackerActivity.tracks.get(i);
                        }
                    }
                    this.m_iWindowLastYPos = DrawTrackAndRouteInfoLine(canvas, track, route);
                }
                this.m_iWindowLastYPos = DrawNavigationWindow(canvas);
                if (MMTrackerActivity.m_bSettingsShowCompass) {
                    DrawCompass(canvas);
                }
            }
            RenderScreenButtons(canvas);
            if (this.m_iCurrentTouchMove == TOUCH_MOVE_WAYPOINT && this.m_SelectedTouchMoveWaypoint != null) {
                DrawPositionInfo(canvas, this.m_SelectedTouchMoveWaypoint, null, this.m_iCurrentTouchMove, false, true, false);
            }
            if (this.m_iCurrentTouchMove == TOUCH_MOVE_ROUTEPOINT && this.m_SelectedTouchMoveRoutepoint != null) {
                DrawPositionInfo(canvas, null, this.m_SelectedTouchMoveRoutepoint, this.m_iCurrentTouchMove, false, true, false);
            }
            if (this.m_iTouchMode != TOUCH_MODE_ZOOM) {
                if (this.m_DisplayWpData && MMTrackerActivity.m_SelectedWaypoint != null) {
                    DrawPositionInfo(canvas, MMTrackerActivity.m_SelectedWaypoint, null, TOUCH_MOVE_WAYPOINT, true, true, false);
                }
                if (this.m_DisplayRpData && MMTrackerActivity.m_SelectedRoutepoint != null) {
                    DrawPositionInfo(canvas, null, MMTrackerActivity.m_SelectedRoutepoint, TOUCH_MOVE_ROUTEPOINT, true, true, false);
                }
                if (this.m_DisplayLongLat && this.m_Parent.m_SettingsShowPosition && this.m_Qct != null) {
                    Waypoint wp = new Waypoint();
                    wp.m_fX = (float) this.m_Qct.convertLongLatToX(this.m_dTouchMoveStartLon, this.m_dTouchMoveStartLat);
                    wp.m_fY = (float) this.m_Qct.convertLongLatToY(this.m_dTouchMoveStartLon, this.m_dTouchMoveStartLat);
                    wp.m_dGpsLat = this.m_dTouchMoveStartLat;
                    wp.m_dGpsLong = this.m_dTouchMoveStartLon;
                    DrawPositionInfo(canvas, wp, null, TOUCH_MOVE_POSITION, false, false, true);
                }
            }
            this.m_Parent.m_iGpsLockCenterY = this.m_iWindowLastYPos + ((this.m_iDispSizeY - this.m_iWindowLastYPos) / 2);
            this.m_iZoomCenterY = this.m_Parent.m_iGpsLockCenterY;
        } else {
            Rect aRect = new Rect();
            pText = new Paint();
            pText.setColor(Color.argb(255, 255, 255, 255));
            pText.setTextSize((float) (this.m_Parent.m_iDisplayDensity / 6));
            pText.setFakeBoldText(true);
            pText.setAntiAlias(true);
            String sText = this.m_Parent.getString(C0047R.string.MapView_no_map_loaded);
            pText.getTextBounds(sText, 0, sText.length(), aRect);
            canvas.drawText(sText, (float) ((getWidth() - aRect.width()) / 2), (float) (getHeight() / 4), pText);
        }
        this.m_Parent.m_lDurationLastScreenRefresh = (this.m_Parent.m_lDurationLastScreenRefresh + (System.currentTimeMillis() - this.m_Parent.m_lLastScreenRefresh)) / 2;
        Log.d("MM_TRACKER_TIMER", "Last Refresh Duration: " + this.m_Parent.m_lDurationLastScreenRefresh);
        this.m_bOnDrawRunning = false;
    }

    public void RenderScreenButtons(Canvas canvas) {
        Paint pControls = new Paint();
        Paint pControlsLight = new Paint();
        pControls.setColor(Color.argb(224, 0, 0, 0));
        pControlsLight.setColor(Color.argb(128, 0, 0, 0));
        pControlsLight.setAntiAlias(true);
        if (this.m_Parent.m_SettingsShowZoombuttons) {
            Log.d("MM Tracker - OnDraw", "Draw Zoom Buttons");
            if (this.m_iCurrentActiveButton == BUTTON_ZOOM_PLUS) {
                canvas.drawBitmap(this.m_bmZoomPlusActive, (float) this.m_RectButtonZoomPlus.left, (float) this.m_RectButtonZoomPlus.top, pControls);
                this.m_iCurrentActiveButton = 0;
            } else {
                canvas.drawBitmap(this.m_bmZoomPlus, (float) this.m_RectButtonZoomPlus.left, (float) this.m_RectButtonZoomPlus.top, pControls);
            }
            if (this.m_iCurrentActiveButton == BUTTON_ZOOM_MINUS) {
                canvas.drawBitmap(this.m_bmZoomMinusActive, (float) this.m_RectButtonZoomMinus.left, (float) this.m_RectButtonZoomMinus.top, pControls);
                this.m_iCurrentActiveButton = 0;
            } else {
                canvas.drawBitmap(this.m_bmZoomMinus, (float) this.m_RectButtonZoomMinus.left, (float) this.m_RectButtonZoomMinus.top, pControls);
            }
        }
        if (this.m_Parent.m_SettingsShowMapscaleButtons) {
            Log.d("MM Tracker - OnDraw", "Draw Mapscale Buttons");
            if (this.m_iCurrentActiveButton == BUTTON_SCALE_SMALL) {
                canvas.drawBitmap(this.m_bmMapCoarseActive, (float) this.m_RectButtonMapCoarse.left, (float) this.m_RectButtonMapCoarse.top, pControls);
                this.m_iCurrentActiveButton = 0;
            } else {
                canvas.drawBitmap(this.m_bmMapCoarse, (float) this.m_RectButtonMapCoarse.left, (float) this.m_RectButtonMapCoarse.top, pControls);
            }
            if (this.m_iCurrentActiveButton == BUTTON_SCALE_LARGE) {
                canvas.drawBitmap(this.m_bmMapFineActive, (float) this.m_RectButtonMapFine.left, (float) this.m_RectButtonMapFine.top, pControls);
                this.m_iCurrentActiveButton = 0;
            } else {
                canvas.drawBitmap(this.m_bmMapFine, (float) this.m_RectButtonMapFine.left, (float) this.m_RectButtonMapFine.top, pControls);
            }
        }
        if (MMTrackerActivity.m_CurrentlyCreatedRoute != null) {
            if (this.m_iCurrentActiveButton == BUTTON_ROUTE_CANCEL) {
                canvas.drawBitmap(this.m_bmButtonRtCancelPressed, (float) this.m_RectButtonRtCancel.left, (float) this.m_RectButtonRtCancel.top, pControls);
                this.m_iCurrentActiveButton = 0;
            } else {
                canvas.drawBitmap(this.m_bmButtonRtCancelNormal, (float) this.m_RectButtonRtCancel.left, (float) this.m_RectButtonRtCancel.top, pControls);
            }
            if (this.m_iCurrentActiveButton == BUTTON_ROUTE_FINISH) {
                canvas.drawBitmap(this.m_bmButtonRtFinishPressed, (float) this.m_RectButtonRtFinish.left, (float) this.m_RectButtonRtFinish.top, pControls);
                this.m_iCurrentActiveButton = 0;
            } else {
                canvas.drawBitmap(this.m_bmButtonRtFinishNormal, (float) this.m_RectButtonRtFinish.left, (float) this.m_RectButtonRtFinish.top, pControls);
            }
            if (this.m_iCurrentActiveButton == BUTTON_ROUTE_REMOVE) {
                canvas.drawBitmap(this.m_bmButtonRtRemovePressed, (float) this.m_RectButtonRtRemove.left, (float) this.m_RectButtonRtRemove.top, pControls);
                this.m_iCurrentActiveButton = 0;
            } else {
                canvas.drawBitmap(this.m_bmButtonRtRemoveNormal, (float) this.m_RectButtonRtRemove.left, (float) this.m_RectButtonRtRemove.top, pControls);
            }
        }
        if (MMTrackerActivity.m_NavigationTarget.m_Type != NavigationTarget.TARGET_TYPE_NONE) {
            Rect dest = new Rect(this.m_RectButtonEndNavigation.left + (this.m_RectButtonEndNavigation.width() / 2), this.m_RectButtonEndNavigation.top, this.m_RectButtonEndNavigation.right, this.m_RectButtonEndNavigation.top + (this.m_RectButtonEndNavigation.height() / 2));
            Rect src = new Rect(0, 0, this.m_RectButtonEndNavigation.width(), this.m_RectButtonEndNavigation.height());
            if (this.m_iCurrentActiveButton == BUTTON_END_NAVIGATION) {
                canvas.drawBitmap(this.m_bmButtonEndNavigationPressed, src, dest, pControlsLight);
                this.m_iCurrentActiveButton = 0;
                return;
            }
            canvas.drawBitmap(this.m_bmButtonEndNavigationNormal, src, dest, pControlsLight);
        }
    }

    public void CalcTrueNorthOfMap() {
        this.m_fTrueNorth = 0.0f;
        if (this.m_Qct != null) {
            Point p = convertScreenToMapCoordinates((float) (this.m_iDispSizeX / 2), (float) (this.m_iDispSizeY / 2));
            double dLat1 = this.m_Qct.convertXYtoLatitude(p.x, p.y);
            double dLong1 = this.m_Qct.convertXYtoLongitude(p.x, p.y);
            double X1 = this.m_Qct.convertLongLatToX(dLong1, dLat1);
            double Y1 = this.m_Qct.convertLongLatToY(dLong1, dLat1);
            double X2 = this.m_Qct.convertLongLatToX(dLong1, 10.0d + dLat1);
            this.m_fTrueNorth = ((float) ((180.0d * Math.atan2(this.m_Qct.convertLongLatToY(dLong1, 10.0d + dLat1) - Y1, X2 - X1)) / 3.141592653589793d)) + 90.0f;
        }
    }

    public Point convertScreenToMapCoordinates(float x, float y) {
        double dZoom;
        Point p = new Point();
        if (MMTrackerActivity.m_iSettingsMapRotation != 0) {
            DoublePoint dp = RotatePointNeg((double) x, (double) y, (double) this.m_Parent.m_iRotateCenterX, (double) this.m_Parent.m_iRotateCenterY);
            x = (float) dp.f0x;
            y = (float) dp.f1y;
        }
        if (this.m_iTouchMode == TOUCH_MODE_ZOOM) {
            dZoom = this.m_dPinchScale * m_dZoomFactor[this.m_Parent.m_iZoomLevel];
        } else {
            dZoom = m_dZoomFactor[this.m_Parent.m_iZoomLevel];
        }
        p.x = (int) Math.round(((double) (((float) this.m_Parent.m_iDispPosX) + x)) / dZoom);
        p.y = (int) Math.round(((double) (((float) this.m_Parent.m_iDispPosY) + y)) / dZoom);
        return p;
    }

    public DoublePoint convertMapToScreenCoordinates(float x, float y) {
        DoublePoint p = new DoublePoint();
        double dZoom = m_dZoomFactor[this.m_Parent.m_iZoomLevel];
        p.f0x = (((double) x) * dZoom) - ((double) this.m_Parent.m_iDispPosX);
        p.f1y = (((double) y) * dZoom) - ((double) this.m_Parent.m_iDispPosY);
        if (MMTrackerActivity.m_iSettingsMapRotation != 0) {
            DoublePoint dp = RotatePoint(p.f0x, p.f1y, (double) this.m_Parent.m_iRotateCenterX, (double) this.m_Parent.m_iRotateCenterY);
            p.f0x = dp.f0x;
            p.f1y = dp.f1y;
        }
        return p;
    }

    private void DrawCompass(Canvas canvas) {
        float fCenterX;
        Matrix m = new Matrix();
        Path pathNew = new Path();
        Paint pPathFill = new Paint();
        Paint pPath = new Paint();
        Paint pCircle = new Paint();
        Paint pPathLetter = new Paint();
        Paint pText = new Paint();
        if (this.m_iDispSizeY > this.m_iDispSizeX) {
            fCenterX = ((float) this.m_RectButtonMapFine.left) + this.m_fCompassRadius;
        } else {
            fCenterX = (((float) this.m_RectButtonMapFine.right) + this.m_fCompassRadius) * 1.2f;
        }
        float fCenterY = ((float) this.m_iDispSizeY) - (this.m_fCompassRadius * 1.5f);
        pCircle.setColor(-1058100736);
        pCircle.setStyle(Style.FILL);
        pCircle.setAntiAlias(true);
        canvas.drawCircle(fCenterX, fCenterY, this.m_fCompassRadius * 1.3f, pCircle);
        pCircle.setColor(-1442840338);
        pCircle.setStyle(Style.STROKE);
        pCircle.setStrokeWidth((float) (this.m_Parent.m_iDisplayDensity / 50));
        canvas.drawCircle(fCenterX, fCenterY, this.m_fCompassRadius * 1.3f, pCircle);
        pPathLetter.setStyle(Style.STROKE);
        pPathLetter.setColor(-1073741824);
        pPathLetter.setStrokeWidth((float) (this.m_Parent.m_iDisplayDensity / 50));
        pPathLetter.setStrokeMiter(0.001f);
        pPathLetter.setAntiAlias(true);
        float fScale = this.m_fCompassRadius / 30.0f;
        int i = this.m_Parent.m_iSettingsCompassType;
        if (r0 == 1) {
            Paint pPathLetterRed = new Paint();
            pPathLetterRed.setStyle(Style.STROKE);
            pPathLetterRed.setColor(-1593901056);
            pPathLetterRed.setStrokeWidth((float) (this.m_Parent.m_iDisplayDensity / 50));
            pPathLetterRed.setStrokeMiter(0.001f);
            pPathLetterRed.setAntiAlias(true);
            if (Math.abs(this.m_fTrueNorth) > 4.0f) {
                m.setScale(fScale, fScale, 0.0f, 0.0f);
                this.m_pathLine.transform(m, pathNew);
                m.setRotate(0.0f, 0.0f, this.m_fCompassRadius);
                pathNew.transform(m);
                pathNew.offset(fCenterX, fCenterY - this.m_fCompassRadius);
                canvas.drawPath(pathNew, pPathLetterRed);
            }
            m.setScale(fScale, fScale, 0.0f, 0.0f);
            this.m_pathLine.transform(m, pathNew);
            m.setRotate(90.0f, 0.0f, this.m_fCompassRadius);
            pathNew.transform(m);
            pathNew.offset(fCenterX, fCenterY - this.m_fCompassRadius);
            canvas.drawPath(pathNew, pPathLetterRed);
            m.setScale(fScale, fScale, 0.0f, 0.0f);
            this.m_pathLine.transform(m, pathNew);
            m.setRotate(180.0f, 0.0f, this.m_fCompassRadius);
            pathNew.transform(m);
            pathNew.offset(fCenterX, fCenterY - this.m_fCompassRadius);
            canvas.drawPath(pathNew, pPathLetterRed);
            m.setScale(fScale, fScale, 0.0f, 0.0f);
            this.m_pathLine.transform(m, pathNew);
            m.setRotate(270.0f, 0.0f, this.m_fCompassRadius);
            pathNew.transform(m);
            pathNew.offset(fCenterX, fCenterY - this.m_fCompassRadius);
            canvas.drawPath(pathNew, pPathLetterRed);
        }
        m.setScale(fScale, fScale, 0.0f, 0.0f);
        this.m_pathLetterN.transform(m, pathNew);
        m.setRotate(this.m_fTrueNorth + this.m_Parent.m_fMapRotation, 0.0f, this.m_fCompassRadius);
        pathNew.transform(m);
        pathNew.offset(fCenterX, fCenterY - this.m_fCompassRadius);
        canvas.drawPath(pathNew, pPathLetter);
        m.setScale(fScale, fScale, 0.0f, 0.0f);
        this.m_pathLine.transform(m, pathNew);
        float f = this.m_fTrueNorth;
        m.setRotate((r0 + 90.0f) + this.m_Parent.m_fMapRotation, 0.0f, this.m_fCompassRadius);
        pathNew.transform(m);
        pathNew.offset(fCenterX, fCenterY - this.m_fCompassRadius);
        canvas.drawPath(pathNew, pPathLetter);
        m.setScale(fScale, fScale, 0.0f, 0.0f);
        this.m_pathLine.transform(m, pathNew);
        f = this.m_fTrueNorth;
        m.setRotate((r0 + 180.0f) + this.m_Parent.m_fMapRotation, 0.0f, this.m_fCompassRadius);
        pathNew.transform(m);
        pathNew.offset(fCenterX, fCenterY - this.m_fCompassRadius);
        canvas.drawPath(pathNew, pPathLetter);
        m.setScale(fScale, fScale, 0.0f, 0.0f);
        this.m_pathLine.transform(m, pathNew);
        f = this.m_fTrueNorth;
        m.setRotate((r0 + 270.0f) + this.m_Parent.m_fMapRotation, 0.0f, this.m_fCompassRadius);
        pathNew.transform(m);
        pathNew.offset(fCenterX, fCenterY - this.m_fCompassRadius);
        canvas.drawPath(pathNew, pPathLetter);
        pPathFill.setStyle(Style.FILL);
        pPathFill.setColor(-1593835521);
        pPath.setAntiAlias(true);
        pPath.setStyle(Style.STROKE);
        pPath.setColor(-1593901056);
        pPath.setStrokeWidth((float) (this.m_Parent.m_iDisplayDensity / 50));
        m.setScale(this.m_fCompassRadius / 30.0f, this.m_fCompassRadius / 30.0f, 0.0f, 0.0f);
        this.m_pathCompassArrow.transform(m, pathNew);
        if (this.m_Parent.m_iSettingsCompassType == 0) {
            f = this.m_Parent.m_fCompassValue;
            float f2 = this.m_Parent.m_fCompassOrientationCorrection;
            f2 = this.m_fTrueNorth;
            m.setRotate(((r0 + r0) + r0) + this.m_Parent.m_fMapRotation, 0.0f, 0.0f);
        } else {
            m.setRotate((-this.m_Parent.m_fCompassValue) - this.m_Parent.m_fCompassOrientationCorrection, 0.0f, 0.0f);
        }
        pathNew.transform(m);
        pathNew.offset(fCenterX, fCenterY);
        canvas.drawPath(pathNew, pPathFill);
        canvas.drawPath(pathNew, pPath);
        if (this.m_Parent.m_bSettingsShowCompassHeading) {
            pCircle.setColor(-1136128);
            pCircle.setStyle(Style.FILL);
            pCircle.setAntiAlias(true);
            if (m_sUnitsAngleLength[MMTrackerActivity.m_SettingsUnitsAngles] == 4) {
                canvas.drawCircle(fCenterX, fCenterY, this.m_fCompassRadius / 1.5f, pCircle);
            } else {
                canvas.drawCircle(fCenterX, fCenterY, this.m_fCompassRadius / 2.0f, pCircle);
            }
            pText.setColor(-16777216);
            pText.setTextSize(this.m_fCompassRadius / 2.0f);
            pText.setAntiAlias(true);
            Rect rectHeading = new Rect();
            float fTemp = this.m_Parent.m_fCompassValue + this.m_Parent.m_fCompassOrientationCorrection;
            if (fTemp < 0.0f) {
                fTemp += 360.0f;
            }
            if (Math.round(fTemp) == 360) {
                fTemp = 0.0f;
            }
            String sHeading = CreateAngleString((double) fTemp, false, false);
            pText.getTextBounds("XXXX", 0, sHeading.length(), rectHeading);
            canvas.drawText(sHeading, fCenterX - ((float) (rectHeading.width() / 2)), ((float) (rectHeading.height() / 2)) + fCenterY, pText);
        }
    }

    private void DrawAccuracyCircle(Canvas canvas) {
        Paint paintInnerCircle = new Paint();
        Paint paintOuterCircle = new Paint();
        DoublePoint dp = RotatePoint((double) (this.m_iGpsX - this.m_Parent.m_iDispPosX), (double) (this.m_iGpsY - this.m_Parent.m_iDispPosY), (double) this.m_Parent.m_iRotateCenterX, (double) this.m_Parent.m_iRotateCenterY);
        paintInnerCircle.setColor(MMTrackerActivity.CalcColor(this.m_Parent.m_iSettingsColorTriangulationFill, 1140850688));
        paintInnerCircle.setStyle(Style.FILL);
        paintOuterCircle.setColor(-2013265665);
        paintOuterCircle.setStyle(Style.STROKE);
        paintOuterCircle.setStrokeWidth(3.0f);
        paintOuterCircle.setAntiAlias(true);
        Point p1 = convertScreenToMapCoordinates((float) dp.f0x, (float) dp.f1y);
        Point p2 = convertScreenToMapCoordinates(((float) dp.f0x) + 50.0f, (float) dp.f1y);
        int iRadius = (int) Math.round(((((double) this.m_Parent.m_fGpsAccuracy) / (1000.0d * Tools.calcDistance(this.m_Qct.convertXYtoLatitude(p1.x, p1.y), this.m_Qct.convertXYtoLongitude(p1.x, p1.y), this.m_Qct.convertXYtoLatitude(p2.x, p2.y), this.m_Qct.convertXYtoLongitude(p2.x, p2.y)))) * 50.0d) / 2.0d);
        canvas.drawCircle((float) dp.f0x, (float) dp.f1y, (float) iRadius, paintInnerCircle);
        canvas.drawCircle((float) dp.f0x, (float) dp.f1y, (float) iRadius, paintOuterCircle);
    }

    private String CreateCoordinateStringFromScreenPosition(float x, float y) {
        Point p = convertScreenToMapCoordinates(x, y);
        return CreateCoordinateStringFromLatLon(this.m_Qct.convertXYtoLatitude(p.x, p.y), this.m_Qct.convertXYtoLongitude(p.x, p.y));
    }

    private String CreateCoordinateStringFromLatLon(double latitude, double longitude) {
        StringCoord c;
        if (MMTrackerActivity.m_SettingsPositionType != 5) {
            c = Tools.CoordToString(latitude, longitude, MMTrackerActivity.m_SettingsPositionType, true);
            return c.sLat + "   " + c.sLong;
        } else if (this.m_OsgbTemp.SetLatLonCoord(latitude, longitude, 0.0d)) {
            return this.m_OsgbTemp.GetOsgbAsText();
        } else {
            c = Tools.CoordToString(latitude, longitude, 0, true);
            return c.sLat + "   " + c.sLong;
        }
    }

    private void DrawPositionInfo(Canvas canvas, Waypoint active_wp, Routepoint active_rp, int iType, boolean bTinyShowDistance, boolean bDrawCrosshair, boolean bCopyToClipboard) {
        float fX;
        float fY;
        double dPositionX;
        double dText1X;
        double dText2X;
        Paint paintLine = new Paint();
        Paint paintText = new Paint();
        Paint paintRect = new Paint();
        String sText1 = "";
        String sText2 = "";
        paintLine.setColor(Color.argb(255, 255, 0, 0));
        paintLine.setStyle(Style.STROKE);
        paintLine.setStrokeWidth(1.0f);
        paintText.setColor(Color.argb(255, 0, 0, 0));
        paintText.setTextSize((float) (this.m_Parent.m_iDisplayDensity / 10));
        paintText.setFakeBoldText(true);
        paintText.setAntiAlias(true);
        paintRect.setColor(Color.argb(192, 255, 255, 0));
        paintRect.setAntiAlias(true);
        if (active_wp != null) {
            fX = active_wp.m_fX;
            fY = active_wp.m_fY;
        } else if (active_rp != null) {
            fX = active_rp.m_fX;
            fY = active_rp.m_fY;
        } else {
            return;
        }
        boolean bDrawBearingAndDistance = (iType == TOUCH_MOVE_WAYPOINT && this.m_Parent.m_bGpsFix) || iType == TOUCH_MOVE_ROUTEPOINT || (iType == TOUCH_MOVE_POSITION && this.m_Parent.m_bGpsFix);
        DoublePoint dp = convertMapToScreenCoordinates(fX, fY);
        if (bDrawCrosshair) {
            canvas.drawLine(0.0f, (float) dp.f1y, (float) this.m_iDispSizeX, (float) dp.f1y, paintLine);
            canvas.drawLine((float) dp.f0x, 0.0f, (float) dp.f0x, (float) this.m_iDispSizeY, paintLine);
        } else {
            canvas.drawCircle((float) dp.f0x, (float) dp.f1y, (float) Math.round((float) (this.m_Parent.m_iDisplayDensity / 25)), paintRect);
        }
        String sPosition = CreateCoordinateStringFromScreenPosition((float) dp.f0x, (float) dp.f1y);
        Rect rectPos = new Rect();
        Rect rectText1 = new Rect();
        Rect rectText2 = new Rect();
        paintText.getTextBounds(sPosition, 0, sPosition.length(), rectPos);
        if (bDrawBearingAndDistance) {
            double dBearing;
            int iBearing;
            if ((iType == TOUCH_MOVE_WAYPOINT || iType == TOUCH_MOVE_POSITION) && active_wp != null) {
                dBearing = Tools.CalcBearing(this.m_Parent.m_dGpsLatitude, this.m_Parent.m_dGpsLongitude, active_wp.m_dGpsLat, active_wp.m_dGpsLong);
                if (dBearing < 0.0d) {
                    dBearing += 6.283185307179586d;
                }
                iBearing = (int) Math.round(Math.toDegrees(dBearing));
                if (iBearing >= 360) {
                    iBearing -= 360;
                }
                dBearing = Math.toDegrees(dBearing);
                double dDistanceKm = Tools.calcDistance(this.m_Parent.m_dGpsLatitude, this.m_Parent.m_dGpsLongitude, active_wp.m_dGpsLat, active_wp.m_dGpsLong);
                sText1 = CreateAngleString((double) iBearing, false, true);
                sText2 = String.format("%.2f %s", new Object[]{Double.valueOf(Tools.m_dUnitDistanceFactor[MMTrackerActivity.m_SettingsUnitsDistances] * dDistanceKm), Tools.m_sUnitDistance[MMTrackerActivity.m_SettingsUnitsDistances]});
                paintText.getTextBounds(sText1, 0, sText1.length(), rectText1);
                paintText.getTextBounds(sText2, 0, sText2.length(), rectText2);
            }
            if (iType == TOUCH_MOVE_ROUTEPOINT && active_rp != null) {
                Object[] objArr;
                Routepoint rp1 = null;
                Routepoint rp2 = null;
                int iIndex = active_rp.m_ParentRoute.routepoints.indexOf(active_rp);
                if (iIndex > 0) {
                    rp1 = (Routepoint) active_rp.m_ParentRoute.routepoints.get(iIndex - 1);
                }
                if (iIndex < active_rp.m_ParentRoute.routepoints.size() - 1) {
                    rp2 = (Routepoint) active_rp.m_ParentRoute.routepoints.get(iIndex + 1);
                }
                if (rp1 != null) {
                    dBearing = Tools.CalcBearing(active_rp.m_dGpsLat, active_rp.m_dGpsLong, rp1.m_dGpsLat, rp1.m_dGpsLong);
                    if (dBearing < 0.0d) {
                        dBearing += 6.283185307179586d;
                    }
                    iBearing = (int) Math.round(Math.toDegrees(dBearing));
                    if (iBearing >= 360) {
                        iBearing -= 360;
                    }
                    dBearing = Math.toDegrees(dBearing);
                    objArr = new Object[3];
                    objArr[0] = Double.valueOf(Tools.m_dUnitDistanceFactor[MMTrackerActivity.m_SettingsUnitsDistances] * Tools.calcDistance(active_rp.m_dGpsLat, active_rp.m_dGpsLong, rp1.m_dGpsLat, rp1.m_dGpsLong));
                    objArr[1] = Tools.m_sUnitDistance[MMTrackerActivity.m_SettingsUnitsDistances];
                    objArr[2] = CreateAngleString((double) iBearing, false, true);
                    sText1 = String.format("Leg bef.: %.2f %s , %s", objArr);
                    paintText.getTextBounds(sText1, 0, sText1.length(), rectText1);
                }
                if (rp2 != null) {
                    dBearing = Tools.CalcBearing(active_rp.m_dGpsLat, active_rp.m_dGpsLong, rp2.m_dGpsLat, rp2.m_dGpsLong);
                    if (dBearing < 0.0d) {
                        dBearing += 6.283185307179586d;
                    }
                    iBearing = (int) Math.round(Math.toDegrees(dBearing));
                    if (iBearing >= 360) {
                        iBearing -= 360;
                    }
                    dBearing = Math.toDegrees(dBearing);
                    objArr = new Object[3];
                    objArr[0] = Double.valueOf(Tools.m_dUnitDistanceFactor[MMTrackerActivity.m_SettingsUnitsDistances] * Tools.calcDistance(active_rp.m_dGpsLat, active_rp.m_dGpsLong, rp2.m_dGpsLat, rp2.m_dGpsLong));
                    objArr[1] = Tools.m_sUnitDistance[MMTrackerActivity.m_SettingsUnitsDistances];
                    objArr[2] = CreateAngleString((double) iBearing, false, true);
                    sText2 = String.format("Leg aft.: %.2f %s , %s", objArr);
                    paintText.getTextBounds(sText2, 0, sText2.length(), rectText2);
                }
            }
        }
        double dShowDistanceX = (double) (this.m_Parent.m_iDisplayDensity / 5);
        double dShowDistanceY = (double) (this.m_Parent.m_iDisplayDensity / 5);
        if (bTinyShowDistance) {
            dShowDistanceX /= 5.0d;
        }
        if (iType == TOUCH_MOVE_POSITION) {
            dShowDistanceX = 0.0d;
            dShowDistanceY = 0.0d;
        }
        if (dp.f0x > ((double) (this.m_iDispSizeX / 2))) {
            dp.f0x -= dShowDistanceX;
            dPositionX = dp.f0x - ((double) rectPos.width());
            dText1X = dp.f0x - ((double) rectText1.width());
            dText2X = dp.f0x - ((double) rectText2.width());
        } else {
            dp.f0x += dShowDistanceX;
            dPositionX = dp.f0x;
            dText1X = dp.f0x;
            dText2X = dp.f0x;
        }
        double dPositionY = (dp.f1y - dShowDistanceY) - ((double) (rectPos.height() * 3));
        if (dPositionY - (((double) rectPos.height()) * 1.3d) < ((double) this.m_iWindowLastYPos)) {
            dPositionY = ((double) this.m_iWindowLastYPos) + (((double) rectPos.height()) * 1.3d);
        }
        double dBoxX = Math.min(Math.min(dPositionX, dText1X), dText2X);
        Rect rect = new Rect((int) Math.round(dBoxX), (int) Math.round(dPositionY - (((double) rectPos.height()) * 1.1d)), ((int) Math.round(dBoxX)) + Math.max(Math.max(rectPos.width(), rectText1.width()), rectText2.width()), (int) Math.round(((double) (rectPos.height() * 3)) + dPositionY));
        rect.inset(-3, -3);
        canvas.drawRect(rect, paintRect);
        canvas.drawText(sPosition, (float) dPositionX, (float) dPositionY, paintText);
        if (bCopyToClipboard) {
            ((ClipboardManager) this.m_Parent.getSystemService("clipboard")).setText(sPosition);
        }
        if (bDrawBearingAndDistance) {
            if (iType == TOUCH_MOVE_WAYPOINT || iType == TOUCH_MOVE_POSITION) {
                dPositionY += ((double) rectPos.height()) * 1.4d;
                canvas.drawText(sText1, (float) dText1X, (float) dPositionY, paintText);
                dPositionY += ((double) rectPos.height()) * 1.4d;
                canvas.drawText(sText2, (float) dText2X, (float) dPositionY, paintText);
            }
            if (iType == TOUCH_MOVE_ROUTEPOINT) {
                dPositionY += ((double) rectPos.height()) * 1.4d;
                canvas.drawText(sText1, (float) dText1X, (float) dPositionY, paintText);
                canvas.drawText(sText2, (float) dText2X, (float) (dPositionY + (((double) rectPos.height()) * 1.4d)), paintText);
            }
        }
    }

    private int DrawNavigationWindow(Canvas canvas) {
        NavigationTarget nav_target = MMTrackerActivity.m_NavigationTarget;
        if (nav_target.m_Type == NavigationTarget.TARGET_TYPE_NONE) {
            return this.m_iWindowLastYPos;
        }
        int l_iDispSizeX;
        int l_iDispSizeY;
        double dAngleToNavTarget;
        Paint pPath = new Paint();
        Paint pPathFill = new Paint();
        Paint pText = new Paint();
        Paint pRectFrame = new Paint();
        Paint pRectGray = new Paint();
        Matrix m = new Matrix();
        Path pathNew = new Path();
        Rect rectArrow = new Rect();
        Rect rectText = new Rect();
        int iSetting = this.m_Parent.m_SettingsNaviDisplay;
        long diff_time = 0;
        double dRouteEndDist = 0.0d;
        int l_iWindowLastYPos = this.m_iWindowLastYPos;
        if (this.m_iDispSizeY > this.m_iDispSizeX) {
            l_iDispSizeX = this.m_iDispSizeX;
            l_iDispSizeY = this.m_iDispSizeY;
        } else {
            l_iDispSizeX = this.m_iDispSizeY;
            l_iDispSizeY = this.m_iDispSizeX;
        }
        pPathFill.setStyle(Style.FILL);
        pPathFill.setColor(-805306368 | this.m_Parent.m_iSettingsColorNaviArrowFill);
        pPath.setAntiAlias(true);
        pPath.setStyle(Style.STROKE);
        if (this.m_dArrowLength_km_s >= MMTrackerActivity.ARROW_MIN_SPEED_TO_SHOW / 3600.0d || MMTrackerActivity.m_iSettingsUseCompassForVector != 0) {
            pPath.setColor(this.m_Parent.m_iSettingsColorNaviArrowLine | -16777216);
        } else {
            pPath.setColor(-526344032);
        }
        pPath.setStrokeWidth((float) (this.m_Parent.m_iDisplayDensity / 30));
        pPath.setAntiAlias(true);
        pText.setColor(-16777216);
        pText.setFakeBoldText(true);
        pText.setAntiAlias(true);
        pRectFrame.setColor(-16777216);
        pRectFrame.setStyle(Style.STROKE);
        pRectFrame.setStrokeWidth(1.0f);
        pRectGray.setColor(-808398640);
        pRectGray.setStyle(Style.FILL);
        double dDeltaX = this.m_Qct.convertLongLatToX(nav_target.m_dGpsLong, nav_target.m_dGpsLat) - this.m_Qct.convertLongLatToX(this.m_Parent.m_dGpsLongitude, this.m_Parent.m_dGpsLatitude);
        double dDeltaY = this.m_Qct.convertLongLatToY(nav_target.m_dGpsLong, nav_target.m_dGpsLat) - this.m_Qct.convertLongLatToY(this.m_Parent.m_dGpsLongitude, this.m_Parent.m_dGpsLatitude);
        if (dDeltaX != 0.0d) {
            dAngleToNavTarget = Math.atan(dDeltaY / dDeltaX);
        } else if (dDeltaY < 0.0d) {
            dAngleToNavTarget = -1.5707963267948966d;
        } else {
            dAngleToNavTarget = 1.5707963267948966d;
        }
        if (dDeltaX < 0.0d) {
            dAngleToNavTarget += 3.141592653589793d;
        }
        double dNavAngle = -(Math.toDegrees(this.m_dArrowAngle - dAngleToNavTarget) + ((double) this.m_fTrueNorth));
        float dScale = ((float) l_iDispSizeX) / 300.0f;
        if (iSetting <= 1) {
            dScale /= 2.0f;
        }
        rectArrow.left = 0;
        rectArrow.top = Math.round((float) ((l_iDispSizeY / 300) + l_iWindowLastYPos));
        rectArrow.bottom = Math.round((((float) l_iWindowLastYPos) + (80.0f * dScale)) + ((float) ((l_iDispSizeX / 25) * 2)));
        rectArrow.right = Math.round((80.0f * dScale) + ((float) ((l_iDispSizeX / 25) * 2)));
        rectText.left = rectArrow.right;
        rectText.top = rectArrow.top;
        rectText.bottom = rectArrow.bottom;
        rectText.right = l_iDispSizeX;
        m.setScale(dScale, dScale, 0.0f, 0.0f);
        this.m_pathNaviArrow.transform(m, pathNew);
        m.setRotate((float) dNavAngle, 0.0f, 0.0f);
        pathNew.transform(m);
        pathNew.offset((float) (rectArrow.left + (rectArrow.width() / 2)), (float) (rectArrow.top + (rectArrow.height() / 2)));
        canvas.drawPath(pathNew, pPathFill);
        canvas.drawPath(pathNew, pPath);
        if (iSetting == 1 || iSetting == 3) {
            canvas.drawRect(rectText, pRectGray);
            canvas.drawRect(rectText, pRectFrame);
        }
        canvas.drawRect(rectArrow, pRectFrame);
        if (iSetting == 1 || iSetting == 3) {
            SimpleDateFormat simpleDateFormat;
            StringBuilder stringBuilder;
            double dNextWpETE = 0.0d;
            double dRouteEndETE = 0.0d;
            long lNextWpETA = 0;
            long lRouteEndETA = 0;
            double dNextWpDist = Tools.calcDistance(this.m_Parent.m_dGpsLatitude, this.m_Parent.m_dGpsLongitude, nav_target.m_dGpsLat, nav_target.m_dGpsLong);
            if (nav_target.m_Type == NavigationTarget.TARGET_TYPE_ROUTEPOINT) {
                dRouteEndDist = dNextWpDist + nav_target.m_ParentRoute.CalcLengthKm(nav_target.m_Routepoint);
            }
            if (this.m_Parent.m_NavigationStartTime != null) {
                diff_time = new Date().getTime() - this.m_Parent.m_NavigationStartTime.getTime();
                if (!(this.m_Parent.m_NavigationTotalDist == 0.0d || diff_time == 0)) {
                    double dSpeed = this.m_Parent.m_NavigationTotalDist / ((double) (diff_time / 1000));
                    dRouteEndETE = dRouteEndDist / dSpeed;
                    dNextWpETE = dNextWpDist / dSpeed;
                    lRouteEndETA = System.currentTimeMillis() + Math.round(1000.0d * dRouteEndETE);
                    lNextWpETA = System.currentTimeMillis() + Math.round(1000.0d * dNextWpETE);
                }
            }
            String sNextWpDistText = "";
            String sNextWpETAText = "";
            String sRouteEndDistText = "";
            String sRouteEndETAText = "";
            if (nav_target.m_Type == NavigationTarget.TARGET_TYPE_ROUTEPOINT || iSetting >= 2) {
                pText.setTextSize((float) ((int) Math.round(((double) rectText.height()) / 5.8d)));
            } else {
                pText.setTextSize((float) ((int) Math.round(((double) rectText.height()) / 3.6249999999999996d)));
            }
            if (this.m_Parent.m_bGpsFix) {
                sNextWpDistText = String.format("WP Dist: %.2f %s", new Object[]{Double.valueOf(Tools.m_dUnitDistanceFactor[MMTrackerActivity.m_SettingsUnitsDistances] * dNextWpDist), Tools.m_sUnitDistance[MMTrackerActivity.m_SettingsUnitsDistances]});
                if (this.m_Parent.m_NavigationTotalDist == 0.0d || diff_time == 0 || this.m_Parent.m_NavigationStartTime == null) {
                    if (this.m_Parent.m_bSettingsShowETA) {
                        sNextWpETAText = String.format("WP ETA: --:--:--", new Object[0]);
                    } else {
                        sNextWpETAText = String.format("WP ETE: --:--:--", new Object[0]);
                    }
                } else if (this.m_Parent.m_bSettingsShowETA) {
                    simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                    stringBuilder = new StringBuilder(simpleDateFormat.format(Long.valueOf(lNextWpETA)));
                    sNextWpETAText = String.format("WP ETA: %s", new Object[]{stringBuilder});
                } else {
                    sNextWpETAText = String.format("WP ETE: %02d:%02d:%02d", new Object[]{Integer.valueOf((int) (dNextWpETE / 3600.0d)), Integer.valueOf((int) ((dNextWpETE % 3600.0d) / 60.0d)), Integer.valueOf((int) ((dNextWpETE % 3600.0d) % 60.0d))});
                }
            } else {
                sNextWpDistText = String.format("WP Dist: -.- %s", new Object[]{Tools.m_sUnitDistance[MMTrackerActivity.m_SettingsUnitsDistances]});
                if (this.m_Parent.m_bSettingsShowETA) {
                    sNextWpETAText = String.format("WP ETA: --:--:--", new Object[0]);
                } else {
                    sNextWpETAText = String.format("WP ETE: --:--:--", new Object[0]);
                }
            }
            if (nav_target.m_Type == NavigationTarget.TARGET_TYPE_ROUTEPOINT || iSetting >= 2) {
                canvas.drawText(sNextWpDistText, (float) (rectText.left + (rectText.width() / 15)), (float) (rectText.top + ((rectText.height() / 9) * 2)), pText);
                canvas.drawText(sNextWpETAText, (float) (rectText.left + (rectText.width() / 15)), (float) (rectText.top + ((rectText.height() / 9) * 4)), pText);
            } else {
                canvas.drawText(sNextWpDistText, (float) (rectText.left + (rectText.width() / 15)), (float) (rectText.top + ((rectText.height() / 9) * 4)), pText);
                canvas.drawText(sNextWpETAText, (float) (rectText.left + (rectText.width() / 15)), (float) (rectText.top + ((rectText.height() / 9) * 7)), pText);
            }
            if (nav_target.m_Type == NavigationTarget.TARGET_TYPE_ROUTEPOINT) {
                if (this.m_Parent.m_bGpsFix) {
                    sRouteEndDistText = String.format("Route Len: %.2f %s", new Object[]{Double.valueOf(Tools.m_dUnitDistanceFactor[MMTrackerActivity.m_SettingsUnitsDistances] * dRouteEndDist), Tools.m_sUnitDistance[MMTrackerActivity.m_SettingsUnitsDistances]});
                    if (this.m_Parent.m_NavigationTotalDist == 0.0d || diff_time == 0 || this.m_Parent.m_NavigationStartTime == null) {
                        if (this.m_Parent.m_bSettingsShowETA) {
                            sRouteEndETAText = String.format("Route ETA: --:--:--", new Object[0]);
                        } else {
                            sRouteEndETAText = String.format("Route ETE: --:--:--", new Object[0]);
                        }
                    } else if (this.m_Parent.m_bSettingsShowETA) {
                        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                        stringBuilder = new StringBuilder(simpleDateFormat.format(Long.valueOf(lRouteEndETA)));
                        sRouteEndETAText = String.format("Route ETA: %s", new Object[]{stringBuilder});
                    } else {
                        sRouteEndETAText = String.format("Route ETE: %02d:%02d:%02d", new Object[]{Integer.valueOf((int) (dRouteEndETE / 3600.0d)), Integer.valueOf((int) ((dRouteEndETE % 3600.0d) / 60.0d)), Integer.valueOf((int) ((dRouteEndETE % 3600.0d) % 60.0d))});
                    }
                } else {
                    sRouteEndDistText = String.format("Route Len: -.- %s", new Object[]{Tools.m_sUnitDistance[MMTrackerActivity.m_SettingsUnitsDistances]});
                    if (this.m_Parent.m_bSettingsShowETA) {
                        sRouteEndETAText = String.format("Route ETA: --:--:--", new Object[0]);
                    } else {
                        sRouteEndETAText = String.format("Route ETE: --:--:--", new Object[0]);
                    }
                }
                canvas.drawText(sRouteEndDistText, (float) (rectText.left + (rectText.width() / 15)), (float) (rectText.top + ((rectText.height() / 9) * 6)), pText);
                canvas.drawText(sRouteEndETAText, (float) (rectText.left + (rectText.width() / 15)), (float) (rectText.top + ((rectText.height() / 9) * 8)), pText);
            }
            this.m_RectButtonEndNavigation = new Rect(0, 0, this.m_bmButtonEndNavigationNormal.getWidth(), this.m_bmButtonEndNavigationNormal.getHeight());
            this.m_RectButtonEndNavigation.offsetTo((int) Math.round(((double) rectText.right) - (((double) this.m_bmButtonEndNavigationNormal.getWidth()) * 1.05d)), (int) Math.round(((double) rectText.top) + (((double) this.m_bmButtonEndNavigationNormal.getWidth()) * 0.05d)));
        } else {
            this.m_RectButtonEndNavigation = new Rect(0, 0, this.m_bmButtonEndNavigationNormal.getWidth(), this.m_bmButtonEndNavigationNormal.getHeight());
            this.m_RectButtonEndNavigation.offsetTo((int) Math.round(((double) rectArrow.right) - (((double) this.m_bmButtonEndNavigationNormal.getWidth()) * 1.05d)), (int) Math.round(((double) rectArrow.top) + (((double) this.m_bmButtonEndNavigationNormal.getWidth()) * 0.05d)));
        }
        return rectArrow.bottom;
    }

    private int DrawStatusLine(Canvas canvas, double speed, double height) {
        if (!this.m_Parent.m_SettingsShowStausline) {
            return this.m_iWindowLastYPos;
        }
        String sSpeed;
        String sHeight;
        Paint pText = new Paint();
        Paint pRectGray = new Paint();
        Paint pRectDarkGray = new Paint();
        Paint pRectFrame = new Paint();
        Paint pIcons = new Paint();
        Rect RectV = new Rect();
        Rect RectSpeed = new Rect();
        Rect RectH = new Rect();
        Rect RectHeight = new Rect();
        Rect RectDummy = new Rect();
        Rect RectFirstLine = new Rect();
        int iUnit = MMTrackerActivity.m_SettingsUnitsDistances;
        int l_iWindowLastYPos = this.m_iWindowLastYPos;
        pRectGray.setColor(Color.argb(207, 208, 208, 208));
        pRectGray.setStyle(Style.FILL);
        pRectDarkGray.setColor(Color.argb(207, 144, 144, 144));
        pRectFrame.setColor(Color.argb(255, 0, 0, 0));
        pRectFrame.setStyle(Style.STROKE);
        pRectFrame.setStrokeWidth(1.0f);
        pText.setColor(Color.argb(255, 0, 0, 0));
        pText.setTextSize((float) (this.m_Parent.m_iDisplayDensity / 8));
        pText.setFakeBoldText(true);
        pText.setAntiAlias(true);
        pIcons.setColor(Color.argb(255, 0, 0, 0));
        if (this.m_Parent.m_bGpsFix) {
            sSpeed = String.format("%.1f %s", new Object[]{Double.valueOf(Tools.m_dUnitSpeedFactor[iUnit] * speed), Tools.m_sUnitSpeed[iUnit]});
            sHeight = String.format("%.1f %s", new Object[]{Double.valueOf(Tools.m_dUnitHeightFactor[iUnit] * height), Tools.m_sUnitHeight[iUnit]});
        } else {
            sSpeed = "-.-" + Tools.m_sUnitSpeed[iUnit];
            sHeight = "-.-" + Tools.m_sUnitHeight[iUnit];
        }
        pText.getTextBounds("kp", 0, 2, RectDummy);
        int iTextHeight = RectDummy.height();
        if (iTextHeight < this.m_bmMiniGpsLocked.getHeight()) {
            iTextHeight = this.m_bmMiniGpsLocked.getHeight();
        }
        RectFirstLine.set(0, l_iWindowLastYPos, this.m_iDispSizeX, ((int) Math.round(((double) iTextHeight) * 1.3d)) + l_iWindowLastYPos);
        pText.getTextBounds("vv", 0, 2, RectV);
        RectV.top = RectFirstLine.top;
        RectV.bottom = RectFirstLine.bottom;
        pText.getTextBounds("hh", 0, 2, RectH);
        RectH.top = RectFirstLine.top;
        RectH.bottom = RectFirstLine.bottom;
        pText.getTextBounds(sSpeed, 0, sSpeed.length(), RectSpeed);
        RectSpeed.top = RectFirstLine.top;
        RectSpeed.bottom = RectFirstLine.bottom;
        pText.getTextBounds(sHeight, 0, sHeight.length(), RectHeight);
        RectHeight.top = RectFirstLine.top;
        RectHeight.bottom = RectFirstLine.bottom;
        canvas.drawRect(RectFirstLine, pRectGray);
        canvas.drawRect(RectV, pRectDarkGray);
        String str = "v";
        canvas.drawText(r22, (float) ((int) Math.round(((double) RectV.width()) * 0.25d)), (float) (iTextHeight + l_iWindowLastYPos), pText);
        RectSpeed.offset((int) Math.round(((double) RectV.width()) * 1.1d), 0);
        canvas.drawText(sSpeed, (float) RectSpeed.left, (float) (iTextHeight + l_iWindowLastYPos), pText);
        RectH.offset((int) Math.round(1.1d * ((double) (RectV.width() + RectSpeed.width()))), 0);
        canvas.drawRect(RectH, pRectDarkGray);
        str = "h";
        canvas.drawText(r22, (float) ((int) Math.round(((double) RectH.left) + (((double) RectH.width()) * 0.25d))), (float) (iTextHeight + l_iWindowLastYPos), pText);
        RectHeight.offset((int) Math.round(((double) RectH.left) + (((double) RectH.width()) * 1.1d)), 0);
        canvas.drawText(sHeight, (float) RectHeight.left, (float) (iTextHeight + l_iWindowLastYPos), pText);
        int iLeft = (int) Math.round(((double) this.m_iDispSizeX) - (((double) this.m_bmMiniTrackStopped.getWidth()) * 1.2d));
        if (MMTrackerActivity.m_bTrackRecording) {
            canvas.drawBitmap(this.m_bmMiniTrackRunning, (float) iLeft, (float) (((RectFirstLine.height() - this.m_bmMiniTrackRunning.getHeight()) / 2) + l_iWindowLastYPos), pIcons);
        } else {
            canvas.drawBitmap(this.m_bmMiniTrackStopped, (float) iLeft, (float) (((RectFirstLine.height() - this.m_bmMiniTrackStopped.getHeight()) / 2) + l_iWindowLastYPos), pIcons);
        }
        iLeft = (int) Math.round(((double) iLeft) - (((double) this.m_bmMiniScreenLocked.getWidth()) * 1.2d));
        if (this.m_Parent.m_ScreenLock) {
            canvas.drawBitmap(this.m_bmMiniScreenLocked, (float) iLeft, (float) (((RectFirstLine.height() - this.m_bmMiniScreenLocked.getHeight()) / 2) + l_iWindowLastYPos), pIcons);
        } else {
            canvas.drawBitmap(this.m_bmMiniScreenUnlocked, (float) iLeft, (float) (((RectFirstLine.height() - this.m_bmMiniScreenUnlocked.getHeight()) / 2) + l_iWindowLastYPos), pIcons);
        }
        iLeft = (int) Math.round(((double) iLeft) - (((double) this.m_bmMiniGpsLocked.getWidth()) * 1.2d));
        if (this.m_Parent.m_PositionLock) {
            canvas.drawBitmap(this.m_bmMiniGpsLocked, (float) iLeft, (float) (((RectFirstLine.height() - this.m_bmMiniGpsLocked.getHeight()) / 2) + l_iWindowLastYPos), pIcons);
        } else {
            canvas.drawBitmap(this.m_bmMiniGpsUnlocked, (float) iLeft, (float) (((RectFirstLine.height() - this.m_bmMiniGpsUnlocked.getHeight()) / 2) + l_iWindowLastYPos), pIcons);
        }
        canvas.drawRect(RectFirstLine, pRectFrame);
        return RectFirstLine.bottom;
    }

    private int DrawOSGridPositionLine(Canvas canvas, double dLat, double dLong) {
        String sCoord;
        String str;
        Paint pText = new Paint();
        Paint pRectGray = new Paint();
        Paint pRectDarkGray = new Paint();
        Paint pRectFrame = new Paint();
        Rect RectV = new Rect();
        Rect RectCoord = new Rect();
        Rect RectDummy = new Rect();
        Rect RectFirstLine = new Rect();
        int l_iDispSizeX = this.m_iDispSizeX;
        int l_iDispSizeY = this.m_iDispSizeY;
        int l_iWindowLastYPos = this.m_iWindowLastYPos + Math.round((float) (l_iDispSizeY / 300));
        pRectGray.setColor(Color.argb(207, 208, 208, 208));
        pRectGray.setStyle(Style.FILL);
        pRectDarkGray.setColor(Color.argb(207, 144, 144, 144));
        pRectFrame.setColor(Color.argb(255, 0, 0, 0));
        pRectFrame.setStyle(Style.STROKE);
        pRectFrame.setStrokeWidth(1.0f);
        pText.setColor(Color.argb(255, 0, 0, 0));
        pText.setTextSize((float) (this.m_Parent.m_iDisplayDensity / 8));
        pText.setFakeBoldText(true);
        pText.setAntiAlias(true);
        pText.getTextBounds("kp", 0, 2, RectDummy);
        int iTextHeight = RectDummy.height();
        RectFirstLine.set(0, l_iWindowLastYPos, l_iDispSizeX, ((int) Math.round(((double) iTextHeight) * 1.3d)) + l_iWindowLastYPos);
        pText.getTextBounds("IOSGBI", 0, 6, RectV);
        RectV.top = RectFirstLine.top;
        RectV.bottom = RectFirstLine.bottom;
        canvas.drawRect(RectFirstLine, pRectGray);
        if (!this.m_Parent.m_bGpsFix) {
            if (!this.m_Parent.m_bNetworkFix) {
                sCoord = "- -   - - - - -   - - - - -";
                canvas.drawRect(RectV, pRectDarkGray);
                str = "OSGB";
                canvas.drawText(r17, (float) ((int) Math.round(((double) RectV.width()) * 0.1d)), (float) (iTextHeight + l_iWindowLastYPos), pText);
                RectCoord.offset(Math.round((float) (RectV.right + (l_iDispSizeX / 35))), 0);
                canvas.drawText(sCoord, (float) RectCoord.left, (float) (iTextHeight + l_iWindowLastYPos), pText);
                canvas.drawRect(RectFirstLine, pRectFrame);
                return RectFirstLine.bottom;
            }
        }
        sCoord = this.m_OsgbTemp.GetOsgbAsText();
        canvas.drawRect(RectV, pRectDarkGray);
        str = "OSGB";
        canvas.drawText(r17, (float) ((int) Math.round(((double) RectV.width()) * 0.1d)), (float) (iTextHeight + l_iWindowLastYPos), pText);
        RectCoord.offset(Math.round((float) (RectV.right + (l_iDispSizeX / 35))), 0);
        canvas.drawText(sCoord, (float) RectCoord.left, (float) (iTextHeight + l_iWindowLastYPos), pText);
        canvas.drawRect(RectFirstLine, pRectFrame);
        return RectFirstLine.bottom;
    }

    private int DrawLatLonPositionLine(Canvas canvas, int iType, double dLat, double dLong) {
        Paint pText = new Paint();
        Paint pRectGray = new Paint();
        Paint pRectDarkGray = new Paint();
        Paint pRectFrame = new Paint();
        Rect RectV = new Rect();
        Rect RectLat = new Rect();
        Rect RectH = new Rect();
        Rect RectLong = new Rect();
        Rect RectDummy = new Rect();
        Rect RectFirstLine = new Rect();
        int l_iDispSizeX = this.m_iDispSizeX;
        int l_iWindowLastYPos = this.m_iWindowLastYPos + Math.round((float) (this.m_iDispSizeY / 300));
        pRectGray.setColor(Color.argb(207, 208, 208, 208));
        pRectGray.setStyle(Style.FILL);
        pRectDarkGray.setColor(Color.argb(207, 144, 144, 144));
        pRectFrame.setColor(Color.argb(255, 0, 0, 0));
        pRectFrame.setStyle(Style.STROKE);
        pRectFrame.setStrokeWidth(1.0f);
        pText.setColor(Color.argb(255, 0, 0, 0));
        pText.setTextSize((float) (this.m_Parent.m_iDisplayDensity / 8));
        pText.setFakeBoldText(true);
        pText.setAntiAlias(true);
        boolean z = this.m_Parent.m_bGpsFix || this.m_Parent.m_bNetworkFix;
        StringCoord c = Tools.CoordToString(dLat, dLong, iType, z);
        pText.getTextBounds("kp", 0, 2, RectDummy);
        int iTextHeight = RectDummy.height();
        RectFirstLine.set(0, l_iWindowLastYPos, l_iDispSizeX, ((int) Math.round(((double) iTextHeight) * 1.3d)) + l_iWindowLastYPos);
        pText.getTextBounds("Latn", 0, 4, RectV);
        RectV.top = RectFirstLine.top;
        RectV.bottom = RectFirstLine.bottom;
        pText.getTextBounds("Lonn", 0, 4, RectH);
        RectH.top = RectFirstLine.top;
        RectH.bottom = RectFirstLine.bottom;
        pText.getTextBounds(c.sLat, 0, c.sLat.length(), RectLat);
        RectLat.top = RectFirstLine.top;
        RectLat.bottom = RectFirstLine.bottom;
        pText.getTextBounds(c.sLong, 0, c.sLong.length(), RectLong);
        RectLong.top = RectFirstLine.top;
        RectLong.bottom = RectFirstLine.bottom;
        canvas.drawRect(RectFirstLine, pRectGray);
        canvas.drawRect(RectV, pRectDarkGray);
        canvas.drawText("Lat", (float) ((int) Math.round(((double) RectV.width()) * 0.1d)), (float) (iTextHeight + l_iWindowLastYPos), pText);
        RectLat.offset(Math.round((float) (RectV.right + (l_iDispSizeX / 100))), 0);
        canvas.drawText(c.sLat, (float) RectLat.left, (float) (iTextHeight + l_iWindowLastYPos), pText);
        RectH.offset(Math.round((float) RectLat.right), 0);
        canvas.drawRect(RectH, pRectDarkGray);
        canvas.drawText("Lon", (float) ((int) Math.round(((double) RectH.left) + (((double) RectH.width()) * 0.1d))), (float) (iTextHeight + l_iWindowLastYPos), pText);
        RectLong.offset(Math.round((float) (RectH.right + (l_iDispSizeX / 100))), 0);
        canvas.drawText(c.sLong, (float) RectLong.left, (float) (iTextHeight + l_iWindowLastYPos), pText);
        canvas.drawRect(RectFirstLine, pRectFrame);
        return RectFirstLine.bottom;
    }

    private int DrawHeadingAndBearing(Canvas canvas, double dHeading, double dBearing) {
        boolean z;
        String sHeading;
        String sBearing;
        int iTextHeight;
        String str;
        Paint pText = new Paint();
        Paint pRectGray = new Paint();
        Paint pRectDarkGray = new Paint();
        Paint pRectFrame = new Paint();
        Rect RectV = new Rect();
        Rect RectLat = new Rect();
        Rect RectH = new Rect();
        Rect RectLong = new Rect();
        Rect RectDummy = new Rect();
        Rect RectFirstLine = new Rect();
        int l_iDispSizeX = this.m_iDispSizeX;
        int l_iDispSizeY = this.m_iDispSizeY;
        int l_iWindowLastYPos = this.m_iWindowLastYPos + Math.round((float) (l_iDispSizeY / 300));
        pRectGray.setColor(Color.argb(207, 208, 208, 208));
        pRectGray.setStyle(Style.FILL);
        pRectDarkGray.setColor(Color.argb(207, 144, 144, 144));
        pRectFrame.setColor(Color.argb(255, 0, 0, 0));
        pRectFrame.setStyle(Style.STROKE);
        pRectFrame.setStrokeWidth(1.0f);
        pText.setColor(Color.argb(255, 0, 0, 0));
        pText.setTextSize((float) (this.m_Parent.m_iDisplayDensity / 8));
        pText.setFakeBoldText(true);
        pText.setAntiAlias(true);
        double toDegrees = Math.toDegrees(dHeading);
        if (Double.compare(dHeading, Double.NaN) != 0) {
            if (this.m_Parent.m_bGpsFix) {
                z = false;
                sHeading = CreateAngleString(toDegrees, z, true);
                toDegrees = Math.toDegrees(dBearing);
                if (Double.compare(dBearing, Double.NaN) != 0) {
                    if (this.m_Parent.m_bGpsFix) {
                        z = false;
                        sBearing = CreateAngleString(toDegrees, z, true);
                        pText.getTextBounds("kp", 0, 2, RectDummy);
                        iTextHeight = RectDummy.height();
                        RectFirstLine.set(0, l_iWindowLastYPos, l_iDispSizeX, ((int) Math.round(((double) iTextHeight) * 1.3d)) + l_iWindowLastYPos);
                        pText.getTextBounds("Headinggg", 0, 9, RectV);
                        RectV.top = RectFirstLine.top;
                        RectV.bottom = RectFirstLine.bottom;
                        pText.getTextBounds("Bearinggg", 0, 9, RectH);
                        RectH.top = RectFirstLine.top;
                        RectH.bottom = RectFirstLine.bottom;
                        pText.getTextBounds(new StringBuilder(String.valueOf(sHeading)).append("x").toString(), 0, sHeading.length() + 1, RectLat);
                        RectLat.top = RectFirstLine.top;
                        RectLat.bottom = RectFirstLine.bottom;
                        pText.getTextBounds(new StringBuilder(String.valueOf(sBearing)).append("x").toString(), 0, sBearing.length() + 1, RectLong);
                        RectLong.top = RectFirstLine.top;
                        RectLong.bottom = RectFirstLine.bottom;
                        canvas.drawRect(RectFirstLine, pRectGray);
                        canvas.drawRect(RectV, pRectDarkGray);
                        str = "Heading";
                        canvas.drawText(r21, (float) ((int) Math.round(((double) RectV.width()) * 0.1d)), (float) (iTextHeight + l_iWindowLastYPos), pText);
                        RectLat.offset(Math.round((float) (RectV.right + (l_iDispSizeX / 100))), 0);
                        canvas.drawText(sHeading, (float) RectLat.left, (float) (iTextHeight + l_iWindowLastYPos), pText);
                        RectH.offset(Math.round((float) RectLat.right), 0);
                        canvas.drawRect(RectH, pRectDarkGray);
                        str = "Bearing";
                        canvas.drawText(r21, (float) ((int) Math.round(((double) RectH.left) + (((double) RectH.width()) * 0.1d))), (float) (iTextHeight + l_iWindowLastYPos), pText);
                        RectLong.offset(Math.round((float) (RectH.right + (l_iDispSizeX / 100))), 0);
                        canvas.drawText(sBearing, (float) RectLong.left, (float) (iTextHeight + l_iWindowLastYPos), pText);
                        canvas.drawRect(RectFirstLine, pRectFrame);
                        return RectFirstLine.bottom;
                    }
                }
                z = true;
                sBearing = CreateAngleString(toDegrees, z, true);
                pText.getTextBounds("kp", 0, 2, RectDummy);
                iTextHeight = RectDummy.height();
                RectFirstLine.set(0, l_iWindowLastYPos, l_iDispSizeX, ((int) Math.round(((double) iTextHeight) * 1.3d)) + l_iWindowLastYPos);
                pText.getTextBounds("Headinggg", 0, 9, RectV);
                RectV.top = RectFirstLine.top;
                RectV.bottom = RectFirstLine.bottom;
                pText.getTextBounds("Bearinggg", 0, 9, RectH);
                RectH.top = RectFirstLine.top;
                RectH.bottom = RectFirstLine.bottom;
                pText.getTextBounds(new StringBuilder(String.valueOf(sHeading)).append("x").toString(), 0, sHeading.length() + 1, RectLat);
                RectLat.top = RectFirstLine.top;
                RectLat.bottom = RectFirstLine.bottom;
                pText.getTextBounds(new StringBuilder(String.valueOf(sBearing)).append("x").toString(), 0, sBearing.length() + 1, RectLong);
                RectLong.top = RectFirstLine.top;
                RectLong.bottom = RectFirstLine.bottom;
                canvas.drawRect(RectFirstLine, pRectGray);
                canvas.drawRect(RectV, pRectDarkGray);
                str = "Heading";
                canvas.drawText(r21, (float) ((int) Math.round(((double) RectV.width()) * 0.1d)), (float) (iTextHeight + l_iWindowLastYPos), pText);
                RectLat.offset(Math.round((float) (RectV.right + (l_iDispSizeX / 100))), 0);
                canvas.drawText(sHeading, (float) RectLat.left, (float) (iTextHeight + l_iWindowLastYPos), pText);
                RectH.offset(Math.round((float) RectLat.right), 0);
                canvas.drawRect(RectH, pRectDarkGray);
                str = "Bearing";
                canvas.drawText(r21, (float) ((int) Math.round(((double) RectH.left) + (((double) RectH.width()) * 0.1d))), (float) (iTextHeight + l_iWindowLastYPos), pText);
                RectLong.offset(Math.round((float) (RectH.right + (l_iDispSizeX / 100))), 0);
                canvas.drawText(sBearing, (float) RectLong.left, (float) (iTextHeight + l_iWindowLastYPos), pText);
                canvas.drawRect(RectFirstLine, pRectFrame);
                return RectFirstLine.bottom;
            }
        }
        z = true;
        sHeading = CreateAngleString(toDegrees, z, true);
        toDegrees = Math.toDegrees(dBearing);
        if (Double.compare(dBearing, Double.NaN) != 0) {
            if (this.m_Parent.m_bGpsFix) {
                z = false;
                sBearing = CreateAngleString(toDegrees, z, true);
                pText.getTextBounds("kp", 0, 2, RectDummy);
                iTextHeight = RectDummy.height();
                RectFirstLine.set(0, l_iWindowLastYPos, l_iDispSizeX, ((int) Math.round(((double) iTextHeight) * 1.3d)) + l_iWindowLastYPos);
                pText.getTextBounds("Headinggg", 0, 9, RectV);
                RectV.top = RectFirstLine.top;
                RectV.bottom = RectFirstLine.bottom;
                pText.getTextBounds("Bearinggg", 0, 9, RectH);
                RectH.top = RectFirstLine.top;
                RectH.bottom = RectFirstLine.bottom;
                pText.getTextBounds(new StringBuilder(String.valueOf(sHeading)).append("x").toString(), 0, sHeading.length() + 1, RectLat);
                RectLat.top = RectFirstLine.top;
                RectLat.bottom = RectFirstLine.bottom;
                pText.getTextBounds(new StringBuilder(String.valueOf(sBearing)).append("x").toString(), 0, sBearing.length() + 1, RectLong);
                RectLong.top = RectFirstLine.top;
                RectLong.bottom = RectFirstLine.bottom;
                canvas.drawRect(RectFirstLine, pRectGray);
                canvas.drawRect(RectV, pRectDarkGray);
                str = "Heading";
                canvas.drawText(r21, (float) ((int) Math.round(((double) RectV.width()) * 0.1d)), (float) (iTextHeight + l_iWindowLastYPos), pText);
                RectLat.offset(Math.round((float) (RectV.right + (l_iDispSizeX / 100))), 0);
                canvas.drawText(sHeading, (float) RectLat.left, (float) (iTextHeight + l_iWindowLastYPos), pText);
                RectH.offset(Math.round((float) RectLat.right), 0);
                canvas.drawRect(RectH, pRectDarkGray);
                str = "Bearing";
                canvas.drawText(r21, (float) ((int) Math.round(((double) RectH.left) + (((double) RectH.width()) * 0.1d))), (float) (iTextHeight + l_iWindowLastYPos), pText);
                RectLong.offset(Math.round((float) (RectH.right + (l_iDispSizeX / 100))), 0);
                canvas.drawText(sBearing, (float) RectLong.left, (float) (iTextHeight + l_iWindowLastYPos), pText);
                canvas.drawRect(RectFirstLine, pRectFrame);
                return RectFirstLine.bottom;
            }
        }
        z = true;
        sBearing = CreateAngleString(toDegrees, z, true);
        pText.getTextBounds("kp", 0, 2, RectDummy);
        iTextHeight = RectDummy.height();
        RectFirstLine.set(0, l_iWindowLastYPos, l_iDispSizeX, ((int) Math.round(((double) iTextHeight) * 1.3d)) + l_iWindowLastYPos);
        pText.getTextBounds("Headinggg", 0, 9, RectV);
        RectV.top = RectFirstLine.top;
        RectV.bottom = RectFirstLine.bottom;
        pText.getTextBounds("Bearinggg", 0, 9, RectH);
        RectH.top = RectFirstLine.top;
        RectH.bottom = RectFirstLine.bottom;
        pText.getTextBounds(new StringBuilder(String.valueOf(sHeading)).append("x").toString(), 0, sHeading.length() + 1, RectLat);
        RectLat.top = RectFirstLine.top;
        RectLat.bottom = RectFirstLine.bottom;
        pText.getTextBounds(new StringBuilder(String.valueOf(sBearing)).append("x").toString(), 0, sBearing.length() + 1, RectLong);
        RectLong.top = RectFirstLine.top;
        RectLong.bottom = RectFirstLine.bottom;
        canvas.drawRect(RectFirstLine, pRectGray);
        canvas.drawRect(RectV, pRectDarkGray);
        str = "Heading";
        canvas.drawText(r21, (float) ((int) Math.round(((double) RectV.width()) * 0.1d)), (float) (iTextHeight + l_iWindowLastYPos), pText);
        RectLat.offset(Math.round((float) (RectV.right + (l_iDispSizeX / 100))), 0);
        canvas.drawText(sHeading, (float) RectLat.left, (float) (iTextHeight + l_iWindowLastYPos), pText);
        RectH.offset(Math.round((float) RectLat.right), 0);
        canvas.drawRect(RectH, pRectDarkGray);
        str = "Bearing";
        canvas.drawText(r21, (float) ((int) Math.round(((double) RectH.left) + (((double) RectH.width()) * 0.1d))), (float) (iTextHeight + l_iWindowLastYPos), pText);
        RectLong.offset(Math.round((float) (RectH.right + (l_iDispSizeX / 100))), 0);
        canvas.drawText(sBearing, (float) RectLong.left, (float) (iTextHeight + l_iWindowLastYPos), pText);
        canvas.drawRect(RectFirstLine, pRectFrame);
        return RectFirstLine.bottom;
    }

    private int DrawTrackAndRouteInfoLine(Canvas canvas, Track track, Route route) {
        String sLength;
        String sTime;
        Paint pText = new Paint();
        Paint pRectGray = new Paint();
        Paint pRectDarkGray = new Paint();
        Paint pRectFrame = new Paint();
        Rect RectV = new Rect();
        Rect RectLength = new Rect();
        Rect RectH = new Rect();
        Rect RectTime = new Rect();
        Rect RectDummy = new Rect();
        Rect RectText = new Rect();
        Rect RectFirstLine = new Rect();
        int l_iDispSizeX = this.m_iDispSizeX;
        int l_iDispSizeY = this.m_iDispSizeY;
        int l_iWindowLastYPos = this.m_iWindowLastYPos + Math.round((float) (l_iDispSizeY / 300));
        int iUnit = MMTrackerActivity.m_SettingsUnitsDistances;
        pRectGray.setColor(Color.argb(207, 208, 208, 208));
        pRectGray.setStyle(Style.FILL);
        pRectDarkGray.setColor(Color.argb(207, 144, 144, 144));
        pRectFrame.setColor(Color.argb(255, 0, 0, 0));
        pRectFrame.setStyle(Style.STROKE);
        pRectFrame.setStrokeWidth(1.0f);
        pText.setColor(Color.argb(255, 0, 0, 0));
        pText.setTextSize((float) (this.m_Parent.m_iDisplayDensity / 8));
        pText.setFakeBoldText(true);
        pText.setAntiAlias(true);
        double dLength;
        if (track != null) {
            dLength = track.CalcLengthKm();
            sLength = String.format("%.1f %s", new Object[]{Double.valueOf(Tools.m_dUnitDistanceFactor[iUnit] * dLength), Tools.m_sUnitDistance[iUnit]});
            if (track.m_AllDetailsAvailable) {
                long lTime = track.CalcTotalTime();
                sTime = String.format("%d:%02d:%02d", new Object[]{Long.valueOf(lTime / 3600), Long.valueOf((lTime % 3600) / 60), Long.valueOf((lTime % 3600) % 60)});
            } else {
                sTime = "--:--:--";
            }
        } else {
            if (route != null) {
                if (route.routepoints.size() > 1) {
                    dLength = route.CalcLengthKm(route.get(0));
                    sLength = String.format("%.1f %s", new Object[]{Double.valueOf(Tools.m_dUnitDistanceFactor[iUnit] * dLength), Tools.m_sUnitDistance[iUnit]});
                    sTime = "--:--:--";
                }
            }
            sLength = String.format("-.- %s", new Object[]{Tools.m_sUnitDistance[iUnit]});
            sTime = "--:--:--";
        }
        pText.getTextBounds("kp", 0, 2, RectDummy);
        int iTextHeight = RectDummy.height();
        RectFirstLine.set(0, l_iWindowLastYPos, l_iDispSizeX, ((int) Math.round(((double) iTextHeight) * 1.3d)) + l_iWindowLastYPos);
        pText.getTextBounds("Lengthx", 0, 7, RectV);
        RectV.top = RectFirstLine.top;
        RectV.bottom = RectFirstLine.bottom;
        pText.getTextBounds("Timex", 0, 5, RectH);
        RectH.top = RectFirstLine.top;
        RectH.bottom = RectFirstLine.bottom;
        pText.getTextBounds(sLength, 0, sLength.length(), RectLength);
        RectLength.top = RectFirstLine.top;
        RectLength.bottom = RectFirstLine.bottom;
        pText.getTextBounds(sTime, 0, sTime.length(), RectTime);
        RectTime.top = RectFirstLine.top;
        RectTime.bottom = RectFirstLine.bottom;
        canvas.drawRect(RectFirstLine, pRectGray);
        canvas.drawRect(RectV, pRectDarkGray);
        String str = "Length";
        canvas.drawText(r27, (float) ((int) Math.round(((double) RectV.width()) * 0.1d)), (float) (iTextHeight + l_iWindowLastYPos), pText);
        RectLength.offset(Math.round((float) (RectV.right + (l_iDispSizeX / 100))), 0);
        canvas.drawText(sLength, (float) RectLength.left, (float) (iTextHeight + l_iWindowLastYPos), pText);
        pText.getTextBounds("0000.0 nmi", 0, 10, RectText);
        RectH.offset(Math.round((float) ((RectText.width() + RectV.width()) + (l_iDispSizeX / 50))), 0);
        canvas.drawRect(RectH, pRectDarkGray);
        str = "Time";
        canvas.drawText(r27, (float) ((int) Math.round(((double) RectH.left) + (((double) RectH.width()) * 0.1d))), (float) (iTextHeight + l_iWindowLastYPos), pText);
        RectTime.offset(Math.round((float) (RectH.right + (l_iDispSizeX / 100))), 0);
        canvas.drawText(sTime, (float) RectTime.left, (float) (iTextHeight + l_iWindowLastYPos), pText);
        canvas.drawRect(RectFirstLine, pRectFrame);
        return RectFirstLine.bottom;
    }

    private void DrawScale(Canvas canvas) {
        Paint pText = new Paint();
        Paint pRectWhite = new Paint();
        Paint pRectBlack = new Paint();
        Paint pRectFrame = new Paint();
        Rect aRect = new Rect();
        Rect bRect = new Rect();
        Rect cRect = new Rect();
        int iUnit = MMTrackerActivity.m_SettingsUnitsDistances;
        int iShiftForCompass = 0;
        pText.setColor(Color.argb(255, 0, 0, 0));
        pText.setTextSize((float) (this.m_Parent.m_iDisplayDensity / 10));
        pText.setFakeBoldText(true);
        pText.setAntiAlias(true);
        pRectWhite.setColor(Color.argb(255, 208, 208, 208));
        pRectBlack.setColor(Color.argb(255, 0, 0, 0));
        pRectFrame.setColor(Color.argb(255, 0, 0, 0));
        pRectFrame.setStyle(Style.STROKE);
        pRectFrame.setStrokeWidth(1.0f);
        if (MMTrackerActivity.m_bSettingsShowCompass) {
            if (this.m_iDispSizeY > this.m_iDispSizeX) {
                iShiftForCompass = (int) Math.round(((double) this.m_fCompassRadius) * 2.6d);
            } else {
                iShiftForCompass = (int) Math.round(((double) this.m_fCompassRadius) * 5.6d);
            }
        }
        int iMeasurementSize = ((this.m_iDispSizeX * 9) / 10) - iShiftForCompass;
        Point p1 = convertScreenToMapCoordinates(0.0f, (float) this.m_iDispSizeY);
        Point p2 = convertScreenToMapCoordinates((float) iMeasurementSize, (float) this.m_iDispSizeY);
        double dist = (Tools.calcDistance(this.m_Qct.convertXYtoLatitude(p1.x, p1.y), this.m_Qct.convertXYtoLongitude(p1.x, p1.y), this.m_Qct.convertXYtoLatitude(p2.x, p2.y), this.m_Qct.convertXYtoLongitude(p2.x, p2.y)) * Tools.m_dUnitDistanceFactor[iUnit]) * 100000.0d;
        int iUnitIndex = 0;
        while (Math.round(m_dUnitsFactor[iUnit][iUnitIndex] * dist) >= 1000 && iUnitIndex < 7) {
            iUnitIndex++;
            dist /= 10.0d;
        }
        double dDecimate = 1.0d;
        double dist_old = dist;
        while (dist / dDecimate > 10.0d) {
            dDecimate *= 10.0d;
        }
        int iBlockCount = (int) (dist / dDecimate);
        dist = (double) Math.round(((double) iBlockCount) * dDecimate);
        if (iBlockCount > 0) {
            String sDist;
            if (iBlockCount <= 2) {
                iBlockCount = 4;
            }
            if (iBlockCount > 9) {
                iBlockCount = 4;
            }
            int iBlockWidth = (int) Math.round(((((double) iMeasurementSize) * dist) / dist_old) / ((double) iBlockCount));
            int iX = (this.m_iDispSizeX / 20) + iShiftForCompass;
            aRect.top = ((this.m_iDispSizeY * 19) / 20) + (this.m_iDispSizeY / 100);
            aRect.bottom = ((this.m_iDispSizeY * 39) / 40) + (this.m_iDispSizeY / 100);
            for (int i = 0; i < iBlockCount; i++) {
                aRect.left = iX;
                aRect.right = aRect.left + iBlockWidth;
                if (i % 2 == 0) {
                    canvas.drawRect(aRect, pRectBlack);
                } else {
                    canvas.drawRect(aRect, pRectWhite);
                }
                iX += iBlockWidth;
            }
            if (m_dUnitsFactor[iUnit][iUnitIndex] >= 1.0d) {
                sDist = String.format("%d %s", new Object[]{Long.valueOf(Math.round(m_dUnitsFactor[iUnit][iUnitIndex] * dist)), m_sUnitsName[iUnit][iUnitIndex]});
            } else {
                sDist = String.format("%.1f %s", new Object[]{Double.valueOf(((double) Math.round(dist)) * m_dUnitsFactor[iUnit][iUnitIndex]), m_sUnitsName[iUnit][iUnitIndex]});
            }
            pText.getTextBounds(sDist, 0, sDist.length(), cRect);
            bRect.left = (this.m_iDispSizeX / 20) + iShiftForCompass;
            bRect.right = aRect.right;
            bRect.bottom = ((this.m_iDispSizeY * 19) / 20) + (this.m_iDispSizeY / 100);
            bRect.top = (bRect.bottom - cRect.height()) - (this.m_iDispSizeY / 75);
            cRect.set(bRect);
            canvas.drawRect(bRect, pRectWhite);
            canvas.drawText(sDist, (float) ((this.m_iDispSizeX / 18) + iShiftForCompass), (float) ((this.m_iDispSizeY * 19) / 20), pText);
            canvas.drawRect(bRect, pRectFrame);
            bRect.bottom = aRect.bottom;
            canvas.drawRect(bRect, pRectFrame);
        }
    }

    private Rect CalcMaxRectFromScreen() {
        Point p1 = convertScreenToMapCoordinates(0.0f, 0.0f);
        Point p2 = convertScreenToMapCoordinates((float) this.m_iDispSizeX, (float) this.m_iDispSizeY);
        Point p3 = convertScreenToMapCoordinates(0.0f, (float) this.m_iDispSizeY);
        Point p4 = convertScreenToMapCoordinates((float) this.m_iDispSizeX, 0.0f);
        int x_min = Math.min(Math.min(Math.min(p1.x, p2.x), p3.x), p4.x);
        int x_max = Math.max(Math.max(Math.max(p1.x, p2.x), p3.x), p4.x);
        int y_min = Math.min(Math.min(Math.min(p1.y, p2.y), p3.y), p4.y);
        return this.m_Qct.getLongLatRect(x_min, Math.max(Math.max(Math.max(p1.y, p2.y), p3.y), p4.y), x_max, y_min);
    }

    private void DrawAllTracks(Canvas canvas, double dZoom, Rect MapRect) {
        Paint pTrack = new Paint();
        Paint pTrackSelected1 = new Paint();
        Paint pTrackSelected2 = new Paint();
        float fX = 0.0f;
        float fY = 0.0f;
        float[] pts = null;
        int iOldTrackCount = -1;
        pTrack.setStyle(Style.FILL);
        pTrack.setStrokeMiter(1.0f);
        pTrack.setAntiAlias(true);
        pTrackSelected1.setStyle(Style.FILL);
        pTrackSelected1.setStrokeMiter(1.0f);
        pTrackSelected1.setAntiAlias(true);
        pTrackSelected2.setStyle(Style.FILL);
        pTrackSelected2.setStrokeMiter(1.0f);
        pTrackSelected2.setAntiAlias(true);
        int l_iDispPosX = this.m_Parent.m_iDispPosX;
        int l_iDispPosY = this.m_Parent.m_iDispPosY;
        int l_iPixelResolution = MMTrackerActivity.MIN_TRACK_RESOLUTION_PIXEL;
        if (MMTrackerActivity.m_bTracksLoaded) {
            Iterator it = MMTrackerActivity.tracks.iterator();
            while (it.hasNext()) {
                Track track = (Track) it.next();
                int iDistX = 9999;
                int iDistY = 9999;
                if (track != null && Rect.intersects(track.m_Areal, MapRect) && track.m_bVisible) {
                    if (track.m_bActive) {
                        track.RefreshXY(this.m_Qct);
                        track.m_bCacheVaild = false;
                    }
                    pTrack.setColor(track.m_iColor);
                    pTrack.setStrokeWidth(track.m_fWidth);
                    int iIndex = 0;
                    int iTrackCount;
                    DoublePoint dp;
                    if (track.m_bCacheVaild) {
                        iTrackCount = track.chachepoints.size();
                        if (iTrackCount > 1) {
                            if (iTrackCount > iOldTrackCount) {
                                pts = null;
                                pts = new float[(iTrackCount * 4)];
                                iOldTrackCount = iTrackCount;
                            }
                            Iterator it2 = track.chachepoints.iterator();
                            while (it2.hasNext()) {
                                TrackpointCache tpc = (TrackpointCache) it2.next();
                                fX = (float) Math.round(tpc.m_fX - ((float) l_iDispPosX));
                                fY = (float) Math.round(tpc.m_fY - ((float) l_iDispPosY));
                                if (MMTrackerActivity.m_iSettingsMapRotation != 0) {
                                    dp = RotatePoint((double) fX, (double) fY, (double) this.m_Parent.m_iRotateCenterX, (double) this.m_Parent.m_iRotateCenterY);
                                    fX = (float) dp.f0x;
                                    fY = (float) dp.f1y;
                                }
                                pts[(iIndex * 4) + 0] = fX;
                                pts[(iIndex * 4) + 1] = fY;
                                if (iIndex > 0) {
                                    pts[((iIndex - 1) * 4) + 2] = pts[(iIndex * 4) + 0];
                                    pts[((iIndex - 1) * 4) + 3] = pts[(iIndex * 4) + 1];
                                }
                                iIndex++;
                            }
                            pts[((iIndex - 1) * 4) + 2] = (float) (Math.round(((double) fX) * dZoom) - ((long) l_iDispPosX));
                            pts[((iIndex - 1) * 4) + 3] = (float) (Math.round(((double) fY) * dZoom) - ((long) l_iDispPosY));
                            if (MMTrackerActivity.m_SelectedTrack == track) {
                                pTrackSelected1.setColor(-1);
                                pTrackSelected1.setStrokeWidth(track.m_fWidth + 4.0f);
                                pTrackSelected2.setColor(-16777216);
                                pTrackSelected2.setStrokeWidth(track.m_fWidth + 8.0f);
                                canvas.drawLines(pts, 0, (iTrackCount - 1) * 4, pTrackSelected2);
                                canvas.drawLines(pts, 0, (iTrackCount - 1) * 4, pTrackSelected1);
                            }
                            canvas.drawLines(pts, 0, (iTrackCount - 1) * 4, pTrack);
                        }
                    } else {
                        iTrackCount = track.GetCount();
                        if (iTrackCount <= 1) {
                            continue;
                        } else {
                            Trackpoint t;
                            if (iTrackCount > iOldTrackCount) {
                                pts = null;
                                pts = new float[(iTrackCount * 4)];
                                iOldTrackCount = iTrackCount;
                            }
                            track.chachepoints.clear();
                            int i = 0;
                            while (i < iTrackCount) {
                                t = track.get(i);
                                if (t != null) {
                                    float fXraw = (float) (((double) t.m_fX) * dZoom);
                                    float fYraw = (float) (((double) t.m_fY) * dZoom);
                                    fX = (float) Math.round(fXraw - ((float) l_iDispPosX));
                                    fY = (float) Math.round(fYraw - ((float) l_iDispPosY));
                                    if (MMTrackerActivity.m_iSettingsMapRotation != 0) {
                                        dp = RotatePoint((double) fX, (double) fY, (double) this.m_Parent.m_iRotateCenterX, (double) this.m_Parent.m_iRotateCenterY);
                                        fX = (float) dp.f0x;
                                        fY = (float) dp.f1y;
                                    }
                                    if (iIndex > 0) {
                                        iDistX = (int) Math.abs(pts[((iIndex - 1) * 4) + 0] - fX);
                                        iDistY = (int) Math.abs(pts[((iIndex - 1) * 4) + 1] - fY);
                                    }
                                    if (iDistX > l_iPixelResolution || iDistY > l_iPixelResolution) {
                                        pts[(iIndex * 4) + 0] = fX;
                                        pts[(iIndex * 4) + 1] = fY;
                                        if (i > 0) {
                                            pts[((iIndex - 1) * 4) + 2] = pts[(iIndex * 4) + 0];
                                            pts[((iIndex - 1) * 4) + 3] = pts[(iIndex * 4) + 1];
                                        }
                                        iIndex++;
                                        track.chachepoints.add(new TrackpointCache(fXraw, fYraw));
                                    }
                                    i++;
                                } else {
                                    return;
                                }
                            }
                            t = track.get(iTrackCount - 1);
                            if (t != null) {
                                fX = (float) (Math.round(((double) t.m_fX) * dZoom) - ((long) l_iDispPosX));
                                fY = (float) (Math.round(((double) t.m_fY) * dZoom) - ((long) l_iDispPosY));
                                if (MMTrackerActivity.m_iSettingsMapRotation != 0) {
                                    dp = RotatePoint((double) fX, (double) fY, (double) this.m_Parent.m_iRotateCenterX, (double) this.m_Parent.m_iRotateCenterY);
                                    fX = (float) dp.f0x;
                                    fY = (float) dp.f1y;
                                }
                                pts[((iIndex - 1) * 4) + 2] = fX;
                                pts[((iIndex - 1) * 4) + 3] = fY;
                            }
                            if (MMTrackerActivity.m_SelectedTrack == track) {
                                pTrackSelected1.setColor(-1);
                                pTrackSelected1.setStrokeWidth(track.m_fWidth + 4.0f);
                                pTrackSelected2.setColor(-16777216);
                                pTrackSelected2.setStrokeWidth(track.m_fWidth + 8.0f);
                                canvas.drawLines(pts, 0, (iTrackCount - 1) * 4, pTrackSelected2);
                                canvas.drawLines(pts, 0, (iTrackCount - 1) * 4, pTrackSelected1);
                            }
                            canvas.drawLines(pts, 0, (iIndex - 1) * 4, pTrack);
                            track.m_bCacheVaild = true;
                        }
                    }
                }
            }
        }
    }

    private void DrawAllRoutes(Canvas canvas, double dZoom, Rect MapRect) {
        Paint pRoute = new Paint();
        Paint pCircle = new Paint();
        Paint pCircleInner = new Paint();
        Paint pCircleTarget = new Paint();
        Paint pCircleSelected1 = new Paint();
        Paint pCircleSelected2 = new Paint();
        Paint pRouteSelected1 = new Paint();
        Paint pRouteSelected2 = new Paint();
        Paint pCross = new Paint();
        float fX = 0.0f;
        float fY = 0.0f;
        Matrix m = new Matrix();
        Path pathNew = new Path();
        NavigationTarget nav_target = MMTrackerActivity.m_NavigationTarget;
        pRoute.setStyle(Style.FILL);
        pRoute.setStrokeMiter(1.0f);
        pRoute.setAntiAlias(true);
        pCircle.setStyle(Style.FILL);
        pCircle.setAntiAlias(true);
        pCircleSelected1.setStyle(Style.FILL);
        pCircleSelected1.setAntiAlias(true);
        pCircleSelected1.setColor(-1);
        pCircleSelected2.setStyle(Style.FILL);
        pCircleSelected2.setAntiAlias(true);
        pCircleSelected2.setColor(-16777216);
        pCircleInner.setStyle(Style.FILL);
        pCircleInner.setAntiAlias(true);
        pCircleInner.setColor(-1);
        pCircleTarget.setColor(-16711681);
        pCircleTarget.setStyle(Style.FILL);
        pCircleTarget.setAntiAlias(true);
        pRouteSelected1.setStyle(Style.FILL);
        pRouteSelected1.setStrokeMiter(1.0f);
        pRouteSelected1.setAntiAlias(true);
        pRouteSelected2.setStyle(Style.FILL);
        pRouteSelected2.setStrokeMiter(1.0f);
        pRouteSelected2.setAntiAlias(true);
        pCircleSelected2.setStyle(Style.FILL);
        pCross.setAntiAlias(true);
        pCross.setStrokeWidth(2.0f);
        pCross.setColor(-16777216);
        float[] pts = null;
        int iTrackCount = 0;
        int l_iDispPosX = this.m_Parent.m_iDispPosX;
        int l_iDispPosY = this.m_Parent.m_iDispPosY;
        if (MMTrackerActivity.m_bTracksLoaded) {
            Iterator it = MMTrackerActivity.routes.iterator();
            while (it.hasNext()) {
                Route route = (Route) it.next();
                if (route != null && Rect.intersects(route.m_Areal, MapRect) && route.m_bVisible) {
                    int i;
                    pRoute.setColor(route.m_iColor);
                    pCircle.setColor(route.m_iColor);
                    pRoute.setStrokeWidth(route.m_fWidth);
                    int iIndex = 0;
                    DoublePoint dp;
                    if (route.m_bCacheVaild) {
                        iTrackCount = route.chachepoints.size();
                        if (iTrackCount >= 1) {
                            pts = new float[(iTrackCount * 4)];
                            Iterator it2 = route.chachepoints.iterator();
                            while (it2.hasNext()) {
                                RoutepointCache rpc = (RoutepointCache) it2.next();
                                fX = (float) Math.round(rpc.m_fX - ((float) l_iDispPosX));
                                fY = (float) Math.round(rpc.m_fY - ((float) l_iDispPosY));
                                if (MMTrackerActivity.m_iSettingsMapRotation != 0) {
                                    dp = RotatePoint((double) fX, (double) fY, (double) this.m_Parent.m_iRotateCenterX, (double) this.m_Parent.m_iRotateCenterY);
                                    fX = (float) dp.f0x;
                                    fY = (float) dp.f1y;
                                }
                                pts[(iIndex * 4) + 0] = fX;
                                pts[(iIndex * 4) + 1] = fY;
                                if (iIndex > 0) {
                                    pts[((iIndex - 1) * 4) + 2] = pts[(iIndex * 4) + 0];
                                    pts[((iIndex - 1) * 4) + 3] = pts[(iIndex * 4) + 1];
                                }
                                iIndex++;
                            }
                            pts[((iIndex - 1) * 4) + 2] = (float) (Math.round(((double) fX) * dZoom) - ((long) l_iDispPosX));
                            pts[((iIndex - 1) * 4) + 3] = (float) (Math.round(((double) fY) * dZoom) - ((long) l_iDispPosY));
                            if (MMTrackerActivity.m_SelectedRoutepoint != null && MMTrackerActivity.m_SelectedRoutepoint.m_ParentRoute == route) {
                                pRouteSelected1.setColor(-1);
                                pRouteSelected1.setStrokeWidth(route.m_fWidth + 4.0f);
                                pRouteSelected2.setColor(-16777216);
                                pRouteSelected2.setStrokeWidth(route.m_fWidth + 8.0f);
                                canvas.drawLines(pts, 0, (iTrackCount - 1) * 4, pRouteSelected2);
                                canvas.drawLines(pts, 0, (iTrackCount - 1) * 4, pRouteSelected1);
                            }
                            canvas.drawLines(pts, 0, (iTrackCount - 1) * 4, pRoute);
                        }
                    } else {
                        int iRouteCount = route.GetCount();
                        if (iRouteCount >= 1) {
                            Routepoint r;
                            pts = new float[(iRouteCount * 4)];
                            route.chachepoints.clear();
                            i = 0;
                            while (i < iRouteCount) {
                                r = route.get(i);
                                if (r != null) {
                                    if (nav_target.m_Type == NavigationTarget.TARGET_TYPE_ROUTEPOINT && nav_target.m_Routepoint == r) {
                                        this.m_Parent.m_NavigationTargetIndex = i;
                                    }
                                    float fXraw = (float) (((double) r.m_fX) * dZoom);
                                    float fYraw = (float) (((double) r.m_fY) * dZoom);
                                    fX = (float) Math.round(fXraw - ((float) l_iDispPosX));
                                    fY = (float) Math.round(fYraw - ((float) l_iDispPosY));
                                    if (MMTrackerActivity.m_iSettingsMapRotation != 0) {
                                        dp = RotatePoint((double) fX, (double) fY, (double) this.m_Parent.m_iRotateCenterX, (double) this.m_Parent.m_iRotateCenterY);
                                        fX = (float) dp.f0x;
                                        fY = (float) dp.f1y;
                                    }
                                    pts[(iIndex * 4) + 0] = fX;
                                    pts[(iIndex * 4) + 1] = fY;
                                    if (i > 0) {
                                        pts[((iIndex - 1) * 4) + 2] = pts[(iIndex * 4) + 0];
                                        pts[((iIndex - 1) * 4) + 3] = pts[(iIndex * 4) + 1];
                                    }
                                    iIndex++;
                                    route.chachepoints.add(new RoutepointCache(fXraw, fYraw));
                                    i++;
                                } else {
                                    return;
                                }
                            }
                            r = route.get(iRouteCount - 1);
                            if (r != null) {
                                pts[((iIndex - 1) * 4) + 2] = (float) (Math.round(((double) r.m_fX) * dZoom) - ((long) l_iDispPosX));
                                pts[((iIndex - 1) * 4) + 3] = (float) (Math.round(((double) r.m_fY) * dZoom) - ((long) l_iDispPosY));
                            }
                            if (MMTrackerActivity.m_SelectedRoutepoint != null && MMTrackerActivity.m_SelectedRoutepoint.m_ParentRoute == route) {
                                pRouteSelected1.setColor(-1);
                                pRouteSelected1.setStrokeWidth(route.m_fWidth + 4.0f);
                                pRouteSelected2.setColor(-16777216);
                                pRouteSelected2.setStrokeWidth(route.m_fWidth + 8.0f);
                                canvas.drawLines(pts, 0, (iIndex - 1) * 4, pRouteSelected2);
                                canvas.drawLines(pts, 0, (iIndex - 1) * 4, pRouteSelected1);
                            }
                            canvas.drawLines(pts, 0, (iIndex - 1) * 4, pRoute);
                            iTrackCount = iIndex;
                            route.m_bCacheVaild = true;
                        }
                    }
                    if (pts != null) {
                        i = 0;
                        while (i < iTrackCount) {
                            double X1 = (double) pts[i * 4];
                            double Y1 = (double) pts[(i * 4) + 1];
                            double dNavAngle1 = 0.0d;
                            double deltax = 0.0d;
                            double dLegLength = 0.0d;
                            if (i > 0) {
                                deltax = X1 - ((double) pts[(i - 1) * 4]);
                                double deltay = Y1 - ((double) pts[((i - 1) * 4) + 1]);
                                dNavAngle1 = (Math.atan(deltay / deltax) / 3.141592653589793d) * 180.0d;
                                if (deltax < 0.0d) {
                                    dNavAngle1 += 180.0d;
                                }
                                dLegLength = Math.sqrt((deltax * deltax) + (deltay * deltay));
                            }
                            double ShiftX = ((double) (this.m_Parent.m_iDisplayDensity / 25)) * Math.cos((dNavAngle1 / 180.0d) * 3.141592653589793d);
                            double ShiftY = ((double) (this.m_Parent.m_iDisplayDensity / 25)) * Math.sin((dNavAngle1 / 180.0d) * 3.141592653589793d);
                            float arrow_scale = (2.0f * route.m_fWidth) / 6.0f;
                            if (((double) arrow_scale) < 0.5d) {
                                arrow_scale = 0.5f;
                            }
                            m.setScale(arrow_scale, arrow_scale, 0.0f, 0.0f);
                            this.m_pathWaypointArrow.transform(m, pathNew);
                            m.setRotate((float) dNavAngle1, 0.0f, 0.0f);
                            pathNew.transform(m);
                            pathNew.offset((float) (X1 - ShiftX), (float) (Y1 - ShiftY));
                            if (nav_target.m_Type != NavigationTarget.TARGET_TYPE_NONE && this.m_Parent.m_NavigationTargetIndex == i && nav_target.m_ParentRoute == route) {
                                canvas.drawCircle((float) X1, (float) Y1, (float) (this.m_Parent.m_iDisplayDensity / 15), pCircleTarget);
                            }
                            if (i > 0 && ((double) ((((float) PATH_ARROW_LENGTH) * arrow_scale) * 2.0f)) < dLegLength) {
                                canvas.drawPath(pathNew, pCircle);
                            }
                            if (MMTrackerActivity.m_SelectedRoutepoint != null && MMTrackerActivity.m_SelectedRoutepoint.m_ParentRoute == route) {
                                canvas.drawCircle((float) X1, (float) Y1, (float) (this.m_Parent.m_iDisplayDensity / 18), pCircleSelected2);
                                canvas.drawCircle((float) X1, (float) Y1, (float) (this.m_Parent.m_iDisplayDensity / 21), pCircleSelected1);
                            }
                            canvas.drawCircle((float) X1, (float) Y1, (float) (this.m_Parent.m_iDisplayDensity / 25), pCircle);
                            canvas.drawCircle((float) X1, (float) Y1, (float) (this.m_Parent.m_iDisplayDensity / 35), pCircleInner);
                            if (route.m_bLocked) {
                                float len = (float) (this.m_Parent.m_iDisplayDensity / 65);
                                canvas.drawLine(((float) X1) - len, ((float) Y1) - len, ((float) X1) + len, ((float) Y1) + len, pCross);
                                canvas.drawLine(((float) X1) + len, ((float) Y1) - len, ((float) X1) - len, ((float) Y1) + len, pCross);
                            }
                            i++;
                        }
                    }
                }
            }
        }
    }

    private void DrawAllWaypoints(Canvas canvas, double dZoom, Rect MapRect) {
        Rect rectText = new Rect();
        int l_iDisplayDensity = this.m_Parent.m_iDisplayDensity;
        float fNaviCircleRadius = (float) (l_iDisplayDensity / 15);
        Paint pText = new Paint();
        Paint pRectText = new Paint();
        Paint pCircle = new Paint();
        Paint pCircleInnerWP = new Paint();
        Paint pCircleTarget = new Paint();
        Paint pCircleSelected1 = new Paint();
        Paint pCircleSelected2 = new Paint();
        Paint pBitmap = new Paint();
        NavigationTarget nav_target = MMTrackerActivity.m_NavigationTarget;
        int l_iDispPosX = this.m_Parent.m_iDispPosX;
        int l_iDispPosY = this.m_Parent.m_iDispPosY;
        pCircle.setStyle(Style.FILL);
        pCircle.setAntiAlias(true);
        pCircleInnerWP.setStyle(Style.FILL);
        pCircleInnerWP.setAntiAlias(true);
        pCircleTarget.setColor(-16711681);
        pCircleTarget.setStyle(Style.FILL);
        pCircleTarget.setAntiAlias(true);
        pCircleSelected1.setStyle(Style.FILL);
        pCircleSelected1.setAntiAlias(true);
        pCircleSelected1.setColor(-1);
        pCircleSelected2.setStyle(Style.FILL);
        pCircleSelected2.setAntiAlias(true);
        pCircleSelected2.setStrokeWidth(2.0f);
        pCircleSelected2.setColor(-16777216);
        pText.setColor(Color.argb(255, 0, 0, 0));
        pText.setTextSize((float) (this.m_Parent.m_iDisplayDensity / 10));
        pText.setFakeBoldText(true);
        pText.setAntiAlias(true);
        pRectText.setColor(Color.argb(160, 208, 208, 208));
        pBitmap.setStyle(Style.FILL);
        pBitmap.setAntiAlias(true);
        pBitmap.setColor(-16777216);
        if (MMTrackerActivity.m_bTracksLoaded) {
            Iterator it = MMTrackerActivity.waypoints.iterator();
            while (it.hasNext()) {
                Waypoint waypoint = (Waypoint) it.next();
                if (waypoint != null) {
                    if (MapRect.contains((int) Math.round(waypoint.m_dGpsLong * 10000.0d), (int) Math.round(waypoint.m_dGpsLat * 10000.0d)) && waypoint.m_bVisible) {
                        float fX;
                        float fY;
                        if (waypoint.m_bCacheVaild) {
                            fX = (float) Math.round(waypoint.m_fCacheX - ((float) l_iDispPosX));
                            fY = (float) Math.round(waypoint.m_fCacheY - ((float) l_iDispPosY));
                        } else {
                            float fXraw = (float) (((double) waypoint.m_fX) * dZoom);
                            float fYraw = (float) (((double) waypoint.m_fY) * dZoom);
                            waypoint.m_fCacheX = fXraw;
                            waypoint.m_fCacheY = fYraw;
                            fX = (float) Math.round(fXraw - ((float) l_iDispPosX));
                            fY = (float) Math.round(fYraw - ((float) l_iDispPosY));
                        }
                        if (MMTrackerActivity.m_iSettingsMapRotation != 0) {
                            DoublePoint dp = RotatePoint((double) fX, (double) fY, (double) this.m_Parent.m_iRotateCenterX, (double) this.m_Parent.m_iRotateCenterY);
                            fX = (float) dp.f0x;
                            fY = (float) dp.f1y;
                        }
                        if (waypoint.m_iSymbol == 0) {
                            if (nav_target.m_Type == NavigationTarget.TARGET_TYPE_WAYPOINT && nav_target.m_Waypoint == waypoint) {
                                canvas.drawCircle(fX, fY, fNaviCircleRadius, pCircleTarget);
                            }
                            pCircle.setColor(waypoint.m_iColor);
                            pCircleInnerWP.setColor((waypoint.m_iColor & -16777216) | 16776960);
                            if (MMTrackerActivity.m_SelectedWaypoint == waypoint) {
                                canvas.drawCircle(fX, fY, (float) (l_iDisplayDensity / 18), pCircleSelected2);
                                canvas.drawCircle(fX, fY, (float) (l_iDisplayDensity / 21), pCircleSelected1);
                            }
                            canvas.drawCircle(fX, fY, (float) (l_iDisplayDensity / 25), pCircle);
                            canvas.drawCircle(fX, fY, (float) (l_iDisplayDensity / 35), pCircleInnerWP);
                            if (waypoint.m_bLocked) {
                                float len = (float) (l_iDisplayDensity / 65);
                                canvas.drawLine(fX - len, fY - len, fX + len, fY + len, pCircleSelected2);
                                canvas.drawLine(fX + len, fY - len, fX - len, fY + len, pCircleSelected2);
                            }
                            if (waypoint.m_bShowLabel || MMTrackerActivity.m_SelectedWaypoint == waypoint) {
                                pText.getTextBounds(waypoint.m_sName, 0, waypoint.m_sName.length(), rectText);
                                rectText.inset(-3, -3);
                                rectText.offset(Math.round(fX + fNaviCircleRadius), Math.round(fY - fNaviCircleRadius));
                                canvas.drawRect(rectText, pRectText);
                                canvas.drawText(waypoint.m_sName, fX + fNaviCircleRadius, fY - fNaviCircleRadius, pText);
                            }
                        } else {
                            pBitmap.setColor(waypoint.m_iColor);
                            if (MMTrackerActivity.m_SelectedWaypoint == waypoint) {
                                canvas.drawBitmap(this.m_bmWpIconBaseActive, fX - ((float) (this.m_bmWpIconBaseActive.getWidth() / 2)), fY - ((float) this.m_bmWpIconBaseActive.getHeight()), pBitmap);
                            } else {
                                canvas.drawBitmap(this.m_bmWpIconBasePassive, fX - ((float) (this.m_bmWpIconBasePassive.getWidth() / 2)), fY - ((float) this.m_bmWpIconBasePassive.getHeight()), pBitmap);
                            }
                            if (waypoint.m_iSymbol == 1) {
                                canvas.drawBitmap(this.m_bmWpIcon[0], fX - ((float) (this.m_bmWpIcon[0].getWidth() / 2)), fY - ((float) this.m_bmWpIcon[0].getHeight()), pBitmap);
                            }
                            if (waypoint.m_iSymbol == 2) {
                                canvas.drawBitmap(this.m_bmWpIcon[1], fX - ((float) (this.m_bmWpIcon[1].getWidth() / 2)), fY - ((float) this.m_bmWpIcon[1].getHeight()), pBitmap);
                            }
                            if (waypoint.m_bShowLabel || MMTrackerActivity.m_SelectedWaypoint == waypoint) {
                                pText.getTextBounds(waypoint.m_sName, 0, waypoint.m_sName.length(), rectText);
                                rectText.inset(-3, -3);
                                rectText.offset(Math.round((2.0f * fNaviCircleRadius) + fX), Math.round(fY - fNaviCircleRadius));
                                canvas.drawRect(rectText, pRectText);
                                canvas.drawText(waypoint.m_sName, (2.0f * fNaviCircleRadius) + fX, fY - fNaviCircleRadius, pText);
                            }
                        }
                    }
                }
            }
        }
    }

    private void DrawDirectionArrow(Canvas canvas) {
        double dArrowLength;
        double l_dArrowAngle = this.m_dArrowAngle;
        DoublePoint dp = RotatePoint((double) (this.m_iGpsX - this.m_Parent.m_iDispPosX), (double) (this.m_iGpsY - this.m_Parent.m_iDispPosY), (double) this.m_Parent.m_iRotateCenterX, (double) this.m_Parent.m_iRotateCenterY);
        double x = dp.f0x;
        double y = dp.f1y;
        Paint pLine = new Paint();
        pLine.setStrokeWidth(5.0f);
        pLine.setColor(this.m_Parent.m_iSettingsColorSpeedVector | -16777216);
        pLine.setAntiAlias(true);
        Point p1 = convertScreenToMapCoordinates(0.0f, (float) dp.f1y);
        Point p2 = convertScreenToMapCoordinates((float) this.m_iDispSizeX, (float) dp.f1y);
        double dPixelPerKm = ((double) this.m_iDispSizeX) / Tools.calcDistance(this.m_Qct.convertXYtoLatitude(p1.x, p1.y), this.m_Qct.convertXYtoLongitude(p1.x, p1.y), this.m_Qct.convertXYtoLatitude(p2.x, p2.y), this.m_Qct.convertXYtoLongitude(p2.x, p2.y));
        if (this.m_Parent.m_SettingsArrowLength != 9999) {
            dArrowLength = (this.m_dArrowLength_km_s * dPixelPerKm) * ((double) this.m_Parent.m_SettingsArrowLength);
        } else if (this.m_dArrowLength_km_s >= MMTrackerActivity.ARROW_MIN_SPEED_TO_SHOW / 3600.0d || MMTrackerActivity.m_iSettingsUseCompassForVector != 0) {
            dArrowLength = ((((double) (this.m_iDispSizeX + this.m_iDispSizeY)) + Math.abs(x)) + Math.abs(y)) * 2.0d;
        } else {
            dArrowLength = 0.0d;
        }
        if (MMTrackerActivity.m_iSettingsUseCompassForVector != 0 && dArrowLength < (((double) this.m_bmCrosshairActive.getWidth()) / 2.0d) * 1.1d) {
            dArrowLength = (((double) this.m_bmCrosshairActive.getWidth()) / 2.0d) * 1.1d;
        }
        double dArrowTipsLen = (double) (this.m_Parent.m_iDisplayDensity / 10);
        double dMapRotationRad = ((double) (this.m_Parent.m_fMapRotation / 180.0f)) * 3.141592653589793d;
        if (dArrowLength > dArrowTipsLen || MMTrackerActivity.m_iSettingsUseCompassForVector != 0) {
            double dNewX = Math.cos(l_dArrowAngle + dMapRotationRad) * dArrowLength;
            double dNewY = Math.sin(l_dArrowAngle + dMapRotationRad) * dArrowLength;
            double dTipX1 = Math.cos(((l_dArrowAngle - 3.141592653589793d) + 0.20943951023931953d) + dMapRotationRad) * dArrowTipsLen;
            double dTipY1 = Math.sin(((l_dArrowAngle - 3.141592653589793d) + 0.20943951023931953d) + dMapRotationRad) * dArrowTipsLen;
            double dTipX2 = Math.cos(((l_dArrowAngle - 3.141592653589793d) - 0.20943951023931953d) + dMapRotationRad) * dArrowTipsLen;
            double dTipY2 = Math.sin(((l_dArrowAngle - 3.141592653589793d) - 0.20943951023931953d) + dMapRotationRad) * dArrowTipsLen;
            canvas.drawLine((float) x, (float) y, (float) (x + dNewX), (float) (y + dNewY), pLine);
            canvas.drawLine((float) (x + dNewX), (float) (y + dNewY), (float) ((x + dNewX) + dTipX1), (float) ((y + dNewY) + dTipY1), pLine);
            canvas.drawLine((float) (x + dNewX), (float) (y + dNewY), (float) ((x + dNewX) + dTipX2), (float) ((y + dNewY) + dTipY2), pLine);
        }
    }

    public void SetSpeedVector(boolean bOnlyForCompass, Location[] gps_list) {
        int iLastElement = MMTrackerActivity.POSITION_ARRAY_SIZE - 1;
        if (this.m_Qct == null || gps_list == null) {
            this.m_dArrowLength_km_s = 0.0d;
            this.m_dArrowAngle = Math.toRadians(90.0d);
        } else if (gps_list[0] == null || gps_list[iLastElement] == null) {
            this.m_dArrowLength_km_s = 0.0d;
            if (MMTrackerActivity.m_iSettingsUseCompassForVector == 0) {
                this.m_dArrowAngle = Math.toRadians(90.0d);
            } else {
                this.m_dArrowAngle = Math.toRadians((double) (((this.m_Parent.m_fCompassValue + this.m_Parent.m_fCompassOrientationCorrection) + this.m_fTrueNorth) - 90.0f));
            }
        } else {
            if (!bOnlyForCompass) {
                this.m_dArrowLength_km_s = MMTrackerActivity.m_dCalculatedGpsSpeed / 1000.0d;
            }
            if (MMTrackerActivity.m_iSettingsUseCompassForVector != 0 && (MMTrackerActivity.m_iSettingsUseCompassForVector != 2 || MMTrackerActivity.m_dCalculatedGpsSpeed <= MMTrackerActivity.ARROW_MIN_SPEED_TO_SWITCH_TO_COMPASS / 3.6d)) {
                this.m_dArrowAngle = Math.toRadians((double) (((this.m_Parent.m_fCompassValue + this.m_Parent.m_fCompassOrientationCorrection) + this.m_fTrueNorth) - 90.0f));
            } else if (!bOnlyForCompass) {
                this.m_dArrowAngle = MMTrackerActivity.m_dCalculatedGpsAngle - Math.toRadians(90.0d);
            }
        }
    }

    public void SetCurrentPosition(double longitude, double latitude, boolean bMoveToPosition, boolean bAdaptRotationCenter) {
        if (this.m_Qct != null) {
            int iZoomLevel = this.m_Parent.m_iZoomLevel;
            if (iZoomLevel > MAX_ZOOM_LEVELS - 1) {
                iZoomLevel = MAX_ZOOM_LEVELS - 1;
                this.m_Parent.m_iZoomLevel = iZoomLevel;
            }
            if (iZoomLevel < 0) {
                iZoomLevel = 0;
                this.m_Parent.m_iZoomLevel = 0;
            }
            double dZoom = m_dZoomFactor[iZoomLevel];
            int iGpsOldX = this.m_iGpsX;
            int iGpsOldY = this.m_iGpsY;
            this.m_iGpsX = (int) Math.round(this.m_Qct.convertLongLatToX(longitude, latitude) * dZoom);
            this.m_iGpsY = (int) Math.round(this.m_Qct.convertLongLatToY(longitude, latitude) * dZoom);
            if (this.m_iCurrentTouchMove != TOUCH_MOVE_MAP) {
                if (bMoveToPosition) {
                    this.m_Parent.m_iDispPosX = Math.round((float) (this.m_Parent.m_iDispPosX - (iGpsOldX - this.m_iGpsX)));
                    this.m_Parent.m_iDispPosY = Math.round((float) (this.m_Parent.m_iDispPosY - (iGpsOldY - this.m_iGpsY)));
                }
                if (!bAdaptRotationCenter) {
                    return;
                }
                if (this.m_Parent.m_PositionLock && (this.m_Parent.m_bGpsFix || this.m_Parent.m_bNetworkFix)) {
                    SetMapRotationCenter(this.m_iGpsX, this.m_iGpsY, MAP_COORDINATES);
                } else {
                    SetMapRotationCenter(this.m_Parent.m_iGpsLockCenterX, this.m_Parent.m_iGpsLockCenterY, SCREEN_COORDINATES);
                }
            }
        }
    }

    private void SetMapRotationCenter(int iNewRotCenterX, int iNewRotCenterY, int iInputType) {
        DoublePoint dp;
        int iRcXold = this.m_Parent.m_iRotateCenterX;
        int iRcYold = this.m_Parent.m_iRotateCenterY;
        this.m_Parent.bAvoidRotation = true;
        if (iInputType == MAP_COORDINATES) {
            dp = RotatePoint((double) (iNewRotCenterX - this.m_Parent.m_iDispPosX), (double) (iNewRotCenterY - this.m_Parent.m_iDispPosY), (double) this.m_Parent.m_iRotateCenterX, (double) this.m_Parent.m_iRotateCenterY);
            this.m_Parent.m_iRotateCenterX = (int) Math.round(dp.f0x);
            this.m_Parent.m_iRotateCenterY = (int) Math.round(dp.f1y);
        } else {
            this.m_Parent.m_iRotateCenterX = iNewRotCenterX;
            this.m_Parent.m_iRotateCenterY = iNewRotCenterY;
            dp = new DoublePoint();
            dp.f0x = (double) iNewRotCenterX;
            dp.f1y = (double) iNewRotCenterY;
        }
        dp = RotatePointNeg((double) iRcXold, (double) iRcYold, dp.f0x, dp.f1y);
        MMTrackerActivity mMTrackerActivity = this.m_Parent;
        mMTrackerActivity.m_iDispPosX = (int) (((long) mMTrackerActivity.m_iDispPosX) - Math.round(dp.f0x - ((double) iRcXold)));
        mMTrackerActivity = this.m_Parent;
        mMTrackerActivity.m_iDispPosY = (int) (((long) mMTrackerActivity.m_iDispPosY) - Math.round(dp.f1y - ((double) iRcYold)));
        this.m_Parent.bAvoidRotation = false;
    }

    public void PanMapToCenterPosition(double longitude, double latitude) {
        if (this.m_Qct != null) {
            int iZoomLevel = this.m_Parent.m_iZoomLevel;
            if (iZoomLevel > MAX_ZOOM_LEVELS - 1) {
                iZoomLevel = MAX_ZOOM_LEVELS - 1;
                this.m_Parent.m_iZoomLevel = iZoomLevel;
            }
            if (iZoomLevel < 0) {
                iZoomLevel = 0;
                this.m_Parent.m_iZoomLevel = 0;
            }
            double dZoom = m_dZoomFactor[iZoomLevel];
            double dGpsX = this.m_Qct.convertLongLatToX(longitude, latitude) * dZoom;
            double dGpsY = this.m_Qct.convertLongLatToY(longitude, latitude) * dZoom;
            this.m_Parent.m_iDispPosX = (int) Math.round(dGpsX - ((double) this.m_Parent.m_iGpsLockCenterX));
            this.m_Parent.m_iDispPosY = (int) Math.round(dGpsY - ((double) this.m_Parent.m_iGpsLockCenterY));
        }
    }

    public void SetGpsLock(boolean bDoInvalidate, boolean bUseOldCenterValues) {
        if (bUseOldCenterValues) {
            this.m_Parent.m_iDispPosX = this.m_iGpsX - this.m_Parent.m_iRotateCenterX;
            this.m_Parent.m_iDispPosY = this.m_iGpsY - this.m_Parent.m_iRotateCenterY;
        } else {
            this.m_Parent.m_iDispPosX = this.m_iGpsX - this.m_Parent.m_iGpsLockCenterX;
            this.m_Parent.m_iDispPosY = this.m_iGpsY - this.m_Parent.m_iGpsLockCenterY;
            if (this.m_Parent.m_PositionLock && (this.m_Parent.m_bGpsFix || this.m_Parent.m_bNetworkFix)) {
                this.m_Parent.m_iRotateCenterX = this.m_Parent.m_iGpsLockCenterX;
                this.m_Parent.m_iRotateCenterY = this.m_Parent.m_iGpsLockCenterY;
            } else {
                SetMapRotationCenter(this.m_Parent.m_iGpsLockCenterX, this.m_Parent.m_iGpsLockCenterY, SCREEN_COORDINATES);
            }
        }
        this.m_iZoomCenterX = this.m_Parent.m_iGpsLockCenterX;
        this.m_iZoomCenterY = this.m_Parent.m_iGpsLockCenterY;
        if (bDoInvalidate) {
            invalidateMapScreen(true);
        }
    }

    public void DisableGpsLock(boolean bDoInvalidate) {
        SetMapRotationCenter(this.m_Parent.m_iGpsLockCenterX, this.m_Parent.m_iGpsLockCenterY, SCREEN_COORDINATES);
        this.m_iZoomCenterX = this.m_Parent.m_iGpsLockCenterX;
        this.m_iZoomCenterY = this.m_Parent.m_iGpsLockCenterY;
        if (bDoInvalidate) {
            invalidateMapScreen(true);
        }
    }

    private DoublePoint RotatePoint(double px, double py, double cx, double cy) {
        DoublePoint dp = new DoublePoint();
        dp.f0x = (((px - cx) * this.m_Parent.m_dCosMapRotation) - ((py - cy) * this.m_Parent.m_dSinMapRotation)) + cx;
        dp.f1y = (((px - cx) * this.m_Parent.m_dSinMapRotation) + ((py - cy) * this.m_Parent.m_dCosMapRotation)) + cy;
        return dp;
    }

    private DoublePoint RotatePointNeg(double px, double py, double cx, double cy) {
        DoublePoint dp = new DoublePoint();
        dp.f0x = (((px - cx) * this.m_Parent.m_dCosMapRotationNeg) - ((py - cy) * this.m_Parent.m_dSinMapRotationNeg)) + cx;
        dp.f1y = (((px - cx) * this.m_Parent.m_dSinMapRotationNeg) + ((py - cy) * this.m_Parent.m_dCosMapRotationNeg)) + cy;
        return dp;
    }

    public double getCurrentZoom() {
        return m_dZoomFactor[this.m_Parent.m_iZoomLevel];
    }

    private String CreateAngleString(double dDegrees, boolean bShowInvalid, boolean bDrawUnit) {
        String sResult = "";
        if (bShowInvalid) {
            if (m_sUnitsAngleLength[MMTrackerActivity.m_SettingsUnitsAngles] == 3) {
                sResult = "- - -";
            } else if (m_sUnitsAngleLength[MMTrackerActivity.m_SettingsUnitsAngles] == 4) {
                sResult = "- - - -";
            } else {
                sResult = "";
            }
        } else if (MMTrackerActivity.m_SettingsUnitsAngles == 0) {
            int iDegrees = (int) Math.round(dDegrees);
            if (iDegrees >= 360) {
                iDegrees -= 360;
            }
            sResult = String.format("%03d", new Object[]{Integer.valueOf(iDegrees)});
        } else if (MMTrackerActivity.m_SettingsUnitsAngles == 1) {
            int iMils = (int) Math.round(m_dUnitsFactorAngle[1] * dDegrees);
            if (iMils >= 6400) {
                iMils -= 6400;
            }
            sResult = String.format("%4d", new Object[]{Integer.valueOf(iMils)});
        } else {
            sResult = "";
        }
        if (bDrawUnit) {
            return new StringBuilder(String.valueOf(sResult)).append(m_sUnitsAngleName[MMTrackerActivity.m_SettingsUnitsAngles]).toString();
        }
        return sResult;
    }
}
