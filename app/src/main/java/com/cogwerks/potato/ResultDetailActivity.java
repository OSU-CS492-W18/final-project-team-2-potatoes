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

public class ResultDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    private static final String TAG = ResultDetailActivity.class.getSimpleName();

    private static final String ANALYZE_URL_KEY = "visionAnalyzeURL";
    private static final String ANALYZE_RAW_IMAGE_DATA = "visionAnalyzeBytes";
    private static final int VISION_ANALYZE_LOADER_ID = 0;

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

        doVisionAnalyze();
        getSupportLoaderManager().initLoader(VISION_ANALYZE_LOADER_ID, null, this);
    }

    private void doVisionAnalyze() {
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
        if (data != null) {
            // declare ArrayList of custom-class instances that represents list of grabbed tags. Assign parse results of "data" to it.
            ArrayList<MSAzureComputerVisionUtils.AnalyzeResult> tags = MSAzureComputerVisionUtils.parseAnalyzeResultsJSON(data);
            // Through the adapter, update the results with the above ArrayList
            mPotatoAdapter.updateResults(tags);
        } else {
            // set loading error to be visible (see MainActivity version for ref)
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        // Nothing to do
    }
}
