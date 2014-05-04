package com.acbelter.directionalcarousel;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import java.util.ArrayList;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

public class MainActivity extends FragmentActivity {
    private static final int HORIZONTAL_PAGER = 0;
    private static final int VERTICAL_PAGER = 1;
    private static final int NULL_PAGER = -1;

    private CarouselPagerAdapter mPagerAdapter;
    private ViewPager mHorizontalPager;
    private VerticalViewPager mVerticalPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int size = 5;
        ArrayList<PageItem> items = new ArrayList<PageItem>(size);
        for (int i = 0; i < size; i++) {
            items.add(new PageItem("Item " + i));
        }

        mPagerAdapter = new CarouselPagerAdapter(this, getSupportFragmentManager(), items);

        if (savedInstanceState != null) {
            int pos = savedInstanceState.getInt("position");
            initPager(mPagerAdapter, pos);
        } else {
            initPager(mPagerAdapter, mPagerAdapter.getFirstPage());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (getPagerType() == HORIZONTAL_PAGER) {
            outState.putInt("position", mHorizontalPager.getCurrentItem());
        } else if (getPagerType() == VERTICAL_PAGER) {
            outState.putInt("position", mVerticalPager.getCurrentItem());
        } else {
            outState.putInt("position", mPagerAdapter.getFirstPage());
        }
    }

    private int getPagerType() {
        if (mHorizontalPager != null && mVerticalPager == null) {
            return HORIZONTAL_PAGER;
        }
        if (mVerticalPager != null && mHorizontalPager == null) {
            return VERTICAL_PAGER;
        }
        return NULL_PAGER;
    }

    private void initPager(CarouselPagerAdapter adapter, int savedPosition) {
        mHorizontalPager = (ViewPager) findViewById(R.id.horizontal_pager);
        mVerticalPager = (VerticalViewPager) findViewById(R.id.vertical_pager);

        if (getPagerType() == HORIZONTAL_PAGER) {
            mHorizontalPager.setAdapter(adapter);
            mHorizontalPager.setOnPageChangeListener(adapter);

            // Set current item to the middle page so we can fling to both
            // directions left and right
            mHorizontalPager.setCurrentItem(savedPosition);

            // Necessary or the pager will only have one extra page to show
            // make this at least however many pages you can see
            mHorizontalPager.setOffscreenPageLimit(3);

            // Set margin for pages as a negative number, so a part of next and
            // previous pages will be showed
            mHorizontalPager.setPageMargin(-300);
        } else if (getPagerType() == VERTICAL_PAGER) {
            mVerticalPager.setAdapter(adapter);
            mVerticalPager.setOnPageChangeListener(adapter);

            // Set current item to the middle page so we can fling to both
            // directions left and right
            mVerticalPager.setCurrentItem(savedPosition);

            // Necessary or the pager will only have one extra page to show
            // make this at least however many pages you can see
            mVerticalPager.setOffscreenPageLimit(3);

            // Set margin for pages as a negative number, so a part of next and
            // previous pages will be showed
            mVerticalPager.setPageMargin(-300);
        }
    }

    public ViewGroup getPager() {
        if (getPagerType() == HORIZONTAL_PAGER) {
            return mHorizontalPager;
        } else if (getPagerType() == VERTICAL_PAGER) {
            return mVerticalPager;
        }
        return null;
    }
}
