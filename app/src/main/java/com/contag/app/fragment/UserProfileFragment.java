package com.contag.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.model.ContagContag;

/**
 * Created by Bedprakash on 9/19/2015.
 */
public class UserProfileFragment extends BaseFragment {

    public interface ViewMode {
        int PERSONAL_DETAILS = 0;
        int SOCIAL_DETAILS = 1;
        int UNKNOWN = 2;
        int TABS_COUNT = 3; // its not a view mode
    }

    ContagContag contact;


    public static UserProfileFragment newInstance(long userId) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.Keys.KEY_CONTAG_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        Bundle args = getArguments();

        ViewPager pager = (ViewPager) view.findViewById(R.id.root_pager);

        PersonalDetailsTabsAdapter homeTabsAdapter = new PersonalDetailsTabsAdapter(mBaseActivity.getSupportFragmentManager());
        pager.setAdapter(homeTabsAdapter);

        return view;
    }

    private class PersonalDetailsTabsAdapter extends FragmentPagerAdapter {

        public PersonalDetailsTabsAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            Fragment fragment = null;
            switch (position) {
                case ViewMode.PERSONAL_DETAILS: {
                    fragment = PersonalDetailsFragment.getInstance(contact);
                    break;
                }
                case ViewMode.SOCIAL_DETAILS: {
                    fragment = PersonalDetailsFragment.getInstance(contact);
                    break;
                }
                case ViewMode.UNKNOWN: {
                    fragment = PersonalDetailsFragment.getInstance(contact);
                    break;
                }
            }
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return ViewMode.TABS_COUNT;
        }
    }
}
