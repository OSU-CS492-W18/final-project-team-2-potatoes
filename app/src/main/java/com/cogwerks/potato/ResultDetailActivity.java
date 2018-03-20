package com.cogwerks.potato;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cogwerks.potato.utils.MSAzureComputerVisionUtils;
import com.cogwerks.potato.utils.MSAzureComputerVisionUtils.FullApiResult;

public class ResultDetailActivity extends AppCompatActivity {
    private static final String TAG = ResultDetailActivity.class.getSimpleName();


    public static String dataExtra = "complete_result_extra";

    private RecyclerView mResultListRecyclerView;
    private PotatoAdapter mPotatoAdapter;
    private FullApiResult mCompleteResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_detail);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //setting up recycler view
        mResultListRecyclerView = (RecyclerView)findViewById(R.id.rv_result_list);
        mResultListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mResultListRecyclerView.setHasFixedSize(true);

        //create new adapter and link to recycler view
        mPotatoAdapter = new PotatoAdapter();
        mResultListRecyclerView.setAdapter(mPotatoAdapter);

        TextView mAdultFlagTV  = findViewById(R.id.tv_adult_flag);
        TextView mAdultScoreTV = findViewById(R.id.tv_adult_score);
        TextView mRacyFlagTV   = findViewById(R.id.tv_racy_flag);
        TextView mRacyScoreTV  = findViewById(R.id.tv_racy_score);
        TextView misBwImgTV    = findViewById(R.id.tv_bw_img);
        TextView mClipartTV    = findViewById(R.id.tv_clipart);


        Intent intent = getIntent();

        if(intent != null){
            Bundle resultBundle = intent.getExtras();
            mCompleteResult = (FullApiResult) resultBundle.getSerializable(dataExtra);
            mPotatoAdapter.updateResults(mCompleteResult.tags);

            mAdultFlagTV.setText(mCompleteResult.isAdult);
            mAdultScoreTV.setText(MSAzureComputerVisionUtils.roundConfidence(mCompleteResult.adultScore) + "%");
            mRacyFlagTV.setText(mCompleteResult.isRacy);
            mRacyScoreTV.setText(MSAzureComputerVisionUtils.roundConfidence(mCompleteResult.racyScore) + "%");
            misBwImgTV.setText(mCompleteResult.blackAndWhite);
            mClipartTV.setText(mCompleteResult.clipArtType);
        }
        boolean hideExtraTags = prefs.getBoolean(getString(R.string.pref_fields_tag_key), Boolean.parseBoolean(getString(R.string.pref_fields_tag_default)));
        if(!hideExtraTags)
        {
            findViewById(R.id.rv_result_list).setVisibility(View.INVISIBLE);
        }
        boolean hideAdultStats = prefs.getBoolean(getString(R.string.pref_fields_adult_key), Boolean.parseBoolean(getString(R.string.pref_fields_adult_default)));
        if(!hideAdultStats)
        {
            findViewById(R.id.ll_adult_flag).setVisibility(View.INVISIBLE);
            findViewById(R.id.ll_adult_score).setVisibility(View.INVISIBLE);
            findViewById(R.id.ll_racy_flag).setVisibility(View.INVISIBLE);
            findViewById(R.id.ll_racy_score).setVisibility(View.INVISIBLE);
        }

        boolean hideColorStats = prefs.getBoolean(getString(R.string.pref_fields_color_key), Boolean.parseBoolean(getString(R.string.pref_fields_color_default)));
        if(!hideColorStats)
        {
            findViewById(R.id.ll_bw_img).setVisibility(View.INVISIBLE);
        }
        boolean hideClipartStats = prefs.getBoolean(getString(R.string.pref_fields_clipart_key), Boolean.parseBoolean(getString(R.string.pref_fields_clipart_default)));
        if(!hideClipartStats)
        {
            findViewById(R.id.ll_clipart).setVisibility(View.INVISIBLE);
        }
    }


}
