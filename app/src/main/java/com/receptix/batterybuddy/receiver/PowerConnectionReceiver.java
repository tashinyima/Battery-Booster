package com.receptix.batterybuddy.receiver;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.receptix.batterybuddy.activities.LockAdsActivity;
import com.receptix.batterybuddy.helper.LogUtil;
import com.receptix.batterybuddy.helper.UserSessionManager;

 /**
 * Created by zero1 on 5/8/2017.
 * Opens up Full Screen Lock Ads Activity {@link LockAdsActivity} whenever user connects/disconnects charger.
 */

public class PowerConnectionReceiver extends BroadcastReceiver {
    private static final String TAG = "PowerConnectionReceiver";
    Context context;
    UserSessionManager userSessionManager;
    private static final int SCREEN_SHOW_DELAY = 300;

    @Override
    public void onReceive(final Context context, Intent intent) {
        try {
            LogUtil.d(TAG, "onReceive()");
            this.context = context;
            userSessionManager = new UserSessionManager(context);
            // show full-screen popup every time user connects charger
            showFullScreenLockAds();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showFullScreenLockAds() {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(context, LockAdsActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(i);
                }
            }, SCREEN_SHOW_DELAY);
    }
}
