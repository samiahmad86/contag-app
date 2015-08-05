package com.contag.app.activity;

import android.os.Bundle;
import android.util.Log;

import com.contag.app.R;
import com.contag.app.util.Router;

/**
 * Created by tanay on 30/7/15.
 * TODO:
 *  1) Create animation
 *  2) Check if user is logged in,
 *      a) if not then start LoginActivity
 *      b) else open HomeActivity
 *  3) Close the activity after corresponding activity is launched
 */

public class SplashActivity extends BaseActivity {

    public static final String TAG = SplashActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Log.d(TAG, getBaseContext().getCacheDir().getAbsolutePath());
        Router.startLoginActivity(SplashActivity.this, SplashActivity.class.getName());
    }
}
