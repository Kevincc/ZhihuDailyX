package com.kevin.zhihudaily.http;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kevin.zhihudaily.ZhihuDailyApplication;

public class NetworkListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean isConnected = NetworkUtil.getConnectivtityStatus(context);
        int networkType = NetworkUtil.getConnectivtityType(context);

        // set application fields
        ZhihuDailyApplication.sIsConnected = isConnected;
        ZhihuDailyApplication.sNetworkType = networkType;

        // broadcast network state

    }
}
