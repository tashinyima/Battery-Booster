package com.receptix.batterybuddy.receiver;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.receptix.batterybuddy.LockScreenWidgetService;
import com.receptix.batterybuddy.activities.BatteryAdActivity;
import com.receptix.batterybuddy.activities.LockAdsActivity;
import com.receptix.batterybuddy.helper.LogUtil;
import com.receptix.batterybuddy.helper.UserSessionManager;

/**
 * Created by zero1 on 5/8/2017.
 */

public class PowerConnectionReceiver extends BroadcastReceiver {
    private static final String TAG = "PowerConnectionReceiver";
    Context context;
    KeyguardManager keyguardManager;
    UserSessionManager userSessionManager;
    private static final int SCREEN_SHOW_DELAY = 2000;

    @Override
    public void onReceive(final Context context, Intent intent) {
        try {
            keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            boolean isLockedScreen = keyguardManager.inKeyguardRestrictedInputMode();

            userSessionManager = new UserSessionManager(context);

            LogUtil.d(TAG, "isLockedScreen : " + isLockedScreen);

            // show popup every time user connects charger
            //showScreen(isLockedScreen);
            /*
            SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_IS_ACTIVE, MODE_PRIVATE);
            if (sharedPreferences != null) {
                boolean isActive = sharedPreferences.getBoolean(IS_ACTIVE, false);
                if (!isActive) {
                    showScreen(isLockedScreen);
                    long lastScreenOnTimestamp = userSessionManager.getScreenOnTimestampPowerConnectionReceiver();
                    if (lastScreenOnTimestamp != 0) {
                        LogUtil.e("Last TimeStamp (PCR)", lastScreenOnTimestamp + "");
                        LogUtil.e("Current Timestamp (PCR)", System.currentTimeMillis() + "");
                        long currentTimeStamp = System.currentTimeMillis();
                        Date lastDate = new Date();
                        Date currentDate = new Date();
                        lastDate.setTime(lastScreenOnTimestamp);
                        currentDate.setTime(currentTimeStamp);
                        long diffMs = currentDate.getTime() - lastDate.getTime();
                        long diffSec = diffMs / 1000;
                        long elapsedMinutesSinceLastScreenOn = diffSec / 60;
                        long elapsedSecondsSinceLastScreenOn = diffSec % 60;
                        LogUtil.e("Difference is", elapsedMinutesSinceLastScreenOn + " mins, " + elapsedSecondsSinceLastScreenOn + " seconds");
                        if (elapsedMinutesSinceLastScreenOn >= SCREEN_LOCK_ADS_TIMER_VALUE_MINUTES) //24 hours have 1440 minutes
                        {
                            showScreen(isLockedScreen);
                        }
                    } else {
                        showScreen(isLockedScreen);
                    }
                    long currentTimeStamp = System.currentTimeMillis();
                    //save timestamp to SharedPreferences
                    userSessionManager.setScreenOnTimestampPowerConnectionReceiver(currentTimeStamp);
                }
            }*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.context = context;
    }

    /**
     * Show Activity depending on whether or not screen is locked.
     *
     * @param isLockedScreen true if Screen Locked (Keyguard enabled), false otherwise.
     */
    private void showScreen(boolean isLockedScreen) {
        if (!isLockedScreen) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent i = new Intent(context, BatteryAdActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            }, SCREEN_SHOW_DELAY );
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent intent_lockScreenWidgetService = new Intent(context, LockScreenWidgetService.class);
                    context.stopService(intent_lockScreenWidgetService);

                    Intent i = new Intent(context, LockAdsActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            }, SCREEN_SHOW_DELAY);
        }
    }
}
