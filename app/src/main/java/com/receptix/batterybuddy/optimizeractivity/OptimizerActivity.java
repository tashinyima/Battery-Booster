package com.receptix.batterybuddy.optimizeractivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.receptix.batterybuddy.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jp.wasabeef.recyclerview.animators.FadeInAnimator;

import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_LEVEL;
import static com.receptix.batterybuddy.helper.Constants.Params.MINIMUM_INSTALLED_APPS;
import static com.receptix.batterybuddy.helper.Constants.Params.NUMBER_OF_SYSTEM_APPS_TO_SHOW;
import static com.receptix.batterybuddy.helper.Constants.Preferences.IS_ACTIVE;
import static com.receptix.batterybuddy.helper.Constants.Preferences.PREFERENCES_IS_ACTIVE;
import static com.receptix.batterybuddy.helper.Constants.ShortHandNotations.MINUTES;

public class OptimizerActivity extends AppCompatActivity {

    private static final String TAG = OptimizerActivity.class.getSimpleName();
    Context context;
    View view_optimize;
    MyOptimizerAdapter myOptimizerAdapter;
    ArrayList<OptimizerData> optimizerDataArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    PackageManager packageManager;
    Drawable appicon;
    Toolbar toolbar;
    NotificationManager notificationManager;
    private long mShortAnimationDuration = 300;
    private boolean isOptimizationInProgress = false;
    TextView textView_analysisProgressIndicator;
    TextView textView_estimatedExtendedTime;
    int receivedBatteryLevel = 0;
    ImageView batteryImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        view_optimize = getLayoutInflater().inflate(R.layout.activity_optimizer, null);
        setContentView(view_optimize);
        packageManager = this.getPackageManager();
        handleIntent(getIntent());
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(999);
        initView();
        setupToolBar(getString(R.string.poweroptimization));

        //get count of apps installed
        int count = 100;
        isOptimizationInProgress = true;
        startCountAnimation(count);

