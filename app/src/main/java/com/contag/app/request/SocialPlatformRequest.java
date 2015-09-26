package com.contag.app.request;

import com.contag.app.model.APIInterface;
import com.contag.app.model.SocialPlatform;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by tanay on 27/9/15.
 */
public class SocialPlatformRequest extends RetrofitSpiceRequest<SocialPlatform.List, APIInterface> {

    public SocialPlatformRequest() {
        super(SocialPlatform.List.class, APIInterface.class);
    }

    @Override
    public SocialPlatform.List loadDataFromNetwork() throws Exception {
        return getService().getAllSocialPlatforms(PrefUtils.getAuthToken());
    }
}
