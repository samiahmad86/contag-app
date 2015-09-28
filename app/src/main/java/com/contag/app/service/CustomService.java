package com.contag.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.contag.app.config.Constants;
import com.contag.app.config.ContagApplication;
import com.contag.app.model.DaoSession;
import com.contag.app.model.SocialPlatform;
import com.contag.app.model.SocialPlatformDao;
import com.contag.app.model.SocialPlatformResponse;
import com.contag.app.request.SocialPlatformRequest;
import com.contag.app.util.PrefUtils;
import com.google.gson.Gson;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * Created by tanay on 27/9/15.
 */
public class CustomService extends Service {

    public static final String TAG = CustomService.class.getName();
    private SpiceManager mSpiceManager;

    public CustomService() {
        super();
        mSpiceManager = new SpiceManager(APIService.class);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSpiceManager.start(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            int type = intent.getIntExtra(Constants.Keys.KEY_SERVICE_TYPE, 0);
            if(type == Constants.Types.SERVICE_GET_ALL_PLATFORMS) {
                SocialPlatformRequest spr = new SocialPlatformRequest();
                mSpiceManager.execute(spr, new RequestListener<SocialPlatformResponse.List>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {

                    }

                    @Override
                    public void onRequestSuccess(SocialPlatformResponse.List socialPlatforms) {

                        DaoSession session = ((ContagApplication) getApplicationContext()).getDaoSession();

                        for(SocialPlatformResponse spr : socialPlatforms) {
                            SocialPlatformDao spDao = session.getSocialPlatformDao();
                            SocialPlatform sp = new SocialPlatform();
                            sp.setPlatformBaseUrl(spr.syncType);
                            sp.setPlatformName(spr.platformName);
                            spDao.insertOrReplace(sp);
                        }

                        CustomService.this.stopSelf();
                    }
                });
            }
        }
        return START_REDELIVER_INTENT;
    }

        @Override
    public void onDestroy() {
        super.onDestroy();
        if(mSpiceManager.isStarted()) {
            mSpiceManager.shouldStop();
        }
    }
}
