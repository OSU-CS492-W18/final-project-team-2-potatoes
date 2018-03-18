package com.cogwerks.potato;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.cogwerks.potato.utils.MSAzureComputerVisionUtils;

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
            mPotatoAdapter.addResult(searchString);
        }
        loadResults();
    }

    public void loadResults() {
        String url = MSAzureComputerVisionUtils.buildAnalyzeURL();
        Log.d(TAG, "loadResults: " + url);
    }
}
