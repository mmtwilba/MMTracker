package com.meixi;

public class NavigationTarget {
    public static int TARGET_TYPE_NONE;
    public static int TARGET_TYPE_ROUTEPOINT;
    public static int TARGET_TYPE_WAYPOINT;
    public Route m_ParentRoute;
    public Routepoint m_Routepoint;
    public int m_Type;
    public Waypoint m_Waypoint;
    public double m_dGpsLat;
    public double m_dGpsLong;

    public NavigationTarget() {
        this.m_dGpsLong = 0.0d;
        this.m_dGpsLat = 0.0d;
        this.m_Type = TARGET_TYPE_NONE;
        this.m_ParentRoute = null;
        this.m_Routepoint = null;
        this.m_Waypoint = null;
    }

    static {
        TARGET_TYPE_NONE = 0;
        TARGET_TYPE_WAYPOINT = 1;
        TARGET_TYPE_ROUTEPOINT = 2;
    }
}
