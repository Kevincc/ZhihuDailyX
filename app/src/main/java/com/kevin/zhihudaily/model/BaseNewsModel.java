package com.kevin.zhihudaily.model;

import org.json.JSONObject;

public class BaseNewsModel {
    String image_source = null;
    String title = null;
    String url = null;
    String image = null;
    String share_url = null;
    int id = 0;
    String ga_prefix = null;
    String thumbnail = null;
    String date = null;

    public String getImage_source() {
        return image_source;
    }

    public void setImage_source(String image_source) {
        this.image_source = image_source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGa_prefix() {
        return ga_prefix;
    }

    public void setGa_prefix(String ga_prefix) {
        this.ga_prefix = ga_prefix;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean parseJSON(JSONObject json) {
        if (json == null) {
            return false;
        }

        this.image_source = json.optString("image_source");
        this.title = json.optString("title");
        this.url = json.optString("url");
        this.image = json.optString("image");
        this.share_url = json.optString("share_url");
        this.thumbnail = json.optString("thumbnail");
        this.ga_prefix = json.optString("ga_prefix");
        this.id = json.optInt("id");
        return true;
    }
}
