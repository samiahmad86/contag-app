package com.contag.app.request;

import com.contag.app.model.APIInterface;
import com.contag.app.model.InterestPost;
import com.contag.app.model.Response;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by varunj on 15/10/15.
 */
public class InterestRequest extends RetrofitSpiceRequest<Response, APIInterface> {

    private InterestPost mInterestPost;

    public InterestRequest(InterestPost postInterest) {
        super(Response.class, APIInterface.class);
        this.mInterestPost = postInterest;
    }

    @Override
    public Response loadDataFromNetwork() throws Exception {
        return getService().addUserInterests(PrefUtils.getAuthToken(), mInterestPost);
    }
}
