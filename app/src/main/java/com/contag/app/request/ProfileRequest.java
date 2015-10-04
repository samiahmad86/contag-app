package com.contag.app.request;

import com.contag.app.model.APIInterface;
import com.contag.app.model.MessageResponse;
import com.contag.app.model.ProfileRequestModel;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by tanay on 2/10/15.
 */
public class ProfileRequest extends RetrofitSpiceRequest<MessageResponse, APIInterface> {

    private ProfileRequestModel prm;

    public ProfileRequest(ProfileRequestModel mProfileRequestModel) {
        super(MessageResponse.class, APIInterface.class);
        this.prm = mProfileRequestModel;
    }

    @Override
    public MessageResponse loadDataFromNetwork() throws Exception {
        return getService().makeProfileRequest(PrefUtils.getAuthToken(), prm);
    }
}
