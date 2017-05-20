package com.receptix.batterybuddy;

import android.app.KeyguardManager;
import android.app.NativeActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
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

import com.receptix.batterybuddy.activities.AboutUsActivity;
import com.receptix.batterybuddy.activities.TermsPolicyActivity;
import com.receptix.batterybuddy.charge.ChargeFragment;
import com.receptix.batterybuddy.home.HomeFragment;
import com.receptix.batterybuddy.optimizeractivity.OptimizerActivity;
import com.receptix.batterybuddy.rank.RankFragment;

import static com.receptix.batterybuddy.helper.Constants.Params.BROADCAST_RECEIVER;
import static com.receptix.batterybuddy.helper.Constants.Params.FROM;
import static com.receptix.batterybuddy.helper.Constants.Params.IS_SCREEN_ON;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


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


        // new code..

        Bundle bundle = getIntent().getExtras();

        try {
            boolean isScreenOn = bundle.getBoolean(IS_SCREEN_ON);
            Log.d(TAG, String.valueOf(isScreenOn));
            keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
            boolean isLockedScreen = keyguardManager.inKeyguardRestrictedInputMode();
            Log.d(TAG, "isLockedScreen : " + isLockedScreen);

            String from = bundle.getString(FROM);
            if (from != null && from.equalsIgnoreCase(BROADCAST_RECEIVER)) {
                if (!isLockedScreen) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            startActivity(new Intent(getApplicationContext(), BatteryAdActivity.class));
                            finish();
                        }
                    }, 4000);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(getApplicationContext(), LockAdsActivity.class));
                            finish();
                        }
                    }, 4000);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        /*getPermission();*/

        sendCustomNotification();
        /*  sendNotification();*/
        //  setupToolBar(getString(R.string.batterybuddy));
        /*initView();*/
        setupBottomNavigationBar();


        // ends here...

    }


    // main activity codee...


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
        notificationManager.notify(NOTIFICATION_ID, notification);
    }


    //  ends here....

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

        if (id == R.id.nav_about_us) {

//            start about us activity
            startActivity(new Intent(NavigationActivity.this, AboutUsActivity.class));


        } else if (id == R.id.nav_feedback) {


        } else if (id == R.id.nav_terms_policy) {

            startActivity(new Intent(NavigationActivity.this, TermsPolicyActivity.class));

        } else if (id == R.id.nav_share) {
            String url ="https://play.google.com/store/apps/details?id=com.earnmoney.appbucks&hl=en";
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
           // sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
            sendIntent.setData(Uri.parse(url));
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share_to)));

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
