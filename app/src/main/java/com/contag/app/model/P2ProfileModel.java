package com.contag.app.model;

/**
 * Created by archit on 14/11/15.
 */
public class P2ProfileModel {

    public String key;
    public String value;
    public int viewType;
    public String[] values;
    public int inputType;
    public String regex;

    public P2ProfileModel(String key, String value, int viewType, int inputType) {
        this.key = key;
        this.value = value;
        this.viewType = viewType;
        this.inputType = inputType;
    }

    public P2ProfileModel(String key, String value, int viewType, int inputType, String regex) {
        this.key = key;
        this.value = value;
        this.viewType = viewType;
        this.inputType = inputType;
        this.regex = regex;
    }

    public P2ProfileModel(String key, String value, int viewType, String[] values) {
        this.key = key;
        this.value = value;
        this.viewType = viewType;
        this.values = values;
    }
}
