package com.contag.app.model;

import io.realm.RealmObject;

/**
 * Created by tanay on 16/9/15.
 */
public class Interest extends RealmObject {

    private long id;
    private String name;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
