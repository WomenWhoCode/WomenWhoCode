package com.example.womenwhocode.womenwhocode.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
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

import com.bumptech.glide.Glide;
import com.example.womenwhocode.womenwhocode.ParseApplication;
import com.example.womenwhocode.womenwhocode.R;
import com.example.womenwhocode.womenwhocode.adapters.PostsAdapter;
import com.example.womenwhocode.womenwhocode.models.Awesome;
import com.example.womenwhocode.womenwhocode.models.Post;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Created by zassmin on 10/26/15.
 */
public class PostsListFragment extends Fragment {
    private View v;
    private RecyclerView rvPosts;
    private ArrayList<Post> posts;
    private PostsAdapter aPosts;
    private ProgressBar pb;
    private ParseQuery<Awesome> awesomeParseQuery;
    private ParseUser currentUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        posts = new ArrayList<>();
        aPosts = new PostsAdapter(posts);
        awesomeParseQuery = ParseQuery.getQuery(Awesome.class);
        currentUser = ParseUser.getCurrentUser();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_posts_list, container, false);
        rvPosts = (RecyclerView) v.findViewById(R.id.lvPosts);

        // setup the view
        setUpView();

        // set adapter
        rvPosts.setAdapter(aPosts);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPosts.setItemAnimator(new SlideInUpAnimator());

        aPosts.setOnItemClickListener(new PostsAdapter.OnItemClickListener() {
            @Override
            public void onAwesomeClick(final View itemView, final int position) {
                final Post post = posts.get(position);

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
                final Post post = posts.get(position);
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = post.getDescription();
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.subject_share_post);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share Via"));
            }
        });

        return v;
    }

    private void setSpinners() {
        rvPosts.setVisibility(ListView.INVISIBLE);
        pb = (ProgressBar) v.findViewById(R.id.pbLoading);
        pb.setVisibility(ProgressBar.VISIBLE);
    }

    void clearSpinners() {
        rvPosts.setVisibility(ListView.VISIBLE);
        pb.setVisibility(ProgressBar.GONE);
    }

    void add(List<Post> postList) {
        posts.addAll(postList);
    }

    void add(Post postList) {
        posts.add(0, postList);
    }

    void scrollToPosition() {
//        rvPosts.scrollToPosition(posts.size() - 1);
    }

    void clear() {
        posts.clear();
    }

    void notifiedDataChanged() {
        aPosts.notifyDataSetChanged();
    }

    void noPostsView(String color) {
        CoordinatorLayout rlPostLists = (CoordinatorLayout) v.findViewById(R.id.rlPostLists);
        int intColor = Color.parseColor(String.valueOf(color));
        rlPostLists.setBackgroundColor(intColor);
    }

    void populatePosts() {
        // override this in the other fragments
    }

    private void setUpView() {
        setSpinners();

        // populate data
        populatePosts();
    }

    private void animateOnAwesome(final ImageButton awesomeIcon) {
        Handler handler = new Handler();
        switch (ParseApplication.currentPosition) {
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
        // TODO: it's probably safe to do this before the onAwesome
        tvAwesomeCount.setText(getString(R.string.label_awesome_x, awesomeCount));

        // Send data to parse
        awesome.saveInBackground();
        savedPost.setAwesomeCount(awesomeCount);
        savedPost.saveInBackground();
    }
}
