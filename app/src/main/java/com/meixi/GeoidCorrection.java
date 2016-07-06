package com.meixi;

import android.content.Context;
import java.io.IOException;
import java.io.InputStream;

public class GeoidCorrection {
    byte[] buffer;
    boolean m_bReadOk;

    public GeoidCorrection(Context context) {
        this.m_bReadOk = false;
        InputStream ins = context.getResources().openRawResource(C0047R.raw.geoid);
        this.m_bReadOk = false;
        try {
            this.buffer = new byte[ins.available()];
            ins.read(this.buffer);
            ins.close();
            this.m_bReadOk = true;
        } catch (IOException e) {
            this.m_bReadOk = false;
        }
    }

    public double Compensate(double dLat, double dLon, double dAlt) {
        return (this.m_bReadOk && this.buffer != null && dLat >= -90.0d && dLat <= 90.0d && dLon >= -180.0d && dLon <= 180.0d) ? dAlt - ((double) this.buffer[(((int) Math.round(dLat + 90.0d)) * 361) + ((int) Math.round(dLon + 180.0d))]) : dAlt;
    }
}
