package com.meixi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class MapCacheDbAdapter {
    private static final String DATABASE_TABLE = "mapcache";
    public static final String KEY_BOTTOM = "bottom";
    public static final String KEY_DATE = "date";
    public static final String KEY_LEFT = "left";
    public static final String KEY_NAME = "name";
    public static final String KEY_RIGHT = "right";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_SIZE = "size";
    public static final String KEY_TOP = "top";
    private Context context;
    private SQLiteDatabase database;
    private MapCacheHelper dbHelper;

    public MapCacheDbAdapter(Context context) {
        this.context = context;
    }

    public MapCacheDbAdapter open() throws SQLException {
        this.dbHelper = new MapCacheHelper(this.context);
        try {
            this.database = this.dbHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            this.database = null;
        }
        return this;
    }

    public void close() {
        this.dbHelper.close();
    }

    public boolean isOpen() {
        if (this.database == null) {
            return false;
        }
        return this.database.isOpen();
    }

    public long createCacheEntry(String name, long size, long date, double top, double left, double bottom, double right) {
        return this.database.insert(DATABASE_TABLE, null, createContentValues(name, size, date, top, left, bottom, right));
    }

    public boolean updateCacheEntry(long rowId, String name, long size, long date, double top, double left, double bottom, double right) {
        return this.database.update(DATABASE_TABLE, createContentValues(name, size, date, top, left, bottom, right), new StringBuilder("_id=").append(rowId).toString(), null) > 0;
    }

    public boolean deleteCacheEntry(long rowId) {
        return this.database.delete(DATABASE_TABLE, new StringBuilder("_id=").append(rowId).toString(), null) > 0;
    }

    public void clearAll() {
        this.database.execSQL("DROP TABLE IF EXISTS mapcache");
    }

    public Cursor fetchAllMapCaches() {
        Cursor cursor = null;
        try {
            cursor = this.database.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_NAME, KEY_SIZE, KEY_DATE, KEY_TOP, KEY_LEFT, KEY_BOTTOM, KEY_RIGHT}, null, null, null, null, null);
        } catch (Exception e) {
        }
        return cursor;
    }

    public long getRowID(Cursor cr) {
        if (cr == null || cr.isClosed()) {
            return -1;
        }
        int col = cr.getColumnIndex(KEY_ROWID);
        if (col >= 0) {
            return cr.getLong(col);
        }
        return -1;
    }

    public Cursor fetchCacheEntryAtId(long rowId) throws SQLException {
        Cursor mCursor;
        try {
            mCursor = this.database.query(true, DATABASE_TABLE, new String[]{KEY_ROWID, KEY_NAME, KEY_SIZE, KEY_DATE, KEY_TOP, KEY_LEFT, KEY_BOTTOM, KEY_RIGHT}, "_id=" + rowId, null, null, null, null, null);
        } catch (Exception e) {
            mCursor = null;
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor fetchCacheEntryByData(String name, long size, long date) throws SQLException {
        Cursor mCursor;
        try {
            mCursor = this.database.query(true, DATABASE_TABLE, new String[]{KEY_ROWID, KEY_NAME, KEY_SIZE, KEY_DATE, KEY_TOP, KEY_LEFT, KEY_BOTTOM, KEY_RIGHT}, "name=\"" + name + "\" AND " + KEY_SIZE + "=" + size + " AND " + KEY_DATE + "=" + date, null, null, null, null, null);
        } catch (Exception e) {
            mCursor = null;
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    private ContentValues createContentValues(String name, long size, long date, double top, double left, double bottom, double right) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_SIZE, Long.valueOf(size));
        values.put(KEY_DATE, Long.valueOf(date));
        values.put(KEY_TOP, Double.valueOf(top));
        values.put(KEY_LEFT, Double.valueOf(left));
        values.put(KEY_BOTTOM, Double.valueOf(bottom));
        values.put(KEY_RIGHT, Double.valueOf(right));
        return values;
    }

    public boolean CompareMapData(Cursor cr, String name, long size, long date) {
        if (cr == null || cr.getCount() == 0 || cr.isClosed()) {
            return false;
        }
        int col = cr.getColumnIndex(KEY_NAME);
        if (col < 0) {
            return false;
        }
        String db_name = cr.getString(col);
        col = cr.getColumnIndex(KEY_SIZE);
        if (col < 0) {
            return false;
        }
        long db_size = cr.getLong(col);
        col = cr.getColumnIndex(KEY_DATE);
        if (col < 0) {
            return false;
        }
        long db_date = cr.getLong(col);
        if (name.equals(db_name) && size == db_size && date == db_date) {
            return true;
        }
        return false;
    }

    public boolean TestPointInside(Cursor cr, double dLat, double dLong) {
        if (cr == null) {
            return false;
        }
        if (cr.getCount() == 0) {
            return false;
        }
        if (cr.isClosed()) {
            return false;
        }
        int col = cr.getColumnIndex(KEY_TOP);
        if (col < 0) {
            return false;
        }
        double top = cr.getDouble(col);
        col = cr.getColumnIndex(KEY_LEFT);
        if (col < 0) {
            return false;
        }
        double left = cr.getDouble(col);
        col = cr.getColumnIndex(KEY_BOTTOM);
        if (col < 0) {
            return false;
        }
        double bottom = cr.getDouble(col);
        col = cr.getColumnIndex(KEY_RIGHT);
        if (col < 0) {
            return false;
        }
        double right = cr.getDouble(col);
        if (dLat > top || dLat < bottom || dLong < left || dLong > right) {
            return false;
        }
        return true;
    }
}
