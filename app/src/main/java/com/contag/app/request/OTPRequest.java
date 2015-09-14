package com.contag.app.request;

import com.contag.app.config.Constants;
import com.contag.app.model.APIInterface;
import com.contag.app.model.OTP;
import com.contag.app.model.OTPResponse;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.HashMap;

/**
 * Created by tanay on 14/9/15.
 */
public class OTPRequest extends RetrofitSpiceRequest<OTPResponse, APIInterface> {

    private OTP otp;
    private HashMap<String, String> headers;

    public OTPRequest(OTP otp, HashMap<String, String> hmHeaders) {
        super(OTPResponse.class, APIInterface.class);
        this.otp = otp;
        this.headers = hmHeaders;
    }

    @Override
    public OTPResponse loadDataFromNetwork() throws Exception {
        return getService().otp(headers.get(Constants.Headers.HEADER_DEVICE_ID),
                headers.get(Constants.Headers.HEADER_PUSH_ID),
                headers.get(Constants.Headers.HEADER_DEVICE_TYPE),
                headers.get(Constants.Headers.HEADER_APP_VERSION_ID),
                otp);
    }
}
