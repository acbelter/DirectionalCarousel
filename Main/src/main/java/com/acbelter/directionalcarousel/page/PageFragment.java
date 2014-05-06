package com.acbelter.directionalcarousel.page;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acbelter.directionalcarousel.CarouselViewPager;
import com.acbelter.directionalcarousel.R;

public class PageFragment extends Fragment {
    public static Fragment newInstance(Context context, PageItem item, float scale) {
        Bundle args = new Bundle();
        args.putParcelable("item", item);
        args.putFloat("scale", scale);
        return Fragment.instantiate(context, PageFragment.class.getName(), args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.page, container, false);

        PageItem item = getArguments().getParcelable("item");
        float scale = getArguments().getFloat("scale");

        TextView title = (TextView) layout.findViewById(R.id.title);
        title.setText(item.getTitle());

        PageLayout root = (PageLayout) layout.findViewById(R.id.root);
        root.setScaleBoth(scale);

        if (CarouselViewPager.ORIENTATION == CarouselViewPager.VERTICAL) {
            layout.setScaleX(1.0f / CarouselViewPager.scaleY);
            layout.setScaleY(1.0f / CarouselViewPager.scaleX);
            layout.setRotation(-90);
        }

        return layout;
    }
}