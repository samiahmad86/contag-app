package com.contag.app.request;

import com.contag.app.model.APIInterface;
import com.contag.app.model.FeedsResponse;
import com.contag.app.model.NotificationsResponse;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by tanay on 13/10/15.
 */
public class NotificationsRequest extends RetrofitSpiceRequest<NotificationsResponse.NotificationList, APIInterface> {

    int startIndex, endIndex;

    public NotificationsRequest(int startIndex, int endIndex) {
        super(NotificationsResponse.NotificationList.class, APIInterface.class);
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public NotificationsResponse.NotificationList loadDataFromNetwork() throws Exception {
        return getService().getNotifications(PrefUtils.getAuthToken(), startIndex, endIndex);
    }
}
