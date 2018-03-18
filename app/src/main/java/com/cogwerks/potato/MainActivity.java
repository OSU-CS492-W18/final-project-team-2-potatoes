package com.cogwerks.potato;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
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

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText mPotatoSearchText;
    private ImageView mUserPic;
    private ImageButton mChooseGallery;
    private ImageButton mChooseCamera;
    private int PICK_IMAGE_REQUEST = 1;
    private static final String IMAGE_TYPE = "image/*";
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView mLoadingErrorMessageTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        //Add onClickListener for camera button here

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data.getData() != null){
            Uri uri = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ImageView imageView = (ImageView) findViewById(R.id.iv_user_image);
                imageView.setImageBitmap(bitmap);
                //send this data to our api
            } catch (IOException e) {
                Log.d("Error: ", "Failed to receive image. Please try again");
            }
        }
    }
}
