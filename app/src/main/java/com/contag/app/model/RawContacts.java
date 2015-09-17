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

    @Override
    public boolean equals(Object object) {

        RawContacts rc = (RawContacts) object;
        if(rc.contact_number.equalsIgnoreCase(this.contact_number) && rc.contact_name.equalsIgnoreCase(this.contact_name)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int sum = 0;
        for(int i = 0; i < contact_name.length(); i ++) {
            sum = sum + contact_name.charAt(i);
        }
        for(int i = 0; i < contact_number.length(); i ++) {
            sum = sum + contact_number.charAt(i);
        }
        return sum;
    }
}
