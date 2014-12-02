package com.kevin.zhihudaily.http;

import retrofit.RestAdapter;

public class ZhihuRequest {

    private static ZhihuRequestService mRequestService;

    public static final String BASE_URL = "http://news.at.zhihu.com/api/2";

    public static ZhihuRequestService getRequestService() {
        if (mRequestService == null) {
            RestAdapter adapter = new RestAdapter.Builder().setEndpoint(BASE_URL).build();

            mRequestService = adapter.create(ZhihuRequestService.class);
        }
        return mRequestService;
    }
}
