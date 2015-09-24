package com.contag.app.model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    @Expose
    public long id;
    @SerializedName("profile_rights")
    @Expose
    public List<ProfileRight> profileRights = new ArrayList<ProfileRight>();
    @SerializedName("social_profile")
    @Expose
    public List<String> socialProfile = new ArrayList<String>();
    @SerializedName("user_interests")
    @Expose
    public List<String> userInterests = new ArrayList<String>();
    @SerializedName("created_on")
    @Expose
    public String createdOn;
    @SerializedName("updated_on")
    @Expose
    public String updatedOn;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("mobile_number")
    @Expose
    public String mobileNumber;
    @SerializedName("is_mobile_verified")
    @Expose
    public boolean isMobileVerified;
    @SerializedName("contag")
    @Expose
    public String contag;
    @SerializedName("gender")
    @Expose
    public String gender;
    @SerializedName("status_update")
    @Expose
    public String statusUpdate;
    @SerializedName("landline_number")
    @Expose
    public String landlineNumber;
    @SerializedName("emergency_contact_number")
    @Expose
    public String emergencyContactNumber;
    @SerializedName("personal_email")
    @Expose
    public String personalEmail;
    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("date_of_birth")
    @Expose
    public String dateOfBirth;
    @SerializedName("marital_status")
    @Expose
    public boolean maritalStatus;
    @SerializedName("marriage_anniversary")
    @Expose
    public String marriageAnniversary;
    @SerializedName("work_email")
    @Expose
    public String workEmail;
    @SerializedName("work_mobile_number")
    @Expose
    public String workMobileNumber;
    @SerializedName("work_landline_number")
    @Expose
    public String workLandlineNumber;
    @SerializedName("work_address")
    @Expose
    public String workAddress;
    @SerializedName("website")
    @Expose
    public String website;
    @SerializedName("designation")
    @Expose
    public String designation;
    @SerializedName("work_facebook_page")
    @Expose
    public String workFacebookPage;
    @SerializedName("android_app_link")
    @Expose
    public String androidAppLink;
    @SerializedName("ios_app_link")
    @Expose
    public String iosAppLink;
    @SerializedName("avatar_url")
    @Expose
    public String avatarUrl;
    @SerializedName("blood_group")
    @Expose
    public String bloodGroup;
}
