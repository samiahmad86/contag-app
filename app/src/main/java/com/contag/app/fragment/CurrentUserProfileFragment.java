package com.contag.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.contag.app.view.EditViewPager;
import com.contag.app.view.SlidingTabLayout;

import java.util.List;

/**
 * Created by tanay on 28/9/15.
 */
public class CurrentUserProfileFragment extends BaseFragment implements View.OnTouchListener, View.OnClickListener {

    public static final String TAG = CurrentUserProfileFragment.class.getName();

    private EditViewPager mEditViewPager;
    private SlidingTabLayout mSlidingTabLayout;
    private boolean isComingFromNotification;
    private Bundle requestBundle;
    private int fragmentToBeOpened;
    private String fieldName;

    public static CurrentUserProfileFragment newInstance() {
        CurrentUserProfileFragment currentUserProfileFragment = new CurrentUserProfileFragment();
        Bundle args = new Bundle();
        currentUserProfileFragment.setArguments(args);
        return currentUserProfileFragment;
    }

    public static CurrentUserProfileFragment newInstance(boolean isComingFromNotification, int fragmentToBeOpened,
                                                         Bundle requestBundle, String fieldName) {
        CurrentUserProfileFragment currentUserProfileFragment = new CurrentUserProfileFragment();
        Bundle args = new Bundle();
        args.putBundle(Constants.Keys.KEY_DATA, requestBundle);
        args.putBoolean(Constants.Keys.KEY_COMING_FROM_NOTIFICATION, isComingFromNotification);
        args.putInt(Constants.Keys.KEY_FRAGMENT_TYPE, fragmentToBeOpened);
        args.putString(Constants.Keys.KEY_FIELD_NAME, fieldName);
        currentUserProfileFragment.setArguments(args);
        return currentUserProfileFragment;
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

        mEditViewPager = (EditViewPager) view.findViewById(R.id.root_pager);

        PersonalDetailsTabsAdapter homeTabsAdapter = new
                PersonalDetailsTabsAdapter(getChildFragmentManager());
        mEditViewPager.setAdapter(homeTabsAdapter);

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.stl_current_user);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.parseColor("#ffffff");
            }
        });
        mSlidingTabLayout.setViewPager(mEditViewPager);

        Bundle args = getArguments();

        isComingFromNotification = args.getBoolean(Constants.Keys.KEY_COMING_FROM_NOTIFICATION);

        if(isComingFromNotification) {
            fragmentToBeOpened = args.getInt(Constants.Keys.KEY_FRAGMENT_TYPE);
            fieldName = args.getString(Constants.Keys.KEY_FIELD_NAME);
            requestBundle = args.getBundle(Constants.Keys.KEY_DATA);
            switch (fragmentToBeOpened) {
                case Constants.Types.PROFILE_PERSONAL: {
                    mEditViewPager.setCurrentItem(UserProfileFragment.ViewMode.PERSONAL_DETAILS);
                    break;
                }
                case Constants.Types.PROFILE_PROFESSIONAL: {
                    mEditViewPager.setCurrentItem(UserProfileFragment.ViewMode.PROFESSIONAL_DETAILS);
                    break;
                }
                case Constants.Types.PROFILE_SOCIAL: {
                    mEditViewPager.setCurrentItem(UserProfileFragment.ViewMode.SOCIAL_DETAILS);
                    break;
                }
            }
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(brToggleSwipe,
                new IntentFilter(getActivity().getString(R.string.intent_filter_edit_mode_enabled)));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(brToggleSwipe);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof CurrentUserSocialProfileEditFragment) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int DRAWABLE_LEFT = 0;
        final int DRAWABLE_TOP = 1;
        final int DRAWABLE_RIGHT = 2;
        final int DRAWABLE_BOTTOM = 3;

        EditText edit = (EditText) v;

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getRawX() >= (edit.getRight() - edit.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                showToast("yes");
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {

    }

    private BroadcastReceiver brToggleSwipe = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean mode = intent.getBooleanExtra(Constants.Keys.KEY_EDIT_MODE_TOGGLE, true);
            log(TAG, "broadcast received " + mode);
            mEditViewPager.setSwipeEnabled(mode);
            mSlidingTabLayout.setEnabled(mode);
        }
    };

    private class PersonalDetailsTabsAdapter extends FragmentPagerAdapter {

        public PersonalDetailsTabsAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case UserProfileFragment.ViewMode.PERSONAL_DETAILS: {
                    if(fragmentToBeOpened == Constants.Types.PROFILE_PERSONAL && isComingFromNotification) {
                        fragment = CurrentUserProfileEditFragment.newInstance(Constants.Types.PROFILE_PERSONAL, isComingFromNotification,
                                requestBundle, fieldName);
                        isComingFromNotification = false;
                    } else {
                        fragment = CurrentUserProfileEditFragment.newInstance(Constants.Types.PROFILE_PERSONAL);
                    }
                    break;
                }
                case UserProfileFragment.ViewMode.SOCIAL_DETAILS: {
                    if(fragmentToBeOpened == Constants.Types.PROFILE_SOCIAL && isComingFromNotification) {
                        fragment = CurrentUserSocialProfileEditFragment.newInstance(isComingFromNotification,
                                requestBundle, fieldName);
                        isComingFromNotification = false;
                    } else {
                        fragment = CurrentUserSocialProfileEditFragment.newInstance();
                    }
                    break;
                }
                case UserProfileFragment.ViewMode.PROFESSIONAL_DETAILS: {
                    if(fragmentToBeOpened == Constants.Types.PROFILE_PROFESSIONAL && isComingFromNotification) {
                        fragment = CurrentUserProfileEditFragment.newInstance(Constants.Types.PROFILE_PROFESSIONAL,
                                isComingFromNotification, requestBundle, fieldName);
                        isComingFromNotification = false;
                    } else {
                        fragment = CurrentUserProfileEditFragment.newInstance(Constants.Types.PROFILE_PROFESSIONAL);
                    }
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
