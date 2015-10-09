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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by tanay on 28/9/15.
 */
public class CurrentUserSocialProfileEditFragment extends BaseFragment implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        FacebookCallback<LoginResult>, GraphRequest.GraphJSONObjectCallback {

    private HashMap<Integer, ProfileModel> hmProfileModel;
    private boolean isListDrawn = false;
    private ArrayList<ViewHolder> viewHolderArrayList;
    private LinearLayout llViewContainer;
    public static final String TAG = CurrentUserSocialProfileEditFragment.class.getName();
    private CallbackManager mCallbackManager;
    private boolean isGoogleSync = false, isFbSync = false;
    private TwitterAuthClient mTwitterAuthClient;
    private GoogleApiClient mGoogleApiClient;

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
        TextView tvProfileType = (TextView) view.findViewById(R.id.tv_profile_type);
        tvProfileType.setText("Professional Details");
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
            case R.id.btn_edit: {
                if (!DeviceUtils.isInternetConnected(getActivity())) {
                    showToast("Sorry there is no internet.");
                    return;
                }
                int tag = (int) v.getTag();
                ViewHolder vh = viewHolderArrayList.get(tag);
                ProfileModel pm = hmProfileModel.get(tag);
                if (pm.fieldType == Constants.Types.FIELD_STRING) {
                    vh.etFieldValue.setText(vh.tvFieldValue.getText().toString());
                } else {
                    String value = convertKeytoLabel(pm.key).toLowerCase();
                    if (value.contains("google")) {
                        vh.btnGplus.setVisibility(View.VISIBLE);
                    } else if (value.contains("facebook")) {
                        vh.btnFb.setVisibility(View.VISIBLE);
                    } else if (value.contains("twitter")) {
                        vh.btnTwitter.setVisibility(View.VISIBLE);
                    } else if (value.contains("instagram")) {
                        vh.btnInstagram.setVisibility(View.VISIBLE);
                    } else if (value.contains("linkedin")) {
                        vh.btnLinkedIn.setVisibility(View.VISIBLE);
                    }
                }
                vh.tvFieldValue.setVisibility(View.GONE);
                int fieldType = hmProfileModel.get(tag).fieldType;
                if (fieldType == Constants.Types.FIELD_STRING) {
                    vh.etFieldValue.setVisibility(View.VISIBLE);
                    vh.btnUpdate.setVisibility(View.VISIBLE);
                }
                v.setVisibility(View.GONE);
                break;
            }

