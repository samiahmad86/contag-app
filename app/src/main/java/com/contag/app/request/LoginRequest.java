package com.contag.app.request;

import com.contag.app.model.APIInterface;
import com.contag.app.model.Login;
import com.contag.app.model.Response;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

public class LoginRequest extends RetrofitSpiceRequest<Response, APIInterface> {

    private Login login;
    public LoginRequest(Login mLogin) {
        super(Response.class, APIInterface.class);
        this.login = mLogin;
    }

    public Response loadDataFromNetwork() {
        return getService().login(login);
    }
}
