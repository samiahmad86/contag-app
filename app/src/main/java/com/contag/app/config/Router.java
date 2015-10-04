package com.contag.app.config;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.contag.app.activity.HomeActivity;
import com.contag.app.activity.LoginActivity;
import com.contag.app.activity.NewUserActivity;
import com.contag.app.activity.UserActivity;
import com.contag.app.service.ContactService;
import com.contag.app.service.CustomService;
import com.contag.app.service.GcmRegisterIntentService;
import com.contag.app.service.UserService;
import com.google.android.gms.appindexing.Action;

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

    public static void startUserActivity(Context mContext, String className, long userID) {
        Intent iUsrProf = new Intent(mContext, UserActivity.class);
        iUsrProf.putExtra(Constants.Keys.KEY_PREVIOUS_ACTIVITY, className);
        iUsrProf.putExtra(Constants.Keys.KEY_USER_ID, userID);
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

    public static void startUserService(Context context, int type) {
        startUserService(context, type, null);
    }

    public static void startUserService(Context context, int type, String userArray) {
       startUserService(context, type, userArray, 0);
    }

    public static void startUserService(Context context, int type, String userArray, int profileType) {
        Intent iUser = new Intent(context, UserService.class);
        iUser.putExtra(Constants.Keys.KEY_REQUEST_TYPE, type);
        iUser.putExtra(Constants.Keys.KEY_USER_ARRAY, userArray);
        iUser.putExtra(Constants.Keys.KEY_USER_PROFILE_TYPE, profileType);
        context.startService(iUser);
    }


    public static void startCustomService(Context context, int type) {
        Intent iCustomService = new Intent(context, CustomService.class);
        iCustomService.putExtra(Constants.Keys.KEY_SERVICE_TYPE, type);
        context.startService(iCustomService);
    }

    public static void startProfileRequestService(Context mContext, int type, long id, String fieldName) {
        Intent iCustomService = new Intent(mContext, CustomService.class);
        iCustomService.putExtra(Constants.Keys.KEY_SERVICE_TYPE, type);
        iCustomService.putExtra(Constants.Keys.KEY_PROFILE_REQUEST_FOR_USER, id);
        iCustomService.putExtra(Constants.Keys.KEY_PROFILE_REQUEST_TYPE, fieldName);
        mContext.startService(iCustomService);
    }

    public static void openSocialProfile(Context context, String link) {
        Intent socialIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        context.startActivity(socialIntent);
    }
}
