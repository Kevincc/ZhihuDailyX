/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kevin.zhihudaily.http;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.kevin.zhihudaily.Constants;

public class BroadcastNotifier {

    private LocalBroadcastManager mBroadcaster;

    /**
     * Creates a BroadcastNotifier containing an instance of
     * LocalBroadcastManager. LocalBroadcastManager is more efficient than
     * BroadcastManager; because it only broadcasts to components within the
     * app, it doesn't have to do parceling and so forth.
     * 
     * @param context
     *            a Context from which to get the LocalBroadcastManager
     */
    public BroadcastNotifier(Context context) {

        // Gets an instance of the support library local broadcastmanager
        mBroadcaster = LocalBroadcastManager.getInstance(context);

    }

    /**
     * 
     * Uses LocalBroadcastManager to send an {@link Intent} containing
     * {@code status}. The {@link Intent} has the action
     * {@code BROADCAST_ACTION} and the category {@code DEFAULT}.
     * 
     * @param status
     *            {@link Integer} denoting a work request status
     */
    public void broadcastNetworkState(boolean isConnected, int networkType) {

        Intent localIntent = new Intent();

        // The Intent contains the custom broadcast action for this app
        localIntent.setAction(Constants.Action.ACTION_NOTIFY_NET_STATE.toString());

        // Puts the status into the Intent
        localIntent.putExtra(Constants.EXTRA_NETWORK_ISCONNECTED, isConnected);
        localIntent.putExtra(Constants.EXTRA_NETWORK_TYPE, networkType);
        localIntent.addCategory(Intent.CATEGORY_DEFAULT);

        // Broadcasts the Intent
        mBroadcaster.sendBroadcast(localIntent);

    }

    /**
    * Uses LocalBroadcastManager to send an {@link String} containing a logcat
    * message. {@link Intent} has the action {@code BROADCAST_ACTION} and the
    * category {@code DEFAULT}.
    * 
    * @param logData
    *            a {@link String} to insert into the log.
    */
    public void notifyProgress(int progress) {
        Intent localIntent = new Intent();

        // The Intent contains the custom broadcast action for this app
        localIntent.setAction(Constants.Action.ACTION_NOTIFY_NEWS_LIST_UI.toString());

        // Puts data into the Intent
        localIntent.putExtra(Constants.EXTRA_NOTIFY_UI, Constants.NOTIFY_OFFLINE_DATA_READY);
        localIntent.putExtra(Constants.EXTRA_PROGRESS_PROGRESS, progress);
        localIntent.addCategory(Intent.CATEGORY_DEFAULT);

        // Broadcasts the Intent
        mBroadcaster.sendBroadcast(localIntent);

    }

    public void notifyDailyNewsDataReady(String cacheKey) {
        Intent localIntent = new Intent();

        localIntent.setAction(Constants.Action.ACTION_NOTIFY_NEWS_LIST_UI.toString());

        localIntent.putExtra(Constants.EXTRA_NOTIFY_UI, Constants.NOTIFY_NEWS_LIST_READY);

        localIntent.putExtra(Constants.EXTRA_CACHE_KEY, cacheKey);

        mBroadcaster.sendBroadcast(localIntent);
    }

    public void notifyNewsBodyDataReady(String date, int id) {
        Intent localIntent = new Intent();

        localIntent.setAction(Constants.Action.ACTION_NOTIFY_NEWS_DETAIL_READY.toString());

        localIntent.putExtra(Constants.EXTRA_NEWS_DATE, date);
        localIntent.putExtra(Constants.EXTRA_NEWS_ID, id);

        mBroadcaster.sendBroadcast(localIntent);
    }

    public void notifyCommentDataReady(int news_id, int comment_type) {
        Intent localIntent = new Intent();

        localIntent.setAction(Constants.Action.ACTION_NOTIFY_COMMENTS_READY.toString());

        localIntent.putExtra(Constants.EXTRA_NEWS_ID, news_id);
        localIntent.putExtra(Constants.EXTRA_COMMENT_TYPE, comment_type);

        mBroadcaster.sendBroadcast(localIntent);
    }
}
