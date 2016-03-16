package com.womenwhocode.womenwhocode.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.womenwhocode.womenwhocode.R;
import com.womenwhocode.womenwhocode.utils.KeyBoardSupport;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by pnroy on 10/19/15.
 */
public class Question1Fragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        //return super.onCreateView(inflater, container, savedInstanceState);ssz
        View view = inflater.inflate(R.layout.fragment_question1, container, false);
        try {

            LinearLayout l = (LinearLayout) view.findViewById(R.id.chkLayout);
            Button btnNext = (Button) view.findViewById(R.id.btnNext);
            GifImageView img1 = (GifImageView) view.findViewById(R.id.img_1);
            GifImageView img2 = (GifImageView) view.findViewById(R.id.img_2);
            GifImageView img3 = (GifImageView) view.findViewById(R.id.img_3);

            TextView tvQuestion = (TextView) view.findViewById(R.id.tvQuestion);

            Bundle bundle = this.getArguments();
            int clickcnt = bundle.getInt("clickCnt");
            String Question = bundle.getString("Questions");
            String[] Ans = bundle.getStringArray("Answers");
            switch (clickcnt) {
                case 0:
                    Picasso.with(getContext())
                            .load(R.drawable.personalization_1)
                            .into(img1);
                    Picasso.with(getContext())
                            .load(R.drawable.personalization_2_dark)
                            .into(img2);
                    Picasso.with(getContext())
                            .load(R.drawable.personalization_3_dark)
                            .into(img3);
                    break;
                case 1:
                    img1.setImageResource(R.drawable.personalization);
                    Picasso.with(getContext())
                            .load(R.drawable.personalization_2)
                            .into(img2);
                    Picasso.with(getContext())
                            .load(R.drawable.personalization_3_dark)
                            .into(img3);
                    break;
                case 2:
                    img1.setImageResource(R.drawable.personalization_done);
                    img2.setImageResource(R.drawable.personalization);
                    Picasso.with(getContext())
                            .load(R.drawable.personalization_3)
                            .into(img3);
                    break;

            }

            btnNext.setId(clickcnt + 1);

            tvQuestion.setText(Question);
            tvQuestion.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            for (String An : Ans) {
                CheckBox cb = new CheckBox(getContext());
                //cb.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checkbox,0,0,0);
                //cb.setCompoundDrawablePadding(-50);
                KeyBoardSupport.helperHideKeyboard(cb, getActivity());

                cb.setButtonDrawable(ContextCompat.getDrawable(getContext(), R.drawable.check_box_selector));

                cb.setText(An);

                cb.setPadding(32, 16, 4, 16);

                cb.setTextColor(ContextCompat.getColor(getContext(), R.color.tabOpacity));
                l.addView(cb);
            }


        } catch (Exception e) {

        }


        return view;

    }


}
