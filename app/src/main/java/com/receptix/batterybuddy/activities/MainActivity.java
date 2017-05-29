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


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.receptix.batterybuddy.R;
import com.receptix.batterybuddy.ScreenListenerService;
import com.receptix.batterybuddy.helper.LogUtil;
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


    @Override
    public void onBackPressed() {
        // do nothing if user presses back button (let the activity load)
    }
}
