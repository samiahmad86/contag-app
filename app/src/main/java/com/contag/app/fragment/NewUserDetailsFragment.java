package com.contag.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.config.Router;
import com.contag.app.request.UserRequest;
import com.contag.app.util.PrefUtils;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        btnProceed.setOnClickListener(this);
        btnFb.setFragment(this);
        log(TAG, "uncle fucker");
        btnFb.setReadPermissions(Arrays.asList("public_profile"));
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
                parameters.putString("fields", "id,name,gender");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                log(TAG, "cancellation");
            }

            @Override
            public void onError(FacebookException e) {
                log(TAG, "error");
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
                    oUsr.put(Constants.Keys.KEY_USER_FIELD_VISIBILITY, 1);
                    arrUsr.put(oUsr);
                    oUsr = new JSONObject();
                    if(rgGender.getCheckedRadioButtonId() == R.id.rb_male) {
                        oUsr.put(Constants.Keys.KEY_USER_GENDER, "m");
                    } else {
                        oUsr.put(Constants.Keys.KEY_USER_GENDER, "f");
                    }
                    oUsr.put(Constants.Keys.KEY_USER_FIELD_VISIBILITY, 1);
                    arrUsr.put(oUsr);
                    oUsr = new JSONObject();
                    if(imageUrl != null) {
                        oUsr.put(Constants.Keys.KEY_USER_AVATAR_URL, imageUrl);
                        oUsr.put(Constants.Keys.KEY_USER_FIELD_VISIBILITY, 1);
                        arrUsr.put(oUsr);
                    }
                    log(TAG, arrUsr.toString());
                    Router.startUserService(getActivity(), Constants.Types.REQUEST_PUT, arrUsr.toString());
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                    break;
            }
        }
    }
}