        /*new TestAsync().execute();*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showListOfInstalledApps();
            }
        }, 1500);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                performOptimization();
            }
        }, 5000);

    }

    private void handleIntent(Intent intent)
    {
        if(intent!=null)
        {
            if(intent.getExtras() != null)
            {
                int batteryLevel = intent.getExtras().getInt(BATTERY_LEVEL, 0);
                if(batteryLevel!=0)
                {
                    receivedBatteryLevel = batteryLevel;
                }
            }
        }
    }


    private void setupToolBar(String title) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        TextView textViewTitle = (TextView) toolbar.findViewById(R.id.textViewTitle);
        textViewTitle.setText(title);
    }

    private void performOptimization() {
        Log.e(TAG, "performOptimization");
        KillAllProcess();
        removeRecyclerItems();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(OptimizerActivity.this, SuccessOptimizerActivity.class);
                intent.putExtra(BATTERY_LEVEL, receivedBatteryLevel);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

    private void KillAllProcess() {
        //get list of installed apps
        List<ApplicationInfo> packages;
        packages = packageManager.getInstalledApplications(0);

        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String myPackage = getApplicationContext().getPackageName();

        for (ApplicationInfo packageInfo : packages) {
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) continue;
            if (packageInfo.packageName.equals(myPackage)) continue;
            mActivityManager.killBackgroundProcesses(packageInfo.packageName);
        }
    }

    private void startCountAnimation(int count) {
        ValueAnimator animator = ValueAnimator.ofInt(0, count);
        animator.setDuration(1400);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                textView_analysisProgressIndicator.setText(animation.getAnimatedValue() + " % completed");
            }
        });
        animator.start();
    }

    private void showListOfInstalledApps() {
        Log.e(TAG, "showListOfInstalledApps");
        List<ApplicationInfo> applicationInfoList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo applicationInfo : applicationInfoList) {
            // list only NON-SYSTEM apps
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String packageLabel = applicationInfo.loadLabel(packageManager).toString();
                String packageName = applicationInfo.packageName;
                appicon = applicationInfo.loadIcon(packageManager);
                try {
                    appicon = packageManager.getApplicationIcon(packageName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                OptimizerData singleApplicationData = new OptimizerData(appicon, packageName, packageLabel);
                Log.d("Label", singleApplicationData.getPackageLable());
                // add all applications to list besides our application
                if (!packageName.equalsIgnoreCase(context.getPackageName())) {
                    optimizerDataArrayList.add(singleApplicationData);
                    myOptimizerAdapter.notifyItemInserted(0);
                }
            }
        }

        if (optimizerDataArrayList.size() < MINIMUM_INSTALLED_APPS) {
            int numSystemApps = 0;
            for (ApplicationInfo applicationInfo : applicationInfoList) {
                if (numSystemApps < NUMBER_OF_SYSTEM_APPS_TO_SHOW) {
                    // list only NON-SYSTEM apps
                    if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                        String packageLabel = applicationInfo.loadLabel(packageManager).toString();
                        String packageName = applicationInfo.packageName;
                        appicon = applicationInfo.loadIcon(packageManager);
                        try {
                            appicon = packageManager.getApplicationIcon(packageName);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        OptimizerData singleApplicationData = new OptimizerData(appicon, packageName, packageLabel);
                        Log.d("Label", singleApplicationData.getPackageLable());
                        // add all applications to list besides our application
                        if (!packageName.equalsIgnoreCase(context.getPackageName())) {
                            optimizerDataArrayList.add(singleApplicationData);
                            myOptimizerAdapter.notifyItemInserted(0);
                            numSystemApps += 1;
                        }
                    }
                }
            }
        }


    }

    public void removeRecyclerItems() {
        if (optimizerDataArrayList.size() > 0) {
            for (int i = 0; i < optimizerDataArrayList.size(); i++) {
                myOptimizerAdapter.removeItem(i);
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.animate()
                        .alpha(0f)
                        .setDuration(mShortAnimationDuration)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                recyclerView.setVisibility(View.INVISIBLE);
                            }
                        });
            }
        }, 500);
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.optimizerecycler);
        FadeInAnimator animator = new FadeInAnimator(new OvershootInterpolator(1f));
        recyclerView.setItemAnimator(animator);
        recyclerView.getItemAnimator().setAddDuration(300);
        recyclerView.getItemAnimator().setRemoveDuration(300);
        myOptimizerAdapter = new MyOptimizerAdapter(optimizerDataArrayList, context);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 5));
        recyclerView.setAdapter(myOptimizerAdapter);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        textView_analysisProgressIndicator = (TextView) findViewById(R.id.applistTextView);
        textView_estimatedExtendedTime = (TextView) findViewById(R.id.estimatedextensionTextView);
        batteryImageView = (ImageView) findViewById(R.id.batteryImageView);
        AnimationDrawable animation = new AnimationDrawable();

        animation.addFrame(ContextCompat.getDrawable(context,R.drawable.batterylevel1),100);
        animation.addFrame(ContextCompat.getDrawable(context,R.drawable.batterylevel2),200);
        animation.addFrame(ContextCompat.getDrawable(context,R.drawable.batterylevel3),300);
        animation.addFrame(ContextCompat.getDrawable(context,R.drawable.batterylevel4),400);
        animation.setOneShot(false);
        batteryImageView.setImageDrawable(animation);
        animation.start();


        int startRange = 0;
        int endRange = 0;

        if(receivedBatteryLevel<=10)
        {
            startRange = 10;
            endRange = 15;
        }
        if(receivedBatteryLevel>10 && receivedBatteryLevel<=30)
        {
            startRange = 20;
            endRange = 35;
        }
        if(receivedBatteryLevel>30)
        {
            startRange = 40;
            endRange = 50;
        }
        int randomExtendedTimePeriod = getRandomNumberInRange(startRange,endRange);
        textView_estimatedExtendedTime.setText(" " + randomExtendedTimePeriod + " " + MINUTES);
    }

    private static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }


    @Override
    public void onBackPressed() {
        if (!isOptimizationInProgress)
            finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setIsActive(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        setIsActive(false);
    }

    private void setIsActive(boolean isActive)
    {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_IS_ACTIVE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_ACTIVE, isActive);
        editor.commit();
        Log.e(TAG, "isActive = "+isActive);
    }

}
