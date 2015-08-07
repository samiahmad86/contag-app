package com.contag.app.activity;

import android.support.v7.app.AppCompatActivity;

import com.contag.app.util.PrefUtils;

/**
 * Created by tanay on 30/7/15.
 */
public class BaseActivity extends AppCompatActivity {

    public boolean isUserLoggedIn() {
        return PrefUtils.getKeyAccessToken() != null;
    }
}
