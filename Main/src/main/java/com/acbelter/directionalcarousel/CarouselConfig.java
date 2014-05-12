/*
 * Copyright 2014 acbelter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acbelter.directionalcarousel;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

public class CarouselConfig implements Parcelable {
    public static final int LOOPS = 1000;
    public static final float DEFAULT_BIG_SCALE = 1.0f;
    public static final float DEFAULT_SMALL_SCALE = 0.7f;

    public float bigScale = DEFAULT_BIG_SCALE;
    public float smallScale = DEFAULT_SMALL_SCALE;

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    public float scaleX = 1.0f;
    public float scaleY = 1.0f;

    public int orientation = CarouselConfig.HORIZONTAL;
    public boolean infinite = true;
    public boolean scrollScaling = true;

    public int pageMargin = 0;
    public int pageLimit = 0;
    public int pagerId = View.NO_ID;

    private static CarouselConfig sConfig;

    public static CarouselConfig getInstance() {
        if (sConfig == null) {
            sConfig = new CarouselConfig();
        }
        return sConfig;
    }

    private CarouselConfig() {}

    private CarouselConfig(Parcel in) {
        scaleX = in.readFloat();
        scaleY = in.readFloat();
        orientation = in.readInt();
        infinite = in.readInt() == 1;
        scrollScaling = in.readInt() == 1;
        pageMargin = in.readInt();
        pageLimit = in.readInt();
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
        out.writeInt(infinite ? 1 : 0);
        out.writeInt(scrollScaling ? 1 : 0);
        out.writeInt(pageMargin);
        out.writeInt(pageLimit);
        out.writeInt(pagerId);
    }

    public float getDiffScale() {
        return bigScale - smallScale;
    }

    public String getPageFragmentTag(int position) {
        return "android:switcher:" + pagerId + ":" + position;
    }

    @Override
    public String toString() {
        return "CarouselConfig{" +
                "bigScale=" + bigScale +
                ", smallScale=" + smallScale +
                ", scaleX=" + scaleX +
                ", scaleY=" + scaleY +
                ", orientation=" + orientation +
                ", infinite=" + infinite +
                ", scrollScaling=" + scrollScaling +
                ", pageMargin=" + pageMargin +
                ", pageLimit=" + pageLimit +
                ", pagerId=" + pagerId +
                '}';
    }
}