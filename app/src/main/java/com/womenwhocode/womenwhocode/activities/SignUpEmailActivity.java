package com.womenwhocode.womenwhocode.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.womenwhocode.womenwhocode.R;
import com.womenwhocode.womenwhocode.utils.KeyBoardSupport;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by pnroy on 10/22/15.
 */
public class SignUpEmailActivity extends AppCompatActivity {
    private String userAns = "";

    private static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private static boolean isValidUsername(CharSequence target) {
        return !TextUtils.isEmpty(target);
    }

    private static boolean isValidPassword(CharSequence target) {
        return !(TextUtils.isEmpty(target) || target.length() <= 5);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_sign_up);
        Bundle extras = getIntent().getExtras();
        KeyBoardSupport.helperHideKeyboard(findViewById(R.id.signUp), this);

        if (extras != null) {
            userAns = extras.getString("userAns");
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void signUpUser(View view) {

        //ParseUser userDetail = new ParseUser();
        RelativeLayout msnackbar = (RelativeLayout) findViewById(R.id.signUp);
        EditText tvName = (EditText) findViewById(R.id.txtName);
        EditText tvEmail = (EditText) findViewById(R.id.txtEmail);
        EditText tvPassword = (EditText) findViewById(R.id.txtPwd);
        final String name = tvName.getText().toString();
        final String email = tvEmail.getText().toString();
        final String password = tvPassword.getText().toString();
        boolean validEmail = isValidEmail(email);
        boolean validUsername = isValidUsername(name);
        boolean validPassword = isValidPassword(password);
        if (!validUsername && !validPassword) {
            Snackbar.make(msnackbar, getResources().getString(R.string.error_invalid_username_password), Snackbar.LENGTH_LONG).show();
        } else if (!validEmail) {
            Snackbar.make(msnackbar, getResources().getString(R.string.error_invalid_email), Snackbar.LENGTH_LONG).show();
        } else if (!validPassword) {
            Snackbar.make(msnackbar, getResources().getString(R.string.error_invalid_password), Snackbar.LENGTH_LONG).show();
        } else {
            ParseUser user = new ParseUser();
            user.setUsername(tvName.getText().toString());
            user.setPassword(tvPassword.getText().toString());
            user.setEmail(tvEmail.getText().toString());


            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        try {
                            // [ ] TODO: auto subscribe user to features with auto subscribe true

                            Intent i = new Intent(SignUpEmailActivity.this, UserProfileActivity.class);
                            i.putExtra("Name", name);
                            i.putExtra("Email", email);
                            i.putExtra("Password", password);
                            i.putExtra("userAns", userAns);
                            startActivity(i);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        } catch (Exception ex) {

                        }
                    } else {
                        Log.d("User Creation failed", e.toString());
                    }
                }
            });


        }
    }

    public void goHome(View view) {
        Intent intent = new Intent(SignUpEmailActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}


