package com.meixi;

public class Routepoint {
    public Route m_ParentRoute;
    public boolean m_bPointInLeg;
    public double m_dATD;
    public double m_dATDError;
    public double m_dATDinv;
    public double m_dGpsLat;
    public double m_dGpsLong;
    public double m_dLegLength;
    public double m_dXTE;
    public float m_fX;
    public float m_fY;
    public String m_sName;

    public Routepoint(double dGpsLong, double dGpsLat, float fX, float fY, String sName, Route pParent) {
        this.m_dGpsLong = dGpsLong;
        this.m_dGpsLat = dGpsLat;
        this.m_fX = fX;
        this.m_fY = fY;
        this.m_sName = sName;
        this.m_ParentRoute = pParent;
    }

    public boolean isFirstRoutepoint() {
        return this.m_ParentRoute.get(0) == this;
    }

    public void RefreshXY(QuickChartFile qct) {
        this.m_fX = (float) qct.convertLongLatToX(this.m_dGpsLong, this.m_dGpsLat);
        this.m_fY = (float) qct.convertLongLatToY(this.m_dGpsLong, this.m_dGpsLat);
    }
}
