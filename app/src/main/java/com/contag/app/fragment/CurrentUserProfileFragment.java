package com.contag.app.fragment;

import android.content.Intent;
import android.graphics.Color;
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
import com.contag.app.view.SlidingTabLayout;

import java.util.HashMap;
import java.util.List;

/**
 * Created by tanay on 28/9/15.
 */
public class CurrentUserProfileFragment extends BaseFragment {

    public static final String TAG = CurrentUserProfileFragment.class.getName();

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

        ViewPager pager = (ViewPager) view.findViewById(R.id.root_pager);

        PersonalDetailsTabsAdapter homeTabsAdapter = new
                PersonalDetailsTabsAdapter(getChildFragmentManager());
        pager.setAdapter(homeTabsAdapter);

        SlidingTabLayout stl = (SlidingTabLayout) view.findViewById(R.id.stl_current_user);
        stl.setDistributeEvenly(true);
        stl.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.parseColor("#ffffff");
            }
        });
        stl.setViewPager(pager);


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
                case UserProfileFragment.ViewMode.PROFESSIONAL_DETAILS: {
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

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: {
                    return "Personal";
                }
                case 1: {
                    return "Social";
                }
                case 2: {
                    return "Professional";
                }
            }
            return null;
        }

    }


}
