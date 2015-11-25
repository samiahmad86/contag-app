package com.contag.app.config;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.contag.app.activity.BaseActivity;
import com.contag.app.activity.HomeActivity;
import com.contag.app.activity.InstagramActivity;
import com.contag.app.activity.LinkedInActivity;
import com.contag.app.activity.LoginActivity;
import com.contag.app.activity.NewUserActivity;
import com.contag.app.activity.NotificationsActivity;
import com.contag.app.activity.UserActivity;
import com.contag.app.service.ContactService;
import com.contag.app.service.CustomService;
import com.contag.app.service.GcmRegisterIntentService;
import com.contag.app.service.UserService;

import java.util.List;

/**
 * Created by tanay on 30/7/15.
 * All the activity calls will be done through this class
 */

public class Router {

    public static void startLoginActivity(Context mContext, String currentClassName, int flags) {
        Intent iStartLogin = new Intent(mContext, LoginActivity.class);
        iStartLogin.putExtra(Constants.Keys.KEY_PREVIOUS_ACTIVITY, currentClassName);
        iStartLogin.addFlags(flags);
        mContext.startActivity(iStartLogin);
    }

    public static void startHomeActivity(Context mContext, String currentClassName) {
        Intent iStartHome = new Intent(mContext, HomeActivity.class);
        iStartHome.putExtra(Constants.Keys.KEY_PREVIOUS_ACTIVITY, currentClassName);
        mContext.startActivity(iStartHome);
    }

    public static void startNewUserActivity(Context mContext, String className, long phoneNumber) {
        Intent iNewUserDetails = new Intent(mContext, NewUserActivity.class);
        iNewUserDetails.putExtra(Constants.Keys.KEY_PREVIOUS_ACTIVITY, className);
        iNewUserDetails.putExtra(Constants.Keys.KEY_NUMBER, phoneNumber);
        mContext.startActivity(iNewUserDetails);
    }

    public static void startUserActivity(Context mContext, String className, long userID) {
        Intent iUsrProf = new Intent(mContext, UserActivity.class);
        iUsrProf.putExtra(Constants.Keys.KEY_PREVIOUS_ACTIVITY, className);
        iUsrProf.putExtra(Constants.Keys.KEY_USER_ID, userID);
        mContext.startActivity(iUsrProf);
    }


    public static void startEditUserActivity(Context context, String className, long userID, Bundle requestBundle,
                                             int fragmentToBeOpened, String fieldName, boolean isComingFromNotification) {
        Intent iEditUserProfile = new Intent(context, UserActivity.class);
        iEditUserProfile.putExtra(Constants.Keys.KEY_PREVIOUS_ACTIVITY, className);
        iEditUserProfile.putExtra(Constants.Keys.KEY_USER_ID, userID);
        iEditUserProfile.putExtra(Constants.Keys.KEY_FRAGMENT_TYPE, fragmentToBeOpened);
        iEditUserProfile.putExtra(Constants.Keys.KEY_FIELD_NAME, fieldName);
        iEditUserProfile.putExtra(Constants.Keys.KEY_COMING_FROM_NOTIFICATION, isComingFromNotification);
        iEditUserProfile.putExtra(Constants.Keys.KEY_DATA, requestBundle);
        context.startActivity(iEditUserProfile);
    }

    public static void startNotificationsActivity(Context mContext, String className) {
        Intent userNotifications = new Intent(mContext, NotificationsActivity.class);
        userNotifications.putExtra(Constants.Keys.KEY_PREVIOUS_ACTIVITY, className);
        mContext.startActivity(userNotifications);
    }

    public static void startContactService(Context mContext, boolean sendContacts) {
        Log.d("Condev", "In router: Call received to start contact service");
        Intent iStartContactService = new Intent(mContext, ContactService.class);
        iStartContactService.putExtra(Constants.Keys.KEY_SEND_CONTACTS, sendContacts);
        mContext.startService(iStartContactService);
    }


    public static void startUserServiceForPrivacy(Context mContext, String fieldName, Boolean isPublic, String userIDS){
        Intent privacyIntent = new Intent(mContext, UserService.class) ;
        privacyIntent.putExtra(Constants.Keys.KEY_REQUEST_TYPE, Constants.Types.REQUEST_POST_PRIVACY) ;
        privacyIntent.putExtra(Constants.Keys.KEY_FIELD_NAME, fieldName) ;
        privacyIntent.putExtra(Constants.Keys.KEY_IS_PUBLIC, isPublic) ;
        privacyIntent.putExtra(Constants.Keys.KEY_USER_IDS, userIDS) ;
        mContext.startService(privacyIntent) ;
    }

