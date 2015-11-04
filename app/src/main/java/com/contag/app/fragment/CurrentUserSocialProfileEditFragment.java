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
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.activity.BaseActivity;
import com.contag.app.config.Constants;
import com.contag.app.config.Router;
import com.contag.app.model.ProfileModel;
import com.contag.app.model.SocialPlatform;
import com.contag.app.model.SocialProfile;
import com.contag.app.util.DeviceUtils;
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
    private HashMap<Integer, ProfileModel> hmProfileModel;
    private ArrayList<ViewHolder> viewHolderArrayList;
    private LinearLayout llViewContainer;
    private CallbackManager mCallbackManager;
    private boolean isFbSync = false;
    private TwitterAuthClient mTwitterAuthClient;
    private GoogleApiClient mGoogleApiClient;
    private Button btnEditProfile;
    private boolean isEditModeOn;

    public static CurrentUserSocialProfileEditFragment newInstance() {
        CurrentUserSocialProfileEditFragment epdf = new CurrentUserSocialProfileEditFragment();
        Bundle args = new Bundle();
        epdf.setArguments(args);
        return epdf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        hmProfileModel = new HashMap<>();
        viewHolderArrayList = new ArrayList<>();
        llViewContainer = (LinearLayout) view.findViewById(R.id.ll_profile_container);
        btnEditProfile = (Button) view.findViewById(R.id.btn_edit_profile);
        btnEditProfile.setOnClickListener(this);
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
//            case R.id.btn_edit_profile: {
//                if (!DeviceUtils.isInternetConnected(getActivity())) {
//                    showToast("Sorry there is no internet.");
//                    return;
//                }
//                int tag = (int) v.getTag();
//                ViewHolder vh = viewHolderArrayList.get(tag);
//                ProfileModel pm = hmProfileModel.get(tag);
//                if (pm.fieldType == Constants.Types.FIELD_STRING) {
//                    vh.etFieldValue.setText(vh.tvFieldValue.getText().toString());
//                    vh.etFieldValue.setVisibility(View.VISIBLE);
//                } else if (pm.fieldType == Constants.Types.FIELD_GOOGLE) {
//                    vh.btnGplus.setVisibility(View.VISIBLE);
//                } else if (pm.fieldType == Constants.Types.FIELD_FACEBOOK) {
//                    vh.btnFb.setVisibility(View.VISIBLE);
//                } else if (pm.fieldType == Constants.Types.FIELD_TWITTER) {
//                    vh.btnTwitter.setVisibility(View.VISIBLE);
//                } else if (pm.fieldType == Constants.Types.FIELD_INSTAGRAM) {
//                    vh.btnInstagram.setVisibility(View.VISIBLE);
//                } else if (pm.fieldType == Constants.Types.FIELD_LINKEDIN) {
//                    vh.btnLinkedIn.setVisibility(View.VISIBLE);
//                }
//                vh.tvFieldValue.setVisibility(View.GONE);
//                v.setVisibility(View.GONE);
//                break;
//            }

            case R.id.btn_edit_profile: {
                if (!DeviceUtils.isInternetConnected(getActivity())) {
                    showToast("Sorry there is no internet.");
                    return;
                }
                if (!isEditModeOn) {
                    openEditMode();
                } else {
                    sendData();
                }
                break;
            }

            case R.id.btn_update: {
                int tag = (int) v.getTag();
                ViewHolder vh = viewHolderArrayList.get(tag);
                String value = vh.etFieldValue.getText().toString();
                int index = value.lastIndexOf("/");
                if (index == -1 || index == value.length() - 1) {
                    showToast("Enter a valid id");
                    return;
                }
                Bundle args = new Bundle();
                args.putString(Constants.Keys.KEY_PLATFORM_ID, value.substring(index + 1));
                args.putString(Constants.Keys.KEY_SOCIAL_PLATFORM_NAME, vh.tvFieldLabel.getText().toString());
                new SendSocialNetworkData().execute(args);
                vh.pbUpdate.setVisibility(View.VISIBLE);
                break;
            }

            case R.id.btn_facebook_login: {
                String text = ((Button) v).getText().toString();
                if (!getActivity().getResources().getString(R.string.fb_unsync_msg).equalsIgnoreCase(text)) {
                    loginFacebook();
                }
                break;
            }

            case R.id.btn_g_plus_login: {
                loginGPlus();
                break;
            }

            case R.id.btn_twitter_login: {
                loginTwitter();
                break;
            }

            case R.id.btn_linkedin_login: {
                loginLinkedIn();
                break;
            }

            case R.id.btn_instagram_login: {
                loginInstagram();
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.Values.RC_INSTAGRAM) {
            if (resultCode == Activity.RESULT_OK) {
                Router.updateSocialProfile(CurrentUserSocialProfileEditFragment.this.getActivity(),
                        data.getBundleExtra(Constants.Keys.KEY_BUNDLE));
            }
        } else if (requestCode == Constants.Values.RC_LINKEDIN) {
            if (resultCode == Activity.RESULT_OK) {
                Router.updateSocialProfile(CurrentUserSocialProfileEditFragment.this.getActivity(),
                        data.getBundleExtra(Constants.Keys.KEY_BUNDLE));
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

    private void loginTwitter() {
        mTwitterAuthClient.authorize(getActivity(), new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = result.data;
                Bundle args = new Bundle();
                args.putString(Constants.Keys.KEY_SOCIAL_PLATFORM_NAME, "twitter");
                args.putString(Constants.Keys.KEY_PLATFORM_ID, session.getUserName());
                args.putString(Constants.Keys.KEY_PLATFORM_SECRET, session.getAuthToken().secret);
                args.putString(Constants.Keys.KEY_PLATFORM_TOKEN, session.getAuthToken().token);
                new SendSocialNetworkData().execute(args);
            }

            @Override
            public void failure(TwitterException e) {

            }
        });
    }

    private void loginInstagram() {
        Router.startInstagramLoginActivity(getActivity(), Constants.Values.RC_INSTAGRAM);
    }

    private void loginLinkedIn() {
        Router.startLinkendInLoginActivity(getActivity(), Constants.Values.RC_LINKEDIN);
    }

    private void sendData() {
        for (int i = 0; i < viewHolderArrayList.size(); i++) {
            ViewHolder vh = viewHolderArrayList.get(i);
            ProfileModel pm = hmProfileModel.get(i);
            String value = vh.etFieldValue.getText().toString();
            int index = value.lastIndexOf("/");
            if (index == -1 || index == value.length() - 1) {
                showToast("Enter a valid id");
                return;
            }
            Bundle args = new Bundle();
            args.putString(Constants.Keys.KEY_PLATFORM_ID, value.substring(index + 1));
            args.putString(Constants.Keys.KEY_SOCIAL_PLATFORM_NAME, vh.tvFieldLabel.getText().toString());
            new SendSocialNetworkData().execute(args);
        }

    }

    private void openEditMode() {
        for (int i = 0; i < viewHolderArrayList.size(); i++) {
            ViewHolder vh = viewHolderArrayList.get(i);
            ProfileModel pm = hmProfileModel.get(i);
            if (pm.fieldType == Constants.Types.FIELD_STRING) {
                vh.etFieldValue.setText(vh.tvFieldValue.getText().toString());
                vh.etFieldValue.setVisibility(View.VISIBLE);
            } else if (pm.fieldType == Constants.Types.FIELD_GOOGLE) {
                if (vh.tvFieldValue.getVisibility() == View.VISIBLE) {
                    vh.etFieldValue.setText(vh.tvFieldValue.getText().toString());
                    vh.etFieldValue.setVisibility(View.VISIBLE);
                } else {
                    vh.btnGplus.setVisibility(View.VISIBLE);
                }
            } else if (pm.fieldType == Constants.Types.FIELD_FACEBOOK) {
                if (vh.tvFieldValue.getVisibility() == View.VISIBLE) {
                    vh.etFieldValue.setText(vh.tvFieldValue.getText().toString());
                    vh.etFieldValue.setVisibility(View.VISIBLE);
                } else {
                    vh.btnFb.setVisibility(View.VISIBLE);
                }
            } else if (pm.fieldType == Constants.Types.FIELD_TWITTER) {
                if (vh.tvFieldValue.getVisibility() == View.VISIBLE) {
                    vh.etFieldValue.setText(vh.tvFieldValue.getText().toString());
                    vh.etFieldValue.setVisibility(View.VISIBLE);
                } else {
                    vh.btnTwitter.setVisibility(View.VISIBLE);
                }
            } else if (pm.fieldType == Constants.Types.FIELD_INSTAGRAM) {
                if (vh.tvFieldValue.getVisibility() == View.VISIBLE) {
                    vh.etFieldValue.setText(vh.tvFieldValue.getText().toString());
                    vh.etFieldValue.setVisibility(View.VISIBLE);
                } else {
                    vh.btnInstagram.setVisibility(View.VISIBLE);
                }
            } else if (pm.fieldType == Constants.Types.FIELD_LINKEDIN) {
                if (vh.tvFieldValue.getVisibility() == View.VISIBLE) {
                    vh.etFieldValue.setText(vh.tvFieldValue.getText().toString());
                    vh.etFieldValue.setVisibility(View.VISIBLE);
                } else {
                    vh.btnLinkedIn.setVisibility(View.VISIBLE);
                }
            }
            vh.tvFieldValue.setVisibility(View.GONE);
            vh.btnAdd.setVisibility(View.GONE);
            vh.btnShare.setVisibility(View.GONE);
        }
        isEditModeOn = true;
        btnEditProfile.setBackgroundResource(R.drawable.btn_add);
    }

    private void addViews() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        int size = hmProfileModel.size();
        llViewContainer.removeAllViews();
        for (int i = 0; i < size; i++) {
            View view = inflater.inflate(R.layout.item_profile_social_edit, llViewContainer, false);
            ViewHolder vh = new ViewHolder();
            vh.etFieldValue = (EditText) view.findViewById(R.id.et_field_value);
            vh.tvFieldLabel = (TextView) view.findViewById(R.id.tv_field_label);
            vh.tvFieldValue = (TextView) view.findViewById(R.id.tv_field_value);
            vh.btnAdd = (Button) view.findViewById(R.id.btn_add);
            vh.btnFb = (Button) view.findViewById(R.id.btn_facebook_login);
//            vh.btnFb.setFragment(this);
//            vh.btnFb.setReadPermissions(Arrays.asList("public_profile"));
//            vh.btnFb.registerCallback(mCallbackManager, this);
            vh.btnGplus = (Button) view.findViewById(R.id.btn_g_plus_login);
            vh.btnInstagram = (Button) view.findViewById(R.id.btn_instagram_login);
            vh.btnLinkedIn = (Button) view.findViewById(R.id.btn_linkedin_login);
            vh.btnTwitter = (Button) view.findViewById(R.id.btn_twitter_login);
            vh.btnShare = (Button) view.findViewById(R.id.btn_share);
            vh.btnAdd.setOnClickListener(this);
            vh.btnTwitter.setOnClickListener(this);
            vh.btnGplus.setOnClickListener(this);
            vh.btnInstagram.setOnClickListener(this);
            vh.btnFb.setOnClickListener(this);
            vh.btnLinkedIn.setOnClickListener(this);
            vh.btnShare.setOnClickListener(this);
            viewHolderArrayList.add(vh);
            llViewContainer.addView(view);
        }
    }

    private void setViewContent() {
        for (int i = 0; i < hmProfileModel.size(); i++) {
            ViewHolder vh = viewHolderArrayList.get(i);
            vh.btnAdd.setTag(i);
            vh.btnFb.setVisibility(View.GONE);
            vh.btnInstagram.setVisibility(View.GONE);
            vh.btnTwitter.setVisibility(View.GONE);
            vh.btnGplus.setVisibility(View.GONE);
            vh.btnLinkedIn.setVisibility(View.GONE);
            vh.tvFieldValue.setVisibility(View.VISIBLE);
            vh.etFieldValue.setVisibility(View.GONE);
            vh.btnAdd.setVisibility(View.GONE);
            if (hmProfileModel.get(i).fieldType == Constants.Types.FIELD_STRING) {
                vh.etFieldValue.setInputType(hmProfileModel.get(i).inputType);
            }
            vh.tvFieldLabel.setText(convertKeyToLabel(hmProfileModel.get(i).key));
            if (hmProfileModel.get(i).value != null && (String.valueOf(hmProfileModel.get(i).value)).length() != 0) {
                if (hmProfileModel.get(i).fieldType == Constants.Types.FIELD_STRING) {
                    vh.tvFieldValue.setText(String.valueOf(hmProfileModel.get(i).value));
                    vh.btnShare.setVisibility(View.VISIBLE);
                } else {
                    final int fieldType = hmProfileModel.get(i).fieldType;
                    vh.etFieldValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                                 if(hasFocus) {
                                     switch (fieldType) {
                                         case Constants.Types.FIELD_FACEBOOK: {
                                             loginFacebook();
                                             break;
                                         }
                                         case Constants.Types.FIELD_INSTAGRAM: {
                                             loginInstagram();
                                             break;
                                         }
                                         case Constants.Types.FIELD_TWITTER: {
                                             loginTwitter();
                                             break;
                                         }
                                         case Constants.Types.FIELD_GOOGLE: {
                                             loginGPlus();
                                             break;
                                         }
                                         case Constants.Types.FIELD_LINKEDIN: {
                                             loginLinkedIn();
                                             break;
                                         }
                                     }
                                 }
                        }
                    });
                    String value = hmProfileModel.get(i).value.toString();
                    if (value.lastIndexOf("/") != value.length() - 1) {
                        vh.tvFieldValue.setText(value);
                        vh.btnShare.setVisibility(View.VISIBLE);
                    } else {
                        vh.tvFieldValue.setVisibility(View.GONE);
                        vh.btnAdd.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                vh.tvFieldValue.setVisibility(View.GONE);
                vh.btnAdd.setVisibility(View.VISIBLE);
            }
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
            args.putString(Constants.Keys.KEY_SOCIAL_PLATFORM_NAME, "google");
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            if (currentPerson != null) {
                args.putString(Constants.Keys.KEY_PLATFORM_ID, currentPerson.getId());
            }
            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            if (email != null) {
                args.putString(Constants.Keys.KEY_PLATFORM_EMAIL_ID, email);
            }
            new SendSocialNetworkData().execute(args);
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
        args.putString(Constants.Keys.KEY_SOCIAL_PLATFORM_NAME, "facebook");
        try {
            args.putString(Constants.Keys.KEY_PLATFORM_EMAIL_ID, object.getString("email"));
            args.putString(Constants.Keys.KEY_PLATFORM_ID, object.getString("id"));
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        args.putString(Constants.Keys.KEY_PLATFORM_PERMISSION, "email, public_profile");
        isFbSync = false;
        new SendSocialNetworkData().execute(args);
    }


    private class LoadUser extends AsyncTask<Integer, Void, HashMap<Integer, ProfileModel>> {
        @Override
        protected HashMap<Integer, ProfileModel> doInBackground(Integer... params) {
            HashMap<Integer, ProfileModel> hm = new HashMap<>();
            ArrayList<SocialPlatform> socialPlatforms = ((BaseActivity) CurrentUserSocialProfileEditFragment.this.getActivity()).
                    getSocialPlatforms();
            HashMap<String, String> hmNameToUrl = new HashMap<>();
            for (SocialPlatform sp : socialPlatforms) {
                hmNameToUrl.put(sp.getPlatformName(), sp.getPlatformBaseUrl());
            }
            ArrayList<SocialProfile> socialProfiles = ((BaseActivity) CurrentUserSocialProfileEditFragment.this.getActivity()).
                    getCurrentUserSocialProfiles();
            int counter = 0;
            for (SocialProfile sp : socialProfiles) {
                if (hmNameToUrl.containsKey(sp.getSocial_platform())) {
                    String keyLowerCase = sp.getSocial_platform().toLowerCase();
                    if (keyLowerCase.contains("google")) {
                        hm.put(counter++, new ProfileModel(sp.getSocial_platform(),
                                hmNameToUrl.get(sp.getSocial_platform()) + "/" + sp.getPlatform_id() + "/posts",
                                Constants.Types.FIELD_GOOGLE));
                    } else if (keyLowerCase.contains("linkedin")) {
                        hm.put(counter++, new ProfileModel(sp.getSocial_platform(),
                                hmNameToUrl.get(sp.getSocial_platform()) + sp.getPlatform_id(),
                                Constants.Types.FIELD_LINKEDIN));
                    } else if (keyLowerCase.contains("facebook")) {
                        hm.put(counter++, new ProfileModel(sp.getSocial_platform(),
                                hmNameToUrl.get(sp.getSocial_platform()) + "/" + sp.getPlatform_id(),
                                Constants.Types.FIELD_FACEBOOK));
                    } else if (keyLowerCase.contains("twitter")) {
                        hm.put(counter++, new ProfileModel(sp.getSocial_platform(),
                                hmNameToUrl.get(sp.getSocial_platform()) + "/" + sp.getPlatform_id(),
                                Constants.Types.FIELD_TWITTER));
                    } else if (keyLowerCase.contains("instagram")) {
                        hm.put(counter++, new ProfileModel(sp.getSocial_platform(),
                                hmNameToUrl.get(sp.getSocial_platform()) + "/" + sp.getPlatform_id(),
                                Constants.Types.FIELD_INSTAGRAM));
                    } else {
                        hm.put(counter++, new ProfileModel(sp.getSocial_platform(),
                                hmNameToUrl.get(sp.getSocial_platform()) + "/" + sp.getPlatform_id(),
                                Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_TEXT));
                    }
                    hmNameToUrl.remove(sp.getSocial_platform());
                }
            }
            for (SocialPlatform sp : socialPlatforms) {
                if (hmNameToUrl.containsKey(sp.getPlatformName())) {
                    String keyLowerCase = sp.getPlatformName().toLowerCase();
                    if (keyLowerCase.contains("facebook")) {
                        hm.put(counter++, new ProfileModel(sp.getPlatformName(), sp.getPlatformBaseUrl() + "/", Constants.Types.FIELD_FACEBOOK));
                    } else if (keyLowerCase.contains("google")) {
                        hm.put(counter++, new ProfileModel(sp.getPlatformName(), sp.getPlatformBaseUrl() + "/", Constants.Types.FIELD_GOOGLE));
                    } else if (keyLowerCase.contains("twitter")) {
                        hm.put(counter++, new ProfileModel(sp.getPlatformName(), sp.getPlatformBaseUrl() + "/", Constants.Types.FIELD_TWITTER));
                    } else if (keyLowerCase.contains("instagram")) {
                        hm.put(counter++, new ProfileModel(sp.getPlatformName(), sp.getPlatformBaseUrl() + "/", Constants.Types.FIELD_INSTAGRAM));
                    } else if (keyLowerCase.contains("linkedin")) {
                        hm.put(counter++, new ProfileModel(sp.getPlatformName(), sp.getPlatformBaseUrl() + "/", Constants.Types.FIELD_LINKEDIN));
                    } else {
                        hm.put(counter++, new ProfileModel(sp.getPlatformName(), sp.getPlatformBaseUrl() + "/", Constants.Types.FIELD_STRING, InputType.TYPE_CLASS_TEXT));
                    }
                }
            }

            return hm;
        }

        @Override
        protected void onPostExecute(HashMap<Integer, ProfileModel> hm) {
            hmProfileModel.clear();
            hmProfileModel.putAll(hm);
            if (viewHolderArrayList.size() != hmProfileModel.size()) {
                addViews();
            }
            setViewContent();
            isEditModeOn = false;
            btnEditProfile.setBackgroundResource(R.drawable.edit_pencil_contag);
        }
    }


    private class SendSocialNetworkData extends AsyncTask<Bundle, Void, Bundle> {
        @Override
        protected Bundle doInBackground(Bundle... params) {
            if (params.length > 0) {
                Bundle args = params[0];
                String platformName = args.getString(Constants.Keys.KEY_SOCIAL_PLATFORM_NAME);
                SocialPlatform sp = (CurrentUserSocialProfileEditFragment.this.getBaseActivity())
                        .getPlatformFromName(platformName);
                long id = sp.getId();
                args.remove(Constants.Keys.KEY_SOCIAL_PLATFORM_NAME);
                args.putLong(Constants.Keys.KEY_SOCIAL_PLATFORM_ID, id);
                return args;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            Router.updateSocialProfile(getActivity(), bundle);
        }
    }


    private class ViewHolder {
        public TextView tvFieldLabel;
        public TextView tvFieldValue;
        public EditText etFieldValue;
        public Button btnAdd;
        public Button btnShare;
        public Button btnFb;
        public Button btnGplus;
        public Button btnInstagram;
        public Button btnLinkedIn;
        public Button btnTwitter;
        public ProgressBar pbUpdate;
    }
}
