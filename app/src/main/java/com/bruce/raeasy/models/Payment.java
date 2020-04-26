package com.bruce.raeasy.models;

public class Payment {

    private String code, itemId, userId, amount, type, date, phone;

    public Payment() {
    }

    public Payment(String code, String itemId, String userId, String amount, String type,
                   String date, String phone) {
        this.code = code;
        this.itemId = itemId;
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
