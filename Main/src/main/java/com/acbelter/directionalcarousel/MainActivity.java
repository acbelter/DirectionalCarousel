package com.acbelter.directionalcarousel;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

public class MainActivity extends FragmentActivity {
    public final static int PAGES = 5;
    // You can choose a bigger number for LOOPS, but you know, nobody will fling
    // more than 10000 times just in order to test your "infinite" ViewPager :D
    public final static int LOOPS = 10000;
    public final static int FIRST_PAGE = PAGES * LOOPS / 2;
    public final static float BIG_SCALE = 1.0f;
    public final static float SMALL_SCALE = 0.7f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;

    private CarouselPagerAdapter mPagerAdapter;
    private ViewPager mHorizontalPager;
    private VerticalViewPager mVerticalPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPager();
    }

    private void initPager() {
        mPagerAdapter = new CarouselPagerAdapter(this, getSupportFragmentManager());

        mHorizontalPager = (ViewPager) findViewById(R.id.horizontal_pager);
        mVerticalPager = (VerticalViewPager) findViewById(R.id.vertical_pager);

        if (mHorizontalPager != null && mVerticalPager == null) {
            mHorizontalPager.setAdapter(mPagerAdapter);
            mHorizontalPager.setOnPageChangeListener(mPagerAdapter);


            // Set current item to the middle page so we can fling to both
            // directions left and right
            mHorizontalPager.setCurrentItem(FIRST_PAGE);

            // Necessary or the pager will only have one extra page to show
            // make this at least however many pages you can see
            mHorizontalPager.setOffscreenPageLimit(3);

            // Set margin for pages as a negative number, so a part of next and
            // previous pages will be showed
            mHorizontalPager.setPageMargin(-300);
        }

        if (mVerticalPager != null && mHorizontalPager == null) {
            mVerticalPager.setAdapter(mPagerAdapter);
            mVerticalPager.setOnPageChangeListener(mPagerAdapter);


            // Set current item to the middle page so we can fling to both
            // directions left and right
            mVerticalPager.setCurrentItem(FIRST_PAGE);

            // Necessary or the pager will only have one extra page to show
            // make this at least however many pages you can see
            mVerticalPager.setOffscreenPageLimit(3);

            // Set margin for pages as a negative number, so a part of next and
            // previous pages will be showed
            mVerticalPager.setPageMargin(-300);
        }
    }

    public ViewGroup getPager() {
        if (mHorizontalPager != null && mVerticalPager == null) {
            return mHorizontalPager;
        }
        if (mVerticalPager != null && mHorizontalPager == null) {
            return mVerticalPager;
        }
        return null;
    }
}
