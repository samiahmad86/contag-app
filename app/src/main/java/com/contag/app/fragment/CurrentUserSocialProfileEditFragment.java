package com.contag.app.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.activity.BaseActivity;
import com.contag.app.config.Constants;
import com.contag.app.config.ContagApplication;
import com.contag.app.config.Router;
import com.contag.app.model.ContagContag;
import com.contag.app.model.ContagContagDao;
import com.contag.app.model.DaoSession;
import com.contag.app.model.DeleteSocialProfile;
import com.contag.app.model.Response;
import com.contag.app.model.SocialPlatform;
import com.contag.app.model.SocialPlatformDao;
import com.contag.app.model.SocialProfile;
import com.contag.app.model.SocialProfileDao;
import com.contag.app.model.SocialProfileModel;
import com.contag.app.model.SocialRequestModel;
import com.contag.app.request.DeleteSocialProfileRequest;
import com.contag.app.request.SocialProfileRequest;
import com.contag.app.util.DeviceUtils;
import com.contag.app.util.PrefUtils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CurrentUserSocialProfileEditFragment extends BaseFragment implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        FacebookCallback<LoginResult>, GraphRequest.GraphJSONObjectCallback {

    public static final String TAG = CurrentUserSocialProfileEditFragment.class.getName();

    private ArrayList<ViewHolder> viewHolderArrayList;
    private LinearLayout llViewContainer;
    private Button btnEditProfile;

    private boolean isFbSync = false;
    private boolean isEditModeOn;
    private boolean isComingFromNotification;
    private Bundle requestBundle;
    private String fieldName;
    private String fbAccessToken;
    private int fbViewPosition, googlePlusPosition, instagramViewPosition, twitterViewPosition, linkedInViewPosition;

    private CallbackManager mCallbackManager;
    private TwitterAuthClient mTwitterAuthClient;
    private GoogleApiClient mGoogleApiClient;
    private HashMap<Integer, SocialProfileModel> hmSocialProfileModel;
    private ArrayList<Bundle> bSocialProfileInfo;

    public static CurrentUserSocialProfileEditFragment newInstance() {
        CurrentUserSocialProfileEditFragment currentUserSocialProfileEditFragment = new CurrentUserSocialProfileEditFragment();
        Bundle args = new Bundle();
        currentUserSocialProfileEditFragment.setArguments(args);
        return currentUserSocialProfileEditFragment;
    }

    public static CurrentUserSocialProfileEditFragment newInstance(boolean isComingFromNotification, Bundle requestBundle, String fieldName) {
        CurrentUserSocialProfileEditFragment currentUserSocialProfileEditFragment = new CurrentUserSocialProfileEditFragment();
        Bundle args = new Bundle();
        args.putBundle(Constants.Keys.KEY_DATA, requestBundle);
        args.putBoolean(Constants.Keys.KEY_COMING_FROM_NOTIFICATION, isComingFromNotification);
        args.putString(Constants.Keys.KEY_FIELD_NAME, fieldName);
        currentUserSocialProfileEditFragment.setArguments(args);
        return currentUserSocialProfileEditFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        isComingFromNotification = args.getBoolean(Constants.Keys.KEY_COMING_FROM_NOTIFICATION);
        if(isComingFromNotification) {
            requestBundle = args.getBundle(Constants.Keys.KEY_DATA);
            fieldName = args.getString(Constants.Keys.KEY_FIELD_NAME);
        }
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, this);
        mTwitterAuthClient = new TwitterAuthClient();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.PLUS_LOGIN))
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile_details, container, false);
        hmSocialProfileModel = new HashMap<>();
        viewHolderArrayList = new ArrayList<>();
        bSocialProfileInfo = new ArrayList<>();
        llViewContainer = (LinearLayout) view.findViewById(R.id.ll_profile_container);
        btnEditProfile = (Button) view.findViewById(R.id.btn_edit_profile);
        btnEditProfile.setOnClickListener(this);
        btnEditProfile.setTag(0);
        btnEditProfile.setVisibility(View.VISIBLE);
        isEditModeOn = false;
        new LoadUser().execute();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).
                registerReceiver(brUsr, new IntentFilter(getActivity().getResources().getString(R.string.intent_filter_user_social)));
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(brUsr);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_add: {
            }

            case R.id.btn_edit_profile: {
                if (!DeviceUtils.isInternetConnected(getActivity())) {
                    showToast("Sorry there is no internet.");
                    return;
                }
                if (!isEditModeOn) {
                    openEditMode();
                    scrollToPosition((int) v.getTag());
                } else {
                    sendData();
                }
                break;
            }

            case R.id.btn_facebook_login: {
                String text = ((Button) v).getText().toString();
                if (getActivity().getResources().getString(R.string.fb_unsync_msg).equalsIgnoreCase(text)) {
                    break;
                }
            }
            case R.id.btn_g_plus_login: {
            }
            case R.id.btn_twitter_login: {
            }
            case R.id.btn_linkedin_login: {
            }
            case R.id.btn_instagram_login: {
                SocialProfileModel socialProfileModel = hmSocialProfileModel.get(v.getTag());
                doSocialMediaLogin(socialProfileModel.mViewType, socialProfileModel.mSocialPlatform.getId());
                break;
            }

            case R.id.tv_field_value: {
                SocialProfileModel socialProfileModel = hmSocialProfileModel.get(v.getTag());
                if (socialProfileModel.mViewType == Constants.Types.FIELD_FACEBOOK) {
                    Router.openFacebookProfile(getActivity(), socialProfileModel.mSocialProfile.getPlatform_id());
                } else if (socialProfileModel.mViewType == Constants.Types.FIELD_TWITTER) {
                    Router.openTwitterProfile(getActivity(), socialProfileModel.mSocialProfile.getPlatform_id());
                } else if (socialProfileModel.mViewType == Constants.Types.FIELD_LINKEDIN) {
                    Router.openLinkedInProfile(getActivity(), socialProfileModel.mSocialProfile.getPlatform_id());
                } else if (socialProfileModel.mViewType == Constants.Types.FIELD_INSTAGRAM) {
                    Router.openInstagramProfile(getActivity(), socialProfileModel.mSocialProfile.getPlatform_id());
                } else if (socialProfileModel.mViewType == Constants.Types.FIELD_GOOGLE) {
                    Router.openGooglePlusProfile(getActivity(), socialProfileModel.mSocialPlatform.getPlatformBaseUrl()
                            + "/" + socialProfileModel.mSocialProfile.getPlatform_id() + "/posts");
                }
                break;
            }

            case R.id.btn_disconnect: {
                final int position = (int) v.getTag();

//                DeleteSocialProfile mDeleteSocialProfile = new DeleteSocialProfile(hmSocialProfileModel.get(position).mSocialPlatform.getId());
                DeleteSocialProfileRequest mDeleteSocialProfileRequest = new DeleteSocialProfileRequest(hmSocialProfileModel.get(position).mSocialPlatform.getId());

                getSpiceManager().execute(mDeleteSocialProfileRequest, new RequestListener<Response>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {

                    }

                    @Override
                    public void onRequestSuccess(Response response) {
                        if (response.result) {
                            new DeleteSocialProfile().execute(position);
                        }
                    }
                });
                break;
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.Values.RC_INSTAGRAM) {
            if (resultCode == Activity.RESULT_OK) {
                addBundletoList(data.getBundleExtra(Constants.Keys.KEY_BUNDLE), instagramViewPosition, Constants.Types.FIELD_INSTAGRAM);
            }
        } else if (requestCode == Constants.Values.RC_LINKEDIN) {
            if (resultCode == Activity.RESULT_OK) {
                addBundletoList(data.getBundleExtra(Constants.Keys.KEY_BUNDLE), linkedInViewPosition, Constants.Types.FIELD_LINKEDIN);
            }
        } else if (requestCode == Constants.Values.RC_GPLUS_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                mGoogleApiClient.connect();
            }
        } else if (isFbSync) {
            super.onActivityResult(requestCode, resultCode, data);
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void loginFacebook() {
        isFbSync = true;
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
    }

    private void loginGPlus() {
        mGoogleApiClient.connect();
    }

    private void loginTwitter(final long platformID) {
        mTwitterAuthClient.authorize(getActivity(), new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = result.data;
                Bundle args = new Bundle();
                args.putLong(Constants.Keys.KEY_SOCIAL_PLATFORM_ID, platformID);
                args.putString(Constants.Keys.KEY_PLATFORM_ID, session.getUserName());
                log(TAG, "twitter user name " + session.getUserName());
                args.putString(Constants.Keys.KEY_USER_PLATFORM_USERNAME, session.getUserName());
                args.putString(Constants.Keys.KEY_PLATFORM_SECRET, session.getAuthToken().secret);
                args.putString(Constants.Keys.KEY_PLATFORM_TOKEN, session.getAuthToken().token);
                addBundletoList(args, twitterViewPosition, Constants.Types.FIELD_INSTAGRAM);
            }

            @Override
            public void failure(TwitterException e) {

            }
        });
    }

    private void loginInstagram(long instagramId) {
        Router.startInstagramLoginActivity(getActivity(), Constants.Values.RC_INSTAGRAM, instagramId);
    }

    private void loginLinkedIn(long linkedInId) {
        Router.startLinkendInLoginActivity(getActivity(), Constants.Values.RC_LINKEDIN, linkedInId);
    }


    private void addViews() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        int size = hmSocialProfileModel.size();
        llViewContainer.removeAllViews();
        for (int i = 0; i < size; i++) {
            View view = inflater.inflate(R.layout.item_profile_social_edit, llViewContainer, false);
            ViewHolder vh = new ViewHolder();
            vh.rlContainer = (RelativeLayout) view.findViewById(R.id.rl_social_container);
            vh.etFieldValue = (EditText) view.findViewById(R.id.et_field_value);
            vh.etFieldBaseValue = (EditText) view.findViewById(R.id.et_field_base_value);
            vh.tvFieldName = (TextView) view.findViewById(R.id.tv_field_name);
            vh.tvFieldValue = (TextView) view.findViewById(R.id.tv_field_value);
            vh.tvConnectedAs = (TextView) view.findViewById(R.id.tv_connected_as);
            vh.btnAdd = (Button) view.findViewById(R.id.btn_add);
            vh.btnFb = (Button) view.findViewById(R.id.btn_facebook_login);
            vh.btnGplus = (Button) view.findViewById(R.id.btn_g_plus_login);
            vh.btnInstagram = (Button) view.findViewById(R.id.btn_instagram_login);
            vh.btnLinkedIn = (Button) view.findViewById(R.id.btn_linkedin_login);
            vh.btnTwitter = (Button) view.findViewById(R.id.btn_twitter_login);
            vh.btnDisconnect = (Button) view.findViewById(R.id.btn_disconnect);
            vh.btnShare = (Button) view.findViewById(R.id.btn_share);
            vh.pbDisconnectSocialProfile = (ProgressBar) view.findViewById(R.id.pb_delete_social);
            vh.rlEditContainer = (RelativeLayout) view.findViewById(R.id.rl_social_edit_container);
            vh.rlEditContainerInner = (RelativeLayout) view.findViewById(R.id.rl_social_edit_container_inner);
            vh.tvFieldValue.setOnClickListener(this);
            vh.btnAdd.setOnClickListener(this);
            vh.btnTwitter.setOnClickListener(this);
            vh.btnGplus.setOnClickListener(this);
            vh.btnInstagram.setOnClickListener(this);
            vh.btnFb.setOnClickListener(this);
            vh.btnLinkedIn.setOnClickListener(this);
            vh.btnShare.setOnClickListener(this);
            vh.btnDisconnect.setOnClickListener(this);
            viewHolderArrayList.add(vh);
            llViewContainer.addView(view);
        }
    }

    private void setViewContent() {
        int size = viewHolderArrayList.size();
        for (int i = 0; i < size; i++) {
            setUpViewContent(i);
        }
    }

    private void setUpViewContent(int position) {
        SocialProfileModel mSocialProfileModel = hmSocialProfileModel.get(position);
        ViewHolder mViewHolder = viewHolderArrayList.get(position);
        mViewHolder.tvFieldName.setText(convertKeyToLabel(mSocialProfileModel.mSocialPlatform.getPlatformName()));
        mViewHolder.rlEditContainerInner.setVisibility(View.GONE);
        mViewHolder.btnDisconnect.setVisibility(View.GONE);
        if (mSocialProfileModel.isAdded) {
            mViewHolder.tvConnectedAs.setVisibility(View.VISIBLE);
            log(TAG, mSocialProfileModel.mSocialProfile.getPlatform_username());
            mViewHolder.tvFieldValue.setText(mSocialProfileModel.mSocialProfile.getPlatform_username());
            mViewHolder.tvFieldValue.setVisibility(View.VISIBLE);
            mViewHolder.tvFieldValue.setTag(position);
            mViewHolder.btnShare.setVisibility(View.VISIBLE);
            mViewHolder.btnAdd.setVisibility(View.GONE);
        } else {
            mViewHolder.btnAdd.setTag(position);
            mViewHolder.btnAdd.setVisibility(View.VISIBLE);
            mViewHolder.btnShare.setVisibility(View.GONE);
            mViewHolder.tvConnectedAs.setVisibility(View.GONE);
            mViewHolder.tvFieldValue.setVisibility(View.GONE);
        }
        if (mSocialProfileModel.mViewType == Constants.Types.FIELD_GOOGLE) {
            googlePlusPosition = position;
            mViewHolder.btnGplus.setTag(position);
        } else if (mSocialProfileModel.mViewType == Constants.Types.FIELD_FACEBOOK) {
            fbViewPosition = position;
            mViewHolder.btnFb.setTag(position);
        } else if (mSocialProfileModel.mViewType == Constants.Types.FIELD_INSTAGRAM) {
            instagramViewPosition = position;
            mViewHolder.btnInstagram.setTag(position);
        } else if (mSocialProfileModel.mViewType == Constants.Types.FIELD_LINKEDIN) {
            linkedInViewPosition = position;
            mViewHolder.btnLinkedIn.setTag(position);
        } else if (mSocialProfileModel.mViewType == Constants.Types.FIELD_TWITTER) {
            twitterViewPosition = position;
            mViewHolder.btnTwitter.setTag(position);
        }
        mViewHolder.btnDisconnect.setTag(position);

    }

    private void openEditMode() {
        for (int i = 0; i < viewHolderArrayList.size(); i++) {
            setUpEditMode(i);
        }
        isEditModeOn = true;
        Intent iDisableSwipe = new Intent(getActivity().getResources().getString(R.string.intent_filter_edit_mode_enabled));
        iDisableSwipe.putExtra(Constants.Keys.KEY_EDIT_MODE_TOGGLE, false);
        log(TAG, "sending broadcast false");
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(iDisableSwipe);
        btnEditProfile.setBackgroundResource(R.drawable.btn_add);
    }


    public void setUpEditMode(int position) {
        setUpViewContent(position);
        ViewHolder mViewHolder = viewHolderArrayList.get(position);
        mViewHolder.btnGplus.setVisibility(View.GONE);
        mViewHolder.btnFb.setVisibility(View.GONE);
        mViewHolder.btnTwitter.setVisibility(View.GONE);
        mViewHolder.btnLinkedIn.setVisibility(View.GONE);
        mViewHolder.btnInstagram.setVisibility(View.GONE);
        mViewHolder.etFieldBaseValue.setVisibility(View.GONE);
        mViewHolder.etFieldValue.setVisibility(View.GONE);
        SocialProfileModel mSocialProfileModel = hmSocialProfileModel.get(position);
        if (mSocialProfileModel.isAdded) {
            mViewHolder.btnDisconnect.setVisibility(View.VISIBLE);
            mViewHolder.btnShare.setVisibility(View.GONE);
        } else {
            mViewHolder.rlEditContainerInner.setVisibility(View.VISIBLE);
            mViewHolder.btnAdd.setVisibility(View.GONE);
            if (mSocialProfileModel.mViewType == Constants.Types.FIELD_GOOGLE) {
                mViewHolder.btnGplus.setVisibility(View.VISIBLE);
            } else if (mSocialProfileModel.mViewType == Constants.Types.FIELD_FACEBOOK) {
                mViewHolder.btnFb.setVisibility(View.VISIBLE);
            } else if (mSocialProfileModel.mViewType == Constants.Types.FIELD_INSTAGRAM) {
                mViewHolder.btnInstagram.setVisibility(View.VISIBLE);
            } else if (mSocialProfileModel.mViewType == Constants.Types.FIELD_LINKEDIN) {
                mViewHolder.btnLinkedIn.setVisibility(View.VISIBLE);
            } else if (mSocialProfileModel.mViewType == Constants.Types.FIELD_TWITTER) {
                mViewHolder.btnTwitter.setVisibility(View.VISIBLE);
            } else {
                mViewHolder.etFieldBaseValue.setText(mSocialProfileModel.mSocialPlatform.getPlatformBaseUrl() + "/");
                mViewHolder.etFieldBaseValue.setVisibility(View.VISIBLE);
                mViewHolder.etFieldBaseValue.setEnabled(false);
                mViewHolder.etFieldValue.setVisibility(View.VISIBLE);
            }
        }
    }

    private void doSocialMediaLogin(int viewType, long platformId) {
        switch (viewType) {
            case Constants.Types.FIELD_FACEBOOK: {
                loginFacebook();
                break;
            }
            case Constants.Types.FIELD_INSTAGRAM: {
                loginInstagram(platformId);
                break;
            }
            case Constants.Types.FIELD_TWITTER: {
                loginTwitter(platformId);
                break;
            }
            case Constants.Types.FIELD_GOOGLE: {
                loginGPlus();
                break;
            }
            case Constants.Types.FIELD_LINKEDIN: {
                loginLinkedIn(platformId);
                break;
            }
        }
    }

    private void sendData() {
        for (int i = 0; i < viewHolderArrayList.size(); i++) {
            ViewHolder vh = viewHolderArrayList.get(i);
            SocialProfileModel socialProfileModel = hmSocialProfileModel.get(i);
            String value = vh.etFieldValue.getText().toString();
            if (socialProfileModel.mViewType == Constants.Types.FIELD_SOCIAL && value.length() != 0) {
                Bundle args = new Bundle();
                args.putString(Constants.Keys.KEY_PLATFORM_ID, value);
                args.putString(Constants.Keys.KEY_USER_PLATFORM_USERNAME, value);
                args.putLong(Constants.Keys.KEY_SOCIAL_PLATFORM_ID, socialProfileModel.mSocialPlatform.getId());
                bSocialProfileInfo.add(args);
            }
        }
        SocialRequestModel.List socialRequestModels = new SocialRequestModel.List();
        for (Bundle args : bSocialProfileInfo) {
            SocialRequestModel srm = new SocialRequestModel(args.getLong(Constants.Keys.KEY_SOCIAL_PLATFORM_ID, 0),
                    args.getString(Constants.Keys.KEY_PLATFORM_ID, null),
                    args.getString(Constants.Keys.KEY_PLATFORM_TOKEN, null),
                    args.getString(Constants.Keys.KEY_PLATFORM_PERMISSION, null),
                    args.getString(Constants.Keys.KEY_PLATFORM_SECRET, null),
                    args.getString(Constants.Keys.KEY_PLATFORM_EMAIL_ID, null),
                    args.getString(Constants.Keys.KEY_USER_PLATFORM_USERNAME, null));
            socialRequestModels.add(srm);
        }
        final SocialRequestModel.List copySocialRequestModels = socialRequestModels;
        SocialProfileRequest socialProfileRequest = new SocialProfileRequest(socialRequestModels);
        getSpiceManager().execute(socialProfileRequest, new RequestListener<Response>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(Response response) {
                new SaveSocialProfile().execute(copySocialRequestModels);
            }
        });
    }


    private void addBundletoList(Bundle args, int position, int viewType) {
        SocialProfileModel socialProfileModel = hmSocialProfileModel.get(position);
        SocialProfile newSocialProfile = new SocialProfile(0l, socialProfileModel.mSocialPlatform.getPlatformName(),
                args.getString(Constants.Keys.KEY_PLATFORM_ID), args.getString(Constants.Keys.KEY_USER_PLATFORM_USERNAME, null)
                , PrefUtils.getCurrentUserID());
        hmSocialProfileModel.remove(position);
        hmSocialProfileModel.put(position, new SocialProfileModel(newSocialProfile,
                socialProfileModel.mSocialPlatform, true, viewType));
        bSocialProfileInfo.add(args);
        setUpEditMode(position);
    }

    private void scrollToPosition(int position) {
        if (hmSocialProfileModel.get(position).mViewType == Constants.Types.FIELD_SOCIAL) {
            viewHolderArrayList.get(position).etFieldValue.requestFocus();
        } else {
            viewHolderArrayList.get(position).rlEditContainerInner.requestFocus();
        }
    }


    private String convertKeyToLabel(String key) {
        String str = key.replace("_", " ");
        str = str.toLowerCase();
        char ch = str.charAt(0);
        str = ((char) (ch - 32)) + str.substring(1);
        int position = str.indexOf(" ");
        while (position != -1) {
            ch = str.charAt(position + 1);
            str = str.substring(0, position + 1) + (char) (ch - 32) + str.substring(position + 2);
            position = str.indexOf(" ", position + 1);
        }
        return str;
    }

    private BroadcastReceiver brUsr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra(Constants.Keys.KEY_USER_PROFILE_TYPE, 0);
            if (type == Constants.Types.PROFILE_SOCIAL) {
                new LoadUser().execute();
            }
        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Bundle args = new Bundle();
            args.putLong(Constants.Keys.KEY_SOCIAL_PLATFORM_ID, hmSocialProfileModel.get(googlePlusPosition).mSocialPlatform.getId());
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            log(TAG, "id is " + (currentPerson == null));
            if (currentPerson != null) {
                args.putString(Constants.Keys.KEY_USER_PLATFORM_USERNAME, currentPerson.getDisplayName());
                args.putString(Constants.Keys.KEY_PLATFORM_ID, currentPerson.getId());
            }
            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            if (email != null) {
                args.putString(Constants.Keys.KEY_PLATFORM_EMAIL_ID, email);
            }
            addBundletoList(args, googlePlusPosition, Constants.Types.FIELD_GOOGLE);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(getActivity(), Constants.Values.RC_GPLUS_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        fbAccessToken = loginResult.getAccessToken().getToken();
        GraphRequest.newMeRequest(loginResult.getAccessToken(), this).executeAsync();
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(FacebookException error) {
        error.printStackTrace();
    }

    @Override
    public void onCompleted(JSONObject object, GraphResponse response) {
        Bundle args = new Bundle();
        args.putLong(Constants.Keys.KEY_SOCIAL_PLATFORM_ID, hmSocialProfileModel.get(fbViewPosition).mSocialPlatform.getId());
        try {
            log(TAG, object.toString());
            args.putString(Constants.Keys.KEY_PLATFORM_TOKEN, fbAccessToken);
            args.putString(Constants.Keys.KEY_USER_PLATFORM_USERNAME, object.getString("name"));
            args.putString(Constants.Keys.KEY_PLATFORM_EMAIL_ID, object.getString("email"));
            args.putString(Constants.Keys.KEY_PLATFORM_ID, object.getString("id"));
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        args.putString(Constants.Keys.KEY_PLATFORM_PERMISSION, "email, public_profile");
        addBundletoList(args, fbViewPosition, Constants.Types.FIELD_FACEBOOK);
        isFbSync = false;
    }


    private class LoadUser extends AsyncTask<Integer, Void, HashMap<Integer, SocialProfileModel>> {
        @Override
        protected HashMap<Integer, SocialProfileModel> doInBackground(Integer... params) {
            HashMap<Integer, SocialProfileModel> hm = new HashMap<>();
            ArrayList<SocialPlatform> socialPlatforms = ((BaseActivity) CurrentUserSocialProfileEditFragment.this.getActivity()).
                    getSocialPlatforms();
            HashMap<String, SocialPlatform> hmNameToPlatform = new HashMap<>();
            for (SocialPlatform socialPlatform : socialPlatforms) {
                log(TAG, socialPlatform.getPlatformName());
                hmNameToPlatform.put(socialPlatform.getPlatformName(), socialPlatform);
            }
            ArrayList<SocialProfile> socialProfiles = ((BaseActivity) CurrentUserSocialProfileEditFragment.this.getActivity()).
                    getCurrentUserSocialProfiles();
            int counter = 0;
            for (SocialProfile socialProfile : socialProfiles) {
                log(TAG, "user is on " + socialProfile.getSocial_platform() + " as " + socialProfile.getPlatform_id());
                log(TAG,"size " + hmNameToPlatform.size());
                if (hmNameToPlatform.containsKey(socialProfile.getSocial_platform())) {
                    String keyLowerCase = socialProfile.getSocial_platform().toLowerCase();
                    if (keyLowerCase.contains("google")) {
                        hm.put(counter++, new SocialProfileModel
                                (socialProfile, hmNameToPlatform.get(socialProfile.getSocial_platform()), true, Constants.Types.FIELD_GOOGLE));
                    } else if (keyLowerCase.contains("linkedin")) {
                        hm.put(counter++, new SocialProfileModel
                                (socialProfile, hmNameToPlatform.get(socialProfile.getSocial_platform()), true, Constants.Types.FIELD_LINKEDIN));
                    } else if (keyLowerCase.contains("facebook")) {
                        hm.put(counter++, new SocialProfileModel
                                (socialProfile, hmNameToPlatform.get(socialProfile.getSocial_platform()), true, Constants.Types.FIELD_FACEBOOK));
                    } else if (keyLowerCase.contains("twitter")) {
                        hm.put(counter++, new SocialProfileModel
                                (socialProfile, hmNameToPlatform.get(socialProfile.getSocial_platform()), true, Constants.Types.FIELD_TWITTER));
                    } else if (keyLowerCase.contains("instagram")) {
                        hm.put(counter++, new SocialProfileModel
                                (socialProfile, hmNameToPlatform.get(socialProfile.getSocial_platform()), true, Constants.Types.FIELD_INSTAGRAM));
                    } else {
                        hm.put(counter++, new SocialProfileModel
                                (socialProfile, hmNameToPlatform.get(socialProfile.getSocial_platform()), true, Constants.Types.FIELD_SOCIAL));
                    }
                    hmNameToPlatform.remove(socialProfile.getSocial_platform());
                }
            }
            for (SocialPlatform socialPlatform : socialPlatforms) {
                if (hmNameToPlatform.containsKey(socialPlatform.getPlatformName())) {
                    String keyLowerCase = socialPlatform.getPlatformName().toLowerCase();
                    if (keyLowerCase.contains("facebook")) {
                        hm.put(counter++, new SocialProfileModel(socialPlatform, false, Constants.Types.FIELD_FACEBOOK));
                    } else if (keyLowerCase.contains("google")) {
                        hm.put(counter++, new SocialProfileModel(socialPlatform, false, Constants.Types.FIELD_GOOGLE));
                    } else if (keyLowerCase.contains("twitter")) {
                        hm.put(counter++, new SocialProfileModel(socialPlatform, false, Constants.Types.FIELD_TWITTER));
                    } else if (keyLowerCase.contains("instagram")) {
                        hm.put(counter++, new SocialProfileModel(socialPlatform, false, Constants.Types.FIELD_INSTAGRAM));
                    } else if (keyLowerCase.contains("linkedin")) {
                        hm.put(counter++, new SocialProfileModel(socialPlatform, false, Constants.Types.FIELD_LINKEDIN));
                    } else {
                        hm.put(counter++, new SocialProfileModel(socialPlatform, false, Constants.Types.FIELD_SOCIAL));
                    }
                }
            }

            return hm;
        }

        @Override
        protected void onPostExecute(HashMap<Integer, SocialProfileModel> hm) {
            hmSocialProfileModel.clear();
            hmSocialProfileModel.putAll(hm);
            if (viewHolderArrayList.size() != hmSocialProfileModel.size()) {
                addViews();
            }
            setViewContent();
            if(isEditModeOn) {
                Intent iEnableSwipe = new Intent(getActivity().getResources().getString(R.string.intent_filter_edit_mode_enabled));
                iEnableSwipe.putExtra(Constants.Keys.KEY_EDIT_MODE_TOGGLE, true);
                log(TAG, "sending broadcast true");
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(iEnableSwipe);
            }
            isEditModeOn = false;
            btnEditProfile.setBackgroundResource(R.drawable.edit_pencil_contag);
            if(isComingFromNotification) {
                log(TAG, "opening edit mode");
                openEditMode();
                for (int position = 0; position < hmSocialProfileModel.size(); position++) {
                    SocialProfileModel socialProfileModel = hmSocialProfileModel.get(position);
                    log(TAG, socialProfileModel.mSocialPlatform.getPlatformName());
                    if(socialProfileModel.mSocialPlatform.getPlatformName().equalsIgnoreCase(fieldName)) {
                        log(TAG, "position found " + position);
                        scrollToPosition(position);
                        break;
                    }
                }
                isComingFromNotification = false;
            }
        }
    }

    private class SaveSocialProfile extends AsyncTask<SocialRequestModel.List, Void, Boolean> {
        @Override
        protected Boolean doInBackground(SocialRequestModel.List... params) {
            if (params.length > 0) {
                ArrayList<SocialRequestModel> socialRequestModels = params[0];
                DaoSession session = ((ContagApplication) getActivity().getApplicationContext()).getDaoSession();
                ContagContagDao ccDao = session.getContagContagDao();
                ContagContag cc = ccDao.queryBuilder().where(ContagContagDao.Properties.Id.eq(PrefUtils.getCurrentUserID())).
                        list().get(0);
                for (SocialRequestModel socialRequestModel : socialRequestModels) {
                    SocialProfile socialProfile = new SocialProfile(socialRequestModel.socialPlatformId);
                    socialProfile.setPlatform_id(socialRequestModel.platformId);
                    socialProfile.setPlatform_username(socialRequestModel.platformUsername);
                    socialProfile.setContagContag(cc);
                    SocialPlatformDao socialPlatformDao = session.getSocialPlatformDao();
                    String socialPlatformName = socialPlatformDao.queryBuilder().
                            where(SocialPlatformDao.Properties.Id.eq(socialRequestModel.socialPlatformId)).list().get(0).getPlatformName();
                    socialProfile.setSocial_platform(socialPlatformName);
                    SocialProfileDao socialProfileDao = session.getSocialProfileDao();
                    log(TAG, "social profile id = " + socialProfile.getId());
                    socialProfileDao.insertOrReplace(socialProfile);
                }
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.d(TAG, "socialProfileUpdated");
            bSocialProfileInfo.clear();
            new LoadUser().execute();
        }
    }

    private class DeleteSocialProfile extends AsyncTask<Integer, Void, Void> {

        private int position;

        @Override
        protected Void doInBackground(Integer... params) {
            position = params[0];
            ((BaseActivity) getActivity()).deleteSocialProfileFromId(hmSocialProfileModel.get(position).mSocialProfile.getId());
            ArrayList<SocialProfile> socialProfiles = ((BaseActivity) getActivity()).getSocialProfiles(PrefUtils.getCurrentUserID());
            log(TAG, socialProfiles.size() + "");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (position == fbViewPosition) {
                LoginManager.getInstance().logOut();
            } else if (position == googlePlusPosition && mGoogleApiClient.isConnected()) {
                Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
            }
            SocialProfileModel mSocialProfileModel = hmSocialProfileModel.get(position);
            SocialProfileModel newSocialProfileModel = new SocialProfileModel(mSocialProfileModel.mSocialPlatform, false, mSocialProfileModel.mViewType);
            viewHolderArrayList.get(position).btnDisconnect.setVisibility(View.GONE);
            hmSocialProfileModel.remove(position);
            hmSocialProfileModel.put(position, newSocialProfileModel);
            log(TAG, "calling edit mode " + hmSocialProfileModel.get(position).mSocialPlatform.getPlatformName()
                    + " " + (hmSocialProfileModel.get(position).mSocialProfile == null));
            setUpEditMode(position);
        }
    }


    private class ViewHolder {
        public TextView tvFieldName;
        public TextView tvFieldValue;
        public TextView tvConnectedAs;
        public EditText etFieldBaseValue;
        public EditText etFieldValue;
        public Button btnDisconnect;
        public Button btnAdd;
        public Button btnShare;
        public Button btnFb;
        public Button btnGplus;
        public Button btnInstagram;
        public Button btnLinkedIn;
        public Button btnTwitter;
        public RelativeLayout rlEditContainer;
        public RelativeLayout rlEditContainerInner;
        public RelativeLayout rlContainer;
        public ProgressBar pbDisconnectSocialProfile;
    }
}
