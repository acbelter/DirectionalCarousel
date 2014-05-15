package com.acbelter.directionalcarouseldemo;

import android.view.View;
import android.widget.TextView;
import com.acbelter.directionalcarousel.page.PageFragment;
import com.acbelter.directionalcarousel.page.PageLayout;

public class MyPageFragment extends PageFragment<MyPageItem> {
    @Override
    public View setupPage(PageLayout pageLayout, MyPageItem pageItem) {
        View pageContent = pageLayout.findViewById(R.id.page_content);
        TextView title = (TextView) pageContent.findViewById(R.id.title);
        title.setText(pageItem.getTitle());
        return pageContent;
    }
}
