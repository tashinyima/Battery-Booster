package com.receptix.batterybuddy.helper;

import android.content.Context;
import android.content.SharedPreferences;

import static com.receptix.batterybuddy.helper.Constants.Preferences.IS_FIRST_TIME;
import static com.receptix.batterybuddy.helper.Constants.Preferences.IS_ONE_DAY_FINISHED;
import static com.receptix.batterybuddy.helper.Constants.Preferences.IS_OPTIMIZED_NOW;
import static com.receptix.batterybuddy.helper.Constants.Preferences.LAST_TIMESTAMP_LOCK_ADS;
import static com.receptix.batterybuddy.helper.Constants.Preferences.LAST_TIMESTAMP_LOCK_ADS_POWER_CONNECTION_RECEIVER;
import static com.receptix.batterybuddy.helper.Constants.Preferences.PREFER_NAME;
import static com.receptix.batterybuddy.helper.Constants.Preferences.PRIVATE_MODE;

/**
 * Created by hello on 5/15/2017.
 */

public class UserSessionManager {

    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    Context context;
    private long screenOnTimestamp;

    public UserSessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public boolean isOptimized() {
        return sharedPreferences.getBoolean(IS_OPTIMIZED_NOW, false);
    }


    public void setIsOptimized(boolean isOptimizedNow) {
        editor.putBoolean(IS_OPTIMIZED_NOW, isOptimizedNow);
        editor.commit();
    }

    public boolean isFirstTime() {
        return sharedPreferences.getBoolean(IS_FIRST_TIME, true);
    }

    public void setIsFirstTime(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME, isFirstTime);
        editor.commit();
    }

    public boolean isOneDayFinished() {

        return sharedPreferences.getBoolean(IS_ONE_DAY_FINISHED, false);
    }

    public void setIsOneDayFinished(boolean isOneDayFinished) {

        editor.putBoolean(IS_ONE_DAY_FINISHED, isOneDayFinished);
        editor.commit();
    }

    public long getScreenOnTimestamp() {
        return sharedPreferences.getLong(LAST_TIMESTAMP_LOCK_ADS, 0);
    }

    public void setScreenOnTimestamp(long screenOnTimestamp) {
        editor.putLong(LAST_TIMESTAMP_LOCK_ADS, screenOnTimestamp);
        editor.commit();
    }

    public long getScreenOnTimestampPowerConnectionReceiver() {
        return sharedPreferences.getLong(LAST_TIMESTAMP_LOCK_ADS_POWER_CONNECTION_RECEIVER, 0);
    }

    public void setScreenOnTimestampPowerConnectionReceiver(long screenOnTimestampPowerConnectionReceiver) {
        editor.putLong(LAST_TIMESTAMP_LOCK_ADS_POWER_CONNECTION_RECEIVER, screenOnTimestampPowerConnectionReceiver);
        editor.commit();
    }
}
