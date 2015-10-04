package com.contag.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.config.ContagApplication;
import com.contag.app.model.ContagContag;
import com.contag.app.model.ContagContagDao;
import com.contag.app.model.DaoSession;
import com.contag.app.model.Interest;
import com.contag.app.model.InterestDao;
import com.contag.app.model.SocialProfile;
import com.contag.app.model.SocialProfileDao;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Bedprakash on 9/19/2015.
 */
public class UserProfileFragment extends BaseFragment {

    public interface ViewMode {
        int PERSONAL_DETAILS = 0;
        int SOCIAL_DETAILS = 1;
        int PROFRESSIONAL_DETAILS = 2;
        int TABS_COUNT = 3; // its not a view mode
    }

    private long userID;
    private HashMap<Integer, View> hmIndicator;


    public static UserProfileFragment newInstance(long userId) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.Keys.KEY_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            userID = args.getLong(Constants.Keys.KEY_USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        hmIndicator = new HashMap<>();
        hmIndicator.put(0, view.findViewById(R.id.v_indicator_1));
        hmIndicator.put(1, view.findViewById(R.id.v_indicator_2));
        hmIndicator.put(2, view.findViewById(R.id.v_indicator_3));

        hmIndicator.get(0).setBackgroundResource(R.drawable.bg_indicator_white);


        ViewPager pager = (ViewPager) view.findViewById(R.id.root_pager);

        PersonalDetailsTabsAdapter homeTabsAdapter = new PersonalDetailsTabsAdapter(mBaseActivity.getSupportFragmentManager());
        pager.setAdapter(homeTabsAdapter);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return view;
    }

    private class PersonalDetailsTabsAdapter extends FragmentPagerAdapter {

        public PersonalDetailsTabsAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case ViewMode.PERSONAL_DETAILS: {
                    fragment = UserProfileViewFragment.newInstance(Constants.Types.PROFILE_PERSONAL, userID);
                    break;
                }
                case ViewMode.SOCIAL_DETAILS: {
                    fragment = UserProfileViewFragment.newInstance(Constants.Types.PROFILE_SOCIAL, userID);
                    break;
                }
                case ViewMode.PROFRESSIONAL_DETAILS: {
                    fragment = UserProfileViewFragment.newInstance(Constants.Types.PROFILE_PROFESSIONAL, userID);
                    break;
                }
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return ViewMode.TABS_COUNT;
        }
    }

    private void setIndicator(int pos) {
        for(int i = 0; i < UserProfileFragment.ViewMode.TABS_COUNT; i ++) {
            hmIndicator.get(i).setBackgroundResource(R.drawable.bg_indicator_transparent);
        }
        hmIndicator.get(pos).setBackgroundResource(R.drawable.bg_indicator_white);
    }


}
