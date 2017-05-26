package com.receptix.batterybuddy.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.receptix.batterybuddy.helper.LogUtil;

import org.json.JSONObject;

import static com.receptix.batterybuddy.helper.Constants.Params.APP_NAME;
import static com.receptix.batterybuddy.helper.Constants.Params.REFERRER;
import static com.receptix.batterybuddy.helper.Constants.Params.REFERRER_JSON_OBJECT;
import static com.receptix.batterybuddy.helper.Constants.Params.STATUS_SUCCESS;
import static com.receptix.batterybuddy.helper.Constants.Urls.URL_TRACKING_OZOCK;

/**
 * Created by hello on 5/23/2017.
 */

public class InstallReferrerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        try {
            String referrer = intent.getStringExtra("referrer");
            Log.e("Referrer String", referrer);

            String url = URL_TRACKING_OZOCK ;

            final JsonObject referrerJsonObject = new JsonObject();
            referrerJsonObject.addProperty(REFERRER, referrer);
            referrerJsonObject.addProperty(APP_NAME, context.getPackageName());
            LogUtil.d(REFERRER_JSON_OBJECT, referrerJsonObject.toString());

            Ion.with(context)
                    .load(url)
                    .setJsonObjectBody(referrerJsonObject)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                String status = result.get("status").toString();
                                if (status.equalsIgnoreCase(STATUS_SUCCESS)) {
                                    LogUtil.d("Install_Referrer", "Success");
                                    /*//once successful, start sending this data daily to Pixel Server on a daily basis
                                    Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                                    alarmIntent.putExtra(REFERRER_JSON_OBJECT, referrerJsonObject.toString());
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
                                    AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                                    manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);*/
                                }
                            }
                        }
                    });
        } catch (Exception ex) {

            ex.printStackTrace();
        }


    }
}
