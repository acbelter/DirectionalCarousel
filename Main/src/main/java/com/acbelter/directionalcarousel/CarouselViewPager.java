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

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

public class CarouselViewPager extends ViewPager {
    private static final boolean DEBUG = false;
    private static final String TAG = "CarouselViewPager";

    private int mViewPagerWidth;
    private int mViewPagerHeight;

    private int mMinOffset = 20;
    private float mVisiblePart = 0.5f;

    private int mPageLimit;
    private int mPageMargin;

    private Resources mResources;
    private CarouselConfig mConfig;

    public CarouselViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mConfig = CarouselConfig.getInstance();
        mConfig.pagerId = getId();
        mResources = context.getResources();

        DisplayMetrics metrics = mResources.getDisplayMetrics();
        mMinOffset *= metrics.density;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CarouselViewPager);
        try {
            if (a != null) {
                mConfig.orientation = a.getInt(R.styleable.CarouselViewPager_android_orientation,
                        CarouselConfig.HORIZONTAL);
                mConfig.infinite =
                        a.getBoolean(R.styleable.CarouselViewPager_infinite, true);
                mConfig.scrollScaling =
                        a.getBoolean(R.styleable.CarouselViewPager_scrollScaling, true);
            }
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        CarouselState ss = new CarouselState(super.onSaveInstanceState());
        ss.position = getCurrentItem();

        PagerAdapter adapter = getAdapter();
        if (adapter == null) {
            ss.itemsCount = 0;
        } else {
            ss.itemsCount = adapter.getCount();
        }
        ss.infinite = mConfig.infinite;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof CarouselState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        CarouselState ss = (CarouselState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        if (getAdapter() == null) {
            return;
        }

        if (ss.infinite && !mConfig.infinite) {
            int offset = ss.position - ss.itemsCount / 2;
            if (offset > 0) {
                setCurrentItem(offset);
            } else if (offset < 0) {
                setCurrentItem(ss.itemsCount / CarouselConfig.LOOPS + offset);
            }
        } else if (!ss.infinite && mConfig.infinite) {
            setCurrentItem(ss.itemsCount * CarouselConfig.LOOPS / 2 + ss.position);
        } else {
            setCurrentItem(ss.position);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mViewPagerWidth = MeasureSpec.getSize(widthMeasureSpec);
        mViewPagerHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (mConfig.orientation == CarouselConfig.VERTICAL) {
            setRotation(90);
            mConfig.scaleX = (float) mViewPagerHeight / mViewPagerWidth;
            mConfig.scaleY = (float) mViewPagerWidth / mViewPagerHeight;
            setScaleX(mConfig.scaleX);
            setScaleY(mConfig.scaleY);
        }

        setMeasuredDimension(mViewPagerWidth, mViewPagerHeight);

        calculatePageLimitAndMargin();

        setOffscreenPageLimit(mPageLimit);
        setPageMargin(mPageMargin);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (DEBUG) {
            Log.d(TAG, mConfig.toString());
        }
    }

    private void calculatePageLimitAndMargin() {
        if (mConfig.orientation != CarouselConfig.HORIZONTAL
                && mConfig.orientation != CarouselConfig.VERTICAL) {
            throw new IllegalArgumentException("Invalid orientation value.");
        }

        int contentSize, viewSize;
        if (mConfig.orientation == CarouselConfig.HORIZONTAL) {
            contentSize = mResources.getDimensionPixelSize(R.dimen.page_content_width);
            viewSize = mViewPagerWidth;
        } else {
            contentSize = mResources.getDimensionPixelSize(R.dimen.page_content_height);
            viewSize = mViewPagerHeight;
        }

        int minOffset;
        if (mConfig.scrollScaling) {
            minOffset = (int) (CarouselConfig.DIFF_SCALE * contentSize / 2) + mMinOffset;
            contentSize *= CarouselConfig.SMALL_SCALE;
        } else {
            minOffset = mMinOffset;
        }

        if (contentSize + 2*minOffset > viewSize) {
            Log.w(TAG, "Page content is too large.");
            return;
        }

        final float step = 0.1f;
        while (contentSize + 2*contentSize*(mVisiblePart-step) + 2*minOffset > viewSize
                && Math.abs(mVisiblePart-step) > 1e-6) {
            mVisiblePart -= step;
        }

        int fullPages = 0;
        final int s = viewSize - (int) (2*contentSize*mVisiblePart);
        while (minOffset + (fullPages+1)*(contentSize + minOffset) <= s) {
            fullPages++;
        }

        if (fullPages != 0 && fullPages%2 == 0) {
            fullPages--;
        }

        int offset = (s - fullPages * contentSize) / (fullPages + 1);

        if (Math.abs(mVisiblePart) > 1e-6) {
            mPageLimit = (fullPages + 2) - 1;
        } else {
            mPageLimit = fullPages - 1;
        }
        // Reserve pages for correct scrolling
        mPageLimit *= 2;
        mConfig.pageLimit = mPageLimit;

        if (mConfig.orientation == CarouselConfig.VERTICAL) {
            mPageMargin = -(int) ((viewSize - contentSize - offset) * mConfig.scaleY);
        } else {
            mPageMargin = -(viewSize - contentSize - offset);
        }
        mConfig.pageMargin = mPageMargin;
    }

    public static class CarouselState extends BaseSavedState {
        int position;
        int itemsCount;
        boolean infinite;

        public CarouselState(Parcelable superState) {
            super(superState);
        }

        private CarouselState(Parcel in) {
            super(in);
            position = in.readInt();
            itemsCount = in.readInt();
            infinite = in.readInt() == 1;
        }

        public static final Parcelable.Creator<CarouselState> CREATOR =
                new Parcelable.Creator<CarouselState>() {
            @Override
            public CarouselState createFromParcel(Parcel in) {
                return new CarouselState(in);
            }

            @Override
            public CarouselState[] newArray(int size) {
                return new CarouselState[0];
            }
        };

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(position);
            out.writeInt(itemsCount);
            out.writeInt(infinite ? 1 : 0);
        }
    }
}
