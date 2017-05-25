package com.receptix.batterybuddy.helper;

import android.util.Log;

import com.receptix.batterybuddy.BuildConfig;

/**
 * Created by futech on 25-May-17.
 */

public class LogUtil {

    public static void e(String TAG, String message)
    {
        if(BuildConfig.DEBUG)
        {
            Log.e(TAG, message);
        }
    }

    public static void d(String TAG, String message)
    {
        if(BuildConfig.DEBUG)
        {
            Log.d(TAG, message);
        }
    }

    public static void releaseMessage(String TAG, String message)
    {
        if(!BuildConfig.DEBUG)
            Log.e(TAG, message);
    }

}
