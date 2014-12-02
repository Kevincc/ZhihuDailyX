package com.kevin.zhihudaily.http;

import com.kevin.zhihudaily.model.CommentsModel;
import com.kevin.zhihudaily.model.DailyNewsModel;
import com.kevin.zhihudaily.model.NewsModel;
import retrofit.http.GET;
import retrofit.http.Path;

public interface ZhihuRequestService {

    @GET("/news/latest") DailyNewsModel getDailyNewsToday();

    @GET("/news/{id}") NewsModel getNewsById(@Path("id") int id);

    @GET("/news/before/{date}") DailyNewsModel getDailyNewsByDate(@Path("date") String date);

    @GET("/story/{id}/long-comments") CommentsModel getLongCommentsById(@Path("id") int id);

    @GET("/story/{id}/short-comments") CommentsModel getShortCommentsById(@Path("id") int id);
}
