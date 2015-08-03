package com.contag.app.util;

import android.app.Activity;
import android.content.Intent;

import com.contag.app.activity.HomeActivity;
import com.contag.app.activity.LoginActivity;

/**
 * Created by tanay on 30/7/15.
 * All the activity calls will be done through this class
 */

public class Router {


    /*
        Start the login activity
     */
    public static void startLoginActivity(Activity mActivity, String currentClassName) {
        Intent iStartLogin = new Intent(mActivity, LoginActivity.class);
        iStartLogin.putExtra(Constants.Global.PREVIOUS_ACTIVITY, currentClassName);
        mActivity.startActivity(iStartLogin);
    }

    public static void startHomeActivity(Activity mActivity, String currentClassName) {
        Intent iStartHome = new Intent(mActivity, HomeActivity.class);
        iStartHome.putExtra(Constants.Global.PREVIOUS_ACTIVITY, currentClassName);
        mActivity.startActivity(iStartHome);
    }

}
