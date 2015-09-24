package com.contag.app.config;

import android.content.Context;
import android.content.Intent;

import com.contag.app.activity.HomeActivity;
import com.contag.app.activity.LoginActivity;
import com.contag.app.activity.NewUserActivity;
import com.contag.app.activity.UserActivity;
import com.contag.app.service.ContactService;
import com.contag.app.service.GcmRegisterIntentService;
import com.contag.app.service.UserService;

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

    public static void startNewUserActivity(Context mContext, String className, long phoneNumber) {
        Intent iNewUserDetails = new Intent(mContext, NewUserActivity.class);
        iNewUserDetails.putExtra(Constants.Keys.KEY_PREVIOUS_ACTIVITY, className);
        iNewUserDetails.putExtra(Constants.Keys.KEY_NUMBER, phoneNumber);
        mContext.startActivity(iNewUserDetails);
    }

    public static void startUserActivity(Context mContext, String className) {
        Intent iUsrProf = new Intent(mContext, UserActivity.class);
        iUsrProf.putExtra(Constants.Keys.KEY_PREVIOUS_ACTIVITY, className);
        mContext.startActivity(iUsrProf);
    }

    public static void startContactService(Context mContext, boolean sendContacts) {
        Intent iStartContactService = new Intent(mContext, ContactService.class);
        iStartContactService.putExtra(Constants.Keys.KEY_SEND_CONTACTS, sendContacts);
        mContext.startService(iStartContactService);
    }

    public static void startGcmRegisterService(Context context) {
        Intent iRegisterGcm = new Intent(context, GcmRegisterIntentService.class);
        context.startService(iRegisterGcm);
    }

    public static void startUserService(Context context, int type, String userArray) {
        Intent iUser = new Intent(context, UserService.class);
        iUser.putExtra(Constants.Keys.KEY_REQUEST_TYPE, type);
        iUser.putExtra(Constants.Keys.KEY_USER_ARRAY, userArray);
        context.startService(iUser);
    }

}
