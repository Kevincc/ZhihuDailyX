package com.kevin.zhihudaily.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.atomic.AtomicInteger;

public class DataBaseManager {
    private DataBaseHelper mHelper;
    private SQLiteDatabase db;
    private static DataBaseManager mInstance;
    private static Context mContext;
    private AtomicInteger mUseCounter = new AtomicInteger();

    private DataBaseManager(Context context) {
        mHelper = new DataBaseHelper(context);

        db = mHelper.getWritableDatabase();

        initDataTimeStamp();
    }

    public static synchronized void newInstance(Context context) {
        if (mInstance == null) {
            mContext = context;
            mInstance = new DataBaseManager(context);
        }
    }

    public static synchronized DataBaseManager getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException(DataBaseManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }
        return mInstance;
    }

    private void initDataTimeStamp() {
        SharedPreferences timestamp = mContext.getSharedPreferences(DataBaseConstants.SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        DataBaseConstants.TIME_STAMP_ID = timestamp.getInt(DataBaseConstants.SP_TIME_STAMP, 0);
    }

    public int getDataTimeStamp() {
        return DataBaseConstants.TIME_STAMP_ID;
    }

    private synchronized SQLiteDatabase openDatabase() {
        if (mUseCounter.incrementAndGet() == 1) {
            // open database
            db = mHelper.getWritableDatabase();
        }
        return db;
    }

    private synchronized void closeDatabase() {
        if (mUseCounter.decrementAndGet() == 0) {
            // close database
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public void executeQuery(QueryExecutor executor) {
        SQLiteDatabase database = openDatabase();
        executor.run(database);
        closeDatabase();
    }

    public void executeQueryTask(final QueryExecutor executor) {
        new Thread(new Runnable() {
            @Override public void run() {
                SQLiteDatabase database = openDatabase();
                executor.run(database);
                closeDatabase();
            }
        }).start();
    }

    /**
     * Check data expire
     *
     * @param timestamp
     * @return
     */
    public int checkDataExpire(int timestamp) {
        if (timestamp > DataBaseConstants.TIME_STAMP_ID) {
            return 1;
        } else if (timestamp == DataBaseConstants.TIME_STAMP_ID) {
            return 0;
        } else {
            return -1;
        }
    }

    public void setDataTimeStamp(int timestamp) {
        DataBaseConstants.TIME_STAMP_ID = timestamp;
        SharedPreferences sp = mContext.getSharedPreferences(DataBaseConstants.SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putInt(DataBaseConstants.SP_TIME_STAMP, timestamp);
        editor.commit();
    }

}
