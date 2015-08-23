package com.contag.app.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

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
 *      via {@link com.contag.app.config.Router#startEditUserActivity(Context, String)}
 * else goto {@link HomeActivity}
 *      via {@link com.contag.app.config.Router#startHomeActivity(Context, String)}
 */

public class LoginActivity extends BaseActivity implements BaseFragment.OnFragmentInteractionListener {

    private static final String TAG = LoginActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setUpActionBar(R.id.tb_login);

        if (savedInstanceState != null) {
            return;
        }

        onFragmentInteraction(Constants.Values.FRAG_LOGIN, null);
    }


    @Override
    public void onFragmentInteraction(int fragmentType, Bundle args) {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (fragmentType == Constants.Values.FRAG_OTP) {
            ft.replace(R.id.fl_login, LoginFragment.newInstance(fragmentType, args.getBoolean(Constants.Keys.KEY_NEW_USER)));
        } else {
            ft.add(R.id.fl_login, LoginFragment.newInstance(Constants.Values.FRAG_LOGIN, false));
        }
        ft.commit();

    }
}
