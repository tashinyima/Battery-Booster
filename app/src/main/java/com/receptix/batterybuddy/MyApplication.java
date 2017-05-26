package com.receptix.batterybuddy;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.inmobi.sdk.InMobiSdk;
import com.receptix.batterybuddy.activities.LockAdsActivity;
import com.receptix.batterybuddy.helper.LogUtil;

import static com.receptix.batterybuddy.helper.Constants.BannerPlacementIds.INMOBI_ACCOUNT_ID;

/**
 * Created by futech on 23-May-17.
 */

public class MyApplication extends MultiDexApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.e("MyApplication", "onCreate()");
        // initialize InMobi SDK
        InMobiSdk.init(this, INMOBI_ACCOUNT_ID);
        InMobiSdk.setLogLevel(InMobiSdk.LogLevel.DEBUG);
    }
}
