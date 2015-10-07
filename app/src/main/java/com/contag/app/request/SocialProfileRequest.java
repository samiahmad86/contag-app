package com.contag.app.request;

import com.contag.app.model.APIInterface;
import com.contag.app.model.Response;
import com.contag.app.model.SocialRequestModel;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by tanay on 7/10/15.
 */
public class SocialProfileRequest extends RetrofitSpiceRequest<Response, APIInterface> {

    private SocialRequestModel srm;

    public SocialProfileRequest(SocialRequestModel socialRequestModel) {
        super(Response.class, APIInterface.class);
        this.srm = socialRequestModel;
    }

    @Override
    public Response loadDataFromNetwork() throws Exception {
        return getService().addSocialPlatform(PrefUtils.getAuthToken(), srm);
    }
}
