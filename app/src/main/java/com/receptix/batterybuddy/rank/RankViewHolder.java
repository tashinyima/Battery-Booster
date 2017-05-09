package com.receptix.batterybuddy.rank;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by zero1 on 5/9/2017.
 */

public class RankViewHolder extends RecyclerView.ViewHolder {

    ImageView packageImage;
    TextView packageName,batteryUsage;
    int packageId;

    public RankViewHolder(View itemView) {
        super(itemView);


    }
}
