package com.contag.app.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.config.Router;
import com.contag.app.fragment.BaseFragment;
import com.contag.app.fragment.LoginFragment;
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
 * If the user is first time user then goto {@link UserDetailsActivity}
 * else goto {@link HomeActivity}
 */

public class LoginActivity extends BaseActivity implements BaseFragment.OnFragmentInteractionListener {

    private static final String TAG = LoginActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final Toolbar tb = (Toolbar) findViewById(R.id.tb_login);
        setSupportActionBar(tb);

        if (savedInstanceState != null) {
            return;
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fl_login, LoginFragment.newInstance(Constants.Values.FRAG_LOGIN, false));
        ft.commit();
    }


    @Override
    public void onFragmentInteraction(int code, Bundle args) {

        if(code == Constants.Values.FRAG_OTP) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fl_login, LoginFragment.newInstance(code, args.getBoolean(Constants.Keys.KEY_NEW_USER)));
            ft.commit();
        }
    }
}
