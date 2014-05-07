package com.acbelter.directionalcarousel;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

public class CarouselConfig implements Parcelable {
    // You can choose a bigger number for LOOPS, but you know, nobody will fling
    // more than 10000 times just in order to test your "infinite" ViewPager :D
    public static final int LOOPS = 10000;
    public static final float BIG_SCALE = 1.0f;
    public static final float SMALL_SCALE = 0.7f;
    public static final float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    public float scaleX = 1.0f;
    public float scaleY = 1.0f;

    public int orientation = CarouselConfig.HORIZONTAL;
    public int visiblePages = 0;
    public int pagerId = View.NO_ID;

    public CarouselConfig() {}

    private CarouselConfig(Parcel in) {
        scaleX = in.readFloat();
        scaleY = in.readFloat();
        orientation = in.readInt();
        visiblePages = in.readInt();
        pagerId = in.readInt();
    }

    public static final Parcelable.Creator<CarouselConfig> CREATOR =
            new Parcelable.Creator<CarouselConfig>() {
                @Override
                public CarouselConfig createFromParcel(Parcel in) {
                    return new CarouselConfig(in);
                }

                @Override
                public CarouselConfig[] newArray(int size) {
                    return new CarouselConfig[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeFloat(scaleX);
        out.writeFloat(scaleY);
        out.writeInt(orientation);
        out.writeInt(visiblePages);
        out.writeInt(pagerId);
    }

    public String getPageFragmentTag(int position) {
        return "android:switcher:" + pagerId + ":" + position;
    }

    @Override
    public String toString() {
        return "scaleX=" + scaleX + " scaleY=" + scaleY +
                " orientation=" + orientation + " visiblePages" + visiblePages +
                " pagerId=" + pagerId;
    }
}