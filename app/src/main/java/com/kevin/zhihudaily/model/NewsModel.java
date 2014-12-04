package com.kevin.zhihudaily.model;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONObject;

public class NewsModel extends BaseNewsModel implements Parcelable {
    public NewsModel() {

    }

    String body = null;

    int is_top_story = 0;

    //    String js = null;

    //    String css = null;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    //    public String getJs() {
    //        return js;
    //    }
    //
    //    public void setJs(String js) {
    //        this.js = js;
    //    }

    //    public String getCss() {
    //        return css;
    //    }
    //
    //    public void setCss(String css) {
    //        this.css = css;
    //    }

    public int isIs_top_story() {
        return is_top_story;
    }

    public void setIs_top_story(int is_top_story) {
        this.is_top_story = is_top_story;
    }

    @Override
    public boolean parseJSON(JSONObject json) {
        // TODO Auto-generated method stub
        if (json == null) {
            return false;
        }
        this.body = json.optString("body");
        //        this.js = json.optString("js");
        //        this.css = json.optString("css");
        return super.parseJSON(json);
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image_source);
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(image);
        dest.writeString(share_url);
        dest.writeInt(id);
        dest.writeString(ga_prefix);
        dest.writeString(thumbnail);
        dest.writeString(date);
        dest.writeString(body);
        dest.writeInt(is_top_story);
    }

    public static final Parcelable.Creator<NewsModel> CREATOR = new Parcelable.Creator<NewsModel>() {

        @Override public NewsModel createFromParcel(Parcel source) {
            return new NewsModel(source);
        }

        @Override public NewsModel[] newArray(int size) {
            return new NewsModel[size];
        }
    };

    private NewsModel(Parcel in) {
        image_source = in.readString();
        title = in.readString();
        url = in.readString();
        image = in.readString();
        share_url = in.readString();
        id = in.readInt();
        ga_prefix = in.readString();
        thumbnail = in.readString();
        date = in.readString();
        body = in.readString();
        is_top_story = in.readInt();

    }
}
