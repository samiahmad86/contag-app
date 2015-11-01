package com.contag.app.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.fragment.CurrentUserProfileFragment;
import com.contag.app.fragment.UserProfileFragment;
import com.contag.app.model.ContagContag;
import com.contag.app.util.ImageUtils;
import com.contag.app.util.PrefUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class UserActivity extends BaseActivity {

    private static final String TAG = UserActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // adding @UserProfileFragment

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        setUpActionBar(R.id.tb_user);

        View toolbar =  findViewById(R.id.tb_user) ;
        toolbar.setBackgroundResource(R.color.bg_tb_home);

        Intent intent = getIntent();
        long userID = intent.getLongExtra(Constants.Keys.KEY_USER_ID, 0);

        new LoadUser().execute(userID);

        if(userID != PrefUtils.getCurrentUserID()) {
            UserProfileFragment userFragment = UserProfileFragment.newInstance(userID);
            transaction.add(R.id.root_user_fragment, userFragment);
            transaction.commit();
        } else {
            CurrentUserProfileFragment cupf = CurrentUserProfileFragment.newInstance();
            transaction.add(R.id.root_user_fragment, cupf, CurrentUserProfileFragment.TAG).commit();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CurrentUserProfileFragment lf = (CurrentUserProfileFragment) getSupportFragmentManager().
                findFragmentByTag(CurrentUserProfileFragment.TAG);
        if(lf != null) {
            log(TAG, "fuck bro");
            lf.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class LoadUser extends AsyncTask<Long, Void, ContagContag> {
        @Override
        protected ContagContag doInBackground(Long... params) {
            long id = params[0];
            return UserActivity.this.getUser(id);
        }

        @Override
        protected void onPostExecute(ContagContag ccUser) {
            Toolbar tbHome = (Toolbar) UserActivity.this.findViewById(R.id.tb_user);
            ((TextView) tbHome.findViewById(R.id.tv_user_name)).setText(ccUser.getName());
            ((TextView) tbHome.findViewById(R.id.tv_user_contag_id)).setText(ccUser.getContag());
            ((TextView) tbHome.findViewById(R.id.tv_user_status)).setText(ccUser.getStatus_update());
            Picasso.with(UserActivity.this).load(ccUser.getAvatarUrl()).placeholder(R.drawable.default_profile_pic_small).
                    into(((ImageView) tbHome.findViewById(R.id.iv_user_photo)));
            Picasso.with(UserActivity.this).load(ccUser.getAvatarUrl()).placeholder(R.drawable.default_profile_pic_small).
                    into(picaasoTarget);


        }
    }

    private Target picaasoTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Bitmap blurredBitmap = ImageUtils.fastblur(bitmap, 3);
            ((ImageView) UserActivity.this.findViewById(R.id.iv_bg_usr_img)).setImageBitmap(blurredBitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

}
