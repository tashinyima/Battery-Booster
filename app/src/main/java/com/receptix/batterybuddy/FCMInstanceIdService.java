package com.receptix.batterybuddy;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.receptix.batterybuddy.helper.MCrypt;
import com.receptix.batterybuddy.helper.UserSessionManager;

import static com.receptix.batterybuddy.helper.Constants.JsonProperties.AUTH_KEY;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.DATA;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.DEVICE_ID;
import static com.receptix.batterybuddy.helper.Constants.Params.APP_NAME;
import static com.receptix.batterybuddy.helper.Constants.Params.FCM_TOKEN;
import static com.receptix.batterybuddy.helper.Constants.Params.JSON_OBJECT;
import static com.receptix.batterybuddy.helper.Constants.Urls.URL_UPDATE_FCM_TOKEN;

/**
 * Created by futech on 30-May-17.
 */

public class FCMInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = FCMInstanceIdService.class.getSimpleName();

    UserSessionManager userSessionManager;

    @Override
    public void onTokenRefresh() {
        Context context = getApplicationContext();
        userSessionManager = new UserSessionManager(context);
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed FCM token: " + refreshedToken);
        saveTokenToSharedPreferences(refreshedToken);
        updateFcmTokenOnServer(refreshedToken);
    }

    private void updateFcmTokenOnServer(String refreshedToken) {
        try {
            Context context = getApplicationContext();
            JsonObject jsonObject = new JsonObject();
            //add firebase token to JSON Object
            jsonObject.addProperty(FCM_TOKEN, refreshedToken);
            // package name
            jsonObject.addProperty(APP_NAME, context.getPackageName());
            // get device id
            String userDeviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            jsonObject.addProperty(DEVICE_ID, userDeviceId);
            try {
                // encrypt device Id to make Auth Key
                MCrypt mCrypt = new MCrypt();
                String authorizationKey = MCrypt.bytesToHex(mCrypt.encrypt(userDeviceId));
                jsonObject.addProperty(AUTH_KEY, authorizationKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
            JsonObject dataObject = new JsonObject();
            dataObject.add(DATA, jsonObject);
            Log.e(TAG + "__" + "update_fcm.php => " + JSON_OBJECT, dataObject.toString());
            // send to server
            Ion.with(context)
                    .load(URL_UPDATE_FCM_TOKEN)
                    .setBodyParameter(DATA, jsonObject.toString())
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            Log.d(TAG, "update_fcm.php => onCompleted()");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveTokenToSharedPreferences(String refreshedToken) {
        try {
            Context context = getApplicationContext();
            userSessionManager = new UserSessionManager(context);
            // save firebase token to SharedPreferences
            userSessionManager.setToken(refreshedToken);
            Log.e(TAG, "FCM Token Saved for Sending Later.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
