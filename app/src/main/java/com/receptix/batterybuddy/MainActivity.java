package com.receptix.batterybuddy;

import android.Manifest;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.receptix.batterybuddy.adapter.HomePagersAdapter;
import com.receptix.batterybuddy.charge.ChargeFragment;
import com.receptix.batterybuddy.home.HomeFragment;
import com.receptix.batterybuddy.optimizeractivity.AfterOptimizerActivity;
import com.receptix.batterybuddy.optimizeractivity.OptimizerActivity;
import com.receptix.batterybuddy.rank.RankFragment;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 101;
    private static final String TAG = "BatteryBuddy";
    static WindowManager.LayoutParams params;
    static WindowManager wm;
    static ViewGroup lockScreen;
    NotificationCompat.Builder myBuilder;
    Intent intent;
    PendingIntent pintent;
    NotificationManager nmanager;
    KeyguardManager keyguard;
    Toolbar toolbar;


    private Fragment fragment;
    private FragmentManager fragmentManager;
    int NotificationId =999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();

        try {

            boolean isScreenOn = bundle.getBoolean("isScreenOn");
            Log.d(TAG, String.valueOf(isScreenOn));
            keyguard = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
            boolean isLockedScreen = keyguard.inKeyguardRestrictedInputMode();
            Log.d(TAG, "isLockedScren: " + isLockedScreen);


            if (!isLockedScreen) {

                String from = bundle.getString("from");
                if (from.equalsIgnoreCase("broadcast")) {

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            startActivity(new Intent(getApplicationContext(), BatteryAdActivity.class));
                            finish();
                        }
                    }, 4000);


                }
            } else {

                String from = bundle.getString("from");
                if (from.equalsIgnoreCase("broadcast")) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            startActivity(new Intent(getApplicationContext(), LockAdsActivity.class));
                            finish();

                        }
                    },4000);


                }

            }


        } catch (Exception ex) {

        }

        getPermission();

        sendCustomNotification();
        //  sendNotification();
        setupToolBar(getString(R.string.batterybuddy));


        initView();
        AddBottomNavigation();
    }

    private void setupToolBar(String string) {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(string.toUpperCase());
    }

    private void AddBottomNavigation() {

        fragmentManager = getSupportFragmentManager();
        fragment = new HomeFragment();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.main_container, fragment).commit();
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.action_charge:
                        fragment = new ChargeFragment();
                        break;
                    case R.id.action_rank:
                        fragment = new RankFragment();
                        break;
                }
                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container, fragment).commit();
                return true;
            }
        });

    }

    private void initView() {


    }

    private void sendCustomNotification() {


        nmanager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
        contentView.setImageViewResource(R.id.image, R.drawable.brush_notification);
        contentView.setTextViewText(R.id.title, "Custom notification");
        contentView.setTextViewText(R.id.text, "This is a custom layout");
        intent = new Intent(getApplicationContext(), OptimizerActivity.class);
        pintent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon)
                .setContent(contentView);

        contentView.setOnClickPendingIntent(R.id.notificationOptimizerBtn, pintent);


        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        nmanager.notify(NotificationId,notification);
    }

    private void sendNotification() {


        //end of the notification...

        intent = new Intent(getApplicationContext(), MainActivity.class);
        pintent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        nmanager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        myBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("This is the content")
                .setContentText("This is the description of the notification")
                .setContentIntent(pintent);

        nmanager.notify(1, myBuilder.build());
    }

    private void getPermission() {
        String[] PERMISSIONS = {Manifest.permission.BATTERY_STATS, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.REORDER_TASKS};
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
