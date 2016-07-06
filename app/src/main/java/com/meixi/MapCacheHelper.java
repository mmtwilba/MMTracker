package com.meixi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MapCacheHelper extends SQLiteOpenHelper {
    private static final String DATABASE_CREATE = "create table mapcache (_id integer primary key autoincrement, name string, size long, date long, top real, left real, bottom real, right real);";
    private static final String DATABASE_NAME = "mapcachedb";
    private static final int DATABASE_VERSION = 1;
    public static final String KEY_BOTTOM = "bottom";
    public static final String KEY_DATE = "date";
    public static final String KEY_LEFT = "left";
    public static final String KEY_NAME = "name";
    public static final String KEY_RIGHT = "right";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_SIZE = "size";
    public static final String KEY_TOP = "top";

    public MapCacheHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS mapcache");
        onCreate(database);
    }
}
