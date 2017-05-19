package com.receptix.batterybuddy.rank;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.receptix.batterybuddy.R;

import net.bohush.geometricprogressview.GeometricProgressView;

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
    private static final String TAG = "BatteryBuddy";
    RankAdapter rankAdapter;
    ArrayList<RankData> rankDataList = new ArrayList<RankData>();
    RecyclerView recyclerView_runningServices;
    LinearLayoutManager myLinearLayout;
    Context context;
    View view;
    PackageManager packageManager;
    Drawable appicon;
    int totalMemoryInMb = 0;
    HashMap<String, Integer> hashMap_packageRamUsage;
    private GeometricProgressView progressBar;
    private ImageView imageView_systemAnalyzerProgress;
    private long mShortAnimationDuration = 300;

    public RankFragment() {
        // Required empty public constructor
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
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
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
            ApplicationInfo applicationInfo;
            try {
                applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            } catch (final PackageManager.NameNotFoundException e) {
                applicationInfo = null;
            }
            if (applicationInfo != null && (applicationInfo.flags & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP | ApplicationInfo.FLAG_SYSTEM)) > 0) {
                type = 0;
            }

            final String applicationName = (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "(unknown)");
            try {
                appicon = packageManager.getApplicationIcon(packageName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            // percentage of memory used by service = memory used by service * 100 / total memory
            int valueInMb = memoryUsedByPackage/1024;
            int percentageUsed = (valueInMb*100)/totalMemoryInMb;
           /* Log.e(packageName+"\t","\t" + memoryUsedByPackage/1024 + "Mb \t ==> "+ percentageUsed + "%");*/
            // show only those apps which are using memory > 0 %
            if(percentageUsed >0 ){
                RankData rankdata = new RankData(packageName, percentageUsed+" %", appicon, type, applicationName);
                rankDataList.add(rankdata);
                rankAdapter.notifyDataSetChanged();
            }
        }
    }

    private boolean isSystemPackage(ApplicationInfo ai) {
        if((ai.flags & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP | ApplicationInfo.FLAG_SYSTEM)) == 0)
        {
            return true;
        }
        return false;
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

        // extract all PIDs and Process Names from RunningServiceInfo list
        for(ActivityManager.RunningServiceInfo runningServiceInfo : services)
        {
            if ((runningServiceInfo.flags & ActivityManager.RunningServiceInfo.FLAG_SYSTEM_PROCESS) == 0)
            {
                // add class name to list
                String className = runningServiceInfo.service.getPackageName();
                processNameList.add(className);
                //add pid to list
                int pid = runningServiceInfo.pid;
                pidList.add(pid);
            }
        }

        /*Log.e("getAllProcessesMemoryInfo()", "pidList obtained");*/

        int[] pidIntegerArray = new int[pidList.size()];

        Log.e("pidListSize", pidList.size() + "");

        for(int i=0, len = pidList.size(); i < len; i++)
            pidIntegerArray[i] = pidList.get(i);

        Debug.MemoryInfo[] memoryInfo = activityManager.getProcessMemoryInfo(pidIntegerArray);
        /*Log.e("getAllProcessesMemoryInfo()", "getProcessMemoryInfo");*/

        for(int i=0; i<memoryInfo.length; i++)
        {
            // put PROCESS NAME and MEMORY USAGE into Hashmap
            // this value is already in kB
            hashMap.put(processNameList.get(i), memoryInfo[i].getTotalPrivateDirty());
        }

        /*Log.e("getAllProcessesMemoryInfo()", "hashMap obtained");*/

       /* ActivityManager.MemoryInfo ramInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(ramInfo);*/

        hashMap = sortByValues(hashMap);

        // copy to global hashmap
        hashMap_packageRamUsage = hashMap;

       /* // print Contents of Hashmap (Maximum RAM Usage First)
        for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            // percentage of memory used by service = memory used by service * 100 / total memory
            int valueInMb = value/1024;
            int percentageUsed = (valueInMb*100)/totalMemoryInMb;

            Log.e(key+"\t","\t" + value/1024 + "Mb \t ==> "+ percentageUsed + "%");
        }*/
    }

    private void initView(View view) {

        recyclerView_runningServices = (RecyclerView) view.findViewById(R.id.recyclerview_listOfRunningServices);
        myLinearLayout = new LinearLayoutManager(context);
        if (rankDataList != null)
            rankDataList.clear();
        rankAdapter = new RankAdapter(context, rankDataList);
        recyclerView_runningServices.setLayoutManager(myLinearLayout);
        recyclerView_runningServices.setAdapter(rankAdapter);

        progressBar = (GeometricProgressView) view.findViewById(R.id.progressbar_loading_ranks);
        imageView_systemAnalyzerProgress = (ImageView) view.findViewById(R.id.imageview_system_analyzer_progress);

    }

    private class LoadRankData extends AsyncTask<Void, Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // keep the contentView i.e. Recycler hidden initially
            recyclerView_runningServices.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            /*imageView_systemAnalyzerProgress.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.Bounce)
                    .duration(1000)
                    .repeat(10)
                    .playOn(imageView_systemAnalyzerProgress);*/
            Log.e("LoadRankData", "onPreExecute()");
        }


        @Override
        protected Void doInBackground(Void... params) {
            // get total memory
            Log.e("LoadRankData", "doInBackground()");
            ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            totalMemoryInMb = (int) (memoryInfo.totalMem / (1024 * 1024));
            getAllProcessesMemoryInfo();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("LoadRankData", "onPostExecute()");
            if (progressBar != null)
                progressBar.setVisibility(View.GONE);

            LoadSystemApps(view);

            // now perform cross-fading of loading imageView and recyclerView_runningServices
            // Set the content view to 0% opacity but visible, so that it is visible
            // (but fully transparent) during the animation.
            recyclerView_runningServices.setAlpha(0f);
            recyclerView_runningServices.setVisibility(View.VISIBLE);

            // Animate the content view to 100% opacity, and clear any animation
            // listener set on the view.
            recyclerView_runningServices.animate()
                    .alpha(1f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(null);

            // Animate the loading view to 0% opacity. After the animation ends,
            // set its visibility to GONE as an optimization step (it won't
            // participate in layout passes, etc.)
            imageView_systemAnalyzerProgress.animate()
                    .alpha(0f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            imageView_systemAnalyzerProgress.setVisibility(View.GONE);
                        }
                    });
        }
    }
}