package com.kevin.zhihudaily.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class DailyNewsModel implements Parcelable {

    String date = null;
    List<NewsModel> news;
    boolean is_today = false;
    List<NewsModel> top_stories;
    String display_date = null;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<NewsModel> getNewsList() {
        return news;
    }

    public void setNewsList(List<NewsModel> news) {
        this.news = news;
    }

    public boolean isIs_today() {
        return is_today;
    }

    public void setIs_today(boolean is_today) {
        this.is_today = is_today;
    }

    public List<NewsModel> getTopStories() {
        return top_stories;
    }

    public void setTopStories(List<NewsModel> top_stories) {
        this.top_stories = top_stories;
    }

    public String getDisplay_date() {
        return display_date;
    }

    public void setDisplay_date(String display_date) {
        this.display_date = display_date;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeTypedList(news);
        dest.writeByte(is_today ? (byte) 1 : (byte) 0);
        dest.writeTypedList(top_stories);
        dest.writeString(this.display_date);
    }

    public DailyNewsModel() {
        news = new ArrayList<NewsModel>();
        top_stories = new ArrayList<NewsModel>();
    }

    private DailyNewsModel(Parcel in) {
        this.date = in.readString();
        news = new ArrayList<NewsModel>();
        in.readTypedList(news, NewsModel.CREATOR);
        this.is_today = in.readByte() != 0;
        top_stories = new ArrayList<NewsModel>();
        in.readTypedList(top_stories, NewsModel.CREATOR);
        this.display_date = in.readString();
    }

    public static final Parcelable.Creator<DailyNewsModel> CREATOR = new Parcelable.Creator<DailyNewsModel>() {
        public DailyNewsModel createFromParcel(Parcel source) {
            return new DailyNewsModel(source);
        }

        public DailyNewsModel[] newArray(int size) {
            return new DailyNewsModel[size];
        }
    };
}
