package com.acbelter.directionalcarousel;

import android.content.Context;
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
    // You can choose a bigger number for LOOPS, but you know, nobody will fling
    // more than 10000 times just in order to test your "infinite" ViewPager :D
    private static final int LOOPS = 10000;
    public static final float BIG_SCALE = 1.0f;
    public static final float SMALL_SCALE = 0.7f;
    //public static final float SMALL_SCALE = 1.0f;
    public static final float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;

    private int mPages;
    private int mFirstPage;

    private Context mContext;
    private FragmentManager mFragmentManager;
    private ArrayList<PageItem> mItems;
    private int mPagerId;

    public CarouselPagerAdapter(Context context,
                                FragmentManager fragmentManager,
                                int pagerId,
                                ArrayList<PageItem> items) {
        super(fragmentManager);
        mContext = context;
        mFragmentManager = fragmentManager;
        mPagerId = pagerId;
        if (items == null) {
            mItems = new ArrayList<PageItem>(0);
        } else {
            mItems = items;
        }
        mPages = mItems.size();
        mFirstPage = mPages * LOOPS / 2;
    }

    @Override
    public Fragment getItem(int position) {
        // Make the first pager bigger than others
        float scale = (position == mFirstPage) ? BIG_SCALE : SMALL_SCALE;
        position = position % mPages;
        return PageFragment.newInstance(mContext, mItems.get(position), scale);
    }

    @Override
    public int getCount() {
        return mPages * LOOPS;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        if (positionOffset >= 0.0f && positionOffset <= 1.0f) {
            PageLayout current = getRootView(position);
            PageLayout next = getRootView(position + 1);
            PageLayout prev = getRootView(position - 1);

            current.setScaleBoth(BIG_SCALE - DIFF_SCALE * positionOffset);
            if (next != null) {
                next.setScaleBoth(SMALL_SCALE + DIFF_SCALE * positionOffset);
            }
            if (prev != null) {
                prev.setScaleBoth(SMALL_SCALE + DIFF_SCALE * positionOffset);
            }
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
        Fragment f = mFragmentManager.findFragmentByTag(getFragmentTag(position));
        if (f != null && f.getView() != null) {
            return (PageLayout) f.getView().findViewById(R.id.root);
        }
        return null;
    }

    private String getFragmentTag(int position) {
        return "android:switcher:" + mPagerId + ":" + position;
    }
}
