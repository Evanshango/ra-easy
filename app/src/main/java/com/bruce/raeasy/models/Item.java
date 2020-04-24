package com.bruce.raeasy.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Item implements Parcelable {

    private String id, name, desc, tradeType, duration, price, tradeIn, amount, date, ownerId;
    private List<ImageUri> imageUris;

    public Item() {
    }

    public Item(String id, String name, String desc, String tradeType, String duration,
                String price, String tradeIn, String amount, String date, String ownerId,
                List<ImageUri> imageUris) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.tradeType = tradeType;
        this.duration = duration;
        this.price = price;
        this.tradeIn = tradeIn;
        this.amount = amount;
        this.date = date;
        this.ownerId = ownerId;
        this.imageUris = imageUris;
    }

    protected Item(Parcel in) {
        id = in.readString();
        name = in.readString();
        desc = in.readString();
        tradeType = in.readString();
        duration = in.readString();
        price = in.readString();
        tradeIn = in.readString();
        amount = in.readString();
        date = in.readString();
        ownerId = in.readString();
        imageUris = in.createTypedArrayList(ImageUri.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(desc);
        dest.writeString(tradeType);
        dest.writeString(duration);
        dest.writeString(price);
        dest.writeString(tradeIn);
        dest.writeString(amount);
        dest.writeString(date);
        dest.writeString(ownerId);
        dest.writeTypedList(imageUris);
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<ImageUri> getImageUris() {
        return imageUris;
    }

    public void setImageUris(List<ImageUri> imageUris) {
        this.imageUris = imageUris;
    }
}
