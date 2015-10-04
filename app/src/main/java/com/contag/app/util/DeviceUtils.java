package com.contag.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.provider.Settings;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by tanay on 5/8/15.
 */
public class DeviceUtils {

    private static String mDeviceId;
    private static int deviceWidth = 0;

    public static String getmDeviceId(Context context) {
        if (mDeviceId == null) {
            mDeviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return mDeviceId;
    }

    public static int getDeviceWidth(Context context) {
        if (deviceWidth == 0) {
            Display mDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).
                    getDefaultDisplay();
            Point point = new Point();
            mDisplay.getSize(point);
            deviceWidth = point.x;
        }
        return deviceWidth;
    }


}
