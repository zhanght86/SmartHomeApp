package com.gatz.smarthomeapp.provider;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by zhouh on 2017/3/17.
 */
public class DbContext extends ContextWrapper{
    public DbContext(Context base) {
        super(base);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode,
                                               SQLiteDatabase.CursorFactory factory,
                                               DatabaseErrorHandler errorHandler) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("/dnake/cfg/smart_db", null);
        return db;
    }
}
