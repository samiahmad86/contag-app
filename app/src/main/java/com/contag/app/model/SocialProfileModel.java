package com.contag.app.model;

/**
 * Created by archit on 5/11/15.
 */
public class SocialProfileModel {

    public SocialProfile mSocialProfile;
    public SocialPlatform mSocialPlatform;
    public boolean isAdded;
    public int mViewType;

    public SocialProfileModel(SocialProfile socialProfile, SocialPlatform socialPlatform, boolean isAdded, int viewType) {
        this.isAdded = isAdded;
        this.mSocialPlatform = socialPlatform;
        this.mSocialProfile = socialProfile;
        this.mViewType = viewType;
    }

    public SocialProfileModel(SocialPlatform socialPlatform, boolean isAdded, int viewType) {
        this.isAdded = isAdded;
        this.mSocialPlatform = socialPlatform;
        this.mViewType = viewType;
    }
}
