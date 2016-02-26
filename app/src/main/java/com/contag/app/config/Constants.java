package com.contag.app.config;

/**
 * Created by tanay on 30/7/15.
 * Use camel casing for the values of the constants.
 */
public class Constants {

    public interface Keys {
        String KEY_PROFILE_CATEGORY = "profile_category";
        String KEY_DATA = "fucking_data";
        String KEY_PREVIOUS_ACTIVITY = "previous_activity";
        String KEY_APP_PREFS = "app_prefs";
        String KEY_AUTH_TOKEN = "auth_token";
        String KEY_GCM_TOKEN = "gcm_token";
        String KEY_FRAGMENT_TYPE = "fragment_type";
        String KEY_NEW_USER = "is_new_user";
        String KEY_OTP = "otp";
        String KEY_FIELD_TYPE = "field_type";
        String KEY_FIELD_NAME = "field_name";
        String KEY_IS_PUBLIC = "is_public";
        String KEY_USER_IDS = "user_ids";
        String KEY_NUMBER = "number";
        String KEY_CONTAG_ID = "contag_id";
        String KEY_CONTAG_USER = "contag_user";
        String KEY_FB_ACCESS_TOKEN = "fb_access_token";
        String KEY_CONTACT_NAME = "contact_name";
        String KEY_DATE_VALUE = "date_set";
        String KEY_VIEW_POSITION = "view_position";
        String KEY_CONTACT_NUMBER = "contact_number";
        String KEY_SEND_CONTACTS = "send_contact";
        String KEY_ADD_CONTACT = "contact_add_request";
        String KEY_CONTACTS_UPDATED = "contacts_updated";
        String KEY_CONTACT_BY_CONTAG_ID = "contact_contag_id";
        String KEY_REQUEST_TYPE = "fuck_all_of_them";
        String KEY_CURRENT_USER = "current_sodomized_user";
        String KEY_USER_ARRAY = "user_fucked";
        String KEY_SERVICE_TYPE = "fucking_service_fucker";
        String KEY_SOCIAL_PLATFORMS = "shut_your_face_uncle_fucker";
        String KEY_CONTACTS_UPDATED_TIMESTAMP = "contact_fucking_timestamp";
        String KEY_USER_ID = "id";
        String KEY_USER_PROFILE_RIGHTS = "profile_rights";
        String KEY_USER_PROFILE_CUSTOM_SHARES = "profile_share";
        String KEY_USER_SOCIAL_PROFILE = "social_profile";
        String KEY_USER_INTERESTS = "user_interests";
        String KEY_USER_CREATED_ON = "created_on";
        String KEY_USER_UPDATED_ON = "updated_on";
        String KEY_USER_NAME = "name";
        String KEY_USER_MOBILE_NUMBER = "mobile_number";
        String KEY_USER_IS_MOBILE_VERIFIED = "is_mobile_verified";
        String KEY_USER_CONTAG = "contag";
        String KEY_USER_GENDER = "gender";
        String KEY_USER_STATUS_UPDATE = "status_update";
        String KEY_USER_LANDLINE_NUMBER = "landline_number";
        String KEY_USER_EMERGENCY_CONTACT_NUMBER = "emergency_contact_number";
        String KEY_USER_PERSONAL_EMAIL = "personal_email";
        String KEY_USER_ADDRESS = "address";
        String KEY_USER_DATE_OF_BIRTH = "date_of_birth";
        String KEY_USER_MARITAL_STATUS = "marital_status";
        String KEY_USER_MARRIAGE_ANNIVERSARY = "marriage_anniversary";
        String KEY_USER_WORK_EMAIL = "work_email";
        String KEY_USER_WORK_MOBILE_NUMBER = "work_mobile_number";
        String KEY_USER_WORK_LANDLINE_NUMBER = "work_landline_number";
        String KEY_USER_WORK_ADDRESS = "work_address";
        String KEY_USER_WEBSITE = "website";
        String KEY_USER_DESIGNATION = "designation";
        String KEY_USER_WORK_FACEBOOK_PAGE = "work_facebook_page";
        String KEY_USER_ANDROID_APP_LINK = "android_app_link";
        String KEY_USER_IOS_APP_LINK = "ios_app_link";
        String KEY_USER_AVATAR_URL = "avatar_url";
        String KEY_USER_BLOOD_GROUP = "blood_group";
        String KEY_USER_PROFILE_TYPE = "come_fuck_me";
        String KEY_CURRENT_USER_ID = "fucking_user_id";
        String KEY_INTEREST_SUGGESTION_SLUG = "slug";
        String KEY_INTEREST_SUGGESTION_LIST = "interest_suggestion_list";
        String KEY_PROFILE_REQUEST_FOR_USER = "for_user";
        String KEY_PROFILE_REQUEST_TYPE = "request_type";
        String KEY_SOCIAL_PLATFORM_NAME = "fucking_social_platform_name";
        String KEY_SOCIAL_PLATFORM_ID = "social_platform_id";
        String KEY_PLATFORM_ID = "platform_id";
        String KEY_PLATFORM_SECRET = "platform_secret";
        String KEY_PLATFORM_TOKEN = "platform_token";
        String KEY_USER_PLATFORM_USERNAME = "platform_username";
        String KEY_PLATFORM_PERMISSION = "platform_permissions";
        String KEY_PLATFORM_EMAIL_ID = "platform_email";
        String KEY_BUNDLE = "fucking_bundle";
        String KEY_FEEDS_START_INDEX = "start_index";
        String KEY_FEEDS_END_INDEX = "end_index";
        String KEY_INTEREST_IDS = "interest_ids";
        String KEY_EDIT_MODE_TOGGLE = "fucking_edit_fucker_mode";
        String KEY_PROFILE_REQUEST_ADD = "profile_request_add";
        String KEY_PROFILE_REQUEST_SHARE = "profile_request_share";
        String KEY_ADD_CONTAG = "contag_add_request";
        String KEY_COMING_FROM_NOTIFICATION = "fucking_coming_from_notification";
        String KEY_REQUEST_ID = "request_id";
        String KEY_REQUEST_FROM_USER_ID = "from_user_id";
        String KEY_REQUEST_FROM_USER_NAME = "from_user_name";
        String KEY_NOTIF_STATUS = "status";
        String KEY_INTRODUCED_USER = "introduced_user";
        String KEY_INTRODUCED_TO_USERS = "introduced_to_users";
        String KEY_INTRODUCTION_MESSAGE = "message";
        String KEY_NOTIF_USER_ID = "user_id";
        String KEY_NOTIFICATION_COUNT = "notification_count";
        String KEY_IMAGE_PATH = "fucking_profile_image_path";
        String KEY_COMPANY_NAME = "company_name";
        String KEY_NOTIFICATION_ID = "notification_id";
        String KEY_IS_CONTAG_CONTACT = "fucking_chutiya_victor";
        String KEY_MESSAGE = "send_this_azel_message";
        String KEY_TIMESTAMP = "bloody_fucking_message_timestamp";
        String KEY_LAUNCH_MODE = "launch_mode";
    }

