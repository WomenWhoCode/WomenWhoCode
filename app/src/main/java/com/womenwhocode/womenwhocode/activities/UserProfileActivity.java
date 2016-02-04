package com.womenwhocode.womenwhocode.activities;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.womenwhocode.womenwhocode.R;
import com.womenwhocode.womenwhocode.models.Feature;
import com.womenwhocode.womenwhocode.models.FeatureTag;
import com.womenwhocode.womenwhocode.models.Message;
import com.womenwhocode.womenwhocode.models.Notification;
import com.womenwhocode.womenwhocode.models.PersonalizationDetails;
import com.womenwhocode.womenwhocode.models.Profile;
import com.womenwhocode.womenwhocode.models.Recommendation;
import com.womenwhocode.womenwhocode.models.Subscribe;
import com.womenwhocode.womenwhocode.models.Tag;
import com.womenwhocode.womenwhocode.models.UserNotification;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by pnroy on 10/19/15.
 */
public class UserProfileActivity extends AppCompatActivity {
    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private static final int SELECTED_PICTURE = 1;
    private final String photoFileName = "photo.jpg";
    private final Profile userProfile = new Profile();
    private final PersonalizationDetails pd = new PersonalizationDetails();
    private EditText txtName;
    private EditText txtjobTitle;
    private Spinner spnNetwork;
    private String userAns;
    private String email = "";
    private ImageView ivPic;
    private ParseUser currentUser;
    private boolean animate_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        Bundle extras = getIntent().getExtras();
        final ArrayList<String> networks = new ArrayList<>();
        // TODO: needs a take photo intent for when where is no camera

        currentUser = ParseUser.getCurrentUser();
        if (extras != null) {
            email = extras.getString("Email");
            userAns = extras.getString("userAns");

            savePersonalizationDetails();
            recommendUserToTopic();
        }

        txtName = (EditText) findViewById(R.id.txtName);
        EditText txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtjobTitle = (EditText) findViewById(R.id.etJob);
        spnNetwork = (Spinner) findViewById(R.id.spnNetwork);
        ivPic = (ImageView) findViewById(R.id.ivphoto);
        ivPic.clearAnimation();

