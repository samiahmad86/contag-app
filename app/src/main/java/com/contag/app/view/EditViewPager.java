package com.contag.app.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by archit on 15/11/15.
 */
public class EditViewPager extends ViewPager {

    private boolean isEnabled;

    public EditViewPager(Context context) {
        super(context);
        isEnabled = true;
    }

    public EditViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        isEnabled = true;
    }

    public void setSwipeEnabled(boolean value) {
        this.isEnabled = value;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.isEnabled) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.isEnabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

}
