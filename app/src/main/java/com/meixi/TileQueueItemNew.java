package com.meixi;

public class TileQueueItemNew {
    public QctTile m_Bitmap;
    public boolean m_bNeedsToBeLoaded;
    public boolean m_bScheduleForDelete;
    public boolean m_bTileInUse;
    public double m_dAngle;
    public int m_iDecimtaion;
    public int m_iScreenX;
    public int m_iScreenY;
    public int m_iTileX;
    public int m_iTileY;

    public TileQueueItemNew() {
        this.m_bTileInUse = false;
        this.m_bNeedsToBeLoaded = false;
        this.m_bScheduleForDelete = false;
        this.m_iTileX = -99999;
        this.m_iTileY = -99999;
        this.m_dAngle = 0.0d;
        this.m_iDecimtaion = 1;
        this.m_Bitmap = new QctTile(1);
    }
}
