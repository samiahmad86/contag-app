package com.contag.app.model;

/**
 * Created by tanaytandon on 21/11/15.
 */
public class SocialProfileViewModel {

    public int fieldType;
    public boolean isAdded;
    public SocialPlatform mSocialPlatform;
    public SocialProfile mSocialProfile;

    public SocialProfileViewModel(SocialPlatform socialPlatform, SocialProfile socialProfile, boolean isAdded, int fieldType) {
        this.isAdded = isAdded;
        this.fieldType = fieldType;
        this.mSocialPlatform = socialPlatform;
        this.mSocialProfile = socialProfile;
    }

    public SocialProfileViewModel(SocialPlatform socialPlatform, boolean isAdded, int fieldType) {
        this.isAdded = isAdded;
        this.fieldType = fieldType;
        this.mSocialPlatform = socialPlatform;
    }

}
