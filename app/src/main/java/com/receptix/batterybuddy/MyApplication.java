package com.receptix.batterybuddy;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by futech on 23-May-17.
 */

public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
