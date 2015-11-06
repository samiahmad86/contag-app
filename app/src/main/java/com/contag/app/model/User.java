package com.contag.app.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.contag.app.config.Constants;
import com.contag.app.config.ContagApplication;
import com.contag.app.util.PrefUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
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


    public static void saveUserInterest(DaoSession session, ArrayList<Interest> interestList){
        InterestDao interestDao = session.getInterestDao();
        interestDao.queryBuilder().where(InterestDao.Properties.ContagUserId.eq(PrefUtils.getCurrentUserID())).buildDelete().executeDeleteWithoutDetachingEntities();
        session.clear() ;
        Log.d("iList", "Deleted interests") ;

        if (interestList != null && interestList.size() > 0) {
            Log.d("iList", "Saving user interest") ;
            for (Interest interest : interestList) {
                interestDao.insertOrReplace(interest);
                Log.d("iList", "Interest inserted: "+  interest.getName()) ;
            }
        }
    }

    public static ContagContag getContagContagObject(User user){

        ContagContag cc = new ContagContag(user.id);
        cc.setCreatedOn(user.createdOn);
        cc.setUpdatedOn(user.updatedOn);
        cc.setName(user.name);
        cc.setMobileNumber(user.mobileNumber);
        cc.setContag(user.contag);
        cc.setLandLineNumber(user.landlineNumber);
        cc.setEmergencyContactNumber(user.emergencyContactNumber);
        cc.setIsMobileVerified(user.isMobileVerified);
        cc.setGender(user.gender);
        cc.setAddress(user.address);
        cc.setWorkEmail(user.workEmail);
        cc.setWorkMobileNumber(user.workMobileNumber);
        cc.setWorkLandLineNumber(user.workLandlineNumber);
        cc.setWebsite(user.website);
        cc.setDesignation(user.designation);
        cc.setWorkFacebookPage(user.workFacebookPage);
        cc.setAndroidAppLink(user.androidAppLink);
        cc.setIosAppLink(user.iosAppLink);
        cc.setAvatarUrl(Constants.Urls.BASE_URL + user.avatarUrl);
        cc.setBloodGroup(user.bloodGroup);
        cc.setDateOfBirth(user.dateOfBirth);
        cc.setIsMobileVerified(user.isMobileVerified);
        cc.setMaritalStatus(user.maritalStatus);
        cc.setMarriageAnniversary(user.marriageAnniversary);
        cc.setPersonalEmail(user.personalEmail);
        cc.setWorkAddress(user.workAddress);
        cc.setStatus_update(user.statusUpdate);

        return cc ;
    }

    public static ArrayList<Interest> getInterestList(List<InterestResponse> interests,
                                                       User user, ContagContag cc){
        ArrayList<Interest> mInterest = new ArrayList<>() ;
        for (InterestResponse ir : interests) {
            Interest interest = new Interest(ir.id);
            interest.setName(ir.name);
            interest.setContagUserId(user.id);
            interest.setContagContag(cc);
            mInterest.add(interest) ;
        }
        Log.d("myuser", "size of interests: " + mInterest.size()) ;
        return mInterest ;
    }

    public static void storeInterests(ArrayList<Interest> interestList, DaoSession session){
        if (interestList != null) {
            Log.d("myuser", "Going to store interests now") ;
            InterestDao interestDao = session.getInterestDao();
            for(Interest interest: interestList){
                interestDao.insertOrReplace(interest) ;
            }
        }
    }


    public static ArrayList<SocialProfile> getSocialProfileList(List<SocialProfileResponse> socialProfiles, User user, ContagContag cc){

        ArrayList<SocialProfile> mProfiles = new ArrayList<>() ;
        for (SocialProfileResponse spr : socialProfiles) {
            SocialProfile socialProfile = new SocialProfile();
            socialProfile.setPlatform_id(spr.platformId);
            socialProfile.setSocial_platform(spr.socialPlatform);
            socialProfile.setContagContag(cc);
            socialProfile.setContagUserId(user.id);
            mProfiles.add(socialProfile) ;
        }
        Log.d("myuser", "size of social profiles: " + mProfiles.size()) ;

        return mProfiles ;
    }

    public static void storeSocialProfile(List<SocialProfile> socialProfileList, DaoSession session) {

        if (socialProfileList != null) {
            Log.d("myuser", "Going to store social profiles") ;
            SocialProfileDao spDao = session.getSocialProfileDao();
            for(SocialProfile profile: socialProfileList){
                spDao.insertOrReplace(profile);
            }
        }
    }

    public static ArrayList<CustomShare> getCustomShareList(List<CustomShareResponse> customShares, ContagContag cc){

            ArrayList<CustomShare> mShares = new ArrayList<>() ;
            for (CustomShareResponse csr : customShares) {
                CustomShare cs = new CustomShare();
                cs.setField_name(csr.fieldName);
                cs.setUser_ids(csr.userIDS);
                cs.setIs_public(csr.isPublic);
                cs.setIs_private(csr.isPrivate);
                cs.setContagContag(cc);
                mShares.add(cs);
            }
        Log.d("myuser", "size of custom share: " + mShares.size()) ;
        return mShares ;

        }

    public static void storeCustomShare(ArrayList<CustomShare> customShares,  DaoSession session){

        if(customShares != null ){


            CustomShareDao csDao = session.getCustomShareDao() ;
            for(CustomShare cs: customShares) {
                Log.d("shave","cd user ids: " +cs.getUser_ids()) ;
                Log.d("shave","cs is public: " + cs.getIs_public()) ;
                Log.d("shave", "cs field name:" + cs.getField_name()) ;
                long result = csDao.insertOrReplace(cs);
                Log.d("shave", "cs result:" + result) ;
            }
        }
    }

    public static String getSharesAsString(String fieldName, String userID, Context mContext){
        CustomShare cs = getCustomShareByFieldName(fieldName, mContext.getApplicationContext()) ;
        List<String> userIDS = Arrays.asList(cs.getUser_ids().split(",")) ;

        if(!userIDS.contains(userID))
            userIDS.add(userID) ;

        return TextUtils.join(",",userIDS) ;

    }


    public static void updatePrivacy(String fieldName, boolean isPublic, String userIDS, Context mContext ){

        CustomShare cs = getCustomShareByFieldName(fieldName, mContext) ;

        cs.setUser_ids(userIDS) ;
        cs.setIs_public(isPublic);
        cs.update() ;

    }

    public static CustomShare getCustomShareByFieldName(String fieldName, Context mContext){
        DaoSession session = ((ContagApplication) mContext).getDaoSession();
        CustomShareDao csDao = session.getCustomShareDao();

        CustomShare cs = csDao.queryBuilder().where(CustomShareDao.
                Properties.Field_name.eq(fieldName)).list().get(0);

        return cs;

    }



}
