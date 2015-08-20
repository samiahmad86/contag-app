package com.contag.app.model;

import com.contag.app.config.Constants;
import com.contag.app.model.LoginResponse;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by tanay on 7/8/15.
 */
public interface APIInterface {
    @Headers({
            "Content-Type: application/json"
    })
    @POST("/agent_login/")
    LoginResponse login(@Header(Constants.Keys.HEADER_TOKEN) String token, @Body Login login);

}
