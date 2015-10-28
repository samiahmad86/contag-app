package com.contag.app.config;

/**
 * Created by tanay on 30/7/15.
 * Use camel casing for the values of the constants.
 */
public class Constants {

    public class Keys {
        public static final String KEY_DATA = "fucking_data";
        public static final String KEY_PREVIOUS_ACTIVITY = "previous_activity";
        public static final String KEY_APP_PREFS = "app_prefs";
        public static final String KEY_AUTH_TOKEN = "auth_token";
        public static final String KEY_GCM_TOKEN = "gcm_token";
        public static final String KEY_FRAGMENT_TYPE = "fragment_type";
        public static final String KEY_NEW_USER = "is_new_user";
        public static final String KEY_OTP = "otp";
        public static final String KEY_FIELD_TYPE = "field_type";
        public static final String KEY_NUMBER = "number";
        public static final String KEY_CONTAG_ID = "contag_id";
        public static final String KEY_CONTAG_USER = "contag_user";
        public static final String KEY_FB_ACCESS_TOKEN = "fb_access_token";
        public static final String KEY_CONTACT_NAME = "contact_name";
        public static final String KEY_DATE_VALUE = "date_set";
        public static final String KEY_VIEW_POSITION = "view_position";
        public static final String KEY_CONTACT_NUMBER = "contact_number";
        public static final String KEY_SEND_CONTACTS = "send_contact";
        public static final String KEY_ADD_CONTACT = "add_contact";
        public static final String KEY_CONTACTS_UPDATED = "contacts_updated";
        public static final String KEY_CONTACT_BY_CONTAG_ID = "contact_contag_id" ;
        public static final String KEY_REQUEST_TYPE = "fuck_all_of_them";
        public static final String KEY_CURRENT_USER = "current_sodomized_user";
        public static final String KEY_USER_ARRAY = "user_fucked";
        public static final String KEY_SERVICE_TYPE = "fucking_service_fucker";
        public static final String KEY_SOCIAL_PLATFORMS = "shut_your_face_uncle_fucker";
        public static final String KEY_CONTACTS_UPDATED_TIMESTAMP = "contact_fucking_timestamp";
        public static final String KEY_USER_ID = "id";
        public static final String KEY_USER_PROFILE_RIGHTS = "profile_rights";
        public static final String KEY_USER_SOCIAL_PROFILE ="social_profile";
        public static final String KEY_USER_INTERESTS = "user_interests";
        public static final String KEY_USER_CREATED_ON = "created_on";
        public static final String KEY_USER_UPDATED_ON = "updated_on";
        public static final String KEY_USER_NAME = "name";
        public static final String KEY_USER_MOBILE_NUMBER = "mobile_number";
        public static final String KEY_USER_IS_MOBILE_VERIFIED = "is_mobile_verified";
        public static final String KEY_USER_CONTAG = "contag";
        public static final String KEY_USER_GENDER = "gender";
        public static final String KEY_USER_STATUS_UPDATE = "status_update";
        public static final String KEY_USER_LANDLINE_NUMBER = "landline_number";
        public static final String KEY_USER_EMERGENCY_CONTACT_NUMBER = "emergency_contact_number";
        public static final String KEY_USER_PERSONAL_EMAIL = "personal_email";
        public static final String KEY_USER_ADDRESS = "address";
        public static final String KEY_USER_DATE_OF_BIRTH = "date_of_birth";
        public static final String KEY_USER_MARITAL_STATUS = "marital_status";
        public static final String KEY_USER_MARRIAGE_ANNIVERSARY = "marriage_anniversary";
        public static final String KEY_USER_WORK_EMAIL = "work_email";
        public static final String KEY_USER_WORK_MOBILE_NUMBER = "work_mobile_number";
        public static final String KEY_USER_WORK_LANDLINE_NUMBER = "work_landline_number";
        public static final String KEY_USER_WORK_ADDRESS = "work_address";
        public static final String KEY_USER_WEBSITE = "website";
        public static final String KEY_USER_DESIGNATION = "designation";
        public static final String KEY_USER_WORK_FACEBOOK_PAGE = "work_facebook_page";
        public static final String KEY_USER_ANDROID_APP_LINK = "android_app_link";
        public static final String KEY_USER_IOS_APP_LINK = "ios_app_link";
        public static final String KEY_USER_AVATAR_URL = "avatar_url";
        public static final String KEY_USER_BLOOD_GROUP = "blood_group";
        public static final String KEY_USER_FIELD_VISIBILITY = "visibility";
        public static final String KEY_USER_PROFILE_TYPE = "come_fuck_me";
        public static final String KEY_CURRENT_USER_ID = "fucking_user_id";
        public static final String KEY_INTEREST_SUGGESTION_SLUG = "slug";
        public static final String KEY_INTEREST_SUGGESTION_LIST = "fucking_cock_sucking_list";
        public static final String KEY_PROFILE_REQUEST_FOR_USER = "for_user";
        public static final String KEY_PROFILE_REQUEST_TYPE = "request_type";
        public static final String KEY_SOCIAL_PLATFORM_ID = "social_platform_id";
        public static final String KEY_PLATFORM_ID = "platform_id";
        public static final String KEY_PLATFORM_SECRET = "platform_secret";
        public static final String KEY_PLATFORM_TOKEN = "platform_token";
        public static final String KEY_PLATFORM_PERMISSION = "platform_permissions";
        public static final String KEY_PLATFORM_EMAIL_ID = "platform_email";
        public static final String KEY_BUNDLE = "fucking_bundle";
        public static final String KEY_FEEDS_START_INDEX = "start_index";
        public static final String KEY_FEEDS_END_INDEX = "end_index";
        public static final String KEY_INTEREST_IDS = "interest_ids";

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
        public static final int RC_LINKEDIN = 1;
        public static final int RC_INSTAGRAM = 2;
        public static final long ONE_DAY_IN_MILLISECONDS = 24 * 60 * 60 * 1000;
        public static final String FILTER_PLATFORM = "Platform";
        public static final String FILTER_NAME = "Name";
        public static final String FILTER_BLOOD_GROUP = "Blood Group";
    }

