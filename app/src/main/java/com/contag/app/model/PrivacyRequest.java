package com.contag.app.model;

import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by varunj on 05/11/15.
 */
public class PrivacyRequest extends RetrofitSpiceRequest<MessageResponse, APIInterface> {
    private ProfilePrivacyRequestModel profilePrivacy;

    public PrivacyRequest(ProfilePrivacyRequestModel profilePrivacy) {
        super(MessageResponse.class, APIInterface.class);
        this.profilePrivacy = profilePrivacy;
    }

    @Override
    public MessageResponse loadDataFromNetwork() throws Exception {
        return getService().setProfilePrivacy(PrefUtils.getAuthToken(), profilePrivacy);
    }
}
