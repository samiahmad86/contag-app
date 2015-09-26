package com.contag.app.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.config.Router;
import com.contag.app.fragment.ContactListFragment;
import com.contag.app.fragment.FeedsFragment;
import com.contag.app.fragment.NewUserDetailsFragment;
import com.contag.app.model.User;
import com.contag.app.util.PrefUtils;
import com.contag.app.view.SlidingTabLayout;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;


public class HomeActivity extends BaseActivity {

    public static final String TAG = HomeActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        String className = intent.getStringExtra(Constants.Keys.KEY_PREVIOUS_ACTIVITY);

        setUpActionBar(R.id.tb_home);

        // we do not want to redraw the activity once the orientation changes
        if (savedInstanceState != null) {
            return;
        }

        Gson gson = new Gson();
        User user = gson.fromJson(PrefUtils.getCurrentUser(), User.class);

        Toolbar tbHome = (Toolbar) findViewById(R.id.tb_home);
        ((TextView) tbHome.findViewById(R.id.tv_user_name)).setText(user.name);
        ((TextView) tbHome.findViewById(R.id.tv_user_id)).setText(user.contag);
        Picasso.with(this).load(user.avatarUrl).placeholder(R.drawable.camera_icon).into(((ImageView) tbHome.findViewById(R.id.iv_user_photo)));

        ViewPager vpHome = (ViewPager) findViewById(R.id.vp_home);
        HomePagerAdapter hpa = new HomePagerAdapter(getSupportFragmentManager());
        vpHome.setAdapter(hpa);

        SlidingTabLayout stl = (SlidingTabLayout) findViewById(R.id.stl_home);
        stl.setDistributeEvenly(true);
        stl.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.parseColor("#ffffff");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

}
