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
        // Make the first pager bigger than others
        float scale = (position == mFirstPage) ?
                CarouselConfig.BIG_SCALE : CarouselConfig.SMALL_SCALE;
        position = position % mPages;
        return PageFragment.newInstance(mItems.get(position), scale);
    }

    @Override
    public int getCount() {
        return mPages * CarouselConfig.LOOPS;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        if (positionOffset >= 0.0f && positionOffset <= 1.0f) {
//            int n = CarouselViewPager.getConfig().visiblePages;
//            if (n != 0) {
//                n--;
//            }

            // FIXME Fix scaling while scrolling
            PageLayout current = getRootView(position);
            current.setScaleBoth(CarouselConfig.BIG_SCALE -
                    CarouselConfig.DIFF_SCALE * positionOffset);

//            ArrayList<PageLayout> neighbors = new ArrayList<PageLayout>(n/2);
//            for (int i = 0; i < n/2; i++) {
//                neighbors.add(getRootView(position + i));
//                neighbors.add(getRootView(position - i));
//            }
            PageLayout next = getRootView(position + 1);
            PageLayout prev = getRootView(position - 1);

            current.setScaleBoth(CarouselConfig.BIG_SCALE -
                    CarouselConfig.DIFF_SCALE * positionOffset);
            if (next != null) {
                next.setScaleBoth(CarouselConfig.SMALL_SCALE +
                        CarouselConfig.DIFF_SCALE * positionOffset);
            }
            if (prev != null) {
                prev.setScaleBoth(CarouselConfig.SMALL_SCALE +
                        CarouselConfig.DIFF_SCALE * positionOffset);
            }

//            for (int i = 0; i < neighbors.size(); i++) {
//                if (neighbors.get(i) != null) {
//                    neighbors.get(i).setScaleBoth(CarouselConfig.SMALL_SCALE +
//                            CarouselConfig.DIFF_SCALE * positionOffset);
//                }
//            }
        }
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public int getFirstPage() {
        return mFirstPage;
    }

    private PageLayout getRootView(int position) {
        String tag = CarouselViewPager.getConfig().getPageFragmentTag(position);
        Fragment f = mFragmentManager.findFragmentByTag(tag);
        if (f != null && f.getView() != null) {
            return (PageLayout) f.getView().findViewById(R.id.root);
        }
        return null;
    }
}
