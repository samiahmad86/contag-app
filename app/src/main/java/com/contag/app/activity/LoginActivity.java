package com.contag.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.contag.app.R;
import com.contag.app.fragment.LoginFragment;
import com.contag.app.util.Constants;

/**
 * Created by tanay on 30/7/15.
 * Called by Splash Screen Activity.
 *
 */

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(savedInstanceState != null) {
            return;
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fl_login, LoginFragment.newInstance(), Constants.Login.TAG_LOGIN_FRAG).commit();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            LoginFragment lf = (LoginFragment) getSupportFragmentManager().findFragmentByTag(Constants.Login.TAG_LOGIN_FRAG);
            if(lf != null) {
                lf.onActivityResult(requestCode, resultCode, data);
            }
    }

}
