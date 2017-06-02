package com.receptix.batterybuddy;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.receptix.batterybuddy.activities.LockAdsActivity;
import com.receptix.batterybuddy.helper.InternetUtils;
import com.receptix.batterybuddy.helper.LogUtil;
import com.receptix.batterybuddy.helper.MCrypt;
import com.receptix.batterybuddy.helper.UserSessionManager;

import java.util.Date;

import static com.receptix.batterybuddy.helper.Constants.JsonProperties.AUTH_KEY;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.DATA;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.DEVICE_ID;
import static com.receptix.batterybuddy.helper.Constants.Params.APP_NAME;
import static com.receptix.batterybuddy.helper.Constants.Params.FCM_TOKEN;
import static com.receptix.batterybuddy.helper.Constants.Params.SCREEN_LOCK_ADS_TIMER_VALUE_MINUTES;
import static com.receptix.batterybuddy.helper.Constants.Urls.URL_TRACKING_OZOCK_INSTALLED;
import static com.receptix.batterybuddy.helper.Constants.Urls.URL_UPDATE_FCM_TOKEN;

/**
 * Created by futech on 23-May-17.
 */

public class ScreenListenerService extends Service {

    private static final String TAG = ScreenListenerService.class.getSimpleName() ;
    private UserSessionManager userSessionManager;
    private String userDeviceId, authorizationKey = "";


    private BroadcastReceiver mScreenStateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            userSessionManager = new UserSessionManager(context);
            if (userSessionManager != null) {
                long lastScreenOnTimestamp = userSessionManager.getScreenOnTimestamp();
                LogUtil.e("lastScreenOnTimeStamp", lastScreenOnTimestamp + "");
                if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    if(InternetUtils.isInternetConnected(context))
                    {
                        try {
                            // first we check if install referrer data is sent once or not
                            boolean isReferrerDataSentOnce = userSessionManager.isReferrerDataSentOnce();
                            Log.e(TAG, "isReferrerDataSentOnce = "+ isReferrerDataSentOnce);
                            if (!isReferrerDataSentOnce) {
                                // if app has not sent Referrer Data even once, we send it via Network Call
                                getUserDeviceIdAndAuthKey(context);
                                String referrerJsonData = userSessionManager.getReferrerJsonData();
                                if(referrerJsonData!=null)
                                {
                                    //send Install Referrer Data to Server
                                    sendReferrerDataToServer(referrerJsonData, context);
                                }
                                else
                                {
                                    Log.e(TAG, "referrerData NOT AVAILABLE YET");
                                }
                            }
                            else
                            {
                                Log.e(TAG, "referrerData ALREADY SENT");
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();

                        }
                    }
                    else
                    {
                        Log.e(TAG, "No Internet Connection");
                    }

                    //first we check if 24 hours have passed between the last time we showed the user the Lock Screen Ads Activity
                    if (lastScreenOnTimestamp != 0) {
                        LogUtil.e("Last TimeStamp", lastScreenOnTimestamp + "");
                        LogUtil.e("Current Timestamp", System.currentTimeMillis() + "");
                        long currentTimeStamp = System.currentTimeMillis();
                        Date lastDate = new Date();
                        Date currentDate = new Date();
                        lastDate.setTime(lastScreenOnTimestamp);
                        currentDate.setTime(currentTimeStamp);
                        long diffMs = currentDate.getTime() - lastDate.getTime();
                        long diffSec = diffMs / 1000;
                        long elapsedMinutesSinceLastScreenOn = diffSec / 60;
                        long elapsedSecondsSinceLastScreenOn = diffSec % 60;
                        LogUtil.e("Difference is", elapsedMinutesSinceLastScreenOn + " mins, " + elapsedSecondsSinceLastScreenOn + " seconds");
                        if (elapsedMinutesSinceLastScreenOn >= SCREEN_LOCK_ADS_TIMER_VALUE_MINUTES) //24 hours have 1440 minutes
                        {
                            Intent i = new Intent(context, LockAdsActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                        }
                    }
                    // if last timestamp hasn't been saved yet
                    else {
                        Intent i = new Intent(context, LockAdsActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }
                    long currentTimeStamp = System.currentTimeMillis();
                    //save timestamp to SharedPreferences
                    userSessionManager.setScreenOnTimestamp(currentTimeStamp);
                }
            }

        }
    };

    private void getUserDeviceIdAndAuthKey(Context context) {
        // get device id
        userDeviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            // encrypt device Id to make Auth Key
            MCrypt mCrypt = new MCrypt();
            authorizationKey = MCrypt.bytesToHex(mCrypt.encrypt(userDeviceId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendReferrerDataToServer(String referrerJsonData, final Context context) {
        try {
            // print requestBody to Log
            Log.e(TAG, "install.php => " + referrerJsonData);
            //send Install Referrer Data to Server
            Ion.with(context)
                    .load(URL_TRACKING_OZOCK_INSTALLED)
                    .setBodyParameter("data", referrerJsonData)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            Log.e(TAG, "install.php => onCompleted()");
                            if (result != null) {
                                boolean hasStatus = result.has("status");
                                if (hasStatus) {
                                    String status = String.valueOf(result.get("status"));
                                    if (status.equalsIgnoreCase("1")) {
                                        // mark "referrerDataSentOnce" to true (so that install data is not sent again and again)
                                        userSessionManager.setReferrerDataSentOnce(true);
                                        SendFCMDataNow(context);
                                    }
                                }
                            }

                        }
                    });
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void SendFCMDataNow(Context context) {
        try {
            final UserSessionManager userSessionManager = new UserSessionManager(context);
            JsonObject jsonObject = new JsonObject();
            String fcmToken = userSessionManager.getToken();
            jsonObject.addProperty(FCM_TOKEN, fcmToken);
            jsonObject.addProperty(APP_NAME, context.getPackageName());
            jsonObject.addProperty(DEVICE_ID, userDeviceId);
            jsonObject.addProperty(AUTH_KEY, authorizationKey);
            // print requestBody to Log
            Log.e(TAG + " update_fcm.php =>", jsonObject.toString());
            Ion.with(context)
                    .load(URL_UPDATE_FCM_TOKEN)
                    .setBodyParameter(DATA, jsonObject.toString())
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            Log.e(TAG, "update_fcm.php => onCompleted()");
                            Log.e(TAG, "isReferrerDataSentOnce = "+ userSessionManager.isReferrerDataSentOnce());
                        }
                    });

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mScreenStateBroadcastReceiver, intentFilter);
        LogUtil.e("ScreenListener", "onCreate()");

    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mScreenStateBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Nothing should bind to this service
        return null;
    }
}
