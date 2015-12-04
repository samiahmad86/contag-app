package com.contag.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tanay on 16/9/15.
 */
public class InterestResponse {

    @Expose
    @SerializedName("id")
    public long id ;

    @Expose
    @SerializedName("interest_id")
    public long interest_id;

    @Expose
    @SerializedName("interest_name")
    public String name;
}
