package com.bruce.raeasy.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class ImageUri implements Parcelable {

    private Uri mUri;

    public ImageUri(Uri uri) {
        mUri = uri;
    }

    protected ImageUri(Parcel in) {
        mUri = in.readParcelable(Uri.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mUri, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageUri> CREATOR = new Creator<ImageUri>() {
        @Override
        public ImageUri createFromParcel(Parcel in) {
            return new ImageUri(in);
        }

        @Override
        public ImageUri[] newArray(int size) {
            return new ImageUri[size];
        }
    };

    public Uri getUri() {
        return mUri;
    }
}
