package com.contag.app.config;

/**
 * Created by tanay on 30/7/15.
 * Use camel casing for the values of the constants.
 */
public class Constants {

    public class Keys {
        public static final String PREVIOUS_ACTIVITY = "previousActivity";
        public static final String HEADER_DEVICE_ID = "X-Device-ID";
        public static final String HEADER_TOKEN = "token";
        public static final String APP_PREFS = "appPrefs";
        public static final String KEY_ACCESS_TOKEN = "accessToken";
    }

    public class Values {
        // Request code for google plus sign in
        public static final int RC_GPLUS_SIGN_IN = 0;
    }

    public class Regex {
        public static final String PHONE_NUM = "^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$";
    }
}
