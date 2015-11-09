package com.contag.app.request;

import com.contag.app.model.APIInterface;
import com.contag.app.model.DeleteSocialProfile;
import com.contag.app.model.Response;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by archit on 7/11/15.
 */
public class DeleteSocialProfileRequest extends RetrofitSpiceRequest<Response, APIInterface> {

    private long id;
    public DeleteSocialProfileRequest(long id) {
        super(Response.class, APIInterface.class);
        this.id = id;
    }

    @Override
    public Response loadDataFromNetwork() throws Exception {
        return getService().deleteSocialPlatform(PrefUtils.getAuthToken(), id);
    }
}
