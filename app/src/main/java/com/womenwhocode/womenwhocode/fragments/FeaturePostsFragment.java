package com.womenwhocode.womenwhocode.fragments;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.womenwhocode.womenwhocode.models.Feature;
import com.womenwhocode.womenwhocode.models.Post;
import com.womenwhocode.womenwhocode.utils.LocalDataStore;
import com.womenwhocode.womenwhocode.utils.NetworkConnectivityReceiver;

import android.os.Bundle;
import android.util.Log;

import java.util.List;

/**
 * Created by shehba.shahab on 10/27/15.
 */
public class FeaturePostsFragment extends PostsListFragment {
    private static final String FEATURE_ID = "feature_id";
    private static String featureId;
    private ParseQuery<Post> postParseQuery;
    private Feature feature;

    public static FeaturePostsFragment newInstance(String featureObjectId) {
        FeaturePostsFragment featurePostsFragment = new FeaturePostsFragment();
        Bundle args = new Bundle();
        args.putString(FEATURE_ID, featureObjectId);
        featurePostsFragment.setArguments(args);
        return featurePostsFragment;
    }

    @Override
    protected void populatePosts() {
        featureId = getArguments().getString(FEATURE_ID, "");

        ParseQuery<Feature> featureParseQuery = ParseQuery.getQuery(Feature.class);
        postParseQuery = ParseQuery.getQuery(Post.class);
        if (!NetworkConnectivityReceiver.isNetworkAvailable(getContext())) {
            postParseQuery.fromPin(featureId);
        }

        featureParseQuery.getInBackground(featureId, new GetCallback<Feature>() {
            @Override
            public void done(Feature f, ParseException e) {
                feature = f;
                postParseQuery.whereEqualTo(Post.FEATURE_KEY, feature);
                postParseQuery.orderByDescending("createdAt");
                postParseQuery.findInBackground(new FindCallback<Post>() {
                    @Override
                    public void done(List<Post> list, ParseException e) {
                        if (e == null && list.size() > 0) {
                            clear();
                            add(list);
                            notifiedDataChanged();
                            clearSpinners();
                            LocalDataStore.unpinAndRepin(list, featureId);
                        } else if (e != null) {
                            Log.d("FEATURES_POST_FAIL", "Error: " + e.getMessage());
                        } else {
                            clearSpinners();
                            noPostsView(feature.getHexColor());
                        }
                    }
                });
            }
        });
    }
}
