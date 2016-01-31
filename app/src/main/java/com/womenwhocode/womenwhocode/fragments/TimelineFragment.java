package com.womenwhocode.womenwhocode.fragments;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.womenwhocode.womenwhocode.R;
import com.womenwhocode.womenwhocode.WomenWhoCodeApplication;
import com.womenwhocode.womenwhocode.adapters.TimelineAdapter;
import com.womenwhocode.womenwhocode.models.Awesome;
import com.womenwhocode.womenwhocode.models.Event;
import com.womenwhocode.womenwhocode.models.Feature;
import com.womenwhocode.womenwhocode.models.Post;
import com.womenwhocode.womenwhocode.models.Subscribe;
import com.womenwhocode.womenwhocode.models.UserNotification;
import com.womenwhocode.womenwhocode.utils.LocalDataStore;
import com.womenwhocode.womenwhocode.utils.NetworkConnectivityReceiver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Created by shehba.shahab on 10/16/15.
 */
public class TimelineFragment extends Fragment {
    private final int RUN_FREQUENCY = 1000; // ms
    private TimelineAdapter aPosts;
    private ArrayList<Post> posts;
    private RecyclerView rvPosts;
    private ProgressBar pb;
    private ParseUser currentUser;
    private ParseQuery<Awesome> awesomeParseQuery;
    private OnItemClickListener listener;
    private ArrayList<UserNotification> userNotifs;
    private ArrayList<Object> items;
    private Runnable runnable;
    private Handler handler;
    private int listCounter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        posts = new ArrayList<>();
        items = new ArrayList<>();
        aPosts = new TimelineAdapter(items);
        currentUser = ParseUser.getCurrentUser();
        awesomeParseQuery = ParseQuery.getQuery(Awesome.class);
        userNotifs = new ArrayList<>();
        listCounter = 0;
        handler = new Handler();

