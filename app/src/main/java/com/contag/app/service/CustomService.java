package com.contag.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.config.ContagApplication;
import com.contag.app.fragment.CurrentUserProfileEditFragment;
import com.contag.app.model.ContagContag;
import com.contag.app.model.ContagContagDao;
import com.contag.app.model.CustomShare;
import com.contag.app.model.CustomShareDao;
import com.contag.app.model.DaoSession;
import com.contag.app.model.InterestPost;
import com.contag.app.model.InterestSuggestion;
import com.contag.app.model.MessageResponse;
import com.contag.app.model.ProfileRequestModel;
import com.contag.app.model.Response;
import com.contag.app.model.SocialPlatform;
import com.contag.app.model.SocialPlatformDao;
import com.contag.app.model.SocialPlatformResponse;
import com.contag.app.model.SocialProfile;
import com.contag.app.model.SocialProfileDao;
import com.contag.app.model.SocialRequestModel;
import com.contag.app.model.User;
import com.contag.app.request.InterestRequest;
import com.contag.app.request.InterestSuggestionRequest;
import com.contag.app.request.LogMessageRequest;
import com.contag.app.request.ProfileRequest;
import com.contag.app.request.SocialPlatformRequest;
import com.contag.app.request.SocialProfileRequest;
import com.contag.app.util.PrefUtils;
import com.google.gson.Gson;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

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
    public int onStartCommand(Intent intent, int flags, final int startId) {
        if (intent != null) {
            final int serviceID = startId;
            int type = intent.getIntExtra(Constants.Keys.KEY_SERVICE_TYPE, 0);
            if (type == Constants.Types.SERVICE_GET_ALL_PLATFORMS) {
                SocialPlatformRequest spr = new SocialPlatformRequest();
                Log.d("SocialVocial", "About to make the social request");
                mSpiceManager.execute(spr, new RequestListener<SocialPlatformResponse.List>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        Log.d("SocialVocial", "Request Failed");
                        CustomService.this.stopSelf(serviceID);
                    }

                    @Override
                    public void onRequestSuccess(SocialPlatformResponse.List socialPlatforms) {
                        Log.d("SocialVocial", "Request was successful");
                        DaoSession session = ((ContagApplication) getApplicationContext()).getDaoSession();
                        Log.d("SocialVocial", String.valueOf(socialPlatforms.size()));
                        for (SocialPlatformResponse spr : socialPlatforms) {
                            SocialPlatformDao spDao = session.getSocialPlatformDao();
                            SocialPlatform sp = new SocialPlatform(spr.id);
                            sp.setPlatformBaseUrl(spr.platformUrl);
                            sp.setPlatformName(spr.platformName);
                            Log.d("SocialVocial", "Platform id: " +  spr.id) ;
                            Log.d("SocialVocial", "Platform: " + spr.platformName);
                            spDao.insertOrReplace(sp);
                        }

                        CustomService.this.stopSelf(serviceID);
                    }
                });
            } else if (type == Constants.Types.SERVICE_GET_INTEREST_SUGGESTIONS) {
                InterestSuggestionRequest isr = new InterestSuggestionRequest
                        (intent.getStringExtra(Constants.Keys.KEY_INTEREST_SUGGESTION_SLUG));
                final int view = intent.getIntExtra(Constants.Keys.KEY_VIEW_POSITION, -1);
                mSpiceManager.execute(isr, new RequestListener<InterestSuggestion.List>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {

                    }

                    @Override
                    public void onRequestSuccess(InterestSuggestion.List interestSuggestions) {
                        Gson gson = new Gson();
                        Log.d(TAG, gson.toJson(interestSuggestions));
                        Intent iSuggestion = new Intent(getResources().getString(R.string.intent_filter_interest_suggestion));
                        iSuggestion.putExtra(Constants.Keys.KEY_INTEREST_SUGGESTION_LIST, gson.toJson(interestSuggestions));
                        iSuggestion.putExtra(Constants.Keys.KEY_VIEW_POSITION, view);
                        LocalBroadcastManager.getInstance(CustomService.this).sendBroadcast(iSuggestion);
                        CustomService.this.stopSelf(serviceID);
                    }
                });
            } else if (type == Constants.Types.SERVICE_MAKE_PROFILE_REQUEST) {
                ProfileRequestModel prm = new ProfileRequestModel(
                        intent.getLongExtra(Constants.Keys.KEY_PROFILE_REQUEST_FOR_USER, 0),
                        intent.getStringExtra(Constants.Keys.KEY_PROFILE_REQUEST_TYPE),
                        intent.getStringExtra(Constants.Keys.KEY_FIELD_TYPE));
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
//                        Log.d(TAG, messageResponse.message);
                        Toast.makeText(CustomService.this, "Request Sent", Toast.LENGTH_LONG).show();
                     //   Toast.makeText(CustomService.this, messageResponse.message, Toast.LENGTH_LONG).show();
                        CustomService.this.stopSelf(serviceID);
                    }
                });
            } else if (type == Constants.Types.SERVICE_ADD_SOCIAL_PROFILE) {
                Bundle args = intent.getBundleExtra(Constants.Keys.KEY_BUNDLE);
                Log.d("SocialVocial", "Syncing facebook:" + args.getLong(Constants.Keys.KEY_SOCIAL_PLATFORM_ID)) ;
                Log.d("NewUser", "Username received for server request: " +
                        args.getString(Constants.Keys.KEY_USER_PLATFORM_USERNAME)) ;
                final SocialRequestModel sm = new SocialRequestModel(
                        args.getLong(Constants.Keys.KEY_SOCIAL_PLATFORM_ID, 1l),
                        args.getString(Constants.Keys.KEY_PLATFORM_ID, null),
                        args.getString(Constants.Keys.KEY_PLATFORM_TOKEN, null),
                        args.getString(Constants.Keys.KEY_PLATFORM_PERMISSION, null),
                        args.getString(Constants.Keys.KEY_PLATFORM_SECRET, null),
                        args.getString(Constants.Keys.KEY_PLATFORM_EMAIL_ID, null),
                        args.getString(Constants.Keys.KEY_USER_PLATFORM_USERNAME, null));
                SocialRequestModel.List srm = new SocialRequestModel.List() ;
                srm.add(sm) ;
                final SocialRequestModel.List srmList = srm ;
                SocialProfileRequest socialProfileRequest = new SocialProfileRequest(srmList);
                mSpiceManager.execute(socialProfileRequest, new RequestListener<User>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                    }

                    @Override
                    public void onRequestSuccess(User user) {
                        new SaveSocialProfile().execute(srmList);
                    }
                });
            } else if (type == Constants.Types.SERVICE_POST_INTERESTS) {
                String interestIDs = intent.getStringExtra(Constants.Keys.KEY_INTEREST_IDS);
                final int viewPosition = intent.getIntExtra(Constants.Keys.KEY_VIEW_POSITION, -1);
                InterestPost ip = new InterestPost(interestIDs);
                InterestRequest ir = new InterestRequest(ip);
                mSpiceManager.execute(ir, new RequestListener<Response>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {

                    }

                    @Override
                    public void onRequestSuccess(Response response) {
                        CustomService.this.stopSelf(serviceID);
                    }
                });
            } else if(type == Constants.Types.SERVICE_SEND_LOG_MESSAGES) {
                long timestamp = intent.getLongExtra(Constants.Keys.KEY_TIMESTAMP, 0l);
                String logMessage = intent.getStringExtra(Constants.Keys.KEY_MESSAGE);
                if(logMessage != null && timestamp != 0l) {
                    Log.d(CurrentUserProfileEditFragment.TAG, "fuck this world " + logMessage + " " + timestamp);
                    LogMessageRequest mLogMessageRequest = new LogMessageRequest(logMessage, timestamp);
                    mSpiceManager.execute(mLogMessageRequest, new RequestListener<MessageResponse>() {
                        @Override
                        public void onRequestFailure(SpiceException spiceException) {

                        }

                        @Override
                        public void onRequestSuccess(MessageResponse messageResponse) {
                            Log.d(CurrentUserProfileEditFragment.TAG, "Fuck me "  + messageResponse.message);
                            CustomService.this.stopSelf(startId);
                        }
                    });
                }
            }
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSpiceManager.isStarted()) {
            mSpiceManager.shouldStop();
        }
    }

    private class SaveSocialProfile extends AsyncTask<SocialRequestModel.List, Void, Boolean> {
        @Override
        protected Boolean doInBackground(SocialRequestModel.List... params) {
            if (params.length > 0) {
                SocialRequestModel.List srmList = params[0];
                SocialRequestModel srm = srmList.get(0)  ;
                Log.d("NewUser", "Srm has this in SaveSocialProfile: "+ srm.platformUsername) ;
                DaoSession session = ((ContagApplication) CustomService.this.getApplicationContext()).getDaoSession();
                SocialProfile socialProfile = new SocialProfile(srm.socialPlatformId);
                socialProfile.setPlatform_id(srm.platformId);
                ContagContagDao ccDao = session.getContagContagDao();
                ContagContag cc = ccDao.queryBuilder().where(ContagContagDao.Properties.Id.eq(PrefUtils.getCurrentUserID())).
                        list().get(0);
                socialProfile.setContagContag(cc);
                SocialPlatformDao socialPlatformDao = session.getSocialPlatformDao();
                String socialPlatformName = socialPlatformDao.queryBuilder().
                        where(SocialPlatformDao.Properties.Id.eq(srm.socialPlatformId)).list().get(0).getPlatformName();
                socialProfile.setSocial_platform(socialPlatformName);
                socialProfile.setPlatform_username(srm.platformUsername);
                SocialProfileDao socialProfileDao = session.getSocialProfileDao();
                socialProfileDao.insertOrReplace(socialProfile);

                // Now save the share object, this is done because the user object has already been saved previous
                // to saving the facebook social platform. And at that time the social platform was not saved on the server
                // hence no share record for this was returned
                CustomShare mCustomShare = new CustomShare() ;
                CustomShareDao mCustomDao = session.getCustomShareDao() ;
                mCustomShare.setIs_public(false);
                mCustomShare.setUser_ids("");
                mCustomShare.setField_name(socialPlatformName);
                mCustomShare.setIs_private(true);
                mCustomShare.setContagContag(cc);
                mCustomShare.setUserID(PrefUtils.getCurrentUserID());
                mCustomDao.insertOrReplace(mCustomShare) ;
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.d(TAG, "socialProfileUpdated");
            Intent intent = new Intent(getResources().getString(R.string.intent_filter_user_social));
            intent.putExtra(Constants.Keys.KEY_USER_PROFILE_TYPE, Constants.Types.PROFILE_SOCIAL);
            LocalBroadcastManager.getInstance(CustomService.this).sendBroadcast(intent);
            CustomService.this.stopSelf();

        }
    }
}
