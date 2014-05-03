package com.acbelter.directionalcarousel;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class CarouselPagerAdapter extends FragmentPagerAdapter implements
        ViewPager.OnPageChangeListener {
    private MainActivity mContext;
    private FragmentManager mFragmentManager;

    public CarouselPagerAdapter(MainActivity context, FragmentManager fragmentManager) {
        super(fragmentManager);
        mFragmentManager = fragmentManager;
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // Make the first pager bigger than others
        float scale;
        if (position == MainActivity.FIRST_PAGE) {
            scale = MainActivity.BIG_SCALE;
        } else {
            scale = MainActivity.SMALL_SCALE;
        }

        position = position % MainActivity.PAGES;
        return PageFragment.newInstance(mContext, position, scale);
    }

    @Override
    public int getCount() {
        return MainActivity.PAGES * MainActivity.LOOPS;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        if (positionOffset >= 0.0f && positionOffset <= 1.0f) {
            PageLayout current = getRootView(position);
            PageLayout mNext = getRootView(position + 1);

            current.setScaleBoth(MainActivity.BIG_SCALE
                    - MainActivity.DIFF_SCALE * positionOffset);
            mNext.setScaleBoth(MainActivity.SMALL_SCALE
                    + MainActivity.DIFF_SCALE * positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private PageLayout getRootView(int position) {
        Fragment f = mFragmentManager.findFragmentByTag(getFragmentTag(position));
        return (PageLayout) f.getView().findViewById(R.id.root);
    }

    private String getFragmentTag(int position) {
        return "android:switcher:" + mContext.getPager().getId() + ":" + position;
    }
}
