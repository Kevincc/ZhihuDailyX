package com.kevin.zhihudaily.model;

import java.util.List;

public class CommentsModel {
    List<Comment> latest;

    public List<Comment> getComments() {
        return latest;
    }

    public void setComments(List<Comment> latest) {
        this.latest = latest;
    }
}
