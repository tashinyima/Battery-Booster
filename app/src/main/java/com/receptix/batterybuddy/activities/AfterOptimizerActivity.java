package com.receptix.batterybuddy.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.inmobi.ads.InMobiBanner;
import com.inmobi.sdk.InMobiSdk;
import com.receptix.batterybuddy.R;
import com.receptix.batterybuddy.helper.LogUtil;

import static com.receptix.batterybuddy.helper.Constants.Preferences.IS_ACTIVE;
import static com.receptix.batterybuddy.helper.Constants.Preferences.PREFERENCES_IS_ACTIVE;

public class AfterOptimizerActivity extends AppCompatActivity {
    Toolbar toolbar;
    private static final String TAG = AfterOptimizerActivity.class.getSimpleName();

    int extendedTime ;
    TextView extentedTextViewAfter;
    InMobiBanner inMobiBanner;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_after_optimizer);
        context =getApplicationContext();

        Intent intent = getIntent();
        extendedTime = intent.getIntExtra("extendedTime",0);


        initView();
        setupToolBar(getString(R.string.powerconsumptionToolbar));
    }

    private void setupToolBar(String title) {

        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView textViewTitle = (TextView) toolbar.findViewById(R.id.textViewTitle);
        textViewTitle.setText(title);


    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        extentedTextViewAfter = (TextView) findViewById(R.id.extentedTextViewAfter);
        extentedTextViewAfter.setText(extendedTime+" Minutes");
        inMobiBanner = (InMobiBanner) findViewById(R.id.banner);
        inMobiBanner.load();
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
