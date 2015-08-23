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
        mSharedPref = context.getSharedPreferences(Constants.Keys.APP_PREFS, Context.MODE_PRIVATE);
        mEditor = mSharedPref.edit();
        mEditor.commit();
    }

    public static void setKeyAccessToken(String accessToken) {
        mEditor.putString(Constants.Keys.KEY_ACCESS_TOKEN, accessToken).commit();
    }

    public static void setGcmToken(String token) {
        mEditor.putString(Constants.Keys.KEY_GCM_TOKEN, token).commit();
    }

    public static String getKeyAccessToken() {
        return mSharedPref.getString(Constants.Keys.KEY_ACCESS_TOKEN, null);
    }

    public static String getGcmToken() {
        return mSharedPref.getString(Constants.Keys.KEY_GCM_TOKEN, null);
    }
}
