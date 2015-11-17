package com.contag.app.request;

import com.contag.app.model.APIInterface;
import com.contag.app.model.SocialRequestModel;
import com.contag.app.model.User;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
/**
 * Created by tanay on 7/10/15.
 */
public class SocialProfileRequest extends RetrofitSpiceRequest<User, APIInterface> {

    private SocialRequestModel.List srmList;


    public SocialProfileRequest(SocialRequestModel.List srmList) {
        super(User.class, APIInterface.class);
        this.srmList = srmList;
    }

    @Override
    public User loadDataFromNetwork() throws Exception {
        return getService().addSocialPlatform(PrefUtils.getAuthToken(), srmList);
    }
}
