package com.receptix.batterybuddy;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.receptix.batterybuddy.activities.LockAdsActivity;
import com.receptix.batterybuddy.helper.LogUtil;
import com.receptix.batterybuddy.helper.UserSessionManager;

import java.util.Date;

import static com.receptix.batterybuddy.helper.Constants.Params.SCREEN_LOCK_ADS_TIMER_VALUE_MINUTES;

/**
 * Created by futech on 23-May-17.
 */

public class ScreenListenerService extends Service {

    private UserSessionManager userSessionManager;
    private BroadcastReceiver mScreenStateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            userSessionManager = new UserSessionManager(context);
            if(userSessionManager!=null)
            {
                long lastScreenOnTimestamp = userSessionManager.getScreenOnTimestamp();
                LogUtil.e("lastScreenOnTimeStamp", lastScreenOnTimestamp+"");

                if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    //first we check if 24 hours have passed between the last time we showed the user the Lock Screen Ads Activity
                    if(lastScreenOnTimestamp!=0)
                    {
                        LogUtil.e("Last TimeStamp", lastScreenOnTimestamp+"");
                        LogUtil.e("Current Timestamp", System.currentTimeMillis()+"");
                        long currentTimeStamp = System.currentTimeMillis();
                        Date lastDate = new Date();
                        Date currentDate = new Date();
                        lastDate.setTime(lastScreenOnTimestamp);
                        currentDate.setTime(currentTimeStamp);
                        long diffMs = currentDate.getTime() - lastDate.getTime();
                        long diffSec = diffMs / 1000;
                        long elapsedMinutesSinceLastScreenOn = diffSec / 60;
                        long elapsedSecondsSinceLastScreenOn = diffSec % 60;
                        LogUtil.e("Difference is", elapsedMinutesSinceLastScreenOn+" mins, "+elapsedSecondsSinceLastScreenOn + " seconds");
                        if(elapsedMinutesSinceLastScreenOn >= SCREEN_LOCK_ADS_TIMER_VALUE_MINUTES) //24 hours have 1440 minutes
                        {
                            Intent i = new Intent(context, LockAdsActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                        }
                    }
                    // if last timestamp hasn't been saved yet
                    else
                    {
                        Intent i = new Intent(context, LockAdsActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }
                    long currentTimeStamp = System.currentTimeMillis();
                    //save timestamp to SharedPreferences
                    userSessionManager.setScreenOnTimestamp(currentTimeStamp);
                }
            }

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mScreenStateBroadcastReceiver, intentFilter);
        LogUtil.e("ScreenListener", "onCreate()");

    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mScreenStateBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Nothing should bind to this service
        return null;
    }
}