    public interface Headers {
        String HEADER_DEVICE_TYPE = "X-Device-Type";
        String HEADER_PUSH_ID = "X-Push-ID";
        String HEADER_DEVICE_ID = "X-Device-ID";
        String HEADER_APP_VERSION_ID = "X-App-Version-ID";
        String HEADER_TOKEN = "token";
    }

    public interface Values {
        // Request code for google plus sign in
        int REQUEST_CODE_GPLUS_SIGN_IN = 0;
        int REQUEST_CODE_LINKEDIN = 1;
        int REQUEST_CODE_INSTAGRAM = 2;
        int REQUEST_CODE_IMAGE_UPLOAD = 3;
        long ONE_DAY_IN_MILLISECONDS = 24 * 60 * 60 * 1000;
        String FILTER_PLATFORM = "Platform";
        String FILTER_NAME = "Name";
        String FILTER_BLOOD_GROUP = "Blood Group";
        String FILTER_INTERESTS = "Interests";
        String STATUS_ALLOWED = "allow";
        String STATUS_DECLINE = "declined";
        int REQUEST_TIMEOUT = 60 * 1000;
        int READ_TIMEOUT = 60 * 1000;
    }

    public interface Types {
        int REQUEST_GET_CURRENT_USER = 1;
        int REQUEST_GET_USER_BY_ID = 2;
        int REQUEST_GET_USER_BY_CONTAG_ID = 3;
        int REQUEST_POST = 4;
        int REQUEST_PUT = 5;
        int REQUEST_PUT_ADD_USER = 6;
        int REQUEST_UPDATE_USER_INTEREST = 7;
        int REQUEST_POST_PRIVACY = 8;
        int REQUEST_ADD_CONTAG_NOTIFICATION = 9;
        int ITEM_CONTAG = 0;
        int ITEM_NON_CONTAG = 1;
        int ITEM_ADD_CONTAG = 2;
        int ITEM_SHARE_CONTAG = 3;
        int ITEM_INTRODUCE_CONTAG = 4;
        int FRAG_LOGIN = 0;
        int FRAG_OTP = 1;
        int FRAG_USER_DETAILS = 2;
        int FRAG_CREATE_USER = 3;
        int SERVICE_GET_ALL_PLATFORMS = 1;
        int SERVICE_GET_INTEREST_SUGGESTIONS = 2;
        int SERVICE_MAKE_PROFILE_REQUEST = 3;
        int SERVICE_ADD_SOCIAL_PROFILE = 4;
        int SERVICE_POST_INTERESTS = 5;
        int REQUEST_GET_USER_BY_USER_ID = 79;
        int SERVICE_UPLOAD_PROFILE_PICTURE = 69;
        int SERVICE_REJECT_FIELD_REQUEST = 6;
        int SERVICE_ALLOW_FIELD_REQUEST = 37;
        int SERVICE_SEND_LOG_MESSAGES = 6600;
        int PROFILE_PERSONAL = 1;
        int PROFILE_PROFESSIONAL = 2;
        int PROFILE_SOCIAL = 3;
        int PROFILE_STATUS = 4;
        int FIELD_STRING = 1;
        int FIELD_LIST = 2;
        int FIELD_DATE = 3;
        int FIELD_SOCIAL = 4;
        int FIELD_FACEBOOK = 5;
        int FIELD_TWITTER = 6;
        int FIELD_INSTAGRAM = 7;
        int FIELD_GOOGLE = 8;
        int FIELD_LINKEDIN = 9;
        int FIELD_EMAIL = 10;
        int FIELD_ADDRESS = 11;
        int FIELD_NUMBER = 12;
        int LIST_BLOCKED_USERS = 1;
        int LIST_MUTED_USERS = 2;
        int NOTIF_BUTTON_SHARE = 1;
        int NOTIF_BUTTON_ADD = 0;
        int REQUEST_POST_NFC_ADD_USER = 100;

