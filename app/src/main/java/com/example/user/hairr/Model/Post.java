package com.example.user.hairr.Model;

public class Post {
    String username, postImageUrl, userImage, posterId, userSpecialization, posttText, date;
    int likes, comments;

    public Post() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getPosterId() {
        return posterId;
    }

    public void setPosterId(String posterId) {
        this.posterId = posterId;
    }

    public String getUserSpecialization() {
        return userSpecialization;
    }

    public void setUserSpecialization(String userSpecialization) {
        this.userSpecialization = userSpecialization;
    }

    public String getPosttText() {
        return posttText;
    }

    public void setPosttText(String posttText) {
        this.posttText = posttText;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }
}