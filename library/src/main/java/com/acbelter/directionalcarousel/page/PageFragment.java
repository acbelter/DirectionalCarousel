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
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.acbelter.directionalcarousel.CarouselConfig;
import com.acbelter.directionalcarousel.CarouselViewPager;

public abstract class PageFragment<T extends Parcelable> extends Fragment {
    /**
     * @param pageLayout Layout of page.
     * @param pageItem Item for customize page content view.
     * @return View of page content.
     */
    public abstract View setupPage(PageLayout pageLayout, T pageItem);

    public static Bundle createArgs(int pageLayoutId, Parcelable item) {
        Bundle args = new Bundle();
        args.putInt("page_layout_id", pageLayoutId);
        args.putParcelable("item", item);
        return args;
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

        int pageLayoutId = getArguments().getInt("page_layout_id");
        PageLayout pageLayout = (PageLayout) inflater.inflate(pageLayoutId, container, false);
        if (pageLayout == null) {
            throw new IllegalStateException("PageFragment root layout must have id " +
                    "R.id.page.");
        }

        CarouselConfig config = CarouselConfig.getInstance();
        float scale;
        if (config.scrollScalingMode == CarouselConfig.SCROLL_MODE_BIG_CURRENT ||
                config.scrollScalingMode == CarouselConfig.SCROLL_MODE_BIG_ALL) {
            scale = config.smallScale;
        } else {
            scale = config.bigScale;
        }
        pageLayout.setScaleBoth(scale);

        if (config.orientation == CarouselConfig.VERTICAL) {
            pageLayout.setRotation(-90);
        }

        T item = getArguments().getParcelable("item");
        View pageContent = setupPage(pageLayout, item);
        if (pageContent != null) {
            pageContent.setOnTouchListener((CarouselViewPager) container);
            pageContent.setTag(item);
        }
        return pageLayout;
    }
}