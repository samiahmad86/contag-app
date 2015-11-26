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
import com.contag.app.config.Router;
import com.contag.app.fragment.NewUserDetailsFragment;
import com.contag.app.model.ContactResponse;
import com.contag.app.model.ContagContag;
import com.contag.app.model.ContagContagDao;
import com.contag.app.model.CustomShare;
import com.contag.app.model.DaoSession;
import com.contag.app.model.FieldRequestNotificationResponse;
import com.contag.app.model.ImageUploadResponse;
import com.contag.app.model.Interest;
import com.contag.app.model.InterestPost;
import com.contag.app.model.MessageResponse;
import com.contag.app.model.PrivacyRequest;
import com.contag.app.model.ProfilePrivacyRequestModel;
import com.contag.app.model.Response;
import com.contag.app.model.SocialProfile;
import com.contag.app.model.User;
import com.contag.app.request.ContactRequest;
import com.contag.app.request.FieldRequest;
import com.contag.app.request.ImageUploadRequest;
import com.contag.app.request.InterestRequest;
import com.contag.app.request.UserRequest;
import com.contag.app.util.ContactUtils;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.io.File;
import java.util.ArrayList;

import retrofit.mime.TypedFile;

public class UserService extends Service implements RequestListener<User> {
    private SpiceManager mSpiceManager = new SpiceManager(APIService.class);
    private static final String TAG = UserService.class.getName();
    private int profileType = 0;
    private int requestType = 0;
    private boolean isContagContact;

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
                    long userID = intent.getLongExtra(Constants.Keys.KEY_USER_ID, 1l);
                    UserRequest mUserRequest = new UserRequest(requestType, userID);
                    mSpiceManager.execute(mUserRequest, this);
                    break;
                }
                case Constants.Types.REQUEST_PUT: {
                    String userArrayStr = intent.getStringExtra(Constants.Keys.KEY_USER_ARRAY);
                    Log.d(TAG, "making request " + userArrayStr);
                    UserRequest mUserRequest = new UserRequest(requestType, userArrayStr);
                    profileType = intent.getIntExtra(Constants.Keys.KEY_USER_PROFILE_TYPE, 0);
                    mSpiceManager.execute(mUserRequest, this);
                    break;
                }
                case Constants.Types.REQUEST_UPDATE_USER_INTEREST: {
                    Log.d("iList", "About to start service");
                    String interestList = intent.getStringExtra(Constants.Keys.KEY_INTEREST_IDS);
                    InterestPost interestIDList = new InterestPost(interestList);
                    InterestRequest interestRequest = new InterestRequest(interestIDList);
                    mSpiceManager.execute(interestRequest, new RequestListener<Response>() {
                        @Override
                        public void onRequestFailure(SpiceException spiceException) {
                            Log.d("iList", "request failed");
                        }

                        @Override
                        public void onRequestSuccess(Response response) {
                            Log.d("iList", response.toString());
                            Intent intent = new Intent(getResources().getString(R.string.intent_filter_interest_updated));
                            LocalBroadcastManager.getInstance(UserService.this).sendBroadcast(intent);
                        }
                    });
                    break;
                }
                case Constants.Types.REQUEST_POST_PRIVACY: {

                    final String fieldName = intent.getStringExtra(Constants.Keys.KEY_FIELD_NAME);
                    final Boolean isPublic = intent.getBooleanExtra(Constants.Keys.KEY_IS_PUBLIC, false);
                    final String userIDS = intent.getStringExtra(Constants.Keys.KEY_USER_IDS);

                    ProfilePrivacyRequestModel privacyRequestModel = new ProfilePrivacyRequestModel(fieldName, isPublic, userIDS);

                    PrivacyRequest privacyRequest = new PrivacyRequest(privacyRequestModel);
                    mSpiceManager.execute(privacyRequest, new RequestListener<MessageResponse>() {
                        @Override
                        public void onRequestFailure(SpiceException spiceException) {
                            Log.d("share", "failure");
                        }

                        @Override
                        public void onRequestSuccess(MessageResponse response) {
                            User.updatePrivacy(fieldName, isPublic, userIDS, getApplicationContext());
                            Toast.makeText(UserService.this, "Shared successfully!", Toast.LENGTH_LONG).show();

                        }
                    });
                    break;
                }

                case Constants.Types.SERVICE_REJECT_FIELD_REQUEST: {
                }
                case Constants.Types.SERVICE_ALLOW_FIELD_REQUEST: {
                    long requestID = intent.getLongExtra(Constants.Keys.KEY_REQUEST_ID, 0l);
                    String status = (requestType == Constants.Types.SERVICE_REJECT_FIELD_REQUEST) ? Constants.Values.STATUS_DECLINE :
                            Constants.Values.STATUS_ALLOWED;
                    FieldRequestNotificationResponse mFieldRequestNotificationResponse = new FieldRequestNotificationResponse(status, requestID);
                    FieldRequest mFieldRequest = new FieldRequest(mFieldRequestNotificationResponse);
                    mSpiceManager.execute(mFieldRequest, new RequestListener<Response>() {
                        @Override
                        public void onRequestFailure(SpiceException spiceException) {

                        }

                        @Override
                        public void onRequestSuccess(Response response) {
                            Log.d(TAG, response.result + " fuck");
                            UserService.this.stopSelf();
                        }
                    });
                    break;
                }

                case Constants.Types.SERVICE_UPLOAD_PROFILE_PICTURE: {
                    String selectedImagePath = intent.getStringExtra(Constants.Keys.KEY_IMAGE_PATH);
                    File selectedImageFile = new File(selectedImagePath);
                    final String serviceStartID = startId + "";
                    Log.d(TAG, selectedImagePath);
                    String extension = selectedImageFile.getAbsolutePath().
                            substring(selectedImageFile.getAbsolutePath().lastIndexOf(".") + 1);
                    if (selectedImageFile.length() / 1024 >= 1024) {
                        Toast.makeText(this, "The selected image is too big", Toast.LENGTH_SHORT).show();
                    }  else {
                        Log.d(TAG, "Starting image upload");
                        ImageUploadRequest mImageUploadRequest = new ImageUploadRequest
                                (new TypedFile("multipart/form-data", selectedImageFile));
                        mSpiceManager.execute(mImageUploadRequest, new RequestListener<ImageUploadResponse>() {
                            @Override
                            public void onRequestFailure(SpiceException spiceException) {

                            }

                            @Override
                            public void onRequestSuccess(ImageUploadResponse response) {
                                if (response.result) {
                                    if (PrefUtils.getCurrentUserID() != 0l) {
                                        new ChangeAvatarUrl().execute(response.avatarUrl, serviceStartID);
                                    } else {
                                        sendImageBroadcast(Constants.Urls.BASE_URL + response.avatarUrl);
                                    }
                                }
                            }
                        });

                    }
                    break;
                }
                case Constants.Types.REQUEST_GET_USER_BY_USER_ID: {
                    Log.d("newprofile", "Contact request being made") ;
                    ContactRequest contactRequest = new ContactRequest
                            (intent.getLongExtra(Constants.Keys.KEY_NOTIF_USER_ID, 0l), requestType);
                    isContagContact = intent.getBooleanExtra(Constants.Keys.KEY_IS_CONTAG_CONTACT, false);
                    mSpiceManager.execute(contactRequest, new RequestListener<ContactResponse.ContactList>() {
                        @Override
                        public void onRequestFailure(SpiceException spiceException) {

                        }

                        @Override
                        public void onRequestSuccess(ContactResponse.ContactList contactResponses) {
                            Log.d("newprofile", "Request is successfull" + contactResponses.size()) ;

                            if(contactResponses.size() == 1) {
                                new InsertContagContact().execute(contactResponses);
                            }
                        }
                    });
                    break;
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
        if (profileType != 0) {
            intent.putExtra(Constants.Keys.KEY_USER_PROFILE_TYPE, profileType);
        }
        LocalBroadcastManager.getInstance(UserService.this).sendBroadcast(intent);
    }

    @Override
    public void onRequestSuccess(User user) {
        Log.d(TAG, "success");
        if (user.name != null) {
            if (requestType == Constants.Types.REQUEST_GET_CURRENT_USER || requestType == Constants.Types.REQUEST_PUT) {
                PrefUtils.setCurrentUserID(user.id);
            }
            new SaveUser().execute(user);
        }
    }

    private void sendImageBroadcast(String avatarUrl) {
        Intent iProfilePictureChanged = new Intent(getResources().getString(R.string.intent_filter_profile_picture_changed));
        iProfilePictureChanged.putExtra(Constants.Keys.KEY_USER_AVATAR_URL, avatarUrl);
        LocalBroadcastManager.getInstance(UserService.this).sendBroadcast(iProfilePictureChanged);
    }

    public class SaveUser extends AsyncTask<User, Void, Void> {
        @Override
        protected Void doInBackground(User... params) {


            User user = params[0];
            DaoSession session = ((ContagApplication) getApplicationContext()).getDaoSession();

            ContagContag cc = User.getContagContagObject(user);
            ArrayList<Interest> interestList = User.getInterestList(user.userInterest, user, cc);
            ArrayList<SocialProfile> socialProfiles = User.getSocialProfileList(user.socialProfile, user, cc);
            ArrayList<CustomShare> customShares = User.getCustomShareList(user.customShares, cc);

            User.storeInterests(interestList, session);
            User.storeSocialProfile(socialProfiles, session);
            User.storeCustomShare(customShares, session);

            ContagContagDao ccDao = session.getContagContagDao();
            ccDao.insertOrReplace(cc);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Intent intent;
            if (requestType != Constants.Types.REQUEST_GET_USER_BY_ID)
                intent = new Intent(getResources().getString(R.string.intent_filter_user_received));
            else
                intent = new Intent("com.contag.app.user.id");

            if (profileType != 0)
                intent.putExtra(Constants.Keys.KEY_USER_PROFILE_TYPE, profileType);

            Log.d(NewUserDetailsFragment.TAG, "sending broadcast about new user created");
            LocalBroadcastManager.getInstance(UserService.this).sendBroadcast(intent);

            UserService.this.stopSelf();
        }
    }


    private class ChangeAvatarUrl extends AsyncTask<String, Void, ContagContag> {
        private int startID;

        @Override
        protected ContagContag doInBackground(String... params) {
            startID = Integer.parseInt(params[1]);
            DaoSession session = ((ContagApplication) getApplicationContext()).getDaoSession();
            ContagContagDao contagContagDao = session.getContagContagDao();
            ContagContag mContagContag = contagContagDao.queryBuilder().
                    where(ContagContagDao.Properties.Id.eq(PrefUtils.getCurrentUserID())).list().get(0);
            mContagContag.setAvatarUrl(Constants.Urls.BASE_URL + params[0]);
            contagContagDao.update(mContagContag);
            return mContagContag;
        }

        @Override
        protected void onPostExecute(ContagContag mContagContag) {
            if (mContagContag != null) {
                sendImageBroadcast(mContagContag.getAvatarUrl());
                UserService.this.stopSelf(startID);
            }
        }
    }

    private class InsertContagContact extends AsyncTask<ContactResponse.ContactList, Void, Void> {
        private long userId;
        @Override
        protected Void doInBackground(ContactResponse.ContactList... params) {
            ContactUtils.saveSingleContact(UserService.this, params[0], isContagContact);
            userId = params[0].get(0).contagContactResponse.id;
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            if(!isContagContact) {
                Intent iStartUserActivity = new Intent(getResources().getString(R.string.intent_filter_contag_contact_inserted));
                iStartUserActivity.putExtra(Constants.Keys.KEY_USER_ID, userId);
                LocalBroadcastManager.getInstance(UserService.this).sendBroadcast(iStartUserActivity);
            }
        }
    }
}
