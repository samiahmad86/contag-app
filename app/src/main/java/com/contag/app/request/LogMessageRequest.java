package com.contag.app.request;

import com.contag.app.model.APIInterface;
import com.contag.app.model.MessageResponse;
import com.contag.app.model.Response;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by tanaytandon on 03/12/15.
 */
public class LogMessageRequest extends RetrofitSpiceRequest<MessageResponse, APIInterface> {

    private String logMessage;
    private long timestamp;

    public LogMessageRequest(String logMessage, long timestamp) {
        super(MessageResponse.class, APIInterface.class);
        this.logMessage = logMessage;
        this.timestamp = timestamp;
    }

    @Override
    public MessageResponse loadDataFromNetwork() throws Exception {
        return getService().logFuckingMessage(PrefUtils.getAuthToken(), timestamp, logMessage);
    }
}
