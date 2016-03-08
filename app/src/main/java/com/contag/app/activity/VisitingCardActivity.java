package com.contag.app.activity;

/**
 * Created by SAMI on 02-03-2016.
 */

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.adapter.NotificationsAdapter;
import com.contag.app.config.Constants;
import com.contag.app.config.Router;
import com.contag.app.fragment.NavDrawerFragment;
import com.contag.app.model.ContactResponse;
import com.contag.app.model.ContagContag;
import com.contag.app.model.NotificationsResponse;
import com.contag.app.request.ContactRequest;
import com.contag.app.request.NotificationsRequest;
import com.contag.app.util.PrefUtils;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class VisitingCardActivity extends BaseActivity implements NavDrawerFragment.OnFragmentInteractionListener, View.OnClickListener {

    public static final String TAG = NotificationsActivity.class.getName();
    private boolean isLoading = false;
    private ArrayList<NotificationsResponse> notifications;
    private NotificationsAdapter notificationsAdapter;
    private View progressbar;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visiting_card);
        setUpActionBar(R.id.tb_home);
        findViewById(R.id.iv_user_photo).setOnClickListener(this);

        Button btnBack = (Button) findViewById(R.id.btn_back);

        Button btnSave = (Button) findViewById(R.id.btn_save);
        Button btnShare = (Button) findViewById(R.id.btn_share);
        btnShare.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        progressbar = findViewById(R.id.pb_edit_profile_1);
        btnBack.setOnClickListener(this);



    }


    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }

    @Override
    public void onStop() {
        super.onStop();


    }


    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onFragmentInteraction(int value) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_user_photo: {
                Router.startUserActivity(this, TAG, PrefUtils.getCurrentUserID());
                break;
            }
            case R.id.btn_back: {
                this.finish();
                Router.startHomeActivity(this, TAG);
                break;
            }
            case R.id.btn_save: {
                Bitmap bitmap = getBitmapFromView(findViewById(R.id.rl_card));
                saveImage(bitmap);
                break;
            }
        }
    }

    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    private void saveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Contag");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        sendBroadcast(new Intent(
                Intent.ACTION_MEDIA_MOUNTED,
                Uri.parse("file://" + Environment.getExternalStorageDirectory())));
    }

    @Override
    public void onBackPressed() {
        finish();
        // Router.startHomeActivity(this, TAG);
    }
}
