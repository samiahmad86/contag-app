package com.contag.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.contag.app.R;
import com.contag.app.config.Constants;

public class SmsReceiver extends BroadcastReceiver {

    private final static String TAG = SmsReceiver.class.getName();

    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final SmsManager manager = SmsManager.getDefault();
        Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] oPdus = (Object[]) bundle.get("pdus");
                for (Object pdus : oPdus) {
                    String message = SmsMessage.createFromPdu((byte[]) pdus).getDisplayMessageBody();
                    Log.d(TAG, message);
                    String blahblah = "Dear user you One Time Password(OTP) for login to Contag is ";
                    int startIndex = blahblah.length();
                    String otp = message.substring(startIndex, startIndex + 6);
                    Intent iSms = new Intent(context.getResources().getString(R.string.intent_filter_sms));
                    iSms.putExtra(Constants.Keys.KEY_OTP, otp);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(iSms);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
