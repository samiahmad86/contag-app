package com.contag.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.fragment.CurrentUserProfileFragment;
import com.contag.app.fragment.UserProfileFragment;
import com.contag.app.util.PrefUtils;

public class UserActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // adding @UserProfileFragment

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        Intent intent = getIntent();
        long userID = intent.getLongExtra(Constants.Keys.KEY_CONTAG_ID, 0);

        if(userID != PrefUtils.getCurrentUserID()) {
            UserProfileFragment userFragment = UserProfileFragment.newInstance(userID);
            transaction.add(R.id.root_user_fragment, userFragment);
            transaction.commit();
        } else {
            CurrentUserProfileFragment cupf = CurrentUserProfileFragment.newInstance();
            transaction.add(R.id.root_user_fragment, cupf).commit();
        }
    }
}
