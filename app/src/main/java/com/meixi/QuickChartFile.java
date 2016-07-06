package com.meixi;

import android.graphics.Bitmap;
import android.graphics.Rect;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class QuickChartFile {
    public static int QCT_COULD_NOT_OPEN;
    public static int QCT_OPEN_OK;
    public static int QCT_WRONG_FORMAT;
    IntBuffer b1;
    boolean m_bFileOpen;
    public int m_iTilesHeight;
    public int m_iTilesWidth;
    String m_sMapName;

    private static native boolean CheckPixelPointInsideMap(double d, double d2);

    private static native boolean CheckPointInsideMap(double d, double d2);

    private static native boolean CheckPointInsideMapFile(String str, double d, double d2, int i);

    private static native void CloseQct();

    private static native double ConvertLongLatToX(double d, double d2);

    private static native double ConvertLongLatToY(double d, double d2);

    private static native double ConvertXYtoLatitude(int i, int i2);

    private static native double ConvertXYtoLongitude(int i, int i2);

    private static native double[] GetBoundingBoxFromMapFile(String str);

    private static native int GetHeightTiles();

    private static native double GetScale();

    private static native double GetScaleMapFile(String str);

    private static native int GetWidthTiles();

    private static native int OpenQct(String str);

    private static native void RenderTile(IntBuffer intBuffer, int i, int i2, int i3);

    private static native void RenderTileMulti(IntBuffer intBuffer, int i, int i2, int i3);

    private static native void SetNightMode(int i);

    static {
        QCT_OPEN_OK = 1;
        QCT_COULD_NOT_OPEN = -1;
        QCT_WRONG_FORMAT = -2;
        System.loadLibrary("qcttile");
    }

    public QuickChartFile() {
        this.m_sMapName = "";
        this.b1 = ByteBuffer.allocateDirect(24576).asIntBuffer();
        this.m_bFileOpen = false;
        this.m_sMapName = "";
    }

    public int openMap(String MapName, int iNightMode) {
        this.m_bFileOpen = false;
        int iResult = OpenQct(MapName);
        if (iResult == QCT_OPEN_OK) {
            this.m_bFileOpen = true;
            if (iNightMode > 0) {
                SetNightMode(iNightMode);
            }
            this.m_sMapName = MapName;
            this.m_iTilesWidth = GetWidthTiles();
            this.m_iTilesHeight = GetHeightTiles();
        }
        return iResult;
    }

    public double[] GetBoundingBoxFromFile(String sFile) {
        return GetBoundingBoxFromMapFile(sFile);
    }

    public boolean IsInsideMap(double dLong, double dLat) {
        return CheckPointInsideMap(dLong, dLat);
    }

    public boolean IsPixelInsideMap(double dLong, double dLat) {
        return CheckPixelPointInsideMap(dLong, dLat);
    }

    public boolean IsInsideMapFile(String filename, double dLong, double dLat, int iType) {
        return CheckPointInsideMapFile(filename, dLong, dLat, iType);
    }

    public double getScaleMapFile(String filename) {
        return GetScaleMapFile(filename);
    }

    public double getScale() {
        return GetScale();
    }

    public void setNightMode(int iMode) {
        SetNightMode(iMode);
    }

    public Rect getLongLatRect(int iX1, int iY1, int iX2, int iY2) {
        Rect r = new Rect();
        double dLong1 = convertXYtoLongitude(iX1, iY1);
        double dLong2 = convertXYtoLongitude(iX2, iY2);
        double dLat1 = convertXYtoLatitude(iX1, iY1);
        double dLat2 = convertXYtoLatitude(iX2, iY2);
        r.set((int) Math.round(Math.min(dLong1, dLong2) * 10000.0d), (int) Math.round(Math.min(dLat1, dLat2) * 10000.0d), (int) Math.round(Math.max(dLong1, dLong2) * 10000.0d), (int) Math.round(Math.max(dLat1, dLat2) * 10000.0d));
        return r;
    }

    public boolean closeMap() {
        CloseQct();
        this.m_bFileOpen = false;
        this.m_sMapName = "";
        return true;
    }

    boolean getTile(int iX, int iY, int decimate, QctTile aTile) {
        RenderTile(this.b1, iX, iY, 1);
        Bitmap bitmap = aTile.m_Bitmap;
        Buffer buffer = this.b1;
        buffer.position(0);
        bitmap.copyPixelsFromBuffer(buffer);
        return true;
    }

    boolean getTileMulti(int iX, int iY, int decimate, QctTile aTile) {
        RenderTileMulti(this.b1, iX, iY, decimate);
        Bitmap bitmap = aTile.m_Bitmap;
        Buffer buffer = this.b1;
        buffer.position(0);
        bitmap.copyPixelsFromBuffer(buffer);
        return true;
    }

    int getWidthTiles() {
        return GetWidthTiles();
    }

    int getHeightTiles() {
        return GetHeightTiles();
    }

    double convertXYtoLongitude(int x, int y) {
        return ConvertXYtoLongitude(x, y);
    }

    double convertXYtoLatitude(int x, int y) {
        return ConvertXYtoLatitude(x, y);
    }

    double convertLongLatToX(double lo, double la) {
        return ConvertLongLatToX(lo, la);
    }

    double convertLongLatToY(double lo, double la) {
        return ConvertLongLatToY(lo, la);
    }
}
