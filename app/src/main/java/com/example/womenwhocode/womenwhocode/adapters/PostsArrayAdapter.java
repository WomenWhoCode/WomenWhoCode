package com.example.womenwhocode.womenwhocode.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.womenwhocode.womenwhocode.R;
import com.example.womenwhocode.womenwhocode.models.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by shehba.shahab on 10/17/15.
 */
public class PostsArrayAdapter extends ArrayAdapter<Post> {

    public PostsArrayAdapter(Context context, List<Post> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Post post = getItem(position);

        // Check if we are using a recycled view, if not we need to inflate
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_timeline, parent, false);
        }

        // Look up views to populate data
        ImageView ivPostPhoto = (ImageView) convertView.findViewById(R.id.ivPostPhoto);
        TextView tvPostTitle = (TextView) convertView.findViewById(R.id.tvPostTitle);
        TextView tvPostDescription = (TextView) convertView.findViewById(R.id.tvPostDescription);
        TextView tvAwesomeCount = (TextView) convertView.findViewById(R.id.tvAwesomeCount);

        // Clear out the image views
        ivPostPhoto.setImageResource(0);

        // Insert the model data into each of the view items
        String title = post.getTitle();
        String description = post.getDescription();
        int awesomeCount = post.getAwesomeCount();

        tvPostTitle.setText(title);
        tvPostDescription.setText(description);
        tvAwesomeCount.setText(Integer.valueOf(awesomeCount).toString());

        // Insert the image using picasso
        Picasso.with(getContext()).load(post.getImageUrl()).into(ivPostPhoto);

        return convertView;
    }
}