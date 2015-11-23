package com.contag.app.request;

import com.contag.app.model.APIInterface;
import com.contag.app.model.Response;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import retrofit.mime.TypedFile;

/**
 * Created by tanaytandon on 21/11/15.
 */
public class ImageUploadRequest extends RetrofitSpiceRequest<Response, APIInterface> {

    private TypedFile mProfileImage;

    public ImageUploadRequest(TypedFile mProfileImage) {
        super(Response.class, APIInterface.class);
        this.mProfileImage = mProfileImage;
    }

    @Override
    public Response loadDataFromNetwork() throws Exception {
        return getService().uploadImage(PrefUtils.getAuthToken(), mProfileImage);
    }
}