    public static void startServiceToGetUserByUserID(Context context, long userID, boolean isContagContact) {
        Intent getUserIntent = new Intent(context, UserService.class);
        getUserIntent.putExtra(Constants.Keys.KEY_NOTIF_USER_ID, userID);
        getUserIntent.putExtra(Constants.Keys.KEY_REQUEST_TYPE, Constants.Types.REQUEST_GET_USER_BY_USER_ID);
        getUserIntent.putExtra(Constants.Keys.KEY_IS_CONTAG_CONTACT, isContagContact);
        context.startService(getUserIntent);
    }

    public static void startProfilePicutreUpload(Context mContext, String filePath) {
        Intent uploadImageIntent = new Intent(mContext, UserService.class);
        uploadImageIntent.putExtra(Constants.Keys.KEY_IMAGE_PATH, filePath);
        uploadImageIntent.putExtra(Constants.Keys.KEY_REQUEST_TYPE, Constants.Types.SERVICE_UPLOAD_PROFILE_PICTURE);
        mContext.startService(uploadImageIntent);
    }

    public static void sendFieldRequestNotificationResponse(Context mContext, long requestID, int serviceType) {
        Intent iFieldRequestNotifResponse = new Intent(mContext, UserService.class);
        iFieldRequestNotifResponse.putExtra(Constants.Keys.KEY_REQUEST_ID, requestID);
        iFieldRequestNotifResponse.putExtra(Constants.Keys.KEY_REQUEST_TYPE, serviceType);
        mContext.startService(iFieldRequestNotifResponse);
    }

    public static void addContagUser(Context mContext, Long userID){
        Log.d("conadd", "Adding this user to server") ;
        Intent iStartContactService = new Intent(mContext, ContactService.class);
        iStartContactService.putExtra(Constants.Keys.KEY_ADD_CONTACT, true);
        iStartContactService.putExtra(Constants.Keys.KEY_USER_ID, userID);
        mContext.startService(iStartContactService);
    }

    public static void startGcmRegisterService(Context context) {
        Intent iRegisterGcm = new Intent(context, GcmRegisterIntentService.class);
        context.startService(iRegisterGcm);
    }

    public static void startUserService(Context context, int type) {
        startUserService(context, type, null);
    }

    public static void startUserService(Context context, int type, String userArray) {
        startUserService(context, type, userArray, 0);
    }

    public static void startUserService(Context context, int type, String userArray, int profileType) {
        Intent iUser = new Intent(context, UserService.class);
        iUser.putExtra(Constants.Keys.KEY_REQUEST_TYPE, type);
        iUser.putExtra(Constants.Keys.KEY_USER_ARRAY, userArray);
        iUser.putExtra(Constants.Keys.KEY_USER_PROFILE_TYPE, profileType);
        context.startService(iUser);
    }

    public static void startUserService(Context context, int type, long userID) {
        Intent iUser = new Intent(context, UserService.class);
        iUser.putExtra(Constants.Keys.KEY_REQUEST_TYPE, type);
        iUser.putExtra(Constants.Keys.KEY_USER_ID, userID);
        context.startService(iUser);
    }

    public static void startInterestUpdateService(Context context, String interestList) {
        Log.d("iList", "Starting service");
        Intent iInterest = new Intent(context, UserService.class);
        iInterest.putExtra(Constants.Keys.KEY_REQUEST_TYPE, Constants.Types.REQUEST_UPDATE_USER_INTEREST);
        iInterest.putExtra(Constants.Keys.KEY_INTEREST_IDS, interestList);
        context.startService(iInterest);
    }

