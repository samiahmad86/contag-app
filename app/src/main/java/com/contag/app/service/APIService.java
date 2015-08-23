package com.contag.app.service;

import android.app.Application;

import com.contag.app.config.Constants;
import com.contag.app.util.DeviceUtils;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.retrofit.GsonRetrofitObjectPersisterFactory;
import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

import java.io.File;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * Created by tanay on 5/8/15.
 * TODO:
 * 1) Add base url to be returned in {@link APIService#getServerUrl()}
 * 2) Add retrofit interface in {@link APIService#onCreate()}
 */
public class APIService extends RetrofitGsonSpiceService {

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    protected String getServerUrl() {
        return Constants.Values.BASE_URL;
    }


    @Override
    protected RestAdapter.Builder createRestAdapterBuilder() {
        RestAdapter.Builder builder = new RestAdapter.Builder().setEndpoint(getServerUrl()).setConverter(getConverter()).setRequestInterceptor(new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader(Constants.Keys.HEADER_DEVICE_ID, DeviceUtils.getmDeviceId(getBaseContext()));
            }
        });
        return builder;
    }

    /////////////////////////// cache methods //////////////////////////////////////////
    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        CacheManager cacheManager = new CacheManager();
        cacheManager.addPersister(new GsonRetrofitObjectPersisterFactory(application, getConverter(), getCacheFolder()));
        return cacheManager;
    }

    public File getCacheFolder() {
        return getBaseContext().getCacheDir();
    }
}
