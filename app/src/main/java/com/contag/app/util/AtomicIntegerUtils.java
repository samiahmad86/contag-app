package com.contag.app.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tanaytandon on 20/11/15.
 */
public class AtomicIntegerUtils {

    private static AtomicInteger mNotificationID;

    public static int getmNotificationID() {
        if(mNotificationID == null) {
            mNotificationID = new AtomicInteger(0);
        }
        return mNotificationID.incrementAndGet();
    }
}
