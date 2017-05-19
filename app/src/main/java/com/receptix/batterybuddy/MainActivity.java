package com.receptix.batterybuddy;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.RemoteViews;

import com.receptix.batterybuddy.charge.ChargeFragment;
import com.receptix.batterybuddy.home.HomeFragment;
import com.receptix.batterybuddy.optimizeractivity.OptimizerActivity;
import com.receptix.batterybuddy.rank.RankFragment;

import static com.receptix.batterybuddy.helper.Constants.Params.BROADCAST_RECEIVER;
import static com.receptix.batterybuddy.helper.Constants.Params.FROM;
import static com.receptix.batterybuddy.helper.Constants.Params.IS_SCREEN_ON;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 101;
    private static final String TAG = "BatteryBuddy";
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
        setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();

        try {
            boolean isScreenOn = bundle.getBoolean(IS_SCREEN_ON);
            Log.d(TAG, String.valueOf(isScreenOn));
            keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
            boolean isLockedScreen = keyguardManager.inKeyguardRestrictedInputMode();
            Log.d(TAG, "isLockedScreen : " + isLockedScreen);

            String from = bundle.getString(FROM);
            if (from!=null && from.equalsIgnoreCase(BROADCAST_RECEIVER)) {
                if(!isLockedScreen)
                {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            startActivity(new Intent(getApplicationContext(), BatteryAdActivity.class));
                            finish();
                        }
                    }, 4000);
                }
                else
                {
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
            ex.printStackTrace();
        }

        /*getPermission();*/

        sendCustomNotification();
        /*  sendNotification();*/
        setupToolBar(getString(R.string.batterybuddy));
        /*initView();*/
        setupBottomNavigationBar();
    }

    private void setupToolBar(String string) {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setTitle(string.toUpperCase());
            /*getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
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

    private void initView() {


    }

    private void sendCustomNotification() {
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
        notificationManager.notify(NOTIFICATION_ID,notification);
    }

    private void sendNotification() {
        //end of the notification...
        intent = new Intent(getApplicationContext(), MainActivity.class);
        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        myBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("This is the content")
                .setContentText("This is the description of the notification")
                .setContentIntent(pendingIntent);

        notificationManager.notify(1, myBuilder.build());
    }

  /*  private void getPermission() {
        String[] PERMISSIONS = {Manifest.permission.BATTERY_STATS, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.REORDER_TASKS};
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
        }
    }*/

  /*public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }*/
}
