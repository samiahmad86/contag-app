package com.contag.app.request;

import com.contag.app.model.APIInterface;
import com.contag.app.model.FeedsResponse;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by tanay on 13/10/15.
 */
public class FeedsRequest extends RetrofitSpiceRequest<FeedsResponse.FeedList, APIInterface> {

    int startIndex, endIndex;

    public FeedsRequest(int startIndex, int endIndex) {
        super(FeedsResponse.FeedList.class, APIInterface.class);
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public FeedsResponse.FeedList loadDataFromNetwork() throws Exception {
        return getService().getFeeds(PrefUtils.getAuthToken(), startIndex, endIndex);
    }
}
