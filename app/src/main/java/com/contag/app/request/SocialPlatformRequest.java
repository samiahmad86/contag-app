package com.contag.app.request;

import com.contag.app.model.APIInterface;
import com.contag.app.model.SocialPlatformResponse;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by tanay on 27/9/15.
 */
public class SocialPlatformRequest extends RetrofitSpiceRequest<SocialPlatformResponse.List, APIInterface> {

    public SocialPlatformRequest() {
        super(SocialPlatformResponse.List.class, APIInterface.class);
    }

    @Override
    public SocialPlatformResponse.List loadDataFromNetwork() throws Exception {
        return getService().getAllSocialPlatforms(PrefUtils.getAuthToken());
    }
}
