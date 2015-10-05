package com.contag.app.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
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

    public static void sendSms(Context context, String number, String body) {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", number);
        if (body != null) {
            smsIntent.putExtra("sms_body", body);
        }
        context.startActivity(smsIntent);
    }

    public static void dialNumber(Context context, String number) {
        Uri num = Uri.parse("tel:" + number);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, num);
        context.startActivity(callIntent);
    }
}
