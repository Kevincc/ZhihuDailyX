package com.kevin.zhihudaily;

public class Constants {

    public static final String EXTRA_CACHE_ID = "cache_id";

    public static enum Action {
        /**
         *
         */
        ACTION_NONE,
        /**
         *
         */
        ACTION_WRITE_DAILY_NEWS,
        /**
         *
         */
        ACTION_WRITE_NEWS_DEATIL,
        /**
         *
         */
        ACTION_READ_DAILY_NEWS,
        /**
         *
         */
        ACTION_READ_NEWS_DEATIL,
        /**
         *
         */
        ACTION_GET_TODAY_NEWS,
        /**
         *
         */
        ACTION_GET_DAILY_NEWS,
        /**
         *
         */
        ACTION_GET_NEWS_DETAIL,
        /**
         *
         */
        ACTION_START_OFFLINE_DOWNLOAD,
        /**
         *
         */
        ACTION_GET_LONG_COMMENTS,
        /**
         *
         */
        ACTION_GET_SHORT_COMMENTS,
        /**
         *
         */
        ACTION_READ_LASTEST_NEWS,
        /**
         *
         */
        ACTION_NOTIFY_NET_STATE,
        /**
         *
         */
        ACTION_NOTIFY_NEWS_LIST_UI,
        /**
         *
         */
        ACTION_NOTIFY_NEWS_DETAIL_READY,
        /**
         *
         */
        ACTION_NOTIFY_COMMENTS_READY,

    }

    public static final String EXTRA_NEWS_NUM = "news_num";
    public static final String EXTRA_NEWS_INDEX = "news_index";
    public static final String EXTRA_NEWS_DATE = "news_date";
    public static final String EXTRA_NEWS_ID = "news_id";
    public static final String EXTRA_NEWS_TITLE = "news_title";
    public static final String EXTRA_NEWS_URL = "news_url";
    public static final String EXTRA_NEWS_IMAGE_SOURCE = "news_image_source";
    public static final String EXTRA_NEWS_IMAGE_URL = "news_image_url";
    public static final String EXTRA_NEWS_BODY = "news_body";

    // Defines a custom Intent action
    //    public static final String ACTION_BROADCAST = "com.kevin.zhihudaily.BROADCAST";

    // Defines the key for the status "extra" in an Intent
    public static final String EXTRA_NETWORK_ISCONNECTED = "com.kevin.zhihudaily.NETWORK_ISCONNECTED";
    public static final String EXTRA_NETWORK_TYPE = "com.kevin.zhihudaily.NETWORK_TYPE";

    // Defines a custom Intent action
    public static final String EXTRA_NOTIFY_UI = "notify_ui";

    public static final int NOTIFY_NEWS_LIST_READY = 0x01;
    public static final int NOTIFY_OFFLINE_DATA_READY = 0x02;

    public static final String EXTRA_CACHE_KEY = "com.kevin.zhihudaily.CACHE_KEY";

    public static final String EXTRA_PROGRESS_MAX = "com.kevin.zhihudaily.PROGRESS_MAX";
    public static final String EXTRA_PROGRESS_PROGRESS = "com.kevin.zhihudaily.PROGRESS_INCR";

    public static final String EXTRA_COMMENT_TYPE = "comment_type";
    public static final int COMMENT_TYPE_LONG = 0;
    public static final int COMMENT_TYPE_SHORT = 1;

}
