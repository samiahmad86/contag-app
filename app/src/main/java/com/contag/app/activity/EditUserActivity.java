package com.contag.app.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.fragment.BaseFragment;
import com.contag.app.fragment.EditUserFragment;
import com.contag.app.fragment.SocialMediaFragment;

/**
 * When a new user logs he fills out his details here.
 * The activity shows two fragments one after the other
 *      1) {@link com.contag.app.fragment.SocialMediaFragment}
 *      2) {@link com.contag.app.fragment.EditUserFragment}
 */

public class EditUserActivity extends BaseActivity implements BaseFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        setUpActionBar(R.id.tb_user_details);

        if(savedInstanceState != null) {
            return;
        }

        onFragmentInteraction(Constants.Types.FRAG_SOCIAL, null);
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
        if(fragmentType == Constants.Types.FRAG_SOCIAL) {
            ft.add(R.id.fl_user, SocialMediaFragment.newInstance());
        } else if(fragmentType == Constants.Types.FRAG_EDIT_USER) {
            ft.replace(R.id.fl_user, EditUserFragment.newInstance(fragmentType));
        }
        ft.commit();
    }
}
