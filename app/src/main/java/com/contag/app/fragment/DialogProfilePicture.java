package com.contag.app.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.contag.app.R;
import com.contag.app.config.Constants;
import com.squareup.picasso.Picasso;

/**
 * Created by SAMI on 29-12-2015.
 */
public class DialogProfilePicture extends DialogFragment {

    public ImageView ivProfilePicture;
    public static String imageUrl;
    public static DialogProfilePicture newInstance(String url) {

        DialogProfilePicture dp = new DialogProfilePicture();
        Bundle args = new Bundle();
        imageUrl = url;
        return dp;
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);



    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dialog_profile_pic, container, false);
        ivProfilePicture=(ImageView) view.findViewById(R.id.iv_profile_pic);
        Picasso.with(getActivity()).load(imageUrl).placeholder(R.drawable.default_profile_pic_small)
                . fit()
                .centerCrop()
                .into(ivProfilePicture);
        return view;

    }
}
