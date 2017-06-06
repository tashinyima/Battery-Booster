package com.receptix.batterybuddy;

/**
 * Created by futech on 06-Jun-17.
 */

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiBanner;
import com.inmobi.sdk.InMobiSdk;
import com.receptix.batterybuddy.activities.LockAdsActivity;
import com.receptix.batterybuddy.helper.LogUtil;

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
import static com.receptix.batterybuddy.helper.Constants.DateFormats.FORMAT_DATE_MONTH_YEAR_HOUR_MINUTES;
import static com.receptix.batterybuddy.helper.Constants.DateFormats.FORMAT_FULL_LENGTH_DAY;

/**
 * Created on 2/20/2016.
 */
public class LockScreenTextService extends Service {

    private static final String TAG = LockScreenTextService.class.getSimpleName();
    private BroadcastReceiver mReceiver;
    private boolean isShowing = false;
    Calendar calendar;
    ActivityManager myActivityManager;
    Context context;
    int USED_RAM_PERCENTAGE_THRESHOLD = 70;
    InMobiBanner inMobiBanner;
    ArcProgress lockbatteryArcProgress;
    TextView lockBatteryLevelTextView;
    TextView lockbatteryChargingStatusTextView;

    ArcProgress lockramArcProgress;

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
                lockbatteryArcProgress.setSuffixText(getString(R.string.percentage_symbol));
                lockbatteryArcProgress.setProgress(batteryLevel);
                String batteryLevelString = batteryLevel + getString(R.string.percentage_symbol);
                lockBatteryLevelTextView.setText(batteryLevelString);

                if (isChargerConnected(context)) {
                    lockbatteryChargingStatusTextView.setText(R.string.charging);
                    lockbatteryChargingStatusTextView.setVisibility(View.VISIBLE);
                } else {
                    lockbatteryChargingStatusTextView.setText(R.string.discharging);
                    lockbatteryChargingStatusTextView.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    private WindowManager windowManager;
    private TextView textview;
    RelativeLayout widgetLayout;
    WindowManager.LayoutParams params;

    @Override
    public void onCreate() {

        super.onCreate();

        context = getApplicationContext();

        Log.e(TAG, "onCreate()");

        InMobiSdk.init(getApplicationContext(), INMOBI_ACCOUNT_ID);

        windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        widgetLayout = (RelativeLayout) layoutInflater.inflate(R.layout.widget_lock_ads, null);

        myActivityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        lockbatteryArcProgress = (ArcProgress) widgetLayout.findViewById(R.id.lockbatteryArcProgress);
        lockBatteryLevelTextView = (TextView) widgetLayout.findViewById(R.id.lockBatteryLevelTextView);
        lockbatteryChargingStatusTextView =  (TextView) widgetLayout.findViewById(R.id.lockbatteryChargingStatusTextView);
        lockramArcProgress = (ArcProgress) widgetLayout.findViewById(R.id.lockramArcProgress);

        //set parameters for the textview
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                750,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_FULLSCREEN,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP;

        Toast.makeText(context, "Service onCreate()", Toast.LENGTH_SHORT).show();

        //Register receiver for determining screen off and if user is present
        mReceiver = new LockScreenStateReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);

        registerReceiver(mReceiver, filter);

        try {
        getBatteryInformation();
        getCurrentSystemDateTime();
        getRamInformation();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        inMobiBanner = (InMobiBanner) widgetLayout.findViewById(R.id.inmobi_banner);
        inMobiBanner.load();
        Log.e(TAG, "inMobiBanner.load()");
        inMobiBanner.setListener(new InMobiBanner.BannerAdListener() {
            @Override
            public void onAdLoadSucceeded(InMobiBanner inMobiBanner) {
                Log.e(TAG, "inMobiBanner.onAdLoadSucceeded");
            }

            @Override
            public void onAdLoadFailed(InMobiBanner inMobiBanner, InMobiAdRequestStatus inMobiAdRequestStatus) {
                Log.e(TAG, "onAdLoadFailed" + inMobiAdRequestStatus.getMessage());
            }

            @Override
            public void onAdDisplayed(InMobiBanner inMobiBanner) {
                Log.e(TAG, "inMobiBanner.onAdDisplayed");
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
            lockramArcProgress.setProgress(usedRamPercentage);
            lockramArcProgress.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.red));
            lockramArcProgress.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            lockramArcProgress.setProgress(usedRamPercentage);
        }

        LogUtil.d(TAG, "MEM=" + String.valueOf(availableDeviceMemory) + "Total Ram=" + String.valueOf(totalDeviceMemory) + "Percentage=" + usedRamPercentage);

    }

    private void getCurrentSystemDateTime() {
        try {
            calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE_MONTH_YEAR_HOUR_MINUTES);
            String currentDate = sdf.format(calendar.getTime());
            SimpleDateFormat sdf_ = new SimpleDateFormat(FORMAT_FULL_LENGTH_DAY);
            Date date = new Date();
            String dayName = sdf_.format(date);
            /*binding.lockdatetv.setText("" + dayName + " " + currentDate + "");*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getBatteryInformation() {
        IntentFilter intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        if (battery_info_receiver != null)
            registerReceiver(battery_info_receiver, intentfilter);
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public class LockScreenStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    //if screen is turn off show the textview
                    if (!isShowing) {
                        windowManager.addView(widgetLayout, params);
                        isShowing = true;
                    }
                } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                    //Handle resuming events if user is present/screen is unlocked remove the textview immediately
                    if (isShowing) {
                        windowManager.removeViewImmediate(widgetLayout);
                        isShowing = false;
                    }
                }
            }
            catch (Exception e)
            {
            e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        try {
            //unregister receiver when the service is destroy
            if (mReceiver != null) {
                unregisterReceiver(mReceiver);
            }

            if (battery_info_receiver != null)
                unregisterReceiver(battery_info_receiver);

            Log.e(TAG, "onDestroy()");

            //remove view if it is showing and the service is destroy
            if (isShowing) {
                windowManager.removeViewImmediate(widgetLayout);
                isShowing = false;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        super.onDestroy();
    }

}
