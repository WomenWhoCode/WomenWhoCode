package com.womenwhocode.womenwhocode.fragments;

import com.womenwhocode.womenwhocode.R;
import com.womenwhocode.womenwhocode.activities.SignUpEmailActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by pnroy on 10/23/15.
 */
public class SignUpFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        Button btnLogin = (Button) view.findViewById(R.id.btnEmailSignUp);
        Bundle bundle = this.getArguments();
        final String userAns = bundle.getString("userAns");

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SignUpEmailActivity.class);
                i.putExtra("userAns", userAns);
                startActivity(i);

            }
        });
        return view;
    }
}