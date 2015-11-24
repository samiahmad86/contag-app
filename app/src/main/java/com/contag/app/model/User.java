package com.contag.app.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.contag.app.adapter.NotificationsAdapter;
import com.contag.app.config.Constants;
import com.contag.app.config.ContagApplication;
import com.contag.app.util.PrefUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class User {

    @SerializedName(Constants.Keys.KEY_USER_ID)
    @Expose
    public long id;

    @SerializedName(Constants.Keys.KEY_USER_PROFILE_CUSTOM_SHARES)
    @Expose
    public List<CustomShareResponse> customShares = new ArrayList<>();

    @SerializedName(Constants.Keys.KEY_USER_SOCIAL_PROFILE)
    @Expose
    public List<SocialProfileResponse> socialProfile = new ArrayList<>();
    @SerializedName(Constants.Keys.KEY_USER_INTERESTS)
    @Expose
    public List<InterestResponse> userInterest = new ArrayList<>();
    @SerializedName(Constants.Keys.KEY_USER_CREATED_ON)
    @Expose
    public String createdOn;
    @SerializedName(Constants.Keys.KEY_USER_UPDATED_ON)
    @Expose
    public String updatedOn;
    @SerializedName(Constants.Keys.KEY_USER_NAME)
    @Expose
    public String name;
    @SerializedName(Constants.Keys.KEY_USER_MOBILE_NUMBER)
    @Expose
    public String mobileNumber;
    @SerializedName(Constants.Keys.KEY_USER_IS_MOBILE_VERIFIED)
    @Expose
    public boolean isMobileVerified;
    @SerializedName(Constants.Keys.KEY_USER_CONTAG)
    @Expose
    public String contag;
    @SerializedName(Constants.Keys.KEY_USER_GENDER)
    @Expose
    public String gender;
    @SerializedName(Constants.Keys.KEY_USER_STATUS_UPDATE)
    @Expose
    public String statusUpdate;
    @SerializedName("landline_number")
    @Expose
    public String landlineNumber;
    @SerializedName(Constants.Keys.KEY_USER_EMERGENCY_CONTACT_NUMBER)
    @Expose
    public String emergencyContactNumber;
    @SerializedName(Constants.Keys.KEY_USER_PERSONAL_EMAIL)
    @Expose
    public String personalEmail;
    @SerializedName(Constants.Keys.KEY_USER_ADDRESS)
    @Expose
    public String address;
    @SerializedName(Constants.Keys.KEY_USER_DATE_OF_BIRTH)
    @Expose
    public String dateOfBirth;
    @SerializedName(Constants.Keys.KEY_USER_MARITAL_STATUS)
    @Expose
    public String maritalStatus;
    @SerializedName(Constants.Keys.KEY_USER_MARRIAGE_ANNIVERSARY)
    @Expose
    public String marriageAnniversary;
    @SerializedName(Constants.Keys.KEY_USER_WORK_EMAIL)
    @Expose
    public String workEmail;
    @SerializedName(Constants.Keys.KEY_USER_WORK_MOBILE_NUMBER)
    @Expose
    public String workMobileNumber;
    @SerializedName(Constants.Keys.KEY_USER_WORK_LANDLINE_NUMBER)
    @Expose
    public String workLandlineNumber;
    @SerializedName(Constants.Keys.KEY_USER_WORK_ADDRESS)
    @Expose
    public String workAddress;
    @SerializedName(Constants.Keys.KEY_USER_WEBSITE)
    @Expose
    public String website;
    @SerializedName(Constants.Keys.KEY_USER_DESIGNATION)
    @Expose
    public String designation;
    @SerializedName(Constants.Keys.KEY_USER_WORK_FACEBOOK_PAGE)
    @Expose
    public String workFacebookPage;
    @SerializedName(Constants.Keys.KEY_USER_ANDROID_APP_LINK)
    @Expose
    public String androidAppLink;
    @SerializedName(Constants.Keys.KEY_USER_IOS_APP_LINK)
    @Expose
    public String iosAppLink;
    @SerializedName(Constants.Keys.KEY_USER_AVATAR_URL)
    @Expose
    public String avatarUrl;
    @SerializedName(Constants.Keys.KEY_USER_BLOOD_GROUP)
    @Expose
    public String bloodGroup;


    public static void saveUserInterest(DaoSession session, ArrayList<Interest> interestList) {
        InterestDao interestDao = session.getInterestDao();
        interestDao.queryBuilder().where(InterestDao.Properties.ContagUserId.eq(PrefUtils.getCurrentUserID())).buildDelete().executeDeleteWithoutDetachingEntities();
        session.clear();
        Log.d("iList", "Deleted interests");

        if (interestList != null && interestList.size() > 0) {
            Log.d("iList", "Saving user interest");
            for (Interest interest : interestList) {
                interestDao.insertOrReplace(interest);
                Log.d("iList", "Interest inserted: " + interest.getName());
            }
        }
    }

    public static ContagContag getContagContagObject(User user) {

        ContagContag mContagContag = new ContagContag(user.id);
        mContagContag.setCreatedOn(user.createdOn);
        mContagContag.setUpdatedOn(user.updatedOn);
        mContagContag.setName(user.name);
        mContagContag.setMobileNumber(user.mobileNumber);
        mContagContag.setContag(user.contag);
        mContagContag.setLandLineNumber(user.landlineNumber);
        mContagContag.setEmergencyContactNumber(user.emergencyContactNumber);
        mContagContag.setIsMobileVerified(user.isMobileVerified);
        mContagContag.setGender(user.gender);
        mContagContag.setAddress(user.address);
        mContagContag.setWorkEmail(user.workEmail);
        mContagContag.setWorkMobileNumber(user.workMobileNumber);
        mContagContag.setWorkLandLineNumber(user.workLandlineNumber);
        mContagContag.setWebsite(user.website);
        mContagContag.setDesignation(user.designation);
        mContagContag.setWorkFacebookPage(user.workFacebookPage);
        mContagContag.setAndroidAppLink(user.androidAppLink);
        mContagContag.setIosAppLink(user.iosAppLink);
        mContagContag.setAvatarUrl(Constants.Urls.BASE_URL + user.avatarUrl);
        mContagContag.setBloodGroup(user.bloodGroup);
        mContagContag.setDateOfBirth(user.dateOfBirth);
        mContagContag.setIsMobileVerified(user.isMobileVerified);
        mContagContag.setMaritalStatus(user.maritalStatus);
        mContagContag.setMarriageAnniversary(user.marriageAnniversary);
        mContagContag.setPersonalEmail(user.personalEmail);
        mContagContag.setWorkAddress(user.workAddress);
        mContagContag.setStatus_update(user.statusUpdate);

        return mContagContag;
    }

    public static ArrayList<Interest> getInterestList(List<InterestResponse> interests,
                                                      User user, ContagContag cc) {
        ArrayList<Interest> mInterest = new ArrayList<>();
        for (InterestResponse ir : interests) {
            Interest interest = new Interest(ir.id);
            interest.setName(ir.name);
            interest.setContagUserId(user.id);
            interest.setContagContag(cc);
            mInterest.add(interest);
        }
        Log.d("myuser", "size of interests: " + mInterest.size());
        return mInterest;
    }

    public static void storeInterests(ArrayList<Interest> interestList, DaoSession session) {
        if (interestList != null) {
            Log.d("myuser", "Going to store interests now");
            InterestDao interestDao = session.getInterestDao();
            for (Interest interest : interestList) {
                interestDao.insertOrReplace(interest);
            }
        }
    }


    public static ArrayList<SocialProfile> getSocialProfileList(List<SocialProfileResponse> socialProfiles, User user, ContagContag cc) {

        ArrayList<SocialProfile> mProfiles = new ArrayList<>();
        for (SocialProfileResponse mSocialProfileResponse : socialProfiles) {
            SocialProfile socialProfile = new SocialProfile(mSocialProfileResponse.id);
            socialProfile.setPlatform_id(mSocialProfileResponse.platformId);
            socialProfile.setSocial_platform(mSocialProfileResponse.socialPlatform);
            socialProfile.setPlatform_username(mSocialProfileResponse.platformUsername);
            socialProfile.setContagContag(cc);
            socialProfile.setContagUserId(user.id);
            mProfiles.add(socialProfile);
        }
        Log.d("myuser", "size of social profiles: " + mProfiles.size());

        return mProfiles;
    }

    public static void storeSocialProfile(List<SocialProfile> socialProfileList, DaoSession session) {

        if (socialProfileList != null) {
            Log.d("myuser", "Going to store social profiles");
            SocialProfileDao spDao = session.getSocialProfileDao();
            for (SocialProfile profile : socialProfileList) {
                spDao.insertOrReplace(profile);
            }
        }
    }

    public static ArrayList<CustomShare> getCustomShareList(List<CustomShareResponse> customShares, ContagContag cc) {

        ArrayList<CustomShare> mShares = new ArrayList<>();
        for (CustomShareResponse csr : customShares) {
            CustomShare cs = new CustomShare();
            cs.setField_name(csr.fieldName);
            Log.d("ShareFubar", "FieldName: "+  csr.fieldName) ;
            cs.setUser_ids(csr.userIDS);
            cs.setIs_public(csr.isPublic);
            cs.setIs_private(csr.isPrivate);
            cs.setContagContag(cc);
            mShares.add(cs);
        }
        Log.d("ShareFubar", "size of custom share: " + mShares.size());
        return mShares;

    }

    public static void storeCustomShare(ArrayList<CustomShare> customShares, DaoSession session) {

        if (customShares != null) {


            CustomShareDao csDao = session.getCustomShareDao();
            for (CustomShare cs : customShares) {
                Log.d("ShareFubar", "cd user ids: " + cs.getUser_ids());
                Log.d("ShareFubar", "cs is public: " + cs.getIs_public());
                Log.d("ShareFubar", "cs field name:" + cs.getField_name());
                long result = csDao.insertOrReplace(cs);
                Log.d("ShareFubar", "cs result:" + result);
            }
        }
    }

    public static String getSharesAsString(String fieldName, String userID, Context mContext) {
        CustomShare cs = getCustomShareByFieldName(fieldName, mContext.getApplicationContext());
        ArrayList<String> userIDS = new ArrayList<>();
        if (cs.getUser_ids() != null && cs.getUser_ids().length() != 0) {
            if (cs.getUser_ids().indexOf(",") == -1) {
                userIDS.add(cs.getUser_ids());
            } else {
                String[] arr = cs.getUser_ids().split(",");
                for(String str : arr) {
                    userIDS.add(str);
                }
            }
            if (!userIDS.contains(userID)) {
                userIDS.add(userID);
            }
        } else {
            Log.d(NotificationsAdapter.TAG, "pissu chod");
            return userID;
        }
        if(userIDS.size() == 1) {
            Log.d(NotificationsAdapter.TAG, "pissu chod 2");
            return userIDS.get(0);
        }
        return TextUtils.join(",", userIDS);

    }


    public static void updatePrivacy(String fieldName, boolean isPublic, String userIDS, Context mContext) {

        CustomShare cs = getCustomShareByFieldName(fieldName, mContext);

        cs.setUser_ids(userIDS);
        cs.setIs_public(isPublic);
        cs.update();

    }

    public static CustomShare getCustomShareByFieldName(String fieldName, Context mContext) {
        DaoSession session = ((ContagApplication) mContext).getDaoSession();
        CustomShareDao csDao = session.getCustomShareDao();

        try {
            CustomShare cs = csDao.queryBuilder().where(CustomShareDao.
                    Properties.Field_name.eq(fieldName)).list().get(0);

            return cs;
        } catch (IndexOutOfBoundsException ex) {

            CustomShare customShare = new CustomShare(csDao.count() + 1l, fieldName, null, null, null, PrefUtils.getCurrentUserID());
            csDao.insert(customShare);
            return customShare;
        }
    }


}
