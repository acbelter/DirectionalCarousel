package com.acbelter.directionalcarousel;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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

        return layout;
    }
}