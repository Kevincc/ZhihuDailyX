package com.kevin.zhihudaily.http;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.kevin.zhihudaily.ZhihuDailyApplication;

public class NetworkListener extends BroadcastReceiver {
    public static final String TAG = NetworkListener.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = null;
        try {
            activeNetInfo = connectivityManager.getActiveNetworkInfo();
        } catch (Exception e) {
        }

        if (activeNetInfo == null) {
            return;
        }

        boolean isConnected = activeNetInfo.isConnected();
        int networkType = activeNetInfo.getType();

        // set application fields
        ZhihuDailyApplication.sIsConnected = isConnected;
        ZhihuDailyApplication.sNetworkType = networkType;

        // broadcast network state
        BroadcastNotifier notifier = new BroadcastNotifier(ZhihuDailyApplication.getInstance());
        notifier.broadcastNetworkState(isConnected, networkType);
    }
}
