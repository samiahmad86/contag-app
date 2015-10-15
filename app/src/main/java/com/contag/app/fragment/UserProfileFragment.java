package com.contag.app.fragment;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.contag.app.R;
import com.contag.app.activity.BaseActivity;
import com.contag.app.config.Constants;
import com.contag.app.model.ContagContag;
import com.contag.app.util.DeviceUtils;
import com.contag.app.view.SlidingTabLayout;

import java.util.HashMap;

/**
 * Created by Bedprakash on 9/19/2015.
 */
public class UserProfileFragment extends BaseFragment implements View.OnClickListener{

    public interface ViewMode {
        int PERSONAL_DETAILS = 0;
        int SOCIAL_DETAILS = 1;
        int PROFESSIONAL_DETAILS = 2;
        int TABS_COUNT = 3; // its not a view mode
    }

    private long userID;
    private Button btnCall, btnMessage;

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


        btnCall = (Button) view.findViewById(R.id.btn_call);
        btnCall.setOnClickListener(this);
        btnMessage = (Button) view.findViewById(R.id.btn_msg);
        btnMessage.setOnClickListener(this);

        new LoadNumber().execute();

        ViewPager pager = (ViewPager) view.findViewById(R.id.root_pager);

        PersonalDetailsTabsAdapter homeTabsAdapter = new PersonalDetailsTabsAdapter(mBaseActivity.getSupportFragmentManager());
        pager.setAdapter(homeTabsAdapter);

        SlidingTabLayout stl = (SlidingTabLayout) view.findViewById(R.id.stl_user);
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
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_call: {
                String number = (String) v.getTag();
                if(number != null) {
                    DeviceUtils.dialNumber(getActivity(), number);
                }
                break;
            }
            case R.id.btn_msg: {
                String number = (String) v.getTag();
                if(number != null) {
                    DeviceUtils.sendSms(getActivity(), number, null);
                }
                break;
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
                case ViewMode.PERSONAL_DETAILS: {
                    fragment = UserProfileViewFragment.newInstance(Constants.Types.PROFILE_PERSONAL, userID);
                    break;
                }
                case ViewMode.SOCIAL_DETAILS: {
                    fragment = UserProfileViewFragment.newInstance(Constants.Types.PROFILE_SOCIAL, userID);
                    break;
                }
                case ViewMode.PROFESSIONAL_DETAILS: {
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

    private class LoadNumber extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            ContagContag user = ((BaseActivity) UserProfileFragment.this.getActivity()).getUser(userID);
            return user.getMobileNumber();
        }

        @Override
        protected void onPostExecute(String number) {
            btnCall.setTag(number);
            btnMessage.setTag(number);
        }
    }

}
