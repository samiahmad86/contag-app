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

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.config.Router;
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

/**
 * Created by tanay on 20/8/15.
 */
public class NewUserDetailsFragment extends BaseFragment implements  View.OnClickListener{

    public static final String TAG = NewUserDetailsFragment.class.getName();
    private CallbackManager cbm;
    private EditText etUserName;
    private String imageUrl;

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
        final ImageView ivUserProfilePic = (ImageView) view.findViewById(R.id.iv_user_photo);
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
                                btnFb.setVisibility(View.INVISIBLE);
                                btnProceed.setVisibility(View.VISIBLE);
                                JSONObject oUser = response.getJSONObject();
                                try {
                                    imageUrl = "https://graph.facebook.com/" + oUser.getLong("id") + "/picture?type=large";

                                    Picasso.with(getActivity()).load(imageUrl).into(ivUserProfilePic);
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

    }
}
