package com.receptix.batterybuddy.activities;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiBanner;
import com.inmobi.sdk.InMobiSdk;
import com.receptix.batterybuddy.LockScreenWidgetService;
import com.receptix.batterybuddy.R;
import com.receptix.batterybuddy.databinding.ActivityLockAdsBinding;
import com.receptix.batterybuddy.helper.LogUtil;
import com.receptix.batterybuddy.helper.UserSessionManager;
import com.romainpiel.shimmer.Shimmer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static com.receptix.batterybuddy.helper.Constants.BannerPlacementIds.INMOBI_ACCOUNT_ID;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_LEVEL;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_SCALE;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_TEMPERATURE;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_TEMPERATURE_CONVERSION_UNIT;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.IS_BATTERY_PRESENT;
import static com.receptix.batterybuddy.helper.Constants.DateFormats.FORMAT_DATE_ONLY;
import static com.receptix.batterybuddy.helper.Constants.DateFormats.FORMAT_HOUR_MINUTES;

public class LockAdsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int BANNER_WIDTH = 320;
    public static final int BANNER_HEIGHT = 50;
    private static final String TAG = LockAdsActivity.class.getSimpleName();
    Calendar calendar;
    ActivityManager myActivityManager;
    Context context;
    ActivityLockAdsBinding binding;
    int USED_RAM_PERCENTAGE_THRESHOLD = 70;
    InMobiBanner inMobiBanner;
    boolean locked = false;
    private BroadcastReceiver timeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //update time value on time change
            getCurrentSystemDateTime();
            // update RAM information on time change
            getRamInformation();
            //update CPU Usage
            getCpuUsageInfo();
        }
    };
    private BroadcastReceiver battery_info_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            boolean isPresent = intent.getBooleanExtra(IS_BATTERY_PRESENT, false);
            if (isPresent) {

                // Calculate Battery Temperature (currently unused)
                int temperature = intent.getIntExtra(BATTERY_TEMPERATURE, 0);
                double temperatureInDouble = temperature * BATTERY_TEMPERATURE_CONVERSION_UNIT;
                int batteryTemperature = (int) temperatureInDouble;

                // Calculate Battery Charging Level
                int level = intent.getIntExtra(BATTERY_LEVEL, 0);
                int scale = intent.getIntExtra(BATTERY_SCALE, 0);
                float percentage = level / (float) scale;
                int batteryLevel = (int) ((percentage) * 100);
                binding.lockbatteryArcProgress.setSuffixText(getString(R.string.percentage_symbol));
                binding.lockbatteryArcProgress.setProgress(batteryLevel);
                String batteryLevelString = batteryLevel + getString(R.string.percentage_symbol);
                binding.lockBatteryLevelTextView.setText(batteryLevelString);

                if (isChargerConnected(context)) {
                    binding.lockbatteryChargingStatusTextView.setText(R.string.charging);
                    binding.lockbatteryChargingStatusTextView.setVisibility(View.VISIBLE);
                } else {
                    binding.lockbatteryChargingStatusTextView.setText(R.string.discharging);
                    binding.lockbatteryChargingStatusTextView.setVisibility(View.INVISIBLE);
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        /*getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);*/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        InMobiSdk.init(LockAdsActivity.this, INMOBI_ACCOUNT_ID);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_lock_ads);

        myActivityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        this.context = getApplicationContext();

        // mark lock ads shown in SharedPreferences
        UserSessionManager userSessionManager = new UserSessionManager(getApplicationContext());
        userSessionManager.setLockAdsShowing(true);

        inMobiBanner = (InMobiBanner) findViewById(R.id.inmobi_banner);
        inMobiBanner.load();
        inMobiBanner.setListener(new InMobiBanner.BannerAdListener() {
            @Override
            public void onAdLoadSucceeded(InMobiBanner inMobiBanner) {

            }

            @Override
            public void onAdLoadFailed(InMobiBanner inMobiBanner, InMobiAdRequestStatus inMobiAdRequestStatus) {
                Log.e("onAdLoadFailed", inMobiAdRequestStatus.getMessage());
            }

            @Override
            public void onAdDisplayed(InMobiBanner inMobiBanner) {

            }

            @Override
            public void onAdDismissed(InMobiBanner inMobiBanner) {

            }

            @Override
            public void onAdInteraction(InMobiBanner inMobiBanner, Map<Object, Object> map) {

            }

            @Override
            public void onUserLeftApplication(InMobiBanner inMobiBanner) {

            }

            @Override
            public void onAdRewardActionCompleted(InMobiBanner inMobiBanner, Map<Object, Object> map) {

            }
        });

        getBatteryInformation();
        getCurrentSystemDateTime();
        getRamInformation();
        getCpuUsageInfo();

        // Register Time Change Receiver
        try {
            IntentFilter intentfilter = new IntentFilter(Intent.ACTION_TIME_CHANGED);
            intentfilter.addAction(Intent.ACTION_TIME_TICK);
            intentfilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            if (timeChangeReceiver != null)
                registerReceiver(timeChangeReceiver, intentfilter);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // Add Animation to SWIPE TO DISMISS TextView (Shimmer Effect)
        Shimmer shimmer = new Shimmer();
        shimmer.setRepeatCount(100)
                .setDuration(1000)
                .setStartDelay(300)
                .setDirection(Shimmer.ANIMATION_DIRECTION_LTR);
        shimmer.start(binding.swipeToDismiss);

        binding.closeLockScreenPopup.setOnClickListener(this);
    }

    private void getRamInformation() {
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        myActivityManager.getMemoryInfo(memInfo);
        long availableDeviceMemory = memInfo.availMem;
        long totalDeviceMemory = memInfo.totalMem;

        long usedDeviceMemory = totalDeviceMemory - availableDeviceMemory;
        double ratio = usedDeviceMemory / (double) totalDeviceMemory;
        double percentage = ratio * 100;
        int usedRamPercentage = (int) percentage;
        if (usedRamPercentage > USED_RAM_PERCENTAGE_THRESHOLD) {
            binding.lockramArcProgress.setProgress(usedRamPercentage);
            binding.lockramArcProgress.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.red));
            binding.lockramArcProgress.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            binding.lockramArcProgress.setProgress(usedRamPercentage);
        }

        LogUtil.d(TAG, "Available Memory " + String.valueOf(availableDeviceMemory) + ", Total Memory" + String.valueOf(totalDeviceMemory) + ", Percentage=" + usedRamPercentage);

    }

    private void getCurrentSystemDateTime() {
        try {

            calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat_dateOnly = new SimpleDateFormat(FORMAT_DATE_ONLY);
            String currentDate = dateFormat_dateOnly.format(calendar.getTime());
            SimpleDateFormat dateFormat_hoursMinutes = new SimpleDateFormat(FORMAT_HOUR_MINUTES);
            Date date = new Date();
            String timeValue = dateFormat_hoursMinutes.format(date);
            binding.textviewTime.setText(timeValue);
            binding.textviewDate.setText(currentDate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getBatteryInformation() {
        IntentFilter intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        if (battery_info_receiver != null)
            LockAdsActivity.this.registerReceiver(battery_info_receiver, intentfilter);
    }

    private void getCpuUsageInfo() {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("abi: ").append(Build.CPU_ABI).append("\n");
            if (new File("/proc/stat").exists()) {
                try {
                    BufferedReader br = new BufferedReader(
                            new FileReader(new File("/proc/stat")));
                    String aLine;
                    while ((aLine = br.readLine()) != null) {
                        sb.append(aLine + "\n");
                    }
                    if (br != null) {
                        br.close();
                    }
                } catch (IOException e) {
                    //set default progress as 40%
                    binding.lockCpuArcProgress.setProgress(40);
                    e.printStackTrace();
                }
            }
            Log.d(TAG, sb.toString());
            String[] lines = sb.toString().split("\n");
            String cpuLine = "";
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].contains("cpu "))
                    cpuLine = lines[i];
            }

            //now that we have obtained individual values, we separate them out
            String[] values = cpuLine.split(" ");
            String idleValue = "";
            int totalCpuTime = 0;
            for (int x = 1; x < values.length; x++) {
                Log.e("value[" + x + "]", values[x]);
                if (!values[x].isEmpty()) {
                    int currentCpuTime = Integer.parseInt(values[x].trim());
                    totalCpuTime += currentCpuTime;
                    if (x == 5)
                        idleValue = values[x];
                }

            }

            // sum up all the columns in the 1st line "cpu" : ( user + nice + system + idle + iowait + irq + softirq )
            // this will yield 100% of CPU time
            Log.e("totalCpuTime", totalCpuTime + "");
            //calculate the average percentage of total 'idle' out of 100% of CPU time :
            // ( user + nice + system + idle + iowait + irq + softirq ) = 100%
            // ( idle ) = X %
            int idleTime = Integer.parseInt(idleValue.trim());
            Log.e("idleTime", idleTime + "");
            // hence
            // average idle percentage X % = ( idle * 100 ) / ( user + nice + system + idle + iowait + irq + softirq )
            float avgIdlePercetage = (idleTime * 100) / (totalCpuTime);
            Log.e("Average Idle Percentage", avgIdlePercetage + " %");
            int averageUsagePercentage = (int) (100 - avgIdlePercetage);
            Log.e("CPU Usage", averageUsagePercentage + " %");
            binding.lockCpuArcProgress.setProgress(averageUsagePercentage);
        } catch (Exception e) {
            //set default progress as 40%
            binding.lockCpuArcProgress.setProgress(40);
            e.printStackTrace();
        }
    }

    private boolean isChargerConnected(Context context) {
        if (context != null) {
            Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if (intent != null) {
                int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
            }
            return false;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.closeLockScreenPopup) {
            finish();
            //mark lock ads as closed in SharedPreferences
            UserSessionManager userSessionManager = new UserSessionManager(getApplicationContext());
            userSessionManager.setLockAdsShowing(false);
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

            /*//start lock screen widget service
            Intent intent = new Intent(this, LockScreenWidgetService.class);
            startService(intent);*/
        }
    }

    @Override
    public void onBackPressed() {
        // do nothing if user presses back
    }

    @Override
    protected void onDestroy() {
        if (battery_info_receiver != null)
            LockAdsActivity.this.unregisterReceiver(battery_info_receiver);
        if(timeChangeReceiver != null)
            unregisterReceiver(timeChangeReceiver);

        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.d(TAG, "onStart()");
        //stop existing LockScreenWidgetService whenever Full Screen Widget is shown
        Intent lockScreenWidgetService = new Intent(context, LockScreenWidgetService.class);
        stopService(lockScreenWidgetService);
    }

    @Override
    protected void onStop() {
        Log.e("locked status => ", locked+"");
        if(locked)
        {
            Intent intent_lockScreenWidgetService = new Intent(context, LockScreenWidgetService.class);
            context.startService(intent_lockScreenWidgetService);
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        locked = km.inKeyguardRestrictedInputMode();
    }

}
