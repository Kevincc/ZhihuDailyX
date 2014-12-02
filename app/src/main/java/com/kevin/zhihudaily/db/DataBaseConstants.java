package com.kevin.zhihudaily.db;

import android.provider.BaseColumns;

public class DataBaseConstants implements BaseColumns {

    public static final String SHARED_PREFERENCES_NAME = "timestamp";
    public static final String DATABASE_NAME = "news.db";
    public static final int DATABASE_VERSION = 1;

    // data update timestamp id
    public static int TIME_STAMP_ID = 0;
    public static final String SP_TIME_STAMP = "timestamp";

    // Constants for building SQLite tables during initialization
    private static final String TEXT_TYPE = "TEXT";
    private static final String PRIMARY_KEY_TYPE = "INTEGER PRIMARY KEY";
    private static final String INTEGER_TYPE = "INTEGER";

    public static final String NEWS_TABLE_NAME = "NewsData";
    public static final String ID = "id";
    public static final String DATE = "date";
    public static final String GA_PREFIX = "ga_prefix";
    public static final String IS_TOP_STORY = "is_top_story";
    public static final String TITLE = "title";
    public static final String URL = "url";
    public static final String IMAGE_SOURCE = "image_source";
    public static final String IMAGE_URL = "image";
    public static final String IMAGE_THUMBNAIL = "thumbnail";
    public static final String SHARE_URL = "share_url";
    public static final String BODY = "body";
    public static final String CSS = "css";
    public static final String JS = "js";

    public static final String CREATE_NEWS_TABLE = "CREATE TABLE if not Exists [" + NEWS_TABLE_NAME + "] ( " //
            + ID + " " + PRIMARY_KEY_TYPE + " ," //
            + DATE + " " + TEXT_TYPE + " ," //
            + GA_PREFIX + " " + TEXT_TYPE + " ," //
            + IS_TOP_STORY + " " + INTEGER_TYPE + " DEFAULT 0," //
            + TITLE + " " + TEXT_TYPE + " ," //
            + URL + " " + TEXT_TYPE + " ," //
            + IMAGE_SOURCE + " " + TEXT_TYPE + " ," //
            + IMAGE_URL + " " + TEXT_TYPE + " ," //
            + IMAGE_THUMBNAIL + " " + TEXT_TYPE + " ," //
            + SHARE_URL + " " + TEXT_TYPE + " ," //
            + BODY + " " + TEXT_TYPE + " ," //
            + CSS + " " + TEXT_TYPE + " ," //
            + JS + " " + TEXT_TYPE + ")"; //

}
