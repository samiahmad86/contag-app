package com.contag.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.config.ContagApplication;
import com.contag.app.model.ContagContag;
import com.contag.app.model.ContagContagDao;
import com.contag.app.model.DaoSession;
import com.contag.app.model.Interest;
import com.contag.app.model.InterestDao;
import com.contag.app.model.InterestResponse;
import com.contag.app.model.SocialProfile;
import com.contag.app.model.SocialProfileDao;
import com.contag.app.model.SocialProfileResponse;
import com.contag.app.model.User;
import com.contag.app.request.UserRequest;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class UserService extends Service implements RequestListener<User> {
    private SpiceManager mSpiceManager = new SpiceManager(APIService.class);
    private static final String TAG = UserService.class.getName();
    private int profileType = 0;

    public UserService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int type = intent.getIntExtra(Constants.Keys.KEY_REQUEST_TYPE, 0);
            switch (type) {
                case Constants.Types.REQUEST_GET: {
                    UserRequest mUserRequest = new UserRequest(type);
                    mSpiceManager.execute(mUserRequest, this);
                    break;
                }
                case Constants.Types.REQUEST_PUT: {
                    String userArrayStr = intent.getStringExtra(Constants.Keys.KEY_USER_ARRAY);
                    UserRequest mUserRequest = new UserRequest(type, userArrayStr);
                    profileType = intent.getIntExtra(Constants.Keys.KEY_USER_PROFILE_TYPE, 0);
                    mSpiceManager.execute(mUserRequest, this);
                    break;
                }
            }
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSpiceManager.start(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSpiceManager.isStarted()) {
            mSpiceManager.shouldStop();
        }
    }


    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Toast.makeText(this, "There was an error in updating your profile", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getResources().getString(R.string.intent_filter_user_received));
        if(profileType != 0) {
            intent.putExtra(Constants.Keys.KEY_USER_PROFILE_TYPE, profileType);
        }
        LocalBroadcastManager.getInstance(UserService.this).sendBroadcast(intent);
    }

    @Override
    public void onRequestSuccess(User user) {

        PrefUtils.setCurrentUserID(user.id);
        new SaveUser().execute(user);
    }

    private class SaveUser extends AsyncTask<User, Void, Void> {
        @Override
        protected Void doInBackground(User... params) {
            User user = params[0];
            DaoSession session = ((ContagApplication) getApplicationContext()).getDaoSession();
            ContagContagDao ccDao = session.getContagContagDao();
            ContagContag cc = new ContagContag(user.id);
            cc.setCreatedOn(user.createdOn);
            cc.setUpdatedOn(user.updatedOn);
            cc.setName(user.name);
            cc.setMobileNumber(user.mobileNumber);
            cc.setContag(user.contag);
            cc.setLandLineNumber(user.landlineNumber);
            cc.setEmergencyContactNumber(user.emergencyContactNumber);
            cc.setIsMobileVerified(user.isMobileVerified);
            cc.setGender(user.gender);
            cc.setAddress(user.address);
            cc.setWorkEmail(user.workEmail);
            cc.setWorkMobileNumber(user.workMobileNumber);
            cc.setWorkLandLineNumber(user.workLandlineNumber);
            cc.setWebsite(user.website);
            cc.setDesignation(user.designation);
            cc.setWorkFacebookPage(user.workFacebookPage);
            cc.setAndroidAppLink(user.androidAppLink);
            cc.setIosAppLink(user.iosAppLink);
            cc.setAvatarUrl(user.avatarUrl);
            cc.setBloodGroup(user.bloodGroup);
            cc.setDateOfBirth(user.dateOfBirth);
            cc.setIsMobileVerified(user.isMobileVerified);
            cc.setMaritalStatus(user.maritalStatus);
            cc.setMarriageAnniversary(user.marriageAnniversary);
            cc.setPersonalEmail(user.personalEmail);
            cc.setWorkAddress(user.workAddress);

            if (user.userInterest != null && user.userInterest.size() > 0) {
                InterestDao interestDao = session.getInterestDao();
                for (InterestResponse ir : user.userInterest) {
                    Interest interest = new Interest(ir.id);
                    interest.setName(ir.name);
                    interest.setContagUserId(user.id);
                    interest.setContagContag(cc);
                    interestDao.insertOrReplace(interest);
                }
            }

            if (user.socialProfile != null && user.socialProfile.size() > 0) {
                SocialProfileDao spDao = session.getSocialProfileDao();
                for (SocialProfileResponse spr : user.socialProfile) {
                    SocialProfile socialProfile = new SocialProfile();
                    socialProfile.setPlatform_id(spr.platformId);
                    socialProfile.setSocial_platform(spr.socialPlatform);
                    socialProfile.setContagContag(cc);
                    socialProfile.setContagUserId(user.id);
                    spDao.insertOrReplace(socialProfile);
                }
            }


            ccDao.insertOrReplace(cc);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Intent intent = new Intent(getResources().getString(R.string.intent_filter_user_received));
            if(profileType != 0) {
                intent.putExtra(Constants.Keys.KEY_USER_PROFILE_TYPE, profileType);
            }
            LocalBroadcastManager.getInstance(UserService.this).sendBroadcast(intent);
            UserService.this.stopSelf();
        }
    }

}
