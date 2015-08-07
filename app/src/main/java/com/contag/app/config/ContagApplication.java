package com.contag.app.config;

import android.app.Application;

import com.contag.app.util.PrefUtils;
import com.facebook.FacebookSdk;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by tanay on 5/8/15.
 */
public class ContagApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PrefUtils.init(this);
        FlowManager.init(this);
        FacebookSdk.sdkInitialize(this);
    }
}
