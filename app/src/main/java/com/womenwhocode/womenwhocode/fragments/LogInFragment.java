package com.womenwhocode.womenwhocode.fragments;

import com.womenwhocode.womenwhocode.R;
import com.womenwhocode.womenwhocode.activities.LoginEmailActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by pnroy on 10/19/15.
 */
public class LogInFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        Button btnLogin = (Button) view.findViewById(R.id.btnEmailLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LoginEmailActivity.class);
                startActivity(i);
            }
        });
        return view;
    }


}
