package com.cogwerks.potato;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.File;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private EditText mPotatoSearchText;
    private ImageView mUserPic;
    private ImageButton mChooseGallery;
    private ImageButton mChooseCamera;
    private String mImageFileName = "imageFile";
    private int PICK_IMAGE_REQUEST = 1;
    private int CAMERA_RES_REQUEST = 2;

    private static final String IMAGE_TYPE = "image/*";
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView mLoadingErrorMessageTV;

    private Uri mPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Startup");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPotatoSearchText = (EditText) findViewById(R.id.et_search_box);
        mLoadingErrorMessageTV = findViewById(R.id.tv_loading_error_message);

        Button tellmeButton = (Button) findViewById(R.id.btn_tell_me);
        //Called when user hits tell me button
        tellmeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = mPotatoSearchText.getText().toString();
                if (!TextUtils.isEmpty(searchText)) {
                    Intent intent = new Intent(getApplicationContext(), ResultDetailActivity.class);
                    intent.putExtra("searchString", searchText);
                    startActivity(intent);
                }
            }
        });

        mChooseGallery = (ImageButton) findViewById(R.id.ib_gallery_btn);
        mChooseGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType(IMAGE_TYPE);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select a saved picture"), PICK_IMAGE_REQUEST);
            }
        });

        mChooseCamera = (ImageButton) findViewById(R.id.ib_camera_btn);
        mChooseCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCameraIntent();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data.getData() != null) {
            Uri uri = data.getData();
            try {

                updateThumbnailPicture(uri);
                //send this data to our api


            } catch (IOException e) {
                Log.d(TAG, "Failed to receive gallery image. Please try again");
            }
        } else if (requestCode == CAMERA_RES_REQUEST) {
            try {
                updateThumbnailPicture(mPhotoUri);
            } catch (IOException e) {
                Log.d(TAG, "Failed to receive camera image. Please try again");
            }


        }
    }

    private void updateThumbnailPicture(Uri imageUri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
        ImageView imageView = (ImageView) findViewById(R.id.iv_user_image);
        imageView.setImageBitmap(bitmap);

        // prepare our acquired image data for our api... by saving to a local file first
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        FileOutputStream fos = openFileOutput(mImageFileName, Context.MODE_PRIVATE);
        fos.write(bytes.toByteArray());
        fos.close();
    }


    private void launchCameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // If there is no app to handle the camera, bail out
        if (cameraIntent.resolveActivity(getPackageManager()) == null) {
            Log.d(TAG, "No camera app located. Cancelling request.");
            return;
        }

        //Allocate URI for camera intent to use
        File cameraFile = null;
        try {
            cameraFile = createImageFile();
        } catch (IOException e) {
            Log.d(TAG, "Error while creating image file: " + e.getMessage());
        }

        if (cameraFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.cogwerks.fileprovider", cameraFile);
            mPhotoUri = photoURI;
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(cameraIntent, CAMERA_RES_REQUEST);
        }

    }

    //Based on sample code from https://developer.android.com/training/camera/photobasics.html
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );
        Log.d(TAG, "Created file: " + image);
        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }
}

