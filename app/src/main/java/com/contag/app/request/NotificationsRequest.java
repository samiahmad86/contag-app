package com.contag.app.request;

import android.util.Log;

import com.contag.app.model.APIInterface;
import com.contag.app.model.NotificationsResponse;
import com.contag.app.util.PrefUtils;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by tanay on 13/10/15.
 */
public class NotificationsRequest extends RetrofitSpiceRequest<NotificationsResponse.NotificationList, APIInterface> {

    int startIndex, endIndex;
    String type = "get" ;
    long notificationID ;

    public NotificationsRequest(int startIndex, int endIndex) {
        super(NotificationsResponse.NotificationList.class, APIInterface.class);
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public NotificationsRequest(long notificationID, String type){
        super(NotificationsResponse.NotificationList.class, APIInterface.class) ;
        this.type = type ;
        this.notificationID = notificationID ;

    }


    @Override
    public NotificationsResponse.NotificationList loadDataFromNetwork() throws Exception {
        Log.d("notifdelete", "Here loading data from network") ;
        Log.d("notifdelete", "Notification id: " + this.notificationID) ;
        if(this.type.equals("get")){
            return getService().getNotifications(PrefUtils.getAuthToken(), startIndex, endIndex);
        }else {
            Log.d("notifdelete", "Going to delete this bitch") ;
            return getService().hideNotification(PrefUtils.getAuthToken(), notificationID) ;
        }
    }
}
