package com.example.sns.navigation.model;

import java.util.HashMap;
import java.util.Map;

public class ContentDTO {
    private String explain;
    private String imageUri;
    private String uid;
    private String email;
    private String userId;
    private String timestamp;
    private int favoriteCount;
    private String username;
    private String getProfileUri;
    private Map<String,Boolean> favorites  = new HashMap<>();
    private String destinationUid = null;
    public ContentDTO(){

    }
    public String getDestinationUid() {
        return destinationUid;
    }

    public void setDestinationUid(String destinationUid) {
        this.destinationUid = destinationUid;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public Map<String, Boolean> getFavorites() {
        return favorites;
    }

    public void setFavorites(Map<String, Boolean> favorites) {
        this.favorites = favorites;
    }

    public String getProfileUri() {
        return getProfileUri;
    }

    public void setProfileUri(String getProfileUri) {
        this.getProfileUri = getProfileUri;
    }

    public static class Comment{
        private String uid;
        private String userId;
        private String comment;
        private String timestamp;

        public Comment(){

        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }


    }

}
