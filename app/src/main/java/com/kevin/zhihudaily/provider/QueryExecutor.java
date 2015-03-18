package com.kevin.zhihudaily.provider;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by chenchao04 on 2014-12-02.
 */
public interface QueryExecutor {
    public void run(SQLiteDatabase database);
}
