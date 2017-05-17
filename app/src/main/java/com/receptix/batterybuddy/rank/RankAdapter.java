package com.receptix.batterybuddy.rank;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.receptix.batterybuddy.R;

import java.util.ArrayList;

/**
 * Created by zero1 on 5/9/2017.
 */

public class RankAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    Context context;
    ArrayList<RankData> rankDatas;
    int total_type;

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

        final RankData rankData = rankDatas.get(position);

        if(rankData!=null){
            try{

            if(holder instanceof SystemTypeViewHolder){

                    ((SystemTypeViewHolder) holder).spackageName.setText(rankData.getPackageLabel());
                    ((SystemTypeViewHolder) holder).sbatteyUsage.setText(rankData.getPowerUsage());
                    ((SystemTypeViewHolder) holder).spackageImage.setImageDrawable(rankData.getPackageIcon());
                   // Glide.with(context).load(rankData.getPackageIcon()).error(R.drawable.kiwi).into(((SystemTypeViewHolder) holder).spackageImage);
                ((SystemTypeViewHolder) holder).checkbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        showMessage("System");
                        Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        i.addCategory(Intent.CATEGORY_DEFAULT);
                        i.setData(Uri.parse("package:" + rankData.getPackageName()));
                        context.startActivity(i);

                    }
                });


                } else if(holder instanceof InstalledTypeViewHolder){

                ((InstalledTypeViewHolder) holder).packageName.setText(rankData.getPackageLabel());
                ((InstalledTypeViewHolder) holder).batteryUsage.setText(rankData.getPowerUsage());
                ((InstalledTypeViewHolder) holder).packageImage.setImageDrawable(rankData.getPackageIcon());
               //  Glide.with(context).load(rankData.getPackageIcon()).error(R.drawable.rabbit32).into(((InstalledTypeViewHolder) holder).packageImage);
                ((InstalledTypeViewHolder) holder).closebutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(Intent.ACTION_DELETE);
                        intent.setData(Uri.parse("package:"+rankData.getPackageName()));
                        context.startActivity(intent);

                        showMessage("Non_System");

                    }
                });



            }

            }catch (Exception ex){

                ex.fillInStackTrace();
            }
        }




    }

    private void showMessage(String system) {
        Toast.makeText(context,system, Toast.LENGTH_SHORT).show();
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

    public static class InstalledTypeViewHolder extends RecyclerView.ViewHolder {

        ImageView packageImage;
        TextView packageName, batteryUsage;
        Button closebutton;

        public InstalledTypeViewHolder(View itemView) {
            super(itemView);
            packageImage = (ImageView) itemView.findViewById(R.id.packageIconImageView);
            packageName = (TextView) itemView.findViewById(R.id.packageNameTextView);
            packageName.setEms(9);
            batteryUsage = (TextView) itemView.findViewById(R.id.powerConsumedTextView);
            closebutton = (Button) itemView.findViewById(R.id.checkbutton);


        }
    }

    public static class SystemTypeViewHolder extends RecyclerView.ViewHolder {

        TextView spackageName, sbatteyUsage;
        ImageView spackageImage;
        Button checkbutton;


        public SystemTypeViewHolder(View itemView) {
            super(itemView);
            sbatteyUsage = (TextView) itemView.findViewById(R.id.spowerConsumedTextView);
            spackageImage = (ImageView) itemView.findViewById(R.id.spackageIconImageView);
            spackageName = (TextView) itemView.findViewById(R.id.spackageNameTextView);
            spackageName.setEms(9);
            checkbutton = (Button) itemView.findViewById(R.id.scheckbutton);


        }
    }
}
