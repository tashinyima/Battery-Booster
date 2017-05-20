package com.receptix.batterybuddy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.receptix.batterybuddy.R;
import com.receptix.batterybuddy.model.OptimizerData;

import java.util.ArrayList;

/**
 * Created by hello on 5/15/2017.
 */

public class MyOptimizerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    ArrayList<OptimizerData> optimizerDataArrayList;
    Context context;

    public MyOptimizerAdapter(ArrayList<OptimizerData> optimizerDataArrayList, Context context) {
        this.optimizerDataArrayList = optimizerDataArrayList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gridview_row_recycler,parent,false);
        return new GridListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        OptimizerData data = optimizerDataArrayList.get(position);
        if(data!=null){
            if(holder instanceof GridListViewHolder){
                ((GridListViewHolder) holder).gridImageView.setImageDrawable(data.getDrawableIcon());
            }
        }
    }

    @Override
    public int getItemCount() {
        return optimizerDataArrayList.size();
    }

    public void clearApplications() {
        int size = this.optimizerDataArrayList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                optimizerDataArrayList.remove(0);
            }
            this.notifyItemRangeRemoved(0, size);
        }
    }

    public void removeItem(int position) {
        notifyItemRemoved(position);
    }

    public static class GridListViewHolder extends RecyclerView.ViewHolder {

        ImageView gridImageView;

        GridListViewHolder(View itemView) {
            super(itemView);
            gridImageView = (ImageView) itemView.findViewById(R.id.gridviewImageView);
        }
    }


}
