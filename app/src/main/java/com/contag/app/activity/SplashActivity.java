package com.contag.app.activity;

import android.content.Context;
import android.os.Bundle;

import com.contag.app.R;
import com.contag.app.config.Router;
import com.contag.app.util.PrefUtils;

/**
 * Created by tanay on 30/7/15.
 * TODO:
 * 1) Create animation
 * 2) Check if user is logged in,
 * a) if not then start {@link LoginActivity} using {@link Router#startLoginActivity(Context, String, int)}
 * b) else open {@link HomeActivity} using {@link Router#startHomeActivity(Context, String)}
 * 3) Close the activity after corresponding activity is launched.
 */

public class SplashActivity extends BaseActivity {

    public static final String TAG = SplashActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (isUserLoggedIn()) {
            if(PrefUtils.getCurrentUser() != null) {
                Router.startHomeActivity(this, TAG);
            } else {
                Router.startNewUserActivity(this, TAG, 0);
            }
        } else {
            Router.startGcmRegisterService(this);
        }

    }
    
}
