package com.contag.app.model;

import com.google.gson.annotations.Expose;

/**
 * Created by tanay on 14/9/15.
 */
public class Login {

    @Expose
    public long number;

    public Login(long number) {
        this.number = number;
    }
}
