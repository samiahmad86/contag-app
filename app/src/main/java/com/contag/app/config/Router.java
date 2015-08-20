package com.contag.app.config;

import android.content.Context;
import android.content.Intent;

import com.contag.app.activity.HomeActivity;
import com.contag.app.activity.LoginActivity;
import com.contag.app.activity.UserDetailsActivity;
import com.contag.app.activity.UserProfileActivity;
import com.contag.app.service.ContactService;

/**
 * Created by tanay on 30/7/15.
 * All the activity calls will be done through this class
 */

public class Router {

    public static void startLoginActivity(Context mContext, String currentClassName) {
        Intent iStartLogin = new Intent(mContext, LoginActivity.class);
        iStartLogin.putExtra(Constants.Keys.PREVIOUS_ACTIVITY, currentClassName);
        mContext.startActivity(iStartLogin);
    }

    public static void startHomeActivity(Context mContext, String currentClassName) {
        Intent iStartHome = new Intent(mContext, HomeActivity.class);
        iStartHome.putExtra(Constants.Keys.PREVIOUS_ACTIVITY, currentClassName);
        mContext.startActivity(iStartHome);
    }

    public static void startUserDetailsActivity(Context mContext, String className) {
        Intent iStartUserDetails = new Intent(mContext, UserDetailsActivity.class);
        iStartUserDetails.putExtra(Constants.Keys.PREVIOUS_ACTIVITY, className);
        mContext.startActivity(iStartUserDetails);
    }

    public static void startUserProfileActivity(Context mContext, String className) {
        Intent iUsrProf = new Intent(mContext, UserProfileActivity.class);
        iUsrProf.putExtra(Constants.Keys.PREVIOUS_ACTIVITY, className);
        mContext.startActivity(iUsrProf);
    }

    public static void startContactService(Context mContext) {
        Intent iStartContactService = new Intent(mContext, ContactService.class);
        mContext.startService(iStartContactService);
    }

}
