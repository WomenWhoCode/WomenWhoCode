package com.womenwhocode.womenwhocode.adapters;

import com.womenwhocode.womenwhocode.R;
import com.womenwhocode.womenwhocode.models.Event;
import com.womenwhocode.womenwhocode.utils.Utilities;
import com.womenwhocode.womenwhocode.viewholders.ViewHolderTopicHeader;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zassmin on 10/18/15.
 */
public class EventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static OnItemClickListener listener;
    private final List<Object> items;
    private final int EVENT = 0, EVENT_HEADER = 1;

    public EventsAdapter(List<Object> items) {
        this.items = items;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        EventsAdapter.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case EVENT:
                View v1 = inflater.inflate(R.layout.item_event, parent, false);
                viewHolder = new ViewHolder(v1);
                break;
            case EVENT_HEADER:
                View v2 = inflater.inflate(R.layout.item_topic_header, parent, false);
                viewHolder = new ViewHolderTopicHeader(v2);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case EVENT:
                ViewHolder vh1 = (ViewHolder) holder;
                configureViewHolder(vh1, position);
                break;
            case EVENT_HEADER:
                ViewHolderTopicHeader vh2 = (ViewHolderTopicHeader) holder;
                configureViewHolderTopicHeader(vh2, position);
                break;
        }
    }

    private void configureViewHolderTopicHeader(ViewHolderTopicHeader holder, int position) {
        TextView tvTopicHeader = holder.tvTopicHeader;
        String header = (String) items.get(position);
        tvTopicHeader.setText(header);
    }

    private void configureViewHolder(ViewHolder holder, int position) {
        Event event = (Event) items.get(position);

        TextView tvEventNetwork = holder.tvEventNetwork;
        TextView tvEventTitle = holder.tvEventTopicTitle;
        TextView tvEventLocation = holder.tvEventLocation;
        TextView tvEventTime = holder.tvEventTime;
        TextView tvEventDate = holder.tvEventDate;

        Context context = tvEventNetwork.getContext(); // get context from parent

        if (event.getNetwork() != null) {
            tvEventNetwork.setText(context.getString(R.string.network_title, event.getNetwork().getTitle()));
        }

        long time = Utilities.setLocalDateTime(event.getEventDateTime());
        String date = Utilities.dateTimeParser(time, Utilities.DATE_FORMAT);
        String prettyTime = Utilities.dateTimeParser(time, Utilities.TIME_FORMAT);

        tvEventTitle.setText(event.getTitle());
        tvEventLocation.setText(context.getString(R.string.event_location, event.getLocation()));
        tvEventTime.setText(prettyTime);
        tvEventDate.setText(date);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Event) {
            return EVENT;
        } else if (items.get(position) instanceof String) {
            return EVENT_HEADER;
        }
        return -1;
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tvEventTopicTitle)
        public TextView tvEventTopicTitle;
        @Bind(R.id.tvEventLocation)
        public TextView tvEventLocation;
        @Bind(R.id.tvEventTime)
        public TextView tvEventTime;
        @Bind(R.id.tvEventDate)
        public TextView tvEventDate;
        @Bind(R.id.tvEventNetwork)
        public TextView tvEventNetwork;

        public ViewHolder(final View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(itemView, getLayoutPosition());
                    }
                }
            });
        }
    }
}