package com.contag.app.util;

import android.content.Context;
import android.content.Intent;

import com.contag.app.activity.HomeActivity;
import com.contag.app.activity.LoginActivity;
import com.contag.app.service.ContactService;

/**
 * Created by tanay on 30/7/15.
 * All the activity calls will be done through this class
 */

public class Router {


    public static void startLoginActivity(Context mContext, String currentClassName) {
        Intent iStartLogin = new Intent(mContext, LoginActivity.class);
        iStartLogin.putExtra(Constants.Global.PREVIOUS_ACTIVITY, currentClassName);
        mContext.startActivity(iStartLogin);
    }

    public static void startHomeActivity(Context mContext, String currentClassName) {
        Intent iStartHome = new Intent(mContext, HomeActivity.class);
        iStartHome.putExtra(Constants.Global.PREVIOUS_ACTIVITY, currentClassName);
        mContext.startActivity(iStartHome);
    }

    public static void startContactService(Context mContext) {
        Intent iStartContactService = new Intent(mContext, ContactService.class);
        mContext.startService(iStartContactService);
    }

}
