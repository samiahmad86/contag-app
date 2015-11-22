package com.contag.app.model;

/**
 * Created by tanaytandon on 21/11/15.
 */
public class ProfileViewModel {

    public String key;
    public String value;
    public boolean isAdded;
    public int fieldType;
    public SocialProfile socialProfile;

    public ProfileViewModel(String key, String value, int fieldType) {
        this.key = key;
        this.fieldType = fieldType;
        this.value = value;
        this.isAdded = (value != null && value.length() != 0);
    }

    public ProfileViewModel(String key, SocialProfile socialProfile, int fieldType) {
        this.key = key;
        this.fieldType = fieldType;
        this.socialProfile = socialProfile;
        this.isAdded = socialProfile != null;
    }

    public ProfileViewModel(String key, int fieldType) {
        this.key = key;
        this.fieldType = fieldType;
        this.isAdded = false;
    }
}
