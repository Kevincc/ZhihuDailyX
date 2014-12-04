package com.kevin.zhihudaily;

import com.halfbit.tinybus.TinyBus;

/**
 * Created by chenchao04 on 2014-12-04.
 */
public class EventBus {
    private static TinyBus mBus;

    private EventBus() {

    }

    public static TinyBus getInstance() {
        if (mBus == null) {
            mBus = TinyBus.from(ZhihuDailyApplication.getInstance().getApplicationContext());
        }
        return mBus;
    }
}
