package com.contag.app.model;

import com.contag.app.config.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tanay on 15/9/15.
 */
public class RawContacts {

    public String contact_name;

    public String contact_number;

    public RawContacts(String name, String phoneNum) {
        this.contact_name = name;
        this.contact_number = phoneNum;
    }
}
