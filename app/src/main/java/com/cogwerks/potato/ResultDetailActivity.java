package com.cogwerks.potato;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.cogwerks.potato.utils.MSAzureComputerVisionUtils;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class ResultDetailActivity extends AppCompatActivity {
    private static final String TAG = ResultDetailActivity.class.getSimpleName();



    private RecyclerView mResultListRecyclerView;
    private PotatoAdapter mPotatoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_detail);

        //setting up recycler view
        mResultListRecyclerView = (RecyclerView)findViewById(R.id.rv_result_list);
        mResultListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mResultListRecyclerView.setHasFixedSize(true);

        //create new adapter and link to recycler view
        mPotatoAdapter = new PotatoAdapter();
        mResultListRecyclerView.setAdapter(mPotatoAdapter);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("searchString")){
            String searchString = intent.getExtras().getString("searchString");
        }

    }


}
