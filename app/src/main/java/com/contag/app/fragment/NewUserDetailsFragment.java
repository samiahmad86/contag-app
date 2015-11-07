package com.contag.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.config.ContagApplication;
import com.contag.app.config.Router;
import com.contag.app.model.ContagContagDao;
import com.contag.app.model.DaoSession;
import com.contag.app.model.SocialPlatform;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class NewUserDetailsFragment extends BaseFragment implements  View.OnClickListener{

    public static final String TAG = NewUserDetailsFragment.class.getName();
    private CallbackManager cbm;
    private EditText etUserName;
    private String imageUrl;
    private RadioGroup rgGender;

    public static NewUserDetailsFragment newInstance() {
        NewUserDetailsFragment nudf = new NewUserDetailsFragment();
        Bundle bundle = new Bundle();
        nudf.setArguments(bundle);
        return nudf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cbm = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_new_user_edit, container, false);
        final LoginButton btnFb = (LoginButton) view.findViewById(R.id.btn_fb_sync);
        final View btnProceed = view.findViewById(R.id.btn_proceed);
        final ImageView ivUserProfilePic = (ImageView) view.findViewById(R.id.iv_profile_img);
        etUserName = (EditText) view.findViewById(R.id.et_user_name);
        rgGender = (RadioGroup) view.findViewById(R.id.rg_gender);
        DaoSession session = ((ContagApplication) getActivity().getApplicationContext()).getDaoSession();
        ContagContagDao ccDao = session.getContagContagDao();

        btnProceed.setOnClickListener(this);
        btnFb.setFragment(this);
        btnFb.setReadPermissions(Arrays.asList("public_profile, email"));
        btnFb.registerCallback(cbm, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                log(TAG, "success" + loginResult.getAccessToken().getToken());
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                new SendData().execute(object);
                                view.findViewById(R.id.ll_fb_container).setVisibility(View.INVISIBLE);
                                JSONObject oUser = response.getJSONObject();
                                try {
                                    imageUrl = "https://graph.facebook.com/" + oUser.getLong("id") + "/picture?type=large";
                                    Picasso.with(getActivity()).load(imageUrl).into(ivUserProfilePic);
                                    etUserName.setText(oUser.getString(Constants.Keys.KEY_USER_NAME));
                                    String gender = oUser.getString(Constants.Keys.KEY_USER_GENDER);
                                    if(gender.equalsIgnoreCase("male")) {
                                        ((RadioButton) view.findViewById(R.id.rb_male)).setChecked(true);
                                    } else {
                                        ((RadioButton) view.findViewById(R.id.rb_female)).setChecked(true);
                                    }

                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                                log(TAG, response.toString());
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,gender,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                log(TAG, "cancellation");
            }

            @Override
            public void onError(FacebookException e) {
                showToast("Sorry! It seems like we are facing some issue with facebook login at the moment.");
                e.printStackTrace();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(brUser,
                new IntentFilter(getActivity().getResources().getString(R.string.intent_filter_user_received)));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(brUser);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cbm.onActivityResult(requestCode, resultCode, data);
    }

    private BroadcastReceiver brUser = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Router.startHomeActivity(getActivity(), TAG);
            getActivity().finish();
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_proceed: {
                JSONArray arrUsr = new JSONArray();
                JSONObject oUsr = new JSONObject();
                try {
                    String name = etUserName.getText().toString();
//                    String regexExpression = "^[\\\\p{L} .'-]+$";
//                    if(!name.matches(regexExpression)) {
//                        log(TAG, name + "fuckkk");
//                        showToast("Enter a valid name");
//                        return;
//                    }
                    oUsr.put(Constants.Keys.KEY_USER_NAME, name);
                    arrUsr.put(oUsr);
                    oUsr = new JSONObject();
                    if(rgGender.getCheckedRadioButtonId() == R.id.rb_male) {
                        oUsr.put(Constants.Keys.KEY_USER_GENDER, "male");
                    } else {
                        oUsr.put(Constants.Keys.KEY_USER_GENDER, "female");
                    }
                    arrUsr.put(oUsr);
                    oUsr = new JSONObject();
                    if(imageUrl != null) {
                        oUsr.put(Constants.Keys.KEY_USER_AVATAR_URL, imageUrl);
                        arrUsr.put(oUsr);
                    }
                    log("NewFubar", arrUsr.toString());
                    Router.startUserService(getActivity(), Constants.Types.REQUEST_PUT, arrUsr.toString());
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                    break;
            }
        }
    }

    private class SendData extends AsyncTask<JSONObject, Void, Bundle> {
        @Override
        protected Bundle doInBackground(JSONObject... params) {
            if(params.length > 0) {
                JSONObject object = params[0];
                long id ;
                SocialPlatform sp = (NewUserDetailsFragment.this.getBaseActivity())
                        .getPlatformFromName("facebook");
                id = sp.getId();
                Log.d("SocialVocial", "ID of platform is: " + id) ;
                Log.d("SocialVocial", object.toString()) ;
                Bundle args = new Bundle();
                try {

                    args.putLong(Constants.Keys.KEY_SOCIAL_PLATFORM_ID, id);
                    args.putString(Constants.Keys.KEY_PLATFORM_EMAIL_ID, object.getString("email"));
                    args.putString(Constants.Keys.KEY_PLATFORM_ID, object.getString("id"));
                    args.putString(Constants.Keys.KEY_PLATFORM_PERMISSION, "email, public_profile");

                } catch (JSONException ex) {
                    ex.printStackTrace();
                    args.putString(Constants.Keys.KEY_PLATFORM_EMAIL_ID, "user@contagapp.com");
                    args.putString(Constants.Keys.KEY_PLATFORM_ID, "contag_user");
                }
                return args;
            }
            return null ;
        }
        @Override
        protected void onPostExecute(Bundle bundle) {
            Router.updateSocialProfile(getActivity(), bundle);
        }

    }

}
