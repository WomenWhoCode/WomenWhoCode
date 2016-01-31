package com.womenwhocode.womenwhocode.adapters;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.womenwhocode.womenwhocode.R;
import com.womenwhocode.womenwhocode.models.Awesome;
import com.womenwhocode.womenwhocode.models.Message;
import com.womenwhocode.womenwhocode.models.Post;
import com.womenwhocode.womenwhocode.models.Profile;
import com.womenwhocode.womenwhocode.utils.CircleTransform;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zassmin on 10/26/15.
 */
public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    private static OnItemClickListener listener;
    private final List<Post> mPosts;

    public PostsAdapter(List<Post> posts) {
        this.mPosts = posts;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        PostsAdapter.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.item_post, parent, false);
        return new ViewHolder(postView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Post post = mPosts.get(position);
        ParseUser currentUser = ParseUser.getCurrentUser();

        // set up vars
        TextView tvPostNameBy = holder.tvPostNameBy;
        ImageView ivUserPhoto = holder.ivUserPhoto;
        ImageView ivPostPic = holder.ivPostPic;

        TextView tvPostDescription = holder.tvPostDescription;
        TextView tvRelativeTime = holder.tvRelativeTime;
        TextView tvAwesomeCount = holder.tvAwesomeCount;
        final ImageButton btnAwesomeIcon = holder.btnAwesomeIcon;

        // to resolve bug where icon changes on many items
        btnAwesomeIcon.setImageDrawable(null);
        btnAwesomeIcon.setImageResource(R.drawable.awesome);

        // user details
        ivUserPhoto.setImageDrawable(null);
        Context context = tvPostNameBy.getContext();
        ParseUser postUser = post.getUser();
        Profile profile = getUserProfile(postUser);
        if (postUser != null) {
            String username = postUser.getUsername();
            String imageUrl = post.getFeatureImageUrl();
            if (profile != null) {
                if (!TextUtils.isEmpty(profile.getFullName())) {
                    username = profile.getFullName();
                }
                if (profile.getPhotoFile() != null) {
                    imageUrl = profile.getPhotoFile().getUrl();
                }
            }

            tvPostNameBy.setText(username);
            Picasso.with(context)
                    .load(imageUrl)
                    .transform(new CircleTransform())
                    .resize(40, 40)
                    .centerCrop()
                    .into(ivUserPhoto);
        } else {
            tvPostNameBy.setText(R.string.org_name_short);
            ivUserPhoto.setImageResource(R.mipmap.ic_launcher);
        }

        // Changes color of 'awesomeddd_light' posts
        ParseQuery<Awesome> awesomeParseQuery = ParseQuery.getQuery(Awesome.class);
        awesomeParseQuery.whereEqualTo(Awesome.POST_KEY, post);
        awesomeParseQuery.whereEqualTo(Awesome.USER_KEY, currentUser);
        awesomeParseQuery.getFirstInBackground(new GetCallback<Awesome>() {
            @Override
            public void done(Awesome a, ParseException e) {
                if (e == null) {
                    if (a.getAwesomed()) {
                        holder.btnAwesomeIcon.setImageResource(R.drawable.awesomeddd);
                    }
                } else {
                    Log.d("POST AWESOME ERROR", "Error getting post awesome status.");
                }
            }
        });
        if (post.getDescription() != null) {
            tvPostDescription.setText(post.getDescription());
        }
        // don't display the image view if there are no images
        ivPostPic.setImageDrawable(null); // for memory leak issues
        ivPostPic.setVisibility(ImageView.GONE);
        Context pf = ivPostPic.getContext();
        if (post.getPostPicFile() != null) {
            Picasso.with(pf)
                    .load(post.getPostPicFile().getUrl())
                    .into(ivPostPic);
            ivPostPic.setVisibility(ImageView.VISIBLE);
        }
        tvRelativeTime.setText(post.getPostDateTime());

        int awesomeCount = post.getAwesomeCount();
        tvAwesomeCount.setText(context.getResources().getString(R.string.label_awesome_x, awesomeCount));
    }

    private Profile getUserProfile(ParseUser user) {
        Profile profile = null;
        try {
            profile = user.getParseObject(Message.PROFILE_KEY).fetchIfNeeded();
        } catch (ParseException | NullPointerException e1) {
            e1.printStackTrace();
        }
        return profile;
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public interface OnItemClickListener {
        void onAwesomeClick(View itemView, int position);

        void onShareButtonClick(View itemView, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ivUserPhoto)
        public ImageView ivUserPhoto;
        @Bind(R.id.postPic)
        public ImageView ivPostPic;
        @Bind(R.id.tvPostNameBy)
        public TextView tvPostNameBy;
        @Bind(R.id.tvPostDescription)
        public TextView tvPostDescription;
        @Bind(R.id.tvAwesomeCount)
        public TextView tvAwesomeCount;
        @Bind(R.id.btnAwesomeIcon)
        public ImageButton btnAwesomeIcon;
        @Bind(R.id.tvRelativeTime)
        public TextView tvRelativeTime;
        @Bind(R.id.btnShare)
        public ImageButton btnShare;

        public ViewHolder(final View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            btnAwesomeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onAwesomeClick(itemView, getLayoutPosition());
                    }
                }
            });

            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onShareButtonClick(itemView, getLayoutPosition());
                    }
                }
            });
        }
    }
}