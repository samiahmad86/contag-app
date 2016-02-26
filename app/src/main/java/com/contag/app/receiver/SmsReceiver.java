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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                    String otp = extractDigits(message);
                    Intent iSms = new Intent(context.getResources().getString(R.string.intent_filter_sms));
                    iSms.putExtra(Constants.Keys.KEY_OTP, otp);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(iSms);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String extractDigits(final String in) {
        final Pattern p = Pattern.compile("(\\d{6})");
        final Matcher m = p.matcher(in);
        if ( m.find() ) {
            return m.group( 0 );
        }
        return "";
    }
}