        int NFC_OPEN_PROFILE = 1;

    }

    public interface Urls {
        String BASE_URL = "http://54.255.219.32/";
        String URL_LOGIN = "/login/";
        String URL_LOGOUT = "/logout/";
        String URL_OTP = "/otp/";
        String URL_USER = "/user/";
        String URL_CONTACT = "/contact/";
        String URL_ADD_CONTACT = "/add_contact/";
        String URL_FEEDS = "/feed/";
        String URL_SOCIAL_PROFILE = "/social_profile/";
        String URL_USER_INTEREST = "/interest/";
        String URL_PROFILE_REQUEST = "/profile_request/";
        String URL_PROFILE_PRIVACY = "/privacy/";
        String URL_NOTIFICATIONS = "/notification/";
        String URL_INTRODUCE = "/introduce/";
        String URL_IMAGE_UPLOAD = "/image_upload/";
        String URL_LOG = "/log/";
        String URL_ADD_BY_NFC = "/add_via_nfc/";
    }

    public static interface Arrays {
        String[] USER_GENDER = {"Gender", "female", "male", "other"};
        String[] USER_BLOOD_GROUPS = {"Blood Group", "O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-"};
        String[] USER_MARITAL_STATUS = {"Marital Status", "Married", "Single"};
        String[] SEARCH_FILTER = {Values.FILTER_NAME, Values.FILTER_PLATFORM,
                Values.FILTER_BLOOD_GROUP, Values.FILTER_INTERESTS};
        String[] SHARE_WITH = {"Private", "Public"};
    }

    public interface Regex {
        String PHONE_NUM = "^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$";
        String CONTAG_ID = "^[a-zA-Z]{4}[0-9]{4}$";
    }
}
