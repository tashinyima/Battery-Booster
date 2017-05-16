package com.receptix.batterybuddy.optimizeractivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.receptix.batterybuddy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hello on 5/15/2017.
 */

public class MyOptimizerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    ArrayList<OptimizerData> optimizerDatas;
    Context context;




   public static class GridListViewHolder extends RecyclerView.ViewHolder {

       ImageView gridImageView;


       public GridListViewHolder(View itemView) {
           super(itemView);
           gridImageView = (ImageView) itemView.findViewById(R.id.gridviewImageView);
       }
   }

    public MyOptimizerAdapter(ArrayList<OptimizerData> optimizerDatas, Context context) {
        this.optimizerDatas = optimizerDatas;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gridview_row_recycler,parent,false);

        return new GridListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        OptimizerData data = optimizerDatas.get(position);
        if(data!=null){
            if(holder instanceof GridListViewHolder){

                ((GridListViewHolder) holder).gridImageView.setImageDrawable(data.getDrawableIcon());
            }
        }



    }

    @Override
    public int getItemCount() {
        return optimizerDatas.size();
    }




    public void clearApplications() {
        int size = this.optimizerDatas.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                optimizerDatas.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }


    }


}
