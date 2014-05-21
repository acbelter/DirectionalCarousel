package com.acbelter.directionalcarouseldemo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;
import com.acbelter.directionalcarousel.CarouselPagerAdapter;
import com.acbelter.directionalcarousel.CarouselViewPager;
import com.acbelter.directionalcarousel.page.OnPageClickListener;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements OnPageClickListener<MyPageItem> {
    private CarouselPagerAdapter<MyPageItem> mPagerAdapter;
    private CarouselViewPager mViewPager;
    private ArrayList<MyPageItem> mItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            int size = 20;
            mItems = new ArrayList<MyPageItem>(size);
            for (int i = 0; i < size; i++) {
                mItems.add(new MyPageItem("Item " + i));
            }
        } else {
            mItems = savedInstanceState.getParcelableArrayList("items");
        }

        mViewPager = (CarouselViewPager) findViewById(R.id.carousel_pager);
        mPagerAdapter = new CarouselPagerAdapter<MyPageItem>(getSupportFragmentManager(),
                MyPageFragment.class, R.layout.page_layout, mItems);
        mPagerAdapter.setOnPageClickListener(this);

        mViewPager.setAdapter(mPagerAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("items", mItems);
    }

    @Override
    public void onSingleTap(View view, MyPageItem item) {
        Toast.makeText(getApplicationContext(), "Single tap: " + item.getTitle(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDoubleTap(View view, MyPageItem item) {
        Toast.makeText(getApplicationContext(), "Double tap: " + item.getTitle(),
                Toast.LENGTH_SHORT).show();
    }
}
