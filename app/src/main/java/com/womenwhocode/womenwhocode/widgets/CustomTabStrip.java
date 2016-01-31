package com.womenwhocode.womenwhocode.widgets;

import com.astuetz.PagerSlidingTabStrip;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by zassmin on 11/3/15.
 */
public class CustomTabStrip extends PagerSlidingTabStrip {
    private boolean disabled;

    public CustomTabStrip(Context context) {
        super(context);
    }

    public CustomTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTabStrip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return disabled || super.onInterceptTouchEvent(ev);
    }
}