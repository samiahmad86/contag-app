package com.contag.app.model;

import com.google.gson.annotations.Expose;

/**
 * Created by tanay on 25/9/15.
 */
public class UserRequestModel {

    @Expose
    public String user;

    public UserRequestModel(String user) {
        this.user = user;
    }
}
