package com.acbelter.directionalcarousel.page;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.acbelter.directionalcarousel.CarouselConfig;

public class PageLayout extends LinearLayout {
    private float mScale = CarouselConfig.BIG_SCALE;

    public PageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    public void setScaleBoth(float scale) {
        mScale = scale;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.scale(mScale, mScale, getWidth() / 2, getHeight() / 2);
    }
}