    public static void startGmailApp(Context context) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setType("plain/text");
        sendIntent.setData(Uri.parse("team@contagapp.com"));
        sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"team@contagapp.com"});
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for contag");
        context.startActivity(sendIntent);
    }

    public static void openGmailForEmailID(Context context, String emailID) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setType("plain/text");
        sendIntent.setData(Uri.parse(emailID));
        sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailID});
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "");
        context.startActivity(sendIntent);
    }

    public static void openGoogleMapsWithAddress(Context context, String address) {
        String map = "http://maps.google.co.in/maps?q=" + address;
        Intent openMapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
        context.startActivity(openMapIntent);
    }

    public static void getSocialPlatforms(Context context, int type) {
        Intent iCustomService = new Intent(context, CustomService.class);
        iCustomService.putExtra(Constants.Keys.KEY_SERVICE_TYPE, type);
        context.startService(iCustomService);
    }

    public static void startProfileRequestService(Context mContext, int type, long id, String fieldName, String fieldType) {
        Intent iCustomService = new Intent(mContext, CustomService.class);
        iCustomService.putExtra(Constants.Keys.KEY_SERVICE_TYPE, type);
        iCustomService.putExtra(Constants.Keys.KEY_PROFILE_REQUEST_FOR_USER, id);
        iCustomService.putExtra(Constants.Keys.KEY_PROFILE_REQUEST_TYPE, fieldName);
        iCustomService.putExtra(Constants.Keys.KEY_FIELD_TYPE, fieldType);
        mContext.startService(iCustomService);
    }

    public static void openSocialProfile(Context context, String link) {
        Intent socialIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        context.startActivity(socialIntent);
    }

    public static void startLinkendInLoginActivity(Context context, int requestCode, long linkedInId) {
        Intent iLinkedIn = new Intent(context, LinkedInActivity.class);
        iLinkedIn.putExtra(Constants.Keys.KEY_SOCIAL_PLATFORM_ID, linkedInId);
        ((BaseActivity) context).startActivityForResult(iLinkedIn, requestCode);
    }

    public static void startInstagramLoginActivity(Context context, int requestCode, long instagramId) {
        Intent iInsta = new Intent(context, InstagramActivity.class);
        iInsta.putExtra(Constants.Keys.KEY_SOCIAL_PLATFORM_ID, instagramId);
        ((BaseActivity) context).startActivityForResult(iInsta, requestCode);
    }

    public static void openFacebookProfile(Context context, String facebookId) {
        Intent facebookIntent = getFacebookIntent(context, facebookId);
        context.startActivity(facebookIntent);
    }

    public static Intent getFacebookIntent(Context context, String facebookId) {

        String url = "https://www.facebook.com/";

        try {
            // Check if FB app is even installed
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);

            String facebookScheme = "fb://facewebmodal/f?href=" + url + facebookId;
            return new Intent(Intent.ACTION_VIEW, Uri.parse(facebookScheme));
        } catch (Exception e) {

            // Cache and Open a url in browser
            String facebookProfileUri = "https://www.facebook.com/" + facebookId;
            return new Intent(Intent.ACTION_VIEW, Uri.parse(facebookProfileUri));
        }

    }

    public static void openTwitterProfile(Context context, String twitterUserName) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + twitterUserName)));
        } catch (Exception e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/" + twitterUserName)));
        }
    }

    public static void openGooglePlusProfile(Context context, String gPlusId) {
        String link = "https://plus.google.com/" + gPlusId + "/posts";
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
    }

    public static void openInstagramProfile(Context context, String userName) {
        Uri uri = Uri.parse("http://instagram.com/_u/" + userName);
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

        likeIng.setPackage("com.instagram.android");

        try {
            context.startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com/" + userName)));
        }
    }

    public static void openLinkedInProfile(Context context, String linkedInID) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://profile/" + linkedInID));
        final PackageManager packageManager = context.getPackageManager();
        final List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.isEmpty()) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.linkedin.com/profile/view?id=" + linkedInID));
        }
        context.startActivity(intent);
    }

    public static void updateSocialProfile(Context context, Bundle args) {
        Intent iSocial = new Intent(context, CustomService.class);
        iSocial.putExtra(Constants.Keys.KEY_SERVICE_TYPE, Constants.Types.SERVICE_ADD_SOCIAL_PROFILE);
        iSocial.putExtra(Constants.Keys.KEY_BUNDLE, args);
        context.startService(iSocial);
    }

    public static void getInterestSuggestions(Context context, String slug, int viewPosition, int type) {
        Intent iSuggestions = new Intent(context, CustomService.class);
        iSuggestions.putExtra(Constants.Keys.KEY_INTEREST_SUGGESTION_SLUG, slug);
        iSuggestions.putExtra(Constants.Keys.KEY_VIEW_POSITION, viewPosition);
        iSuggestions.putExtra(Constants.Keys.KEY_SERVICE_TYPE, type);
        context.startService(iSuggestions);
    }

    public static void postInterestSuggestions(Context context, String interestIds, int type, int viewPosition) {
        Intent iPostSuggestions = new Intent(context, CustomService.class);
        iPostSuggestions.putExtra(Constants.Keys.KEY_INTEREST_IDS, interestIds);
        iPostSuggestions.putExtra(Constants.Keys.KEY_SERVICE_TYPE, type);
        iPostSuggestions.putExtra(Constants.Keys.KEY_VIEW_POSITION, viewPosition);
        context.startService(iPostSuggestions);
    }

}
