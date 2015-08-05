package com.contag.app.util;

import android.content.Context;
import android.provider.Settings;

/**
 * Created by tanay on 5/8/15.
 */
public class DeviceUtils {

    private static String mDeviceId;

    public static String getmDeviceId(Context context) {
        if(mDeviceId == null) {
            mDeviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return mDeviceId;
    }
}
