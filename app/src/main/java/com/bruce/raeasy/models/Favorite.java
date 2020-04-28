package com.bruce.raeasy.models;

public class Favorite {

    private String favId, itemId, date, userId;

    public Favorite() {
    }

    public Favorite(String favId, String itemId, String date, String userId) {
        this.favId = favId;
        this.itemId = itemId;
        this.date = date;
        this.userId = userId;
    }

    public String getFavId() {
        return favId;
    }

    public void setFavId(String favId) {
        this.favId = favId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Favorite{" +
                "favId='" + favId + '\'' +
                ", itemId='" + itemId + '\'' +
                ", date='" + date + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
