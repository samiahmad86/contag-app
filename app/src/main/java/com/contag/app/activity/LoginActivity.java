package com.contag.app.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.fragment.BaseFragment;
import com.contag.app.fragment.LoginFragment;

/**
 * Created by tanay on 30/7/15.
 * Called by Splash Screen Activity.
 * Logs in user with their phone number.
 * On log in success
 * If the user is first time user then goto {@link EditUserActivity}
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
    public void onFragmentInteraction(int fragmentType, Bundle args) {

        if(fragmentType == Constants.Values.FRAG_OTP) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fl_login, LoginFragment.newInstance(fragmentType, args.getBoolean(Constants.Keys.KEY_NEW_USER)));
            ft.commit();
        }
    }
}
