package com.contag.app.request;

import com.contag.app.config.Constants;
import com.contag.app.model.APIInterface;
import com.contag.app.model.NewUser;
import com.contag.app.model.NewUserResponse;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.HashMap;

/**
 * Created by tanay on 14/9/15.
 */
public class NewUserRequest extends RetrofitSpiceRequest<NewUserResponse, APIInterface> {

    private HashMap<String, String> headers;
    private NewUser newUser;

    public NewUserRequest(HashMap<String, String> hmHeaders, NewUser mNewUser) {
        super(NewUserResponse.class, APIInterface.class);
        this.headers = hmHeaders;
        this.newUser = mNewUser;
    }

    @Override
    public NewUserResponse loadDataFromNetwork() throws Exception {
        return getService().newUser(headers.get(Constants.Headers.HEADER_DEVICE_ID),
                headers.get(Constants.Headers.HEADER_PUSH_ID),
                headers.get(Constants.Headers.HEADER_DEVICE_TYPE),
                headers.get(Constants.Headers.HEADER_APP_VERSION_ID),
                headers.get(Constants.Headers.HEADER_TOKEN),
                newUser);
    }
}
