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
import android.widget.LinearLayout;
import android.widget.TextView;
import com.acbelter.directionalcarousel.CarouselConfig;
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

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.page, container, false);
        PageItem item = getArguments().getParcelable("item");

        CarouselConfig config = CarouselConfig.getInstance();
        float scale;
        if (config.scrollScaling) {
            scale = CarouselConfig.SMALL_SCALE;
        } else {
            scale = CarouselConfig.BIG_SCALE;
        }

        PageLayout root = (PageLayout) layout.findViewById(R.id.root);
        root.setScaleBoth(scale);

        TextView title = (TextView) layout.findViewById(R.id.title);
        title.setText(item.getTitle());

        if (config.orientation == CarouselConfig.VERTICAL) {
            layout.setScaleX(1.0f / config.scaleY);
            layout.setScaleY(1.0f / config.scaleX);
            layout.setRotation(-90);
        }

        return layout;
    }
}