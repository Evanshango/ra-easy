package com.bruce.raeasy.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Item implements Parcelable {

    private String itemId, name, desc, tradeType, duration, price, tradeIn, amount, date, ownerId,
            transCode;
    private List<ImageUrl> imageUrls;
    private List<String> userIds;

    public Item() {
    }

    public Item(String itemId, String name, String desc, String tradeType, String duration,
                String price, String tradeIn, String amount, String date, String ownerId,
                String transCode, List<ImageUrl> imageUrls, List<String> userIds) {
        this.itemId = itemId;
        this.name = name;
        this.desc = desc;
        this.tradeType = tradeType;
        this.duration = duration;
        this.price = price;
        this.tradeIn = tradeIn;
        this.amount = amount;
        this.date = date;
        this.ownerId = ownerId;
        this.transCode = transCode;
        this.imageUrls = imageUrls;
        this.userIds = userIds;
    }

    protected Item(Parcel in) {
        itemId = in.readString();
        name = in.readString();
        desc = in.readString();
        tradeType = in.readString();
        duration = in.readString();
        price = in.readString();
        tradeIn = in.readString();
        amount = in.readString();
        date = in.readString();
        ownerId = in.readString();
        transCode = in.readString();
        imageUrls = in.createTypedArrayList(ImageUrl.CREATOR);
        userIds = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemId);
        dest.writeString(name);
        dest.writeString(desc);
        dest.writeString(tradeType);
        dest.writeString(duration);
        dest.writeString(price);
        dest.writeString(tradeIn);
        dest.writeString(amount);
        dest.writeString(date);
        dest.writeString(ownerId);
        dest.writeString(transCode);
        dest.writeTypedList(imageUrls);
        dest.writeStringList(userIds);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTradeIn() {
        return tradeIn;
    }

    public void setTradeIn(String tradeIn) {
        this.tradeIn = tradeIn;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode;
    }

    public List<ImageUrl> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<ImageUrl> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }
}
