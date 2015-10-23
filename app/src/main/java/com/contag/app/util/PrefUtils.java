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

    public static void setContactBookUpdated(boolean val) {
        mEditor.putBoolean(Constants.Keys.KEY_CONTACTS_UPDATED, val).commit();
    }

    public static boolean isContactBookUpdated() {
        return mSharedPref.getBoolean(Constants.Keys.KEY_CONTACTS_UPDATED, true);
    }

    public static void setContactUpdatedTimestamp(long timestamp) {
        mEditor.putLong(Constants.Keys.KEY_CONTACTS_UPDATED_TIMESTAMP, timestamp).commit();
    }

    public static long getContactUpdatedTimestamp() {
        return mSharedPref.getLong(Constants.Keys.KEY_CONTACTS_UPDATED_TIMESTAMP, 0);
    }

    public static void setCurrentUser(String user) {
        mEditor.putString(Constants.Keys.KEY_CURRENT_USER, user).commit();
    }

    public static String getCurrentUser() {
        return mSharedPref.getString(Constants.Keys.KEY_CURRENT_USER, null);
    }

    public static void setSocialPlatforms(String platforms) {
        mEditor.putString(Constants.Keys.KEY_SOCIAL_PLATFORMS, platforms).commit();
    }

    public static String getSocialPlatforms() {
        return mSharedPref.getString(Constants.Keys.KEY_SOCIAL_PLATFORMS, null);
    }

    public static void setCurrentUserID(long id) {
        mEditor.putLong(Constants.Keys.KEY_CURRENT_USER_ID, id).commit();
    }

    public static long getCurrentUserID() {
        return mSharedPref.getLong(Constants.Keys.KEY_CURRENT_USER_ID, 0);
    }

    public static void clearForLogout(){
        mSharedPref.edit().remove(Constants.Keys.KEY_AUTH_TOKEN).commit();
        mSharedPref.edit().remove(Constants.Keys.KEY_CURRENT_USER).commit();
        mSharedPref.edit().remove(Constants.Keys.KEY_SOCIAL_PLATFORMS).commit();

    }
}
