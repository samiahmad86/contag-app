package com.contag.app.activity;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.config.Router;
import com.contag.app.fragment.ContactListFragment;
import com.contag.app.fragment.FeedsFragment;
import com.contag.app.fragment.NavDrawerFragment;
import com.contag.app.model.ContagContag;
import com.contag.app.util.PrefUtils;
import com.contag.app.util.ShareUtils;
import com.contag.app.view.SlidingTabLayout;
import com.squareup.picasso.Picasso;


public class HomeActivity extends BaseActivity implements NavDrawerFragment.OnFragmentInteractionListener, View.OnClickListener {

    public static final String TAG = HomeActivity.class.getName();
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        setUpActionBar(R.id.tb_home);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary_color_dark));
        }

        ViewPager vpHome = (ViewPager) findViewById(R.id.vp_home);
        HomePagerAdapter hpa = new HomePagerAdapter(getSupportFragmentManager());
        vpHome.setAdapter(hpa);

        findViewById(R.id.iv_user_photo).setOnClickListener(this);
        findViewById(R.id.tv_user_name).setOnClickListener(this);
        findViewById(R.id.badge_ham).setOnClickListener(this);

        if(getIntent().getExtras().containsKey(Constants.Keys.KEY_LAUNCH_MODE)) {
            int mode = getIntent().getExtras().getInt(Constants.Keys.KEY_LAUNCH_MODE);
            if(mode == Constants.Types.NFC_OPEN_PROFILE && getIntent().getExtras().containsKey(Constants.Keys.KEY_USER_ID))
               Router.startUserActivity(this,HomeActivity.class.getSimpleName(),getIntent().getExtras().getLong(Constants.Keys.KEY_USER_ID));
        }
       /* tv_noti_count=(TextView) findViewById(R.id.badge_ham);
        addBadge(PrefUtils.getNewNotificationCount());*/
/*<<<<<<< HEAD
        boolean result=checkContactPermission();
        if(result==true) {
            if (PrefUtils.isContactBookUpdated()) {
                Router.startContactService(this, true);
            } else {
                if ((System.currentTimeMillis() - PrefUtils.getContactUpdatedTimestamp()) > Constants.Values.ONE_DAY_IN_MILLISECONDS) {
                    Router.startContactService(this, false);
                }
            }
        }
        new LoadUser().execute();

=======
>>>>>>> cf51c8a0bc92a3aead89cc6b043e97a639c72c21*/
        SlidingTabLayout stl = (SlidingTabLayout) findViewById(R.id.stl_home);
        stl.setDistributeEvenly(true);
        stl.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.parseColor("#8787ea");

            }
        });
        stl.setViewPager(vpHome);

        if(checkContactPermission()) {
            if (PrefUtils.isContactBookUpdated()) {
                Router.startContactService(this, true);
            } else {
                if ((System.currentTimeMillis() - PrefUtils.getContactUpdatedTimestamp()) > Constants.Values.ONE_DAY_IN_MILLISECONDS) {
                    Router.startContactService(this, false);
                }
            }
        }
        new LoadUser().execute();


    }

    private void addBadge(int newNotificationCount) {
    /*    tv_noti_count.setVisibility(View.VISIBLE);
        tv_noti_count.setText(newNotificationCount+"");*/


    }


    @Override
    public void onResume() {
        super.onResume();

        clearNotificationBar();
    }

    private void clearNotificationBar(){
        NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();
    }

    @Override
    public void onFragmentInteraction(int value) {

    }

    @Override
    public void onPause() {
        super.onPause();
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawers();
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_user_photo: {
                Router.startUserActivity(this, TAG, PrefUtils.getCurrentUserID());
                break;
            }
            case R.id.tv_user_name: {

                Router.startUserActivity(this, TAG, PrefUtils.getCurrentUserID());
                break;
            }
            case R.id.badge_ham: {

                Router.startNotificationsActivity(this, "navDrawer");
                break;
            }
        }
    }

    public class HomePagerAdapter extends FragmentPagerAdapter {

        public HomePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0: {
                    fragment = ContactListFragment.newInstance();
                    break;
                }
                case 1: {

                    fragment = FeedsFragment.newInstance();
                    break;
                }
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: {
                    return "Connections";
                }
                case 1: {
                    return "Feeds";
                }
            }
            return null;
        }

    }

    private class LoadUser extends AsyncTask<Void, Void, ContagContag> {
        @Override
        protected ContagContag doInBackground(Void... params) {
            log(TAG, "wtf");
            return HomeActivity.this.getCurrentUser();
        }

        @Override
        protected void onPostExecute(ContagContag ccUser) {
            log(TAG, "wtf2");

            Toolbar tbHome = (Toolbar) HomeActivity.this.findViewById(R.id.tb_home);
            ((TextView) tbHome.findViewById(R.id.tv_user_name)).setText(ccUser.getName());
            ((TextView) tbHome.findViewById(R.id.tv_user_contag_id)).setText(ccUser.getContag().toLowerCase());
            Picasso.with(HomeActivity.this).load(ccUser.getAvatarUrl()).placeholder(R.drawable.default_profile_pic_small).
                    fit()
                    .centerCrop().
            into(((ImageView) tbHome.findViewById(R.id.iv_user_photo)));
            setUpDrawer(R.id.drawer_layout, R.id.tb_home);

        }
       /* @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                               int[] grantResults) {
            if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted

                } else {
                    Toast.makeText(HomeActivity.this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
                }
            }
            super.onR
        }*/




    }
    public boolean checkContactPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
            return false;
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted

                if (PrefUtils.isContactBookUpdated()) {
                    Router.startContactService(this, true);
                } else {
                    if ((System.currentTimeMillis() - PrefUtils.getContactUpdatedTimestamp()) > Constants.Values.ONE_DAY_IN_MILLISECONDS) {
                        Router.startContactService(this, false);
                    }
                }
            } else {
                Toast.makeText(HomeActivity.this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
