package com.contag.app.model;

/**
 * Created by tanay on 28/9/15.
 */
public class ProfileModel {

    public String key;
    public Object value;
    public int fieldType;
    public int inputType;

    public ProfileModel(String key, Object value, int fieldType, int inputType) {
        this.key = key;
        this.value = value;
        this.fieldType = fieldType;
        this.inputType = inputType;
    }

    public ProfileModel(String key, Object value, int fieldType) {
        this.key = key;
        this.value = value;
        this.fieldType = fieldType;
    }
}
