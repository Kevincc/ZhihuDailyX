package com.kevin.zhihudaily.model;

import java.util.List;

public class CommentsModel {
    List<Comment> latest;
    int comments_type;

    public List<Comment> getComments() {
        return latest;
    }

    public void setComments(List<Comment> latest) {
        this.latest = latest;
    }

    public int getComments_type() {
        return comments_type;
    }

    public void setComments_type(int comments_type) {
        this.comments_type = comments_type;
    }
}
