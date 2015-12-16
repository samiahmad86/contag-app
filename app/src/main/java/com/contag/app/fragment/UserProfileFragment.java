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
import android.widget.ImageView;
import android.widget.TextView;

import com.contag.app.R;
import com.contag.app.activity.BaseActivity;
import com.contag.app.config.Constants;
import com.contag.app.model.ContagContag;
import com.contag.app.util.DeviceUtils;
import com.contag.app.view.SlidingTabLayout;

/**
 * Created by Bedprakash on 9/19/2015.
 */
public class UserProfileFragment extends BaseFragment implements View.OnClickListener {

    public interface ViewMode {
        int PERSONAL_DETAILS = 0;
        int SOCIAL_DETAILS = 1;
        int PROFESSIONAL_DETAILS = 2;
        int TABS_COUNT = 3; // its not a view mode
    }

    private long userID;
    private Button btnCall, btnMessage;
    private static final int[] INTEREST_TV_IDS = {R.id.tv_user_interest_1, R.id.tv_user_interest_2,
            R.id.tv_user_interest_3, R.id.tv_user_interest_4};
    private TextView[] tvInterests = new TextView[4];
    private ImageView tvDots;
    private Button page1,page2,page3;
    ViewPager pager;
    private int profileCategory;

    public static UserProfileFragment newInstance(long userId, int profileCategory) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.Keys.KEY_USER_ID, userId);
        args.putInt(Constants.Keys.KEY_PROFILE_CATEGORY, profileCategory);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            userID = args.getLong(Constants.Keys.KEY_USER_ID);
            profileCategory = args.getInt(Constants.Keys.KEY_PROFILE_CATEGORY);
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
        tvDots=(ImageView)view.findViewById(R.id.tv_dots);
        page1=(Button)view.findViewById(R.id.btn_page1);
        page2=(Button)view.findViewById(R.id.btn_page2);
        page3=(Button)view.findViewById(R.id.btn_page3);
        page1.setOnClickListener(this);
        page2.setOnClickListener(this);
        page3.setOnClickListener(this);

        for (int i = 0; i < 4; i ++) {
            tvInterests[i] = (TextView) view.findViewById(INTEREST_TV_IDS[i]);
        }

        new LoadNumber().execute();
        //new LoadInterests().execute();

        pager = (ViewPager) view.findViewById(R.id.root_pager);

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
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // the position parameter tells us at what page we moved to
                // you could use the other two methods to update the views as
                // soon as the user starts the swipe
                // get a reference to the Buttons and change their backgrounds
               if(position==0)
                tvDots.setImageDrawable(getResources().getDrawable(R.drawable.dots1));
                if(position==1)
                    tvDots.setImageDrawable(getResources().getDrawable(R.drawable.dots2));
                if(position==2)
                    tvDots.setImageDrawable(getResources().getDrawable(R.drawable.dots3));
                //showToast(position+"");
            }

            @Override
            public void onPageScrolled(int position, float offset, int offsetPixel) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if(profileCategory != -1) {
            pager.setCurrentItem(profileCategory);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_call: {
                String number = (String) v.getTag();
                if (number != null) {
                    DeviceUtils.dialNumber(getActivity(), number);
                }
                break;
            }
            case R.id.btn_msg: {
                String number = (String) v.getTag();
                if (number != null) {
                    DeviceUtils.sendSms(getActivity(), number, null);
                }
                break;
            }
            case R.id.btn_page1: {
               pager.setCurrentItem(0);
               // showToast("page1");
                break;
            }
            case R.id.btn_page2: {
                pager.setCurrentItem(1);
               // showToast("page2");
                break;
            }
            case R.id.btn_page3: {
                pager.setCurrentItem(2);
               // showToast("page3");
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
            try {
                return user.getMobileNumber();
            } catch (NullPointerException ex) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String number) {
            if(number != null) {
                btnCall.setTag(number);
                btnMessage.setTag(number);
            }
        }
    }

//    private class LoadInterests extends AsyncTask<Void, Void, ArrayList<Interest>> {
//        @Override
//        protected ArrayList<Interest> doInBackground(Void... params) {
//            Log.d("Inter", "In load interests: " + userID) ;
//            return ((BaseActivity) UserProfileFragment.this.getActivity()).getUserInterests(userID);
//        }
//        @Override
//        protected void onPostExecute(ArrayList<Interest> userInterests) {
//            int size = userInterests.size();
//            for(int i = 0; i < size; i ++) {
//
//                tvInterests[i].setVisibility(View.VISIBLE);
//                if(i % 2 == 0) {
//                    tvInterests[i+1].setVisibility(View.INVISIBLE);
//                }
//                tvInterests[i].setBackgroundResource(R.drawable.bg_white_border_transparent_rect);
//                tvInterests[i].setTextColor(getResources().getColor(R.color.white));
//                tvInterests[i].setText(userInterests.get(i).getName());
//                Log.d("Inter", "onPostExecute: " +userInterests.get(i).getContagUserId()) ;
//            }
//        }
//    }

}
