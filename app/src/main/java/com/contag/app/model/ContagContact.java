package com.contag.app.model;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by tanay on 16/9/15.
 */
public class ContagContact extends RealmObject {

    private long id;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    private RealmList<Interest> userInterest;

    public void setUserInterest(RealmList<Interest> userInterest) {
        this.userInterest = userInterest;
    }

    public RealmList<Interest> getUserInterest() {
        return this.userInterest;
    }

    private String createdOn;

    public void setCreatedOn(String createdOn) {
        if (createdOn != null) {
            this.createdOn = createdOn;
        } else {
            this.createdOn = "";
        }
    }

    public String getCreatedOn() {
        return this.createdOn;
    }

    private String updatedOn;

    public void setUpdatedOn(String updatedOn) {
        if (updatedOn != null) {
            this.updatedOn = updatedOn;
        } else {
            this.updatedOn = "";
        }
    }

    public String getUpdatedOn() {
        return this.getUpdatedOn();
    }

    private String name;

    public void setName(String name) {
        if (name != null) {
            this.name = name;
        } else {
            this.name = "";
        }
    }

    public String getName() {
        return this.name;
    }

    private String mobileNumber;

    public void setMobileNumber(String mobileNumber) {
        if (mobileNumber != null) {
            this.mobileNumber = mobileNumber;
        } else {
            this.mobileNumber = "";
        }
    }

    public String getMobileNumber() {
        return this.mobileNumber;
    }

    private String registeredWith;

    public void setRegisteredWith(String registeredWith) {
        if (registeredWith != null) {
            this.registeredWith = registeredWith;
        } else {
            this.registeredWith = "";
        }
    }

    public String getRegisteredWith() {
        return this.registeredWith;
    }

    private String contag;

    public void setContag(String contag) {
        if (contag != null) {
            this.contag = contag;
        } else {
            this.contag = "";
        }
    }

    public String getContag() {
        return this.contag;
    }

    private String landlineNumber;

    public void setLandlineNumber(String number) {
        if (number != null) {
            this.landlineNumber = number;
        } else {
            this.landlineNumber = "";
        }
    }

    public String getLandlineNumber() {
        return this.landlineNumber;
    }

    private String emergencyContactNumber;

    public void setEmergencyContactNumber(String number) {
        if (number != null) {
            this.emergencyContactNumber = number;
        } else {
            this.emergencyContactNumber = "";
        }
    }

    public String getEmergencyContactNumber() {
        return this.emergencyContactNumber;
    }

    private boolean isMobileVerified;

    public void setMobileVerified(boolean val) {
        this.isMobileVerified = val;
    }

    public boolean isMobileVerified() {
        return this.isMobileVerified;
    }

    private String gender;

    public void setGender(String gender) {
        if (gender != null) {
            this.gender = gender;
        } else {
            this.gender = "";
        }
    }

    public String getGender() {
        return this.gender;
    }

    private String personalEmail;

    public void setPersonalEmail(String email) {
        if (email != null) {
            this.personalEmail = email;
        } else {
            this.personalEmail = "";
        }
    }

    public String getPersonalEmail() {
        return this.personalEmail;
    }

    private String address;

    public void setAddress(String address) {
        if (address != null) {
            this.address = address;
        } else {
            this.address = "";
        }
    }

    public String getAddress() {
        return this.address;
    }

    private String workEmail;

    public void setWorkEmail(String workEmail) {
        if (workEmail != null) {
            this.workEmail = workEmail;
        } else {
            this.workEmail = "";
        }
    }

    public String getWorkEmail() {
        return this.workEmail;
    }

    private String workMobileNumber;

    public void setWorkMobileNumber(String mobileNumber) {
        if (mobileNumber != null) {
            this.workMobileNumber = mobileNumber;
        } else {
            this.workMobileNumber = "";
        }
    }

    public String getWorkMobileNumber() {
        return this.workMobileNumber;
    }

    private String workLandlineNumber;

    public void setWorkLandlineNumber(String number) {
        if (number != null) {
            this.workLandlineNumber = number;
        } else {
            this.workLandlineNumber = "";
        }
    }

    public String getWorkLandlineNumber() {
        return this.workLandlineNumber;
    }

    private String workAddress;

    public void setWorkAddress(String address) {
        if (address != null) {
            this.workAddress = address;
        } else {
            this.workAddress = "";
        }
    }

    public String getWorkAddress() {
        return this.workAddress;
    }

    private String website;

    public void setWebsite(String website) {
        if (website != null) {
            this.website = website;
        } else {
            this.website = "";
        }
    }

    public String getWebsite() {
        return this.website;
    }

    private String designation;

    public void setDesignation(String designation) {
        if (designation != null) {
            this.designation = designation;
        } else {
            this.designation = "";
        }
    }

    public String getDesignation() {
        return this.designation;
    }

    private String workFacebookPage;

    public void setWorkFacebookPage(String workFacebookPage) {
        if (workFacebookPage != null) {
            this.workFacebookPage = workFacebookPage;
        } else {
            this.workFacebookPage = "";
        }
    }

    public String getWorkFacebookPage() {
        return this.workFacebookPage;
    }

    private String androidAppLink;

    public void setAndroidAppLink(String appLink) {
        if (appLink != null) {
            this.androidAppLink = appLink;
        } else {
            this.androidAppLink = "";
        }
    }

    public String getAndroidAppLink() {
        return this.androidAppLink;
    }

    private String iosAppLink;

    public void setIosAppLink(String appLink) {
        if (appLink != null) {
            this.iosAppLink = appLink;
        } else {
            this.iosAppLink = "";
        }
    }

    public String getIosAppLink() {
        return this.iosAppLink;
    }

    private String avatarUrl;

    public void setAvatarUrl(String avatarUrl) {
        if (avatarUrl != null) {
            this.avatarUrl = avatarUrl;
        } else {
            this.avatarUrl = "";
        }
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    private String bloodGroup;

    public void setBloodGroup(String bloodGroup) {
        if (bloodGroup != null) {
            this.bloodGroup = bloodGroup;
        } else {
            this.bloodGroup = "";
        }
    }

    public String getBloodGroup() {
        return this.bloodGroup;
    }

    private String dateOfBirth;

    public void setDateOfBirth(String dateOfBirth) {
        if(dateOfBirth != null) {
        this.dateOfBirth = dateOfBirth;
    } else {
            this.dateOfBirth = "";
        }
    }

    public String getDateOfBirth() {
        return this.dateOfBirth;
    }

    private boolean maritalStatus;

    public void setMaritalStatus(boolean status) {
        this.maritalStatus = status;
    }

    public boolean isMaritalStatus() {
        return this.maritalStatus;
    }

    private String marriageAnniversary;

    public void setMarriageAnniversary(String anniversary) {
        if(anniversary != null) {
            this.marriageAnniversary = anniversary;
        } else {
            this.marriageAnniversary = "";
        }
    }

    public String getMarriageAnniversary() {
        return this.marriageAnniversary;
    }
}
