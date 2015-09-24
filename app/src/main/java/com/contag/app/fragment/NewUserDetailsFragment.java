package com.contag.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.contag.app.R;
import com.contag.app.config.Router;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by tanay on 20/8/15.
 */
public class NewUserDetailsFragment extends BaseFragment {

    public static final String TAG = NewUserDetailsFragment.class.getName();
    private CallbackManager cbm;

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
                                Router.startHomeActivity(NewUserDetailsFragment.this.getActivity(), TAG);
                                NewUserDetailsFragment.this.getActivity().finish();

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cbm.onActivityResult(requestCode, resultCode, data);
    }
}
