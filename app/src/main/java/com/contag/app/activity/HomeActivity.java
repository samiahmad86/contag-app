package com.contag.app.activity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.config.Router;
import com.contag.app.fragment.ContactListFragment;
import com.contag.app.fragment.FeedsFragment;
import com.contag.app.fragment.NavDrawerFragment;
import com.contag.app.model.ContagContag;
import com.contag.app.util.PrefUtils;
import com.contag.app.view.SlidingTabLayout;
import com.squareup.picasso.Picasso;


public class HomeActivity extends BaseActivity implements NavDrawerFragment.OnFragmentInteractionListener, View.OnClickListener {

    public static final String TAG = HomeActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setUpActionBar(R.id.tb_home);

        ViewPager vpHome = (ViewPager) findViewById(R.id.vp_home);
        HomePagerAdapter hpa = new HomePagerAdapter(getSupportFragmentManager());
        vpHome.setAdapter(hpa);

        findViewById(R.id.iv_user_photo).setOnClickListener(this);

        SlidingTabLayout stl = (SlidingTabLayout) findViewById(R.id.stl_home);
        stl.setDistributeEvenly(true);
        stl.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.parseColor("#8787ea");

            }
        });
        stl.setViewPager(vpHome);


        if (PrefUtils.isContactBookUpdated()) {
            Router.startContactService(this, true);
        } else {
            if ((System.currentTimeMillis() - PrefUtils.getContactUpdatedTimestamp()) > Constants.Values.ONE_DAY_IN_MILLISECONDS) {
                Router.startContactService(this, false);
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        new LoadUser().execute();
        setUpDrawer(R.id.drawer_layout, R.id.tb_home);
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
                    fragment = FeedsFragment.newInstance();
                    break;
                }
                case 1: {
                    fragment = ContactListFragment.newInstance();
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
                    return "Feeds";
                }
                case 1: {
                    return "Contacts";
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
            ((TextView) tbHome.findViewById(R.id.tv_user_contag_id)).setText(ccUser.getContag());
            Picasso.with(HomeActivity.this).load(ccUser.getAvatarUrl()).placeholder(R.drawable.default_profile_pic_small).
                    into(((ImageView) tbHome.findViewById(R.id.iv_user_photo)));

        }
    }

}
