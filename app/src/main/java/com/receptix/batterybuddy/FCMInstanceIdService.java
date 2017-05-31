package com.receptix.batterybuddy;

import android.content.Context;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.receptix.batterybuddy.helper.LogUtil;
import com.receptix.batterybuddy.helper.MCrypt;

import static com.receptix.batterybuddy.helper.Constants.JsonProperties.AUTH_KEY;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.DATA;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.DEVICE_ID;
import static com.receptix.batterybuddy.helper.Constants.Params.APP_NAME;
import static com.receptix.batterybuddy.helper.Constants.Params.FCM_TOKEN;
import static com.receptix.batterybuddy.helper.Constants.Params.JSON_OBJECT;
import static com.receptix.batterybuddy.helper.Constants.Params.REFERRER;
import static com.receptix.batterybuddy.helper.Constants.Params.REFERRER_JSON_OBJECT;
import static com.receptix.batterybuddy.helper.Constants.Params.STATUS_SUCCESS;
import static com.receptix.batterybuddy.helper.Constants.Urls.URL_TRACKING_OZOCK_INSTALLED;
import static com.receptix.batterybuddy.helper.Constants.Urls.URL_UPDATE_FCM_TOKEN;

/**
 * Created by futech on 30-May-17.
 */

public class FCMInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = FCMInstanceIdService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendTokenToServer(refreshedToken);
    }

    private void sendTokenToServer(String refreshedToken) {
        try {

            Context context = getApplicationContext();

            String url = URL_UPDATE_FCM_TOKEN;
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
            LogUtil.e("onTokenRefresh " + JSON_OBJECT, dataObject.toString());

            // send to server
            Ion.with(context)
                    .load(url)
                    .setBodyParameter(DATA, jsonObject.toString())
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {

                            if(e!=null)
                                e.printStackTrace();

                            if (result != null) {
                                Log.e("onTokenRefresh", "result = " + result.toString());
                            }
                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
