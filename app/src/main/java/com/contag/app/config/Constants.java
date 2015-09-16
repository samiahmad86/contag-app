package com.contag.app.config;

/**
 * Created by tanay on 30/7/15.
 * Use camel casing for the values of the constants.
 */
public class Constants {

    public class Keys {
        public static final String KEY_PREVIOUS_ACTIVITY = "previous_activity";
        public static final String KEY_APP_PREFS = "app_prefs";
        public static final String KEY_AUTH_TOKEN = "auth_token";
        public static final String KEY_GCM_TOKEN = "gcm_token";
        public static final String KEY_FRAGMENT_TYPE = "fragment_type";
        public static final String KEY_NEW_USER = "is_new_user";
        public static final String KEY_OTP = "otp";
        public static final String KEY_NUMBER = "number";
        public static final String KEY_CONTAG_ID = "contag_id";
        public static final String KEY_FB_ACCESS_TOKEN = "fb_access_token";
        public static final String KEY_CONTACT_NAME = "contact_name";
        public static final String KEY_CONTACT_NUMBER = "contact_number";
    }

    public class Headers {
        public static final String HEADER_DEVICE_TYPE = "X-Device-Type";
        public static final String HEADER_PUSH_ID = "X-Push-ID";
        public static final String HEADER_DEVICE_ID = "X-Device-ID";
        public static final String HEADER_APP_VERSION_ID = "X-App-Version-ID";
        public static final String HEADER_TOKEN = "token";
    }

    public class Values {
        // Request code for google plus sign in
        public static final int RC_GPLUS_SIGN_IN = 0;
    }

    public class Types {

        public static final int FRAG_LOGIN = 0;
        public static final int FRAG_OTP = 1;
        public static final int FRAG_USER_DETAILS = 2;
        public static final int FRAG_CREATE_USER = 3;
    }

    public class Urls {
        public static final String BASE_URL = "http://54.255.219.32:7999";
        public static final String URL_LOGIN = "/login/";
        public static final String URL_OTP = "/otp/";
        public static final String URL_USER = "/user/";
        public static final String URL_CONTACT = "/contact/";
    }

    public class Regex {
        public static final String PHONE_NUM = "^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$";
    }
}
