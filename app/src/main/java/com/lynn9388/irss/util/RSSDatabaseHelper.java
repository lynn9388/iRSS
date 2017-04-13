package com.lynn9388.irss.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lynn on 2015/9/9.
 */
public class RSSDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RSS.db";

    public RSSDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RSS.FeedEntry.SQL_CREATE_ENTRY);
        db.execSQL(RSS.ArticleEntry.SQL_CREATE_ENTRY);
        db.execSQL(RSS.FavoriteEntry.SQL_CREATE_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXITSTS " + RSS.FavoriteEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXITSTS " + RSS.ArticleEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXITSTS " + RSS.FeedEntry.TABLE_NAME);
            onCreate(db);
        }
    }
}
