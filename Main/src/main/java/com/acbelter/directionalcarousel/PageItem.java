package com.acbelter.directionalcarousel;

import android.os.Parcel;
import android.os.Parcelable;

public class PageItem implements Parcelable {
    private String mTitle;

    public PageItem(String title) {
        mTitle = title;
    }

    private PageItem(Parcel in) {
        mTitle = in.readString();
    }

    public String getTitle() {
        return mTitle;
    }

    public static final Parcelable.Creator<PageItem> CREATOR =
            new Parcelable.Creator<PageItem>() {
                @Override
                public PageItem createFromParcel(Parcel in) {
                    return new PageItem(in);
                }

                @Override
                public PageItem[] newArray(int size) {
                    return new PageItem[size];
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
