package com.example.user.your_breakfast.model;

public class Comment {
    private String userName, timeSubmit, star, contentComment, imgUser;

    public Comment() {
    }

    public Comment( String userName, String timeSubmit, String star, String contentComment, String imgUser) {
        this.userName = userName;
        this.timeSubmit = timeSubmit;
        this.star = star;
        this.contentComment = contentComment;
        this.imgUser = imgUser;
    }

    public String getImgUser() {
        return imgUser;
    }

    public void setImgUser(String imgUser) {
        this.imgUser = imgUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTimeSubmit() {
        return timeSubmit;
    }

    public void setTimeSubmit(String timeSubmit) {
        this.timeSubmit = timeSubmit;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getContentComment() {
        return contentComment;
    }

    public void setContentComment(String contentComment) {
        this.contentComment = contentComment;
    }
}
