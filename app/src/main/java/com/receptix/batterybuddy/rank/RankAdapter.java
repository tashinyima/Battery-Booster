package com.receptix.batterybuddy.rank;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.receptix.batterybuddy.R;

import java.util.ArrayList;

/**
 * Created by zero1 on 5/9/2017.
 */

public class RankAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    Context context;
    ArrayList<RankData> rankDatas;
    int total_type;

    public static class InstalledTypeViewHolder extends RecyclerView.ViewHolder {

        ImageView packageImage;
        TextView packageName, batteryUsage;

        public InstalledTypeViewHolder(View itemView) {
            super(itemView);
            packageImage = (ImageView) itemView.findViewById(R.id.packageIconImageView);
            packageName = (TextView) itemView.findViewById(R.id.packageNameTextView);
            batteryUsage = (TextView) itemView.findViewById(R.id.powerConsumedTextView);


        }
    }

    public static class SystemTypeViewHolder extends RecyclerView.ViewHolder {

        TextView spackageName,sbatteyUsage;
        ImageView spackageImage;


        public SystemTypeViewHolder(View itemView) {
            super(itemView);
            sbatteyUsage = (TextView) itemView.findViewById(R.id.spowerConsumedTextView);
            spackageImage = (ImageView) itemView.findViewById(R.id.spackageIconImageView);
            spackageName = (TextView) itemView.findViewById(R.id.spackageNameTextView);

        }
    }


    public RankAdapter(Context context, ArrayList<RankData> rankDatas) {

        this.context = context;
        this.rankDatas = rankDatas;
        total_type = rankDatas.size();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case RankData.SYSTEM_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rank_row_system_layout, parent, false);
                return new SystemTypeViewHolder(view);
            case RankData.INSTALL_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rank_row_layout, parent, false);

                return new InstalledTypeViewHolder(view);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        RankData rankData = rankDatas.get(position);

        if(rankData!=null){
            try{

            if(holder instanceof SystemTypeViewHolder){

                    ((SystemTypeViewHolder) holder).spackageName.setText(rankData.getPackageName());
                    ((SystemTypeViewHolder) holder).sbatteyUsage.setText(rankData.getPowerUsage());
                    Glide.with(context).load(rankData.getPackageIconUrl()).error(R.drawable.kiwi).into(((SystemTypeViewHolder) holder).spackageImage);

                } else if(holder instanceof InstalledTypeViewHolder){

                ((InstalledTypeViewHolder) holder).packageName.setText(rankData.getPackageName());
                ((InstalledTypeViewHolder) holder).batteryUsage.setText(rankData.getPowerUsage());
                 Glide.with(context).load(rankData.getPackageIconUrl()).error(R.drawable.rabbit32).into(((InstalledTypeViewHolder) holder).packageImage);

            }

            }catch (Exception ex){

                ex.fillInStackTrace();
            }
        }




    }

    @Override
    public int getItemCount() {
        return rankDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (rankDatas.get(position).type) {

            case 0:
                return RankData.SYSTEM_TYPE;
            case 1:
                return RankData.INSTALL_TYPE;
            default:
                return -1;
        }
    }
}
