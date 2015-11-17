package com.contag.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.config.ContagApplication;
import com.contag.app.model.ContagContag;
import com.contag.app.model.ContagContagDao;
import com.contag.app.model.CustomShare;
import com.contag.app.model.DaoSession;
import com.contag.app.model.Interest;
import com.contag.app.model.InterestPost;
import com.contag.app.model.MessageResponse;
import com.contag.app.model.PrivacyRequest;
import com.contag.app.model.ProfilePrivacyRequestModel;
import com.contag.app.model.Response;
import com.contag.app.model.SocialProfile;
import com.contag.app.model.User;
import com.contag.app.request.InterestRequest;
import com.contag.app.request.UserRequest;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;

public class UserService extends Service implements RequestListener<User> {
    private SpiceManager mSpiceManager = new SpiceManager(APIService.class);
    private static final String TAG = UserService.class.getName();
    private int profileType = 0;
    private int requestType =  0 ;

    public UserService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            requestType = intent.getIntExtra(Constants.Keys.KEY_REQUEST_TYPE, 0);
            switch (requestType) {
                case Constants.Types.REQUEST_GET_CURRENT_USER: {
                    UserRequest mUserRequest = new UserRequest(requestType);
                    mSpiceManager.execute(mUserRequest, this);
                    break;
                }
                case Constants.Types.REQUEST_GET_USER_BY_ID: {
                    long userID = intent.getLongExtra(Constants.Keys.KEY_USER_ID, 1l) ;
                    UserRequest mUserRequest = new UserRequest(requestType, userID);
                    mSpiceManager.execute(mUserRequest, this);
                    break ;
                }
                case Constants.Types.REQUEST_PUT: {
                    String userArrayStr = intent.getStringExtra(Constants.Keys.KEY_USER_ARRAY);
                    Log.d(TAG, "making request " + userArrayStr);
                    UserRequest mUserRequest = new UserRequest(requestType, userArrayStr);
                    profileType = intent.getIntExtra(Constants.Keys.KEY_USER_PROFILE_TYPE, 0);
                    mSpiceManager.execute(mUserRequest, this);
                    break;
                }
                case Constants.Types.REQUEST_UPDATE_USER_INTEREST:{
                    Log.d("iList", "About to start service") ;
                    String interestList = intent.getStringExtra(Constants.Keys.KEY_INTEREST_IDS) ;
                    InterestPost interestIDList = new InterestPost(interestList) ;
                    InterestRequest interestRequest = new InterestRequest(interestIDList) ;
                    mSpiceManager.execute(interestRequest, new RequestListener<Response>() {
                        @Override
                        public void onRequestFailure(SpiceException spiceException) {
                            Log.d("iList", "request failed") ;
                        }

                        @Override
                        public void onRequestSuccess(Response response) {
                            Log.d("iList", response.toString()) ;
                            Intent intent = new Intent(getResources().getString(R.string.intent_filter_interest_updated));
                            LocalBroadcastManager.getInstance(UserService.this).sendBroadcast(intent);
                        }
                    });
                    break ;
                }
                case Constants.Types.REQUEST_POST_PRIVACY: {

                    final String fieldName = intent.getStringExtra(Constants.Keys.KEY_FIELD_NAME) ;
                    final Boolean isPublic = intent.getBooleanExtra(Constants.Keys.KEY_IS_PUBLIC, false) ;
                    final String userIDS = intent.getStringExtra(Constants.Keys.KEY_USER_IDS) ;

                    ProfilePrivacyRequestModel privacyRequestModel  = new ProfilePrivacyRequestModel(fieldName, isPublic, userIDS) ;

                    PrivacyRequest privacyRequest = new PrivacyRequest(privacyRequestModel) ;
                    mSpiceManager.execute(privacyRequest, new RequestListener<MessageResponse>(){
                        @Override
                        public void onRequestFailure(SpiceException spiceException) {
                            Log.d("share", "failure");
                        }

                        @Override
                        public void onRequestSuccess(MessageResponse response) {
                            User.updatePrivacy(fieldName, isPublic, userIDS, getApplicationContext()) ;
                            Toast.makeText(UserService.this, "Shared successfully!", Toast.LENGTH_LONG).show() ;

                        }}) ;
                    break ;
                }
            }
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "spicemanager started");
        mSpiceManager.start(this);
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "user service being destroyed");
        super.onDestroy();
        if (mSpiceManager.isStarted()) {
            mSpiceManager.shouldStop();
        }
    }
    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Log.d(TAG, "failure");
        Toast.makeText(this, "There was an error in updating your profile", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getResources().getString(R.string.intent_filter_user_received));
        if(profileType != 0) {
            intent.putExtra(Constants.Keys.KEY_USER_PROFILE_TYPE, profileType);
        }
        LocalBroadcastManager.getInstance(UserService.this).sendBroadcast(intent);
    }

    @Override
    public void onRequestSuccess(User user) {
        Log.d(TAG, "success");
        if(user.name != null) {
            if(requestType == Constants.Types.REQUEST_GET_CURRENT_USER || requestType == Constants.Types.REQUEST_PUT) {
                PrefUtils.setCurrentUserID(user.id);
            }
            new SaveUser().execute(user);
        }
    }

    public class SaveUser extends AsyncTask<User, Void, Void> {
        @Override
        protected Void doInBackground(User... params) {


            User user = params[0];
            DaoSession session = ((ContagApplication) getApplicationContext()).getDaoSession();

            ContagContag cc = User.getContagContagObject(user);
            ArrayList<Interest> interestList = User.getInterestList(user.userInterest, user, cc);
            ArrayList<SocialProfile> socialProfiles = User.getSocialProfileList(user.socialProfile, user, cc) ;
            ArrayList<CustomShare> customShares = User.getCustomShareList(user.customShares, cc) ;

            User.storeInterests(interestList, session);
            User.storeSocialProfile(socialProfiles, session);
            User.storeCustomShare(customShares, session);

            ContagContagDao ccDao = session.getContagContagDao();
            ccDao.insertOrReplace(cc);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Intent intent ;
            if(requestType != Constants.Types.REQUEST_GET_USER_BY_ID)
                intent = new Intent(getResources().getString(R.string.intent_filter_user_received));
            else
                intent = new Intent("com.contag.app.user.id") ;

            if (profileType != 0)
                intent.putExtra(Constants.Keys.KEY_USER_PROFILE_TYPE, profileType);

            LocalBroadcastManager.getInstance(UserService.this).sendBroadcast(intent);

            UserService.this.stopSelf();
        }
    }

}
