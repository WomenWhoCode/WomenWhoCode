package com.womenwhocode.womenwhocode.fragments;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.womenwhocode.womenwhocode.R;
import com.womenwhocode.womenwhocode.adapters.EventsAdapter;
import com.womenwhocode.womenwhocode.models.Event;
import com.womenwhocode.womenwhocode.models.Network;
import com.womenwhocode.womenwhocode.models.Profile;
import com.womenwhocode.womenwhocode.utils.LocalDataStore;
import com.womenwhocode.womenwhocode.utils.NetworkConnectivityReceiver;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shehba.shahab on 10/16/15.
 */
public class EventsFragment extends Fragment {
    private static final int MILE_RANGE = 25;
    private EventsAdapter aEvents;
    private RecyclerView rvEvents;
    private OnEventItemClickListener listener;
    private ProgressBar pb;
    private ParseQuery<Event> query;
    private ParseQuery<Network> networkParseQuery;
    private ArrayList<Object> items;
    private TextView noEvents;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        items = new ArrayList<>();
        aEvents = new EventsAdapter(items);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        rvEvents = (RecyclerView) view.findViewById(R.id.lvEvents);
        noEvents = (TextView) view.findViewById(R.id.noEventFoundText);
        // hide the recycler view until data is loaded

        // load progress bar
        pb = (ProgressBar) view.findViewById(R.id.pbLoading);
        pb.setVisibility(ProgressBar.VISIBLE);

        rvEvents.setAdapter(aEvents);
        rvEvents.setLayoutManager(new LinearLayoutManager(getContext()));

        populateEvents(); // FIXME: look into making this call happen on a daily basis and/or when there is a change in the user's location

        aEvents.setOnItemClickListener(new EventsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Event event = (Event) items.get(position);
                listener.onEventClickListener(event, itemView);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        if (activity instanceof OnEventItemClickListener) {
            listener = (OnEventItemClickListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement EventsFragment.OnEventItemClickListener");
        }
    }

    private void populateEvents() {
        // load queries
        ParseQuery<Profile> profileParseQuery = ParseQuery.getQuery(Profile.class);
        query = ParseQuery.getQuery(Event.class);
        networkParseQuery = ParseQuery.getQuery(Network.class);

        if (!NetworkConnectivityReceiver.isNetworkAvailable(getContext())) {
            query.fromPin(LocalDataStore.EVENT_PIN);
            networkParseQuery.fromPin(LocalDataStore.LOCAL_NETWORK_PIN); // what if location has changed?
            profileParseQuery.fromPin(LocalDataStore.PROFILE_PIN);
        }

        // TODO: reduce # of network calls for profile - maybe store the location somewhere
        // TODO: add a no events view when there are no events to show
        // TODO: seed at least 2 global events

        profileParseQuery.whereEqualTo(Profile.USER_KEY, ParseUser.getCurrentUser());
        profileParseQuery.getFirstInBackground(new GetCallback<Profile>() {

            @Override
            public void done(Profile profile, ParseException e) {
                if (profile != null) {
                    if (profile.getLocation() == null) {
                        pb.setVisibility(ProgressBar.GONE);
                        noEvents.setVisibility(View.VISIBLE);
                        return;
                    }
                    networkParseQuery.whereWithinMiles(Network.LOCATION_KEY, profile.getLocation(), MILE_RANGE);
                    query.whereMatchesQuery(Event.NETWORK_KEY, networkParseQuery);
                    query.orderByAscending(Event.DATETIME_KEY);
                    query.findInBackground(new FindCallback<Event>() {
                        public void done(List<Event> eventList, ParseException e) {
                            if (eventList != null && e == null) {
                                items.clear();
                                items.add(0, "Upcoming events near you:");
                                items.addAll(eventList);
                                aEvents.notifyDataSetChanged();
                                // hide progress bar, make list view appear
                                pb.setVisibility(ProgressBar.GONE);
                                rvEvents.setVisibility(RecyclerView.VISIBLE);
                                LocalDataStore.unpinAndRepin(eventList, LocalDataStore.EVENT_PIN);
                            } else if (e != null) {
                                Log.d("PARSE_EVENTS_FAIL", "Error: " + e.getMessage());
                            } else {
                                pb.setVisibility(ProgressBar.GONE);
                                noEvents.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        });

        // TODO: Upcoming global events
    }

    public interface OnEventItemClickListener {
        void onEventClickListener(Event event, View itemView);
    }
}