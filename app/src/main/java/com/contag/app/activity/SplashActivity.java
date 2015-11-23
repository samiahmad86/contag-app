package com.contag.app.activity;

import android.os.Bundle;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.config.Router;
import com.contag.app.util.PrefUtils;

/**
 *
 */
public class SplashActivity extends BaseActivity {

    public static final String TAG = SplashActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        if (isUserLoggedIn()) {

            if(PrefUtils.getCurrentUserID() != 0) {
                Router.startHomeActivity(this, TAG);
            } else {
                Router.startNewUserActivity(this, TAG, 0);
            }
        } else {
            Router.getSocialPlatforms(this, Constants.Types.SERVICE_GET_ALL_PLATFORMS);
            Router.startGcmRegisterService(this);
        }

    }
    
}
