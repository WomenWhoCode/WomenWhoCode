package com.womenwhocode.womenwhocode.fragments;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.womenwhocode.womenwhocode.R;
import com.womenwhocode.womenwhocode.adapters.FeaturesAdapter;
import com.womenwhocode.womenwhocode.models.Feature;
import com.womenwhocode.womenwhocode.models.Recommendation;
import com.womenwhocode.womenwhocode.models.Subscribe;
import com.womenwhocode.womenwhocode.utils.LocalDataStore;
import com.womenwhocode.womenwhocode.utils.NetworkConnectivityReceiver;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Created by shehba.shahab on 10/16/15.
 */
public class FeaturesFragment extends Fragment {
    private final int RUN_FREQUENCY = 1000; // ms
    private FeaturesAdapter aFeatures;
    private RecyclerView rvFeatures;
    private ProgressBar pb;
    private ArrayList<Feature> features;
    private OnFeatureItemClickListener listener;
    private ParseUser currentUser;
    private ArrayList<Feature> recommendedFeatures;
    private ArrayList<Feature> subscribedFeatures;
    private int listCounter;
    private ArrayList<Object> items;
    private Runnable runnable;
    private Handler handler;
    private HashSet<Feature> featuresSet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        items = new ArrayList<>();
        features = new ArrayList<>();
        aFeatures = new FeaturesAdapter(items);
        currentUser = ParseUser.getCurrentUser();
        recommendedFeatures = new ArrayList<>();
        subscribedFeatures = new ArrayList<>();
        listCounter = 0;
        handler = new Handler();
        featuresSet = new HashSet<>();

        // Defines a runnable which is run every 100ms
        runnable = new Runnable() {
            @Override
            public void run() {

                // FIXME: only do this when there are new messages - handler.postDelayed(this, RUN_FREQUENCY);
                if (listCounter == 3) {
                    loadItems();
                } else if (listCounter < 3) {
                    handler.postDelayed(this, RUN_FREQUENCY);
                } else {
                    handler.removeCallbacks(this);
                    listCounter = 0;
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_features, container, false);

        rvFeatures = (RecyclerView) view.findViewById(R.id.lvFeatures);
        rvFeatures.setVisibility(RecyclerView.INVISIBLE);

        // show progress bar in the meantime
        pb = (ProgressBar) view.findViewById(R.id.pbLoading);
        pb.setVisibility(ProgressBar.VISIBLE);

        rvFeatures.setAdapter(aFeatures);
        rvFeatures.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFeatures.setItemAnimator(new SlideInUpAnimator()); // FIXME: can't tell if it's working!

        populateFeaturesList();
        handler.postDelayed(runnable, RUN_FREQUENCY);

        aFeatures.setOnItemClickListener(new FeaturesAdapter.OnItemClickListener() { // get on click listener working for this
            @Override
            public void onItemClick(View itemView, int position) {
                if (items.get(position) instanceof Feature) {
                    Feature feature = (Feature) items.get(position);
                    if (feature.getTitle().contains("Recommend")) {
                        showEditDialog();
                    } else {
                        listener.onFeatureClickListener(feature, itemView);
                    }
                }
            }
        });

        return view;
    }

    private void showEditDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        RecommendFeatureDialog recommendFeatureDialog = RecommendFeatureDialog.newInstance();
        recommendFeatureDialog.show(fm, "fragment_dialog_recommend");
    }

    private void populateFeaturesList() {
        ParseQuery<Feature> query = ParseQuery.getQuery(Feature.class);

        if (!NetworkConnectivityReceiver.isNetworkAvailable(getContext())) {
            query.fromPin(LocalDataStore.FEATURES_PIN);
        }

        ParseQuery<Subscribe> subscribeParseQuery = ParseQuery.getQuery(Subscribe.class);
        subscribeParseQuery.whereEqualTo(Subscribe.USER_KEY, currentUser);
        subscribeParseQuery.whereExists(Subscribe.FEATURE_KEY);
        subscribeParseQuery.whereEqualTo(Subscribe.SUBSCRIBED_KEY, true);
        subscribeParseQuery.include("feature");
        subscribeParseQuery.findInBackground(new FindCallback<Subscribe>() {
            @Override
            public void done(List<Subscribe> list, ParseException e) {
                if (e == null && list.size() > 0) {
                    subscribedFeatures.clear();
                    for (Subscribe subscribe : list) {
                        subscribedFeatures.add(subscribe.getFeature());
                    }
                }
                listCounter++;
            }
        });

        ParseQuery<Recommendation> recommendationParseQuery = ParseQuery.getQuery(Recommendation.class);
        recommendationParseQuery.whereEqualTo(Recommendation.USER_ID_KEY, currentUser.getObjectId());
        recommendationParseQuery.whereEqualTo(Recommendation.VALID_KEY, true);
        recommendationParseQuery.include("feature");
        recommendationParseQuery.findInBackground(new FindCallback<Recommendation>() {
            @Override
            public void done(List<Recommendation> list, ParseException e) {
                if (e == null && list.size() > 0) { // has dups
                    recommendedFeatures.clear();
                    for (Recommendation recommendation : list) {
                        recommendedFeatures.add(recommendation.getFeature());
                    }
                }
                listCounter++;
            }
        });

        // FIXME: remove dups
        query.findInBackground(new FindCallback<Feature>() {
            public void done(List<Feature> lFeatures, ParseException e) {
                if (e == null && lFeatures.size() > 0) {
                    features.clear(); // this won't work
                    // only add after feature item
                    features.addAll(lFeatures);
                    LocalDataStore.unpinAndRepin(lFeatures, LocalDataStore.FEATURES_PIN);
                } else {
                    Log.d("Message", "Error: " + e.getMessage());
                }
                listCounter++;
            }
        });
    }

    private void loadItems() {
        handler.removeCallbacks(runnable);
        // stop handler
        items.clear();
        featuresSet.clear();
        if (subscribedFeatures.size() > 0) { // would it be safe to check for null?
            items.add("Following:");
            for (Feature f : subscribedFeatures) {
                if (featuresSet.add(f)) {
                    items.add(f);
                }
            }
        }

        if (recommendedFeatures.size() > 0) {
            items.add("Recommended for you:");
            for (Feature f : recommendedFeatures) {
                if (featuresSet.add(f)) {
                    items.add(f);
                }
            }
        }

        items.add("All topics:");
        for (Feature f : features) {
            if (featuresSet.add(f)) {
                items.add(f);
            }
        }

        aFeatures.notifyDataSetChanged(); // FIXME: last resort, do something else
        // hide progress bar, make list view appear
        pb.setVisibility(ProgressBar.GONE);
        rvFeatures.setVisibility(RecyclerView.VISIBLE);
        listCounter = 0;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        if (activity instanceof OnFeatureItemClickListener) {
            listener = (OnFeatureItemClickListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement FeaturesFragment.OnFeatureItemClickListener");
        }
    }

    public interface OnFeatureItemClickListener {
        void onFeatureClickListener(Feature feature, View itemView);
    }
}