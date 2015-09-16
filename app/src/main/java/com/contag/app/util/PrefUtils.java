package com.contag.app.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.contag.app.config.Constants;

/**
 * Created by tanay on 4/8/15.
 */
public class PrefUtils {

    private static SharedPreferences mSharedPref;
    private static SharedPreferences.Editor mEditor;

    public static void init(Context context) {
        mSharedPref = context.getSharedPreferences(Constants.Keys.KEY_APP_PREFS, Context.MODE_PRIVATE);
        mEditor = mSharedPref.edit();
        mEditor.commit();
    }

    public static void setAuthToken(String authToken) {
        mEditor.putString(Constants.Keys.KEY_AUTH_TOKEN, authToken).commit();
    }

    public static void setGcmToken(String token) {
        mEditor.putString(Constants.Keys.KEY_GCM_TOKEN, token).commit();
    }

    public static String getAuthToken() {
        return mSharedPref.getString(Constants.Keys.KEY_AUTH_TOKEN, null);
    }

    public static String getGcmToken() {
        return mSharedPref.getString(Constants.Keys.KEY_GCM_TOKEN, null);
    }
}
