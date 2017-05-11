package com.receptix.batterybuddy;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class BatteryAdActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG ="AdsActivity";
    LinearLayout adsLinearLayout, swipeLinearLayout;
    ActivityManager myActivityManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_ad);
        myActivityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        initView();
        InitializeSystemData();

    }

    private void InitializeSystemData() {
        getRamInfo();
    }

    private void getRamInfo() {
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        myActivityManager.getMemoryInfo(memInfo);
        long blabla =memInfo.availMem;

        Log.d(TAG,"MEM="+String.valueOf(blabla));


    }

    private void initView() {

        adsLinearLayout = (LinearLayout) findViewById(R.id.adsLinearLayout);
        adsLinearLayout.setOnClickListener(this);
        swipeLinearLayout = (LinearLayout) findViewById(R.id.swipeLinearLayout);
        swipeLinearLayout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "I am clicked ", Toast.LENGTH_SHORT).show();
        switch (v.getId()) {
            case R.id.swipeLinearLayout:
                finish();
                break;
            case R.id.adsLinearLayout:
                break;
        }
    }
}
