package com.receptix.batterybuddy.activities;

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
import android.net.Uri;
import android.os.Bundle;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.receptix.batterybuddy.R;
import com.receptix.batterybuddy.fragments.ChargeFragment;
import com.receptix.batterybuddy.fragments.HomeFragment;
import com.receptix.batterybuddy.fragments.RankFragment;
import com.receptix.batterybuddy.helper.AppKey;
import com.receptix.batterybuddy.helper.LogUtil;
import com.receptix.batterybuddy.helper.MCrypt;
import com.receptix.batterybuddy.receiver.AlarmReceiver;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.receptix.batterybuddy.helper.Constants.AuthKey.AUTH_KEY;
import static com.receptix.batterybuddy.helper.Constants.Preferences.IS_ACTIVE;
import static com.receptix.batterybuddy.helper.Constants.Preferences.PREFERENCES_IS_ACTIVE;
import static com.receptix.batterybuddy.helper.Constants.Urls.URL_EMAIL_ADDRESS_SUPPORT;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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
