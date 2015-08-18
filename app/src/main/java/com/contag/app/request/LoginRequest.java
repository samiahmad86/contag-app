package com.contag.app.request;

import com.contag.app.listeners.APIInterface;
import com.contag.app.model.LoginResponse;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by Kartikey on 8/18/2015.
 */
public class LoginRequest extends RetrofitSpiceRequest<LoginResponse, APIInterface> {

    public LoginRequest() {
        super(LoginResponse.class, APIInterface.class);
    }

    public LoginResponse loadDataFromNetwork() {
        return getService().login("","");
    }
}
