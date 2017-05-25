package com.receptix.batterybuddy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.receptix.batterybuddy.helper.LogUtil;

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
            LogUtil.releaseMessage("Referrer String", referrer);
            String url = URL_TRACKING_OZOCK + referrer;
            LogUtil.d("Campaign Url", url);
            Ion.with(context)
                    .load(url)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                String status = result.get("status").toString();
                                if (status.equalsIgnoreCase(STATUS_SUCCESS)) {
                                    // Toast.makeText(context,"success",Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });

        } catch (Exception ex) {

            ex.printStackTrace();
        }


    }
}
