package com.gatz.smarthomeapp.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.gatz.smarthomeapp.utils.DbUttil;
import com.gatz.smarthomeapp.utils.LinuxCmd;
import com.gatz.smarthomeapp.utils.Utils;

/**
 * Created by zhouh on 2017/3/3.
 */
public class SmartProvider extends ContentProvider {

    private static final String TAG = "SmartProvider-";

    private DbHelper dbHelper;
    private Context context;
    private SQLiteDatabase db;
    //<code>
    public static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int User_Code = 1;
    private static final int Device_Code = 2;
    private static final int Scene_Code = 3;
    private static final int AirState_Code = 4;
    private static final int Version_Code = 5;
    private static final int Ping_Code = 6;

    // </code>
    static {
        matcher.addURI(DbUttil.DATA_BASE_AUTHORITY, DbUttil.USER_TABLE, User_Code);
        matcher.addURI(DbUttil.DATA_BASE_AUTHORITY, DbUttil.DEVICE_TABLE, Device_Code);
        matcher.addURI(DbUttil.DATA_BASE_AUTHORITY, DbUttil.SCENE_TABLE, Scene_Code);
        matcher.addURI(DbUttil.DATA_BASE_AUTHORITY, DbUttil.AIR_STATES_TABLE, AirState_Code);
        matcher.addURI(DbUttil.DATA_BASE_AUTHORITY, DbUttil.VERSION_TABLE, Version_Code);
        matcher.addURI(DbUttil.DATA_BASE_AUTHORITY, DbUttil.PING_TIME_TABLE, Ping_Code);

    }

    @Override
    public boolean onCreate() {
        Utils.showLogE(TAG, "onCreate");
        LinuxCmd.runRootCommand("chmod 777 /dnake/cfg");
        LinuxCmd.runRootCommand("adb remount");
        LinuxCmd.runRootCommand("adb shell");
        this.context = getContext();
        DbContext dbContext = new DbContext(context);
        dbHelper = DbHelper.getInstance(dbContext);
        //dbHelper = DbHelper.getInstance(context);
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = dbHelper.getReadableDatabase();
        }
        if (!db.isOpen()) {
            db = dbHelper.getWritableDatabase();
        }
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int code = matcher.match(uri);
        Cursor cursor = null;
        try {
            switch (code) {
                case User_Code:
                    cursor = db.query(DbUttil.USER_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                    break;
                case Device_Code:
                    cursor = db.query(DbUttil.DEVICE_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                    break;
                case Scene_Code:
                    cursor = db.query(DbUttil.SCENE_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                    break;
                case AirState_Code:
                    cursor = db.query(DbUttil.AIR_STATES_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                    break;
                case Version_Code:
                    cursor = db.query(DbUttil.VERSION_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                    break;
                case Ping_Code:
                    cursor = db.query(DbUttil.PING_TIME_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                    break;
            }
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri result = null;
        long id = 0;
        int code = matcher.match(uri);
        try {
            switch (code) {
                case User_Code:
                    id = db.insert(DbUttil.USER_TABLE, null, values);
                    Utils.showLogE(TAG, "-----User_Table insert-----" + id);
                    break;
                case Device_Code:
                    id = db.insert(DbUttil.DEVICE_TABLE, null, values);
                    Utils.showLogE(TAG, "-----Device_Table insert-----" + id);
                    break;
                case Scene_Code:
                    id = db.insert(DbUttil.SCENE_TABLE, null, values);
                    Utils.showLogE(TAG, "-----Scene_Table insert-----" + id);
                    break;
                case AirState_Code:
                    id = db.insert(DbUttil.AIR_STATES_TABLE, null, values);
                    Utils.showLogE(TAG, "-----Air_State_Table insert-----" + id);
                    break;
                case Version_Code:
                    id = db.insert(DbUttil.VERSION_TABLE, null, values);
                    break;
                case Ping_Code:
                    id = db.insert(DbUttil.PING_TIME_TABLE, null, values);
                    break;
            }
            if (id > 0) {
                result = Uri.parse(uri + "/" + id);
            }
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int coder = -1;
        int code = matcher.match(uri);
        try {
            switch (code) {
                case User_Code:
                    coder = db.update(DbUttil.USER_TABLE, values, selection, selectionArgs);
                    Utils.showLogE(TAG, "-----User_Table update-----" + coder);
                    context.getContentResolver().notifyChange(uri, null);
                    break;
                case Device_Code:
                    coder = db.update(DbUttil.DEVICE_TABLE, values, selection, selectionArgs);
                    Utils.showLogE(TAG, "-----Device_Table update-----" + coder);
                    context.getContentResolver().notifyChange(uri, null);
                    break;
                case Scene_Code:
                    coder = db.update(DbUttil.SCENE_TABLE, values, selection, selectionArgs);
                    break;
                case AirState_Code:
                    coder = db.update(DbUttil.AIR_STATES_TABLE, values, selection, selectionArgs);
                    Utils.showLogE(TAG, "-----AirState_Code update-----" + coder);
                    context.getContentResolver().notifyChange(uri, null);
                    break;
                case Version_Code:
                    coder = db.update(DbUttil.VERSION_TABLE, values, selection, selectionArgs);
                    break;
                case Ping_Code:
                    coder = db.update(DbUttil.PING_TIME_TABLE, values, selection, selectionArgs);
                    break;
            }
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        }
        return coder;
    }
}
