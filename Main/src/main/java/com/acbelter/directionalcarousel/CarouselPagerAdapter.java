package com.acbelter.directionalcarousel;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import com.acbelter.directionalcarousel.page.PageFragment;
import com.acbelter.directionalcarousel.page.PageItem;
import com.acbelter.directionalcarousel.page.PageLayout;

import java.util.ArrayList;

public class CarouselPagerAdapter extends FragmentPagerAdapter implements
        ViewPager.OnPageChangeListener {
    private int mPages;
    private int mFirstPage;

    private FragmentManager mFragmentManager;
    private ArrayList<PageItem> mItems;

    public CarouselPagerAdapter(FragmentManager fragmentManager,
                                ArrayList<PageItem> items) {
        super(fragmentManager);
        mFragmentManager = fragmentManager;
        if (items == null) {
            mItems = new ArrayList<PageItem>(0);
        } else {
            mItems = items;
        }
        mPages = mItems.size();
        mFirstPage = mPages * CarouselConfig.LOOPS / 2;
    }

    @Override
    public Fragment getItem(int position) {
        position = position % mPages;
        return PageFragment.newInstance(mItems.get(position), CarouselConfig.SMALL_SCALE);
    }

    @Override
    public int getCount() {
        return mPages * CarouselConfig.LOOPS;
    }

    @Override
    public void onPageScrolled(final int position, final float positionOffset,
                               int positionOffsetPixels) {
        PageLayout current = getRootView(position);
        PageLayout next = getRootView(position + 1);

        if (current != null) {
            current.setScaleBoth(CarouselConfig.BIG_SCALE
                    - CarouselConfig.DIFF_SCALE * positionOffset);
        }

        if (next != null) {
            next.setScaleBoth(CarouselConfig.SMALL_SCALE
                    + CarouselConfig.DIFF_SCALE * positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {
        // Fix fast scroll scaling bug
        int scalingPages = CarouselConfig.getInstance().pageLimit;
        if (scalingPages == 0) {
            return;
        } else {
            scalingPages--;
        }

        if (scalingPages > 2) {
            int oneSidePages = (scalingPages - 2) / 2;
            for (int i = 0; i < oneSidePages; i++) {
                PageLayout prevSidePage = getRootView(position - 1 - (i + 1));
                if (prevSidePage != null) {
                    prevSidePage.setScaleBoth(CarouselConfig.SMALL_SCALE);
                }
                PageLayout nextSidePage = getRootView(position + 1 + (i + 1));
                if (nextSidePage != null) {
                    nextSidePage.setScaleBoth(CarouselConfig.SMALL_SCALE);
                }
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public int getFirstPage() {
        return mFirstPage;
    }

    private PageLayout getRootView(int position) {
        String tag = CarouselConfig.getInstance().getPageFragmentTag(position);
        Fragment f = mFragmentManager.findFragmentByTag(tag);
        if (f != null && f.getView() != null) {
            return (PageLayout) f.getView().findViewById(R.id.root);
        }
        return null;
    }
}
