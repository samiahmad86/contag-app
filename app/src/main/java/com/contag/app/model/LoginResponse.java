package com.contag.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tanay on 8/8/15.
 */
public class LoginResponse {
    @Expose
    public long id;
    @Expose
    public String name;
    @SerializedName("contact_number")
    @Expose
    public String contactNumber;
    @SerializedName("agent_type")
    @Expose
    public String agentType;
    @Expose
    @SerializedName("auth_token")
    public String authToken;
    @Expose
    @SerializedName("new_user")
    public boolean isNewUser;
}
