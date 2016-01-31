package com.womenwhocode.womenwhocode.activities;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.womenwhocode.womenwhocode.R;
import com.womenwhocode.womenwhocode.WomenWhoCodeApplication;
import com.womenwhocode.womenwhocode.models.Message;
import com.womenwhocode.womenwhocode.models.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            Intent signUpIntent = new Intent(this, LoginActivity.class);
            startActivity(signUpIntent);
        } else {
            Profile profile = null;
            try {
                profile = currentUser.getParseObject(Message.PROFILE_KEY).fetchIfNeeded();
            } catch (ParseException | NullPointerException e) {
                e.printStackTrace();
            }
            // temporary backwards migration for users without profiles
            if (profile == null) {
                WomenWhoCodeApplication.currentPosition = 0; // default theme
                ParseQuery<Profile> profileParseQuery = ParseQuery.getQuery(Profile.class);
                profileParseQuery.whereEqualTo(Profile.USER_KEY, currentUser);
                profileParseQuery.getFirstInBackground(new GetCallback<Profile>() {
                    @Override
                    public void done(Profile p, ParseException e) {
                        if (e == null) {
                            // set profile on current user
                            currentUser.put(Message.PROFILE_KEY, p);
                            currentUser.saveInBackground();
                            // set default theme on user's profile
                            p.setTheme(0);
                            p.saveInBackground();
                        }
                    }
                });
            } else {
                if (profile.getTheme() >= 0) {
                    WomenWhoCodeApplication.currentPosition = profile.getTheme();
                } else {
                    WomenWhoCodeApplication.currentPosition = 0;
                }
            }
            Intent timelineIntent = new Intent(this, TimelineActivity.class);
            startActivity(timelineIntent);
        }
    }
}
