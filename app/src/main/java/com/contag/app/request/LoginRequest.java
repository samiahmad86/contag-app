package com.contag.app.request;

import com.contag.app.model.APIInterface;
import com.contag.app.model.Login;
import com.contag.app.model.LoginResponse;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by Kartikey on 8/18/2015.
 */
public class LoginRequest extends RetrofitSpiceRequest<LoginResponse, APIInterface> {

    private Login loginObject;
    private String deviceID;

    public LoginRequest(String deviceID, Login login) {
        super(LoginResponse.class, APIInterface.class);
        this.loginObject = login;
        this.deviceID = deviceID;
    }

    public LoginResponse loadDataFromNetwork() {
        return getService().login(deviceID, loginObject);
    }
}
