package com.acbelter.directionalcarousel;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class PageLayout extends LinearLayout {
    private float mScale = MainActivity.BIG_SCALE;

    public PageLayout(Context context) {
        super(context);
    }

    public PageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScaleBoth(float scale) {
        mScale = scale;
        invalidate();
        // If you want to see the scale every time you set
        // scale you need to have this line here,
        // invalidate() function will call onDraw(Canvas)
        // to redraw the view for you
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // The main mechanism to display scale animation, you can customize it
        // as your needs
        canvas.scale(mScale, mScale, getWidth() / 2, getHeight() / 2);
    }
}
