package com.contag.app.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tanay on 4/8/15.
 */
public class PrefUtils {

    private static final String APP_PREFS = "contagprefs";
    private static final String KEY_ACCESS_TOKEN = "accesstoken";

    private static SharedPreferences mSharedPref;
    private static SharedPreferences.Editor mEditor;

    public static void init(Context context) {
        mSharedPref = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        mEditor = mSharedPref.edit();
        mEditor.commit();
    }

    public static void setKeyAccessToken(String accessToken) {
        mEditor.putString(KEY_ACCESS_TOKEN, accessToken).commit();
    }

    public static String getKeyAccessToken() {
        return mSharedPref.getString(KEY_ACCESS_TOKEN, null);
    }

}
