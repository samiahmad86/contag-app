package com.contag.app.model;

import android.util.Log;

import com.contag.app.config.Constants;
import com.contag.app.util.PrefUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class User {

    @SerializedName(Constants.Keys.KEY_USER_ID)
    @Expose
    public long id;
    @SerializedName(Constants.Keys.KEY_USER_PROFILE_RIGHTS)
    @Expose
    public List<ProfileRight> profileRights = new ArrayList<ProfileRight>();
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

        Log.d("iList", "Saving user interest") ;
        if (interestList != null && interestList.size() > 0) {
            InterestDao interestDao = session.getInterestDao();
            interestDao.queryBuilder().where(InterestDao.Properties.ContagUserId.eq(PrefUtils.getCurrentUserID())).buildDelete() ;
            Log.d("iList", "Deleted interests") ;
            for (Interest interest : interestList) {
                interestDao.insertOrReplace(interest);
                Log.d("iList", "Interest inserted") ;
            }
        }
    }
}
