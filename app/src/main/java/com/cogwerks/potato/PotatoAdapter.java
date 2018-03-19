package com.cogwerks.potato;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cogwerks.potato.utils.MSAzureComputerVisionUtils;

import java.util.ArrayList;

/**
 * Created by Susmita on 3/5/2018.
 */

public class PotatoAdapter extends RecyclerView.Adapter<PotatoAdapter.PotatoViewHolder> {
    private ArrayList<MSAzureComputerVisionUtils.AnalyzeResult> mResultList;

    public PotatoAdapter(){
        mResultList = new ArrayList<MSAzureComputerVisionUtils.AnalyzeResult>();
    }

    @Override
    public PotatoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.result_list_item, parent, false);
        PotatoViewHolder viewHolder = new PotatoViewHolder(view);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(PotatoViewHolder holder, int position) {
        MSAzureComputerVisionUtils.AnalyzeResult result = mResultList.get(mResultList.size() - position - 1);
        holder.bind(result);
    }

    public void updateResults(ArrayList<MSAzureComputerVisionUtils.AnalyzeResult> results) {
        mResultList = results;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount(){
        return mResultList.size();
    }

    class PotatoViewHolder extends RecyclerView.ViewHolder {
        private TextView mResultTextView;
        private TextView mResultConfidenceView;
        private ProgressBar mResultPercentBar;

        public PotatoViewHolder(View itemView) {
            super(itemView);
            mResultTextView = (TextView) itemView.findViewById(R.id.tv_result_text);
            mResultConfidenceView = (TextView) itemView.findViewById(R.id.tv_result_confidence);
            mResultPercentBar = (ProgressBar) itemView.findViewById(R.id.tv_result_percent_bar);
        }

        public void bind(MSAzureComputerVisionUtils.AnalyzeResult result){
            mResultTextView.setText(result.tag + ":");
            mResultConfidenceView.setText("70%");
            mResultPercentBar.setProgress(70);
        }
    }
}
