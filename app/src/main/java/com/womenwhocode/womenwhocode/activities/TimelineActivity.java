package com.womenwhocode.womenwhocode.activities;

import com.google.android.gms.maps.model.LatLng;

import com.astuetz.PagerSlidingTabStrip;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.womenwhocode.womenwhocode.R;
import com.womenwhocode.womenwhocode.WomenWhoCodeApplication;
import com.womenwhocode.womenwhocode.fragments.EventsFragment;
import com.womenwhocode.womenwhocode.fragments.FeaturesFragment;
import com.womenwhocode.womenwhocode.fragments.RecommendFeatureDialog.RecommendFeatureDialogListener;
import com.womenwhocode.womenwhocode.fragments.TimelineFragment;
import com.womenwhocode.womenwhocode.models.Event;
import com.womenwhocode.womenwhocode.models.Feature;
import com.womenwhocode.womenwhocode.models.Profile;
import com.womenwhocode.womenwhocode.utils.LocalDataStore;
import com.womenwhocode.womenwhocode.utils.LocationProvider;
import com.womenwhocode.womenwhocode.utils.ThemeUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TimelineActivity extends AppCompatActivity implements
        EventsFragment.OnEventItemClickListener,
        FeaturesFragment.OnFeatureItemClickListener,
        LocationProvider.LocationCallback,
        RecommendFeatureDialogListener,
        TimelineFragment.OnItemClickListener {

    public final static String SELECTED_TAB_EXTRA_KEY = "selectedTabIndex";
    private final static int TIMELINE_TAB = 0;
    private final static int TOPICS_TAB = 1;
    private final static int EVENTS_TAB = 2;
    private Profile profile;
    private LocationProvider mLocationProvider;
    private ViewPager vpPager;
    private View parentLayout;
    private ParseQuery<Profile> parseQuery;
    private DrawerLayout mDrawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // MUST BE SET BEFORE setContentView
        ThemeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_timeline);
        parentLayout = findViewById(R.id.timeline_activity_view);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        parseQuery = ParseQuery.getQuery(Profile.class);

        // set tool bar to replace actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // hide the action bar title to only so toolbar title

        //for drawer view
        ParseUser currentUser = ParseUser.getCurrentUser();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Find our drawer view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);
        // Inflate the header view at runtime
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header);

        // Inflate the theme switch menu
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.theme_switch);
        View actionView = MenuItemCompat.getActionView(menuItem);
        onThemeSwitch(actionView); // listener

        // We can now look up items within the header if needed
        final ImageView ivHeaderPhoto = (ImageView) headerLayout.findViewById(R.id.ivPhoto);
        final TextView tvFullName = (TextView) headerLayout.findViewById(R.id.tvFullName);
        final TextView tvJobTitle = (TextView) headerLayout.findViewById(R.id.tvJobTitle);

        parseQuery.whereEqualTo(Profile.USER_KEY, currentUser);
        parseQuery.getFirstInBackground(new GetCallback<Profile>() {
            @Override
            public void done(Profile p, ParseException e) {
                if (e == null) {
                    if (p != null) {
                        profile = p;
                        if (p.getPhotoFile() != null) {
                            Picasso.with(getApplicationContext())
                                    .load(p.getPhotoFile().getUrl())
                                    .resize(48, 48)
                                    .centerCrop()
                                    .into(ivHeaderPhoto);
                        }
                        tvFullName.setText(p.getFullName());
                        tvJobTitle.setText(p.getJobTitle());
                    }
                }
            }
        });

        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.app_name);

        // set up location
        mLocationProvider = new LocationProvider(this, this);

        // Get the viewpager
        vpPager = (ViewPager) findViewById(R.id.viewpager);

        // Set the viewpager adapter for the pager
        vpPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));

        // Find the sliding tabstrip
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Regular.ttf"), Typeface.NORMAL);

        // Attach the tabstrip to the viewpager
        tabStrip.setViewPager(vpPager);

        setSelectedTab();
    }

    private void setSelectedTab() {
        // Fetch the selected tab index with default
        int selectedTabIndex = getIntent().getIntExtra(SELECTED_TAB_EXTRA_KEY, TIMELINE_TAB);
        // Switch to page based on index
        vpPager.setCurrentItem(selectedTabIndex);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationProvider.connectClient();
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationProvider.disconnect();
        super.onStop();
    }

    /*
    * Handle results returned to the FragmentActivity by Google Play services
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {

            case LocationProvider.CONNECTION_FAILURE_RESOLUTION_REQUEST:
            /*
             * If the result code is Activity.RESULT_OK, try to connect again
			 */
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        mLocationProvider.connectClient();
                        break;
                }
        }
    }

    // TODO: add shared transitions from timeline event list to event detail
    // TODO: add shared transitions from timeline feature list to event detail
    @Override
    public void onEventClickListener(Event event, View itemView) {
        Intent i = new Intent(this, EventDetailsActivity.class);
        i.putExtra("event_id", event.getObjectId());
        i.putExtra(SELECTED_TAB_EXTRA_KEY, EVENTS_TAB);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TextView tvEventTitle = (TextView) itemView.findViewById(R.id.tvEventTopicTitle);
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(TimelineActivity.this, tvEventTitle, "eventTopicTitle");
            startActivity(i, options.toBundle());
        } else {
            startActivity(i);
            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
        }
    }

    @Override
    public void onFeatureClickListener(Feature feature, View itemView) {
        Intent i = new Intent(TimelineActivity.this, FeatureDetailsActivity.class);
        i.putExtra("feature_id", feature.getObjectId());
        i.putExtra(SELECTED_TAB_EXTRA_KEY, TOPICS_TAB);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ImageView ivFeatureImage = (ImageView) itemView.findViewById(R.id.ivEventTopicPhoto);
            TextView tvFeatureTitle = (TextView) itemView.findViewById(R.id.tvEventTopicTitle);
            Pair<View, String> p1 = Pair.create((View) ivFeatureImage, "eventTopicPhoto");
            Pair<View, String> p2 = Pair.create((View) tvFeatureTitle, "eventTopicTitle");
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(TimelineActivity.this, p1, p2);
            startActivity(i, options.toBundle());
        } else {
            startActivity(i);
            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out); // TODO: transition with background color
        }

    }

    @Override
    public void handleNewLocation(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        updateUserProfile(latLng);
    }

    private void updateUserProfile(final LatLng latLng) {
        // FIXME: only update if the location has changed.
        // FIXME: clean up dup profile request!
        parseQuery.whereEqualTo(Profile.USER_KEY, ParseUser.getCurrentUser());
        parseQuery.getFirstInBackground(new GetCallback<Profile>() {
            @Override
            public void done(Profile profile, ParseException e) {
                if (e == null && profile != null) {
                    // build object
                    ParseGeoPoint pLocation = new ParseGeoPoint();
                    pLocation.setLatitude(latLng.latitude);
                    pLocation.setLongitude(latLng.longitude);
                    profile.setLocation(pLocation);
                    // save to parse and pin locally for offline mode
                    final ArrayList<Profile> profiles = new ArrayList<>();
                    profiles.add(profile);
                    profile.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("LOCATION_SAVED", "on profile");
                                LocalDataStore.unpinAndRepin(profiles, LocalDataStore.PROFILE_PIN);
                            } else {
                                Log.d("LOCATION_NOT_SAVED", e.toString());
                            }
                        }
                    });
                } else if (profile == null) {
//                    Toast.makeText(getBaseContext(), "profile null", Toast.LENGTH_LONG).show();
                    Log.d("PROFILE_NULL", "");
                } else {
                    Log.d("PROFILE_ERROR", e.toString());
                }
            }
        });
    }

    @Override
    public void onFinishEditDialog(String inputText) {
        if (!TextUtils.isEmpty(inputText)) {
            Snackbar.make(
                    parentLayout, R.string.msg_review, Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    public void logout(MenuItem item) {
        ParseUser.logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onFeatureTimelineClickListener(Feature feature, View itemView) {
        Intent i = new Intent(this, FeatureDetailsActivity.class);
        i.putExtra("feature_id", feature.getObjectId());
        i.putExtra(SELECTED_TAB_EXTRA_KEY, TimelineActivity.TIMELINE_TAB);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ImageView ivFeatureImage = (ImageView) itemView.findViewById(R.id.ivEventTopicPhoto);
            TextView tvFeatureTitle = (TextView) itemView.findViewById(R.id.tvEventTopicTitle);
            Pair<View, String> p1 = Pair.create((View) ivFeatureImage, "eventTopicPhoto");
            Pair<View, String> p2 = Pair.create((View) tvFeatureTitle, "eventTopicTitle");
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(TimelineActivity.this, p1, p2);
            startActivity(i, options.toBundle());
        } else {
            startActivity(i);
            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out); // TODO: transition with background color
        }

    }

    @Override
    public void onEventTimelineClickListener(Event event, View itemView) {
        Intent i = new Intent(this, EventDetailsActivity.class);
        i.putExtra("event_id", event.getObjectId());
        i.putExtra(SELECTED_TAB_EXTRA_KEY, TimelineActivity.TIMELINE_TAB);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ImageView ivEventPhoto = (ImageView) itemView.findViewById(R.id.ivEventTopicPhoto);
            TextView tvEventTitle = (TextView) itemView.findViewById(R.id.tvEventTopicTitle);
            Pair<View, String> p1 = Pair.create((View) ivEventPhoto, "eventTopicPhoto");
            Pair<View, String> p2 = Pair.create((View) tvEventTitle, "eventTopicTitle");
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(TimelineActivity.this, p1, p2);
            startActivity(i, options.toBundle());
        } else {
            startActivity(i);
            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
        }
    }

    private void onThemeSwitch(View actionView) {
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profile == null) {
                    return;
                }

                switch (profile.getTheme()) {
                    case ThemeUtils.THEME_WWCODE_LIGHT:
                        switchThemeForUser(ThemeUtils.THEME_WWCODE_DARK);
                        break;
                    case ThemeUtils.THEME_WWCODE_DARK:
                        switchThemeForUser(ThemeUtils.THEME_WWCODE_LIGHT);
                        break;
                }
            }
        });
    }

    private void switchThemeForUser(int theme) {
        ThemeUtils.changeToTheme(this, theme);
        WomenWhoCodeApplication.currentPosition = theme;
        profile.setTheme(theme);
        profile.saveInBackground();
    }

    public void launchMailClient(MenuItem item) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "contact@womenwhocode.com", null));
        startActivity(Intent.createChooser(emailIntent, null));
    }

    // Return the order of the fragments in the view pager
    public class PagerAdapter extends FragmentPagerAdapter {
        private final String[] tabTitles = {(String) getResources().getText(R.string.title_fragment_timeline), (String) getResources().getText(R.string.title_fragment_topics), (String) getResources().getText(R.string.title_fragment_events)};

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // The order and creation fo fragments within the pager
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new TimelineFragment();
            } else if (position == 1) {
                return new FeaturesFragment();
            } else if (position == 2) {
                return new EventsFragment();
            } else return null;
        }

        // Return the tab title
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        // How many fragments there are to swipe between
        @Override
        public int getCount() {
            return tabTitles.length;
        }


    }
}
