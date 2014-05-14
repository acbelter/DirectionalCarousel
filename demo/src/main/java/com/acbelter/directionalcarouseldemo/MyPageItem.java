package com.acbelter.directionalcarouseldemo;

import android.os.Parcel;
import android.os.Parcelable;

public class MyPageItem implements Parcelable {
    private String mTitle;

    public MyPageItem(String title) {
        mTitle = title;
    }

    private MyPageItem(Parcel in) {
        mTitle = in.readString();
    }

    public String getTitle() {
        return mTitle;
    }

    public static final Parcelable.Creator<MyPageItem> CREATOR =
            new Parcelable.Creator<MyPageItem>() {
                @Override
                public MyPageItem createFromParcel(Parcel in) {
                    return new MyPageItem(in);
                }

                @Override
                public MyPageItem[] newArray(int size) {
                    return new MyPageItem[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mTitle);
    }
}

