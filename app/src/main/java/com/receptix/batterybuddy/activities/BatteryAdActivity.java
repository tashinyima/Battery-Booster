package com.receptix.batterybuddy.activities;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.inmobi.ads.InMobiBanner;
import com.inmobi.sdk.InMobiSdk;
import com.receptix.batterybuddy.R;
import com.receptix.batterybuddy.helper.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_LEVEL;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_SCALE;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_TEMPERATURE;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_TEMPERATURE_CONVERSION_UNIT;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_VOLTAGE;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_VOLTAGE_CONVERSION_UNIT;

public class BatteryAdActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {
    private static final String TAG = "AdsActivity";
    LinearLayout adsLinearLayout, swipeLinearLayout;
    ActivityManager myActivityManager;
    Context context;
    ArcProgress arcProgress_Ram, arcProgress_Battery;
    TextView dateTextView;
    Calendar calendar;
    private SensorManager mSensorManager;
    private Sensor mTempSensor;
    ProgressBar batteryProgressbar;
    TextView batteryLevelTextView,batteryChargingStatusTextView;
    InMobiBanner banner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_battery_ad);
        myActivityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        context = getApplicationContext();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mTempSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mSensorManager.registerListener(this, mTempSensor, SensorManager.SENSOR_DELAY_FASTEST);
        banner = (InMobiBanner) findViewById(R.id.banner);
        InMobiSdk.init(BatteryAdActivity.this, "4a38c3c40747428fa346cb0456d9034f");
        banner.load();

        initView();
        InitializeSystemData();

        if (mTempSensor == null) {
            LogUtil.d(TAG, "There is no sensor in it");

        } else {

            LogUtil.d(TAG, "CPU Temperature" + String.valueOf(mTempSensor));
        }




    }


    private void InitializeSystemData() {
        getRamInfo();
        getSystemDateNow();
        getBatteryTemperature();

    }

    private void getBatteryTemperature() {

        IntentFilter intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(battery_info_receiver, intentfilter);
    }

    private BroadcastReceiver battery_info_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            boolean isPresent = intent.getBooleanExtra("present", false);
            if (isPresent) {


                int temperature = intent.getIntExtra("temperature", 0);
                double doubletemp = temperature * 0.1;
                int progresint = (int) doubletemp;
                int level = intent.getIntExtra(BATTERY_LEVEL, 0);
                int scale = intent.getIntExtra(BATTERY_SCALE, 0);
                int batteryVoltage = intent.getIntExtra(BATTERY_VOLTAGE, 0);
                int batteryTemperature = intent.getIntExtra(BATTERY_TEMPERATURE, 0);
                double voltageValueInDouble = batteryVoltage * BATTERY_VOLTAGE_CONVERSION_UNIT;
                double temperatureValueInDouble = batteryTemperature * BATTERY_TEMPERATURE_CONVERSION_UNIT;

                // Calculate the battery charged percentage
                float percentage = level / (float) scale;
                // Update the progress bar to display current battery charged percentage
                int batteryPercentageProgress = (int) ((percentage) * 100);

                arcProgress_Battery.setProgress(batteryTemperature);

                arcProgress_Battery.setSuffixText((char) 0x00b0 + "C");

                batteryProgressbar.setProgress(batteryPercentageProgress);
                batteryLevelTextView.setText(String.valueOf(batteryPercentageProgress) + "%");
                if(isChargerConnected(context)){


                    batteryChargingStatusTextView.setText("Charging");
                    batteryChargingStatusTextView.setVisibility(View.VISIBLE);
                }


            } else {

                showMessage("There is no battery");
            }

        }
    };

    private void showMessage(String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

    private boolean isChargerConnected(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
    }


    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);

    }

    private void getSystemDateNow() {

//in onStart()
        calendar = Calendar.getInstance();
//date format is:  "Date-Month-Year Hour:Minutes am/pm"
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm a"); //Date and time
        String currentDate = sdf.format(calendar.getTime());

//Day of Name in full form like,"Saturday", or if you need the first three characters you have to put "EEE" in the date format and your result will be "Sat".
        SimpleDateFormat sdf_ = new SimpleDateFormat("EEEE");
        Date date = new Date();
        String dayName = sdf_.format(date);
        dateTextView.setText("" + dayName + " " + currentDate + "");

    }

    private void getRamInfo() {
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        myActivityManager.getMemoryInfo(memInfo);
        long blabla = memInfo.availMem;
        long total = memInfo.totalMem;

        long usedram = total - blabla;


        double ratio = usedram / (double) total;
        double percentage = ratio * 100;
        int rampercentage = (int) percentage;
        if (rampercentage > 70) {
            arcProgress_Ram.setProgress(rampercentage);
            arcProgress_Ram.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.red));
            arcProgress_Ram.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            arcProgress_Ram.setProgress(rampercentage);
        }


        LogUtil.d(TAG, "MEM=" + String.valueOf(blabla) + "Total Ram=" + String.valueOf(total) + "Percentage=" + rampercentage);


    }

    private void initView() {

        adsLinearLayout = (LinearLayout) findViewById(R.id.adsLinearLayout);
        adsLinearLayout.setOnClickListener(this);
        swipeLinearLayout = (LinearLayout) findViewById(R.id.swipeLinearLayout);
        swipeLinearLayout.setOnClickListener(this);
        arcProgress_Ram = (ArcProgress) findViewById(R.id.ramArcProgress);
        dateTextView = (TextView) findViewById(R.id.datetv);
        arcProgress_Battery = (ArcProgress) findViewById(R.id.batteryArcProgress);

        batteryProgressbar = (ProgressBar) findViewById(R.id.batteryProgressBar);
        batteryLevelTextView = (TextView) findViewById(R.id.batteryLevelTextView);
        batteryChargingStatusTextView= (TextView) findViewById(R.id.batteryChargingStatusTextView);

    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "I am clicked ", Toast.LENGTH_SHORT).show();
        switch (v.getId()) {
            case R.id.swipeLinearLayout:

                break;
            case R.id.adsLinearLayout:
                break;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {

            String valsss = String.valueOf(event.values[0]);
            LogUtil.d(TAG, "CPU Temperature" + valsss);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

        LogUtil.d(TAG, "AccuracuTem=" + String.valueOf(accuracy));


    }
}
