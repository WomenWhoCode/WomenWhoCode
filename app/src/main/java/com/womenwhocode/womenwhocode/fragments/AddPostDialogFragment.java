package com.womenwhocode.womenwhocode.fragments;

import com.womenwhocode.womenwhocode.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

/**
 * Created by zassmin on 11/12/15.
 */
public class AddPostDialogFragment extends DialogFragment {
    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private static final int SELECTED_PICTURE = 1;
    private final String photoFileName = "photo.jpg";
    private ImageView ivPreview;
    private Bitmap finalImg = null;
    private EditText mEditText;
    private Toolbar toolbar;

    public AddPostDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static AddPostDialogFragment newInstance() {
        return new AddPostDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_post, container);
        toolbar = (Toolbar) view.findViewById(R.id.tbAddPost);
        toolbar.inflateMenu(R.menu.menu_add_post_fragment);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get views
        mEditText = (EditText) view.findViewById(R.id.etAddPost);
        // Show soft keyboard automatically and request focus to field
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        ImageButton btnCamera = (ImageButton) view.findViewById(R.id.btnCamera);
        ImageButton btnFileUpload = (ImageButton) view.findViewById(R.id.btnpicUpload);
        ivPreview = (ImageView) view.findViewById(R.id.showImage);
        btnCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName)); // set the image file name

                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

            }
        });

        btnFileUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, SELECTED_PICTURE);
            }
        });
        MenuItem save = toolbar.getMenu().getItem(0); // position of id save
        save.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.save) {
                    onSave();
                    return true;
                }
                return false;
            }
        });
    }

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

//    @Override
//    public void onResume() {
//        // Get existing layout params for the window
//        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
//        // Assign window properties to fill the parent
//        params.width = WindowManager.LayoutParams.MATCH_PARENT;
//        params.height = WindowManager.LayoutParams.MATCH_PARENT;
//        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
//        // Call super onResume after sizing
//        super.onResume();
//    }

    private void onSave() {
        String postBody = mEditText.getText().toString();

        if (TextUtils.isEmpty(postBody)) {
            return;
        }

        OnSubmitPostListener listener = (OnSubmitPostListener) getActivity();
        listener.onSubmitPostListener(postBody, finalImg);

        dismiss();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECTED_PICTURE && resultCode == Activity.RESULT_OK
                && null != data) {

            // Get the Image from data
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            // Get the cursor
            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
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

            finalImg = BitmapFactory
                    .decodeFile(filepath);
            ivPreview.setImageBitmap(finalImg);
        }
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri takenPhotoUri = getPhotoFileUri(photoFileName);

                // by this point we have the camera photo on disk
                //Bitmap takenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());

                finalImg = rotateBitmapOrientation(takenPhotoUri.getPath());
                // Load the taken image into a preview

                ivPreview.setImageBitmap(finalImg);
            } else { // Result was a failure
//                    Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
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

    public interface OnSubmitPostListener {
        void onSubmitPostListener(String inputText, Bitmap pic);
    }

}
