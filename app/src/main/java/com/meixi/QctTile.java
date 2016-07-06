package com.meixi;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class QctTile {
    Bitmap m_Bitmap;
    int m_iDecimate;

    public QctTile(int decimate) {
        this.m_iDecimate = 1;
        this.m_iDecimate = decimate;
        this.m_Bitmap = Bitmap.createBitmap(64 / decimate, 64 / decimate, Config.ARGB_8888);
    }

    public int getDecimation() {
        return this.m_iDecimate;
    }
}
