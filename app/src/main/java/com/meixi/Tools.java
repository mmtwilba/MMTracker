package com.meixi;

import android.content.Context;
import android.location.Location;
import android.os.Environment;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

public class Tools {
    public static double EARTH_RADIUS;
    public static int LOAD_RESULT_ERROR;
    public static int LOAD_RESULT_NOPOINTS;
    public static int LOAD_RESULT_OK;
    public static int SD_CARD_READABLE;
    public static int SD_CARD_WRITEABLE;
    static BufferedReader m_GpxTrackFile;
    public static double[] m_dUnitDistanceFactor;
    public static double[] m_dUnitHeightFactor;
    public static double[] m_dUnitSpeedFactor;
    static long m_lErrorPostTime;
    public static String[] m_sUnitDistance;
    public static String[] m_sUnitHeight;
    public static String[] m_sUnitSpeed;

    public class Coord {
        double f2e;
        double f3n;
    }

    public static class StringCoord {
        String sLat;
        String sLong;
    }

    static {
        m_sUnitSpeed = new String[]{"kph", "mph", "mph", "knots"};
        m_sUnitHeight = new String[]{"m", "ft", "m", "ft"};
        m_sUnitDistance = new String[]{"km", "mi", "mi", "nmi"};
        m_dUnitSpeedFactor = new double[]{3.6d, 2.236936d, 2.236936d, 1.943844d};
        m_dUnitHeightFactor = new double[]{1.0d, 3.2808399d, 1.0d, 3.0d, 2808396.0d};
        m_dUnitDistanceFactor = new double[]{1.0d, 0.6213717d, 0.6213717d, 0.5399568d};
        LOAD_RESULT_OK = 0;
        LOAD_RESULT_ERROR = 1;
        LOAD_RESULT_NOPOINTS = 2;
        SD_CARD_READABLE = 1;
        SD_CARD_WRITEABLE = 2;
        EARTH_RADIUS = 6371.0d;
        m_lErrorPostTime = 0;
    }

    public static double estimateDistance(double lat1, double lon1, double lat2, double lon2) {
        double lat = (lat1 - lat2) * 111.0d;
        double lon = (lon1 - lon2) * 111.0d;
        return Math.sqrt((lat * lat) + (lon * lon));
    }

