package com.lynn9388.irss.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by Lynn on 2015/9/9.
 */
public final class RSS {
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String NOT_NULL = " NOT NULL";
    private static final String COMMA_SEP = ",";

    public RSS() {
    }

    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "feed";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_ICON_URL = "icon_url";

        public static final String SQL_CREATE_ENTRY =
                "CREATE TABLE " + TABLE_NAME + " ("
                        + _ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP
                        + COLUMN_NAME_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP
                        + COLUMN_NAME_URL + TEXT_TYPE + NOT_NULL + " UNIQUE" + COMMA_SEP
                        + COLUMN_NAME_ICON_URL + TEXT_TYPE + " );";

        public static long insert(RSSDatabaseHelper databaseHelper, String name,
                                  String url, String iconURL) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_NAME, name);
            values.put(COLUMN_NAME_URL, url);
            values.put(COLUMN_NAME_ICON_URL, iconURL);

            return db.insert(TABLE_NAME, null, values);
        }

        /**
         * Query the {@link com.lynn9388.irss.util.RSS.FeedEntry} table, return over the result set.
         *
         * @param databaseHelper
         * @param projection     Provide witch columns will be returned, set to null for all columns.
         * @return
         */
        public static final Cursor queryAll(RSSDatabaseHelper databaseHelper, String[] projection) {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            String sortOrder = _ID + " DESC";
            return db.query(TABLE_NAME, projection, null, null, null, null, sortOrder);
        }

        /**
         * Query a {@link Cursor} of {@link com.lynn9388.irss.util.RSS.FeedEntry}, it will return
         * a empty result if doesn't exist Feed of id.
         *
         * @param databaseHelper
         * @param id
         * @return A {@link Cursor} object, which has been moved to first.
         */
        public static Cursor query(RSSDatabaseHelper databaseHelper, int id) {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();

            String selection = _ID + " = ?";
            String[] selectionArgs = {String.valueOf(id)};

            Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);
            cursor.moveToFirst();
            return cursor;
        }

        /**
         * Query a {@link Cursor} of {@link com.lynn9388.irss.util.RSS.FeedEntry}, it will return
         * a empty result if doesn't exist Feed of url
         *
         * @param databaseHelper
         * @param url            The homepage address of Feed
         * @return A {@link Cursor} object, which has been moved to first.
         */
        public static Cursor query(RSSDatabaseHelper databaseHelper, String url) {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();

            String selection = COLUMN_NAME_URL + " = ?";
            String[] selectionArgs = {url};

            Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);
            cursor.moveToFirst();
            return cursor;

        }
    }

    public static abstract class ArticleEntry {
        public static final String TABLE_NAME = "article";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_FEED_ID = "feed_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SUBTITLE = "subtitle";
        public static final String COLUMN_NAME_PHOTO_URL = "photo_url";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_PAGE_INDEX = "page_index";


        public static final String SQL_CREATE_ENTRY =
                "CREATE TABLE " + TABLE_NAME + " ("
                        + COLUMN_NAME_URL + TEXT_TYPE + PRIMARY_KEY + COMMA_SEP
                        + COLUMN_NAME_FEED_ID + INTEGER_TYPE + NOT_NULL
                        + " REFERENCES " + TABLE_NAME + " (" + FeedEntry._ID + " )" + COMMA_SEP
                        + COLUMN_NAME_TITLE + TEXT_TYPE + NOT_NULL + COMMA_SEP
                        + COLUMN_NAME_SUBTITLE + TEXT_TYPE + COMMA_SEP
                        + COLUMN_NAME_PHOTO_URL + TEXT_TYPE + COMMA_SEP
                        + COLUMN_NAME_CONTENT + TEXT_TYPE + NOT_NULL + COMMA_SEP
                        + COLUMN_NAME_TIMESTAMP + INTEGER_TYPE + COMMA_SEP
                        + COLUMN_NAME_PAGE_INDEX + INTEGER_TYPE + NOT_NULL + " );";

        public static long insert(RSSDatabaseHelper databaseHelper, String url, int feedId,
                                  String title, String subtitle, String photoURL, String content,
                                  long timestamp, int pageIndex) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_URL, url);
            values.put(COLUMN_NAME_FEED_ID, feedId);
            values.put(COLUMN_NAME_TITLE, title);
            values.put(COLUMN_NAME_SUBTITLE, subtitle);
            values.put(COLUMN_NAME_PHOTO_URL, photoURL);
            values.put(COLUMN_NAME_CONTENT, content);
            values.put(COLUMN_NAME_TIMESTAMP, timestamp);
            values.put(COLUMN_NAME_PAGE_INDEX, pageIndex);

            return db.insert(TABLE_NAME, null, values);
        }

        public static final Cursor queryAll(RSSDatabaseHelper databaseHelper, String[] projection) {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            String sortOrder = COLUMN_NAME_TIMESTAMP + " ASC";
            return db.query(TABLE_NAME, projection, null, null, null, null, sortOrder);
        }

        public static final Cursor query(RSSDatabaseHelper databaseHelper, String[] projection, int feedId) {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            String selection = COLUMN_NAME_FEED_ID + " = ?";
            String[] selectionArgs = {String.valueOf(feedId)};
            String sortOrder = COLUMN_NAME_PAGE_INDEX + " ASC";
            Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            cursor.moveToFirst();
            return cursor;
        }

        /**
         * Query the article of whose address is url.
         *
         * @param databaseHelper
         * @param url
         * @return
         */
        public static final Cursor query(RSSDatabaseHelper databaseHelper, String url) {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            String selection = COLUMN_NAME_URL + " = ?";
            String[] selectionArgs = {url};
            Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);
            cursor.moveToFirst();
            return cursor;
        }
    }

    public static abstract class FavoriteEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorite";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_PHOTO_URL = "photo_url";

        public static final String SQL_CREATE_ENTRY =
                "CREATE TABLE " + TABLE_NAME + " ("
                        + _ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP
                        + COLUMN_NAME_URL + TEXT_TYPE + NOT_NULL + " UNIQUE"
                        + " REFERENCES " + ArticleEntry.TABLE_NAME + " (" + FeedEntry._ID + " )" + COMMA_SEP
                        + COLUMN_NAME_TITLE + TEXT_TYPE + NOT_NULL + COMMA_SEP
                        + COLUMN_NAME_PHOTO_URL + TEXT_TYPE + " );";

        public static long insert(RSSDatabaseHelper databaseHelper, String url,
                                  String title, String photoURL) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_URL, url);
            values.put(COLUMN_NAME_TITLE, title);
            values.put(COLUMN_NAME_PHOTO_URL, photoURL);

            return db.insert(TABLE_NAME, null, values);
        }

        public static final Cursor queryAll(RSSDatabaseHelper databaseHelper, String[] projection) {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            String sortOrder = _ID + " DESC";
            return db.query(TABLE_NAME, projection, null, null, null, null, sortOrder);
        }


    }

}
