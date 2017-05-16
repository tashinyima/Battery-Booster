package com.receptix.batterybuddy.optimizeractivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.receptix.batterybuddy.R;
import com.receptix.batterybuddy.general.UserSessionManager;

import java.util.ArrayList;
import java.util.List;

public class OptimizerActivity extends AppCompatActivity {

    private static final String TAG = "Optimize Android";
    Context context;
    View view_optimized, view_optimize;
    MyOptimizerAdapter myOptimizerAdapter;
    ArrayList<OptimizerData> optimizerDatas = new ArrayList<>();
    RecyclerView recyclerView;
    PackageManager packageManager;
    Drawable appicon;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view_optimize = getLayoutInflater().inflate(R.layout.activity_optimizer, null);
        setContentView(view_optimize);
        packageManager = this.getPackageManager();
        initView();
        setupToolBar(getString(R.string.poweroptimization));

        ListPackageData();


    }

    private void setupToolBar(String title) {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView textViewTitle = (TextView) toolbar.findViewById(R.id.textViewTitle);
        textViewTitle.setText(title);


    }


    @Override
    protected void onStart() {
        super.onStart();
        DeleteRecyclerItems();



    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void DeleteRecyclerItems() {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                myOptimizerAdapter.clearApplications();

                KillAllProcess();

                Intent intent = new Intent(OptimizerActivity.this, SuccessOptimizerActivity.class);

//                requestcode=9000
                startActivity(intent);
                finish();
            }
        }, 2000);


    }

    private void KillAllProcess() {

        List<ApplicationInfo> packages;

        //get a list of installed apps.
        packages = packageManager.getInstalledApplications(0);

        ActivityManager mActivityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        String myPackage = getApplicationContext().getPackageName();
        for (ApplicationInfo packageInfo : packages) {
            if((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM)==1)continue;
            if(packageInfo.packageName.equals(myPackage)) continue;
            mActivityManager.killBackgroundProcesses(packageInfo.packageName);
            // ok nothing is done
        }
    }

    private void ListPackageData() {


        List<ApplicationInfo> list = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        int si = list.size();
        Log.d(TAG, "Total App=" + si);
//        FLagSystem == 1
        //Downloaded apps FLAG==0;


        for (int i = 0; i < list.size(); i++) {
            if ((list.get(i).flags & ApplicationInfo.FLAG_SYSTEM) == 0) {

                String packageLabel = list.get(i).loadLabel(packageManager).toString();
                String packageName = list.get(i).packageName;
                appicon = list.get(i).loadIcon(packageManager);
                try {
                    appicon = packageManager.getApplicationIcon(packageName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                OptimizerData rankdata = new OptimizerData(appicon, packageName, packageLabel);
                optimizerDatas.add(rankdata);
                myOptimizerAdapter.notifyDataSetChanged();

            }

        }


    }

    private void initView() {

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(3000);
        itemAnimator.setRemoveDuration(1000);

        recyclerView = (RecyclerView) findViewById(R.id.optimizerecycler);
        recyclerView.setItemAnimator(itemAnimator);
        myOptimizerAdapter = new MyOptimizerAdapter(optimizerDatas, context);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 5));
        recyclerView.setAdapter(myOptimizerAdapter);
        toolbar = (Toolbar) findViewById(R.id.toolbar);


    }

}
