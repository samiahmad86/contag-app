package com.contag.app.request;

import com.contag.app.model.APIInterface;
import com.contag.app.model.FieldRequestNotificationResponse;
import com.contag.app.model.Response;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by tanaytandon on 18/11/15.
 */
public class FieldRequest extends RetrofitSpiceRequest<Response, APIInterface> {

    private FieldRequestNotificationResponse mFieldRequestNotificationResponse;

    public FieldRequest(FieldRequestNotificationResponse mFieldRequestNotificationResponse) {
        super(Response.class, APIInterface.class);
        this.mFieldRequestNotificationResponse = mFieldRequestNotificationResponse;
    }

    @Override
    public Response loadDataFromNetwork() throws Exception {
        return getService().sendResponseToNotification(PrefUtils.getAuthToken(), mFieldRequestNotificationResponse);
    }
}
