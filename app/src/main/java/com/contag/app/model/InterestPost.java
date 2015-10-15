package com.contag.app.model;

import com.contag.app.config.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by varunj on 15/10/15.
 */
public class InterestPost {

    @Expose
    @SerializedName(Constants.Keys.KEY_INTEREST_IDS)
    public String interestId;

    public InterestPost(String commaSeperatedInterestIds) {
        this.interestId = commaSeperatedInterestIds;
    }
}
