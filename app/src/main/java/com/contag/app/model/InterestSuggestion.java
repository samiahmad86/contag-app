package com.contag.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by tanay on 1/10/15.
 */
public class InterestSuggestion {

    @Expose
    @SerializedName("id")
    public long interest_id;

    @Expose
    @SerializedName("interest")
    public String name;

    public static class List extends ArrayList<InterestSuggestion> {
    }
}
