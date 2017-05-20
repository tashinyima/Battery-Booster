package com.receptix.batterybuddy.activities;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.BatteryManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.inmobi.ads.InMobiBanner;
import com.inmobi.sdk.InMobiSdk;
import com.receptix.batterybuddy.R;
import com.receptix.batterybuddy.databinding.ActivityLockAdsBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_LEVEL;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_SCALE;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_TEMPERATURE;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_TEMPERATURE_CONVERSION_UNIT;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.IS_BATTERY_PRESENT;
import static com.receptix.batterybuddy.helper.Constants.DateFormats.FORMAT_DATE_MONTH_YEAR_HOUR_MINUTES;
import static com.receptix.batterybuddy.helper.Constants.DateFormats.FORMAT_FULL_LENGTH_DAY;

public class LockAdsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LockAdsActivity.class.getSimpleName();
    Calendar calendar;
    ActivityManager myActivityManager;
    Context context;
    InMobiBanner bannerAd;
    ActivityLockAdsBinding binding;
    int USED_RAM_PERCENTAGE_THRESHOLD = 70;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lock_ads);
        
        myActivityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        
        this.context = getApplicationContext();

        // TODO: 19-May-17 Update Code for InMobi ads from Sample
        // initialize InMobiSDK and load ad
        bannerAd = (InMobiBanner) findViewById(R.id.banner);
        InMobiSdk.init(LockAdsActivity.this, "4a38c3c40747428fa346cb0456d9034f");
        bannerAd.load();

        InitializeData();

        binding.closeLockScreenPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void InitializeData() {
        getBatteryInformation();
        getCurrentSystemDateTime();
        getRamInformation();
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

        Log.d(TAG, "MEM=" + String.valueOf(availableDeviceMemory) + "Total Ram=" + String.valueOf(totalDeviceMemory) + "Percentage=" + usedRamPercentage);

    }

    private void getCurrentSystemDateTime() {
        try {
            calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE_MONTH_YEAR_HOUR_MINUTES);
            String currentDate = sdf.format(calendar.getTime());
            SimpleDateFormat sdf_ = new SimpleDateFormat(FORMAT_FULL_LENGTH_DAY);
            Date date = new Date();
            String dayName = sdf_.format(date);
            binding.lockdatetv.setText("" + dayName + " " + currentDate + "");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void getBatteryInformation() {
        IntentFilter intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        if(battery_info_receiver!=null)
            LockAdsActivity.this.registerReceiver(battery_info_receiver, intentfilter);
    }

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
                binding.lockbatteryArcProgress.setSuffixText((char) 0x00b0 + "C");
                binding.lockbatteryArcProgress.setProgress(batteryLevel);
                String batteryLevelString = batteryLevel + getString(R.string.percentage_symbol);
                binding.lockBatteryLevelTextView.setText(batteryLevelString);

                if (isChargerConnected(context)) {
                    binding.lockbatteryChargingStatusTextView.setText(R.string.charging);
                    binding.lockbatteryChargingStatusTextView.setVisibility(View.VISIBLE);
                } else {
                    binding.lockbatteryChargingStatusTextView.setText(R.string.discharging);
                    binding.lockbatteryChargingStatusTextView.setVisibility(View.VISIBLE);
                }
            }

        }
    };

    private boolean isChargerConnected(Context context) {
        if(context!=null)
        {
            Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if(intent!=null)
            {
                int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
            }
            return false;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        /*
        showMessage("I am Clicked man");
        finish();*/
    }

    @Override
    public void onBackPressed() {
        // do nothing if user presses back
    }

    private void showMessage(String s) {
        /*Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();*/
    }
}
