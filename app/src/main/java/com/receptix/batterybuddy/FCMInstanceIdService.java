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
            jsonObject.addProperty(APP_NAME, context.getPackageName());
            // get device id
            String userDeviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            jsonObject.addProperty(DEVICE_ID, userDeviceId);

            // add encoded base64 String
            String encodedDeviceId = Base64.encodeToString(userDeviceId.getBytes(),Base64.NO_WRAP| Base64.URL_SAFE);;

            //add firebase token to JSON Object
            jsonObject.addProperty(FCM_TOKEN, refreshedToken);
            jsonObject.addProperty(AUTH_KEY, encodedDeviceId);

            LogUtil.e("onTokenRefresh " + JSON_OBJECT, jsonObject.toString());

            Ion.with(context)
                    .load(url)
                    .setBodyParameter(DATA, jsonObject.toString())
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {

//                            if (result != null) {
//                                String status = result.get("status").toString();
//                                if (status.equalsIgnoreCase(STATUS_SUCCESS)) {
//                                    LogUtil.d("Install_Referrer", "Success");
//
//
//                                }
//                            }
                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
