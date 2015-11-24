package com.contag.app.service;

import android.app.Application;

import com.contag.app.config.Constants;
import com.contag.app.util.ContagConnectionClient;
import com.contag.app.util.DeviceUtils;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.retrofit.GsonRetrofitObjectPersisterFactory;
import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

import java.io.File;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Client;

public class APIService extends RetrofitGsonSpiceService {

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    protected String getServerUrl() {
        return Constants.Urls.BASE_URL;
    }


    @Override
    protected RestAdapter.Builder createRestAdapterBuilder() {
        final RestAdapter.Builder builder = new RestAdapter.Builder().setClient(new ContagConnectionClient()).
                setEndpoint(getServerUrl()).setConverter(getConverter()).setRequestInterceptor(new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader(Constants.Headers.HEADER_DEVICE_ID, DeviceUtils.getmDeviceId(getBaseContext()));
            }
        });
        return builder;
    }

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
