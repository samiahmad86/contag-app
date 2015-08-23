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
        public static final String KEY_GCM_TOKEN = "gcmToken";
        public static final String KEY_FRAGMENT_TYPE = "fragmentType";
        public static final String KEY_NEW_USER = "newUser";
    }

    public class Values {
        // Request code for google plus sign in
        public static final int RC_GPLUS_SIGN_IN = 0;
        public static final int FRAG_LOGIN = 0;
        public static final int FRAG_OTP = 1;
        public static final int FRAG_SOCIAL = 2;
        public static final int FRAG_EDIT_USER = 3;
        public static final String BASE_URL = "http://api.gonomnom.com";
    }

    public class Regex {
        public static final String PHONE_NUM = "^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$";
    }
}
