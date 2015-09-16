package com.contag.app.model;

import io.realm.RealmObject;

/**
 * Created by tanay on 16/9/15.
 */
public class ContagContact extends RealmObject {

    public long id;
    public String userInterest;
    public String createdOn;
    public String updatedOn;
    public String name;
    public String mobileNumber;
    public String registeredWith;
    public String contag;
    public String landlineNumber;
    public String emergencyContactNumber;
    public boolean isMobileVerified;
    public String gender;
    public String personalEmail;
    public String address;
    public String workEmail;
    public String workMobileNumber;
    public String workLandlineNumber;
    public String workAddress;
    public String website;
    public String designation;
    public String workFacebookPage;
    public String androidAppLink;
    public String iosAppLink;
    public String avatarUrl;
    public String bloodGroup;
    public String dateOfBirth;
    public boolean maritalStatus;
    public String marriageAnniversary;
}
