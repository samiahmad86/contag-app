package com.contag.app.config;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import com.contag.app.activity.BaseActivity;
import com.contag.app.activity.HomeActivity;
import com.contag.app.activity.InstagramActivity;
import com.contag.app.activity.LinkedInActivity;
import com.contag.app.activity.LoginActivity;
import com.contag.app.activity.NewUserActivity;
import com.contag.app.activity.UserActivity;
import com.contag.app.service.ContactService;
import com.contag.app.service.CustomService;
import com.contag.app.service.GcmRegisterIntentService;
import com.contag.app.service.UserService;
import com.google.android.gms.appindexing.Action;

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

    public static void startContactService(Context mContext, boolean sendContacts) {
        Intent iStartContactService = new Intent(mContext, ContactService.class);
        iStartContactService.putExtra(Constants.Keys.KEY_SEND_CONTACTS, sendContacts);
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


    public static void startCustomService(Context context, int type) {
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

    public static void startLinkendInLoginActivity(Context context, int requestCode) {
        Intent iLinkedIn = new Intent(context, LinkedInActivity.class);
        ((BaseActivity)context).startActivityForResult(iLinkedIn, requestCode);
    }

    public static void startInstagramLoginActivity(Context context, int requestCode) {
        Intent iInsta = new Intent(context, InstagramActivity.class);
        ((BaseActivity)context).startActivityForResult(iInsta, requestCode);
    }

    public static void openFacebookProfile(Context context, String facebookUrl) {
        try {
            int versionCode = context.getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {
                Uri uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
                context.startActivity(new Intent(Intent.ACTION_VIEW, uri));;
            } else {
                // open the Facebook app using the old method (fb://profile/id or fb://page/id)
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                        ("fb://page/" + facebookUrl.substring(facebookUrl.lastIndexOf("/") + 1))));
            }
        } catch (PackageManager.NameNotFoundException e) {
            // Facebook is not installed. Open the browser
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));
        }
    }

    public static void openTwitterProfile(Context context, String twitterUserName) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + twitterUserName)));
        }catch (Exception e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/" + twitterUserName)));
        }
    }

    public static void openGooglePlusProfile(Context context, String gPlusId) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(gPlusId)));
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
