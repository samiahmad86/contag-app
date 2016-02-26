package com.contag.app.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.contag.app.activity.BaseActivity;

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

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        for(NetworkInfo info : mConnectivityManager.getAllNetworkInfo()) {
            if(info.isConnected()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mNetworkInfo.isConnected();
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

    public static void copyToClipboard(Context context, String text) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("copied to clipboard", text);
        cm.setPrimaryClip(data);

    }
    public static void openConversationWithWhatsapp(Context context, String phoneNumber) {
        boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp",context);
        if (isWhatsappInstalled) {
             Uri uri = Uri.parse("smsto:" + phoneNumber);
            Intent i = new Intent(Intent.ACTION_SENDTO, uri);
            i.setPackage("com.whatsapp");
            context.startActivity(i);
        } else {
            Toast.makeText(context, "WhatsApp not Installed",
                    Toast.LENGTH_SHORT).show();


        }
    }
        public static boolean whatsappInstalledOrNot(String uri,Context context) {
            PackageManager pm = context.getPackageManager();
            boolean app_installed = false;
            try {
                pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
                app_installed = true;
            } catch (PackageManager.NameNotFoundException e) {
                app_installed = false;
            }
            return app_installed;
        }


}
