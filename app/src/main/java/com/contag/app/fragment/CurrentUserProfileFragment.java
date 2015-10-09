package com.contag.app.fragment;

import android.content.Intent;
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

import java.util.HashMap;
import java.util.List;

/**
 * Created by tanay on 28/9/15.
 */
public class CurrentUserProfileFragment extends BaseFragment {

    public static final String TAG = CurrentUserProfileFragment.class.getName();
    private HashMap<Integer, View> hmIndicator;

    public static CurrentUserProfileFragment newInstance() {
        CurrentUserProfileFragment cupf = new CurrentUserProfileFragment();
        Bundle args = new Bundle();
        cupf.setArguments(args);
        return cupf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_user_profile, container, false);
        Bundle args = getArguments();

        hmIndicator = new HashMap<>();
        hmIndicator.put(0, view.findViewById(R.id.v_indicator_1));
        hmIndicator.put(1, view.findViewById(R.id.v_indicator_2));
        hmIndicator.put(2, view.findViewById(R.id.v_indicator_3));

        hmIndicator.get(0).setBackgroundResource(R.drawable.bg_indicator_white);

        ViewPager pager = (ViewPager) view.findViewById(R.id.root_pager);

        PersonalDetailsTabsAdapter homeTabsAdapter = new
                PersonalDetailsTabsAdapter(getChildFragmentManager());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        log(TAG, "fuck bro");

        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if(fragment instanceof CurrentUserSocialProfileEditFragment) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    private class PersonalDetailsTabsAdapter extends FragmentPagerAdapter {

        public PersonalDetailsTabsAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case UserProfileFragment.ViewMode.PERSONAL_DETAILS: {
                    fragment = CurrentUserProfileEditFragment.newInstance(Constants.Types.PROFILE_PERSONAL);
                    break;
                }
                case UserProfileFragment.ViewMode.SOCIAL_DETAILS: {
                    fragment = CurrentUserSocialProfileEditFragment.newInstance();
                    break;
                }
                case UserProfileFragment.ViewMode.PROFRESSIONAL_DETAILS: {
                    fragment = CurrentUserProfileEditFragment.newInstance(Constants.Types.PROFILE_PROFESSIONAL);
                    break;
                }
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return UserProfileFragment.ViewMode.TABS_COUNT;
        }
    }

    private void setIndicator(int pos) {
        for(int i = 0; i < UserProfileFragment.ViewMode.TABS_COUNT; i ++) {
            log(TAG, "" + i + " " + (hmIndicator.get(i) == null));
            hmIndicator.get(i).setBackgroundResource(R.drawable.bg_indicator_transparent);
        }
        hmIndicator.get(pos).setBackgroundResource(R.drawable.bg_indicator_white);
    }

}
