package com.receptix.batterybuddy.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.receptix.batterybuddy.R;
import com.receptix.batterybuddy.ScreenListenerService;
import com.receptix.batterybuddy.helper.LogUtil;
import com.receptix.batterybuddy.helper.UserSessionManager;


public class MainActivity extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    UserSessionManager userSessionManager;
    Context context;
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

        String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        if (firebaseToken != null)
            LogUtil.e("FCM Token", firebaseToken);

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
