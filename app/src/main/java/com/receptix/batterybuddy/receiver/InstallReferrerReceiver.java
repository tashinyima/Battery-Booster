package com.receptix.batterybuddy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by hello on 5/23/2017.
 */

public class InstallReferrerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String referrer = intent.getStringExtra("referrer");
        Log.d("SDFSD", referrer);

    }
}
