package com.womenwhocode.womenwhocode.adapters;

import com.bumptech.glide.Glide;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.womenwhocode.womenwhocode.R;
import com.womenwhocode.womenwhocode.models.Awesome;
import com.womenwhocode.womenwhocode.models.Event;
import com.womenwhocode.womenwhocode.models.Feature;
import com.womenwhocode.womenwhocode.models.Message;
import com.womenwhocode.womenwhocode.models.Post;
import com.womenwhocode.womenwhocode.models.Profile;
import com.womenwhocode.womenwhocode.models.UserNotification;
import com.womenwhocode.womenwhocode.utils.CircleTransform;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by shehba.shahab on 10/17/15.
 */
public class TimelineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static OnItemClickListener listener;
    private final List<Object> items;
    private final int POST = 0, NOTIFICATION = 1;
    // close listener - set enabled to false and save in background!
    // add additional new holder
    // try to get image working with glide since it handles gifs!

    public TimelineAdapter(List<Object> items) {
        this.items = items;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        TimelineAdapter.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case POST:
                View viewHolderPost = inflater.inflate(R.layout.item_timeline, parent, false);
                viewHolder = new ViewHolderPost(viewHolderPost);
                break;
            case NOTIFICATION:
                View viewHolderNotification = inflater.inflate(R.layout.item_notification, parent, false);
                viewHolder = new ViewHolderNotification(viewHolderNotification);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case POST:
                ViewHolderPost vh1 = (ViewHolderPost) holder;
                configureViewHolderPost(vh1, position);
                break;
            case NOTIFICATION:
                ViewHolderNotification vh2 = (ViewHolderNotification) holder;
                configureViewHolderNotification(vh2, position);
                break;
        }

    }

    private void configureViewHolderNotification(ViewHolderNotification holder, int position) {
        UserNotification un = (UserNotification) items.get(position);

        ImageView ivNotificationImage = holder.ivNotificationImage;
        TextView tvNotificationMessage = holder.tvNotificationMessage;

        Context context = ivNotificationImage.getContext();
        Glide.with(context)
                .load(un.getNotification().getPhotoFile().getUrl())
                .asGif()
                .into(ivNotificationImage);

        tvNotificationMessage.setText(un.getNotification().getMessage());
    }

    private void configureViewHolderPost(ViewHolderPost holder, int position) {
        Post post = (Post) items.get(position);
        ParseUser currentUser = ParseUser.getCurrentUser();

        ProgressBar pb = holder.pb;
        CardView cvPostFeature = holder.cvPostFeature;
        ImageView ivPostPhoto = holder.ivEventTopicPhoto;
        RelativeLayout rlPostFeature = holder.rlPostFeature;
        TextView tvPostNameBy = holder.tvPostNameBy;
        TextView tvPostDescription = holder.tvPostDescription;
        TextView tvRelativeDate = holder.tvRelativeDate;
        TextView tvFeatureTitle = holder.tvEventTopicTitle;
        TextView tvAwesomeCount = holder.tvAwesomeCount;
        ImageView ivPostPic = holder.postPic;
        final ImageButton btnAwesomeIcon = holder.btnAwesomeIcon;

        // Set the progress bar
        pb.setVisibility(ProgressBar.VISIBLE);
        // Hide relative layout so the progress bar is the center of attention
        cvPostFeature.setVisibility(CardView.INVISIBLE);

        // to resolve bug where icon changes on many items
        btnAwesomeIcon.setImageDrawable(null);
        btnAwesomeIcon.setImageResource(R.drawable.awesome);

        // Set up feature/event specific attrs
        ivPostPhoto.setImageDrawable(null);
        Context context = ivPostPhoto.getContext();
        String title = "WWCode";
        Feature feature = post.getFeature();
        Event event = post.getEvent();
        if (feature != null) {
            String imageUrl = feature.getImageUrl();
            if (feature.getPhotoFile() != null) {
                imageUrl = feature.getPhotoFile().getUrl();
            }
            title = feature.getTitle();
            String hexColor = feature.getHexColor();

            // set feature background color
            // set color!
            int color = Color.parseColor(String.valueOf(hexColor));
            rlPostFeature.setBackgroundColor(color); // default color is set in xml

            // Insert the image using picasso
            Picasso.with(context)
                    .load(imageUrl)
                    .resize(40, 40)
                    .centerCrop()
                    .transform(new CircleTransform())
                    .into(ivPostPhoto);

        } else if (event != null) {
            title = event.getTitle();

            // insert icon
            ivPostPhoto.setImageResource(R.drawable.ic_calendar_check); // since we know at this point they are following
        }

        // in case a post has a user
        ParseUser postUser = post.getUser();
        Profile profile = getUserProfile(postUser);
        tvPostNameBy.setText(R.string.org_name_short); // always have a default
        if (postUser != null) {
            String username = postUser.getUsername();
            if (profile != null && !TextUtils.isEmpty(profile.getFullName())) {
                username = profile.getFullName();
            }
            tvPostNameBy.setText(username);
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
                        btnAwesomeIcon.setImageResource(R.drawable.awesomeddd);
                    }
                } else {
                    Log.d("POST AWESOME ERROR", "Error getting post awesome status.");
                }
            }
        });

        // Insert the model data into each of the view items
        String description = post.getDescription();
        String relativeDate = post.getPostDateTime();
        int awesomeCount = post.getAwesomeCount();

        tvPostDescription.setText(description);

        // don't display the image view if there are no images
        ivPostPic.setImageDrawable(null); // for memory leak issues
        ivPostPic.setVisibility(ImageView.GONE);
        Context postPhotoContext = ivPostPic.getContext();
        if (post.getPostPicFile() != null) {
            Picasso.with(postPhotoContext)
                    .load(post.getPostPicFile().getUrl())
                    .into(ivPostPic);
            ivPostPic.setVisibility(ImageView.VISIBLE);
        }

        tvRelativeDate.setText(relativeDate);
        tvFeatureTitle.setText(title);

        tvAwesomeCount.setText(context.getResources().getString(R.string.label_awesome_x, awesomeCount));

        // Hide the progress bar, show the main view
        pb.setVisibility(ProgressBar.GONE);
        cvPostFeature.setVisibility(CardView.VISIBLE);
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
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Post) {
            return POST;
        } else if (items.get(position) instanceof UserNotification) {
            return NOTIFICATION;
        }
        return -1;
    }

    public interface OnItemClickListener {
        void onPostFeatureClick(View itemView, int position);

        void onAwesomeClick(View itemView, int position);

        void onClose(View itemView, int position);

        void onShareButtonClick(View itemView, int position);
    }

    public static class ViewHolderPost extends RecyclerView.ViewHolder {
        @Bind(R.id.ivEventTopicPhoto)
        public ImageView ivEventTopicPhoto;
        @Bind(R.id.tvPostDescription)
        public TextView tvPostDescription;
        @Bind(R.id.tvAwesomeCount)
        public TextView tvAwesomeCount;
        @Bind(R.id.btnAwesomeIcon)
        public ImageButton btnAwesomeIcon;
        @Bind(R.id.cvPostFeature)
        public CardView cvPostFeature;
        @Bind(R.id.tvRelativeDate)
        public TextView tvRelativeDate;
        @Bind(R.id.tvEventTopicTitle)
        public TextView tvEventTopicTitle;
        @Bind(R.id.pbLoading)
        public ProgressBar pb;
        @Bind(R.id.rlPostFeature)
        public RelativeLayout rlPostFeature;
        @Bind(R.id.tvPostNameBy)
        public TextView tvPostNameBy;
        @Bind(R.id.btnShare)
        public ImageButton btnShare;
        @Bind(R.id.postPic)
        public ImageView postPic;

        public ViewHolderPost(final View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            // FIXME: this may not work and I may need to reference: http://stackoverflow.com/questions/24885223/why-doesnt-recyclerview-have-onitemclicklistener-and-how-recyclerview-is-dif/24933117#24933117
            rlPostFeature.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onPostFeatureClick(itemView, getLayoutPosition());
                    }
                }
            });

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

    public static class ViewHolderNotification extends RecyclerView.ViewHolder {
        @Bind(R.id.ivNotificationImage)
        public ImageView ivNotificationImage;
        @Bind(R.id.ibNotificationClose)
        public ImageButton ibNotificationClose;
        @Bind(R.id.tvNotificationMessage)
        public TextView tvNotificationMessage;

        public ViewHolderNotification(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            // set close listener here
            ibNotificationClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClose(itemView, getLayoutPosition());
                    }
                }
            });
        }
    }
}