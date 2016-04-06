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
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class CarouselViewPager extends ViewPager implements OnTouchListener {
    public static final float DEFAULT_SIDE_PAGES_VISIBLE_PART = 0.5f;
    private static final String TAG = CarouselViewPager.class.getSimpleName();
    private static final boolean DEBUG = false;

    private int mViewPagerWidth;
    private int mViewPagerHeight;

    // Distance between pages always will be greater than this value (even when scaling)
    private int mMinPagesOffset;
    private float mSidePagesVisiblePart;
    private int mWrapPadding;

    private boolean mSizeChanged;
    private float gravityOffset;

    private Resources mResources;
    private CarouselConfig mConfig;

    private GestureDetector mGestureDetector;
    private OnGestureListener mGestureListener;
    private View mTouchedView;
    private Parcelable mTouchedItem;

    private final String mPackageName;
    private int mPageContentWidthId;
    private int mPageContentHeightId;

    public CarouselViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarouselViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);

        mConfig = CarouselConfig.getInstance();
        mConfig.pagerId = getId();
        mResources = context.getResources();
        mPackageName = context.getPackageName();
        mPageContentWidthId = mResources.getIdentifier("page_content_width", "dimen",
                mPackageName);
        mPageContentHeightId = mResources.getIdentifier("page_content_height", "dimen",
                mPackageName);

        DisplayMetrics dm = mResources.getDisplayMetrics();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CarouselViewPager,
                defStyle, 0);
        try {
            if (a != null) {
                mConfig.orientation = a.getInt(R.styleable.CarouselViewPager_android_orientation,
                        CarouselConfig.HORIZONTAL);
                mConfig.infinite =
                        a.getBoolean(R.styleable.CarouselViewPager_infinite, true);
                mConfig.scrollScalingMode =
                        a.getInt(R.styleable.CarouselViewPager_scrollScalingMode,
                                CarouselConfig.SCROLL_MODE_BIG_CURRENT);

                float bigScale = a.getFloat(R.styleable.CarouselViewPager_bigScale,
                        CarouselConfig.DEFAULT_BIG_SCALE);
                if (bigScale > 1.0f || bigScale < 0.0f) {
                    bigScale = CarouselConfig.DEFAULT_BIG_SCALE;
                    Log.w(TAG, "Invalid bigScale attribute. Default value " +
                            CarouselConfig.DEFAULT_BIG_SCALE + " will be used.");
                }
                mConfig.bigScale = bigScale;

                float smallScale = a.getFloat(R.styleable.CarouselViewPager_smallScale,
                        CarouselConfig.DEFAULT_SMALL_SCALE);
                if (smallScale > 1.0f || smallScale < 0.0f) {
                    smallScale = CarouselConfig.DEFAULT_SMALL_SCALE;
                    Log.w(TAG, "Invalid smallScale attribute. Default value " +
                            CarouselConfig.DEFAULT_SMALL_SCALE + " will be used.");
                } else if (smallScale > bigScale) {
                    smallScale = bigScale;
                    Log.w(TAG, "Invalid smallScale attribute. Value " + bigScale +
                            " will be used.");
                }
                mConfig.smallScale = smallScale;

                mMinPagesOffset = (int) a.getDimension(R.styleable.CarouselViewPager_minPagesOffset,
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, dm));
                mSidePagesVisiblePart =
                        a.getFloat(R.styleable.CarouselViewPager_sidePagesVisiblePart,
                                DEFAULT_SIDE_PAGES_VISIBLE_PART);
                mWrapPadding = (int) a.getDimension(R.styleable.CarouselViewPager_wrapPadding,
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, dm));
            }
        } finally {
            if (a != null) {
                a.recycle();
            }
        }

        mGestureListener = new SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (getCarouselAdapter() != null) {
                    getCarouselAdapter().sendSingleTap(mTouchedView, mTouchedItem);
                }
                mTouchedView = null;
                mTouchedItem = null;
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (getCarouselAdapter() != null) {
                    getCarouselAdapter().sendDoubleTap(mTouchedView, mTouchedItem);
                }
                mTouchedView = null;
                mTouchedItem = null;
                return true;
            }
        };

        mGestureDetector = new GestureDetector(context, mGestureListener);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        if (adapter == null) {
            return;
        }

        if (adapter.getClass() != CarouselPagerAdapter.class) {
            throw new ClassCastException("Adapter must be instance of CarouselPagerAdapter class.");
        }

        setOnPageChangeListener((OnPageChangeListener) adapter);
        setCurrentItem(((CarouselPagerAdapter) adapter).getFirstPosition());
    }

    private CarouselPagerAdapter getCarouselAdapter() {
        PagerAdapter adapter = getAdapter();
        if (adapter == null) {
            return null;
        }
        return (CarouselPagerAdapter) adapter;
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
            int itemsCount = getAdapter().getCount();
            if (itemsCount == 0) {
                return;
            }

            int offset = (ss.position - ss.itemsCount / 2) % itemsCount;
            if (offset >= 0) {
                setCurrentItem(offset);
            } else {
                setCurrentItem(ss.itemsCount / CarouselConfig.LOOPS + offset);
            }
        } else if (!ss.infinite && mConfig.infinite) {
            setCurrentItem(ss.itemsCount * CarouselConfig.LOOPS / 2 + ss.position);
        } else {
            setCurrentItem(ss.position);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent e) {
        mTouchedView = view;
        mTouchedItem = (Parcelable) view.getTag();
        return mGestureDetector.onTouchEvent(e);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mSizeChanged = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (DEBUG) {
            Log.d(TAG, "w=" + width + " h=" + height);
            Log.d(TAG, "wMode=" + getModeDescription(widthMode) +
                    " hMode=" + getModeDescription(heightMode));
        }

        // FIXME Supported only match_parent and wrap_content attributes
        if (mConfig.orientation == CarouselConfig.VERTICAL) {
            int pageContentWidth = getPageContentWidth();
            int newWidth = width;
            if (widthMode == MeasureSpec.AT_MOST ||
                    pageContentWidth + 2 * mWrapPadding > width) {

                newWidth = pageContentWidth + 2 * mWrapPadding;
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(newWidth, widthMode);
            }

            ViewGroup.LayoutParams lp = getLayoutParams();
            // FIXME Supported only FrameLayout as parent
            if (lp instanceof FrameLayout.LayoutParams) {
                if (!parentHasExactDimensions()) {
                    throw new UnsupportedOperationException("Parent layout should have exact " +
                            "dimensions.");
                }

                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) lp;
                if (!mSizeChanged) {
                    gravityOffset = 0.0f;
                    int hGrav = params.gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
                    if (hGrav == Gravity.CENTER_HORIZONTAL || hGrav == Gravity.CENTER) {
                        gravityOffset = (width - newWidth) * 0.5f;
                    }
                    if (hGrav == Gravity.RIGHT) {
                        gravityOffset = width - newWidth;
                    }
                }

                setRotation(90);
                setTranslationX((newWidth - height) * 0.5f + gravityOffset);
                setTranslationY(-(newWidth - height) * 0.5f);
                params.gravity = Gravity.NO_GRAVITY;
                setLayoutParams(params);
            } else {
                throw new UnsupportedOperationException("Parent layout should be instance of " +
                        "FrameLayout.");
            }

            mSizeChanged = true;
            super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        } else {
            int pageContentHeight = getPageContentHeight();
            if (heightMode == MeasureSpec.AT_MOST ||
                    pageContentHeight + 2 * mWrapPadding > height) {

                int newHeight = pageContentHeight + 2 * mWrapPadding;
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(newHeight, heightMode);
            }

            // FIXME Supported only FrameLayout as parent
            if (!(getLayoutParams() instanceof FrameLayout.LayoutParams)) {
                throw new UnsupportedOperationException("Parent layout should be instance of " +
                        "FrameLayout.");
            } else {
                if (!parentHasExactDimensions()) {
                    throw new UnsupportedOperationException("Parent layout should have exact " +
                            "dimensions.");
                }
            }

            mSizeChanged = true;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        mViewPagerWidth = getMeasuredWidth();
        mViewPagerHeight = getMeasuredHeight();

        if (calculatePageLimitAndMargin()) {
            setOffscreenPageLimit(mConfig.pageLimit);
            setPageMargin(mConfig.pageMargin);
        }

        if (DEBUG) {
            Log.d(TAG, mConfig.toString());
        }
    }

    private boolean parentHasExactDimensions() {
        ViewGroup.LayoutParams params = ((ViewGroup) getParent()).getLayoutParams();
        return !(params.width == ViewGroup.LayoutParams.WRAP_CONTENT ||
                params.height == ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private String getModeDescription(int mode) {
        if (mode == MeasureSpec.AT_MOST) {
            return "AT_MOST";
        }

        if (mode == MeasureSpec.EXACTLY) {
            return "EXACTLY";
        }

        if (mode == MeasureSpec.UNSPECIFIED) {
            return "UNSPECIFIED";
        }

        return "UNKNOWN";
    }

    private int getPageContentWidth() {
        return mResources.getDimensionPixelSize(mPageContentWidthId);
    }

    private int getPageContentHeight() {
        return mResources.getDimensionPixelSize(mPageContentHeightId);
    }

    /**
     * @return True, if config was successfully updated.
     */
    private boolean calculatePageLimitAndMargin() {
        if (mViewPagerWidth == 0 || mViewPagerHeight == 0) {
            return false;
        }

        int contentSize;
        if (mConfig.orientation == CarouselConfig.HORIZONTAL) {
            contentSize = getPageContentWidth();
        } else {
            contentSize = getPageContentHeight();
        }

        int viewSize = mViewPagerWidth;
        int minOffset = 0;
        switch (mConfig.scrollScalingMode) {
            case CarouselConfig.SCROLL_MODE_BIG_CURRENT: {
                minOffset = (int) (mConfig.getDiffScale() * contentSize / 2) + mMinPagesOffset;
                contentSize *= mConfig.smallScale;
                break;
            }
            case CarouselConfig.SCROLL_MODE_BIG_ALL: {
                minOffset = (int) (mConfig.getDiffScale() * contentSize) + mMinPagesOffset;
                contentSize *= mConfig.smallScale;
                break;
            }
            case CarouselConfig.SCROLL_MODE_NONE: {
                minOffset = mMinPagesOffset;
                break;
            }
        }

        if (contentSize + 2 * minOffset > viewSize) {
            if (DEBUG) {
                Log.d(TAG, "Page content is too large.");
            }
            return false;
        }

        while (true) {
            if (mSidePagesVisiblePart < 0.0f) {
                mSidePagesVisiblePart = 0.0f;
                break;
            }
            if (contentSize + 2 * contentSize * (mSidePagesVisiblePart) + 2 * minOffset <= viewSize) {
                break;
            }
            mSidePagesVisiblePart -= 0.1f;
        }

        int fullPages = 1;
        final int s = viewSize - (int) (2 * contentSize * mSidePagesVisiblePart);

        while (minOffset + (fullPages + 1) * (contentSize + minOffset) <= s) {
            fullPages++;
        }

        if (fullPages != 0 && fullPages % 2 == 0) {
            fullPages--;
        }

        int offset = (s - fullPages * contentSize) / (fullPages + 1);
        int pageLimit;
        if (Math.abs(mSidePagesVisiblePart) > 1e-6) {
            pageLimit = (fullPages + 2) - 1;
        } else {
            if (fullPages > 1) {
                pageLimit = fullPages - 1;
            } else {
                pageLimit = 1;
            }
        }
        // Reserve pages for correct scrolling
        pageLimit = 2 * pageLimit + pageLimit / 2;
        mConfig.pageLimit = pageLimit;

        mConfig.pageMargin = -(viewSize - contentSize - offset);
        return true;
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
