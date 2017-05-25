package com.receptix.batterybuddy.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.receptix.batterybuddy.R;
import com.receptix.batterybuddy.helper.LogUtil;

import java.util.Random;

import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_LEVEL;
import static com.receptix.batterybuddy.helper.Constants.Preferences.IS_ACTIVE;
import static com.receptix.batterybuddy.helper.Constants.Preferences.PREFERENCES_IS_ACTIVE;
import static com.receptix.batterybuddy.helper.Constants.ShortHandNotations.MINUTES;

public class SuccessOptimizerActivity extends AppCompatActivity {
    private static final String TAG = SuccessOptimizerActivity.class.getSimpleName();
    Toolbar toolbar;
    ImageView imageView_successfulOptimization;
    TextView textView_extendedTime;
    int receivedBatteryLevel = 0;
    int randomExtendedTimePeriod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_optimizer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

       setupToolBar(getString(R.string.poweroptimization));
        handleIntent(getIntent());
        findViewsById();

        if(Build.VERSION.SDK_INT>19)
        {
            YoYo.with(Techniques.Shake)
                    .repeat(10)
                    .playOn(findViewById(R.id.imageview_successful_optimization));
        }


          // open AdsActivity after animation completes
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // show activity only if user is currently on this activity
                    if(!isFinishing()) {
                        Intent backIntent = new Intent(SuccessOptimizerActivity.this, AfterOptimizerActivity.class);
                        backIntent.putExtra("extendedTime",randomExtendedTimePeriod);
                        startActivity(backIntent);
                        finish();
                    }
                }
            }, 3000);




    }

    private void handleIntent(Intent intent) {
        if(intent!=null)
        {
            if(intent.getExtras() != null)
            {
                int batteryLevel = intent.getExtras().getInt(BATTERY_LEVEL, 0);
                if(batteryLevel!=0)
                {
                    receivedBatteryLevel = batteryLevel;
                }
            }

        }
    }

    private void findViewsById() {
        imageView_successfulOptimization = (ImageView) findViewById(R.id.imageview_successful_optimization);
        textView_extendedTime = (TextView) findViewById(R.id.extendedTextView);

        int startRange = 0;
        int endRange = 0;
        if(receivedBatteryLevel<=10)
        {
            startRange = 10;
            endRange = 15;
        }
        if(receivedBatteryLevel>10 && receivedBatteryLevel<=30)
        {
            startRange = 20;
            endRange = 35;
        }
        if(receivedBatteryLevel>30)
        {
            startRange = 40;
            endRange = 50;
        }
        randomExtendedTimePeriod = getRandomNumberInRange(startRange,endRange);
        textView_extendedTime.setText(" " + randomExtendedTimePeriod + " " + MINUTES);
    }

    private static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    private void setupToolBar(String title) {
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView textViewTitle = (TextView) toolbar.findViewById(R.id.textViewTitle);
        textViewTitle.setText(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
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

    private void setIsActive(boolean isActive)
    {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_IS_ACTIVE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_ACTIVE, isActive);
        editor.commit();
        LogUtil.e(TAG, "isActive = "+isActive);
    }

}
