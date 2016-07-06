package com.meixi;

public class Trackpoint {
    public double m_dAltitude;
    public double m_dGpsLat;
    public double m_dGpsLong;
    public double m_dVelocity;
    public float m_fX;
    public float m_fY;
    public long m_lTime;

    public Trackpoint(double dGpsLong, double dGpsLat, float fX, float fY, double dAltitude, long lTime, double dSpeed) {
        this.m_dGpsLong = dGpsLong;
        this.m_dGpsLat = dGpsLat;
        this.m_fX = fX;
        this.m_fY = fY;
        this.m_dAltitude = dAltitude;
        this.m_dVelocity = dSpeed;
        this.m_lTime = lTime;
    }

    public void RefreshXY(QuickChartFile qct) {
        this.m_fX = (float) qct.convertLongLatToX(this.m_dGpsLong, this.m_dGpsLat);
        this.m_fY = (float) qct.convertLongLatToY(this.m_dGpsLong, this.m_dGpsLat);
    }
}
