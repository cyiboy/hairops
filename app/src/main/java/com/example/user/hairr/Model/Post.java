package com.example.user.hairr.Model;

public class Post {
        String username,postImageUrl,userImage,posterId,userSpecialization,posttText;

    public Post() {
    }

    public String getPosttText() {
        return posttText;
    }

    public void setPosttText(String posttText) {
        this.posttText = posttText;
    }

    public String getUserSpecialization() {
        return userSpecialization;
    }

    public void setUserSpecialization(String userSpecialization) {
        this.userSpecialization = userSpecialization;
    }

    public String getPosterId() {
        return posterId;
    }

    public void setPosterId(String posterId) {
        this.posterId = posterId;
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
}
