package com.acbelter.directionalcarousel.fglayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.acbelter.directionalcarousel.R;

public class ForegroundLinearLayout extends LinearLayout {
    protected Drawable mForegroundDrawable;

    public ForegroundLinearLayout(Context context) {
        super(context);
    }

    public ForegroundLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ForegroundLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ForegroundLinearLayout,
                defStyle, 0);
        try {
            if (a != null) {
                mForegroundDrawable = a.getDrawable(R.styleable
                        .ForegroundLinearLayout_android_foreground);
            }
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mForegroundDrawable != null) {
            mForegroundDrawable.setState(getDrawableState());
            invalidate();
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldwidth, int oldheight) {
        super.onSizeChanged(width, height, oldwidth, oldheight);
        if (mForegroundDrawable != null) {
            mForegroundDrawable.setBounds(0, 0, width, height);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (mForegroundDrawable != null) {
            mForegroundDrawable.draw(canvas);
        }
    }
}
