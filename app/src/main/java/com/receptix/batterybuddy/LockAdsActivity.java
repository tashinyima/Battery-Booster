package com.receptix.batterybuddy;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.inmobi.ads.InMobiBanner;
import com.inmobi.sdk.InMobiSdk;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LockAdsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG ="LockScreenActivity" ;
    TextView lockDateTextView, lockBatteryLevelTextView, lockbatteryChargingStatusTextView;
    ProgressBar lockProgressBar;
    ArcProgress lockcpuProgress, lockramProgress, lockbatteryProgress;
    Calendar calendar;
    ActivityManager myActivityManager;
    Context context;
    InMobiBanner bannerAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_lock_ads);
        myActivityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        this.context =getApplicationContext();

        // initialize InMobiSDK and load ad
        bannerAd = (InMobiBanner) findViewById(R.id.banner);
        InMobiSdk.init(LockAdsActivity.this, "4a38c3c40747428fa346cb0456d9034f");
        bannerAd.load();

        initView();

        InitializeData();
    }

    private void InitializeData() {
        getBatteryTemperature();
        getSystemDateNow();
        getRamInfo();


    }

    private void getRamInfo() {
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        myActivityManager.getMemoryInfo(memInfo);
        long blabla = memInfo.availMem;
        long total = memInfo.totalMem;

        long usedram = total - blabla;


        double ratio = usedram / (double) total;
        double percentage = ratio * 100;
        int rampercentage = (int) percentage;
        if (rampercentage > 70) {
            lockramProgress.setProgress(rampercentage);
            lockramProgress.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.red));
            lockramProgress.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            lockramProgress.setProgress(rampercentage);
        }


        Log.d(TAG, "MEM=" + String.valueOf(blabla) + "Total Ram=" + String.valueOf(total) + "Percentage=" + rampercentage);


    }

    private void getSystemDateNow() {

        calendar = Calendar.getInstance();
//date format is:  "Date-Month-Year Hour:Minutes am/pm"
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm a"); //Date and time
        String currentDate = sdf.format(calendar.getTime());

//Day of Name in full form like,"Saturday", or if you need the first three characters you have to put "EEE" in the date format and your result will be "Sat".
        SimpleDateFormat sdf_ = new SimpleDateFormat("EEEE");
        Date date = new Date();
        String dayName = sdf_.format(date);
        lockDateTextView.setText("" + dayName + " " + currentDate + "");
    }

    private void getBatteryTemperature() {

        IntentFilter intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        this.registerReceiver(battery_info_receiver, intentfilter);
    }

    private BroadcastReceiver battery_info_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            boolean isPresent = intent.getBooleanExtra("present", false);
            if (isPresent) {


                int temperature = intent.getIntExtra("temperature", 0);
                double doubletemp = temperature * 0.1;
                int progresint = (int) doubletemp;
                int level = intent.getIntExtra("level", 0);
                int scale = intent.getIntExtra("scale", 0);
                // Calculate the battery charged percentage
                float percentage = level / (float) scale;
                // Update the progress bar to display current battery charged percentage
                int mProgressStatus = (int) ((percentage) * 100);
                lockbatteryProgress.setProgress(progresint);

                lockbatteryProgress.setSuffixText((char) 0x00b0 + "C");
                lockbatteryProgress.setProgress(mProgressStatus);
                lockBatteryLevelTextView.setText(String.valueOf(mProgressStatus) + "%");
                if (isChargerConnected(context)) {


                    lockbatteryChargingStatusTextView.setText("Charging");
                    lockbatteryChargingStatusTextView.setVisibility(View.VISIBLE);
                }


            } else {

                showMessage("There is no battery");
            }

        }
    };

    private boolean isChargerConnected(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
    }


    private void initView() {

        lockDateTextView = (TextView) findViewById(R.id.lockdatetv);
        lockBatteryLevelTextView = (TextView) findViewById(R.id.lockBatteryLevelTextView);
        lockbatteryChargingStatusTextView = (TextView) findViewById(R.id.lockbatteryChargingStatusTextView);
        lockProgressBar = (ProgressBar) findViewById(R.id.lockProgressBar);
        lockcpuProgress = (ArcProgress) findViewById(R.id.lockcpuArcProgress);
        lockramProgress = (ArcProgress) findViewById(R.id.lockramArcProgress);
        lockbatteryProgress = (ArcProgress) findViewById(R.id.lockbatteryArcProgress);

    }

    @Override
    public void onClick(View v) {
/*
        showMessage("I am Clicked man");
        finish();*/
    }

    private void showMessage(String s) {

        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}
