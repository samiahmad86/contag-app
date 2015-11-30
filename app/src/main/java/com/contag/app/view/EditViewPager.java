package com.contag.app.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * Created by archit on 15/11/15.
 */
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
        } else {
            Log.d("EditViewPager", "here");
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    x1 = event.getX();
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    x2 = event.getX();
                    float deltaX = Math.abs(x2 - x1);
                    if(deltaX > 50) {
                        Log.d("EditViewPager", "More than 50");
                        Toast.makeText(mContext, "You cannot swipe while in edit mode", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("EditViewPager", "less than 50");
                    }
                    break;
                }
            }
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
