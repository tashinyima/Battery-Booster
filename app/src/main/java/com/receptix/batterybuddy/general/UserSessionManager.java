package com.receptix.batterybuddy.general;

import android.content.Context;
import android.content.SharedPreferences;

import static com.receptix.batterybuddy.helper.Constants.Preferences.IS_FIRST_TIME;
import static com.receptix.batterybuddy.helper.Constants.Preferences.IS_OPTIMIZED_NOW;
import static com.receptix.batterybuddy.helper.Constants.Preferences.PREFER_NAME;
import static com.receptix.batterybuddy.helper.Constants.Preferences.PRIVATE_MODE;

/**
 * Created by hello on 5/15/2017.
 */

public class UserSessionManager {

    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    Context context;

    public UserSessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor=sharedPreferences.edit();
    }

    public boolean isOptimized() {
        return sharedPreferences.getBoolean(IS_OPTIMIZED_NOW, false);
    }


    public void setIsOptimized(boolean isOptimizedNow) {
        editor.putBoolean(IS_OPTIMIZED_NOW,isOptimizedNow);
        editor.commit();
    }

    public boolean isFirstTime() {
        return sharedPreferences.getBoolean(IS_FIRST_TIME,true);
    }

    public void setIsFirstTime(boolean isFirstTime){
        editor.putBoolean(IS_FIRST_TIME,isFirstTime);
        editor.commit();
    }
}
