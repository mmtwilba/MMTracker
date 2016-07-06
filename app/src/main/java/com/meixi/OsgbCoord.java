package com.meixi;

import android.location.Location;

public class OsgbCoord {
    public static char[] OSGB_CHARS;
    private double m_dE;
    public double m_dLat;
    public double m_dLon;
    private double m_dN;
    public int m_iEasting;
    public int m_iNorthing;
    private int m_iPrecision;
    public String m_sCode;

    static {
        OSGB_CHARS = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    }

    public OsgbCoord() {
        this.m_iPrecision = 5;
    }

    public boolean SetOsgbCoord(String sCode, int iEasting, int iNorthing, int iPrecision) {
        this.m_sCode = sCode;
        this.m_iNorthing = iNorthing;
        this.m_iEasting = iEasting;
        this.m_iPrecision = iPrecision;
        if (this.m_iPrecision > 5) {
            this.m_iPrecision = 5;
        }
        if (this.m_iPrecision < 2) {
            this.m_iPrecision = 2;
        }
        return ConvertOsGridToLatLon();
    }

    public boolean SetLatLonCoord(double dLat, double dLon, double dHeight) {
        this.m_dLat = dLat;
        this.m_dLon = dLon;
        return ConvertLatLonToOsGrid(dLat, dLon, dHeight);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public String GetOsgbAsText() {
        /*
        r8 = this;
        r7 = 3;
        r6 = 2;
        r5 = 1;
        r4 = 0;
        r0 = new java.lang.String;
        r0.<init>();
        r1 = r8.m_iPrecision;
        switch(r1) {
            case 2: goto L_0x000f;
            case 3: goto L_0x002b;
            case 4: goto L_0x0047;
            case 5: goto L_0x0063;
            default: goto L_0x000e;
        };
    L_0x000e:
        return r0;
    L_0x000f:
        r1 = "%s %02d %02d";
        r2 = new java.lang.Object[r7];
        r3 = r8.m_sCode;
        r2[r4] = r3;
        r3 = r8.m_iEasting;
        r3 = java.lang.Integer.valueOf(r3);
        r2[r5] = r3;
        r3 = r8.m_iNorthing;
        r3 = java.lang.Integer.valueOf(r3);
        r2[r6] = r3;
        r0 = java.lang.String.format(r1, r2);
    L_0x002b:
        r1 = "%s %03d %03d";
        r2 = new java.lang.Object[r7];
        r3 = r8.m_sCode;
        r2[r4] = r3;
        r3 = r8.m_iEasting;
        r3 = java.lang.Integer.valueOf(r3);
        r2[r5] = r3;
        r3 = r8.m_iNorthing;
        r3 = java.lang.Integer.valueOf(r3);
        r2[r6] = r3;
        r0 = java.lang.String.format(r1, r2);
    L_0x0047:
        r1 = "%s %04d %04d";
        r2 = new java.lang.Object[r7];
        r3 = r8.m_sCode;
        r2[r4] = r3;
        r3 = r8.m_iEasting;
        r3 = java.lang.Integer.valueOf(r3);
        r2[r5] = r3;
        r3 = r8.m_iNorthing;
        r3 = java.lang.Integer.valueOf(r3);
        r2[r6] = r3;
        r0 = java.lang.String.format(r1, r2);
    L_0x0063:
        r1 = "%s %05d %05d";
        r2 = new java.lang.Object[r7];
        r3 = r8.m_sCode;
        r2[r4] = r3;
        r3 = r8.m_iEasting;
        r3 = java.lang.Integer.valueOf(r3);
        r2[r5] = r3;
        r3 = r8.m_iNorthing;
        r3 = java.lang.Integer.valueOf(r3);
        r2[r6] = r3;
        r0 = java.lang.String.format(r1, r2);
        goto L_0x000e;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.meixi.OsgbCoord.GetOsgbAsText():java.lang.String");
    }

    public String GetLatLonAsText() {
        String s = new String();
        return String.format("%f %f", new Object[]{Double.valueOf(this.m_dLat), Double.valueOf(this.m_dLon)});
    }

    public void setPrecision(int iPrecision) {
        this.m_iPrecision = iPrecision;
        if (this.m_iPrecision > 5) {
            this.m_iPrecision = 5;
        }
        if (this.m_iPrecision < 2) {
            this.m_iPrecision = 2;
        }
    }

    public boolean ParseOsgbFromString(String sOsgb) {
        sOsgb = sOsgb.trim().toUpperCase().replace(" ", "").replace(",", "").replace("-", "").replace(";", "").replace("_", "").replace(".", "");
        if (sOsgb.length() > 12) {
            return false;
        }
        if (sOsgb.length() < 6) {
            return false;
        }
        if (sOsgb.length() % 2 != 0) {
            return false;
        }
        if (!"ABCDEFGHJKLMNOPQRSTUVWXYZ".contains(sOsgb.subSequence(0, 1))) {
            return false;
        }
        if (!"ABCDEFGHJKLMNOPQRSTUVWXYZ".contains(sOsgb.subSequence(1, 2))) {
            return false;
        }
        this.m_iPrecision = (sOsgb.length() - 2) / 2;
        if (this.m_iPrecision > 5) {
            return false;
        }
        if (this.m_iPrecision < 2) {
            return false;
        }
        this.m_sCode = (String) sOsgb.subSequence(0, 2);
        try {
            int iFactor = (int) Math.round(Math.pow(10.0d, (double) (5 - this.m_iPrecision)));
            this.m_iEasting = Integer.parseInt((String) sOsgb.subSequence(2, this.m_iPrecision + 2)) * iFactor;
            this.m_iNorthing = Integer.parseInt((String) sOsgb.subSequence(this.m_iPrecision + 2, (this.m_iPrecision + 2) + this.m_iPrecision)) * iFactor;
            return ConvertOsGridToLatLon();
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private Location ConvertWgs84ToOsgb36(double dLat, double dLon, double dHeight) {
        Location loc = new Location("dummy");
        double dLatRad = (dLat / 180.0d) * 3.141592653589793d;
        double dLonRad = (dLon / 180.0d) * 3.141592653589793d;
        double e2 = ((6378137.0d * 6378137.0d) - (6356752.3142d * 6356752.3142d)) / (6378137.0d * 6378137.0d);
        double sinPhi = Math.sin(dLatRad);
        double v = 6378137.0d / Math.sqrt(1.0d - ((e2 * sinPhi) * sinPhi));
        double xA = ((v + dHeight) * Math.cos(dLatRad)) * Math.cos(dLonRad);
        double yA = ((v + dHeight) * Math.cos(dLatRad)) * Math.sin(dLonRad);
        double zA = (((1.0d - e2) * v) + dHeight) * sinPhi;
        double xB = (((xA * 1.0000204894d) - 0.00981298828125d) - (yA * -4.082616008623402E-6d)) + (zA * -1.1974897923405538E-6d);
        double yB = (((xA * -4.082616008623402E-6d) + 125.157d) + (yA * 1.0000204894d)) - (zA * -7.281901490265231E-7d);
        double zB = ((-542.06d - (xA * -1.1974897923405538E-6d)) + (yA * -7.281901490265231E-7d)) + (zA * 1.0000204894d);
        e2 = ((6377563.396d * 6377563.396d) - (6356256.91d * 6356256.91d)) / (6377563.396d * 6377563.396d);
        double thr = 4.0d / 6377563.396d;
        double p = Math.sqrt((xB * xB) + (yB * yB));
        double phi = Math.atan2(zB, (1.0d - e2) * p);
        sinPhi = Math.sin(phi);
        double phi_s = 6.283185307179586d;
        while (Math.abs(phi - phi_s) > thr) {
            v = 6377563.396d / Math.sqrt(1.0d - ((e2 * sinPhi) * sinPhi));
            phi_s = phi;
            phi = Math.atan2(((e2 * v) * Math.sin(phi)) + zB, p);
        }
        loc.setLatitude((180.0d * phi) / 3.141592653589793d);
        loc.setLongitude((Math.atan2(yB, xB) * 180.0d) / 3.141592653589793d);
        loc.setAltitude((p / Math.cos(phi)) - v);
        return loc;
    }

    private Location ConvertOsgb36ToWgs84(double dLat, double dLon, double dHeight) {
        Location loc = new Location("dummy");
        double dLatRad = (dLat / 180.0d) * 3.141592653589793d;
        double dLonRad = (dLon / 180.0d) * 3.141592653589793d;
        double e2 = ((6377563.396d * 6377563.396d) - (6356256.91d * 6356256.91d)) / (6377563.396d * 6377563.396d);
        double sinPhi = Math.sin(dLatRad);
        double v = 6377563.396d / Math.sqrt(1.0d - ((e2 * sinPhi) * sinPhi));
        double xA = ((v + dHeight) * Math.cos(dLatRad)) * Math.cos(dLonRad);
        double yA = ((v + dHeight) * Math.cos(dLatRad)) * Math.sin(dLonRad);
        double zA = (((1.0d - e2) * v) + dHeight) * sinPhi;
        double xB = (((xA * 0.9999795106d) + 446.448d) - (yA * 4.082616008623402E-6d)) + (zA * 1.1974897923405538E-6d);
        double yB = (((xA * 4.082616008623402E-6d) - 0.03263818359375d) + (yA * 0.9999795106d)) - (zA * 7.281901490265231E-7d);
        double zB = ((542.06d - (xA * 1.1974897923405538E-6d)) + (yA * 7.281901490265231E-7d)) + (zA * 0.9999795106d);
        e2 = ((6378137.0d * 6378137.0d) - (6356752.3142d * 6356752.3142d)) / (6378137.0d * 6378137.0d);
        double thr = 4.0d / 6378137.0d;
        double p = Math.sqrt((xB * xB) + (yB * yB));
        double phi = Math.atan2(zB, (1.0d - e2) * p);
        sinPhi = Math.sin(phi);
        double phi_s = 6.283185307179586d;
        while (Math.abs(phi - phi_s) > thr) {
            v = 6378137.0d / Math.sqrt(1.0d - ((e2 * sinPhi) * sinPhi));
            phi_s = phi;
            phi = Math.atan2(((e2 * v) * Math.sin(phi)) + zB, p);
        }
        loc.setLatitude((180.0d * phi) / 3.141592653589793d);
        loc.setLongitude((Math.atan2(yB, xB) * 180.0d) / 3.141592653589793d);
        loc.setAltitude((p / Math.cos(phi)) - v);
        return loc;
    }

    private boolean ConvertOsGridToNE() {
        int letref1 = 0;
        int letref2 = 0;
        if (this.m_sCode.length() != 2) {
            return false;
        }
        String sCode = this.m_sCode.toUpperCase();
        for (int i = 0; i < OSGB_CHARS.length; i++) {
            if (sCode.charAt(0) == OSGB_CHARS[i]) {
                letref1 = i;
            }
            if (sCode.charAt(1) == OSGB_CHARS[i]) {
                letref2 = i;
            }
        }
        this.m_dE = (double) (((((letref1 - 2) % 5) * 5) + (letref2 % 5)) * 100000);
        this.m_dN = ((19.0d - (Math.floor(((double) letref1) / 5.0d) * 5.0d)) - Math.floor(((double) letref2) / 5.0d)) * 100000.0d;
        this.m_dE += (double) this.m_iEasting;
        this.m_dN += (double) this.m_iNorthing;
        return true;
    }

    private boolean ConvertOsGridToLatLon() {
        if (!ConvertOsGridToNE()) {
            return false;
        }
        double n = (6377563.396d - 6356256.91d) / (6377563.396d + 6356256.91d);
        double e2 = 1.0d - ((6356256.91d * 6356256.91d) / (6377563.396d * 6377563.396d));
        double dLatRad = 0.8552113334772213d;
        double M = 0.0d;
        do {
            dLatRad += ((this.m_dN - -100000.0d) - M) / (6377563.396d * 0.9996012717d);
            double dLatDiff = dLatRad - 0.8552113334772213d;
            double dLatSum = dLatRad + 0.8552113334772213d;
            double M3 = ((((1.0d * n) * n) + (((1.0d * n) * n) * n)) * Math.sin(2.0d * dLatDiff)) * Math.cos(2.0d * dLatSum);
            double M4 = ((((1.0d * n) * n) * n) * Math.sin(3.0d * dLatDiff)) * Math.cos(3.0d * dLatSum);
            M = (6356256.91d * 0.9996012717d) * (((((((1.0d + n) + ((1.0d * n) * n)) + (((1.0d * n) * n) * n)) * dLatDiff) - (((((3.0d * n) + ((3.0d * n) * n)) + (((2.0d * n) * n) * n)) * Math.sin(dLatDiff)) * Math.cos(dLatSum))) + M3) - M4);
        } while ((this.m_dN - -100000.0d) - M >= 1.0E-5d);
        double v = (6377563.396d * 0.9996012717d) * Math.pow(1.0d - ((Math.sin(dLatRad) * Math.sin(dLatRad)) * e2), -0.5d);
        double p = ((6377563.396d * 0.9996012717d) * (1.0d - e2)) * Math.pow(1.0d - ((Math.sin(dLatRad) * Math.sin(dLatRad)) * e2), -1.5d);
        if (p == 0.0d) {
            return false;
        }
        double nsq = (v / p) - 1.0d;
        double dTan2Lat = Math.tan(dLatRad) * Math.tan(dLatRad);
        double dSecLat = 1.0d / Math.cos(dLatRad);
        double v3 = (v * v) * v;
        double X = dSecLat / v;
        double XI = (dSecLat / (6.0d * v3)) * ((v / p) + (2.0d * dTan2Lat));
        double XII = (dSecLat / (((120.0d * v3) * v) * v)) * ((5.0d + (28.0d * dTan2Lat)) + (24.0d * (dTan2Lat * dTan2Lat)));
        double XIIA = (dSecLat / (((5040.0d * v3) * v3) * v)) * (((61.0d + (662.0d * dTan2Lat)) + (1320.0d * (dTan2Lat * dTan2Lat))) + (720.0d * ((dTan2Lat * dTan2Lat) * dTan2Lat)));
        double Ediff = this.m_dE - 400000.0d;
        double Ediff4 = ((Ediff * Ediff) * Ediff) * Ediff;
        this.m_dLat = ((dLatRad - (((Math.tan(dLatRad) / ((2.0d * p) * v)) * Ediff) * Ediff)) + (((Math.tan(dLatRad) / ((24.0d * p) * v3)) * (((5.0d + (3.0d * dTan2Lat)) + nsq) - ((9.0d * dTan2Lat) * nsq))) * Ediff4)) - (((((Math.tan(dLatRad) / ((((720.0d * p) * v3) * v) * v)) * ((61.0d + (90.0d * dTan2Lat)) + (45.0d * (dTan2Lat * dTan2Lat)))) * Ediff4) * Ediff) * Ediff);
        this.m_dLon = ((((X * Ediff) - 120.51131383831226d) - (((XI * Ediff) * Ediff) * Ediff)) + ((XII * Ediff4) * Ediff)) - ((((XIIA * Ediff4) * Ediff) * Ediff) * Ediff);
        Location l = ConvertOsgb36ToWgs84((this.m_dLat * 180.0d) / 3.141592653589793d, (this.m_dLon * 180.0d) / 3.141592653589793d, 0.0d);
        this.m_dLat = l.getLatitude();
        this.m_dLon = l.getLongitude();
        return true;
    }

    private boolean ConvertLatLonToOsGrid(double dLat, double dLon, double dHeight) {
        Location l = ConvertWgs84ToOsgb36(dLat, dLon, dHeight);
        double dLatRad = (l.getLatitude() / 180.0d) * 3.141592653589793d;
        double dLonRad = (l.getLongitude() / 180.0d) * 3.141592653589793d;
        double e2 = 1.0d - ((6356256.909d * 6356256.909d) / (6377563.396d * 6377563.396d));
        double n = (6377563.396d - 6356256.909d) / (6377563.396d + 6356256.909d);
        double v = (6377563.396d * 0.9996012717d) * Math.pow(1.0d - ((Math.sin(dLatRad) * Math.sin(dLatRad)) * e2), -0.5d);
        double p = ((6377563.396d * 0.9996012717d) * (1.0d - e2)) * Math.pow(1.0d - ((Math.sin(dLatRad) * Math.sin(dLatRad)) * e2), -1.5d);
        if (p == 0.0d) {
            return false;
        }
        double nsq = (v / p) - 1.0d;
        double dLatDiff = dLatRad - 0.8552113334772213d;
        double dLatSum = dLatRad + 0.8552113334772213d;
        double M3 = ((((1.0d * n) * n) + (((1.0d * n) * n) * n)) * Math.sin(2.0d * dLatDiff)) * Math.cos(2.0d * dLatSum);
        double M4 = ((((1.0d * n) * n) * n) * Math.sin(3.0d * dLatDiff)) * Math.cos(3.0d * dLatSum);
        double M = (6356256.909d * 0.9996012717d) * (((((((1.0d + n) + ((1.0d * n) * n)) + (((1.0d * n) * n) * n)) * dLatDiff) - (((((3.0d * n) + ((3.0d * n) * n)) + (((2.0d * n) * n) * n)) * Math.sin(dLatDiff)) * Math.cos(dLatSum))) + M3) - M4);
        double dCosLat = Math.cos(dLatRad);
        double dCos5Lat = (((dCosLat * dCosLat) * dCosLat) * dCosLat) * dCosLat;
        double dTan2Lat = Math.tan(dLatRad) * Math.tan(dLatRad);
        double dLonDiff = dLonRad - -0.03490658503988659d;
        double dLonDiff4 = ((dLonDiff * dLonDiff) * dLonDiff) * dLonDiff;
        return CalcOsGridCode(((((v * dCosLat) * dLonDiff) + 400000.0d) + ((((((((v / 6.0d) * dCosLat) * dCosLat) * dCosLat) * ((v / p) - dTan2Lat)) * dLonDiff) * dLonDiff) * dLonDiff)) + (((((v / 120.0d) * dCos5Lat) * ((((5.0d - (18.0d * dTan2Lat)) + (dTan2Lat * dTan2Lat)) + (14.0d * nsq)) - ((58.0d * dTan2Lat) * nsq))) * dLonDiff) * dLonDiff4), (((((((v / 2.0d) * Math.sin(dLatRad)) * dCosLat) * dLonDiff) * dLonDiff) + (M - 4.4986605644226074E-5d)) + (((((((v / 24.0d) * Math.sin(dLatRad)) * dCosLat) * dCosLat) * dCosLat) * ((5.0d - dTan2Lat) + (9.0d * nsq))) * dLonDiff4)) + (((((((v / 720.0d) * Math.sin(dLatRad)) * dCos5Lat) * ((61.0d - (58.0d * dTan2Lat)) + (dTan2Lat * dTan2Lat))) * dLonDiff4) * dLonDiff) * dLonDiff));
    }

    private boolean CalcOsGridCode(double E, double N) {
        if (E < 0.0d || N < 0.0d) {
            return false;
        }
        int Eint = (int) Math.round(Math.floor(E / 100000.0d));
        int Nint = (int) Math.round(Math.floor(N / 100000.0d));
        if (Nint > 12 || Eint > 6) {
            return false;
        }
        this.m_sCode = new StringBuilder(String.valueOf(String.valueOf(OSGB_CHARS[((19 - Nint) - ((19 - Nint) % 5)) + ((int) Math.round(Math.floor((double) ((Eint + 10) / 5))))]))).append(String.valueOf(OSGB_CHARS[(((19 - Nint) * 5) % 25) + (Eint % 5)])).toString();
        this.m_iEasting = (int) Math.floor(E % 100000.0d);
        this.m_iNorthing = (int) Math.floor(N % 100000.0d);
        return true;
    }
}