    public class Types {
        public static final int REQUEST_GET_APP_USER = 1;
        public static final int REQUEST_GET_USER_BY_ID = 2 ;
        public static final int REQUEST_GET_USER_BY_CONTAG_ID = 3 ;
        public static final int REQUEST_POST = 4;
        public static final int REQUEST_PUT = 5;
        public static final int REQUEST_PUT_ADD_USER = 6 ;
        public static final int ITEM_CONTAG = 0;
        public static final int ITEM_NON_CONTAG = 1;
        public static final int ITEM_ADD_CONTAG = 2 ;
        public static final int FRAG_LOGIN = 0;
        public static final int FRAG_OTP = 1;
        public static final int FRAG_USER_DETAILS = 2;
        public static final int FRAG_CREATE_USER = 3;
        public static final int SERVICE_GET_ALL_PLATFORMS = 1;
        public static final int SERVICE_GET_INTEREST_SUGGESTIONS = 2;
        public static final int SERVICE_MAKE_PROFILE_REQUEST = 3;
        public static final int SERVICE_ADD_SOCIAL_PROFILE = 4;
        public static final int SERVICE_POST_INTERESTS = 5;
        public static final int PROFILE_PERSONAL = 1;
        public static final int PROFILE_PROFESSIONAL = 2;
        public static final int PROFILE_SOCIAL = 3;
        public static final int FIELD_STRING = 1;
        public static final int FIELD_LIST = 2;
        public static final int FIELD_DATE = 3;
        public static final int FIELD_SOCIAL = 4;
        public static final int LIST_BLOCKED_USERS = 1;
        public static final int LIST_MUTED_USERS = 2;
    }

    public class Urls {
        public static final String BASE_URL = "http://54-255-219-32-ln7o33jz2n6l.runscope.net/";
        public static final String URL_LOGIN = "/login/";
        public static final String URL_LOGOUT = "/logout/";
        public static final String URL_OTP = "/otp/";
        public static final String URL_USER = "/user/";
        public static final String URL_CONTACT = "/contact/";
        public static final String URL_FEEDS = "/feed/";
        public static final String URL_SOCIAL_PROFILE = "/social_profile/";
        public static final String URL_USER_INTEREST = "/interest/";
        public static final String URL_PROFILE_REQUEST = "/profile_request/";
        public static final String URL_NOTIFICATIONS = "/notification/" ;
    }

    public static class Arrays {
        public static final String[] USER_GENDER = {"female", "male", "other"};
        public static final String[] USER_BLOOD_GROUPS = {"O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-"};
        public static final String[] USER_MARITAL_STATUS = {"Married", "Single"};
        public static final String[] SEARCH_FILTER = {Values.FILTER_NAME,  Values.FILTER_PLATFORM, Values.FILTER_BLOOD_GROUP};
        public static final String[] SHARE_WITH = {"Private", "Public"};
    }

    public class Regex {
        public static final String PHONE_NUM = "^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$";
        public static final String CONTAG_ID = "^[a-zA-Z]{4}[0-9]{4}$";
    }
}
