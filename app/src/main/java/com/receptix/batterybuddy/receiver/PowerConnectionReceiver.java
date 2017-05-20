package com.receptix.batterybuddy.receiver;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.receptix.batterybuddy.BatteryAdActivity;
import com.receptix.batterybuddy.LockAdsActivity;
import com.receptix.batterybuddy.MainActivity;

import static android.content.Context.MODE_PRIVATE;
import static com.receptix.batterybuddy.helper.Constants.Params.APP_PACKAGE_NAME;
import static com.receptix.batterybuddy.helper.Constants.Params.BROADCAST_RECEIVER;
import static com.receptix.batterybuddy.helper.Constants.Params.FROM;
import static com.receptix.batterybuddy.helper.Constants.Params.IS_SCREEN_ON;
import static com.receptix.batterybuddy.helper.Constants.Preferences.IS_ACTIVE;
import static com.receptix.batterybuddy.helper.Constants.Preferences.PREFERENCES_IS_ACTIVE;

/**
 * Created by zero1 on 5/8/2017.
 */

public class PowerConnectionReceiver extends BroadcastReceiver {
    private static final String TAG = "PowerConnectionReceiver";
    Context context;
    boolean isScreenOn = false;
    KeyguardManager keyguardManager;

    @Override
    public void onReceive(final Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            isScreenOn = true;
        }
        try {
            Log.d(TAG, "isScreenOn = " + String.valueOf(isScreenOn));
            keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            boolean isLockedScreen = keyguardManager.inKeyguardRestrictedInputMode();
            Log.d(TAG, "isLockedScreen : " + isLockedScreen);

            SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_IS_ACTIVE, MODE_PRIVATE);
            if(sharedPreferences!=null)
            {
                boolean isActive = sharedPreferences.getBoolean(IS_ACTIVE, false);
                if(!isActive)
                {
                    if (!isLockedScreen) {
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                Intent i = new Intent(context, BatteryAdActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(i);
                            }
                        }, 2000);
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(context, LockAdsActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(i);
                            }
                        }, 2000);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        this.context = context;
        /*PackageManager pm = context.getPackageManager();
        Intent launchIntent = pm.getLaunchIntentForPackage(APP_PACKAGE_NAME);
        launchIntent.putExtra(FROM, BROADCAST_RECEIVER);
        launchIntent.putExtra(IS_SCREEN_ON, isScreenOn);
        context.startActivity(launchIntent);*/

        /*showMessage("charging"+ isScreenOn);*/
    }

    private void showMessage(String charging) {
        Toast.makeText(context, charging, Toast.LENGTH_SHORT).show();
    }
}
