package com.meixi;

import android.graphics.Rect;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class Route implements Comparable<Route> {
    public static int LOAD_RESULT_ERROR;
    public static int LOAD_RESULT_MULTI;
    public static int LOAD_RESULT_NOPOINTS;
    public static int LOAD_RESULT_OK;
    public static int ROUTE_COLOR_DEFAULT;
    public static int ROUTE_WIDTH_DEFAULT;
    ArrayList<RoutepointCache> chachepoints;
    public Rect m_Areal;
    public boolean m_CreatedByMMTracker;
    public boolean m_bCacheVaild;
    public boolean m_bLocked;
    public boolean m_bNoSaveWarningShown;
    public boolean m_bVisible;
    public double m_dDummyDistance;
    public float m_fWidth;
    public int m_iColor;
    public String m_sFileName;
    public String m_sName;
    ArrayList<Routepoint> routepoints;

    static {
        ROUTE_COLOR_DEFAULT = -65536;
        ROUTE_WIDTH_DEFAULT = 6;
        LOAD_RESULT_OK = 0;
        LOAD_RESULT_MULTI = 1;
        LOAD_RESULT_ERROR = 2;
        LOAD_RESULT_NOPOINTS = 3;
    }

    public Route(String sPath) {
        this.routepoints = new ArrayList();
        this.chachepoints = new ArrayList();
        this.m_fWidth = (float) ROUTE_WIDTH_DEFAULT;
        this.m_iColor = ROUTE_COLOR_DEFAULT;
        this.m_CreatedByMMTracker = false;
        this.m_bCacheVaild = false;
        this.m_sFileName = "";
        this.m_bVisible = true;
        this.m_Areal = new Rect(99999999, 99999999, -99999999, -99999999);
        this.m_dDummyDistance = -1.0d;
        this.m_bLocked = false;
        this.m_bNoSaveWarningShown = false;
        this.m_sName = new SimpleDateFormat("'RT_'yyMMdd'-'HHmmss").format(new Date());
        if (sPath != "") {
            this.m_sFileName = new StringBuilder(String.valueOf(sPath)).append(this.m_sName).append(".gpx").toString();
        }
    }

    public void clear() {
        this.routepoints.clear();
    }

    public boolean add(double dGpsLong, double dGpsLat, float fX, float fY, String sName) {
        this.routepoints.add(new Routepoint(dGpsLong, dGpsLat, fX, fY, sName, this));
        if (((int) Math.round(10000.0d * dGpsLong)) < this.m_Areal.left) {
            this.m_Areal.left = (int) Math.round(10000.0d * dGpsLong);
        }
        if (((int) Math.round(10000.0d * dGpsLong)) > this.m_Areal.right) {
            this.m_Areal.right = (int) Math.round(10000.0d * dGpsLong);
        }
        if (((int) Math.round(10000.0d * dGpsLat)) < this.m_Areal.top) {
            this.m_Areal.top = (int) Math.round(10000.0d * dGpsLat);
        }
        if (((int) Math.round(10000.0d * dGpsLat)) > this.m_Areal.bottom) {
            this.m_Areal.bottom = (int) Math.round(10000.0d * dGpsLat);
        }
        this.m_bCacheVaild = false;
        return true;
    }

    public void UpdateAreal() {
        this.m_Areal.set(99999999, 99999999, -99999999, -99999999);
        Iterator it = this.routepoints.iterator();
        while (it.hasNext()) {
            Routepoint rp = (Routepoint) it.next();
            if (((int) Math.round(rp.m_dGpsLong * 10000.0d)) < this.m_Areal.left) {
                this.m_Areal.left = (int) Math.round(rp.m_dGpsLong * 10000.0d);
            }
            if (((int) Math.round(rp.m_dGpsLong * 10000.0d)) > this.m_Areal.right) {
                this.m_Areal.right = (int) Math.round(rp.m_dGpsLong * 10000.0d);
            }
            if (((int) Math.round(rp.m_dGpsLat * 10000.0d)) < this.m_Areal.top) {
                this.m_Areal.top = (int) Math.round(rp.m_dGpsLat * 10000.0d);
            }
            if (((int) Math.round(rp.m_dGpsLat * 10000.0d)) > this.m_Areal.bottom) {
                this.m_Areal.bottom = (int) Math.round(rp.m_dGpsLat * 10000.0d);
            }
        }
    }

    public int GetCount() {
        return this.routepoints.size();
    }

    public void CalcAllXteAtd(double dPointLat, double dPointLon) {
        if (this.routepoints.size() >= 2) {
            ((Routepoint) this.routepoints.get(0)).m_bPointInLeg = false;
            for (int i = 1; i < this.routepoints.size(); i++) {
                boolean z;
                Routepoint rp1 = (Routepoint) this.routepoints.get(i);
                Routepoint rp0 = (Routepoint) this.routepoints.get(i - 1);
                double dStartPointLat = rp0.m_dGpsLat;
                double dStartPointLon = rp0.m_dGpsLong;
                double dEndPointLat = rp1.m_dGpsLat;
                double dEndPointLon = rp1.m_dGpsLong;
                double dDist13 = Tools.calcDistance(dStartPointLat, dStartPointLon, dPointLat, dPointLon);
                double dDist23 = Tools.calcDistance(dEndPointLat, dEndPointLon, dPointLat, dPointLon);
                rp1.m_dXTE = Math.asin(Math.sin(dDist13 / Tools.EARTH_RADIUS) * Math.sin(Tools.CalcBearing(dStartPointLat, dStartPointLon, dPointLat, dPointLon) - Tools.CalcBearing(dStartPointLat, dStartPointLon, dEndPointLat, dEndPointLon))) * Tools.EARTH_RADIUS;
                rp1.m_dATD = Math.acos(Math.cos(dDist13 / Tools.EARTH_RADIUS) / Math.cos(rp1.m_dXTE / Tools.EARTH_RADIUS)) * Tools.EARTH_RADIUS;
                rp1.m_dATDinv = Math.acos(Math.cos(dDist23 / Tools.EARTH_RADIUS) / Math.cos((-rp1.m_dXTE) / Tools.EARTH_RADIUS)) * Tools.EARTH_RADIUS;
                rp1.m_dLegLength = Tools.calcDistance(dStartPointLat, dStartPointLon, dEndPointLat, dEndPointLon);
                rp1.m_dATDError = ((rp1.m_dATD + rp1.m_dATDinv) - rp1.m_dLegLength) / 2.0d;
                if (rp1.m_dATDError >= MMTrackerActivity.NAVIGATION_LEG_TOLERANCE || Math.abs(rp1.m_dXTE) >= MMTrackerActivity.NAVIGATION_LEG_TOLERANCE) {
                    z = false;
                } else {
                    z = true;
                }
                rp1.m_bPointInLeg = z;
            }
        }
    }

    public double CalcLengthKm(Routepoint start_rp) {
        if (this.routepoints.size() < 1) {
            return 0.0d;
        }
        double dLatOld = ((Routepoint) this.routepoints.get(0)).m_dGpsLat;
        double dLongOld = ((Routepoint) this.routepoints.get(0)).m_dGpsLong;
        double dDist = 0.0d;
        boolean bStartAdding = false;
        Iterator it = this.routepoints.iterator();
        while (it.hasNext()) {
            Routepoint rp = (Routepoint) it.next();
            double dLatTemp = rp.m_dGpsLat;
            double dLongTemp = rp.m_dGpsLong;
            if (rp == start_rp) {
                bStartAdding = true;
                dLatOld = dLatTemp;
                dLongOld = dLongTemp;
            }
            if (bStartAdding || start_rp == null) {
                dDist += Tools.calcDistance(dLatOld, dLongOld, dLatTemp, dLongTemp);
            }
            dLatOld = dLatTemp;
            dLongOld = dLongTemp;
        }
        return dDist;
    }

    public boolean RefreshXY(QuickChartFile qct) {
        if (qct == null) {
            return false;
        }
        Iterator it = this.routepoints.iterator();
        while (it.hasNext()) {
            ((Routepoint) it.next()).RefreshXY(qct);
        }
        return true;
    }

    public Routepoint getPreviousRoutepoint(Routepoint rp) {
        int index = this.routepoints.indexOf(rp);
        if (index <= 0) {
            return null;
        }
        return (Routepoint) this.routepoints.get(index - 1);
    }

    public Routepoint get(int iIndex) {
        return (Routepoint) this.routepoints.get(iIndex);
    }

    public int compareTo(Route e) {
        return this.m_sName.toLowerCase().compareTo(e.m_sName.toLowerCase());
    }

    public String toString() {
        boolean m_bIsNavigating = false;
        if (MMTrackerActivity.m_NavigationTarget.m_Type == NavigationTarget.TARGET_TYPE_ROUTEPOINT) {
            m_bIsNavigating = MMTrackerActivity.m_NavigationTarget.m_ParentRoute == this;
        }
        if (m_bIsNavigating) {
            return this.m_sName + " (navigating)";
        }
        return this.m_sName;
    }

    public boolean WriteGPX(String sFile) {
        try {
            BufferedWriter f = new BufferedWriter(new FileWriter(sFile));
            this.m_sFileName = sFile;
            f.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\r\n");
            f.write("<gpx version=\"1.1\"\r\n");
            f.write(" creator=\"MMTracker\"\r\n");
            f.write(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n");
            f.write(" xmlns=\"http://www.topografix.com/GPX/1/1\"\r\n");
            f.write(" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">\r\n");
            f.write("<metadata>\r\n");
            f.write("<name>" + this.m_sName + "</name>\r\n");
            f.write("<extensions>\r\n");
            f.write("<mmtcolor>" + String.format("%08X", new Object[]{Integer.valueOf(this.m_iColor)}) + "</mmtcolor>\r\n");
            f.write("<mmtwidth>" + String.format(Locale.US, "%.2f", new Object[]{Float.valueOf(this.m_fWidth)}).replace(',', '.') + "</mmtwidth>\r\n");
            f.write("<mmtvisible>" + (this.m_bVisible ? "true" : "false") + "</mmtvisible>\r\n");
            f.write("<mmtlocked>" + (this.m_bLocked ? "true" : "false") + "</mmtlocked>\r\n");
            f.write("</extensions>\r\n");
            f.write("</metadata>\r\n");
            f.write("<rte>\r\n");
            f.write("<name><![CDATA[" + this.m_sName + "]]></name>\r\n");
            f.write("<desc><![CDATA[Track recorded with MMTracker on Android]]></desc>\r\n");
            Iterator it = this.routepoints.iterator();
            while (it.hasNext()) {
                Routepoint rp = (Routepoint) it.next();
                f.write(String.format(Locale.US, "<rtept lat=\"%.8f\" lon=\"%.8f\"> <sym><![CDATA[Dot]]></sym> <type><![CDATA[Waypoints]]></type> </rtept>\r\n", new Object[]{Double.valueOf(rp.m_dGpsLat), Double.valueOf(rp.m_dGpsLong)}).replace(',', '.'));
            }
            f.write("</rte>\r\n");
            f.write("</gpx>\r\n");
            f.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
