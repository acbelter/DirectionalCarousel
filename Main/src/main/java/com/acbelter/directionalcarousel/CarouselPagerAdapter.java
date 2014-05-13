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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import com.acbelter.directionalcarousel.page.OnPageClickListener;
import com.acbelter.directionalcarousel.page.PageFragment;
import com.acbelter.directionalcarousel.page.PageItem;
import com.acbelter.directionalcarousel.page.PageLayout;

import java.util.ArrayList;

public class CarouselPagerAdapter extends FragmentPagerAdapter implements OnPageChangeListener {
    private CarouselConfig mConfig;
    private int mPagesCount;
    private int mFirstPosition;

    private FragmentManager mFragmentManager;
    private OnPageClickListener mCallback;
    private ArrayList<PageItem> mItems;
    private int mCurrentPosition;

    public CarouselPagerAdapter(FragmentManager fragmentManager,
                                OnPageClickListener callback,
                                ArrayList<PageItem> items) {
        super(fragmentManager);
        mConfig = CarouselConfig.getInstance();
        mFragmentManager = fragmentManager;
        mCallback = callback;
        if (items == null) {
            mItems = new ArrayList<PageItem>(0);
        } else {
            mItems = items;
        }
        mPagesCount = mItems.size();
        if (mConfig.infinite) {
            mFirstPosition = mPagesCount * CarouselConfig.LOOPS / 2;
        }
    }

    public void sendSingleTap(View view, PageItem item) {
        if (mCallback != null) {
            mCallback.onSingleTap(view, item);
        }
    }

    public void sendDoubleTap(View view, PageItem item) {
        if (mCallback != null) {
            mCallback.onDoubleTap(view, item);
        }
    }

    @Override
    public Fragment getItem(int position) {
        if (mConfig.infinite) {
            position = position % mPagesCount;
        }

        return PageFragment.newInstance(mItems.get(position));
    }

    @Override
    public int getCount() {
        if (mConfig.infinite) {
            return mPagesCount * CarouselConfig.LOOPS;
        }
        return mPagesCount;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        switch (mConfig.scrollScalingMode) {
            case CarouselConfig.SCROLL_MODE_BIG_CURRENT: {
                PageLayout current = getPageView(position);
                PageLayout next = getPageView(position + 1);

                if (current != null) {
                    current.setScaleBoth(mConfig.bigScale
                            - mConfig.getDiffScale() * positionOffset);
                }

                if (next != null) {
                    next.setScaleBoth(mConfig.smallScale
                            + mConfig.getDiffScale() * positionOffset);
                }
                break;
            }
            case CarouselConfig.SCROLL_MODE_BIG_ALL: {
                PageLayout current = getPageView(position);
                if (current != null) {
                    current.setScaleBoth(mConfig.bigScale);
                }

                if (positionOffset > 0.0f) {
                    scaleAdjacentPages(position, mConfig.pageLimit, mConfig.bigScale);
                }
                break;
            }
            case CarouselConfig.SCROLL_MODE_NONE: {
                break;
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPosition = position;
        int scalingPages = CarouselConfig.getInstance().pageLimit;
        if (scalingPages == 0) {
            return;
        }

        if (mConfig.scrollScalingMode == CarouselConfig.SCROLL_MODE_BIG_CURRENT) {
            // Fix fast scroll scaling bug
            scaleAdjacentPages(position, scalingPages, mConfig.smallScale);
        } else if (mConfig.scrollScalingMode == CarouselConfig.SCROLL_MODE_NONE) {
            scaleAdjacentPages(position, scalingPages, mConfig.bigScale);
        }
    }

    /**
     * @param position Position of the current page.
     * @param scalingPages The number of pages on both sides of the current page,
     *                     which must be scaled.
     * @param scale Scale value.
     */
    private void scaleAdjacentPages(int position, int scalingPages, float scale) {
        if (scalingPages == 0) {
            return;
        }

        for (int i = 0; i < scalingPages / 2; i++) {
            PageLayout prevSidePage = getPageView(position - (i + 1));
            if (prevSidePage != null) {
                prevSidePage.setScaleBoth(scale);
            }
            PageLayout nextSidePage = getPageView(position + (i + 1));
            if (nextSidePage != null) {
                nextSidePage.setScaleBoth(scale);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == CarouselViewPager.SCROLL_STATE_IDLE) {
            int scalingPages = CarouselConfig.getInstance().pageLimit;
            if (scalingPages == 0) {
                return;
            }

            if (mConfig.scrollScalingMode == CarouselConfig.SCROLL_MODE_BIG_ALL) {
                scaleAdjacentPages(mCurrentPosition, scalingPages, mConfig.smallScale);
            }
        }
    }

    public int getFirstPosition() {
        return mFirstPosition;
    }

    private PageLayout getPageView(int position) {
        String tag = CarouselConfig.getInstance().getPageFragmentTag(position);
        Fragment f = mFragmentManager.findFragmentByTag(tag);
        if (f != null && f.getView() != null) {
            return (PageLayout) f.getView().findViewById(R.id.page);
        }
        return null;
    }
}
