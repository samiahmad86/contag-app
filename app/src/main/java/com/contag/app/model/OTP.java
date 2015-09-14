package com.contag.app.model;

import com.google.gson.annotations.Expose;

/**
 * Created by tanay on 14/9/15.
 */
public class OTP {

    @Expose
    public long number;

    @Expose
    public int otp;

    public OTP(long number, int otp) {
        this.number = number;
        this.otp = otp;
    }
}
