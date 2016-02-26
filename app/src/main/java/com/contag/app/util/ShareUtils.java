package com.contag.app.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;

import com.contag.app.activity.BaseActivity;

/**
 * Created by tanay on 22/9/15.
 */
public class ShareUtils {

    public static void shareText(Context context, String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    public static void sendViaNFC(Context pCtx,final String message,NfcAdapter.OnNdefPushCompleteCallback callback) {
        if(!isNFCAvailable(pCtx)) {
            return;
        } else {
            NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(pCtx);
            nfcAdapter.setNdefPushMessageCallback(new NfcAdapter.CreateNdefMessageCallback() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
                    byte[] bytesOut = message.getBytes();
                    NdefRecord ndefRecordOut = new NdefRecord(
                            NdefRecord.TNF_MIME_MEDIA,
                            "text/plain".getBytes(),
                            new byte[] {},
                            bytesOut);
                    NdefMessage ndefMessageout = new NdefMessage(ndefRecordOut);
                    return ndefMessageout;
                }
            },(BaseActivity)pCtx);
            nfcAdapter.setOnNdefPushCompleteCallback(callback,(BaseActivity)pCtx);
        }

    }

    public static boolean isNFCAvailable(Context pCtx) {
        PackageManager packageManager = pCtx.getPackageManager();
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            return false;
        } else if (Build.VERSION.SDK_INT <
                Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return false;
        } else {
            return true;
        }
    }
}
