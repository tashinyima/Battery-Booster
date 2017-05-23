package com.receptix.batterybuddy;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.receptix.batterybuddy.activities.LockAdsActivity;

/**
 * Created by futech on 23-May-17.
 */

public class ScreenListenerService extends Service {
    private BroadcastReceiver mScreenStateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Intent i = new Intent(context, LockAdsActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Intent i = new Intent(context, LockAdsActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                /*context.startActivity(i);*/
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenStateBroadcastReceiver, intentFilter);
        Log.e("ScreenListener", "onCreate()");

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
