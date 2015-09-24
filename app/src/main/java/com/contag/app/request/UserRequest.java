package com.contag.app.request;

import com.contag.app.config.Constants;
import com.contag.app.model.APIInterface;
import com.contag.app.model.NewUser;
import com.contag.app.model.NewUserResponse;
import com.contag.app.model.User;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import org.json.JSONArray;

import java.util.HashMap;

/**
 * Created by tanay on 25/9/15.
 */
public class UserRequest extends RetrofitSpiceRequest<User, APIInterface> {

    private int type;
    private JSONArray fuckArray;

    public UserRequest(int type) {
        super(User.class, APIInterface.class);
        this.type = type;
    }

    public UserRequest(int type, JSONArray userArray) {
        super(User.class, APIInterface.class);
        this.type = type;
        this.fuckArray = userArray;
    }

    @Override
    public User loadDataFromNetwork() throws Exception {
        if(Constants.Types.REQUEST_GET == type) {
            return getService().getUser(PrefUtils.getAuthToken());
        } else if(Constants.Types.REQUEST_PUT == type) {
            return getService().setUser(PrefUtils.getAuthToken(), fuckArray);
        }
        return null;
    }
}
