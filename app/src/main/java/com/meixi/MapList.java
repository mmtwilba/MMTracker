package com.meixi;

public class MapList {
    public boolean m_bActive;
    public double m_dScale;
    public String m_sMapName;
    public String m_sMapPath;

    public MapList(String sName, String sPath, double dScale, boolean bActive) {
        this.m_sMapName = "";
        this.m_sMapPath = "";
        this.m_dScale = 0.0d;
        this.m_bActive = false;
        this.m_sMapName = sName;
        this.m_sMapPath = sPath;
        this.m_dScale = dScale;
        this.m_bActive = bActive;
    }

    public String toString() {
        if (MMTrackerActivity.m_CurrentMapName.equals(this.m_sMapPath)) {
            return ">  " + this.m_sMapName;
        }
        return this.m_sMapName;
    }
}
