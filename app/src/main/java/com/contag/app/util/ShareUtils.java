package com.contag.app.util;

import android.content.Context;
import android.content.Intent;

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
}
