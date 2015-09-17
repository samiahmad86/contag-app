package com.contag.app.config;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.contag.app.model.DaoMaster;
import com.contag.app.model.DaoSession;
import com.contag.app.util.PrefUtils;
import com.facebook.FacebookSdk;

/**
 * Created by tanay on 5/8/15.
 */
public class ContagApplication extends Application {

    private DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        PrefUtils.init(this);
        FacebookSdk.sdkInitialize(this);

        DaoMaster.DevOpenHelper cuntagHelper = new DaoMaster.DevOpenHelper(this, "cuntag", null);
        SQLiteDatabase cuntagDb = cuntagHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(cuntagDb);
        mDaoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return this.mDaoSession;
    }
}
