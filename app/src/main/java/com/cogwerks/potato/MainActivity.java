package com.cogwerks.potato;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.cogwerks.potato.utils.MSAzureComputerVisionUtils;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<String>{

    private EditText mPotatoSearchText;
    private ImageView mUserPic;
    private ImageButton mChooseGallery;
    private ImageButton mChooseCamera;
    private ProgressBar mLoadingPB;
    private Button mTellMeBtn;
    private Button mDetailViewBtn;

    private String mImageFileName = "imageFile";
    private int PICK_IMAGE_REQUEST = 1;
    private int CAMERA_RES_REQUEST = 2;
    private final static String LAST_COMPLETE_RESULT = "lastCompleteResult";

    private static final String IMAGE_TYPE = "image/*";
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView mLoadingErrorMessageTV;
    private static final String ANALYZE_URL_KEY = "visionAnalyzeURL";
    private static final String ANALYZE_RAW_IMAGE_DATA = "visionAnalyzeBytes";
    private static final int VISION_ANALYZE_LOADER_ID = 0;
    MSAzureComputerVisionUtils.FullApiResult mLastCompleteResult = null;
    SharedPreferences mPrefs;

    private Uri mPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Startup");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);


        mPotatoSearchText = (EditText) findViewById(R.id.et_search_box);
        mPotatoSearchText.setText(mPrefs.getString(
                getString(R.string.pref_search_key),
                getString(R.string.pref_search_default)
        ));

        mLoadingErrorMessageTV = findViewById(R.id.tv_loading_error_message);
        mLoadingPB = findViewById(R.id.pb_query_running);
        mUserPic = (ImageView) findViewById(R.id.iv_user_image);

        mTellMeBtn = (Button) findViewById(R.id.btn_tell_me);
        mDetailViewBtn = (Button) findViewById(R.id.btn_detail_view);
        //Called when user hits tell me button
        mTellMeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mUserPic.getTag().equals("default"))
                {
                    mLoadingErrorMessageTV.setText(R.string.error_no_image);
                }
                else
                    doVisionAnalyze();
            }
        });
        mDetailViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mLastCompleteResult != null) {
                    Bundle resultBundle = new Bundle();
                    resultBundle.putSerializable(ResultDetailActivity.dataExtra, mLastCompleteResult);
                    Intent intent = new Intent(getApplicationContext(), ResultDetailActivity.class);
                    intent.putExtras(resultBundle);
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

        mPotatoSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences.Editor edit = mPrefs.edit();
                edit.putString(getString(R.string.pref_search_key), s.toString());
                edit.commit();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPotatoSearchText.setText(mPrefs.getString(
                getString(R.string.pref_search_key),
                getString(R.string.pref_search_default)
        ));
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
        mUserPic.setImageBitmap(bitmap);

        // prepare our acquired image data for our api... by saving to a local file first
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        FileOutputStream fos = openFileOutput(mImageFileName, Context.MODE_PRIVATE);
        fos.write(bytes.toByteArray());
        fos.close();

        mUserPic.setTag("configured");
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

    private void doVisionAnalyze() {

        //Step 1: Deactivate calling buttons
        mTellMeBtn.setClickable(false);
        //Step 2: Activate progress spinner
        mLoadingPB.setVisibility(View.VISIBLE);
        mLoadingErrorMessageTV.setText("");
        //Step 3: Run it

        String visionAnalyzeURL = MSAzureComputerVisionUtils.buildAnalyzeURL();
        byte[] visionAnalyzeBytes = null;
        // put image into input stream
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(this.getApplicationContext().openFileInput("imageFile"));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());
            try {
                visionAnalyzeBytes = IOUtils.toByteArray(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bundle args = new Bundle();
        args.putString(ANALYZE_URL_KEY, visionAnalyzeURL);
        args.putByteArray(ANALYZE_RAW_IMAGE_DATA, visionAnalyzeBytes);
        getSupportLoaderManager().restartLoader(VISION_ANALYZE_LOADER_ID, args, this);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader being called");
        String visionAnalyzeURL = null;
        byte[] visionAnalyzeBytes = null;
        if (args != null) {
            visionAnalyzeURL = args.getString(ANALYZE_URL_KEY);
            visionAnalyzeBytes = args.getByteArray(ANALYZE_RAW_IMAGE_DATA);
        }
        return new PotatoLoader(this, visionAnalyzeURL, visionAnalyzeBytes);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        Log.d(TAG, "onLoadFinished: got results from loader");
        mLoadingPB.setVisibility(View.INVISIBLE);
        mTellMeBtn.setClickable(true);
        mDetailViewBtn.setVisibility(View.VISIBLE);
        if (data != null) {
            // declare ArrayList of custom-class instances that represents list of grabbed tags. Assign parse results of "data" to it.
            mLastCompleteResult = MSAzureComputerVisionUtils.parseAnalyzeResultsJSON(data);
            boolean foundTag = false;
            if(mLastCompleteResult.tags == null)
                return;

            String guess = mPotatoSearchText.getText().toString();
            guess = guess.toLowerCase();
            for(int i = 0; i < mLastCompleteResult.tags.size(); i++)
            {
                if(guess.equals(mLastCompleteResult.tags.get(i).tag.toLowerCase()))
                {
                    foundTag = true;
                }
            }

            configureImageBorder(foundTag);

        } else {
            // set loading error to be visible (see MainActivity version for ref)
            mLoadingErrorMessageTV.setText(R.string.load_fail_err);

        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        // Nothing to do
    }

    void configureImageBorder(boolean success)
    {
        if(success)
            mUserPic.setBackgroundColor(0xFF00FF00);
        else
            mUserPic.setBackgroundColor(0XFFFF0000);
    }

}

