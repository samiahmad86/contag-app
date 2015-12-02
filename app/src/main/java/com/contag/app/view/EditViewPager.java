package com.contag.app.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

public class EditViewPager extends ViewPager {

    private boolean isEnabled;
    private Context mContext;
    private float x1, x2;

    public EditViewPager(Context context) {
        super(context);
        this.isEnabled = true;
        this.mContext = context;
    }

    public EditViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isEnabled = true;
        this.mContext = context;
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
