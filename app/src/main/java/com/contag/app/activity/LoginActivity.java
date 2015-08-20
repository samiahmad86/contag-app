package com.contag.app.activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.contag.app.R;
import com.contag.app.config.Router;
import com.contag.app.fragment.LoginFragment;
import com.contag.app.config.Constants;
import com.contag.app.model.Login;
import com.contag.app.model.LoginResponse;
import com.contag.app.request.LoginRequest;
import com.contag.app.util.DeviceUtils;
import com.contag.app.util.RegexUtils;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * Created by tanay on 30/7/15.
 * Called by Splash Screen Activity.
 * Logs in user with their phone number.
 * On log in success
 *      If the user is first time user then goto {@link UserDetailsActivity}
 *      else goto {@link HomeActivity}
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener, RequestListener<LoginResponse>{

    private static final String TAG = LoginActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final Toolbar tb = (Toolbar) findViewById(R.id.tb_login);
        setSupportActionBar(tb);

        if(savedInstanceState != null) {
            return;
        }

        findViewById(R.id.btn_login).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {

            case R.id.btn_login: {
                String phNum = ((EditText) findViewById(R.id.et_phone_num)).getText().toString();

                if(!RegexUtils.isPhoneNumber(phNum)) {
                    showToast("Enter a valid phone number.");
                }
                findViewById(R.id.pb_login).setVisibility(View.VISIBLE);
                LoginRequest lr = new LoginRequest(DeviceUtils.getmDeviceId(LoginActivity.this), new Login(phNum));
                getSpiceManager().execute(lr, this);
            }
        }
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        findViewById(R.id.pb_login).setVisibility(View.GONE);
        log(TAG, spiceException.getMessage());
    }

    @Override
    public void onRequestSuccess(LoginResponse loginResponse) {
            if(loginResponse.isNewUser) {
                Router.startUserDetailsActivity(LoginActivity.this, TAG);
            } else {
                Router.startHomeActivity(LoginActivity.this, TAG);
            }
    }
}
