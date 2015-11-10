package com.contag.app.model;

import com.google.gson.annotations.Expose;

/**
 * Created by archit on 7/11/15.
 */
public class DeleteSocialProfile {

    @Expose
    public long id;

    public DeleteSocialProfile(long id) {
        this.id = id;
    }
}
