package com.womenwhocode.womenwhocode.utils;

import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import android.util.Log;

import java.util.List;

/**
 * Created by zassmin on 10/23/15.
 */
public class LocalDataStore {
    public static final String EVENT_PIN = "events";
    public static final String FEATURES_PIN = "features";
    public static final String POSTS_PIN = "posts";
    public static final String PROFILE_PIN = "user_profile";
    public static final String LOCAL_NETWORK_PIN = "local_networks";
    public static final String MESSAGE_PIN = "messages";
    public static String NETWORK_PIN = "networks";
    public static String EVENT_POSTS_PIN = "event_posts";
    public static String FEATURE_POSTS_PIN = "feature_posts";
    public static String PERSONALIZATION_DETAIL_PIN = "personalization_detail";

    public static void unpinAndRepin(final List data, final String pinName) {
        // Unpin old data and pin new data to local datastore
        ParseObject.unpinAllInBackground(pinName, new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // Add the latest results for this query to the cache.
                    Log.d("PARSE_PIN_WIN", pinName);
                    ParseObject.pinAllInBackground(pinName, data);
                } else {
                    Log.d("PARSE_PIN_ERROR", e.toString());
                }
            }
        });
    }
}
