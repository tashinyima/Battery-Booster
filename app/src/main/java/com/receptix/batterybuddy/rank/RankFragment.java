package com.receptix.batterybuddy.rank;


import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.support.v4.content.AsyncTaskLoader;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class RankFragment extends Fragment {
    private static String packageNameBuddy = "com.receptix.batterybuddy";

    private static final String TAG = "BatteryBuddy";
    RankAdapter rankAdapter;
    ArrayList<RankData> rankDatas = new ArrayList<RankData>();
    RecyclerView recyclerView;
    LinearLayoutManager myLinearLayout;
    Context context;
    View view;
    PackageManager packageManager;
    Drawable appicon;
    int totalMemoryInMb = 0;
    HashMap<String, Integer> hashMap_packageRamUsage;


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

        new LoadRankData().execute();

        return view;
    }

    private void LoadSystemApps(View view) {
        List<ApplicationInfo> list = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        int si = list.size();
        Log.d(TAG, "Total App=" + si);
        // print Contents of Hashmap (Maximum RAM Usage First)
        for (Map.Entry<String, Integer> entry : hashMap_packageRamUsage.entrySet()) {

            int type=1;

            String packageName = entry.getKey();


            Integer memoryUsedByPackage = entry.getValue();
            ApplicationInfo ai;


            try {
                ai = packageManager.getApplicationInfo(packageName, 0);

            } catch (final PackageManager.NameNotFoundException e) {
                ai = null;
            }

            if((ai.flags & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP | ApplicationInfo.FLAG_SYSTEM)) > 0){

                type =0;
            }

            final String applicationName = (String) (ai != null ? packageManager.getApplicationLabel(ai) : "(unknown)");
            try {
                appicon = packageManager.getApplicationIcon(packageName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            // percentage of memory used by service = memory used by service * 100 / total memory
            int valueInMb = memoryUsedByPackage/1024;
            int percentageUsed = (valueInMb*100)/totalMemoryInMb;
            Log.e(packageName+"\t","\t" + memoryUsedByPackage/1024 + "Mb \t ==> "+ percentageUsed + "%");

            RankData rankdata = new RankData(packageName, percentageUsed+" %", appicon, type, applicationName);
            rankDatas.add(rankdata);
            rankAdapter.notifyDataSetChanged();
        }


    }

    private int getPercentageFromPackage(String packageName) {
        for (Map.Entry<String, Integer> entry : hashMap_packageRamUsage.entrySet()) {
            String key = entry.getKey();
            if(key.equalsIgnoreCase(packageName))
            {
                Integer value = entry.getValue();
                // percentage of memory used by service = memory used by service * 100 / total memory
                int valueInMb = value/1024;
                int percentageUsed = (valueInMb*100)/totalMemoryInMb;
                return  percentageUsed;
            }
        }

        return 0;

    }


    private void getAllProcessesMemoryInfo()
    {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
        ArrayList<Integer> pidList = new ArrayList<>();
        ArrayList<String> processNameList = new ArrayList<>();
        HashMap<String, Integer> hashMap = new HashMap<>();
        for(ActivityManager.RunningServiceInfo runningServiceInfo : services)
        {
            if((runningServiceInfo.flags & runningServiceInfo.FLAG_SYSTEM_PROCESS)==0)
            {
                // add class name to list
                String className = runningServiceInfo.service.getPackageName();
                processNameList.add(className);
                //add pid to list
                int pid = runningServiceInfo.pid;
                pidList.add(pid);
            }

        }

        int[] pidIntegerArray = new int[pidList.size()];
        for(int i=0, len = pidList.size(); i < len; i++)
            pidIntegerArray[i] = pidList.get(i);

        Debug.MemoryInfo[] memoryInfo = activityManager.getProcessMemoryInfo(pidIntegerArray);
        for(int i=0; i<memoryInfo.length; i++)
        {
            // put PROCESS NAME and MEMORY USAGE into Hashmap
            // this value is already in kB
            hashMap.put(processNameList.get(i), memoryInfo[i].getTotalPrivateDirty());
        }


        ActivityManager.MemoryInfo ramInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(ramInfo);

        hashMap = sortByValues(hashMap);

        // copy to global hashmap
        hashMap_packageRamUsage = hashMap;

        // print Contents of Hashmap (Maximum RAM Usage First)
        for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            // percentage of memory used by service = memory used by service * 100 / total memory
            int valueInMb = value/1024;
            int percentageUsed = (valueInMb*100)/totalMemoryInMb;

            Log.e(key+"\t","\t" + value/1024 + "Mb \t ==> "+ percentageUsed + "%");
        }
    }


    private static HashMap sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

    private void initView(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        myLinearLayout = new LinearLayoutManager(context);
        rankAdapter = new RankAdapter(context, rankDatas);
        recyclerView.setLayoutManager(myLinearLayout);
        recyclerView.setAdapter(rankAdapter);

    }

    private class LoadRankData extends AsyncTask<Void, Void,Void> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(progressDialog!=null)
                progressDialog.dismiss();

            LoadSystemApps(view);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // get total memory
            ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);

            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            Log.i(" Total Memory ", memoryInfo.totalMem/(1024*1024) + "MB\n" );
            Log.i(" Available Memory ", memoryInfo.availMem/(1024*1024) + "MB\n" );
            totalMemoryInMb = (int) (memoryInfo.totalMem/(1024*1024));
            Log.e("Total Device memory", String.valueOf(totalMemoryInMb));

            getAllProcessesMemoryInfo();
            return null;
        }
    }
}