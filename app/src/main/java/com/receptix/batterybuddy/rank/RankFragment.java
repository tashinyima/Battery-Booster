package com.receptix.batterybuddy.rank;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.receptix.batterybuddy.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class RankFragment extends Fragment {

    private static final String TAG = "BatteryBuddy";
    RankAdapter rankAdapter;
    ArrayList<RankData> rankDatas = new ArrayList<RankData>();
    RecyclerView recyclerView;
    LinearLayoutManager myLinearLayout;
    Context context;
    View view;


    public RankFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_rank, container, false);
        context = getActivity();
        // Inflate the layout for this fragment

        initView(view);
        LoadSystemApps(view);

        return view;
    }

    private void LoadSystemApps(View view) {

        Log.d(TAG, "installtype"+String.valueOf(RankData.INSTALL_TYPE));

//            public RankData(int type,String packageName, String powerUsage, String packageIconUrl, String id) {


        RankData datarank = new RankData(0, "Google Buss", "40%", ContextCompat.getDrawable(context, R.drawable.flowar).toString(), "0");
        rankDatas.add(datarank);
        datarank = new RankData(1, "Yahoo Buss", "50%", ContextCompat.getDrawable(context, R.drawable.googleplus32).toString(), "1");
        rankDatas.add(datarank);
        datarank = new RankData(0, "Bing Buss", "30%", ContextCompat.getDrawable(context, R.drawable.lockscreenimg).toString(), "2");
        rankDatas.add(datarank);
        datarank = new RankData(1, "Baidu Buss", "70%", ContextCompat.getDrawable(context, R.drawable.stacklayer).toString(), "3");
        rankDatas.add(datarank);

        rankAdapter.notifyDataSetChanged();
    }

    private void initView(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        myLinearLayout = new LinearLayoutManager(context);
        rankAdapter = new RankAdapter(context, rankDatas);
        recyclerView.setLayoutManager(myLinearLayout);
        recyclerView.setAdapter(rankAdapter);

    }

}
