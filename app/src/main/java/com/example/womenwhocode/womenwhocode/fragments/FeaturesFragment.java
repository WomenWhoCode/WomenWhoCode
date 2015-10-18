package com.example.womenwhocode.womenwhocode.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.womenwhocode.womenwhocode.R;
import com.example.womenwhocode.womenwhocode.adapters.FeaturesArrayAdapter;
import com.example.womenwhocode.womenwhocode.models.Feature;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shehba.shahab on 10/16/15.
 */
public class FeaturesFragment extends Fragment {
    FeaturesArrayAdapter aFeatures;
    ArrayList<Feature> features;
    ListView lvFeatures;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        features = new ArrayList<>();
        aFeatures = new FeaturesArrayAdapter(getActivity(), features);
        populateFeaturesList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_features, container, false);
        lvFeatures = (ListView) view.findViewById(R.id.lvFeatures);
        lvFeatures.setAdapter(aFeatures);
        return view;
    }

    void addAll(List<Feature> features) {
        aFeatures.addAll(features);
    }

    void populateFeaturesList() {
        ParseQuery<Feature> query = ParseQuery.getQuery(Feature.class);
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<Feature>() {
            public void done(List<Feature> lFeatures, ParseException e) {
                if (e == null) {
                    aFeatures.clear();
                    addAll(lFeatures);
                    aFeatures.notifyDataSetChanged();
                } else {
                    Log.d("Message", "Error: " + e.getMessage());
                }
            }
        });
    }
}