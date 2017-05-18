package com.receptix.batterybuddy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import static com.receptix.batterybuddy.helper.Constants.Params.APP_PACKAGE_NAME;
import static com.receptix.batterybuddy.helper.Constants.Params.BROADCAST_RECEIVER;
import static com.receptix.batterybuddy.helper.Constants.Params.FROM;
import static com.receptix.batterybuddy.helper.Constants.Params.IS_SCREEN_ON;

/**
 * Created by zero1 on 5/8/2017.
 */

public class PowerConnectionReceiver extends BroadcastReceiver {
    Context context;
    boolean isScreenOn = false;
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            isScreenOn =true;
        }

        this.context = context;
        PackageManager pm = context.getPackageManager();
        Intent launchIntent = pm.getLaunchIntentForPackage(APP_PACKAGE_NAME);
        launchIntent.putExtra(FROM, BROADCAST_RECEIVER);
        launchIntent.putExtra(IS_SCREEN_ON, isScreenOn);
        context.startActivity(launchIntent);

        /*showMessage("charging"+ isScreenOn);*/
    }

    private void showMessage(String charging) {
        Toast.makeText(context, charging, Toast.LENGTH_SHORT).show();
    }
}
