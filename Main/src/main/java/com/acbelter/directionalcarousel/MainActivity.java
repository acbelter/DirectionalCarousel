package com.acbelter.directionalcarousel;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.acbelter.directionalcarousel.page.OnPageClickListener;
import com.acbelter.directionalcarousel.page.PageItem;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements OnPageClickListener {
    private CarouselPagerAdapter mPagerAdapter;
    private CarouselViewPager mViewPager;
    private Button mAddButton;
    private Button mDeleteButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAddButton = (Button) findViewById(R.id.add_button);
        mDeleteButton = (Button) findViewById(R.id.delete_button);

        final int size = 10;
        ArrayList<PageItem> items = new ArrayList<PageItem>(size);
        for (int i = 0; i < size; i++) {
            items.add(new PageItem("Item " + i));
        }

        mViewPager = (CarouselViewPager) findViewById(R.id.carousel_pager);
        mPagerAdapter = new CarouselPagerAdapter(getSupportFragmentManager(), this, items);

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(mPagerAdapter);
        mViewPager.setCurrentItem(mPagerAdapter.getFirstPosition());

        mAddButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mDeleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void onSingleTap(View view, PageItem item) {
        Toast.makeText(getApplicationContext(), "Single tap: " + item.getTitle(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDoubleTap(View view, PageItem item) {
        Toast.makeText(getApplicationContext(), "Double tap: " + item.getTitle(),
                Toast.LENGTH_SHORT).show();
    }
}
