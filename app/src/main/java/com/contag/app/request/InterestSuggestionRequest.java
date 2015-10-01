package com.contag.app.request;

import com.contag.app.model.APIInterface;
import com.contag.app.model.InterestSuggestion;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by tanay on 1/10/15.
 */
public class InterestSuggestionRequest extends RetrofitSpiceRequest<InterestSuggestion.List, APIInterface> {

    private String mSlug;

    public InterestSuggestionRequest(String slug) {
        super(InterestSuggestion.List.class, APIInterface.class);
        this.mSlug = slug;
    }

    @Override
    public InterestSuggestion.List loadDataFromNetwork() throws Exception {
        return getService().getInterestSuggestions(PrefUtils.getAuthToken(), mSlug);
    }
}
