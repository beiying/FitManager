package com.beiying.demo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.beiying.demo.view.RecyclerItemView;

import java.util.List;

/**
 * Created by beiying on 18/6/5.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
    private List<String> mDatas;
    private Context mContext;

    public RecyclerAdapter(Context context, List<String> data) {
        mContext = context;
        mDatas = data;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemView itemView = new RecyclerItemView(parent.getContext());
        RecyclerViewHolder viewHolder = new RecyclerViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.mItemView.setText(mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        RecyclerItemView mItemView;

        public RecyclerViewHolder(RecyclerItemView itemView) {
            super(itemView);
            mItemView = itemView;
        }


    }

}
