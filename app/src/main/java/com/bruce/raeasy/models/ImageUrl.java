package com.bruce.raeasy.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageUrl implements Parcelable {

    private String imgUrl;

    public ImageUrl() {
    }

    public ImageUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    protected ImageUrl(Parcel in) {
        imgUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imgUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageUrl> CREATOR = new Creator<ImageUrl>() {
        @Override
        public ImageUrl createFromParcel(Parcel in) {
            return new ImageUrl(in);
        }

        @Override
        public ImageUrl[] newArray(int size) {
            return new ImageUrl[size];
        }
    };

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
