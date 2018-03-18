package com.cogwerks.potato;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class ResultDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    private static final String TAG = ResultDetailActivity.class.getSimpleName();

    private static final String ANALYZE_URL_KEY = "visionAnalyzeURL";

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
            mPotatoAdapter.addResult(searchString);
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader being called");
        String visionAnalyzeURL = null;
        if (args != null) {
            visionAnalyzeURL = args.getString(ANALYZE_URL_KEY);
        }
        return new PotatoLoader(this, visionAnalyzeURL);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        Log.d(TAG, "onLoadFinished: got results from loader");
        if (data != null) {
            // declare ArrayList of custom-class instances that represents list of grabbed tags. Assign parse results of "data" to it.
            // Through the adapter, update the results with the above ArrayList
            mPotatoAdapter.addResult(data);
        } else {
            // set loading error to be visible (see MainActivity version for ref)
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        // Nothing to do
    }
}
