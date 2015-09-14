package com.contag.app.config;

import android.content.Context;
import android.content.Intent;

import com.contag.app.activity.HomeActivity;
import com.contag.app.activity.LoginActivity;
import com.contag.app.activity.EditUserActivity;
import com.contag.app.activity.UserActivity;
import com.contag.app.service.ContactService;
import com.contag.app.service.GcmRegisterIntentService;

/**
 * Created by tanay on 30/7/15.
 * All the activity calls will be done through this class
 */

public class Router {

    public static void startLoginActivity(Context mContext, String currentClassName, int flags) {
        Intent iStartLogin = new Intent(mContext, LoginActivity.class);
        iStartLogin.putExtra(Constants.Keys.KEY_PREVIOUS_ACTIVITY, currentClassName);
        iStartLogin.addFlags(flags);
        mContext.startActivity(iStartLogin);
    }

    public static void startHomeActivity(Context mContext, String currentClassName) {
        Intent iStartHome = new Intent(mContext, HomeActivity.class);
        iStartHome.putExtra(Constants.Keys.KEY_PREVIOUS_ACTIVITY, currentClassName);
        mContext.startActivity(iStartHome);
    }

    public static void startEditUserActivity(Context mContext, String className) {
        Intent iStartUserDetails = new Intent(mContext, EditUserActivity.class);
        iStartUserDetails.putExtra(Constants.Keys.KEY_PREVIOUS_ACTIVITY, className);
        mContext.startActivity(iStartUserDetails);
    }

    public static void startUserActivity(Context mContext, String className) {
        Intent iUsrProf = new Intent(mContext, UserActivity.class);
        iUsrProf.putExtra(Constants.Keys.KEY_PREVIOUS_ACTIVITY, className);
        mContext.startActivity(iUsrProf);
    }

    public static void startContactService(Context mContext) {
        Intent iStartContactService = new Intent(mContext, ContactService.class);
        mContext.startService(iStartContactService);
    }

    public static void startGcmRegisterService(Context context) {
        Intent iRegisterGcm = new Intent(context, GcmRegisterIntentService.class);
        context.startService(iRegisterGcm);
    }
}
