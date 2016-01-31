package com.womenwhocode.womenwhocode;

import com.parse.Parse;
import com.parse.ParseObject;
import com.womenwhocode.womenwhocode.models.Awesome;
import com.womenwhocode.womenwhocode.models.Event;
import com.womenwhocode.womenwhocode.models.Feature;
import com.womenwhocode.womenwhocode.models.FeatureTag;
import com.womenwhocode.womenwhocode.models.Message;
import com.womenwhocode.womenwhocode.models.Network;
import com.womenwhocode.womenwhocode.models.Notification;
import com.womenwhocode.womenwhocode.models.PersonalizationDetails;
import com.womenwhocode.womenwhocode.models.Post;
import com.womenwhocode.womenwhocode.models.Profile;
import com.womenwhocode.womenwhocode.models.Recommendation;
import com.womenwhocode.womenwhocode.models.Subscribe;
import com.womenwhocode.womenwhocode.models.Tag;
import com.womenwhocode.womenwhocode.models.UserNotification;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by zassmin on 10/15/15.
 */
public class WomenWhoCodeApplication extends Application {
    private static final String PARSE_APPLICATION_ID = "twI2kWhB1ImLgnc5rDpA2F8b7TfRVImbZqsywWO1";
    private static final String PARSE_CLIENT_KEY = "QpMOPn0fte0sdkY8GYKcpsSFhEY2rn3eoJxH73wO";
    public static int currentPosition;

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        // Register parse models
        ParseObject.registerSubclass(Network.class);
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(Feature.class);
        ParseObject.registerSubclass(PersonalizationDetails.class);
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Profile.class);
        ParseObject.registerSubclass(Subscribe.class);
        ParseObject.registerSubclass(Awesome.class);
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(Tag.class);
        ParseObject.registerSubclass(FeatureTag.class);
        ParseObject.registerSubclass(Recommendation.class);
        ParseObject.registerSubclass(UserNotification.class);
        ParseObject.registerSubclass(Notification.class);

        // parse initialization
        Parse.initialize(this, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Hind-Regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }
}
