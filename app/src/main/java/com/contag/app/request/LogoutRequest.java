package com.contag.app.request;

import com.contag.app.model.APIInterface;
import com.contag.app.model.Response;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;


/**
 * Created by varunj on 22/10/15.
 */
public class LogoutRequest extends RetrofitSpiceRequest<Response, APIInterface> {


    public LogoutRequest() {
        super(Response.class, APIInterface.class);


    }

    @Override
    public Response loadDataFromNetwork() {
        return getService().logout();
    }
}

