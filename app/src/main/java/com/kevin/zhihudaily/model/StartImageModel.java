package com.kevin.zhihudaily.model;

import org.json.JSONObject;

public class StartImageModel {

    String text = null;
    String img = null;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public boolean parseJSON(JSONObject json) {
        if (json == null) {
            return false;
        }
        this.text = json.optString("text");
        this.text = json.optString("img");
        return true;
    }
}
