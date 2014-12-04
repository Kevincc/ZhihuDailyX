package com.kevin.zhihudaily;

import android.app.Application;
import android.os.SystemClock;
import com.kevin.zhihudaily.db.DataBaseManager;
import com.kevin.zhihudaily.db.DataCache;

public class ZhihuDailyApplication extends Application {

    private static ZhihuDailyApplication mInstance;
    public static boolean sIsConnected = false;
    public static int sNetworkType = -1;

    private long mStatTime;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        // fot stat
        //        mStatTime = SystemClock.elapsedRealtime();

        //        Utils.enableStrictMode();

        mInstance = this;

        // init database
        DataBaseManager.newInstance(this);
    }

    @Override
    public void onTerminate() {
        // TODO Auto-generated method stub
        super.onTerminate();

        DataCache.getInstance().clearAllCache();

        long livetime = SystemClock.elapsedRealtime() - mStatTime;
        //        StatService.onEventDuration(this, getString(R.string.stat_event_app_live),
        //                getString(R.string.stat_label_app_live), livetime);
    }

    public static ZhihuDailyApplication getInstance() {
        return mInstance;
    }

}