        //get the Network data
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Network");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> networkList, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < networkList.size(); i++) {
                        networks.add(networkList.get(i).getString("title"));
                        Collections.sort(networks);
                    }

                    //  Toast.makeText(getApplicationContext(),networkList.get(0).getString("title"),Toast.LENGTH_LONG).show();

                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });

        txtEmail.setText(email);
        networks.add("Select");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, networks);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item); // The drop down view
        spnNetwork.setAdapter(spinnerArrayAdapter);

        spnNetwork.setSelection(0);

        spnNetwork.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        autoSubscribeUserToTopics();
        registerUserNotifications();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!animate_flag) {
            doAlpha();
        }
    }

    private void doAlpha() {
        Animation alphaAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha_animation);
        ivPic.startAnimation(alphaAnimation); //image is my ImageView
    }

    public void OnFinalize(View view) {
        // Save user with the updated input in Profile model
        if (!TextUtils.isEmpty(txtName.getText().toString())) {
            userProfile.setFullName(txtName.getText().toString());
        }
        if (spnNetwork.getSelectedItem().toString().equals("select")) {
            userProfile.setNetwork("");
        } else {
            userProfile.setNetwork(spnNetwork.getSelectedItem().toString());
        }
        if (!TextUtils.isEmpty(txtjobTitle.getText().toString())) {
            userProfile.setJobTitle(txtjobTitle.getText().toString());
        }
        userProfile.setUser(currentUser);
        userProfile.setTheme(0);
        userProfile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                currentUser.put(Message.PROFILE_KEY, userProfile);
                currentUser.saveInBackground();
            }
        });

        startTimelineActivityIntent();
    }

    private void autoSubscribeUserToTopics() {
        // Auto subscribe user to features
        final ArrayList<Feature> features = new ArrayList<>();
        ParseQuery<Feature> featureQuery = ParseQuery.getQuery(Feature.class);
        featureQuery.whereEqualTo(Feature.AUTO_SUBSCRIBE_KEY, true);
        featureQuery.findInBackground(new FindCallback<Feature>() {
            public void done(List<Feature> featureList, ParseException e) {
                if (e == null) {

                    for (int i = 0; i < featureList.size(); i++) {
                        features.add(featureList.get(i));
                    }

                    for (int j = 0; j < features.size(); j++) {

                        Feature feature = features.get(j);

                        final Subscribe subscribe = new Subscribe();
                        subscribe.setSubscribed(true);
                        subscribe.setUser(currentUser);
                        subscribe.setFeature(feature);
                        subscribe.saveInBackground();

                        int subscribeCount = feature.getSubscribeCount() + 1;
                        feature.setSubscribeCount(subscribeCount);
                        feature.saveInBackground();
                    }
                } else {
                    Log.d("AutoSubscribeError", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void recommendUserToTopic() {
        // parse answers
        String[] parsed = userAns.split("\"");

        // query tags for answers
        ParseQuery<Tag> tagParseQuery = ParseQuery.getQuery(Tag.class);
        tagParseQuery.whereContainedIn(Tag.NAME_KEY, Arrays.asList(parsed));
        // get features with those tags
        ParseQuery<FeatureTag> featureTagParseQuery = ParseQuery.getQuery(FeatureTag.class);
        featureTagParseQuery.whereMatchesQuery(FeatureTag.TAG_KEY, tagParseQuery);
        featureTagParseQuery.include("feature");
        featureTagParseQuery.findInBackground(new FindCallback<FeatureTag>() {
            @Override
            public void done(List<FeatureTag> list, ParseException e) {
                if (e == null) {
                    HashSet<Feature> featureSet = new HashSet<>(); // to catch dups!
                    for (FeatureTag featureTag : list) {
                        if (featureSet.add(featureTag.getFeature())) { // 0 and 3
                            // create recommendations
                            Recommendation r = new Recommendation();
                            r.setFeature(featureTag.getFeature());
                            r.setFeatureId(featureTag.getFeature().getObjectId());
                            r.setUserId(currentUser.getObjectId());
                            r.setValid(true);
                            r.saveInBackground();
                        }
                    }
                }
            }
        });
    }

    private void savePersonalizationDetails() {
        pd.setAnswers(userAns);
        pd.setUser(currentUser);
        pd.saveInBackground();
    }

    private void registerUserNotifications() {
        ParseQuery<Notification> notificationParseQuery = ParseQuery.getQuery(Notification.class);
        notificationParseQuery.findInBackground(new FindCallback<Notification>() {
            @Override
            public void done(List<Notification> list, ParseException e) {
                if (e == null && list.size() > 0) {
                    for (Notification n : list) {
                        UserNotification un = new UserNotification();
                        un.setUserId(currentUser.getObjectId());
                        un.setEnabled(true);
                        un.setNotification(n);
                        un.saveInBackground();
                    }
                }
            }
        });
    }

    private void startTimelineActivityIntent() {
        Intent i = new Intent(UserProfileActivity.this, TimelineActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }

    public void onSelectImage(View view) {
        animate_flag = true;
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, SELECTED_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Bitmap finalImg = null;
        try {
            // When an Image is picked
            if (requestCode == SELECTED_PICTURE && resultCode == RESULT_OK
                    && null != data) {

                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);

                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filepath = cursor.getString(columnIndex);
                cursor.close();
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                options.inJustDecodeBounds = true;
                //Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.pic1);
                Bitmap bmap = BitmapFactory
                        .decodeFile(filepath);

                finalImg = Bitmap.createScaledBitmap(bmap, 150, 150, true);

                // Set the Image in ImageView after decoding the String
                // Picasso.with(getContext()).load(photo.proPic).transform(new CircleTransform()).into(viewHolder.ivProPic);
                ivPic.setImageBitmap(finalImg);
            }

            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    Uri takenPhotoUri = getPhotoFileUri(photoFileName);

                    // by this point we have the camera photo on disk
                    //Bitmap takenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
                    Bitmap rotateImg = rotateBitmapOrientation(takenPhotoUri.getPath());

                    finalImg = Bitmap.createScaledBitmap(rotateImg, 150, 150, true);
                    // Load the taken image into a preview
                    ivPic.setImageBitmap(finalImg);
                } else { // Result was a failure
//                    Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                }
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if (finalImg != null) {
                finalImg.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                // get byte array here
                byte[] bytearray = stream.toByteArray();
                ParseFile imgFile = new ParseFile("profileImg.png", bytearray);
                imgFile.saveInBackground();
                userProfile.setPhotoFile(imgFile);
            }
        } catch (Exception e) {
//            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
//                    .show();
        }
    }

    public void onLaunchCamera(View view) {
        animate_flag = true;
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName)); // set the image file name

        // Start the image capture intent to take photo
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    // Returns the Uri for a photo stored on disk given the fileName
    private Uri getPhotoFileUri(String fileName) {
        // Only continue if the SD Card is mounted
        if (isExternalStorageAvailable()) {
            // Get safe storage directory for photos
            String APP_TAG = "MyCustomApp";
            File mediaStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP_TAG);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                Log.d(APP_TAG, "failed to create directory");
            }

            // Return the file target for the photo based on filename
            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
        }
        return null;
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    private Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        //bounds.inSampleSize = 4;
        //bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        // Return result
        return Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
    }


    public void showCameraIcons(View view) {
        view.clearAnimation();
        RelativeLayout rl = (RelativeLayout) view.getParent();

        LinearLayout child = (LinearLayout) rl.getChildAt(2);

        child.findViewById(R.id.btnUpload).setVisibility(View.VISIBLE);
        child.findViewById(R.id.btnCamera).setVisibility(View.VISIBLE);
    }
}
