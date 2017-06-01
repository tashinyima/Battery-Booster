package com.receptix.batterybuddy;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;

import com.receptix.batterybuddy.helper.LogUtil;

import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_LEVEL;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_SCALE;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_TEMPERATURE;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_TEMPERATURE_CONVERSION_UNIT;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.IS_BATTERY_PRESENT;
import static com.receptix.batterybuddy.helper.Constants.Params.INTENT_ACTION_UPDATE_WIDGET_BATTERY_SERVICE;
import static com.receptix.batterybuddy.helper.Constants.Params.USED_RAM_PERCENTAGE;

/**
 * Created by futech on 20-May-17.
 */

public class BatteryUpdateService extends IntentService {

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


                //calculate used RAM
                ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
                ActivityManager myActivityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                myActivityManager.getMemoryInfo(memInfo);
                long availableDeviceMemory = memInfo.availMem;
                long totalDeviceMemory = memInfo.totalMem;

                long usedDeviceMemory = totalDeviceMemory - availableDeviceMemory;
                double ratio = usedDeviceMemory / (double) totalDeviceMemory;
                double percentageUsed = ratio * 100;
                int usedRamPercentage = (int) percentageUsed;

                Intent intent1 = new Intent();
                intent1.setAction(INTENT_ACTION_UPDATE_WIDGET_BATTERY_SERVICE);
                intent1.putExtra(BATTERY_LEVEL, batteryLevel);
                intent1.putExtra(USED_RAM_PERCENTAGE, usedRamPercentage);
                sendBroadcast(intent1);
            }
        }
    };

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BatteryUpdateService(String name) {
        super(name);
    }

    public BatteryUpdateService() {
        super("BatteryUpdateService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //send broadcast
        LogUtil.e("BatteryUpdateService", "onHandleIntent()");
        getBatteryInformation(getApplicationContext());
    }

    private void getBatteryInformation(Context context) {
        IntentFilter intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        if (battery_info_receiver != null)
            context.registerReceiver(battery_info_receiver, intentfilter);
    }

    @Override
    public void onDestroy() {
        if (getApplicationContext() != null && battery_info_receiver != null)
            getApplicationContext().unregisterReceiver(battery_info_receiver);
        super.onDestroy();
    }

}
