package com.meixi;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Waypoint implements Comparable<Waypoint> {
    public static int WAYPOINT_COLOR_DEFAULT;
    public static int WAYPOINT_COLOR_MMI_LIST;
    public boolean m_CreatedByMMTracker;
    public boolean m_bCacheVaild;
    public boolean m_bFromFile;
    public boolean m_bLocked;
    public boolean m_bShowLabel;
    public boolean m_bVisible;
    public double m_dDummyDistance;
    public double m_dGpsLat;
    public double m_dGpsLong;
    public float m_fCacheX;
    public float m_fCacheY;
    public float m_fX;
    public float m_fY;
    public int m_iColor;
    public int m_iSymbol;
    public String m_sDesc;
    public String m_sFileName;
    public String m_sName;
    public String m_sSymbol;

    static {
        WAYPOINT_COLOR_DEFAULT = -65536;
        WAYPOINT_COLOR_MMI_LIST = -16776961;
    }

    public Waypoint() {
        this.m_sSymbol = "Circle";
        this.m_iSymbol = 0;
        this.m_sName = "";
        this.m_sDesc = "";
        this.m_sFileName = "";
        this.m_bFromFile = true;
    }

    public Waypoint(double dGpsLong, double dGpsLat, int iColor, String sSymbol) {
        this.m_sSymbol = "Circle";
        this.m_iSymbol = 0;
        this.m_sName = "";
        this.m_sDesc = "";
        this.m_sFileName = "";
        this.m_bFromFile = true;
        this.m_dGpsLong = dGpsLong;
        this.m_dGpsLat = dGpsLat;
        this.m_sSymbol = sSymbol;
        this.m_iColor = iColor;
        this.m_bCacheVaild = false;
        this.m_bVisible = true;
        this.m_bShowLabel = false;
        this.m_CreatedByMMTracker = true;
        this.m_bLocked = true;
        this.m_bFromFile = true;
        this.m_sName = new SimpleDateFormat("'WP_'yyMMdd'-'HHmmss").format(new Date());
    }

    public void RefreshXY(QuickChartFile qct) {
        if (qct != null) {
            this.m_fX = (float) qct.convertLongLatToX(this.m_dGpsLong, this.m_dGpsLat);
            this.m_fY = (float) qct.convertLongLatToY(this.m_dGpsLong, this.m_dGpsLat);
        }
    }

    public boolean WriteGpx(String sFile) {
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
            f.write("<mmtvisible>" + (this.m_bVisible ? "true" : "false") + "</mmtvisible>\r\n");
            f.write("<mmtshowlabel>" + (this.m_bShowLabel ? "true" : "false") + "</mmtshowlabel>\r\n");
            f.write("<mmtlocked>" + (this.m_bLocked ? "true" : "false") + "</mmtlocked>\r\n");
            f.write("</extensions>\r\n");
            f.write("</metadata>\r\n");
            f.write(String.format(Locale.US, "<wpt lat=\"%.8f\" lon=\"%.8f\">\r\n", new Object[]{Double.valueOf(this.m_dGpsLat), Double.valueOf(this.m_dGpsLong)}).replace(',', '.'));
            f.write("<name><![CDATA[" + this.m_sName + "]]></name>\r\n");
            f.write("<sym><![CDATA[" + this.m_sSymbol + "]]></sym>\r\n");
            f.write("<desc><![CDATA[" + this.m_sDesc + "]]></desc>\r\n");
            f.write("</wpt>\r\n");
            f.write("</gpx>\r\n");
            f.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public int CalcSymbolNumber() {
        if (this.m_sSymbol == null || this.m_sSymbol.equals("Circle")) {
            return 0;
        }
        if (this.m_sSymbol.equals("Geocache")) {
            return 1;
        }
        if (this.m_sSymbol.equals("Multicache")) {
            return 2;
        }
        return 0;
    }

    public int compareTo(Waypoint e) {
        return this.m_sName.toLowerCase().compareTo(e.m_sName.toLowerCase());
    }

    public String toString() {
        boolean m_bIsNavigating = false;
        if (MMTrackerActivity.m_NavigationTarget.m_Type == NavigationTarget.TARGET_TYPE_WAYPOINT) {
            m_bIsNavigating = MMTrackerActivity.m_NavigationTarget.m_Waypoint == this;
        }
        if (m_bIsNavigating) {
            return this.m_sName + " (navigating)";
        }
        return this.m_sName;
    }
}
