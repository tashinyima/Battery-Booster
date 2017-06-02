package com.receptix.batterybuddy.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static com.receptix.batterybuddy.helper.Constants.Preferences.IS_FIRST_TIME;
import static com.receptix.batterybuddy.helper.Constants.Preferences.IS_ONE_DAY_FINISHED;
import static com.receptix.batterybuddy.helper.Constants.Preferences.IS_OPTIMIZED_NOW;
import static com.receptix.batterybuddy.helper.Constants.Preferences.LAST_TIMESTAMP_LOCK_ADS;
import static com.receptix.batterybuddy.helper.Constants.Preferences.LAST_TIMESTAMP_LOCK_ADS_POWER_CONNECTION_RECEIVER;
import static com.receptix.batterybuddy.helper.Constants.Preferences.PREFER_NAME;
import static com.receptix.batterybuddy.helper.Constants.Preferences.PRIVATE_MODE;
import static com.receptix.batterybuddy.helper.Constants.fcm.AUTH_KEY;
import static com.receptix.batterybuddy.helper.Constants.fcm.DEVICEID;
import static com.receptix.batterybuddy.helper.Constants.fcm.FCM_TOKEN_APP;
import static com.receptix.batterybuddy.helper.Constants.fcm.PACKAGE;

/**
 * Created by hello on 5/15/2017.
 */

public class UserSessionManager {

    private static final String REFERRER_JSON_DATA = "referrerJsonData";
    private static final String FIREBASE_JSON_DATA = "firebaseTokenJsonData";
    private static final String IS_REFERRER_DATA_SENT_ONCE = "isReferrerDataSentOnce";
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    Context context;
    private long screenOnTimestamp;
    private String referrerJsonData;
    private String firebaseTokenJsonData;
    private boolean isReferrerDataSentOnce = false;

    public UserSessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public boolean isOptimized() {
        return sharedPreferences.getBoolean(IS_OPTIMIZED_NOW, false);
    }


    public void setIsOptimized(boolean isOptimizedNow) {
        editor.putBoolean(IS_OPTIMIZED_NOW, isOptimizedNow);
        editor.commit();
    }

    public boolean isFirstTime() {
        return sharedPreferences.getBoolean(IS_FIRST_TIME, true);
    }

    public void setIsFirstTime(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME, isFirstTime);
        editor.commit();
    }

    public boolean isOneDayFinished() {

        return sharedPreferences.getBoolean(IS_ONE_DAY_FINISHED, false);
    }

    public void setIsOneDayFinished(boolean isOneDayFinished) {

        editor.putBoolean(IS_ONE_DAY_FINISHED, isOneDayFinished);
        editor.commit();
    }

    public long getScreenOnTimestamp() {
        return sharedPreferences.getLong(LAST_TIMESTAMP_LOCK_ADS, 0);
    }

    public void setScreenOnTimestamp(long screenOnTimestamp) {
        editor.putLong(LAST_TIMESTAMP_LOCK_ADS, screenOnTimestamp);
        editor.commit();
    }

    public long getScreenOnTimestampPowerConnectionReceiver() {
        return sharedPreferences.getLong(LAST_TIMESTAMP_LOCK_ADS_POWER_CONNECTION_RECEIVER, 0);
    }

    public void setScreenOnTimestampPowerConnectionReceiver(long screenOnTimestampPowerConnectionReceiver) {
        editor.putLong(LAST_TIMESTAMP_LOCK_ADS_POWER_CONNECTION_RECEIVER, screenOnTimestampPowerConnectionReceiver);
        editor.commit();
    }

    public void setAuthKey(String authKey){

        editor.putString(AUTH_KEY,authKey);
        editor.commit();
    }

    public String getAuthKey(){

        return  sharedPreferences.getString(AUTH_KEY,null);

    }

    public void setToken(String authKey){
        editor.putString(FCM_TOKEN_APP,authKey);
        editor.commit();
    }

    public String getToken(){

        return  sharedPreferences.getString(FCM_TOKEN_APP,null);

    }

    public void setDeviceId(String authKey){

        editor.putString(DEVICEID,authKey);
        editor.commit();
    }

    public String getDeviceId(){

        return  sharedPreferences.getString(DEVICEID,null);

    }

    public void setPackageName(String authKey){

        editor.putString(PACKAGE,authKey);
        editor.commit();
    }

    public String getPackageName(){

        return  sharedPreferences.getString(PACKAGE,null);

    }

    public String getReferrerJsonData() {
        return sharedPreferences.getString(REFERRER_JSON_DATA, null);
    }

    public void setReferrerJsonData(String referrerJsonData) {
        this.referrerJsonData = referrerJsonData;
        editor.putString(REFERRER_JSON_DATA, referrerJsonData);
        editor.commit();
        Log.e("Saved referrerJsonData", referrerJsonData);
    }

    public String getFirebaseTokenJsonData() {
        return firebaseTokenJsonData;
    }

    public void setFirebaseTokenJsonData(String firebaseTokenJsonData) {
        this.firebaseTokenJsonData = firebaseTokenJsonData;
        editor.putString(FIREBASE_JSON_DATA, firebaseTokenJsonData);
        editor.commit();
    }

    public boolean isReferrerDataSentOnce() {
        return sharedPreferences.getBoolean(IS_REFERRER_DATA_SENT_ONCE, false);
    }

    public void setReferrerDataSentOnce(boolean referrerDataSentOnce) {
        isReferrerDataSentOnce = referrerDataSentOnce;
        editor.putBoolean(IS_REFERRER_DATA_SENT_ONCE, referrerDataSentOnce);
        editor.commit();
        Log.e("isReferrerDataSentOnce", String.valueOf(isReferrerDataSentOnce));
    }
}
