package com.yy.interview.model.util;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 安Y安酱 on 2018/3/31.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE interview(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "companyName VARCHAR(50)," +
                "interviewTime Date," +
                "interviewAddress VARCHAR(100)," +
                "linkedPhone VARCHAR(20)," +
                "state INTEGER DEFAULT 0" +
                ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "delete from interview";
        db.execSQL(sql);
    }
}
