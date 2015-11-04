package com.contag.app.request;

import android.util.Log;

import com.contag.app.config.Constants;
import com.contag.app.model.APIInterface;
import com.contag.app.model.User;
import com.contag.app.model.UserRequestModel;
import com.contag.app.util.PrefUtils;
import com.google.gson.Gson;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by tanay on 25/9/15.
 */
public class UserRequest extends RetrofitSpiceRequest<User, APIInterface> {

    private int type;
    private String newUserArray;
    private long userID ;

    public UserRequest(int type) {
        super(User.class, APIInterface.class);
        this.type = type;
    }

    public UserRequest(int type, String userArray) {
        super(User.class, APIInterface.class);
        this.type = type;
        this.newUserArray = userArray;
    }

    public UserRequest(int type, long userID){
        super(User.class, APIInterface.class);
        this.type = type ;
        this.userID = userID ;
    }

    @Override
    public User loadDataFromNetwork() throws Exception {

        Log.d("UserService", "In load data from network") ;

        if(Constants.Types.REQUEST_GET_CURRENT_USER == type) {

            return getService().getUser(PrefUtils.getAuthToken());
        }
        else if (Constants.Types.REQUEST_GET_USER_BY_ID == type){

            return getService().getUserByUserID(PrefUtils.getAuthToken(), userID) ;
        }
        else if(Constants.Types.REQUEST_PUT == type) {

            Log.d("UserService", "put request type");
            Log.d("UserService", PrefUtils.getAuthToken());

            User user = getService().setUser(PrefUtils.getAuthToken(), new UserRequestModel(newUserArray));
            Gson gson  = new Gson();
            Log.d("UserService", gson.toJson(new UserRequestModel(newUserArray)).toString());
            Log.d("UserService", gson.toJson(user).toString());
            return user;
        }
        return null;
    }
}
