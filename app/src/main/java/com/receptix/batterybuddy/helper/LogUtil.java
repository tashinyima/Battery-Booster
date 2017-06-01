package com.receptix.batterybuddy.helper;

import android.util.Log;

import com.receptix.batterybuddy.BuildConfig;

/**
 * Created by futech on 25-May-17.
 */

public class LogUtil {

    public static void e(String TAG, String message) {
        // only show log messages for Debug Build
        if (BuildConfig.DEBUG) {
            Log.e(TAG, message);
        }
    }

    public static void d(String TAG, String message) {
        // only show log messages for Debug Build
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message);
        }
    }

    public static void releaseMessage(String TAG, String message) {
        // only show log messages for Release Build (when observing install referrer campaign parameters)
        if (!BuildConfig.DEBUG)
            Log.e(TAG, message);
    }

}
