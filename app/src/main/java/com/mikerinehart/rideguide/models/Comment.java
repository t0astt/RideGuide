package com.mikerinehart.rideguide.models;

import java.util.Date;

public class Comment {

    protected int id;
    protected int reviewee_user_id;
    protected int reviewer_user_id;
    protected String title;
    protected String comment;
    protected Date created_at;
    protected Date updated_at;

    public Comment(int id, int reviewee_user_id, int reviewer_user_id, String title, String comment, Date created_at, Date updated_at) {
        this.id = id;
        this.reviewee_user_id = reviewee_user_id;
        this.reviewer_user_id = reviewer_user_id;
        this.title = title;
        this.comment = comment;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReviewee_user_id() {
        return reviewee_user_id;
    }

    public void setReviewee_user_id(int reviewee_user_id) {
        this.reviewee_user_id = reviewee_user_id;
    }

    public int getReviewer_user_id() {
        return reviewer_user_id;
    }

    public void setReviewer_user_id(int reviewer_user_id) {
        this.reviewer_user_id = reviewer_user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

}
