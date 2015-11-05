package com.contag.app.request;

import com.contag.app.model.APIInterface;
import com.contag.app.model.Response;
import com.contag.app.model.SocialRequestModel;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.ArrayList;

/**
 * Created by tanay on 7/10/15.
 */
public class SocialProfileRequest extends RetrofitSpiceRequest<Response, APIInterface> {

    private SocialRequestModel srm;
    private SocialRequestModel.List srmList;

    public SocialProfileRequest(SocialRequestModel socialRequestModel) {
        super(Response.class, APIInterface.class);
        this.srm = socialRequestModel;
    }

    public SocialProfileRequest(SocialRequestModel.List srmList) {
        super(Response.class, APIInterface.class);
        this.srmList = srmList;
    }

    @Override
    public Response loadDataFromNetwork() throws Exception {
        return getService().addSocialPlatform(PrefUtils.getAuthToken(), srmList);
    }
}
