package com.contag.app.activity;

/**
 * Created by SAMI on 02-03-2016.
 */

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.config.Router;
import com.contag.app.fragment.NavDrawerFragment;
import com.contag.app.model.ContagContag;
import com.contag.app.util.ImageUtils;
import com.contag.app.util.PrefUtils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

public class VisitingCardActivity extends BaseActivity implements NavDrawerFragment.OnFragmentInteractionListener, View.OnClickListener {

    public static final String TAG = VisitingCardActivity.class.getName();

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private boolean editMode=false;
    RelativeLayout rlCardView,rlCardEdit,rlButtons;
    ImageButton btnEdit,btnDownload,btnShare,btnSave;
    public ContagContag myUser;
    public TextView userName,userEmail,userPhone,userContagId,userDesignation;
    public EditText etUserName,etUserEmail,etUserPhone,etUserContagId,etUserDesignation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visiting_card);
        setUpActionBar(R.id.tb_home);
        findViewById(R.id.iv_user_photo).setOnClickListener(this);

        Button btnBack = (Button) findViewById(R.id.btn_back);
        btnEdit = (ImageButton) findViewById(R.id.btn_edit);
        btnDownload = (ImageButton) findViewById(R.id.btn_download);
        btnShare = (ImageButton) findViewById(R.id.btn_share);
        rlCardView=(RelativeLayout) findViewById(R.id.rl_card_view);
        rlCardEdit=(RelativeLayout) findViewById(R.id.rl_card_edit);
        rlButtons=(RelativeLayout) findViewById(R.id.rl_buttons);
        btnSave=(ImageButton) findViewById(R.id.btn_save);

        userName=(TextView) findViewById(R.id.tv_user_name);
        userEmail=(TextView) findViewById(R.id.tv_user_email);
        userPhone=(TextView) findViewById(R.id.tv_user_phone);
        userContagId=(TextView) findViewById(R.id.tv_user_contag_id);
        userDesignation=(TextView) findViewById(R.id.tv_designation);

        etUserName=(EditText) findViewById(R.id.et_user_name);
        etUserPhone=(EditText) findViewById(R.id.et_user_phone);
        etUserEmail=(EditText) findViewById(R.id.et_user_email);
        etUserDesignation=(EditText) findViewById(R.id.et_user_designation);

        btnSave.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnDownload.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        new LoadUser().execute();
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
                //Router.startHomeActivity(this, TAG);
                break;
            }
            case R.id.btn_edit: {
                toggleEditMode();
                break;
            }
            case R.id.btn_download: {
                Bitmap bitmap = ImageUtils.getBitmapFromView(findViewById(R.id.rl_card_view));
                ImageUtils.saveImage(bitmap,this);
                break;
            }
            case R.id.btn_save: {
                syncProfile();
                setUpVisitingCard(myUser+);
                toggleEditMode();
                 break;
            }
        }
    }


    private void toggleEditMode()
    {
        if(editMode) {
            setUpVisitingCard(myUser);
            rlCardView.setVisibility(View.VISIBLE);
            rlCardEdit.setVisibility(View.GONE);
            rlButtons.setVisibility(View.VISIBLE);

        }
        else
        {
            setUpVisitingCardEdit(myUser);
            rlCardView.setVisibility(View.GONE);
            rlCardEdit.setVisibility(View.VISIBLE);
            rlButtons.setVisibility(View.GONE);

        }
        editMode=!editMode;

    }
    @Override
    public void onBackPressed() {
        finish();
        // Router.startHomeActivity(this, TAG);
    }
    private class LoadUser extends AsyncTask<Void, Void, ContagContag> {
        @Override
        protected ContagContag doInBackground(Void... params) {
            log(TAG, "wtf");
            return VisitingCardActivity.this.getCurrentUser();
        }

        @Override
        protected void onPostExecute(ContagContag ccUser) {

            myUser=ccUser;
            setUpVisitingCard(myUser);
            Toolbar tbHome = (Toolbar) VisitingCardActivity.this.findViewById(R.id.tb_home);
            TextView name,id;
            name= (TextView) tbHome.findViewById(R.id.tv_user_name);
            id= (TextView) tbHome.findViewById(R.id.tv_user_contag_id);
            name.setText(ccUser.getName());
            id.setText(ccUser.getContag().toLowerCase());
            name.setTextColor(Color.BLACK);
            id.setTextColor(Color.BLACK);
            Picasso.with(VisitingCardActivity.this).load(ccUser.getAvatarUrl()).placeholder(R.drawable.default_profile_pic_small).
                    fit()
                    .centerCrop().
                    into(((ImageView) tbHome.findViewById(R.id.iv_user_photo)));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            tbHome.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //What to do on back clicked
                    finish();
                    Router.startHomeActivity(VisitingCardActivity.this, TAG);
                }
            });
        }
  }
    public void setUpVisitingCard(ContagContag myUser)
    {

        Log.d("Name",myUser.getName());
        if(myUser.getName()!=null)
            userName.setText(myUser.getName());
        userContagId.setText(myUser.getContag().toLowerCase());
        if(myUser.getWorkEmail()!=null)
            userEmail.setText(myUser.getWorkEmail());
        if(myUser.getMobileNumber()!=null)
            userPhone.setText(myUser.getMobileNumber());
        if(myUser.getDesignation()!=null)
            userDesignation.setText(myUser.getDesignation());
    }
    public void setUpVisitingCardEdit(ContagContag myUser)
    {
        etUserName.setText(myUser.getName());
        if(myUser.getWorkEmail()!=null)
         etUserEmail.setText(myUser.getWorkEmail());
        if(myUser.getDesignation()!=null)
         etUserDesignation.setText(myUser.getDesignation());
        if(myUser.getMobileNumber()!=null)
         etUserPhone.setText(myUser.getMobileNumber());
    }
    public void syncProfile()
    {
        String name=etUserName.getText().toString();
        String email=etUserEmail.getText().toString();
        String phone=etUserPhone.getText().toString();
        String designation=etUserDesignation.getText().toString();
        if(name!=null)
            myUser.setName(name);
        if(email!=null)
            myUser.setWorkEmail(email);
        if(phone!=null)
            myUser.setMobileNumber(phone);
        if(designation!=null)
            myUser.setDesignation(designation);

    }
}
