package com.example.womenwhocode.womenwhocode;

import android.app.Application;

import com.example.womenwhocode.womenwhocode.models.Event;
import com.example.womenwhocode.womenwhocode.models.Feature;
import com.example.womenwhocode.womenwhocode.models.Network;
import com.example.womenwhocode.womenwhocode.models.PersonalizationDetail;
import com.example.womenwhocode.womenwhocode.models.Post;
import com.example.womenwhocode.womenwhocode.models.Profile;
import com.example.womenwhocode.womenwhocode.models.Subscribe;
import com.example.womenwhocode.womenwhocode.models.User;
import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by zassmin on 10/15/15.
 */
public class ParseApplication extends Application {
    public static final String PARSE_APPLICATION_ID = "twI2kWhB1ImLgnc5rDpA2F8b7TfRVImbZqsywWO1";
    public static final String PARSE_CLIENT_KEY = "QpMOPn0fte0sdkY8GYKcpsSFhEY2rn3eoJxH73wO";

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        // Register parse models
        ParseObject.registerSubclass(Network.class);
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(Feature.class);
        ParseObject.registerSubclass(PersonalizationDetail.class);
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Profile.class);
        ParseObject.registerSubclass(Subscribe.class);
        ParseObject.registerSubclass(User.class);

        // parse initialization
        Parse.initialize(this, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);
    }
}
