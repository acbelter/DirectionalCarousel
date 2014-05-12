package com.acbelter.directionalcarousel.page;

import android.view.View;

public interface OnPageClickListener {
    void onSingleTap(View view, PageItem item);
    void onDoubleTap(View view, PageItem item);
}
