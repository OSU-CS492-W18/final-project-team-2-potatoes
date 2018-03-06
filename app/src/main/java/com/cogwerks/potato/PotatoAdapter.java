package com.cogwerks.potato;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Susmita on 3/5/2018.
 */

public class PotatoAdapter extends RecyclerView.Adapter<PotatoAdapter.PotatoViewHolder> {
    private ArrayList<String> mResultList;

    public PotatoAdapter(){
        mResultList = new ArrayList<String>();
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
        String result = mResultList.get(mResultList.size() - position - 1);
        holder.bind(result);
    }

    public void addResult(String result){
        mResultList.add(result);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount(){
        return mResultList.size();
    }

    class PotatoViewHolder extends RecyclerView.ViewHolder {
        private TextView mResultTextView;

        public PotatoViewHolder(View itemView) {
            super(itemView);
            mResultTextView = (TextView) itemView.findViewById(R.id.tv_result_text);
        }

        public void bind(String result){
            mResultTextView.setText(result);
        }
    }
}