            case R.id.btn_update: {
                int tag = (int) v.getTag();
                ViewHolder vh = viewHolderArrayList.get(tag);
                JSONArray arrUsr = new JSONArray();
                JSONObject oUsr = new JSONObject();
                try {
                    int fieldType = hmProfileModel.get(tag).fieldType;
                    if (fieldType == Constants.Types.FIELD_STRING) {
                        oUsr.put(hmProfileModel.get(tag).key, vh.etFieldValue.getText().toString());
                    }
                    arrUsr.put(oUsr);
                    Router.startUserService(getActivity(), Constants.Types.REQUEST_PUT,
                            arrUsr.toString(), Constants.Types.PROFILE_SOCIAL);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                vh.btnEdit.setVisibility(View.GONE);
                vh.pbUpdate.setVisibility(View.VISIBLE);
                break;
            }

            case R.id.btn_facebook_login: {
                log(TAG, "facebook login");
                String text = ((Button) v).getText().toString();
                if (!getActivity().getResources().getString(R.string.fb_unsync_msg).equalsIgnoreCase(text)) {
                    isFbSync = true;
                    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
                }
                break;
            }

            case R.id.btn_g_plus_login: {
                mGoogleApiClient.connect();
                break;
            }

            case R.id.btn_twitter_login: {
                mTwitterAuthClient.authorize(getActivity(), new Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> result) {
                        new SendTwitterData().execute(result.data);
                    }

                    @Override
                    public void failure(TwitterException e) {

                    }
                });
                break;
            }

            case R.id.btn_linkedin_login: {
                Router.startLinkendInLoginActivity(getActivity(), Constants.Values.RC_LINKEDIN);
                break;
            }

            case R.id.btn_instagram_login: {
                Router.startInstagramLoginActivity(getActivity(), Constants.Values.RC_INSTAGRAM);
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        log(TAG, "fuck bro");
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
            log(TAG, "fuck bro revenge");
            super.onActivityResult(requestCode, resultCode, data);
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void addViews(int size) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());


        int initialCount = llViewContainer.getChildCount();
        for (int i = 0; i < size; i++) {
            View view = inflater.inflate(R.layout.item_profile_social_edit, llViewContainer, false);
            ViewHolder vh = new ViewHolder();
            vh.btnEdit = (Button) view.findViewById(R.id.btn_edit);
            vh.btnEdit.setTag(initialCount + i);
            vh.btnEdit.setOnClickListener(this);
            vh.etFieldValue = (EditText) view.findViewById(R.id.et_field_value);
            vh.pbUpdate = (ProgressBar) view.findViewById(R.id.pb_update);
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
            vh.btnUpdate = (Button) view.findViewById(R.id.btn_update);
            vh.btnUpdate.setOnClickListener(this);
            vh.btnAdd.setOnClickListener(this);
            vh.btnTwitter.setOnClickListener(this);
            vh.btnGplus.setOnClickListener(this);
            vh.btnInstagram.setOnClickListener(this);
            vh.btnFb.setOnClickListener(this);
            vh.btnLinkedIn.setOnClickListener(this);
            viewHolderArrayList.add(vh);
            llViewContainer.addView(view, initialCount + i);
        }
    }

    private void removeViews(int size) {
        int initialSize = llViewContainer.getChildCount();
        for (int i = 1; i <= size; i++) {
            llViewContainer.removeViewAt(initialSize - i);
            viewHolderArrayList.remove(initialSize - i);
        }

    }

    private void setViewContent() {
        for (int i = 0; i < hmProfileModel.size(); i++) {
            ViewHolder vh = viewHolderArrayList.get(i);
            vh.btnEdit.setTag(i);
            vh.btnUpdate.setTag(i);
            vh.btnAdd.setTag(i);
            vh.btnFb.setVisibility(View.GONE);
            vh.btnInstagram.setVisibility(View.GONE);
            vh.btnTwitter.setVisibility(View.GONE);
            vh.btnGplus.setVisibility(View.GONE);
            vh.btnLinkedIn.setVisibility(View.GONE);
            vh.btnEdit.setVisibility(View.VISIBLE);
            vh.tvFieldValue.setVisibility(View.VISIBLE);
            vh.pbUpdate.setVisibility(View.GONE);
            vh.etFieldValue.setVisibility(View.GONE);
            vh.btnAdd.setVisibility(View.GONE);
            vh.btnUpdate.setVisibility(View.GONE);
            if (hmProfileModel.get(i).fieldType == Constants.Types.FIELD_STRING) {
                vh.etFieldValue.setInputType(hmProfileModel.get(i).inputType);
            }
            vh.tvFieldLabel.setText(convertKeytoLabel(hmProfileModel.get(i).key));
            if (hmProfileModel.get(i).value != null && (String.valueOf(hmProfileModel.get(i).value)).length() != 0) {
                if (hmProfileModel.get(i).fieldType == Constants.Types.FIELD_STRING) {
                    vh.tvFieldValue.setText(String.valueOf(hmProfileModel.get(i).value));
                } else if (hmProfileModel.get(i).fieldType == Constants.Types.FIELD_SOCIAL) {
                    String value = hmProfileModel.get(i).value.toString();
                    if (value.lastIndexOf("/") != value.length() - 1) {
                        vh.tvFieldValue.setText(value);
                    } else {
                        vh.tvFieldValue.setVisibility(View.GONE);
                        vh.btnEdit.setVisibility(View.GONE);
                        vh.btnAdd.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                vh.tvFieldValue.setVisibility(View.GONE);
                vh.btnEdit.setVisibility(View.GONE);
                vh.btnAdd.setVisibility(View.VISIBLE);
            }
        }
    }

    private String convertKeytoLabel(String key) {
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
                new SendGooglePlusData().execute();
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
        log(TAG, loginResult.getAccessToken().toString());
        GraphRequest.newMeRequest(loginResult.getAccessToken(), this).executeAsync();
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(FacebookException error) {
        log(TAG, "here");
        error.printStackTrace();
    }

    @Override
    public void onCompleted(JSONObject object, GraphResponse response) {
        log(TAG, object.toString() + " uck");
        new SendFacebookData().execute(object);
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
                    if (keyLowerCase.contains("facebook") || keyLowerCase.contains("google") || keyLowerCase.contains("twitter")
                            || keyLowerCase.contains("instagram") || keyLowerCase.contains("linkedin")) {
                        log(TAG, keyLowerCase + " inside");
                        if (keyLowerCase.contains("google")) {
                            hm.put(counter++, new ProfileModel(sp.getSocial_platform(),
                                    hmNameToUrl.get(sp.getSocial_platform()) + "/" + sp.getPlatform_id() + "/posts",
                                    Constants.Types.FIELD_SOCIAL));
                        } else if (keyLowerCase.contains("linkedin")) {
                            hm.put(counter++, new ProfileModel(sp.getSocial_platform(),
                                    hmNameToUrl.get(sp.getSocial_platform()) + sp.getPlatform_id(),
                                    Constants.Types.FIELD_SOCIAL));
                        } else {
                            hm.put(counter++, new ProfileModel(sp.getSocial_platform(),
                                    hmNameToUrl.get(sp.getSocial_platform()) + "/" + sp.getPlatform_id(),
                                    Constants.Types.FIELD_SOCIAL));
                        }
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
                    if (keyLowerCase.contains("facebook") || keyLowerCase.contains("google") || keyLowerCase.contains("twitter")
                            || keyLowerCase.contains("instagram") || keyLowerCase.contains("linkedin")) {
                        hm.put(counter++, new ProfileModel(sp.getPlatformName(), sp.getPlatformBaseUrl() + "/", Constants.Types.FIELD_SOCIAL));
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
            if (!isListDrawn) {
                addViews(hmProfileModel.size());
                isListDrawn = true;
            } else if (hmProfileModel.size() > llViewContainer.getChildCount()) {
                addViews(hmProfileModel.size() - llViewContainer.getChildCount());
            } else if (hmProfileModel.size() < llViewContainer.getChildCount()) {
                removeViews(llViewContainer.getChildCount() - hmProfileModel.size());
            }
            setViewContent();
        }
    }

    private class SendFacebookData extends AsyncTask<JSONObject, Void, Bundle> {
        @Override
        protected Bundle doInBackground(JSONObject... params) {
            if (params.length > 0) {
                JSONObject object = params[0];
                ArrayList<SocialPlatform> socialPlatforms = ((BaseActivity) getActivity()).getSocialPlatforms();
                long id = 0;
                for (SocialPlatform sp : socialPlatforms) {
                    if (sp.getPlatformName().toLowerCase().contains("facebook")) {
                        id = sp.getId();
                    }
                }
                try {
                    Bundle args = new Bundle();
                    args.putLong(Constants.Keys.KEY_SOCIAL_PLATFORM_ID, id);
                    args.putInt(Constants.Keys.KEY_USER_FIELD_VISIBILITY, 1);
                    args.putString(Constants.Keys.KEY_PLATFORM_EMAIL_ID, object.getString("email"));
                    args.putString(Constants.Keys.KEY_PLATFORM_ID, object.getString("id"));
                    args.putString(Constants.Keys.KEY_PLATFORM_PERMISSION, "email, public_profile");
                    isFbSync = false;
                    return args;
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            Router.updateSocialProfile(getActivity(), bundle);
        }

    }

    private class SendTwitterData extends AsyncTask<TwitterSession, Void, Bundle> {
        @Override
        protected Bundle doInBackground(TwitterSession... params) {
            if (params.length > 0) {
                TwitterSession session = params[0];
                ArrayList<SocialPlatform> socialPlatforms = (CurrentUserSocialProfileEditFragment.this.getBaseActivity())
                        .getSocialPlatforms();
                long id = 0;
                for (SocialPlatform sp : socialPlatforms) {
                    if (sp.getPlatformName().toLowerCase().contains("twitter")) {
                        id = sp.getId();
                        break;
                    }
                }
                Bundle args = new Bundle();
                args.putLong(Constants.Keys.KEY_SOCIAL_PLATFORM_ID, id);
                args.putInt(Constants.Keys.KEY_USER_FIELD_VISIBILITY, 1);
                args.putString(Constants.Keys.KEY_PLATFORM_ID, session.getUserName());
                args.putString(Constants.Keys.KEY_PLATFORM_SECRET, session.getAuthToken().secret);
                args.putString(Constants.Keys.KEY_PLATFORM_TOKEN, session.getAuthToken().token);
                return args;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            Router.updateSocialProfile(getActivity(), bundle);
        }
    }

    private class SendGooglePlusData extends AsyncTask<Void, Void, Bundle> {
        @Override
        protected Bundle doInBackground(Void... params) {
            ArrayList<SocialPlatform> socialPlatforms = (CurrentUserSocialProfileEditFragment.this.getBaseActivity())
                    .getSocialPlatforms();
            long id = 0;
            for (SocialPlatform sp : socialPlatforms) {
                if (sp.getPlatformName().toLowerCase().contains("google")) {
                    id = sp.getId();
                    break;
                }
            }
            Bundle args = new Bundle();
            args.putLong(Constants.Keys.KEY_SOCIAL_PLATFORM_ID, id);
            args.putInt(Constants.Keys.KEY_USER_FIELD_VISIBILITY, 1);
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            if(currentPerson != null) {
                log(TAG, currentPerson.getId());
                args.putString(Constants.Keys.KEY_PLATFORM_ID, currentPerson.getId());
            }
            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            if(email != null) {
                args.putString(Constants.Keys.KEY_PLATFORM_EMAIL_ID, email);
            }
            return args;
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
        public Button btnEdit;
        public Button btnAdd;
        public Button btnUpdate;
        public Button btnFb;
        //        public LoginButton btnFb;
        public Button btnGplus;
        public Button btnInstagram;
        public Button btnLinkedIn;
        public Button btnTwitter;
        public ProgressBar pbUpdate;
    }
}
