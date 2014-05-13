/*
 * Copyright 2014 acbelter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acbelter.directionalcarousel.page;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.acbelter.directionalcarousel.CarouselConfig;
import com.acbelter.directionalcarousel.CarouselViewPager;
import com.acbelter.directionalcarousel.R;

public class PageFragment extends Fragment {

    public static PageFragment newInstance(PageItem item) {
        PageFragment pf = new PageFragment();
        Bundle args = new Bundle();
        args.putParcelable("item", item);
        pf.setArguments(args);
        return pf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        if (container.getClass() != CarouselViewPager.class) {
            throw new IllegalArgumentException("PageFragment must be attached to " +
                    "CarouselViewPager.");
        }

        PageLayout pageLayout = (PageLayout) inflater.inflate(R.layout.page, container, false);
        if (pageLayout == null) {
            throw new IllegalStateException("PageFragment root layout must have id " +
                    "R.id.page.");
        }
        PageItem item = getArguments().getParcelable("item");

        CarouselConfig config = CarouselConfig.getInstance();
        float scale;
        if (config.scrollScalingMode == CarouselConfig.SCROLL_MODE_BIG_CURRENT ||
                config.scrollScalingMode == CarouselConfig.SCROLL_MODE_BIG_ALL) {
            scale = config.smallScale;
        } else {
            scale = config.bigScale;
        }
        pageLayout.setScaleBoth(scale);

        TextView title = (TextView) pageLayout.findViewById(R.id.title);
        title.setText(item.getTitle());

        if (config.orientation == CarouselConfig.VERTICAL) {
            pageLayout.setScaleX(1.0f / config.scaleY);
            pageLayout.setScaleY(1.0f / config.scaleX);
            pageLayout.setRotation(-90);
        }

        View pageContent = pageLayout.findViewById(R.id.page_content);
        if (pageContent == null) {
            throw new IllegalStateException("PageFragment layout must contains " +
                    "layout with id R.id.page_content.");
        }

        pageContent.setOnTouchListener((CarouselViewPager) container);
        pageContent.setTag(item);
        return pageLayout;
    }
}