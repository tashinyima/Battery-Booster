package com.receptix.batterybuddy.rank;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.receptix.batterybuddy.R;

import java.util.ArrayList;
import java.util.List;

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
    PackageManager packageManager;
    Drawable appicon;


    public RankFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_rank, container, false);
        context = getActivity();
        // Inflate the layout for this fragment
        packageManager = context.getPackageManager();
        initView(view);
        LoadSystemApps(view);

        ApplicationList();

        return view;
    }

    private void ApplicationList() {


    }

    private void LoadSystemApps(View view) {
        List<ApplicationInfo> list = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        int si = list.size();
         Log.d(TAG,"Total App="+si);
//        FLagSystem == 1
        //Downloaded apps FLAG==0;


  for(int i=0;i<list.size();i++){
      if((list.get(i).flags&ApplicationInfo.FLAG_SYSTEM)==0){

          String packageLabel = list.get(i).loadLabel(packageManager).toString();
          String packageName =list.get(i).packageName;
          appicon =list.get(i).loadIcon(packageManager);
          try {
              appicon =packageManager.getApplicationIcon(packageName);
          } catch (PackageManager.NameNotFoundException e) {
              e.printStackTrace();
          }
          RankData rankdata = new RankData(packageName,"40",appicon,0,packageLabel);
          rankDatas.add(rankdata);
          rankAdapter.notifyDataSetChanged();

      }



  }





    }

    private void initView(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        myLinearLayout = new LinearLayoutManager(context);
        rankAdapter = new RankAdapter(context, rankDatas);
        recyclerView.setLayoutManager(myLinearLayout);
        recyclerView.setAdapter(rankAdapter);

    }

}