        // Defines a runnable which is run every 100ms
        runnable = new Runnable() {
            @Override
            public void run() {

                // FIXME: only do this when there are new messages - handler.postDelayed(this, RUN_FREQUENCY);
                if (listCounter == 2) {
                    loadItems();
                } else if (listCounter < 2) {
                    handler.postDelayed(this, RUN_FREQUENCY);
                } else {
                    listCounter = 0;
                    handler.removeCallbacks(this);
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        rvPosts = (RecyclerView) view.findViewById(R.id.lvPosts);
        rvPosts.setAdapter(aPosts);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPosts.setItemAnimator(new SlideInUpAnimator());

        // hide list view until data is fully loaded
        rvPosts.setVisibility(ListView.INVISIBLE);

        // show progress bar in the meantime
        pb = (ProgressBar) view.findViewById(R.id.pbLoading);
        pb.setVisibility(ProgressBar.VISIBLE);

        // runnable
        handler.postDelayed(runnable, RUN_FREQUENCY);

        // listeners
        aPosts.setOnItemClickListener(new TimelineAdapter.OnItemClickListener() {

            @Override
            public void onPostFeatureClick(View itemView, int position) {
                Post post = (Post) items.get(position);
                Feature feature = post.getFeature();
                Event event = post.getEvent();

                if (feature != null) {
                    listener.onFeatureTimelineClickListener(feature, itemView);
                } else if (event != null) {
                    listener.onEventTimelineClickListener(event, itemView);
                }
            }

            @Override
            public void onAwesomeClick(final View itemView, final int position) {
                final Post post = (Post) items.get(position);

                // find awesome object
                awesomeParseQuery.whereEqualTo(Awesome.POST_KEY, post);
                awesomeParseQuery.whereEqualTo(Awesome.USER_KEY, currentUser);
                awesomeParseQuery.getFirstInBackground(new GetCallback<Awesome>() {
                    @Override
                    public void done(Awesome awesome, ParseException e) {
                        if (e == null) {
                            onAwesome(awesome, post, itemView);
                        } else {
                            onAwesome(null, post, itemView);
                        }
                    }
                });
            }

            @Override
            public void onShareButtonClick(final View itemView, final int position) {
                final Post post = (Post) items.get(position);
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = post.getDescription();
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.subject_share_post);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }

            @Override
            public void onClose(View itemView, int position) {
                // FIXME: there is a potential bug!
                UserNotification un = (UserNotification) items.get(position);

                // update UI
                items.remove(position);
                userNotifs.clear(); // temp hack! - doesn't work if we have more than one notif
                aPosts.notifyDataSetChanged();

                // save to parse
                un.setEnabled(false);
                un.saveInBackground();
            }
        });

        populatePostsList();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        if (activity instanceof OnItemClickListener) {
            listener = (OnItemClickListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement TimelineFragment.OnItemClickListener");
        }
    }

    private void populatePostsList() {
        ParseUser currentUser = ParseUser.getCurrentUser();

        // get notifications
        ParseQuery<UserNotification> userNotificationParseQuery = ParseQuery.getQuery(UserNotification.class);
        userNotificationParseQuery.whereEqualTo(UserNotification.USER_ID_KEY, currentUser.getObjectId());
        userNotificationParseQuery.whereEqualTo(UserNotification.ENABLED_KEY, true);
        userNotificationParseQuery.include(UserNotification.NOTIFICATION_KEY);
        userNotificationParseQuery.findInBackground(new FindCallback<UserNotification>() {
            @Override
            public void done(List<UserNotification> list, ParseException e) {
                if (e == null && list.size() > 0) {
                    userNotifs.clear();
                    userNotifs.addAll(list); // might just be able to directly append to 0...
                }
                listCounter++;
            }
        });

        // get posts
        ParseQuery<Post> postQuery = ParseQuery.getQuery(Post.class);
        ParseQuery<Subscribe> subscribeQuery = ParseQuery.getQuery(Subscribe.class);
        subscribeQuery.whereEqualTo(Subscribe.USER_KEY, currentUser);
        subscribeQuery.whereEqualTo(Subscribe.SUBSCRIBED_KEY, true);

        if (!NetworkConnectivityReceiver.isNetworkAvailable(getContext())) {
            postQuery.fromPin(LocalDataStore.POSTS_PIN);
        }

        ParseQuery<Post> postFeatureQuery = ParseQuery.getQuery(Post.class);
        postFeatureQuery.whereMatchesKeyInQuery(Post.FEATURE_KEY, Subscribe.FEATURE_KEY, subscribeQuery);

        ParseQuery<Post> postEventQuery = ParseQuery.getQuery(Post.class);
        postEventQuery.whereMatchesKeyInQuery(Post.EVENT_KEY, Subscribe.EVENT_KEY, subscribeQuery);

        List<ParseQuery<Post>> subscribedFeaturesAndEvents = new ArrayList<>();
        subscribedFeaturesAndEvents.add(postFeatureQuery);
        subscribedFeaturesAndEvents.add(postEventQuery);

        // Return all posts for features or events to which the user is subscribed
        postQuery = ParseQuery.or(subscribedFeaturesAndEvents);
        postQuery.orderByDescending(Post.AWESOME_COUNT_KEY);
        postQuery.include(Post.FEATURE_KEY);
        postQuery.include(Post.USER_KEY);
        postQuery.include(Post.EVENT_KEY);
        postQuery.findInBackground(new FindCallback<Post>() {
            public void done(List<Post> listPosts, ParseException e) {
                if (e != null) {
                    Log.d("Message", "Error: " + e.getMessage());
                } else if (listPosts != null) {
                    posts.clear();
                    posts.addAll(listPosts);

                    LocalDataStore.unpinAndRepin(listPosts, LocalDataStore.POSTS_PIN);
                }

                listCounter++;
            }
        });
    }

    private void loadItems() {
        handler.removeCallbacks(runnable);
        // stop handler
        items.clear();

        if (userNotifs.size() > 0) {
            items.addAll(userNotifs);
        }

        if (posts.size() > 0) {
            items.addAll(posts);
        }

        listCounter = 0;
        aPosts.notifyDataSetChanged();
        // hide progress bar, make list view appear
        pb.setVisibility(ProgressBar.GONE);
        rvPosts.setVisibility(ListView.VISIBLE);
    }

    private void animateOnAwesome(final ImageButton awesomeIcon) {
        switch (WomenWhoCodeApplication.currentPosition) {
            case 0:
                Glide.with(getContext()).load(R.raw.awesomeddd_light).asGif().into(awesomeIcon);
                break;
            case 1:
                Glide.with(getContext()).load(R.raw.awesomeddd_dark).asGif().into(awesomeIcon);
                break;
            default:
                Log.d("NO_THEME", "No theme selected.");
                break;
        }

        Runnable onAwesomeRunnable = new Runnable() {
            @Override
            public void run() {
                awesomeIcon.setImageResource(R.drawable.awesomeddd);
            }
        };
        handler.postDelayed(onAwesomeRunnable, 2500);
    }

    private void onAwesome(Awesome awesome, Post savedPost, View v) {
        int awesomeCount = savedPost.getAwesomeCount(); // Get latest value
        ImageButton awesomeIcon = (ImageButton) v.findViewById(R.id.btnAwesomeIcon);
        TextView tvAwesomeCount = (TextView) v.findViewById(R.id.tvAwesomeCount);

        if (awesome != null) {
            if (awesome.getAwesomed()) {
                // Update UI thread
                awesomeCount--;
                awesomeIcon.setImageResource(R.drawable.awesome);

                // Build parse request
                awesome.setAwesomed(false);
            } else {
                // Update UI thread
                awesomeCount++;
                animateOnAwesome(awesomeIcon);

                // Build parse request
                awesome.setAwesomed(true);
            }
        } else {
            // Update UI thread
            awesomeCount++;
            animateOnAwesome(awesomeIcon);

            // Build parse request
            awesome = new Awesome();
            awesome.setAwesomed(true);
            awesome.setUser(currentUser);
            awesome.setPost(savedPost);
        }

        // Update the UI thread
        tvAwesomeCount.setText(getString(R.string.label_awesome_x, awesomeCount));

        // Send data to parse
        awesome.saveInBackground();
        savedPost.setAwesomeCount(awesomeCount);
        savedPost.saveInBackground();
    }

    public interface OnItemClickListener {
        void onFeatureTimelineClickListener(Feature feature, View itemView);

        void onEventTimelineClickListener(Event event, View itemView);
    }
}