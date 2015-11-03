package com.contag.app.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.contag.app.R;

/**
 * Created by varunj on 04/11/15.
 */
public class ShareDialog extends DialogFragment  implements View.OnClickListener{


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_share_dialog, container, false);

        Button sharePublic = (Button) view.findViewById(R.id.btn_share_public) ;
        Button shareCustom = (Button) view.findViewById(R.id.btn_share_custom) ;
        Button shareDone = (Button) view.findViewById(R.id.btn_share_done) ;

        sharePublic.setOnClickListener(this);
        shareCustom.setOnClickListener(this);
        shareDone.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        switch (id) {
            case R.id.btn_share_public:{
                Toast.makeText(getActivity(), "Sharing public", Toast.LENGTH_SHORT).show() ;
                break ;
            }
            case R.id.btn_share_custom: {
                Toast.makeText(getActivity(), "Sharing custom", Toast.LENGTH_SHORT).show() ;
                break ;
            }
            case R.id.btn_share_done:{
                Toast.makeText(getActivity(), "Sharing saved!", Toast.LENGTH_SHORT).show() ;
                break ;

            }
        }
    }


}
