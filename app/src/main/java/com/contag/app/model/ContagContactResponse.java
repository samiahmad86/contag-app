package com.contag.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by tanay on 15/9/15.
 */
public class ContagContactResponse {

    @Expose
    public long id;
    @SerializedName("user_interest")
    @Expose
    public ArrayList<InterestResponse> userInterest = new ArrayList<>();
    @Expose
    @SerializedName("social_profile")
    public ArrayList<SocialProfileResponse> socialProfile = new ArrayList<>();
    @SerializedName("created_on")
    @Expose
    public String createdOn;
    @SerializedName("updated_on")
    @Expose
    public String updatedOn;
    @Expose
    public String name;
    @SerializedName("mobile_number")
    @Expose
    public String mobileNumber;
    @SerializedName("registered_with")
    @Expose
    public String registeredWith;
    @Expose
    public String contag;
    @SerializedName("landline_number")
    @Expose
    public String landlineNumber;
    @SerializedName("emergency_contact_number")
    @Expose
    public String emergencyContactNumber;
    @SerializedName("is_mobile_verified")
    @Expose
    public boolean isMobileVerified;
    @Expose
    public String gender;
    @SerializedName("personal_email")
    @Expose
    public String personalEmail;
    @Expose
    public String address;
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
    @Expose
    public String website;
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
    @SerializedName("date_of_birth")
    @Expose
    public String dateOfBirth;
    @SerializedName("marital_status")
    @Expose
    public boolean maritalStatus;
    @SerializedName("marriage_anniversary")
    @Expose
    public String marriageAnniversary;
}
