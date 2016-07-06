package com.meixi;

import android.content.Context;
import android.graphics.Rect;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class Track implements Comparable<Track> {
    public static int TRACK_COLOR_DEFAULT;
    public static int TRACK_WIDTH_DEFAULT;
    public static double VELOCITY_THRESHOLD_MOVE;
    ArrayList<TrackpointCache> chachepoints;
    public boolean m_AllDetailsAvailable;
    public Rect m_Areal;
    public boolean m_CreatedByMMTracker;
    public boolean m_RewriteSemaphore;
    public boolean m_WriteSemaphore;
    public boolean m_bActive;
    public boolean m_bCacheVaild;
    public boolean m_bHasRealSpeedValues;
    public boolean m_bVisible;
    public boolean m_bWriteStarted;
    public double m_dDummyDistance;
    public float m_fWidth;
    public int m_iColor;
    public int m_iFirstDateIndex;
    public int m_iNumberInFile;
    public String m_sFileName;
    public String m_sLoadName;
    public String m_sName;
    ArrayList<Trackpoint> trackpoints;

    static {
        TRACK_COLOR_DEFAULT = -16776961;
        TRACK_WIDTH_DEFAULT = 4;
        VELOCITY_THRESHOLD_MOVE = 0.1d;
    }

    public Track(String sPath) {
        this.trackpoints = new ArrayList();
        this.chachepoints = new ArrayList();
        this.m_fWidth = (float) TRACK_WIDTH_DEFAULT;
        this.m_iColor = TRACK_COLOR_DEFAULT;
        this.m_CreatedByMMTracker = true;
        this.m_bCacheVaild = false;
        this.m_sFileName = "";
        this.m_bActive = false;
        this.m_bVisible = true;
        this.m_RewriteSemaphore = false;
        this.m_WriteSemaphore = false;
        this.m_AllDetailsAvailable = false;
        this.m_bHasRealSpeedValues = false;
        this.m_iFirstDateIndex = -1;
        this.m_Areal = new Rect(99999999, 99999999, -99999999, -99999999);
        this.m_dDummyDistance = -1.0d;
        this.m_bWriteStarted = false;
        this.m_sName = new SimpleDateFormat("'TR_'yyMMdd'-'HHmmss").format(new Date());
        this.m_sLoadName = this.m_sName;
        this.m_iNumberInFile = 1;
        this.m_AllDetailsAvailable = false;
        if (sPath != "") {
            this.m_sFileName = new StringBuilder(String.valueOf(sPath)).append(this.m_sName).append(".gpx").toString();
        }
    }

    public void setTrackpoints(ArrayList<Trackpoint> list) {
        this.trackpoints = list;
    }

    public void clear() {
        this.trackpoints.clear();
    }

    public boolean add(double dGpsLong, double dGpsLat, float fX, float fY, double dAltitude, long lTime, double dSpeed) {
        this.trackpoints.add(new Trackpoint(dGpsLong, dGpsLat, fX, fY, dAltitude, lTime, dSpeed));
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
        this.m_iFirstDateIndex = 0;
        return true;
    }

    public int GetCount() {
        return this.trackpoints.size();
    }

    public double CalcLengthKm() {
        if (this.trackpoints.size() < 1) {
            return 0.0d;
        }
        double dLatOld = ((Trackpoint) this.trackpoints.get(0)).m_dGpsLat;
        double dLongOld = ((Trackpoint) this.trackpoints.get(0)).m_dGpsLong;
        double dDist = 0.0d;
        Iterator it = this.trackpoints.iterator();
        while (it.hasNext()) {
            Trackpoint tp = (Trackpoint) it.next();
            double dLatTemp = tp.m_dGpsLat;
            double dLongTemp = tp.m_dGpsLong;
            dDist += Tools.calcDistance(dLatOld, dLongOld, dLatTemp, dLongTemp);
            dLatOld = dLatTemp;
            dLongOld = dLongTemp;
        }
        return dDist;
    }

    public double CalcLengthKm(int iStartIndex) {
        double d = 0.0d;
        if (this.trackpoints.size() >= 1 && iStartIndex < this.trackpoints.size() - 1 && iStartIndex >= 0) {
            double dLatOld = ((Trackpoint) this.trackpoints.get(iStartIndex)).m_dGpsLat;
            double dLongOld = ((Trackpoint) this.trackpoints.get(iStartIndex)).m_dGpsLong;
            d = 0.0d;
            for (int iIndex = iStartIndex + 1; iIndex < this.trackpoints.size(); iIndex++) {
                Trackpoint tp = (Trackpoint) this.trackpoints.get(iIndex);
                double dLatTemp = tp.m_dGpsLat;
                double dLongTemp = tp.m_dGpsLong;
                d += Tools.calcDistance(dLatOld, dLongOld, dLatTemp, dLongTemp);
                dLatOld = dLatTemp;
                dLongOld = dLongTemp;
            }
        }
        return d;
    }

    public double CalcMaxSpeed() {
        if (this.trackpoints.size() < 1) {
            return 0.0d;
        }
        double dMaxSpeed = -9999999.0d;
        Iterator it = this.trackpoints.iterator();
        while (it.hasNext()) {
            Trackpoint tp = (Trackpoint) it.next();
            if (tp.m_dVelocity > dMaxSpeed) {
                dMaxSpeed = tp.m_dVelocity;
            }
        }
        return dMaxSpeed;
    }

    public double CalcMinSpeed() {
        if (this.trackpoints.size() < 1) {
            return 0.0d;
        }
        double dMinSpeed = 9999999.0d;
        Iterator it = this.trackpoints.iterator();
        while (it.hasNext()) {
            Trackpoint tp = (Trackpoint) it.next();
            if (tp.m_dVelocity < dMinSpeed) {
                dMinSpeed = tp.m_dVelocity;
            }
        }
        return dMinSpeed;
    }

    public double CalcMeanSpeed() {
        if (this.trackpoints.size() < 1) {
            return 0.0d;
        }
        double dMeanSpeed = 0.0d;
        Iterator it = this.trackpoints.iterator();
        while (it.hasNext()) {
            dMeanSpeed += ((Trackpoint) it.next()).m_dVelocity;
        }
        return dMeanSpeed / ((double) this.trackpoints.size());
    }

    public double CalcMeanMovementSpeed() {
        if (this.trackpoints.size() < 1) {
            return 0.0d;
        }
        double dMeanSpeed = 0.0d;
        long lCount = 0;
        Iterator it = this.trackpoints.iterator();
        while (it.hasNext()) {
            Trackpoint tp = (Trackpoint) it.next();
            if (tp.m_dVelocity > VELOCITY_THRESHOLD_MOVE) {
                dMeanSpeed += tp.m_dVelocity;
                lCount++;
            }
        }
        if (lCount > 0) {
            return dMeanSpeed / ((double) lCount);
        }
        return 0.0d;
    }

    public double CalcMaxHeight() {
        if (this.trackpoints.size() < 1) {
            return 0.0d;
        }
        double dMaxHeight = -9999999.0d;
        Iterator it = this.trackpoints.iterator();
        while (it.hasNext()) {
            Trackpoint tp = (Trackpoint) it.next();
            if (tp.m_dAltitude > dMaxHeight) {
                dMaxHeight = tp.m_dAltitude;
            }
        }
        return dMaxHeight;
    }

    public double CalcMinHeight() {
        if (this.trackpoints.size() < 1) {
            return 0.0d;
        }
        double dMinHeight = 9999999.0d;
        Iterator it = this.trackpoints.iterator();
        while (it.hasNext()) {
            Trackpoint tp = (Trackpoint) it.next();
            if (tp.m_dAltitude < dMinHeight) {
                dMinHeight = tp.m_dAltitude;
            }
        }
        return dMinHeight;
    }

    public long CalcMovingTime() {
        if (this.trackpoints.size() < 1) {
            return 0;
        }
        long lTimeOld = -9999;
        long lTimeSum = 0;
        Iterator it = this.trackpoints.iterator();
        while (it.hasNext()) {
            Trackpoint tp = (Trackpoint) it.next();
            if (lTimeOld != -9999 && tp.m_dVelocity > VELOCITY_THRESHOLD_MOVE) {
                lTimeSum += tp.m_lTime - lTimeOld;
            }
            lTimeOld = tp.m_lTime;
        }
        return lTimeSum / 1000;
    }

    public long CalcTotalTime() {
        if (this.trackpoints.size() < 1) {
            return 0;
        }
        return (long) Math.round((float) ((((Trackpoint) this.trackpoints.get(this.trackpoints.size() - 1)).m_lTime - ((Trackpoint) this.trackpoints.get(0)).m_lTime) / 1000));
    }

    public void CreateSpeedValues() {
        if (this.trackpoints.size() >= 1) {
            double dLatOld = 99999.0d;
            double dLongOld = 99999.0d;
            long lTimeOld = 0;
            Iterator it = this.trackpoints.iterator();
            while (it.hasNext()) {
                Trackpoint tp = (Trackpoint) it.next();
                if (dLatOld != 99999.0d) {
                    if (tp.m_lTime != lTimeOld) {
                        tp.m_dVelocity = (Tools.calcDistance(dLatOld, dLongOld, tp.m_dGpsLat, tp.m_dGpsLong) * 1000.0d) / ((double) ((tp.m_lTime - lTimeOld) / 1000));
                    } else {
                        tp.m_dVelocity = 0.0d;
                    }
                }
                dLatOld = tp.m_dGpsLat;
                dLongOld = tp.m_dGpsLong;
                lTimeOld = tp.m_lTime;
            }
        }
    }

    public boolean RefreshXY(QuickChartFile qct) {
        if (qct == null) {
            return false;
        }
        Iterator it = this.trackpoints.iterator();
        while (it.hasNext()) {
            ((Trackpoint) it.next()).RefreshXY(qct);
        }
        return true;
    }

    public Trackpoint get(int iIndex) {
        return (Trackpoint) this.trackpoints.get(iIndex);
    }

    public boolean ReWriteGpx(Context context, String sNewFileName) {
        if (this.m_sFileName == "") {
            return false;
        }
        do {
        } while (this.m_WriteSemaphore);
        this.m_RewriteSemaphore = true;
        try {
            String sWriteFilename;
            String sTemp;
            BufferedReader f_read = new BufferedReader(new FileReader(this.m_sFileName));
            if (sNewFileName.length() > 0) {
                sWriteFilename = new StringBuilder(String.valueOf(Tools.ExtractPath(this.m_sFileName))).append(sNewFileName).toString();
            } else {
                sWriteFilename = this.m_sFileName + "~";
            }
            BufferedWriter f_write = new BufferedWriter(new FileWriter(sWriteFilename));
            f_write.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\r\n");
            f_write.write("<gpx version=\"1.1\"\r\n");
            f_write.write(" creator=\"MMTracker\"\r\n");
            f_write.write(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n");
            f_write.write(" xmlns=\"http://www.topografix.com/GPX/1/1\"\r\n");
            f_write.write(" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">\r\n");
            f_write.write("<metadata>\r\n");
            f_write.write("<name>" + this.m_sName + "</name>\r\n");
            f_write.write("<extensions>\r\n");
            f_write.write("<mmtcolor>" + String.format("%08X", new Object[]{Integer.valueOf(this.m_iColor)}) + "</mmtcolor>\r\n");
            f_write.write("<mmtwidth>" + String.format("%.2f", new Object[]{Float.valueOf(this.m_fWidth)}).replaceAll(",", ".") + "</mmtwidth>\r\n");
            f_write.write("<mmtvisible>" + (this.m_bVisible ? "true" : "false") + "</mmtvisible>\r\n");
            f_write.write("</extensions>\r\n");
            f_write.write("</metadata>\r\n");
            f_write.write("<trk>\r\n");
            f_write.write("<name><![CDATA[" + this.m_sName + "]]></name>\r\n");
            f_write.write("<desc><![CDATA[Track recorded with MMTracker on Android]]></desc>\r\n");
            f_write.write("<trkseg>\r\n");
            do {
                sTemp = f_read.readLine();
                if (sTemp == null) {
                    break;
                }
            } while (sTemp.indexOf("<trkseg>") <= -1);
            while (true) {
                sTemp = f_read.readLine();
                if (sTemp == null) {
                    break;
                }
                f_write.write(new StringBuilder(String.valueOf(sTemp)).append("\r\n").toString());
            }
            f_write.close();
            f_read.close();
            File f1 = new File(this.m_sFileName);
            f1.delete();
            if (sNewFileName.length() <= 0) {
                new File(this.m_sFileName + "~").renameTo(f1);
            } else {
                this.m_sFileName = sWriteFilename;
            }
            setCreateDate(this.m_sFileName);
            this.m_RewriteSemaphore = false;
            return Tools.PostIoResult(null, context);
        } catch (Exception e) {
            this.m_RewriteSemaphore = false;
            return Tools.PostIoResult(e, context);
        }
    }

    public boolean CloseGPX(Context context) {
        do {
        } while (this.m_RewriteSemaphore);
        this.m_WriteSemaphore = true;
        try {
            BufferedWriter f = new BufferedWriter(new FileWriter(this.m_sFileName, true));
            f.write("</trkseg>\r\n");
            f.write("</trk>\r\n");
            f.write("</gpx>\r\n");
            f.close();
            setCreateDate(this.m_sFileName);
            this.m_WriteSemaphore = false;
            return Tools.PostIoResult(null, context);
        } catch (Exception e) {
            this.m_WriteSemaphore = false;
            return Tools.PostIoResult(e, context);
        }
    }

    public boolean WriteLastTrackpointToGPX(Context context) {
        do {
        } while (this.m_RewriteSemaphore);
        this.m_WriteSemaphore = true;
        try {
            BufferedWriter f;
            String sTemp;
            if (this.m_bWriteStarted) {
                f = new BufferedWriter(new FileWriter(this.m_sFileName, true));
            } else {
                f = new BufferedWriter(new FileWriter(this.m_sFileName));
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
                f.write("</extensions>\r\n");
                f.write("</metadata>\r\n");
                f.write("<trk>\r\n");
                f.write("<name><![CDATA[" + this.m_sName + "]]></name>\r\n");
                f.write("<desc><![CDATA[Track recorded with MMTracker on Android]]></desc>\r\n");
                f.write("<trkseg>\r\n");
                this.m_bWriteStarted = true;
            }
            String date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new Date(((Trackpoint) this.trackpoints.get(this.trackpoints.size() - 1)).m_lTime));
            if (this.m_bHasRealSpeedValues) {
                sTemp = String.format(Locale.US, "<trkpt lat=\"%.8f\" lon=\"%.8f\"><ele>%d</ele><speed>%.2f</speed><time>%s</time></trkpt>\r\n", new Object[]{Double.valueOf(tp.m_dGpsLat), Double.valueOf(tp.m_dGpsLong), Integer.valueOf((int) Math.round(tp.m_dAltitude)), Double.valueOf(tp.m_dVelocity), date});
            } else {
                sTemp = String.format(Locale.US, "<trkpt lat=\"%.8f\" lon=\"%.8f\"><ele>%d</ele><time>%s</time></trkpt>\r\n", new Object[]{Double.valueOf(tp.m_dGpsLat), Double.valueOf(tp.m_dGpsLong), Integer.valueOf((int) Math.round(tp.m_dAltitude)), date});
            }
            f.write(sTemp.replace(',', '.'));
            f.close();
            setCreateDate(this.m_sFileName);
            this.m_WriteSemaphore = false;
            return Tools.PostIoResult(null, context);
        } catch (Exception e) {
            this.m_WriteSemaphore = false;
            return Tools.PostIoResult(e, context);
        }
    }

    private void setCreateDate(String sFileName) {
        if (this.m_iFirstDateIndex > -1 && this.trackpoints.size() > this.m_iFirstDateIndex) {
            new File(sFileName).setLastModified(((Trackpoint) this.trackpoints.get(this.m_iFirstDateIndex)).m_lTime);
        }
    }

    public int compareTo(Track e) {
        return this.m_sName.toLowerCase().compareTo(e.m_sName.toLowerCase());
    }

    public String toString() {
        if (this.m_bActive) {
            return this.m_sName + " (recording)";
        }
        return this.m_sName;
    }
}
