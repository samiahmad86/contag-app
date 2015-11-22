package com.contag.app.request;

import android.util.Log;

import com.contag.app.model.APIInterface;
import com.contag.app.model.ContagIntroductionRequestModel;
import com.contag.app.model.Response;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by varunj on 15/10/15.
 */
public class IntroductionRequest extends RetrofitSpiceRequest<Response, APIInterface> {

    private ContagIntroductionRequestModel crm;

    public IntroductionRequest(ContagIntroductionRequestModel crm) {
        super(Response.class, APIInterface.class);
        this.crm = crm;
    }

    @Override
    public Response loadDataFromNetwork() throws Exception {
        Log.d("Introduce", "in load from networks") ;
        return getService().introduceContag(PrefUtils.getAuthToken(), crm);
    }
}
