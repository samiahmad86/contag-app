package com.contag.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.fragment.BaseFragment;
import com.contag.app.fragment.LoginFragment;
import com.contag.app.fragment.NewUserDetailsFragment;
import com.contag.app.fragment.NewUserFragment;

/**
 * When a new user logs he fills out his details here.
 */

public class NewUserActivity extends BaseActivity implements BaseFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        if (savedInstanceState != null) {
            return;
        }

        Intent intent = getIntent();
        String previousClassName = intent.getStringExtra(Constants.Keys.KEY_PREVIOUS_ACTIVITY);

        if (previousClassName.equalsIgnoreCase(LoginFragment.TAG) && !isUserLoggedIn()) {
            onFragmentInteraction(Constants.Types.FRAG_CREATE_USER, intent.getExtras());
        } else {
            onFragmentInteraction(Constants.Types.FRAG_USER_DETAILS, intent.getExtras());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_details, menu);
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

    @Override
    public void onFragmentInteraction(int fragmentType, Bundle args) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (fragmentType == Constants.Types.FRAG_CREATE_USER) {
            ft.add(R.id.fl_user, NewUserFragment.newInstance(args.getLong(Constants.Keys.KEY_NUMBER)));
        } else if (fragmentType == Constants.Types.FRAG_USER_DETAILS) {
            ft.replace(R.id.fl_user, NewUserDetailsFragment.newInstance());
        }
        ft.commit();
    }
}
