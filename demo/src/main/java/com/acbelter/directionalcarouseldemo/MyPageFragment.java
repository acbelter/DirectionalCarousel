package com.acbelter.directionalcarouseldemo;

import android.view.View;
import android.widget.TextView;
import com.acbelter.directionalcarousel.CarouselViewPager;
import com.acbelter.directionalcarousel.page.PageFragment;
import com.acbelter.directionalcarousel.page.PageLayout;

public class MyPageFragment extends PageFragment<MyPageItem> {
    @Override
    public void setupPage(CarouselViewPager container, PageLayout pageLayout, MyPageItem pageItem) {
        View pageContent = pageLayout.findViewById(R.id.page_content);
        pageContent.setOnTouchListener(container);
        pageContent.setTag(pageItem);

        TextView title = (TextView) pageContent.findViewById(R.id.title);
        title.setText(pageItem.getTitle());
    }
}
