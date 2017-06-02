package com.receptix.batterybuddy;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.clevertap.android.sdk.CleverTapAPI;
import com.facebook.stetho.Stetho;
import com.inmobi.sdk.InMobiSdk;
import com.receptix.batterybuddy.helper.LogUtil;

import static com.receptix.batterybuddy.helper.Constants.BannerPlacementIds.INMOBI_ACCOUNT_ID;

/**
 * Created by futech on 23-May-17.
 */

public class MyApplication extends com.clevertap.android.sdk.Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        // From CleverTap Documentation -
        // If you have a custom Application class, call ActivityLifecycleCallback.register(this);
        // before super.onCreate() in your class
        ActivityLifecycleCallback.register(this);

        LogUtil.e("MyApplication", "onCreate()");
        // initialize InMobi SDK
        InMobiSdk.init(this, INMOBI_ACCOUNT_ID);
        InMobiSdk.setLogLevel(InMobiSdk.LogLevel.DEBUG);
        if (BuildConfig.DEBUG) {
            // During development, we set the SDK to debug mode,
            // in order to log warnings or other important messages to Android logging system.
            CleverTapAPI.setDebugLevel(1); // optional
        }
    }
}
