package com.receptix.batterybuddy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

/**
 * Created by zero1 on 5/8/2017.
 */

public class PowerConnectionReceiver extends BroadcastReceiver {
    Context context;
    boolean isSceenOn = false;
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            isSceenOn=true;
        }

        this.context = context;
        PackageManager pm = context.getPackageManager();
        Intent launchIntent = pm.getLaunchIntentForPackage("com.receptix.batterybuddy");
        launchIntent.putExtra("from", "broadcast");
        launchIntent.putExtra("isScreenOn",isSceenOn);
        context.startActivity(launchIntent);

        showMessage("charging"+isSceenOn);
    }

    private void showMessage(String charging) {
        Toast.makeText(context, charging, Toast.LENGTH_SHORT).show();
    }
}
