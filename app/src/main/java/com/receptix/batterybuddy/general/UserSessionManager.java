package com.receptix.batterybuddy.general;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hello on 5/15/2017.
 */

public class UserSessionManager {


    private static final String IS_OPTIMIZED_NOW = "is_optimized";

    private static final String PREFER_NAME = "AndroidExamplePref";
    private static final String IS_FIRST_TIME="is_first_time";

    public SharedPreferences sharedPreferences;
    // Editor reference for Shared preferences
    public SharedPreferences.Editor editor;
    // Context
    Context context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    public UserSessionManager(Context context) {

        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor=sharedPreferences.edit();

    }

    public  boolean getIsOptimizedNow(){

        return sharedPreferences.getBoolean(IS_OPTIMIZED_NOW,false);

    }


    public void setIsOptimizedNow(boolean isOptimizedNow){

        editor.putBoolean(IS_OPTIMIZED_NOW,isOptimizedNow);
        editor.commit();

    }

    public boolean  getIsFirstTime(){

        return sharedPreferences.getBoolean(IS_FIRST_TIME,false);

    }

    public void setIsFirstTime(boolean isFirstTime){

        editor.putBoolean(IS_FIRST_TIME,isFirstTime);
        editor.commit();


    }



}
