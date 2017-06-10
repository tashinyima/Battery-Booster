package com.receptix.batterybuddy;

/**
 * Created by futech on 06-Jun-17.
 */

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiBanner;
import com.inmobi.sdk.InMobiSdk;
import com.receptix.batterybuddy.helper.LogUtil;

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

/**
 * Created on 2/20/2016.
 */
public class LockScreenWidgetService extends Service {

    private static final String TAG = LockScreenWidgetService.class.getSimpleName();
    Calendar calendar;
    ActivityManager myActivityManager;
    Context context;
    int USED_RAM_PERCENTAGE_THRESHOLD = 70;
    InMobiBanner inMobiBanner;

    TextView lockBatteryLevelTextView;
    TextView lockbatteryChargingStatusTextView;
    TextView textview_time, textView_date;
    TextView textView_closeLockScreenWidget;

    ArcProgress lockramArcProgress;
    ArcProgress lockbatteryArcProgress;
    ArcProgress lockCpuArcProgress;

    LinearLayout widgetLayout;
    WindowManager.LayoutParams params;
    private BroadcastReceiver mReceiver;
    private boolean isWidgetAdded = false;
    private WindowManager windowManager;
    private TextView textview;
    private BroadcastReceiver timeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //update time value on time change
            getCurrentSystemDateTime();
            // update RAM information on time change
            getRamInformation();
            // update CPU Usage Information
            getCpuUsageInfo();
        }
    };
    /**
     * Broadcast Receiver for Battery Information
     */
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate()");

        context = getApplicationContext();

        InMobiSdk.init(getApplicationContext(), INMOBI_ACCOUNT_ID);
        myActivityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        // Inflate Widget Layout from XML File
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        widgetLayout = (LinearLayout) layoutInflater.inflate(R.layout.widget_lock_ads, null);
        // find Views By ID
        lockbatteryArcProgress = (ArcProgress) widgetLayout.findViewById(R.id.lockbatteryArcProgress);
        lockBatteryLevelTextView = (TextView) widgetLayout.findViewById(R.id.lockBatteryLevelTextView);
        lockbatteryChargingStatusTextView = (TextView) widgetLayout.findViewById(R.id.lockbatteryChargingStatusTextView);
        lockramArcProgress = (ArcProgress) widgetLayout.findViewById(R.id.lockramArcProgress);
        textview_time = (TextView) widgetLayout.findViewById(R.id.textview_time);
        textView_date = (TextView) widgetLayout.findViewById(R.id.textview_date);
        textView_closeLockScreenWidget = (TextView) widgetLayout.findViewById(R.id.close_lock_screen_popup);
        lockCpuArcProgress = (ArcProgress) widgetLayout.findViewById(R.id.lockCpuArcProgress);

        //set parameters for the Widget layout (manually set height to 800 pixels)
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                800,
                // Allows the view to be on top of the StatusBar
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                // Keeps the button presses from going to the background window
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        // Enables the view to receive touch events
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        // Draws over status bar
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);

        //set transparency and gravity of Widget
        params.alpha = (float) 0.9;
        params.gravity = Gravity.TOP;


        // Register receiver for determining screen off and if user is present
        try {
            mReceiver = new LockScreenStateReceiver();
            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            registerReceiver(mReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Register Time Change Receiver
        try {
            IntentFilter intentfilter = new IntentFilter(Intent.ACTION_TIME_CHANGED);
            intentfilter.addAction(Intent.ACTION_TIME_TICK);
            intentfilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            if (timeChangeReceiver != null)
                registerReceiver(timeChangeReceiver, intentfilter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Get Information to be displayed on the Widget
        try {
            getBatteryInformation();
            getCurrentSystemDateTime();
            getRamInformation();
            getCpuUsageInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // keep the widget hidden initially
        if (!BuildConfig.DEBUG)
            widgetLayout.setVisibility(View.GONE);

        // add Widget to Lock Screen
        windowManager.addView(widgetLayout, params);
        isWidgetAdded = true;

        // Load InMobi Advertisement
        inMobiBanner = (InMobiBanner) widgetLayout.findViewById(R.id.inmobi_banner);
        inMobiBanner.load();
        Log.e(TAG, "inMobiBanner.load()");
        inMobiBanner.setListener(new InMobiBanner.BannerAdListener() {
            @Override
            public void onAdLoadSucceeded(InMobiBanner inMobiBanner) {
                Log.e(TAG, "inMobiBanner.onAdLoadSucceeded 1496930154754");

                // Show Widget only if a PIN, pattern or password is set or a SIM card is locked (after successful ad loading).
                boolean isKeyguardEnabled = ((KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE)).isKeyguardSecure();
                if (isKeyguardEnabled) {
                    widgetLayout.setVisibility(View.VISIBLE);
                    LogUtil.d(TAG, "Widget => setVisibility(VISIBLE)");
                } else
                // keep widget hidden if keyguard is not secure (Swipe to Unlock set for Lock Screen)
                {
                    if (!BuildConfig.DEBUG)
                        widgetLayout.setVisibility(View.GONE);
                    LogUtil.d(TAG, "Widget => setVisibility(GONE)");
                }

            }

            @Override
            public void onAdLoadFailed(InMobiBanner inMobiBanner, InMobiAdRequestStatus inMobiAdRequestStatus) {
                Log.e(TAG, "onAdLoadFailed => 1496930154754 =>" + inMobiAdRequestStatus.getMessage());
                // hide widget when ad load fails (new ad widget is created everytime screen if turned off (when keyguard is secure))
                if (!BuildConfig.DEBUG)
                    widgetLayout.setVisibility(View.GONE);

                LogUtil.d(TAG, "Widget => setVisibility(GONE)");
            }

            @Override
            public void onAdDisplayed(InMobiBanner inMobiBanner) {
                Log.e(TAG, "inMobiBanner.onAdDisplayed => 1496930154754");
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

        textView_closeLockScreenWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                widgetLayout.setVisibility(View.GONE);
            }
        });
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
                    lockCpuArcProgress.setProgress(40);
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
            lockCpuArcProgress.setProgress(averageUsagePercentage);
        } catch (Exception e) {
            //set default progress as 40%
            lockCpuArcProgress.setProgress(40);
            e.printStackTrace();
        }
    }


    /**
     * Get Device RAM information - used RAM percentage.
     */
    private void getRamInformation() {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCurrentSystemDateTime() {
        try {
            calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat_dateOnly = new SimpleDateFormat(FORMAT_DATE_ONLY);
            String currentDate = dateFormat_dateOnly.format(calendar.getTime());
            SimpleDateFormat dateFormat_hoursMinutes = new SimpleDateFormat(FORMAT_HOUR_MINUTES);
            Date date = new Date();
            String timeValue = dateFormat_hoursMinutes.format(date);
            textview_time.setText(timeValue);
            textView_date.setText(currentDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getBatteryInformation() {
        try {
            IntentFilter intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            if (battery_info_receiver != null)
                registerReceiver(battery_info_receiver, intentfilter);
        } catch (Exception e) {
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
    public void onDestroy() {
        try {
            //unregister all registered receivers
            if (mReceiver != null) {
                unregisterReceiver(mReceiver);
            }
            if (battery_info_receiver != null)
                unregisterReceiver(battery_info_receiver);
            if (timeChangeReceiver != null)
                unregisterReceiver(timeChangeReceiver);
            //remove view if it is showing and the service is destroyed
            if (isWidgetAdded) {
                windowManager.removeViewImmediate(widgetLayout);
                isWidgetAdded = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG, "onDestroy()");
        super.onDestroy();
    }

    /**
     * Broadcast Receiver for Device Awake Status (Lock Screen Unlocked or not).
     * Service handles the visibility of Widget View here.
     */
    public class LockScreenStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                    //If user is present/screen is unlocked, remove the Widget immediately
                    if (isWidgetAdded) {
                        windowManager.removeViewImmediate(widgetLayout);
                        isWidgetAdded = false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
