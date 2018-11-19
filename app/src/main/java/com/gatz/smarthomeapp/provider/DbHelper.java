package com.gatz.smarthomeapp.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gatz.smarthomeapp.utils.DbUttil;

/**
 * Created by zhouh on 2017/3/6.
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_ID = "_id";
    private static final String DB_NAME = "smart_db";
    private static final int DB_VERSION = 1;
    private static DbHelper dbHelper;

    public static synchronized DbHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new DbHelper(context);
        }
        return dbHelper;
    }

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DbUttil.USER_TABLE
                + "(" + DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DbUttil.I_PHONE + " TEXT,"
                + DbUttil.I_PWD + " TEXT,"
                + DbUttil.I_SESSOION_ID + " TEXT,"
                + DbUttil.I_ROOM_ID + " TEXT,"
                + DbUttil.I_UNIT_ID + " TEXT,"
                + DbUttil.I_COMMUNITY_ID + " TEXT,"
                + DbUttil.I_BEDROOM_ID + " TEXT,"
                + DbUttil.I_GATEWAY_STATUS + " INTEGER,"
                + DbUttil.I_CONNECT_IP + " TEXT,"
                + DbUttil.I_BUILDING_NAME + " TEXT)");

        db.execSQL("CREATE TABLE " + DbUttil.DEVICE_TABLE
                + "(" + DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DbUttil.DEVICE_NAME + " TEXT,"
                + DbUttil.DEVICE_ROOM + " TEXT,"
                + DbUttil.DEVICE_ROOM_ID + " TEXT,"
                + DbUttil.DEVICE_ID + " TEXT,"
                + DbUttil.DEVICE_KNX_ADDRESS1 + " TEXT,"
                + DbUttil.DEVICE_KNX_ADDRESS2 + " TEXT,"
                + DbUttil.DEVICE_PROTOCOLS + " TEXT,"
                + DbUttil.DEVICE_TYPE + " TEXT,"
                + DbUttil.DEVICE_KEY_WORDS + " TEXT,"
                + DbUttil.DEVICE_STATUS + " TEXT)");

        db.execSQL("CREATE TABLE " + DbUttil.SCENE_TABLE
                + "(" + DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DbUttil.SCENE_NAME + " TEXT,"
                + DbUttil.SCENE_LIST + " TEXT)");

        db.execSQL("CREATE TABLE " + DbUttil.AIR_STATES_TABLE
                + "(" + DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DbUttil.AIR_DEVICE_ID + " TEXT,"
                + DbUttil.AIR_PROTOCOL_ID + " TEXT,"
                + DbUttil.AIR_FUNCTION_NAME + " TEXT,"
                + DbUttil.AIR_PROTOCOL_ADDR + " TEXT,"
                + DbUttil.AIR_VALUETYPE + " TEXT,"
                + DbUttil.AIR_VALUE + " INTEGER,"
                + DbUttil.AIR_BEDROOM_ID + " TEXT,"
                + DbUttil.AIR_DPID + " TEXT,"
                + DbUttil.AIR_SPAN + " TEXT)");

        db.execSQL("create table  if not exists " + DbUttil.TABLE_NAME
                + "(" + DbUttil.LI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DbUttil.LI_TITLE + " text,"
                + DbUttil.LI_CONTENT + " text,"
                + DbUttil.LI_TYPE + " text,"
                + DbUttil.LI_TIME + " text,"
                + DbUttil.LI_URL + " text,"
                + DbUttil.LI_READ + " text)");

        db.execSQL("CREATE TABLE " + DbUttil.VERSION_TABLE
                + "(" + DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DbUttil.VERSION_APP_NAME + " TEXT,"
                + DbUttil.VERSION_NAME + " TEXT)");

        db.execSQL("CREATE TABLE " + DbUttil.PING_TIME_TABLE
                + "(" + DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DbUttil.PING_INTERVAL_TIME + " INTEGER,"
                + DbUttil.PING_OUT_TIME + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS  " + DbUttil.USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS  " + DbUttil.DEVICE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS  " + DbUttil.SCENE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS  " + DbUttil.AIR_STATES_TABLE);
        db.execSQL("drop table if exists " + DbUttil.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS  " + DbUttil.VERSION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS  " + DbUttil.PING_TIME_TABLE);
        onCreate(db);
    }
}
