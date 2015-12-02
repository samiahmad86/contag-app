package com.contag.app.config;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDexApplication;

import com.contag.app.model.DaoMaster;
import com.contag.app.model.DaoSession;
import com.contag.app.util.PrefUtils;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterSession;


import java.io.File;

import io.fabric.sdk.android.Fabric;

/**
 * Created by tanay on 5/8/15.
 */
public class ContagApplication extends MultiDexApplication {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "gOHm5XssdjeHt5QzqhmoXQkGL";
    private static final String TWITTER_SECRET = "Im8VaG4NjSeXLjD1Jr5ybcAlSNnzP6fuaDQZDMdCYDJA5vjchM ";


    private DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig), new Crashlytics());
        PrefUtils.init(this);
        FacebookSdk.sdkInitialize(this);

        Result<TwitterSession> result;

        DaoMaster.DevOpenHelper cuntagHelper = new DaoMaster.DevOpenHelper(this, "cuntag", null);
        SQLiteDatabase cuntagDb = cuntagHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(cuntagDb);
        mDaoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return this.mDaoSession;
    }
}
