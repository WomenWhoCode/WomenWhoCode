package com.womenwhocode.womenwhocode.activities;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.womenwhocode.womenwhocode.R;
import com.womenwhocode.womenwhocode.fragments.AddPostDialogFragment;
import com.womenwhocode.womenwhocode.fragments.FeaturePostsFragment;
import com.womenwhocode.womenwhocode.models.Feature;
import com.womenwhocode.womenwhocode.models.Post;
import com.womenwhocode.womenwhocode.models.Subscribe;
import com.womenwhocode.womenwhocode.utils.LocalDataStore;
import com.womenwhocode.womenwhocode.utils.NetworkConnectivityReceiver;
import com.womenwhocode.womenwhocode.utils.RoundedImageView;
import com.womenwhocode.womenwhocode.utils.ThemeUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by shehba.shahab on 10/18/15.
 */
public class FeatureDetailsActivity extends AppCompatActivity implements
        AddPostDialogFragment.OnSubmitPostListener {

    private static final String SUBSCRIBED_TEXT = "unfollow";
    private static final String SUBSCRIBE_TEXT = "follow";
    private static final String SUBSCRIBERS_TEXT = " followers";
    private String feature_id;
    private RelativeLayout rlFeatures;
    private Feature feature;
    private TextView tvFeatureTitle;
    private TextView tvFeatureDescription;
    private TextView tvSubscriberCount;
    private Button btnSubscribe;
    private Toolbar toolbar;
    private TextView tvToolbarTitle;
    private ParseUser currentUser;
    private Subscribe subscribe;
    private int subscribeCount;
    private RoundedImageView ivFeatureImage;

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_feature_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_view_followers:
                return true;
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // MUST BE SET BEFORE setContentView
        ThemeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_feature_details);

        // set tool bar to replace actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false); // hide the action bar title to only so toolbar title

        currentUser = ParseUser.getCurrentUser();
        feature_id = getIntent().getStringExtra("feature_id");

        setUpView();
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        // get fragment position from parent class
        int fragmentPosition = getIntent().getIntExtra(TimelineActivity.SELECTED_TAB_EXTRA_KEY, 0);
        // send position back to parent
        Intent newIntent = new Intent(this, TimelineActivity.class);
        newIntent.putExtra(TimelineActivity.SELECTED_TAB_EXTRA_KEY, fragmentPosition);
        // Return the created intent as the "up" activity
        return newIntent;

    }

    private void setUpView() {
        // hide scroll view so the progress bar is the center of attention
        rlFeatures = (RelativeLayout) findViewById(R.id.rlFeatures); // TODO: animate!
        tvToolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tvFeatureTitle = (TextView) findViewById(R.id.tvEventTopicTitle);
        tvFeatureDescription = (TextView) findViewById(R.id.tvFeatureDescription);
        tvSubscriberCount = (TextView) findViewById(R.id.tvSubscriberCount);
        btnSubscribe = (Button) findViewById(R.id.btnSubscribe);
        ivFeatureImage = (RoundedImageView) findViewById(R.id.ivEventTopicPhoto);

        ParseQuery<Feature> query = ParseQuery.getQuery(Feature.class);

        if (!NetworkConnectivityReceiver.isNetworkAvailable(this)) {
            query.fromPin(LocalDataStore.FEATURES_PIN);
        }

        query.getInBackground(feature_id, new GetCallback<Feature>() {
            public void done(Feature parseFeature, ParseException e) {
                if (e == null) {
                    if (parseFeature != null) {
                        feature = parseFeature;
                        setFeatureData();

                        // set up count
                        subscribeCount = feature.getSubscribeCount();
                        tvSubscriberCount.setText(String.valueOf(subscribeCount + SUBSCRIBERS_TEXT));

                        ParseQuery<Subscribe> subscribeParseQuery = ParseQuery.getQuery(Subscribe.class);
                        subscribeParseQuery.whereEqualTo(Subscribe.FEATURE_KEY, feature);
                        subscribeParseQuery.whereEqualTo(Subscribe.USER_KEY, currentUser);
                        subscribeParseQuery.getFirstInBackground(new GetCallback<Subscribe>() {
                            @Override
                            public void done(Subscribe sub, ParseException e) {
                                if (sub != null) {
                                    subscribe = sub;
                                    if (sub.getSubscribed()) {
                                        btnSubscribe.setText(SUBSCRIBED_TEXT);
                                    } else {
                                        btnSubscribe.setText(SUBSCRIBE_TEXT);
                                    }
                                } else {
                                    btnSubscribe.setText(SUBSCRIBE_TEXT);
                                }
                            }
                        });
                    } else {
                        Log.d("FEATURE_PS_NO_DATA", e.toString());
                    }
                } else {
                    Log.d("FEATURE_PS_ERROR", e.toString());
                }
            }
        });

        getSupportFragmentManager().beginTransaction().add(R.id.flFeatureContainer, FeaturePostsFragment.newInstance(feature_id)).commit();
    }

    private void setFeatureData() {
        String title = feature.getTitle();

        String description = feature.getDescription();

        int color = Color.parseColor(String.valueOf(feature.getHexColor()));
        rlFeatures.setBackgroundColor(color);

        String imageUrl = feature.getImageUrl();
        if (feature.getPhotoFile() != null) {
            imageUrl = feature.getPhotoFile().getUrl();
        }
        Picasso.with(this)
                .load(imageUrl)
                .resize(40, 40)
                .centerCrop()
                .into(ivFeatureImage);

        tvFeatureTitle.setText(title);
        tvToolbarTitle.setText(title);
        tvFeatureDescription.setText(description);
    }

    public void onSubscribe(View view) {
        btnSubscribe = (Button) findViewById(R.id.btnSubscribe);

        if (subscribe != null) {
            if (subscribe.getSubscribed()) { // maybe just check against icon value
                subscribe.setSubscribed(false);

                // decrement counter
                subscribeCount = feature.getSubscribeCount() - 1;
                feature.setSubscribeCount(subscribeCount);
                feature.saveInBackground();
                tvSubscriberCount.setText(String.valueOf(subscribeCount + SUBSCRIBERS_TEXT));

                subscribe.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        btnSubscribe.setText(SUBSCRIBE_TEXT);
                    }
                });
            } else {
                subscribe.setSubscribed(true);

                // increment counter
                subscribeCount = feature.getSubscribeCount() + 1;
                feature.setSubscribeCount(subscribeCount);
                feature.saveInBackground();
                tvSubscriberCount.setText(String.valueOf(subscribeCount + SUBSCRIBERS_TEXT));

                subscribe.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        btnSubscribe.setText(SUBSCRIBED_TEXT);
                    }
                });
            }
        } else {
            // create subscription - stays the same
            subscribe = new Subscribe();
            subscribe.setSubscribed(true);
            subscribe.setUser(currentUser);
            subscribe.setFeature(feature);

            // increment counter
            subscribeCount = feature.getSubscribeCount() + 1;
            feature.setSubscribeCount(subscribeCount);
            feature.saveInBackground();
            tvSubscriberCount.setText(String.valueOf(subscribeCount + SUBSCRIBERS_TEXT));

            subscribe.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    btnSubscribe.setText(SUBSCRIBED_TEXT);
                }
            });
        }
    }

    public void onLaunchAddPostDialog(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        AddPostDialogFragment addPostDialogFragment = AddPostDialogFragment.newInstance();
        addPostDialogFragment.show(fm, "fragment_add_post");
    }

    private void addPost(String postBody, Bitmap finalImg) {
        final Post post = new Post();
        post.setDescription(postBody);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (finalImg != null) {
            finalImg.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            // get byte array here
            byte[] bytearray = stream.toByteArray();
            ParseFile imgFile = new ParseFile("profileImg.png", bytearray);
            imgFile.saveInBackground();
            post.setPostPicFile(imgFile);
        }
        post.setUser(currentUser);
        post.setFeature(feature);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                // notify fragment
                FeaturePostsFragment pf = (FeaturePostsFragment) getSupportFragmentManager().getFragments().get(0); // make sure it will aways be that 0! posts are zero in view pager
                if (null != pf) {
                    pf.setReceivedPost(post);
                }
            }
        });

    }

    @Override
    public void onSubmitPostListener(String inputText, Bitmap finalimg) {
        addPost(inputText, finalimg);
        CoordinatorLayout v = (CoordinatorLayout) findViewById(R.id.rlPostLists);
        Snackbar.make(v, R.string.msg_thanks_adding_post, Snackbar.LENGTH_SHORT).show();
        // FIXME: make it so you go to the last item position when this is final so the user can see their post was submitted
        // FIXME: add post to bottom of the list!
    }
}



