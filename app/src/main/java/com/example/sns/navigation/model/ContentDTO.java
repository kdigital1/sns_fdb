package com.example.sns.navigation.model;

import java.util.HashMap;
import java.util.Map;

public class ContentDTO {
    private String explain;
    private String imageUri;
    private String uid;
    private String userId;
    private String timestamp;
    private int favoriteCount;
    private Map<String,Boolean> favorites  = new HashMap<>();

    public ContentDTO(){

    }


//    public ContentDTO(String explain, String imageUri, String uid,
//                      String userId, String timestamp, int favoriteCount,
//                      HashMap<String, Boolean> user) {
//        this.explain = explain;
//        this.imageUri = imageUri;
//        this.uid = uid;
//        this.userId = userId;
//        this.timestamp = timestamp;
//        this.favoriteCount = favoriteCount;
//        this.user = user;
//    }

//    public ContentDTO(String explain, String imageUri, String uid,
//                      String userId, String timestamp, int favoriteCount,
//                      HashMap<String, Boolean> user) {
//        this.explain = explain;
//        this.imageUri = imageUri;
//        this.uid = uid;
//        this.userId = userId;
//        this.timestamp = timestamp;
//        this.favoriteCount = favoriteCount;
//        this.user = user;
//    }


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

    public void setUid(String uid) {
        this.uid = uid;
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
