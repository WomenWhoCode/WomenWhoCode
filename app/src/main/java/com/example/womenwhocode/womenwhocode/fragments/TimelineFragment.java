package com.example.womenwhocode.womenwhocode.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.womenwhocode.womenwhocode.R;
import com.example.womenwhocode.womenwhocode.adapters.PostsArrayAdapter;
import com.example.womenwhocode.womenwhocode.models.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shehba.shahab on 10/16/15.
 */
public class TimelineFragment extends Fragment {
    PostsArrayAdapter aPosts;
    ArrayList<Post> posts;
    ListView lvPosts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        posts = new ArrayList<>();
        aPosts = new PostsArrayAdapter(getActivity(), posts);
        populatePostsList();
    }

    // Inflate the fragment layout we defined above for this fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        lvPosts = (ListView) view.findViewById(R.id.lvPosts);
        lvPosts.setAdapter(aPosts);
        return view;
    }

    void addAll(List<Post> posts) {
        aPosts.addAll(posts);
    }

    void populatePostsList() {
        aPosts.clear();
    }
}