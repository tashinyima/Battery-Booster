package com.receptix.batterybuddy.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.receptix.batterybuddy.R;
import com.receptix.batterybuddy.helper.UserSessionManager;


public class MainActivity extends AppCompatActivity {

    UserSessionManager userSessionManager;
    Context context;
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        userSessionManager = new UserSessionManager(context);

        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {

                if(userSessionManager.isFirstTime()){

                    startActivity(new Intent(MainActivity.this,TCActivity.class));
                    finish();
                }else{

                    startActivity(new Intent(MainActivity.this,NavigationActivity.class));
                    finish();
                }

            }
        }, SPLASH_TIME_OUT);





    }

}
