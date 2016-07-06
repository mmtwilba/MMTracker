package com.meixi;

public class TileQueueManager {
    private static int TILE_SIZE;
    private QuickChartFile m_Qct;
    private TileListItem[] m_TileList;
    public TileQueueItemNew[] m_TileQueue;
    private boolean m_bAllTilesCreated;
    private boolean m_bIsLoading;
    private boolean m_bIsModyfying;
    private boolean m_bThreadActive;
    public boolean m_bThreadFinished;
    private boolean m_bThreadRunning;
    private double m_dAngleOld;
    private int m_iDecimationOld;
    private int m_iListCount;
    public int m_iLoadCount;
    private int m_iPosXold;
    private int m_iPosYold;
    private int m_iSizeXold;
    private int m_iSizeYold;
    private int vx1;
    private int vx2;
    private int vy1;
    private int vy2;
    private int x0;
    private int x1;
    private int x2;
    private int x3;
    private int y0;
    private int y1;
    private int y2;
    private int y3;

    /* renamed from: com.meixi.TileQueueManager.1 */
    class C00561 implements Runnable {
        C00561() {
        }

        public void run() {
            int iLoadIndex = 0;
            TileQueueManager.this.m_bThreadRunning = true;
            while (TileQueueManager.this.m_bThreadActive) {
                if (TileQueueManager.this.m_iLoadCount <= 0 || !TileQueueManager.this.m_bAllTilesCreated || TileQueueManager.this.m_Qct == null) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                } else {
                    if (TileQueueManager.this.m_TileQueue[iLoadIndex] != null && TileQueueManager.this.m_TileQueue[iLoadIndex].m_bNeedsToBeLoaded && TileQueueManager.this.m_TileQueue[iLoadIndex].m_bTileInUse) {
                        TileQueueManager.this.m_bIsLoading = true;
                        if (TileQueueManager.this.m_TileQueue[iLoadIndex].m_iDecimtaion > 1) {
                            TileQueueManager.this.m_Qct.getTileMulti(TileQueueManager.this.m_TileQueue[iLoadIndex].m_iTileX, TileQueueManager.this.m_TileQueue[iLoadIndex].m_iTileY, TileQueueManager.this.m_TileQueue[iLoadIndex].m_iDecimtaion, TileQueueManager.this.m_TileQueue[iLoadIndex].m_Bitmap);
                        } else {
                            TileQueueManager.this.m_Qct.getTile(TileQueueManager.this.m_TileQueue[iLoadIndex].m_iTileX, TileQueueManager.this.m_TileQueue[iLoadIndex].m_iTileY, 1, TileQueueManager.this.m_TileQueue[iLoadIndex].m_Bitmap);
                        }
                        TileQueueManager.this.m_TileQueue[iLoadIndex].m_bNeedsToBeLoaded = false;
                        TileQueueManager tileQueueManager = TileQueueManager.this;
                        tileQueueManager.m_iLoadCount--;
                        TileQueueManager.this.m_bIsLoading = false;
                        while (TileQueueManager.this.m_bIsModyfying) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e2) {
                            }
                        }
                    }
                    iLoadIndex++;
                    if (iLoadIndex >= TileQueueManager.this.m_TileQueue.length) {
                        iLoadIndex = 0;
                    }
                }
            }
            TileQueueManager.this.m_bThreadRunning = false;
        }
    }

    static {
        TILE_SIZE = 64;
    }

    public TileQueueManager(QuickChartFile qct, int iTileCount) {
        this.m_iSizeXold = -99999;
        this.m_iSizeYold = -99999;
        this.m_iPosXold = -99999;
        this.m_iPosYold = -99999;
        this.m_dAngleOld = -99999.0d;
        this.m_bThreadFinished = true;
        this.m_bThreadRunning = false;
        this.m_iLoadCount = 0;
        this.m_bIsLoading = false;
        this.m_bIsModyfying = false;
        this.m_iDecimationOld = 1;
        this.m_bAllTilesCreated = false;
        this.m_TileQueue = new TileQueueItemNew[iTileCount];
        this.m_TileList = new TileListItem[iTileCount];
        for (int i = 0; i < this.m_TileQueue.length; i++) {
            this.m_TileQueue[i] = new TileQueueItemNew();
            this.m_TileList[i] = new TileListItem();
        }
        this.m_bAllTilesCreated = true;
        this.m_Qct = qct;
    }

    public void RecycleBitmaps() {
        for (int i = 0; i < this.m_TileQueue.length; i++) {
            if (this.m_TileQueue[i] != null) {
                this.m_TileQueue[i].m_bTileInUse = false;
                this.m_TileQueue[i].m_Bitmap.m_Bitmap.recycle();
                this.m_TileQueue[i].m_Bitmap.m_Bitmap = null;
            }
        }
    }

    public void UpdateTileList(int iSizeX, int iSizeY, int iPosX, int iPosY, double dAngle, int iDecimation, int iRotateCenterX, int iRotateCenterY) {
        if (this.m_Qct == null) {
            this.m_iLoadCount = 0;
            return;
        }
        int queue_index;
        double dAngle_rad = ((-dAngle) / 180.0d) * 3.141592653589793d;
        if (iSizeX == this.m_iSizeXold && iSizeY == this.m_iSizeYold && iPosX == this.m_iPosXold && iPosY == this.m_iPosYold) {
            if (dAngle == this.m_dAngleOld && iDecimation == this.m_iDecimationOld) {
                return;
            }
        }
        this.m_iSizeXold = iSizeX;
        this.m_iSizeYold = iSizeY;
        this.m_iPosXold = iPosX;
        this.m_iPosYold = iPosY;
        this.m_dAngleOld = dAngle;
        this.m_iDecimationOld = iDecimation;
        iPosX *= iDecimation;
        iPosY *= iDecimation;
        double x0_r = (double) iPosX;
        double y0_r = (double) iPosY;
        double x1_r = ((double) (iSizeX * iDecimation)) + x0_r;
        double y1_r = y0_r;
        double x2_r = x0_r;
        double y2_r = ((double) (iSizeY * iDecimation)) + y0_r;
        double x_trans = x0_r + ((double) (iRotateCenterX * iDecimation));
        double y_trans = y0_r + ((double) (iRotateCenterY * iDecimation));
        double dCos = Math.cos(dAngle_rad);
        double dSin = Math.sin(dAngle_rad);
        this.x0 = (int) Math.round((((x0_r - x_trans) * dCos) - ((y0_r - y_trans) * dSin)) + x_trans);
        this.y0 = (int) Math.round((((x0_r - x_trans) * dSin) + ((y0_r - y_trans) * dCos)) + y_trans);
        this.x1 = (int) Math.round((((x1_r - x_trans) * dCos) - ((y1_r - y_trans) * dSin)) + x_trans);
        this.y1 = (int) Math.round((((x1_r - x_trans) * dSin) + ((y1_r - y_trans) * dCos)) + y_trans);
        this.x2 = (int) Math.round((((x2_r - x_trans) * dCos) - ((y2_r - y_trans) * dSin)) + x_trans);
        this.y2 = (int) Math.round((((x2_r - x_trans) * dSin) + ((y2_r - y_trans) * dCos)) + y_trans);
        this.vx1 = this.x1 - this.x0;
        this.vx2 = this.x2 - this.x0;
        this.vy1 = this.y1 - this.y0;
        this.vy2 = this.y2 - this.y0;
        this.x3 = this.x2 + this.vx1;
        this.y3 = this.y2 + this.vy1;
        int iMinX = this.x0;
        int i = this.x1;
        if (r0 < iMinX) {
            iMinX = this.x1;
        }
        i = this.x2;
        if (r0 < iMinX) {
            iMinX = this.x2;
        }
        i = this.x3;
        if (r0 < iMinX) {
            iMinX = this.x3;
        }
        int iMinY = this.y0;
        i = this.y1;
        if (r0 < iMinY) {
            iMinY = this.y1;
        }
        i = this.y2;
        if (r0 < iMinY) {
            iMinY = this.y2;
        }
        i = this.y3;
        if (r0 < iMinY) {
            iMinY = this.y3;
        }
        int iMaxX = this.x0;
        i = this.x1;
        if (r0 > iMaxX) {
            iMaxX = this.x1;
        }
        i = this.x2;
        if (r0 > iMaxX) {
            iMaxX = this.x2;
        }
        i = this.x3;
        if (r0 > iMaxX) {
            iMaxX = this.x3;
        }
        int iMaxY = this.y0;
        i = this.y1;
        if (r0 > iMaxY) {
            iMaxY = this.y1;
        }
        i = this.y2;
        if (r0 > iMaxY) {
            iMaxY = this.y2;
        }
        i = this.y3;
        if (r0 > iMaxY) {
            iMaxY = this.y3;
        }
        iMinX /= TILE_SIZE;
        iMinY /= TILE_SIZE;
        iMaxX = (int) Math.ceil(((double) iMaxX) / ((double) TILE_SIZE));
        iMaxY = (int) Math.ceil(((double) iMaxY) / ((double) TILE_SIZE));
        if (iDecimation > 1) {
            if (iMinX % iDecimation != 0) {
                iMinX -= iMinX % iDecimation;
            }
            if (iMinY % iDecimation != 0) {
                iMinY -= iMinY % iDecimation;
            }
        }
        this.m_bIsModyfying = true;
        this.m_iListCount = 0;
        int iRefX = ((int) x0_r) / TILE_SIZE;
        int iRefY = ((int) y0_r) / TILE_SIZE;
        if (iRefX % iDecimation != 0) {
            iRefX -= iRefX % iDecimation;
        }
        if (iRefY % iDecimation != 0) {
            iRefY -= iRefY % iDecimation;
        }
        int x = iMinX;
        while (x <= iMaxX) {
            int y = iMinY;
            while (y <= iMaxY) {
                if (IsTileInScreen(x, y, iDecimation) && this.m_iListCount < this.m_TileList.length) {
                    this.m_TileList[this.m_iListCount].m_iScreenX = (-((iPosX / iDecimation) % TILE_SIZE)) + ((TILE_SIZE * (x - iRefX)) / iDecimation);
                    this.m_TileList[this.m_iListCount].m_iScreenY = (-((iPosY / iDecimation) % TILE_SIZE)) + ((TILE_SIZE * (y - iRefY)) / iDecimation);
                    this.m_TileList[this.m_iListCount].m_iTileX = x;
                    this.m_TileList[this.m_iListCount].m_iTileY = y;
                    this.m_TileList[this.m_iListCount].m_bTileValid = true;
                    this.m_iListCount++;
                }
                y += iDecimation;
            }
            x += iDecimation;
        }
        while (this.m_bIsLoading) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
        for (TileQueueItemNew tileQueueItemNew : this.m_TileQueue) {
            tileQueueItemNew.m_bScheduleForDelete = true;
        }
        int list_index = 0;
        while (list_index < this.m_iListCount) {
            queue_index = 0;
            while (queue_index < this.m_TileQueue.length) {
                if (this.m_TileQueue[queue_index].m_iTileX == this.m_TileList[list_index].m_iTileX && this.m_TileQueue[queue_index].m_iTileY == this.m_TileList[list_index].m_iTileY) {
                    if (!this.m_TileQueue[queue_index].m_bNeedsToBeLoaded) {
                        this.m_TileList[list_index].m_bTileValid = false;
                        this.m_TileQueue[queue_index].m_iScreenX = this.m_TileList[list_index].m_iScreenX;
                        this.m_TileQueue[queue_index].m_iScreenY = this.m_TileList[list_index].m_iScreenY;
                        this.m_TileQueue[queue_index].m_dAngle = dAngle;
                        this.m_TileQueue[queue_index].m_iDecimtaion = iDecimation;
                        this.m_TileQueue[queue_index].m_bScheduleForDelete = false;
                        this.m_TileQueue[queue_index].m_bTileInUse = true;
                        break;
                    }
                }
                queue_index++;
            }
            list_index++;
        }
        int iLastIndex = 0;
        for (list_index = 0; list_index < this.m_iListCount; list_index++) {
            if (this.m_TileList[list_index].m_bTileValid) {
                queue_index = iLastIndex;
                while (queue_index < this.m_TileQueue.length) {
                    if (this.m_TileQueue[queue_index].m_bScheduleForDelete) {
                        iLastIndex = queue_index;
                        if (this.m_TileQueue[queue_index].m_bTileInUse) {
                            if (this.m_TileQueue[queue_index].m_bNeedsToBeLoaded) {
                                this.m_iLoadCount--;
                            }
                        }
                        this.m_TileQueue[queue_index].m_iScreenX = this.m_TileList[list_index].m_iScreenX;
                        this.m_TileQueue[queue_index].m_iScreenY = this.m_TileList[list_index].m_iScreenY;
                        this.m_TileQueue[queue_index].m_iTileX = this.m_TileList[list_index].m_iTileX;
                        this.m_TileQueue[queue_index].m_iTileY = this.m_TileList[list_index].m_iTileY;
                        this.m_TileQueue[queue_index].m_dAngle = dAngle;
                        this.m_TileQueue[queue_index].m_iDecimtaion = iDecimation;
                        this.m_TileQueue[queue_index].m_bNeedsToBeLoaded = true;
                        this.m_TileQueue[queue_index].m_bScheduleForDelete = false;
                        this.m_TileQueue[queue_index].m_bTileInUse = true;
                        this.m_iLoadCount++;
                    } else {
                        queue_index++;
                    }
                }
            }
        }
        for (queue_index = iLastIndex; queue_index < this.m_TileQueue.length; queue_index++) {
            if (this.m_TileQueue[queue_index].m_bScheduleForDelete) {
                if (this.m_TileQueue[queue_index].m_bTileInUse) {
                    if (this.m_TileQueue[queue_index].m_bNeedsToBeLoaded) {
                        this.m_iLoadCount--;
                    }
                }
                this.m_TileQueue[queue_index].m_bNeedsToBeLoaded = false;
                this.m_TileQueue[queue_index].m_bTileInUse = false;
                this.m_TileQueue[queue_index].m_iTileX = -99999;
                this.m_TileQueue[queue_index].m_iTileY = -99999;
            }
        }
        this.m_bIsModyfying = false;
    }

    private boolean IsPointInScreenRect(double px, double py) {
        double N = (double) ((this.vx1 * this.vy2) - (this.vx2 * this.vy1));
        if (N == 0.0d) {
            return false;
        }
        double a = ((((double) this.vy2) * px) - (((((double) this.vx2) * py) + ((double) (this.vy2 * this.x0))) - ((double) (this.vx2 * this.y0)))) / N;
        if (a > 1.0d || a < 0.0d) {
            return false;
        }
        double b = (-((((double) this.vy1) * px) - (((((double) this.vx1) * py) + ((double) (this.vy1 * this.x0))) - ((double) (this.vx1 * this.y0))))) / N;
        if (b > 1.0d || b < 0.0d) {
            return false;
        }
        return true;
    }

    private boolean IsTileInScreen(int iTileX, int iTileY, int iDecimation) {
        int iPixelXmin = iTileX * TILE_SIZE;
        int iPixelXmax = iPixelXmin + (TILE_SIZE * iDecimation);
        int iPixelYmin = iTileY * TILE_SIZE;
        int iPixelYmax = iPixelYmin + (TILE_SIZE * iDecimation);
        if (this.x0 >= iPixelXmin && this.x0 <= iPixelXmax && this.y0 >= iPixelYmin && this.y0 <= iPixelYmax) {
            return true;
        }
        if (this.x1 >= iPixelXmin && this.x1 <= iPixelXmax && this.y1 >= iPixelYmin && this.y1 <= iPixelYmax) {
            return true;
        }
        if (this.x2 >= iPixelXmin && this.x2 <= iPixelXmax && this.y2 >= iPixelYmin && this.y2 <= iPixelYmax) {
            return true;
        }
        if ((this.x3 >= iPixelXmin && this.x3 <= iPixelXmax && this.y3 >= iPixelYmin && this.y3 <= iPixelYmax) || IsPointInScreenRect((double) iPixelXmin, (double) iPixelYmin) || IsPointInScreenRect((double) iPixelXmin, (double) iPixelYmax) || IsPointInScreenRect((double) iPixelXmax, (double) iPixelYmin) || IsPointInScreenRect((double) iPixelXmax, (double) iPixelYmax)) {
            return true;
        }
        return false;
    }

    public void UpdateQctAndClear(QuickChartFile qct) {
        this.m_Qct = qct;
        this.m_bIsModyfying = true;
        while (this.m_bIsLoading) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
        for (int queue_index = 0; queue_index < this.m_TileQueue.length; queue_index++) {
            this.m_TileQueue[queue_index].m_bTileInUse = false;
            this.m_TileQueue[queue_index].m_bNeedsToBeLoaded = false;
            this.m_TileQueue[queue_index].m_bScheduleForDelete = false;
            this.m_TileQueue[queue_index].m_iTileX = -99999;
            this.m_TileQueue[queue_index].m_iTileY = -99999;
        }
        this.m_iLoadCount = 0;
        this.m_bIsModyfying = false;
    }

    public void StopThread() {
        this.m_bThreadActive = false;
        while (this.m_bThreadRunning) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
    }

    public void StartThread() {
        if (!this.m_bThreadActive) {
            this.m_bThreadActive = true;
            StartLoadingThread();
        }
    }

    private void StartLoadingThread() {
        new Thread(new C00561()).start();
    }
}