    public static double calcDistance(double lat1, double lon1, double lat2, double lon2) {
        double dist = (Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))) + ((Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))) * Math.cos(deg2rad(lon1 - lon2)));
        if (dist > 1.0d) {
            dist = 1.0d;
        }
        if (dist < -1.0d) {
            dist = -1.0d;
        }
        return ((60.0d * rad2deg(Math.acos(dist))) * 1.1515d) * 1.609344d;
    }

    private static double deg2rad(double deg) {
        return (3.141592653589793d * deg) / 180.0d;
    }

    private static double rad2deg(double rad) {
        return (180.0d * rad) / 3.141592653589793d;
    }

    public static boolean CheckSdCard(int readwrite) {
        boolean ExternalStorageWriteable;
        boolean ExternalStorageAvailable;
        String state = Environment.getExternalStorageState();
        if ("mounted".equals(state)) {
            ExternalStorageWriteable = true;
            ExternalStorageAvailable = true;
        } else if ("mounted_ro".equals(state)) {
            ExternalStorageAvailable = true;
            ExternalStorageWriteable = false;
        } else {
            ExternalStorageWriteable = false;
            ExternalStorageAvailable = false;
        }
        if (readwrite == SD_CARD_READABLE) {
            return ExternalStorageAvailable;
        }
        if (readwrite == SD_CARD_WRITEABLE) {
            return ExternalStorageWriteable;
        }
        return false;
    }

    public static String ExtractPath(String sFileName) {
        int iIndex = sFileName.lastIndexOf("/");
        if (iIndex > -1) {
            return sFileName.substring(0, iIndex + 1);
        }
        return "";
    }

    public static String ExtractTrackName(String sZeile) {
        int iIndex1 = sZeile.indexOf("![CDATA[");
        int iIndex2;
        if (iIndex1 > -1) {
            iIndex2 = sZeile.indexOf("]]", iIndex1);
            if (iIndex2 > -1) {
                if (iIndex2 >= iIndex1 + 8) {
                    return sZeile.substring(iIndex1 + 8, iIndex2);
                }
                return "";
            }
        }
        iIndex1 = sZeile.indexOf("<name>");
        if (iIndex1 > -1) {
            iIndex2 = sZeile.indexOf("</name>");
            if (iIndex2 > -1) {
                return sZeile.substring(iIndex1 + 6, iIndex2);
            }
        }
        iIndex1 = sZeile.indexOf("<sym>");
        if (iIndex1 > -1) {
            iIndex2 = sZeile.indexOf("</sym>");
            if (iIndex2 > -1) {
                return sZeile.substring(iIndex1 + 5, iIndex2);
            }
        }
        return "";
    }

    public static int OpenGPXforReReadTrack(Track track) {
        int iTrackNumber = 0;
        try {
            m_GpxTrackFile = new BufferedReader(new FileReader(track.m_sFileName));
            if (m_GpxTrackFile.ready()) {
                while (true) {
                    String sZeile = m_GpxTrackFile.readLine();
                    if (sZeile == null) {
                        m_GpxTrackFile = null;
                        return LOAD_RESULT_ERROR;
                    } else if (sZeile.contains("<trk>")) {
                        iTrackNumber++;
                        if (iTrackNumber == track.m_iNumberInFile) {
                            return LOAD_RESULT_OK;
                        }
                    }
                }
            } else {
                throw new IOException();
            }
        } catch (IOException e) {
            m_GpxTrackFile = null;
            return LOAD_RESULT_ERROR;
        }
    }

    public static int ReReadOneLineOfGPXtrack(Track track_input, ArrayList<Trackpoint> tpoints_output, double dMinDistanceKm) {
        if (m_GpxTrackFile == null) {
            return LOAD_RESULT_ERROR;
        }
        String sZeile;
        do {
            try {
                sZeile = m_GpxTrackFile.readLine();
                if (sZeile != null) {
                    if (sZeile.contains("<trkpt ")) {
                        break;
                    }
                } else {
                    return LOAD_RESULT_ERROR;
                }
            } catch (IOException e) {
                return LOAD_RESULT_ERROR;
            }
        } while (!sZeile.contains("</trk>"));
        if (sZeile.contains("</trk>")) {
            return LOAD_RESULT_NOPOINTS;
        }
        if (sZeile.contains("<trkpt ")) {
            String sTemp;
            String sTrackpoint = sZeile;
            while (true) {
                if (sTrackpoint.indexOf("/trkpt>") >= 0) {
                    break;
                }
                if (sTrackpoint.indexOf("/>") >= 0) {
                    break;
                }
                try {
                    sTemp = m_GpxTrackFile.readLine();
                    if (sTemp == null) {
                        break;
                    }
                    sTrackpoint = new StringBuilder(String.valueOf(sTrackpoint)).append(sTemp).toString();
                } catch (IOException e2) {
                    return LOAD_RESULT_ERROR;
                }
            }
            sTemp = "</trk>";
            int iStart1 = sTrackpoint.indexOf("lat=\"");
            int iStart2 = sTrackpoint.indexOf("lon=\"");
            if (iStart1 > -1 && iStart2 > -1) {
                iStart1 += 5;
                int iEnd1 = sTrackpoint.indexOf("\"", iStart1);
                iStart2 += 5;
                int iEnd2 = sTrackpoint.indexOf("\"", iStart2);
                boolean bUseTrackpoint = true;
                double dTempLat = 0.0d;
                double dTempLon = 0.0d;
                if (iEnd2 <= iStart2 || iEnd1 <= iStart1) {
                    bUseTrackpoint = false;
                } else {
                    try {
                        dTempLat = Double.valueOf((String) sTrackpoint.subSequence(iStart1, iEnd1)).doubleValue();
                    } catch (NumberFormatException e3) {
                        dTempLat = 0.0d;
                    }
                    try {
                        dTempLon = Double.valueOf((String) sTrackpoint.subSequence(iStart2, iEnd2)).doubleValue();
                    } catch (NumberFormatException e4) {
                        dTempLon = 0.0d;
                    }
                }
                if (dMinDistanceKm > 0.0d && !true && estimateDistance(dTempLat, dTempLon, 0.0d, 0.0d) < dMinDistanceKm) {
                    bUseTrackpoint = false;
                }
                if (bUseTrackpoint) {
                    double dOldLat = dTempLat;
                    double dOldLon = dTempLon;
                    Trackpoint trp = new Trackpoint();

                    if (iEnd1 > -1) {
                        trp.m_dGpsLat = dTempLat;
                    }
                    if (iEnd2 > -1) {
                        trp.m_dGpsLong = dTempLon;
                    }
                    iStart1 = sTrackpoint.indexOf("<ele>");
                    if (iStart1 > -1) {
                        iStart1 += 5;
                        iEnd1 = sTrackpoint.indexOf("</ele>", iStart1);
                        if (iEnd1 > iStart1) {
                            try {
                                trp.m_dAltitude = Double.valueOf((String) sTrackpoint.subSequence(iStart1, iEnd1)).doubleValue();
                            } catch (NumberFormatException e5) {
                                trp.m_dAltitude = 0.0d;
                            }
                        }
                    }
                    iStart1 = sTrackpoint.indexOf("<speed>");
                    if (iStart1 > -1) {
                        iStart1 += 7;
                        iEnd1 = sTrackpoint.indexOf("</speed>", iStart1);
                        if (iEnd1 > iStart1) {
                            try {
                                trp.m_dVelocity = Double.valueOf((String) sTrackpoint.subSequence(iStart1, iEnd1)).doubleValue();
                                track_input.m_bHasRealSpeedValues = true;
                            } catch (NumberFormatException e6) {
                                trp.m_dVelocity = 0.0d;
                            }
                        }
                    }
                    iStart1 = sTrackpoint.indexOf("<time>");
                    if (iStart1 > -1) {
                        iStart1 += 6;
                        iEnd1 = sTrackpoint.indexOf("</time>", iStart1);
                        if (iEnd1 > iStart1 && trp != null) {
                            try {
                                String str = (String) sTrackpoint.subSequence(iStart1, iEnd1 - 1);
                                trp.m_lTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(r24).getTime();
                            } catch (Exception e7) {
                                trp.m_lTime = 0;
                            }
                        }
                    }
                    tpoints_output.add(trp);
                }
            }
        }
        return LOAD_RESULT_OK;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int ReadGPXtoTracks(String r56, String r57, ArrayList<com.meixi.Track> r58, double r59, boolean r61, long r62) {
        /*
        r7 = 0;
        r9 = 0;
        r12 = 1;
        r20 = -4496888741037604864; // 0xc197d783fc000000 float:-2.658456E36 double:-9.9999999E7;
        r24 = 4726483295817170944; // 0x4197d783fc000000 float:-2.658456E36 double:9.9999999E7;
        r18 = -4496888741037604864; // 0xc197d783fc000000 float:-2.658456E36 double:-9.9999999E7;
        r22 = 4726483295817170944; // 0x4197d783fc000000 float:-2.658456E36 double:9.9999999E7;
        r16 = 0;
        r13 = 0;
        r11 = 0;
        r47 = 0;
        r35 = 0;
        r41 = "";
        r50 = com.meixi.Track.TRACK_WIDTH_DEFAULT;
        r0 = r50;
        r0 = (float) r0;
        r39 = r0;
        r40 = com.meixi.Track.TRACK_COLOR_DEFAULT;
        r37 = 4607182418800017408; // 0x3ff0000000000000 float:0.0 double:1.0;
        r36 = 1;
        r30 = 0;
        r15 = 0;
        r33 = 0;
        r14 = 0;
        r42 = 0;
        r34 = new java.io.BufferedReader;	 Catch:{ IOException -> 0x0056 }
        r50 = new java.io.FileReader;	 Catch:{ IOException -> 0x0056 }
        r0 = r50;
        r1 = r56;
        r0.<init>(r1);	 Catch:{ IOException -> 0x0056 }
        r0 = r34;
        r1 = r50;
        r0.<init>(r1);	 Catch:{ IOException -> 0x0056 }
        r50 = r34.ready();	 Catch:{ IOException -> 0x0056 }
        if (r50 != 0) goto L_0x05c6;
    L_0x0050:
        r50 = new java.io.IOException;	 Catch:{ IOException -> 0x0056 }
        r50.<init>();	 Catch:{ IOException -> 0x0056 }
        throw r50;	 Catch:{ IOException -> 0x0056 }
    L_0x0056:
        r27 = move-exception;
    L_0x0057:
        r50 = LOAD_RESULT_ERROR;
    L_0x0059:
        return r50;
    L_0x005a:
        r44 = r34.readLine();	 Catch:{ IOException -> 0x0056 }
        if (r44 != 0) goto L_0x0563;
    L_0x0060:
        r44 = "</trk>";
    L_0x0062:
        r50 = "lat=\"";
        r0 = r45;
        r1 = r50;
        r31 = r0.indexOf(r1);	 Catch:{ IOException -> 0x0056 }
        r50 = "lon=\"";
        r0 = r45;
        r1 = r50;
        r32 = r0.indexOf(r1);	 Catch:{ IOException -> 0x0056 }
        r50 = -1;
        r0 = r31;
        r1 = r50;
        if (r0 <= r1) goto L_0x024d;
    L_0x007e:
        r50 = -1;
        r0 = r32;
        r1 = r50;
        if (r0 <= r1) goto L_0x024d;
    L_0x0086:
        r31 = r31 + 5;
        r50 = "\"";
        r0 = r45;
        r1 = r50;
        r2 = r31;
        r28 = r0.indexOf(r1, r2);	 Catch:{ IOException -> 0x0056 }
        r32 = r32 + 5;
        r50 = "\"";
        r0 = r45;
        r1 = r50;
        r2 = r32;
        r29 = r0.indexOf(r1, r2);	 Catch:{ IOException -> 0x0056 }
        r17 = 1;
        r3 = 0;
        r5 = 0;
        r0 = r29;
        r1 = r32;
        if (r0 <= r1) goto L_0x0583;
    L_0x00ae:
        r0 = r28;
        r1 = r31;
        if (r0 <= r1) goto L_0x0583;
    L_0x00b4:
        r0 = r45;
        r1 = r31;
        r2 = r28;
        r50 = r0.subSequence(r1, r2);	 Catch:{ NumberFormatException -> 0x0579 }
        r50 = (java.lang.String) r50;	 Catch:{ NumberFormatException -> 0x0579 }
        r50 = java.lang.Double.valueOf(r50);	 Catch:{ NumberFormatException -> 0x0579 }
        r3 = r50.doubleValue();	 Catch:{ NumberFormatException -> 0x0579 }
    L_0x00c8:
        r0 = r45;
        r1 = r32;
        r2 = r29;
        r50 = r0.subSequence(r1, r2);	 Catch:{ NumberFormatException -> 0x057e }
        r50 = (java.lang.String) r50;	 Catch:{ NumberFormatException -> 0x057e }
        r50 = java.lang.Double.valueOf(r50);	 Catch:{ NumberFormatException -> 0x057e }
        r5 = r50.doubleValue();	 Catch:{ NumberFormatException -> 0x057e }
    L_0x00dc:
        r50 = 0;
        r50 = (r59 > r50 ? 1 : (r59 == r50 ? 0 : -1));
        if (r50 <= 0) goto L_0x00ee;
    L_0x00e2:
        if (r12 != 0) goto L_0x00ee;
    L_0x00e4:
        r50 = estimateDistance(r3, r5, r7, r9);	 Catch:{ IOException -> 0x0056 }
        r50 = (r50 > r59 ? 1 : (r50 == r59 ? 0 : -1));
        if (r50 >= 0) goto L_0x00ee;
    L_0x00ec:
        r17 = 0;
    L_0x00ee:
        if (r17 == 0) goto L_0x024d;
    L_0x00f0:
        r7 = r3;
        r9 = r5;
        r12 = 0;
        r49 = new com.meixi.Trackpoint;	 Catch:{ IOException -> 0x0056 }
        r49.<init>();	 Catch:{ IOException -> 0x0056 }
        r50 = -1;
        r0 = r28;
        r1 = r50;
        if (r0 <= r1) goto L_0x0104;
    L_0x0100:
        r0 = r49;
        r0.m_dGpsLat = r3;	 Catch:{ IOException -> 0x0056 }
    L_0x0104:
        r50 = -1;
        r0 = r29;
        r1 = r50;
        if (r0 <= r1) goto L_0x0110;
    L_0x010c:
        r0 = r49;
        r0.m_dGpsLong = r5;	 Catch:{ IOException -> 0x0056 }
    L_0x0110:
        if (r61 != 0) goto L_0x0114;
    L_0x0112:
        if (r14 != 0) goto L_0x01fc;
    L_0x0114:
        r50 = "<ele>";
        r0 = r45;
        r1 = r50;
        r31 = r0.indexOf(r1);	 Catch:{ IOException -> 0x0056 }
        r50 = -1;
        r0 = r31;
        r1 = r50;
        if (r0 <= r1) goto L_0x0154;
    L_0x0126:
        r31 = r31 + 5;
        r50 = "</ele>";
        r0 = r45;
        r1 = r50;
        r2 = r31;
        r28 = r0.indexOf(r1, r2);	 Catch:{ IOException -> 0x0056 }
        r0 = r28;
        r1 = r31;
        if (r0 <= r1) goto L_0x0154;
    L_0x013a:
        r0 = r45;
        r1 = r31;
        r2 = r28;
        r50 = r0.subSequence(r1, r2);	 Catch:{ NumberFormatException -> 0x0587 }
        r50 = (java.lang.String) r50;	 Catch:{ NumberFormatException -> 0x0587 }
        r50 = java.lang.Double.valueOf(r50);	 Catch:{ NumberFormatException -> 0x0587 }
        r50 = r50.doubleValue();	 Catch:{ NumberFormatException -> 0x0587 }
        r0 = r50;
        r2 = r49;
        r2.m_dAltitude = r0;	 Catch:{ NumberFormatException -> 0x0587 }
    L_0x0154:
        r50 = "<speed>";
        r0 = r45;
        r1 = r50;
        r31 = r0.indexOf(r1);	 Catch:{ IOException -> 0x0056 }
        r50 = -1;
        r0 = r31;
        r1 = r50;
        if (r0 <= r1) goto L_0x019e;
    L_0x0166:
        r31 = r31 + 7;
        r50 = "</speed>";
        r0 = r45;
        r1 = r50;
        r2 = r31;
        r28 = r0.indexOf(r1, r2);	 Catch:{ IOException -> 0x0056 }
        r0 = r28;
        r1 = r31;
        if (r0 <= r1) goto L_0x019e;
    L_0x017a:
        r0 = r45;
        r1 = r31;
        r2 = r28;
        r50 = r0.subSequence(r1, r2);	 Catch:{ NumberFormatException -> 0x0592 }
        r50 = (java.lang.String) r50;	 Catch:{ NumberFormatException -> 0x0592 }
        r50 = java.lang.Double.valueOf(r50);	 Catch:{ NumberFormatException -> 0x0592 }
        r50 = r50.doubleValue();	 Catch:{ NumberFormatException -> 0x0592 }
        r0 = r50;
        r2 = r49;
        r2.m_dVelocity = r0;	 Catch:{ NumberFormatException -> 0x0592 }
        if (r47 == 0) goto L_0x019e;
    L_0x0196:
        r50 = 1;
        r0 = r50;
        r1 = r47;
        r1.m_bHasRealSpeedValues = r0;	 Catch:{ NumberFormatException -> 0x0592 }
    L_0x019e:
        r50 = "<time>";
        r0 = r45;
        r1 = r50;
        r31 = r0.indexOf(r1);	 Catch:{ IOException -> 0x0056 }
        r50 = -1;
        r0 = r31;
        r1 = r50;
        if (r0 <= r1) goto L_0x01fc;
    L_0x01b0:
        r31 = r31 + 6;
        r50 = "</time>";
        r0 = r45;
        r1 = r50;
        r2 = r31;
        r28 = r0.indexOf(r1, r2);	 Catch:{ IOException -> 0x0056 }
        r0 = r28;
        r1 = r31;
        if (r0 <= r1) goto L_0x01fc;
    L_0x01c4:
        if (r49 == 0) goto L_0x01fc;
    L_0x01c6:
        r26 = new java.text.SimpleDateFormat;	 Catch:{ IOException -> 0x0056 }
        r50 = "yyyy-MM-dd'T'HH:mm:ss";
        r0 = r26;
        r1 = r50;
        r0.<init>(r1);	 Catch:{ IOException -> 0x0056 }
        r50 = r28 + -1;
        r0 = r45;
        r1 = r31;
        r2 = r50;
        r50 = r0.subSequence(r1, r2);	 Catch:{ ParseException -> 0x059d }
        r50 = (java.lang.String) r50;	 Catch:{ ParseException -> 0x059d }
        r0 = r26;
        r1 = r50;
        r43 = r0.parse(r1);	 Catch:{ ParseException -> 0x059d }
        r50 = r43.getTime();	 Catch:{ ParseException -> 0x059d }
        r0 = r50;
        r2 = r49;
        r2.m_lTime = r0;	 Catch:{ ParseException -> 0x059d }
        if (r14 != 0) goto L_0x01fc;
    L_0x01f3:
        r14 = 1;
        if (r47 == 0) goto L_0x01fc;
    L_0x01f6:
        r0 = r30;
        r1 = r47;
        r1.m_iFirstDateIndex = r0;	 Catch:{ ParseException -> 0x059d }
    L_0x01fc:
        r0 = r49;
        r0 = r0.m_dGpsLong;	 Catch:{ IOException -> 0x0056 }
        r50 = r0;
        r50 = (r50 > r20 ? 1 : (r50 == r20 ? 0 : -1));
        if (r50 <= 0) goto L_0x020c;
    L_0x0206:
        r0 = r49;
        r0 = r0.m_dGpsLong;	 Catch:{ IOException -> 0x0056 }
        r20 = r0;
    L_0x020c:
        r0 = r49;
        r0 = r0.m_dGpsLong;	 Catch:{ IOException -> 0x0056 }
        r50 = r0;
        r50 = (r50 > r24 ? 1 : (r50 == r24 ? 0 : -1));
        if (r50 >= 0) goto L_0x021c;
    L_0x0216:
        r0 = r49;
        r0 = r0.m_dGpsLong;	 Catch:{ IOException -> 0x0056 }
        r24 = r0;
    L_0x021c:
        r0 = r49;
        r0 = r0.m_dGpsLat;	 Catch:{ IOException -> 0x0056 }
        r50 = r0;
        r50 = (r50 > r18 ? 1 : (r50 == r18 ? 0 : -1));
        if (r50 <= 0) goto L_0x022c;
    L_0x0226:
        r0 = r49;
        r0 = r0.m_dGpsLat;	 Catch:{ IOException -> 0x0056 }
        r18 = r0;
    L_0x022c:
        r0 = r49;
        r0 = r0.m_dGpsLat;	 Catch:{ IOException -> 0x0056 }
        r50 = r0;
        r50 = (r50 > r22 ? 1 : (r50 == r22 ? 0 : -1));
        if (r50 >= 0) goto L_0x023c;
    L_0x0236:
        r0 = r49;
        r0 = r0.m_dGpsLat;	 Catch:{ IOException -> 0x0056 }
        r22 = r0;
    L_0x023c:
        if (r47 == 0) goto L_0x024d;
    L_0x023e:
        r30 = r30 + 1;
        r0 = r47;
        r0 = r0.trackpoints;	 Catch:{ IOException -> 0x0056 }
        r50 = r0;
        r0 = r50;
        r1 = r49;
        r0.add(r1);	 Catch:{ IOException -> 0x0056 }
    L_0x024d:
        r50 = "</trk>";
        r0 = r46;
        r1 = r50;
        r50 = r0.contains(r1);	 Catch:{ IOException -> 0x0056 }
        if (r50 == 0) goto L_0x05c6;
    L_0x0259:
        if (r47 == 0) goto L_0x05c6;
    L_0x025b:
        r50 = new android.graphics.Rect;	 Catch:{ IOException -> 0x0056 }
        r50.<init>();	 Catch:{ IOException -> 0x0056 }
        r0 = r50;
        r1 = r47;
        r1.m_Areal = r0;	 Catch:{ IOException -> 0x0056 }
        r0 = r47;
        r0 = r0.m_Areal;	 Catch:{ IOException -> 0x0056 }
        r50 = r0;
        r51 = 4666723172467343360; // 0x40c3880000000000 float:0.0 double:10000.0;
        r51 = r51 * r24;
        r51 = java.lang.Math.round(r51);	 Catch:{ IOException -> 0x0056 }
        r0 = r51;
        r0 = (int) r0;	 Catch:{ IOException -> 0x0056 }
        r51 = r0;
        r52 = 4666723172467343360; // 0x40c3880000000000 float:0.0 double:10000.0;
        r52 = r52 * r22;
        r52 = java.lang.Math.round(r52);	 Catch:{ IOException -> 0x0056 }
        r0 = r52;
        r0 = (int) r0;	 Catch:{ IOException -> 0x0056 }
        r52 = r0;
        r53 = 4666723172467343360; // 0x40c3880000000000 float:0.0 double:10000.0;
        r53 = r53 * r20;
        r53 = java.lang.Math.round(r53);	 Catch:{ IOException -> 0x0056 }
        r0 = r53;
        r0 = (int) r0;	 Catch:{ IOException -> 0x0056 }
        r53 = r0;
        r54 = 4666723172467343360; // 0x40c3880000000000 float:0.0 double:10000.0;
        r54 = r54 * r18;
        r54 = java.lang.Math.round(r54);	 Catch:{ IOException -> 0x0056 }
        r0 = r54;
        r0 = (int) r0;	 Catch:{ IOException -> 0x0056 }
        r54 = r0;
        r50.set(r51, r52, r53, r54);	 Catch:{ IOException -> 0x0056 }
        r50 = "";
        r0 = r41;
        r1 = r50;
        if (r0 != r1) goto L_0x05a8;
    L_0x02b7:
        r0 = r57;
        r1 = r47;
        r1.m_sName = r0;	 Catch:{ IOException -> 0x0056 }
    L_0x02bd:
        r0 = r47;
        r0 = r0.m_sName;	 Catch:{ IOException -> 0x0056 }
        r50 = r0;
        r0 = r50;
        r1 = r47;
        r1.m_sLoadName = r0;	 Catch:{ IOException -> 0x0056 }
        r0 = r35;
        r1 = r47;
        r1.m_CreatedByMMTracker = r0;	 Catch:{ IOException -> 0x0056 }
        r50 = 0;
        r0 = r50;
        r1 = r47;
        r1.m_AllDetailsAvailable = r0;	 Catch:{ IOException -> 0x0056 }
        r0 = r39;
        r1 = r47;
        r1.m_fWidth = r0;	 Catch:{ IOException -> 0x0056 }
        r0 = r40;
        r1 = r47;
        r1.m_iColor = r0;	 Catch:{ IOException -> 0x0056 }
        r0 = r36;
        r1 = r47;
        r1.m_bVisible = r0;	 Catch:{ IOException -> 0x0056 }
        r0 = r56;
        r1 = r47;
        r1.m_sFileName = r0;	 Catch:{ IOException -> 0x0056 }
        r0 = r33;
        r1 = r47;
        r1.m_iNumberInFile = r0;	 Catch:{ IOException -> 0x0056 }
        r50 = 0;
        r0 = r50;
        r1 = r47;
        r1.m_bHasRealSpeedValues = r0;	 Catch:{ IOException -> 0x0056 }
        r0 = r58;
        r1 = r47;
        r0.add(r1);	 Catch:{ IOException -> 0x0056 }
        r16 = 0;
        r13 = 0;
        r11 = 0;
        r15 = 0;
        r47 = 0;
        r48 = r47;
    L_0x030d:
        r46 = r34.readLine();	 Catch:{ IOException -> 0x05c1 }
        if (r46 != 0) goto L_0x0317;
    L_0x0313:
        if (r15 == 0) goto L_0x05b0;
    L_0x0315:
        r46 = "</trk>";
    L_0x0317:
        r50 = "<trk>";
        r0 = r46;
        r1 = r50;
        r50 = r0.contains(r1);	 Catch:{ IOException -> 0x05c1 }
        if (r50 == 0) goto L_0x05ca;
    L_0x0323:
        r47 = new com.meixi.Track;	 Catch:{ IOException -> 0x05c1 }
        r50 = "";
        r0 = r47;
        r1 = r50;
        r0.<init>(r1);	 Catch:{ IOException -> 0x05c1 }
        r12 = 1;
        r15 = 1;
        r14 = 0;
        r42 = 0;
        r33 = r33 + 1;
    L_0x0335:
        if (r16 != 0) goto L_0x050d;
    L_0x0337:
        r50 = "<metadata>";
        r0 = r46;
        r1 = r50;
        r50 = r0.indexOf(r1);	 Catch:{ IOException -> 0x0056 }
        r51 = -1;
        r0 = r50;
        r1 = r51;
        if (r0 <= r1) goto L_0x034a;
    L_0x0349:
        r13 = 1;
    L_0x034a:
        r50 = "</metadata>";
        r0 = r46;
        r1 = r50;
        r50 = r0.indexOf(r1);	 Catch:{ IOException -> 0x0056 }
        r51 = -1;
        r0 = r50;
        r1 = r51;
        if (r0 <= r1) goto L_0x035d;
    L_0x035c:
        r13 = 0;
    L_0x035d:
        r50 = "<extensions>";
        r0 = r46;
        r1 = r50;
        r50 = r0.indexOf(r1);	 Catch:{ IOException -> 0x0056 }
        r51 = -1;
        r0 = r50;
        r1 = r51;
        if (r0 <= r1) goto L_0x0370;
    L_0x036f:
        r11 = 1;
    L_0x0370:
        r50 = "</extensions>";
        r0 = r46;
        r1 = r50;
        r50 = r0.indexOf(r1);	 Catch:{ IOException -> 0x0056 }
        r51 = -1;
        r0 = r50;
        r1 = r51;
        if (r0 <= r1) goto L_0x03a1;
    L_0x0382:
        r11 = 0;
        if (r42 == 0) goto L_0x03a1;
    L_0x0385:
        r0 = r40;
        r0 = (long) r0;	 Catch:{ IOException -> 0x0056 }
        r50 = r0;
        r52 = 4643176031446892544; // 0x406fe00000000000 float:0.0 double:255.0;
        r52 = r52 * r37;
        r0 = r52;
        r0 = (long) r0;	 Catch:{ IOException -> 0x0056 }
        r52 = r0;
        r54 = 24;
        r52 = r52 << r54;
        r50 = r50 | r52;
        r0 = r50;
        r0 = (int) r0;	 Catch:{ IOException -> 0x0056 }
        r40 = r0;
    L_0x03a1:
        if (r11 == 0) goto L_0x0453;
    L_0x03a3:
        r50 = "<color>";
        r0 = r46;
        r1 = r50;
        r31 = r0.indexOf(r1);	 Catch:{ IOException -> 0x0056 }
        r50 = -1;
        r0 = r31;
        r1 = r50;
        if (r0 <= r1) goto L_0x03e0;
    L_0x03b5:
        r31 = r31 + 7;
        r50 = "</color>";
        r0 = r46;
        r1 = r50;
        r28 = r0.indexOf(r1);	 Catch:{ IOException -> 0x0056 }
        r50 = -1;
        r0 = r28;
        r1 = r50;
        if (r0 <= r1) goto L_0x03e0;
    L_0x03c9:
        r0 = r46;
        r1 = r31;
        r2 = r28;
        r50 = r0.subSequence(r1, r2);	 Catch:{ NumberFormatException -> 0x0547 }
        r50 = (java.lang.String) r50;	 Catch:{ NumberFormatException -> 0x0547 }
        r51 = 16;
        r50 = java.lang.Long.parseLong(r50, r51);	 Catch:{ NumberFormatException -> 0x0547 }
        r0 = r50;
        r0 = (int) r0;
        r40 = r0;
    L_0x03e0:
        r50 = "<opacity>";
        r0 = r46;
        r1 = r50;
        r31 = r0.indexOf(r1);	 Catch:{ IOException -> 0x0056 }
        r50 = -1;
        r0 = r31;
        r1 = r50;
        if (r0 <= r1) goto L_0x0418;
    L_0x03f2:
        r42 = 1;
        r31 = r31 + 9;
        r50 = "</opacity>";
        r0 = r46;
        r1 = r50;
        r28 = r0.indexOf(r1);	 Catch:{ IOException -> 0x0056 }
        r50 = -1;
        r0 = r28;
        r1 = r50;
        if (r0 <= r1) goto L_0x0418;
    L_0x0408:
        r0 = r46;
        r1 = r31;
        r2 = r28;
        r50 = r0.subSequence(r1, r2);	 Catch:{ NumberFormatException -> 0x054c }
        r50 = (java.lang.String) r50;	 Catch:{ NumberFormatException -> 0x054c }
        r37 = java.lang.Double.parseDouble(r50);	 Catch:{ NumberFormatException -> 0x054c }
    L_0x0418:
        r50 = "<width>";
        r0 = r46;
        r1 = r50;
        r31 = r0.indexOf(r1);	 Catch:{ IOException -> 0x0056 }
        r50 = -1;
        r0 = r31;
        r1 = r50;
        if (r0 <= r1) goto L_0x0453;
    L_0x042a:
        r31 = r31 + 7;
        r50 = "</width>";
        r0 = r46;
        r1 = r50;
        r28 = r0.indexOf(r1);	 Catch:{ IOException -> 0x0056 }
        r50 = -1;
        r0 = r28;
        r1 = r50;
        if (r0 <= r1) goto L_0x0453;
    L_0x043e:
        r0 = r46;
        r1 = r31;
        r2 = r28;
        r50 = r0.subSequence(r1, r2);	 Catch:{ NumberFormatException -> 0x0551 }
        r50 = (java.lang.String) r50;	 Catch:{ NumberFormatException -> 0x0551 }
        r50 = java.lang.Double.parseDouble(r50);	 Catch:{ NumberFormatException -> 0x0551 }
        r0 = r50;
        r0 = (float) r0;
        r39 = r0;
    L_0x0453:
        if (r13 == 0) goto L_0x050d;
    L_0x0455:
        r50 = "<mmtcolor>";
        r0 = r46;
        r1 = r50;
        r31 = r0.indexOf(r1);	 Catch:{ IOException -> 0x0056 }
        r50 = -1;
        r0 = r31;
        r1 = r50;
        if (r0 <= r1) goto L_0x0494;
    L_0x0467:
        r35 = 1;
        r31 = r31 + 10;
        r50 = "</mmtcolor>";
        r0 = r46;
        r1 = r50;
        r28 = r0.indexOf(r1);	 Catch:{ IOException -> 0x0056 }
        r50 = -1;
        r0 = r28;
        r1 = r50;
        if (r0 <= r1) goto L_0x0494;
    L_0x047d:
        r0 = r46;
        r1 = r31;
        r2 = r28;
        r50 = r0.subSequence(r1, r2);	 Catch:{ NumberFormatException -> 0x0556 }
        r50 = (java.lang.String) r50;	 Catch:{ NumberFormatException -> 0x0556 }
        r51 = 16;
        r50 = java.lang.Long.parseLong(r50, r51);	 Catch:{ NumberFormatException -> 0x0556 }
        r0 = r50;
        r0 = (int) r0;
        r40 = r0;
    L_0x0494:
        r50 = "<mmtwidth>";
        r0 = r46;
        r1 = r50;
        r31 = r0.indexOf(r1);	 Catch:{ IOException -> 0x0056 }
        r50 = -1;
        r0 = r31;
        r1 = r50;
        if (r0 <= r1) goto L_0x04d1;
    L_0x04a6:
        r35 = 1;
        r31 = r31 + 10;
        r50 = "</mmtwidth>";
        r0 = r46;
        r1 = r50;
        r28 = r0.indexOf(r1);	 Catch:{ IOException -> 0x0056 }
        r50 = -1;
        r0 = r28;
        r1 = r50;
        if (r0 <= r1) goto L_0x04d1;
    L_0x04bc:
        r0 = r46;
        r1 = r31;
        r2 = r28;
        r50 = r0.subSequence(r1, r2);	 Catch:{ NumberFormatException -> 0x055b }
        r50 = (java.lang.String) r50;	 Catch:{ NumberFormatException -> 0x055b }
        r50 = java.lang.Double.parseDouble(r50);	 Catch:{ NumberFormatException -> 0x055b }
        r0 = r50;
        r0 = (float) r0;
        r39 = r0;
    L_0x04d1:
        r50 = "<mmtvisible>";
        r0 = r46;
        r1 = r50;
        r31 = r0.indexOf(r1);	 Catch:{ IOException -> 0x0056 }
        r50 = -1;
        r0 = r31;
        r1 = r50;
        if (r0 <= r1) goto L_0x050d;
    L_0x04e3:
        r35 = 1;
        r31 = r31 + 12;
        r50 = "</mmtvisible>";
        r0 = r46;
        r1 = r50;
        r28 = r0.indexOf(r1);	 Catch:{ IOException -> 0x0056 }
        r50 = -1;
        r0 = r28;
        r1 = r50;
        if (r0 <= r1) goto L_0x050d;
    L_0x04f9:
        r0 = r46;
        r1 = r31;
        r2 = r28;
        r50 = r0.subSequence(r1, r2);	 Catch:{ IOException -> 0x0056 }
        r51 = "true";
        r50 = r50.equals(r51);	 Catch:{ IOException -> 0x0056 }
        if (r50 == 0) goto L_0x0560;
    L_0x050b:
        r36 = 1;
    L_0x050d:
        r50 = "<name>";
        r0 = r46;
        r1 = r50;
        r50 = r0.contains(r1);	 Catch:{ IOException -> 0x0056 }
        if (r50 == 0) goto L_0x051d;
    L_0x0519:
        r41 = ExtractTrackName(r46);	 Catch:{ IOException -> 0x0056 }
    L_0x051d:
        r50 = "<trkpt ";
        r0 = r46;
        r1 = r50;
        r50 = r0.contains(r1);	 Catch:{ IOException -> 0x0056 }
        if (r50 == 0) goto L_0x024d;
    L_0x0529:
        r16 = 1;
        r45 = r46;
    L_0x052d:
        r50 = "/trkpt>";
        r0 = r45;
        r1 = r50;
        r50 = r0.indexOf(r1);	 Catch:{ IOException -> 0x0056 }
        if (r50 >= 0) goto L_0x0062;
    L_0x0539:
        r50 = "/>";
        r0 = r45;
        r1 = r50;
        r50 = r0.indexOf(r1);	 Catch:{ IOException -> 0x0056 }
        if (r50 < 0) goto L_0x005a;
    L_0x0545:
        goto L_0x0062;
    L_0x0547:
        r27 = move-exception;
        r40 = com.meixi.Track.TRACK_COLOR_DEFAULT;	 Catch:{ IOException -> 0x0056 }
        goto L_0x03e0;
    L_0x054c:
        r27 = move-exception;
        r37 = 4607182418800017408; // 0x3ff0000000000000 float:0.0 double:1.0;
        goto L_0x0418;
    L_0x0551:
        r27 = move-exception;
        r39 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        goto L_0x0453;
    L_0x0556:
        r27 = move-exception;
        r40 = com.meixi.Track.TRACK_COLOR_DEFAULT;	 Catch:{ IOException -> 0x0056 }
        goto L_0x0494;
    L_0x055b:
        r27 = move-exception;
        r39 = 1082130432; // 0x40800000 float:4.0 double:5.34643471E-315;
        goto L_0x04d1;
    L_0x0560:
        r36 = 0;
        goto L_0x050d;
    L_0x0563:
        r50 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x0056 }
        r51 = java.lang.String.valueOf(r45);	 Catch:{ IOException -> 0x0056 }
        r50.<init>(r51);	 Catch:{ IOException -> 0x0056 }
        r0 = r50;
        r1 = r44;
        r50 = r0.append(r1);	 Catch:{ IOException -> 0x0056 }
        r45 = r50.toString();	 Catch:{ IOException -> 0x0056 }
        goto L_0x052d;
    L_0x0579:
        r27 = move-exception;
        r3 = 0;
        goto L_0x00c8;
    L_0x057e:
        r27 = move-exception;
        r5 = 0;
        goto L_0x00dc;
    L_0x0583:
        r17 = 0;
        goto L_0x00dc;
    L_0x0587:
        r27 = move-exception;
        r50 = 0;
        r0 = r50;
        r2 = r49;
        r2.m_dAltitude = r0;	 Catch:{ IOException -> 0x0056 }
        goto L_0x0154;
    L_0x0592:
        r27 = move-exception;
        r50 = 0;
        r0 = r50;
        r2 = r49;
        r2.m_dVelocity = r0;	 Catch:{ IOException -> 0x0056 }
        goto L_0x019e;
    L_0x059d:
        r27 = move-exception;
        r50 = 0;
        r0 = r50;
        r2 = r49;
        r2.m_lTime = r0;	 Catch:{ IOException -> 0x0056 }
        goto L_0x01fc;
    L_0x05a8:
        r0 = r41;
        r1 = r47;
        r1.m_sName = r0;	 Catch:{ IOException -> 0x0056 }
        goto L_0x02bd;
    L_0x05b0:
        r34.close();	 Catch:{ IOException -> 0x05c1 }
        if (r30 <= 0) goto L_0x05bb;
    L_0x05b5:
        r50 = LOAD_RESULT_OK;	 Catch:{ IOException -> 0x05c1 }
        r47 = r48;
        goto L_0x0059;
    L_0x05bb:
        r50 = LOAD_RESULT_NOPOINTS;	 Catch:{ IOException -> 0x05c1 }
        r47 = r48;
        goto L_0x0059;
    L_0x05c1:
        r27 = move-exception;
        r47 = r48;
        goto L_0x0057;
    L_0x05c6:
        r48 = r47;
        goto L_0x030d;
    L_0x05ca:
        r47 = r48;
        goto L_0x0335;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.meixi.Tools.ReadGPXtoTracks(java.lang.String, java.lang.String, java.util.ArrayList, double, boolean, long):int");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int ReadGPXtoRoutes(String r39, String r40, ArrayList<Route> r41) {
        /*
        r7 = -4496888741037604864; // 0xc197d783fc000000 float:-2.658456E36 double:-9.9999999E7;
        r11 = 4726483295817170944; // 0x4197d783fc000000 float:-2.658456E36 double:9.9999999E7;
        r5 = -4496888741037604864; // 0xc197d783fc000000 float:-2.658456E36 double:-9.9999999E7;
        r9 = 4726483295817170944; // 0x4197d783fc000000 float:-2.658456E36 double:9.9999999E7;
        r4 = 0;
        r3 = 0;
        r26 = 0;
        r20 = 0;
        r25 = "";
        r33 = com.meixi.Route.ROUTE_WIDTH_DEFAULT;
        r0 = r33;
        r0 = (float) r0;
        r23 = r0;
        r24 = com.meixi.Route.ROUTE_COLOR_DEFAULT;
        r22 = 1;
        r21 = 0;
        r16 = 0;
        r19 = new java.io.BufferedReader;	 Catch:{ IOException -> 0x0049 }
        r33 = new java.io.FileReader;	 Catch:{ IOException -> 0x0049 }
        r0 = r33;
        r1 = r39;
        r0.<init>(r1);	 Catch:{ IOException -> 0x0049 }
        r0 = r19;
        r1 = r33;
        r0.<init>(r1);	 Catch:{ IOException -> 0x0049 }
        r33 = r19.ready();	 Catch:{ IOException -> 0x0049 }
        if (r33 != 0) goto L_0x02f0;
    L_0x0043:
        r33 = new java.io.IOException;	 Catch:{ IOException -> 0x0049 }
        r33.<init>();	 Catch:{ IOException -> 0x0049 }
        throw r33;	 Catch:{ IOException -> 0x0049 }
    L_0x0049:
        r13 = move-exception;
    L_0x004a:
        r33 = LOAD_RESULT_ERROR;
    L_0x004c:
        return r33;
    L_0x004d:
        r33 = "<rte>";
        r33 = r32.contains(r33);	 Catch:{ IOException -> 0x035c }
        if (r33 == 0) goto L_0x0361;
    L_0x0055:
        r26 = new com.meixi.Route;	 Catch:{ IOException -> 0x035c }
        r33 = "";
        r0 = r26;
        r1 = r33;
        r0.<init>(r1);	 Catch:{ IOException -> 0x035c }
    L_0x0060:
        r33 = "</rte>";
        r33 = r32.contains(r33);	 Catch:{ IOException -> 0x0049 }
        if (r33 == 0) goto L_0x0115;
    L_0x0068:
        if (r26 == 0) goto L_0x0115;
    L_0x006a:
        r33 = new android.graphics.Rect;	 Catch:{ IOException -> 0x0049 }
        r33.<init>();	 Catch:{ IOException -> 0x0049 }
        r0 = r33;
        r1 = r26;
        r1.m_Areal = r0;	 Catch:{ IOException -> 0x0049 }
        r0 = r26;
        r0 = r0.m_Areal;	 Catch:{ IOException -> 0x0049 }
        r33 = r0;
        r34 = 4666723172467343360; // 0x40c3880000000000 float:0.0 double:10000.0;
        r34 = r34 * r11;
        r34 = java.lang.Math.round(r34);	 Catch:{ IOException -> 0x0049 }
        r0 = r34;
        r0 = (int) r0;	 Catch:{ IOException -> 0x0049 }
        r34 = r0;
        r35 = 4666723172467343360; // 0x40c3880000000000 float:0.0 double:10000.0;
        r35 = r35 * r9;
        r35 = java.lang.Math.round(r35);	 Catch:{ IOException -> 0x0049 }
        r0 = r35;
        r0 = (int) r0;	 Catch:{ IOException -> 0x0049 }
        r35 = r0;
        r36 = 4666723172467343360; // 0x40c3880000000000 float:0.0 double:10000.0;
        r36 = r36 * r7;
        r36 = java.lang.Math.round(r36);	 Catch:{ IOException -> 0x0049 }
        r0 = r36;
        r0 = (int) r0;	 Catch:{ IOException -> 0x0049 }
        r36 = r0;
        r37 = 4666723172467343360; // 0x40c3880000000000 float:0.0 double:10000.0;
        r37 = r37 * r5;
        r37 = java.lang.Math.round(r37);	 Catch:{ IOException -> 0x0049 }
        r0 = r37;
        r0 = (int) r0;	 Catch:{ IOException -> 0x0049 }
        r37 = r0;
        r33.set(r34, r35, r36, r37);	 Catch:{ IOException -> 0x0049 }
        r33 = "";
        r0 = r25;
        r1 = r33;
        if (r0 != r1) goto L_0x010e;
    L_0x00c6:
        r0 = r40;
        r1 = r26;
        r1.m_sName = r0;	 Catch:{ IOException -> 0x0049 }
    L_0x00cc:
        r0 = r20;
        r1 = r26;
        r1.m_CreatedByMMTracker = r0;	 Catch:{ IOException -> 0x0049 }
        r0 = r23;
        r1 = r26;
        r1.m_fWidth = r0;	 Catch:{ IOException -> 0x0049 }
        r0 = r24;
        r1 = r26;
        r1.m_iColor = r0;	 Catch:{ IOException -> 0x0049 }
        r0 = r22;
        r1 = r26;
        r1.m_bVisible = r0;	 Catch:{ IOException -> 0x0049 }
        r0 = r39;
        r1 = r26;
        r1.m_sFileName = r0;	 Catch:{ IOException -> 0x0049 }
        r0 = r21;
        r1 = r26;
        r1.m_bLocked = r0;	 Catch:{ IOException -> 0x0049 }
        r0 = r41;
        r1 = r26;
        r0.add(r1);	 Catch:{ IOException -> 0x0049 }
        r4 = 0;
        r3 = 0;
        r26 = 0;
        r27 = r26;
    L_0x00fd:
        r32 = r19.readLine();	 Catch:{ IOException -> 0x035c }
        if (r32 != 0) goto L_0x004d;
    L_0x0103:
        r19.close();	 Catch:{ IOException -> 0x035c }
        if (r16 <= 0) goto L_0x0356;
    L_0x0108:
        r33 = LOAD_RESULT_OK;	 Catch:{ IOException -> 0x035c }
        r26 = r27;
        goto L_0x004c;
    L_0x010e:
        r0 = r25;
        r1 = r26;
        r1.m_sName = r0;	 Catch:{ IOException -> 0x0049 }
        goto L_0x00cc;
    L_0x0115:
        if (r4 != 0) goto L_0x01fb;
    L_0x0117:
        r33 = "<metadata>";
        r33 = r32.indexOf(r33);	 Catch:{ IOException -> 0x0049 }
        r34 = -1;
        r0 = r33;
        r1 = r34;
        if (r0 <= r1) goto L_0x0126;
    L_0x0125:
        r3 = 1;
    L_0x0126:
        r33 = "</metadata>";
        r33 = r32.indexOf(r33);	 Catch:{ IOException -> 0x0049 }
        r34 = -1;
        r0 = r33;
        r1 = r34;
        if (r0 <= r1) goto L_0x0135;
    L_0x0134:
        r3 = 0;
    L_0x0135:
        if (r3 == 0) goto L_0x01fb;
    L_0x0137:
        r33 = "<mmtcolor>";
        r17 = r32.indexOf(r33);	 Catch:{ IOException -> 0x0049 }
        r33 = -1;
        r0 = r17;
        r1 = r33;
        if (r0 <= r1) goto L_0x016a;
    L_0x0145:
        r20 = 1;
        r17 = r17 + 10;
        r33 = "</mmtcolor>";
        r14 = r32.indexOf(r33);	 Catch:{ IOException -> 0x0049 }
        r33 = -1;
        r0 = r33;
        if (r14 <= r0) goto L_0x016a;
    L_0x0155:
        r0 = r32;
        r1 = r17;
        r33 = r0.subSequence(r1, r14);	 Catch:{ NumberFormatException -> 0x02f4 }
        r33 = (java.lang.String) r33;	 Catch:{ NumberFormatException -> 0x02f4 }
        r34 = 16;
        r33 = java.lang.Long.parseLong(r33, r34);	 Catch:{ NumberFormatException -> 0x02f4 }
        r0 = r33;
        r0 = (int) r0;
        r24 = r0;
    L_0x016a:
        r33 = "<mmtwidth>";
        r17 = r32.indexOf(r33);	 Catch:{ IOException -> 0x0049 }
        r33 = -1;
        r0 = r17;
        r1 = r33;
        if (r0 <= r1) goto L_0x019b;
    L_0x0178:
        r20 = 1;
        r17 = r17 + 10;
        r33 = "</mmtwidth>";
        r14 = r32.indexOf(r33);	 Catch:{ IOException -> 0x0049 }
        r33 = -1;
        r0 = r33;
        if (r14 <= r0) goto L_0x019b;
    L_0x0188:
        r0 = r32;
        r1 = r17;
        r33 = r0.subSequence(r1, r14);	 Catch:{ NumberFormatException -> 0x02f9 }
        r33 = (java.lang.String) r33;	 Catch:{ NumberFormatException -> 0x02f9 }
        r33 = java.lang.Double.parseDouble(r33);	 Catch:{ NumberFormatException -> 0x02f9 }
        r0 = r33;
        r0 = (float) r0;
        r23 = r0;
    L_0x019b:
        r33 = "<mmtlocked>";
        r17 = r32.indexOf(r33);	 Catch:{ IOException -> 0x0049 }
        r33 = -1;
        r0 = r17;
        r1 = r33;
        if (r0 <= r1) goto L_0x01cb;
    L_0x01a9:
        r20 = 1;
        r17 = r17 + 11;
        r33 = "</mmtlocked>";
        r14 = r32.indexOf(r33);	 Catch:{ IOException -> 0x0049 }
        r33 = -1;
        r0 = r33;
        if (r14 <= r0) goto L_0x01cb;
    L_0x01b9:
        r0 = r32;
        r1 = r17;
        r33 = r0.subSequence(r1, r14);	 Catch:{ IOException -> 0x0049 }
        r34 = "true";
        r33 = r33.equals(r34);	 Catch:{ IOException -> 0x0049 }
        if (r33 == 0) goto L_0x02fe;
    L_0x01c9:
        r21 = 1;
    L_0x01cb:
        r33 = "<mmtvisible>";
        r17 = r32.indexOf(r33);	 Catch:{ IOException -> 0x0049 }
        r33 = -1;
        r0 = r17;
        r1 = r33;
        if (r0 <= r1) goto L_0x01fb;
    L_0x01d9:
        r20 = 1;
        r17 = r17 + 12;
        r33 = "</mmtvisible>";
        r14 = r32.indexOf(r33);	 Catch:{ IOException -> 0x0049 }
        r33 = -1;
        r0 = r33;
        if (r14 <= r0) goto L_0x01fb;
    L_0x01e9:
        r0 = r32;
        r1 = r17;
        r33 = r0.subSequence(r1, r14);	 Catch:{ IOException -> 0x0049 }
        r34 = "true";
        r33 = r33.equals(r34);	 Catch:{ IOException -> 0x0049 }
        if (r33 == 0) goto L_0x0302;
    L_0x01f9:
        r22 = 1;
    L_0x01fb:
        r33 = "<name>";
        r33 = r32.contains(r33);	 Catch:{ IOException -> 0x0049 }
        if (r33 == 0) goto L_0x0209;
    L_0x0203:
        if (r4 != 0) goto L_0x0209;
    L_0x0205:
        r25 = ExtractTrackName(r32);	 Catch:{ IOException -> 0x0049 }
    L_0x0209:
        r33 = "<rtept ";
        r33 = r32.contains(r33);	 Catch:{ IOException -> 0x0049 }
        if (r33 == 0) goto L_0x02f0;
    L_0x0211:
        r4 = 1;
        r30 = r32;
    L_0x0214:
        r33 = "lat=";
        r0 = r30;
        r1 = r33;
        r17 = r0.indexOf(r1);	 Catch:{ IOException -> 0x0049 }
        if (r17 < 0) goto L_0x0306;
    L_0x0220:
        r33 = "lon=";
        r0 = r30;
        r1 = r33;
        r18 = r0.indexOf(r1);	 Catch:{ IOException -> 0x0049 }
        if (r18 < 0) goto L_0x0323;
    L_0x022c:
        r33 = -1;
        r0 = r17;
        r1 = r33;
        if (r0 <= r1) goto L_0x02f0;
    L_0x0234:
        r33 = -1;
        r0 = r18;
        r1 = r33;
        if (r0 <= r1) goto L_0x02f0;
    L_0x023c:
        r17 = r17 + 5;
        r18 = r18 + 5;
        r33 = r17 + -1;
        r0 = r30;
        r1 = r33;
        r2 = r17;
        r29 = r0.substring(r1, r2);	 Catch:{ IOException -> 0x0049 }
        r0 = r30;
        r1 = r29;
        r2 = r17;
        r14 = r0.indexOf(r1, r2);	 Catch:{ IOException -> 0x0049 }
        r0 = r30;
        r1 = r29;
        r2 = r18;
        r15 = r0.indexOf(r1, r2);	 Catch:{ IOException -> 0x0049 }
        r28 = new com.meixi.Routepoint;	 Catch:{ IOException -> 0x0049 }
        r28.<init>();	 Catch:{ IOException -> 0x0049 }
        r0 = r26;
        r1 = r28;
        r1.m_ParentRoute = r0;	 Catch:{ IOException -> 0x0049 }
        r33 = -1;
        r0 = r33;
        if (r14 <= r0) goto L_0x0289;
    L_0x0271:
        r0 = r30;
        r1 = r17;
        r33 = r0.subSequence(r1, r14);	 Catch:{ NumberFormatException -> 0x0340 }
        r33 = (java.lang.String) r33;	 Catch:{ NumberFormatException -> 0x0340 }
        r33 = java.lang.Double.valueOf(r33);	 Catch:{ NumberFormatException -> 0x0340 }
        r33 = r33.doubleValue();	 Catch:{ NumberFormatException -> 0x0340 }
        r0 = r33;
        r2 = r28;
        r2.m_dGpsLat = r0;	 Catch:{ NumberFormatException -> 0x0340 }
    L_0x0289:
        r33 = -1;
        r0 = r33;
        if (r15 <= r0) goto L_0x02a7;
    L_0x028f:
        r0 = r30;
        r1 = r18;
        r33 = r0.subSequence(r1, r15);	 Catch:{ NumberFormatException -> 0x034b }
        r33 = (java.lang.String) r33;	 Catch:{ NumberFormatException -> 0x034b }
        r33 = java.lang.Double.valueOf(r33);	 Catch:{ NumberFormatException -> 0x034b }
        r33 = r33.doubleValue();	 Catch:{ NumberFormatException -> 0x034b }
        r0 = r33;
        r2 = r28;
        r2.m_dGpsLong = r0;	 Catch:{ NumberFormatException -> 0x034b }
    L_0x02a7:
        r0 = r28;
        r0 = r0.m_dGpsLong;	 Catch:{ IOException -> 0x0049 }
        r33 = r0;
        r33 = (r33 > r7 ? 1 : (r33 == r7 ? 0 : -1));
        if (r33 <= 0) goto L_0x02b5;
    L_0x02b1:
        r0 = r28;
        r7 = r0.m_dGpsLong;	 Catch:{ IOException -> 0x0049 }
    L_0x02b5:
        r0 = r28;
        r0 = r0.m_dGpsLong;	 Catch:{ IOException -> 0x0049 }
        r33 = r0;
        r33 = (r33 > r11 ? 1 : (r33 == r11 ? 0 : -1));
        if (r33 >= 0) goto L_0x02c3;
    L_0x02bf:
        r0 = r28;
        r11 = r0.m_dGpsLong;	 Catch:{ IOException -> 0x0049 }
    L_0x02c3:
        r0 = r28;
        r0 = r0.m_dGpsLat;	 Catch:{ IOException -> 0x0049 }
        r33 = r0;
        r33 = (r33 > r5 ? 1 : (r33 == r5 ? 0 : -1));
        if (r33 <= 0) goto L_0x02d1;
    L_0x02cd:
        r0 = r28;
        r5 = r0.m_dGpsLat;	 Catch:{ IOException -> 0x0049 }
    L_0x02d1:
        r0 = r28;
        r0 = r0.m_dGpsLat;	 Catch:{ IOException -> 0x0049 }
        r33 = r0;
        r33 = (r33 > r9 ? 1 : (r33 == r9 ? 0 : -1));
        if (r33 >= 0) goto L_0x02df;
    L_0x02db:
        r0 = r28;
        r9 = r0.m_dGpsLat;	 Catch:{ IOException -> 0x0049 }
    L_0x02df:
        if (r26 == 0) goto L_0x02f0;
    L_0x02e1:
        r16 = r16 + 1;
        r0 = r26;
        r0 = r0.routepoints;	 Catch:{ IOException -> 0x0049 }
        r33 = r0;
        r0 = r33;
        r1 = r28;
        r0.add(r1);	 Catch:{ IOException -> 0x0049 }
    L_0x02f0:
        r27 = r26;
        goto L_0x00fd;
    L_0x02f4:
        r13 = move-exception;
        r24 = com.meixi.Track.TRACK_COLOR_DEFAULT;	 Catch:{ IOException -> 0x0049 }
        goto L_0x016a;
    L_0x02f9:
        r13 = move-exception;
        r23 = 1082130432; // 0x40800000 float:4.0 double:5.34643471E-315;
        goto L_0x019b;
    L_0x02fe:
        r21 = 0;
        goto L_0x01cb;
    L_0x0302:
        r22 = 0;
        goto L_0x01fb;
    L_0x0306:
        r31 = r19.readLine();	 Catch:{ IOException -> 0x0049 }
        if (r31 == 0) goto L_0x0220;
    L_0x030c:
        r33 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x0049 }
        r34 = java.lang.String.valueOf(r30);	 Catch:{ IOException -> 0x0049 }
        r33.<init>(r34);	 Catch:{ IOException -> 0x0049 }
        r0 = r33;
        r1 = r31;
        r33 = r0.append(r1);	 Catch:{ IOException -> 0x0049 }
        r30 = r33.toString();	 Catch:{ IOException -> 0x0049 }
        goto L_0x0214;
    L_0x0323:
        r31 = r19.readLine();	 Catch:{ IOException -> 0x0049 }
        if (r31 == 0) goto L_0x022c;
    L_0x0329:
        r33 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x0049 }
        r34 = java.lang.String.valueOf(r30);	 Catch:{ IOException -> 0x0049 }
        r33.<init>(r34);	 Catch:{ IOException -> 0x0049 }
        r0 = r33;
        r1 = r31;
        r33 = r0.append(r1);	 Catch:{ IOException -> 0x0049 }
        r30 = r33.toString();	 Catch:{ IOException -> 0x0049 }
        goto L_0x0220;
    L_0x0340:
        r13 = move-exception;
        r33 = 0;
        r0 = r33;
        r2 = r28;
        r2.m_dGpsLat = r0;	 Catch:{ IOException -> 0x0049 }
        goto L_0x0289;
    L_0x034b:
        r13 = move-exception;
        r33 = 0;
        r0 = r33;
        r2 = r28;
        r2.m_dGpsLong = r0;	 Catch:{ IOException -> 0x0049 }
        goto L_0x02a7;
    L_0x0356:
        r33 = LOAD_RESULT_NOPOINTS;	 Catch:{ IOException -> 0x035c }
        r26 = r27;
        goto L_0x004c;
    L_0x035c:
        r13 = move-exception;
        r26 = r27;
        goto L_0x004a;
    L_0x0361:
        r26 = r27;
        goto L_0x0060;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.meixi.Tools.ReadGPXtoRoutes(java.lang.String, java.lang.String, java.util.ArrayList):int");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int ReadGPXtoWaypoints(String r28, String r29, ArrayList<com.meixi.Waypoint> r30) {
        /*
        r22 = "";
        r4 = 0;
        r3 = 0;
        r24 = 0;
        r12 = 0;
        r18 = "";
        r19 = "";
        r17 = "";
        r15 = 1;
        r14 = 0;
        r13 = 1;
        r16 = com.meixi.Waypoint.WAYPOINT_COLOR_DEFAULT;
        r8 = 0;
        r11 = new java.io.BufferedReader;	 Catch:{ IOException -> 0x002f }
        r26 = new java.io.FileReader;	 Catch:{ IOException -> 0x002f }
        r0 = r26;
        r1 = r28;
        r0.<init>(r1);	 Catch:{ IOException -> 0x002f }
        r0 = r26;
        r11.<init>(r0);	 Catch:{ IOException -> 0x002f }
        r26 = r11.ready();	 Catch:{ IOException -> 0x002f }
        if (r26 != 0) goto L_0x02d2;
    L_0x0029:
        r26 = new java.io.IOException;	 Catch:{ IOException -> 0x002f }
        r26.<init>();	 Catch:{ IOException -> 0x002f }
        throw r26;	 Catch:{ IOException -> 0x002f }
    L_0x002f:
        r5 = move-exception;
    L_0x0030:
        r26 = LOAD_RESULT_ERROR;
    L_0x0032:
        return r26;
    L_0x0033:
        r26 = "<wpt";
        r0 = r23;
        r1 = r26;
        r26 = r0.contains(r1);	 Catch:{ IOException -> 0x02cd }
        if (r26 == 0) goto L_0x02d6;
    L_0x003f:
        r24 = new com.meixi.Waypoint;	 Catch:{ IOException -> 0x02cd }
        r24.<init>();	 Catch:{ IOException -> 0x02cd }
    L_0x0044:
        if (r4 != 0) goto L_0x013a;
    L_0x0046:
        r26 = "<metadata>";
        r0 = r23;
        r1 = r26;
        r26 = r0.indexOf(r1);	 Catch:{ IOException -> 0x002f }
        r27 = -1;
        r0 = r26;
        r1 = r27;
        if (r0 <= r1) goto L_0x0059;
    L_0x0058:
        r3 = 1;
    L_0x0059:
        r26 = "</metadata>";
        r0 = r23;
        r1 = r26;
        r26 = r0.indexOf(r1);	 Catch:{ IOException -> 0x002f }
        r27 = -1;
        r0 = r26;
        r1 = r27;
        if (r0 <= r1) goto L_0x006c;
    L_0x006b:
        r3 = 0;
    L_0x006c:
        if (r3 == 0) goto L_0x013a;
    L_0x006e:
        r26 = "<mmtcolor>";
        r0 = r23;
        r1 = r26;
        r9 = r0.indexOf(r1);	 Catch:{ IOException -> 0x002f }
        r26 = -1;
        r0 = r26;
        if (r9 <= r0) goto L_0x00a4;
    L_0x007e:
        r12 = 1;
        r9 = r9 + 10;
        r26 = "</mmtcolor>";
        r0 = r23;
        r1 = r26;
        r6 = r0.indexOf(r1);	 Catch:{ IOException -> 0x002f }
        r26 = -1;
        r0 = r26;
        if (r6 <= r0) goto L_0x00a4;
    L_0x0091:
        r0 = r23;
        r26 = r0.subSequence(r9, r6);	 Catch:{ NumberFormatException -> 0x0261 }
        r26 = (java.lang.String) r26;	 Catch:{ NumberFormatException -> 0x0261 }
        r27 = 16;
        r26 = java.lang.Long.parseLong(r26, r27);	 Catch:{ NumberFormatException -> 0x0261 }
        r0 = r26;
        r0 = (int) r0;
        r16 = r0;
    L_0x00a4:
        r26 = "<mmtvisible>";
        r0 = r23;
        r1 = r26;
        r9 = r0.indexOf(r1);	 Catch:{ IOException -> 0x002f }
        r26 = -1;
        r0 = r26;
        if (r9 <= r0) goto L_0x00d6;
    L_0x00b4:
        r12 = 1;
        r9 = r9 + 12;
        r26 = "</mmtvisible>";
        r0 = r23;
        r1 = r26;
        r6 = r0.indexOf(r1);	 Catch:{ IOException -> 0x002f }
        r26 = -1;
        r0 = r26;
        if (r6 <= r0) goto L_0x00d6;
    L_0x00c7:
        r0 = r23;
        r26 = r0.subSequence(r9, r6);	 Catch:{ IOException -> 0x002f }
        r27 = "true";
        r26 = r26.equals(r27);	 Catch:{ IOException -> 0x002f }
        if (r26 == 0) goto L_0x0266;
    L_0x00d5:
        r15 = 1;
    L_0x00d6:
        r26 = "<mmtlocked>";
        r0 = r23;
        r1 = r26;
        r9 = r0.indexOf(r1);	 Catch:{ IOException -> 0x002f }
        r26 = -1;
        r0 = r26;
        if (r9 <= r0) goto L_0x0108;
    L_0x00e6:
        r12 = 1;
        r9 = r9 + 11;
        r26 = "</mmtlocked>";
        r0 = r23;
        r1 = r26;
        r6 = r0.indexOf(r1);	 Catch:{ IOException -> 0x002f }
        r26 = -1;
        r0 = r26;
        if (r6 <= r0) goto L_0x0108;
    L_0x00f9:
        r0 = r23;
        r26 = r0.subSequence(r9, r6);	 Catch:{ IOException -> 0x002f }
        r27 = "true";
        r26 = r26.equals(r27);	 Catch:{ IOException -> 0x002f }
        if (r26 == 0) goto L_0x0269;
    L_0x0107:
        r13 = 1;
    L_0x0108:
        r26 = "<mmtshowlabel>";
        r0 = r23;
        r1 = r26;
        r9 = r0.indexOf(r1);	 Catch:{ IOException -> 0x002f }
        r26 = -1;
        r0 = r26;
        if (r9 <= r0) goto L_0x013a;
    L_0x0118:
        r12 = 1;
        r9 = r9 + 14;
        r26 = "</mmtshowlabel>";
        r0 = r23;
        r1 = r26;
        r6 = r0.indexOf(r1);	 Catch:{ IOException -> 0x002f }
        r26 = -1;
        r0 = r26;
        if (r6 <= r0) goto L_0x013a;
    L_0x012b:
        r0 = r23;
        r26 = r0.subSequence(r9, r6);	 Catch:{ IOException -> 0x002f }
        r27 = "true";
        r26 = r26.equals(r27);	 Catch:{ IOException -> 0x002f }
        if (r26 == 0) goto L_0x026c;
    L_0x0139:
        r14 = 1;
    L_0x013a:
        r26 = "<name>";
        r0 = r23;
        r1 = r26;
        r26 = r0.contains(r1);	 Catch:{ IOException -> 0x002f }
        if (r26 == 0) goto L_0x014a;
    L_0x0146:
        r18 = ExtractTrackName(r23);	 Catch:{ IOException -> 0x002f }
    L_0x014a:
        r26 = "<sym>";
        r0 = r23;
        r1 = r26;
        r26 = r0.contains(r1);	 Catch:{ IOException -> 0x002f }
        if (r26 == 0) goto L_0x015a;
    L_0x0156:
        r19 = ExtractTrackName(r23);	 Catch:{ IOException -> 0x002f }
    L_0x015a:
        r26 = "<desc>";
        r0 = r23;
        r1 = r26;
        r26 = r0.contains(r1);	 Catch:{ IOException -> 0x002f }
        if (r26 == 0) goto L_0x016a;
    L_0x0166:
        r17 = ExtractTrackName(r23);	 Catch:{ IOException -> 0x002f }
    L_0x016a:
        r26 = "<wpt ";
        r0 = r23;
        r1 = r26;
        r26 = r0.contains(r1);	 Catch:{ IOException -> 0x002f }
        if (r26 == 0) goto L_0x01f5;
    L_0x0176:
        r4 = 1;
        r22 = r23;
    L_0x0179:
        r26 = "lat=";
        r0 = r22;
        r1 = r26;
        r9 = r0.indexOf(r1);	 Catch:{ IOException -> 0x002f }
        if (r9 < 0) goto L_0x026f;
    L_0x0185:
        r26 = "lon=";
        r0 = r22;
        r1 = r26;
        r10 = r0.indexOf(r1);	 Catch:{ IOException -> 0x002f }
        if (r10 < 0) goto L_0x028c;
    L_0x0191:
        r26 = -1;
        r0 = r26;
        if (r9 <= r0) goto L_0x01f5;
    L_0x0197:
        r26 = -1;
        r0 = r26;
        if (r10 <= r0) goto L_0x01f5;
    L_0x019d:
        r9 = r9 + 5;
        r10 = r10 + 5;
        r26 = r9 + -1;
        r0 = r22;
        r1 = r26;
        r20 = r0.substring(r1, r9);	 Catch:{ IOException -> 0x002f }
        r0 = r22;
        r1 = r20;
        r6 = r0.indexOf(r1, r9);	 Catch:{ IOException -> 0x002f }
        r0 = r22;
        r1 = r20;
        r7 = r0.indexOf(r1, r10);	 Catch:{ IOException -> 0x002f }
        r26 = -1;
        r0 = r26;
        if (r6 <= r0) goto L_0x01d7;
    L_0x01c1:
        r0 = r22;
        r26 = r0.subSequence(r9, r6);	 Catch:{ NumberFormatException -> 0x02a9 }
        r26 = (java.lang.String) r26;	 Catch:{ NumberFormatException -> 0x02a9 }
        r26 = java.lang.Double.valueOf(r26);	 Catch:{ NumberFormatException -> 0x02a9 }
        r26 = r26.doubleValue();	 Catch:{ NumberFormatException -> 0x02a9 }
        r0 = r26;
        r2 = r24;
        r2.m_dGpsLat = r0;	 Catch:{ NumberFormatException -> 0x02a9 }
    L_0x01d7:
        r26 = -1;
        r0 = r26;
        if (r7 <= r0) goto L_0x01f3;
    L_0x01dd:
        r0 = r22;
        r26 = r0.subSequence(r10, r7);	 Catch:{ NumberFormatException -> 0x02b4 }
        r26 = (java.lang.String) r26;	 Catch:{ NumberFormatException -> 0x02b4 }
        r26 = java.lang.Double.valueOf(r26);	 Catch:{ NumberFormatException -> 0x02b4 }
        r26 = r26.doubleValue();	 Catch:{ NumberFormatException -> 0x02b4 }
        r0 = r26;
        r2 = r24;
        r2.m_dGpsLong = r0;	 Catch:{ NumberFormatException -> 0x02b4 }
    L_0x01f3:
        r8 = r8 + 1;
    L_0x01f5:
        r26 = "</wpt>";
        r0 = r23;
        r1 = r26;
        r26 = r0.contains(r1);	 Catch:{ IOException -> 0x002f }
        if (r26 == 0) goto L_0x02d2;
    L_0x0201:
        if (r24 == 0) goto L_0x02d2;
    L_0x0203:
        r26 = "";
        r0 = r18;
        r1 = r26;
        if (r0 != r1) goto L_0x02bf;
    L_0x020b:
        r0 = r29;
        r1 = r24;
        r1.m_sName = r0;	 Catch:{ IOException -> 0x002f }
    L_0x0211:
        r0 = r24;
        r0.m_CreatedByMMTracker = r12;	 Catch:{ IOException -> 0x002f }
        r0 = r16;
        r1 = r24;
        r1.m_iColor = r0;	 Catch:{ IOException -> 0x002f }
        r0 = r24;
        r0.m_bVisible = r15;	 Catch:{ IOException -> 0x002f }
        r0 = r28;
        r1 = r24;
        r1.m_sFileName = r0;	 Catch:{ IOException -> 0x002f }
        r0 = r24;
        r0.m_bShowLabel = r14;	 Catch:{ IOException -> 0x002f }
        r0 = r24;
        r0.m_bLocked = r13;	 Catch:{ IOException -> 0x002f }
        r0 = r17;
        r1 = r24;
        r1.m_sDesc = r0;	 Catch:{ IOException -> 0x002f }
        r0 = r19;
        r1 = r24;
        r1.m_sSymbol = r0;	 Catch:{ IOException -> 0x002f }
        r26 = r24.CalcSymbolNumber();	 Catch:{ IOException -> 0x002f }
        r0 = r26;
        r1 = r24;
        r1.m_iSymbol = r0;	 Catch:{ IOException -> 0x002f }
        r0 = r30;
        r1 = r24;
        r0.add(r1);	 Catch:{ IOException -> 0x002f }
        r4 = 0;
        r3 = 0;
        r24 = 0;
        r25 = r24;
    L_0x0250:
        r23 = r11.readLine();	 Catch:{ IOException -> 0x02cd }
        if (r23 != 0) goto L_0x0033;
    L_0x0256:
        r11.close();	 Catch:{ IOException -> 0x02cd }
        if (r8 <= 0) goto L_0x02c7;
    L_0x025b:
        r26 = LOAD_RESULT_OK;	 Catch:{ IOException -> 0x02cd }
        r24 = r25;
        goto L_0x0032;
    L_0x0261:
        r5 = move-exception;
        r16 = com.meixi.Track.TRACK_COLOR_DEFAULT;	 Catch:{ IOException -> 0x002f }
        goto L_0x00a4;
    L_0x0266:
        r15 = 0;
        goto L_0x00d6;
    L_0x0269:
        r13 = 0;
        goto L_0x0108;
    L_0x026c:
        r14 = 0;
        goto L_0x013a;
    L_0x026f:
        r21 = r11.readLine();	 Catch:{ IOException -> 0x002f }
        if (r21 == 0) goto L_0x0185;
    L_0x0275:
        r26 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x002f }
        r27 = java.lang.String.valueOf(r22);	 Catch:{ IOException -> 0x002f }
        r26.<init>(r27);	 Catch:{ IOException -> 0x002f }
        r0 = r26;
        r1 = r21;
        r26 = r0.append(r1);	 Catch:{ IOException -> 0x002f }
        r22 = r26.toString();	 Catch:{ IOException -> 0x002f }
        goto L_0x0179;
    L_0x028c:
        r21 = r11.readLine();	 Catch:{ IOException -> 0x002f }
        if (r21 == 0) goto L_0x0191;
    L_0x0292:
        r26 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x002f }
        r27 = java.lang.String.valueOf(r22);	 Catch:{ IOException -> 0x002f }
        r26.<init>(r27);	 Catch:{ IOException -> 0x002f }
        r0 = r26;
        r1 = r21;
        r26 = r0.append(r1);	 Catch:{ IOException -> 0x002f }
        r22 = r26.toString();	 Catch:{ IOException -> 0x002f }
        goto L_0x0185;
    L_0x02a9:
        r5 = move-exception;
        r26 = 0;
        r0 = r26;
        r2 = r24;
        r2.m_dGpsLat = r0;	 Catch:{ IOException -> 0x002f }
        goto L_0x01d7;
    L_0x02b4:
        r5 = move-exception;
        r26 = 0;
        r0 = r26;
        r2 = r24;
        r2.m_dGpsLong = r0;	 Catch:{ IOException -> 0x002f }
        goto L_0x01f3;
    L_0x02bf:
        r0 = r18;
        r1 = r24;
        r1.m_sName = r0;	 Catch:{ IOException -> 0x002f }
        goto L_0x0211;
    L_0x02c7:
        r26 = LOAD_RESULT_NOPOINTS;	 Catch:{ IOException -> 0x02cd }
        r24 = r25;
        goto L_0x0032;
    L_0x02cd:
        r5 = move-exception;
        r24 = r25;
        goto L_0x0030;
    L_0x02d2:
        r25 = r24;
        goto L_0x0250;
    L_0x02d6:
        r24 = r25;
        goto L_0x0044;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.meixi.Tools.ReadGPXtoWaypoints(java.lang.String, java.lang.String, java.util.ArrayList):int");
    }

    public static File[] listFilesAsArray(File directory, FilenameFilter filter, boolean recurse) {
        File[] arr = null;
        Collection<File> files = listFiles(directory, filter, recurse);
        if (files != null) {
            return (File[]) files.toArray(new File[files.size()]);
        }
        return null;
    }

    public static Collection<File> listFiles(File directory, FilenameFilter filter, boolean recurse) {
        if (directory == null || filter == null) {
            return null;
        }
        Collection<File> files = new Vector();
        File[] entries = directory.listFiles();
        if (entries == null) {
            return null;
        }
        for (File entry : entries) {
            if (filter == null || filter.accept(directory, entry.getName())) {
                files.add(entry);
            }
            if (recurse && entry.isDirectory()) {
                Collection<File> temp = listFiles(entry, filter, recurse);
                if (temp != null) {
                    files.addAll(temp);
                }
            }
        }
        return files;
    }

    public static String MakeProperFileName(String s) {
        if (s == null) {
            return "_";
        }
        if (s.length() == 0) {
            return "_";
        }
        return s.replace("\\", "_").replace("/", "_").replace("?", "_").replace("*", "_").replace("\"", "_").replace(";", "_").replace("|", "_").replace("@", "_");
    }

    private static Location ParseCoordinates(String sCoord, String sSep, String sDec) {
        Location result = new Location("dummy");
        sCoord = sCoord.trim();
        int iMiddle = sCoord.indexOf(sSep);
        if (iMiddle < 0) {
            return null;
        }
        if (iMiddle != sCoord.lastIndexOf(sSep)) {
            return null;
        }
        String sLeft = sCoord.substring(0, iMiddle);
        String sRight = sCoord.substring(iMiddle + 1, sCoord.length());
        sLeft = sLeft.replace(sDec, ".");
        sRight = sRight.replace(sDec, ".");
        try {
            double dLat = Double.parseDouble(sLeft);
            try {
                double dLon = Double.parseDouble(sRight);
                result.setLatitude(dLat);
                result.setLongitude(dLon);
                if (dLon > 180.0d) {
                    return null;
                }
                if (dLon < -180.0d) {
                    return null;
                }
                if (dLat > 90.0d) {
                    return null;
                }
                if (dLat < -90.0d) {
                    return null;
                }
                return result;
            } catch (NumberFormatException e) {
                return null;
            }
        } catch (NumberFormatException e2) {
            return null;
        }
    }

    public static int OpacityGranulation(int iInput) {
        if (iInput >= 229) {
            return 255;
        }
        if (iInput >= 178) {
            return 204;
        }
        if (iInput >= 127) {
            return 153;
        }
        if (iInput >= 76) {
            return 102;
        }
        if (iInput >= 38) {
            return 51;
        }
        return 26;
    }

    public static double CalcBearing(double dLatStart, double dLongStart, double dLatEnd, double dLongEnd) {
        dLatStart = Math.toRadians(dLatStart);
        dLongStart = Math.toRadians(dLongStart);
        dLatEnd = Math.toRadians(dLatEnd);
        dLongEnd = Math.toRadians(dLongEnd);
        return Math.atan2(Math.sin(dLongEnd - dLongStart) * Math.cos(dLatEnd), (Math.cos(dLatStart) * Math.sin(dLatEnd)) - ((Math.sin(dLatStart) * Math.cos(dLatEnd)) * Math.cos(dLongEnd - dLongStart)));
    }

    public static Location ConvertTextToLatLon(String sCoord) {
        sCoord = sCoord.trim();
        Location result = ParseCoordinates(sCoord, ",", ".");
        if (result != null) {
            return result;
        }
        result = ParseCoordinates(sCoord, ";", ".");
        if (result != null) {
            return result;
        }
        result = ParseCoordinates(sCoord, ";", ",");
        if (result != null) {
            return result;
        }
        result = ParseCoordinates(sCoord, " ", ".");
        if (result != null) {
            return result;
        }
        result = ParseCoordinates(sCoord, " ", ",");
        if (result != null) {
            return result;
        }
        return null;
    }

    public static StringCoord CoordToString(double dLat, double dLong, int iType, boolean bValid) {
        StringCoord result = new StringCoord();
        result.sLat = "";
        result.sLong = "";
        if (bValid) {
            String str;
            Object[] objArr;
            double dabsLat;
            double dabsLong;
            switch (iType) {
                case 0:
                    result.sLat = String.format("%.5f", new Object[]{Double.valueOf(dLat)});
                    result.sLong = String.format("%.5f", new Object[]{Double.valueOf(dLong)});
                    break;
                case 1:
                    str = "%.5f\u00b0%s";
                    objArr = new Object[2];
                    objArr[0] = Double.valueOf(Math.abs(dLat));
                    objArr[1] = dLat > 0.0d ? "N" : "S";
                    result.sLat = String.format(str, objArr);
                    str = "%.5f\u00b0%s";
                    objArr = new Object[2];
                    objArr[0] = Double.valueOf(Math.abs(dLong));
                    objArr[1] = dLong > 0.0d ? "E" : "W";
                    result.sLong = String.format(str, objArr);
                    break;
                case 2:
                    dabsLat = Math.abs(dLat);
                    dabsLong = Math.abs(dLong);
                    str = "%d\u00b0%.3f'%s";
                    objArr = new Object[3];
                    objArr[0] = Long.valueOf(Math.round(Math.floor(dabsLat)));
                    objArr[1] = Double.valueOf((dabsLat - Math.floor(dabsLat)) * 60.0d);
                    objArr[2] = dLat > 0.0d ? "N" : "S";
                    result.sLat = String.format(str, objArr);
                    str = "%d\u00b0%.3f'%s";
                    objArr = new Object[3];
                    objArr[0] = Long.valueOf(Math.round(Math.floor(dabsLong)));
                    objArr[1] = Double.valueOf((dabsLong - Math.floor(dabsLong)) * 60.0d);
                    objArr[2] = dLong > 0.0d ? "E" : "W";
                    result.sLong = String.format(str, objArr);
                    break;
                case 3:
                    dabsLat = Math.abs(dLat);
                    dabsLong = Math.abs(dLong);
                    double dLatMinute = (dabsLat - Math.floor(dabsLat)) * 60.0d;
                    double dLongMinute = (dabsLong - Math.floor(dabsLong)) * 60.0d;
                    str = "%d\u00b0%d'%.1f''%s";
                    objArr = new Object[4];
                    objArr[0] = Long.valueOf(Math.round(Math.floor(dabsLat)));
                    objArr[1] = Integer.valueOf((int) Math.floor(dLatMinute));
                    objArr[2] = Double.valueOf((dLatMinute - Math.floor(dLatMinute)) * 60.0d);
                    objArr[3] = dLat > 0.0d ? "N" : "S";
                    result.sLat = String.format(str, objArr);
                    str = "%d\u00b0%d'%.1f''%s";
                    objArr = new Object[4];
                    objArr[0] = Long.valueOf(Math.round(Math.floor(dabsLong)));
                    objArr[1] = Integer.valueOf((int) Math.floor(dLongMinute));
                    objArr[2] = Double.valueOf((dLongMinute - Math.floor(dLongMinute)) * 60.0d);
                    objArr[3] = dLong > 0.0d ? "E" : "W";
                    result.sLong = String.format(str, objArr);
                    break;
            }
        }
        String sEmpty = "";
        switch (iType) {
            case 0:
                sEmpty = "- - . - - - - -";
                break;
            case 1:
                sEmpty = "- - . - - - - -";
                break;
            case 2:
                sEmpty = "- -\u00b0- - . - -''-";
                break;
            case 3:
                sEmpty = "- -\u00b0- -'- . - -''-";
                break;
        }
        result.sLat = sEmpty;
        result.sLong = sEmpty;
        result.sLat = result.sLat.replace(",", ".");
        result.sLong = result.sLong.replace(",", ".");
        return result;
    }

    public static boolean PostIoResult(Exception e, Context context) {
        if (e == null) {
            return true;
        }
        if (System.currentTimeMillis() - m_lErrorPostTime <= 3000) {
            return false;
        }
        m_lErrorPostTime = System.currentTimeMillis();
        Toast.makeText(context, context.getString(C0047R.string.Tools_IoResult_Negative) + " '" + e.getLocalizedMessage() + "'", 0).show();
        return false;
    }

    public static int GetArrayIndexPosition(String[] sa, String ref_string) {
        for (int index = 0; index < sa.length; index++) {
            if (sa[index].equals(ref_string)) {
                return index;
            }
        }
        return 0;
    }
}
