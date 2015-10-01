package com.contag.app.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.DatePicker;

import com.contag.app.R;
import com.contag.app.config.Constants;

/**
 * Created by tanay on 2/10/15.
 */
public class DateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private int viewPosition;
    private int fragmentType;
    private static final String TAG = DateFragment.class.getName();

    public static DateFragment newInstance(String date, int position, int fragmentType) {
        DateFragment df = new DateFragment();
        Bundle args = new Bundle();
        args.putString(Constants.Keys.KEY_DATE_VALUE, date);
        args.putInt(Constants.Keys.KEY_VIEW_POSITION, position);
        args.putInt(Constants.Keys.KEY_USER_PROFILE_TYPE, fragmentType);
        df.setArguments(args);
        return df;
    }

    public DateFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        viewPosition = args.getInt(Constants.Keys.KEY_VIEW_POSITION);
        fragmentType = args.getInt(Constants.Keys.KEY_USER_PROFILE_TYPE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] dateArr = getArguments().getString(Constants.Keys.KEY_DATE_VALUE).split("-");
        int year = Integer.parseInt(dateArr[0]);
        int month = Integer.parseInt(dateArr[1]);
        int day = Integer.parseInt(dateArr[2]);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog mDatePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        mDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        return mDatePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        Log.d(TAG, date);
        Intent iDate = new Intent(getActivity().getResources().getString(R.string.intent_filter_date_set));
        iDate.putExtra(Constants.Keys.KEY_DATE_VALUE, date);
        iDate.putExtra(Constants.Keys.KEY_VIEW_POSITION, viewPosition);
        iDate.putExtra(Constants.Keys.KEY_USER_PROFILE_TYPE, fragmentType);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(iDate);
    }
}
