package com.womenwhocode.womenwhocode.adapters;

import com.github.library.bubbleview.BubbleTextVew;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.womenwhocode.womenwhocode.R;
import com.womenwhocode.womenwhocode.models.Message;
import com.womenwhocode.womenwhocode.models.Profile;
import com.womenwhocode.womenwhocode.utils.CircleTransform;
import com.womenwhocode.womenwhocode.utils.Utilities;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zassmin on 11/1/15.
 */
public class ChatListAdapter extends ArrayAdapter<Message> {
    private final String mUserId;

    public ChatListAdapter(Context context, String userId, ArrayList<Message> messages) {
        super(context, 0, messages);
        this.mUserId = userId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.item_chat, parent, false);

            final ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        final Message message = getItem(position);

        // crash on parse user!
        ParseUser user = null;
        try {
            user = message.getParseUser(Message.USER_KEY).fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Profile profile = message.getProfile();
        final ViewHolder holder = (ViewHolder) convertView.getTag();
        final boolean isMe = message.getUserId().equals(mUserId);
        String date = "now";
        if (message.getCreatedAt() != null) {
            date = Utilities.getRelativeTimeAgo(message.getCreatedAt().toString());
        }
        // decide which rl to render
        if (isMe) {
            // make visible one RL rlCurrentUser
            holder.rlCurrentUser.setVisibility(View.VISIBLE);
            holder.rlOtherUser.setVisibility(View.GONE);

            // set views
            holder.ivCurrentUserProfile.setImageResource(R.drawable.default_user_icon);
            holder.tvBody.setText(message.getBody());
            holder.tvCreatedAt.setText(date);
            if (profile != null) {
                if (profile.getPhotoFile() != null && !TextUtils.isEmpty(profile.getPhotoFile().getUrl())) {
                    Picasso.with(getContext())
                            .load(profile.getPhotoFile().getUrl())
                            .transform(new CircleTransform())
                            .resize(40, 40)
                            .centerCrop()
                            .into(holder.ivCurrentUserProfile);
                }
                if (!TextUtils.isEmpty(profile.getFullName())) {
                    holder.tvFullName.setText(profile.getFullName());
                } else {
                    if (user != null) {
                        holder.tvFullName.setText(user.getUsername());
                    }
                }
            }
        } else {
            // make visible the other RL rlOtherUser
            holder.rlOtherUser.setVisibility(View.VISIBLE);
            holder.rlCurrentUser.setVisibility(View.GONE);

            // set views
            holder.ivOtherProfileLeft.setImageResource(R.drawable.default_user_icon);
            holder.tvOtherBody.setText(message.getBody());
            holder.tvOtherCreatedAt.setText(date);
            if (profile != null) {
                if (profile.getPhotoFile() != null && !TextUtils.isEmpty(profile.getPhotoFile().getUrl())) {
                    Picasso.with(getContext())
                            .load(profile.getPhotoFile().getUrl())
                            .transform(new CircleTransform())
                            .resize(40, 40)
                            .centerCrop()
                            .into(holder.ivOtherProfileLeft);
                }
                if (!TextUtils.isEmpty(profile.getFullName())) {
                    holder.tvOtherFullName.setText(profile.getFullName());
                } else {
                    if (user != null) {
                        holder.tvOtherFullName.setText(user.getUsername());
                    }
                }
            }
        }

        return convertView;
    }

    static class ViewHolder {
        // current user message view
        @Bind(R.id.rlCurrentUser)
        public RelativeLayout rlCurrentUser;
        @Bind(R.id.ivCurrentUserProfile)
        public ImageView ivCurrentUserProfile;
        @Bind(R.id.tvFullName)
        public TextView tvFullName;
        @Bind(R.id.tvCreatedAt)
        public TextView tvCreatedAt;
        @Bind(R.id.tvBody)
        public BubbleTextVew tvBody;

        // other users message view
        @Bind(R.id.rlOtherUser)
        public RelativeLayout rlOtherUser;
        @Bind(R.id.ivOtherProfileLeft)
        public ImageView ivOtherProfileLeft;
        @Bind(R.id.tvOtherFullName)
        public TextView tvOtherFullName;
        @Bind(R.id.tvOtherCreatedAt)
        public TextView tvOtherCreatedAt;
        @Bind(R.id.tvOtherBody)
        public BubbleTextVew tvOtherBody;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}