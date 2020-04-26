package com.bruce.raeasy.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ExtraImgUri implements Parcelable {

    private List<ImageUri> mImageUris;

    public ExtraImgUri(List<ImageUri> imageUris) {
        mImageUris = imageUris;
    }

    protected ExtraImgUri(Parcel in) {
        mImageUris = in.createTypedArrayList(ImageUri.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mImageUris);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ExtraImgUri> CREATOR = new Creator<ExtraImgUri>() {
        @Override
        public ExtraImgUri createFromParcel(Parcel in) {
            return new ExtraImgUri(in);
        }

        @Override
        public ExtraImgUri[] newArray(int size) {
            return new ExtraImgUri[size];
        }
    };

    public List<ImageUri> getImageUris() {
        return mImageUris;
    }
}
