package com.contag.app.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.contag.app.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NavDrawerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NavDrawerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NavDrawerFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NavDrawerFragment.
     */
    public static NavDrawerFragment newInstance(ArrayList<NavDrawerItem> navDrawerItems) {
        NavDrawerFragment fragment = new NavDrawerFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("any-key", navDrawerItems);
        fragment.setArguments(args);
        return fragment;
    }

    public NavDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nav_drawer, container, false);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(int value);
    }


    public class NavDrawerAdapter extends RecyclerView.Adapter<NavDrawerViewHolder>{


        @Override
        public NavDrawerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return null;
        }

        @Override
        public void onBindViewHolder(NavDrawerViewHolder navDrawerViewHolder, int i) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    public  class NavDrawerViewHolder extends RecyclerView.ViewHolder{

        public NavDrawerViewHolder(View itemView) {
            super(itemView);
        }
    }


    public static class NavDrawerItem implements Parcelable {

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {

        }

        public static final Parcelable.Creator<NavDrawerItem> CREATOR = new Parcelable.Creator<NavDrawerItem>() {

            @Override
            public NavDrawerItem createFromParcel(Parcel parcel) {
                return null;
            }

            @Override
            public NavDrawerItem[] newArray(int i) {
                return new NavDrawerItem[0];
            }
        };
    }

}
