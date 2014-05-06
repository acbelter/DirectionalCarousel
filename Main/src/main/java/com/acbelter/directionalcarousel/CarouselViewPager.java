package com.acbelter.directionalcarousel;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;

public class CarouselViewPager extends ViewPager {
    private static final String TAG = "CarouselViewPager";
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    private int mViewPagerWidth;
    private int mViewPagerHeight;

    public static float scaleX = 1.0f;
    public static float scaleY = 1.0f;
    public static int ORIENTATION = HORIZONTAL;

    private int mMinOffset = 20;
    private float mVisiblePart = 0.5f;
    // Offsets between two contents
    private int mOffset;

    private int mPageLimit;
    private int mPageMargin;

    private Resources mResources;

    public CarouselViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mResources = context.getResources();

        DisplayMetrics metrics = mResources.getDisplayMetrics();
        mMinOffset *= metrics.density;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CarouselViewPager);
        try {
            if (a != null) {
                // 0 - horizontal, 1 - vertical
                ORIENTATION = a.getInt(R.styleable.CarouselViewPager_android_orientation,
                        HORIZONTAL);
            }
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mViewPagerWidth = MeasureSpec.getSize(widthMeasureSpec);
        mViewPagerHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (ORIENTATION == VERTICAL) {
            setRotation(90);
            scaleX = (float) mViewPagerHeight / mViewPagerWidth;
            scaleY = (float) mViewPagerWidth / mViewPagerHeight;
            setScaleX(scaleX);
            setScaleY(scaleY);
        }

        setMeasuredDimension(mViewPagerWidth, mViewPagerHeight);

        try {
            calculatePageLimitAndMargin();
        } catch (CarouselConfigException e) {
            Log.e(TAG, e.toString());
        }

        setOffscreenPageLimit(mPageLimit);
        setPageMargin(mPageMargin);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void calculatePageLimitAndMargin() throws CarouselConfigException {
        if (ORIENTATION != HORIZONTAL && ORIENTATION != VERTICAL) {
            throw new IllegalArgumentException("Invalid orientation.");
        }

        int contentSize, viewSize;
        if (ORIENTATION == HORIZONTAL) {
            contentSize = mResources.getDimensionPixelSize(R.dimen.content_width);
            viewSize = mViewPagerWidth;
        } else {
            contentSize = mResources.getDimensionPixelSize(R.dimen.content_height);
            viewSize = mViewPagerHeight;
        }

        int minOffset = (int) (CarouselPagerAdapter.DIFF_SCALE * contentSize / 2) + mMinOffset;
        contentSize *= CarouselPagerAdapter.SMALL_SCALE;

        if (contentSize + 2*minOffset > viewSize) {
            throw new CarouselConfigException("Page content is too large.");
        }

        final float step = 0.1f;
        while (contentSize + 2*contentSize*(mVisiblePart-step) + 2*minOffset > viewSize
                && Math.abs(mVisiblePart-step) > 1e-6) {
            mVisiblePart -= 0.1f;
        }

        int fullPages = 0;
        final int s = viewSize - (int) (2*contentSize*mVisiblePart);
        while (minOffset + (fullPages+1)*(contentSize + minOffset) <= s) {
            fullPages++;
        }

        if (fullPages != 0 && fullPages%2 == 0) {
            fullPages--;
        }

        mOffset = (s - fullPages*contentSize) / (fullPages+1);
        if (mOffset < minOffset) {
            mOffset = minOffset;
        }

        if (Math.abs(mVisiblePart) > 1e-6) {
            mPageLimit = (fullPages + 2) - 1;
        } else {
            mPageLimit = fullPages - 1;
        }

        if (ORIENTATION == VERTICAL) {
            mPageMargin = -(int) ((viewSize - contentSize - mOffset) * scaleY);
        } else {
            mPageMargin = -(viewSize - contentSize - mOffset);
        }
    }

    public static class CarouselConfigException extends Exception {
        public CarouselConfigException(String msg) {
            super(msg);
        }
    }
}
