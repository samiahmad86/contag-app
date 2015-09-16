package com.contag.app.model;

import com.contag.app.config.Constants;

import java.util.ArrayList;

import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 * Created by tanay on 7/8/15.
 */
public interface APIInterface {
    @Headers({
            "Content-Type: application/json"
    })
    @POST(Constants.Urls.URL_LOGIN)
    Response login(@Body Login login);

    @Headers({
            "Content-Type: application/json"
    })
    @POST(Constants.Urls.URL_OTP)
    OTPResponse otp(@Header(Constants.Headers.HEADER_DEVICE_ID) String deviceId,
                    @Header(Constants.Headers.HEADER_PUSH_ID) String pushId,
                    @Header(Constants.Headers.HEADER_DEVICE_TYPE) String deviceType,
                    @Header(Constants.Headers.HEADER_APP_VERSION_ID) String appVersionId,
                    @Body OTP otp);

    @Headers({
            "Content-Type: application/json"
    })
    @POST(Constants.Urls.URL_USER)
    NewUserResponse newUser(@Header(Constants.Headers.HEADER_DEVICE_ID) String deviceId,
                            @Header(Constants.Headers.HEADER_PUSH_ID) String pushId,
                            @Header(Constants.Headers.HEADER_DEVICE_TYPE) String deviceType,
                            @Header(Constants.Headers.HEADER_APP_VERSION_ID) String appVersionId,
                            @Header(Constants.Headers.HEADER_TOKEN) String token,
                            @Body NewUser newUser);

    @Headers({
            "Content-Type: application/json"
    })
    @POST(Constants.Urls.URL_CONTACT)
    ContactResponse.ContactList sendContacts(@Header(Constants.Headers.HEADER_TOKEN) String token,
                                             @Body ArrayList<RawContacts> contacts);
}
