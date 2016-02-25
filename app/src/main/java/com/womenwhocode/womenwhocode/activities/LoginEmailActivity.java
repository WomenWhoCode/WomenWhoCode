package com.womenwhocode.womenwhocode.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.womenwhocode.womenwhocode.R;
import com.womenwhocode.womenwhocode.WomenWhoCodeApplication;
import com.womenwhocode.womenwhocode.models.Message;
import com.womenwhocode.womenwhocode.models.Profile;
import com.womenwhocode.womenwhocode.utils.KeyBoardSupport;

import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by pnroy on 10/19/15.
 */
public class LoginEmailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);
        KeyBoardSupport.helperHideKeyboard(findViewById(R.id.email_login), this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void Login(View view) {
        EditText etEmail = (EditText) findViewById(R.id.etEmail);
        String email = etEmail.getText().toString();
        EditText etPassword = (EditText) findViewById(R.id.etPwd);
        final String pwd = etPassword.getText().toString();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("email", email); //
        query.findInBackground(new FindCallback<ParseUser>() {

            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() != 0) {
                        ValidateUser(objects.get(0).getString("username"), pwd);
                        // Toast.makeText(getBaseContext(), objects.get(0).getString("username"), Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    Toast.makeText(getBaseContext(), "Query unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ValidateUser(String username, String pwd) {
        ParseUser.logInInBackground(username, pwd, new LogInCallback() {
            public void done(final ParseUser user, ParseException e) {
                if (user != null) {

                    Profile profile = null;
                    try {
                        profile = user.getParseObject(Message.PROFILE_KEY).fetchIfNeeded();
                    } catch (ParseException | NullPointerException e1) {
                        e1.printStackTrace();
                    }

                    // temporary backwards migration for users without profiles
                    if (profile == null) {
                        WomenWhoCodeApplication.currentPosition = 0; // default theme
                        ParseQuery<Profile> profileParseQuery = ParseQuery.getQuery(Profile.class);
                        profileParseQuery.whereEqualTo(Profile.USER_KEY, user);
                        profileParseQuery.getFirstInBackground(new GetCallback<Profile>() {
                            @Override
                            public void done(Profile p, ParseException e) {
                                if (e == null) {
                                    // set profile on current user
                                    user.put(Message.PROFILE_KEY, p);
                                    user.saveInBackground();
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

                    //Toast.makeText(getBaseContext(), "User Login successful", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginEmailActivity.this, TimelineActivity.class);
                    startActivity(i);
                    finish();
                    LoginActivity.loginActivityinstance.finish();
                    PersonalizationActivity.personlisationActivityinstance.finish();
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                } else {
//                    Toast.makeText(getBaseContext(), "User Login failed", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
