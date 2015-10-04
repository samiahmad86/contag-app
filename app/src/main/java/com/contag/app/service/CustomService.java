package com.contag.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.config.ContagApplication;
import com.contag.app.model.DaoSession;
import com.contag.app.model.InterestSuggestion;
import com.contag.app.model.MessageResponse;
import com.contag.app.model.ProfileRequestModel;
import com.contag.app.model.SocialPlatform;
import com.contag.app.model.SocialPlatformDao;
import com.contag.app.model.SocialPlatformResponse;
import com.contag.app.request.InterestSuggestionRequest;
import com.contag.app.request.ProfileRequest;
import com.contag.app.request.SocialPlatformRequest;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;

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
            final int serviceID = startId;
            int type = intent.getIntExtra(Constants.Keys.KEY_SERVICE_TYPE, 0);
            if(type == Constants.Types.SERVICE_GET_ALL_PLATFORMS) {
                SocialPlatformRequest spr = new SocialPlatformRequest();
                mSpiceManager.execute(spr, new RequestListener<SocialPlatformResponse.List>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        CustomService.this.stopSelf(serviceID);
                    }

                    @Override
                    public void onRequestSuccess(SocialPlatformResponse.List socialPlatforms) {

                        DaoSession session = ((ContagApplication) getApplicationContext()).getDaoSession();

                        for(SocialPlatformResponse spr : socialPlatforms) {
                            SocialPlatformDao spDao = session.getSocialPlatformDao();
                            SocialPlatform sp = new SocialPlatform(spr.id);
                            sp.setPlatformBaseUrl(spr.platformUrl);
                            sp.setPlatformName(spr.platformName);
                            spDao.insertOrReplace(sp);
                        }

                        CustomService.this.stopSelf(serviceID);
                    }
                });
            } else if(type == Constants.Types.SERVICE_GET_INTEREST_SUGGESTIONS) {
                InterestSuggestionRequest isr = new InterestSuggestionRequest
                        (intent.getStringExtra(Constants.Keys.KEY_INTEREST_SUGGESTION_SLUG));
                mSpiceManager.execute(isr, new RequestListener<InterestSuggestion.List>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {

                    }

                    @Override
                    public void onRequestSuccess(InterestSuggestion.List interestSuggestions) {
                        ArrayList<String> suggestions = new ArrayList<>();
                        for(InterestSuggestion is : interestSuggestions) {
                            suggestions.add(is.name);
                        }
                        Intent iSuggestion = new Intent(getResources().getString(R.string.intent_filter_interest_suggestion));
                        iSuggestion.putExtra(Constants.Keys.KEY_INTEREST_SUGGESTION_LIST, suggestions);
                        LocalBroadcastManager.getInstance(CustomService.this).sendBroadcast(iSuggestion);
                        CustomService.this.stopSelf(serviceID);
                    }
                });
            } else if(type == Constants.Types.SERVICE_MAKE_PROFILE_REQUEST) {
                ProfileRequestModel prm = new ProfileRequestModel(
                        intent.getLongExtra(Constants.Keys.KEY_PROFILE_REQUEST_FOR_USER, 0),
                        intent.getStringExtra(Constants.Keys.KEY_PROFILE_REQUEST_TYPE));
                ProfileRequest pr = new ProfileRequest(prm);
                Log.d(TAG, "" + prm.type + " " + prm.id);
                mSpiceManager.execute(pr, new RequestListener<MessageResponse>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        Toast.makeText(CustomService.this, "There was an error please try again", Toast.LENGTH_LONG).show();
                        CustomService.this.stopSelf(serviceID);
                    }

                    @Override
                    public void onRequestSuccess(MessageResponse messageResponse) {
                        Log.d(TAG, messageResponse.message);
                        Toast.makeText(CustomService.this, messageResponse.message, Toast.LENGTH_LONG).show();
                        CustomService.this.stopSelf(serviceID);
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
