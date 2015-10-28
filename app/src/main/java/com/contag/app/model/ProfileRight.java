package com.contag.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileRight {

    @SerializedName("unit_type")
    @Expose
    public String unitType;
    @SerializedName("unit_id")
    @Expose
    public long unitId;
    @SerializedName("is_public")
    @Expose
    public boolean isPublic;
//    @SerializedName("is_private")
//    @Expose
//    public boolean isPrivate;
//    @SerializedName("is_custom")
//    @Expose
//    public boolean is;

}
