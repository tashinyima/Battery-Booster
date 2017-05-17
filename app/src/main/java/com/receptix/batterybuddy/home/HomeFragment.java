package com.receptix.batterybuddy.home;


import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.receptix.batterybuddy.R;
import com.receptix.batterybuddy.optimizeractivity.OptimizerActivity;

import java.lang.reflect.Method;

import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_LEVEL;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_SCALE;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_TECHNOLOGY;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_TEMPERATURE;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_TEMPERATURE_CONVERSION_UNIT;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_VOLTAGE;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_VOLTAGE_CONVERSION_UNIT;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.IS_BATTERY_PRESENT;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevel.BRIGHTNESS_LEVEL_0;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevel.BRIGHTNESS_LEVEL_1;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevel.BRIGHTNESS_LEVEL_2;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevel.BRIGHTNESS_LEVEL_3;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevel.BRIGHTNESS_LEVEL_4;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevel.BRIGHTNESS_LEVEL_5;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevel.BRIGHTNESS_LEVEL_6;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevel.BRIGHTNESS_LEVEL_7;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevel.BRIGHTNESS_LEVEL_8;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevel.BRIGHTNESS_LEVEL_9;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevelValues.BRIGHTNESS_DEFAULT_VALUE;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevelValues.BRIGHTNESS_LEVEL_0_UPPER_LIMIT;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevelValues.BRIGHTNESS_LEVEL_1_UPPER_LIMIT;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevelValues.BRIGHTNESS_LEVEL_2_UPPER_LIMIT;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevelValues.BRIGHTNESS_LEVEL_3_UPPER_LIMIT;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevelValues.BRIGHTNESS_LEVEL_4_UPPER_LIMIT;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevelValues.BRIGHTNESS_LEVEL_5_UPPER_LIMIT;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevelValues.BRIGHTNESS_LEVEL_6_UPPER_LIMIT;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevelValues.BRIGHTNESS_LEVEL_7_UPPER_LIMIT;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevelValues.BRIGHTNESS_LEVEL_8_UPPER_LIMIT;
import static com.receptix.batterybuddy.helper.Constants.BrightnessLevelValues.BRIGHTNESS_LEVEL_9_UPPER_LIMIT;
import static com.receptix.batterybuddy.helper.Constants.LockScreenTimeout.TIMEOUT_10_MINUTES;
import static com.receptix.batterybuddy.helper.Constants.LockScreenTimeout.TIMEOUT_15_SECONDS;
import static com.receptix.batterybuddy.helper.Constants.LockScreenTimeout.TIMEOUT_1_MINUTE;
import static com.receptix.batterybuddy.helper.Constants.LockScreenTimeout.TIMEOUT_2_MINUTE;
import static com.receptix.batterybuddy.helper.Constants.LockScreenTimeout.TIMEOUT_30_MINUTES;
import static com.receptix.batterybuddy.helper.Constants.LockScreenTimeout.TIMEOUT_30_SECONDS;
import static com.receptix.batterybuddy.helper.Constants.LockScreenTimeout.TIMEOUT_5_MINUTES;
import static com.receptix.batterybuddy.helper.Constants.LockScreenTimeout.TIMEOUT_5_SECONDS;
import static com.receptix.batterybuddy.helper.Constants.LockScreenTimeout.TIMEOUT_AUTO_LOCK;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.BATTERY_CAPACITY;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.CPU_ACTIVE;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.CPU_AWAKE;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.CPU_IDLE;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.DSP_AUDIO;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.DSP_VIDEO;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.GPS_ON;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.METHOD_GET_AVERAGE_POWER;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.POWER_PROFILE_CLASS;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.RADIO_ACTIVE;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.RADIO_ON;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.RADIO_SCANNING;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.SCREEN_ON;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.WIFI_ACTIVE;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.WIFI_ON;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.WIFI_SCANNING;
import static com.receptix.batterybuddy.helper.Constants.ShortHandNotations.HOURS;
import static com.receptix.batterybuddy.helper.Constants.ShortHandNotations.MINUTES;
import static com.receptix.batterybuddy.helper.Constants.SoundModes.RINGTONE_MODE;
import static com.receptix.batterybuddy.helper.Constants.SoundModes.SILENT_MODE;
import static com.receptix.batterybuddy.helper.Constants.SoundModes.VIBRATE_MODE;
import static com.receptix.batterybuddy.helper.Constants.StatisticTypes.OVERALL_BATTERY;
import static com.receptix.batterybuddy.helper.Constants.StatisticTypes.VIDEO;
import static com.receptix.batterybuddy.helper.Constants.StatisticTypes.VOICE_CALL;
import static com.receptix.batterybuddy.helper.Constants.StatisticTypes.WIFI;
import static com.receptix.batterybuddy.helper.Constants.Tags.TAG_HOME_FRAGMENT;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final int DELAY = 3000;
    View view;
    Context context;
    Toolbar toolbar;
    TextView textView_batteryTemperature, textView_batteryVoltage, textView_batteryTechnology;
    String batteryTechnology;
    int batteryVoltage, batteryTemperature, batteryLevel, batteryScale;
    DonutProgress batteryProgress;
    Button optimzerButton;
    TextView bluetoothTextView, brightnessTextView, lockscreenTextView, soundTextView;

    LinearLayout brightnessLinearLayout, lockscreenLinearLayout, soundLinearLayout, bluetoothLinearLayout;
    int lockScreenTimeOut = 0;
    int inWhichBrightness = 0;
    int WhatisLockScreenTimeOut = 0;
    int isBluetoothOn; // 1 = bluetooth is off by default....
    BluetoothAdapter bluetoothAdapter;
    AudioManager audioManagerMode = null;
    int audioCurrentMode = 0;
    int inWhichSoundIndex;
    BatteryManager myBatteryManger;
    ImageView soundImageView;
    TextView textView_availableBattery_Hours, textView_availableBattery_Minutes;
    TextView textView_timeLeft_VoiceCall, textview_timeLeft_Video, textview_timeLeft_Wifi;
    private int mProgressStatus = 0;
    private int screenBrightness = 0;
    private BroadcastReceiver battery_info_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isBatteryPresent = intent.getBooleanExtra(IS_BATTERY_PRESENT, false);
            if (isBatteryPresent) {
                batteryTechnology = intent.getStringExtra(BATTERY_TECHNOLOGY);
                batteryVoltage = intent.getIntExtra(BATTERY_VOLTAGE, 0);
                batteryTemperature = intent.getIntExtra(BATTERY_TEMPERATURE, 0);
                batteryLevel = intent.getIntExtra(BATTERY_LEVEL, 0);
                batteryScale = intent.getIntExtra(BATTERY_SCALE, 0);

                double voltageValueInDouble = batteryVoltage * BATTERY_VOLTAGE_CONVERSION_UNIT;
                double temperatureValueInDouble = batteryTemperature * BATTERY_TEMPERATURE_CONVERSION_UNIT;

                // Calculate the battery charged percentage
                float batteryChargedPercentage = batteryLevel / (float) batteryScale;

                int totalBatteryCapacityInMAh = (int) getBatteryCapacity();
                int remainingBatteryCapacityInMah = batteryLevel * totalBatteryCapacityInMAh / 100;

                // ETA FOR SCREEN ON
                double screenPowerConsumption = getPowerConsumption_Total();
                double timeLeft_ScreenOn = remainingBatteryCapacityInMah / screenPowerConsumption;
                getValueInHoursMinutes(timeLeft_ScreenOn, OVERALL_BATTERY);

                // ETA FOR WIFI CONSUMPTION
                double wifiPowerConsumption = getPowerConsumption_Wifi();
                double timeLeft_Wifi = remainingBatteryCapacityInMah / wifiPowerConsumption;
                getValueInHoursMinutes(timeLeft_Wifi, WIFI);

                // ETA FOR VOICE CALLS
                double voiceCallConsumption = getPowerConsumption_VoiceCalls();
                double timeLeft_VoiceCall = remainingBatteryCapacityInMah / voiceCallConsumption;
                getValueInHoursMinutes(timeLeft_VoiceCall, VOICE_CALL);

                // ETA FOR VIDEO WATCHING
                double videoPowerConsumption = getPowerConsumption_Videos();
                double timeLeft_Video = remainingBatteryCapacityInMah / videoPowerConsumption;
                getValueInHoursMinutes(timeLeft_Video, VIDEO);

                // Update the progress bar to display current battery charged percentage
                mProgressStatus = (int) ((batteryChargedPercentage) * 100);

                batteryProgress.setProgress(mProgressStatus);

                textView_batteryVoltage.setText(String.format("%.1f", voltageValueInDouble) + "V");
                textView_batteryTemperature.setText(String.format("%.1f", temperatureValueInDouble) + (char) 0x00b0 + "C");
                textView_batteryTechnology.setText(batteryTechnology);
                Bundle bundle = intent.getExtras();
                Log.d(TAG_HOME_FRAGMENT, bundle.toString());
                Log.d(TAG_HOME_FRAGMENT, "Technology=" + batteryTechnology + "batteryVoltage=" + batteryVoltage + "Temperature=" + batteryTemperature);
            } else {
                showMessage(getString(R.string.no_battery_present));
            }

        }
    };

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();
        audioManagerMode = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);


        initView(view);
        getSystemData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerBatteryInfoReceiver();

    }

    @Override
    public void onPause() {
        if (battery_info_receiver != null)
            context.unregisterReceiver(battery_info_receiver);
        super.onPause();
    }

    private void getSystemData() {
        getDeviceBrightnessLevel();
        checkIfBluetoothOnOrOff();
        getScreenTimeout();
        getDeviceRingerMode();
        getBatteryLevel();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void getBatteryLevel() {
        myBatteryManger = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
    }

    private void getDeviceRingerMode() {

        audioCurrentMode = audioManagerMode.getRingerMode();
        Log.d(TAG_HOME_FRAGMENT, "Audio mode" + String.valueOf(audioCurrentMode));

        switch (audioCurrentMode) {

            case AudioManager.RINGER_MODE_SILENT:
                soundTextView.setText(R.string.silentsound);
                audioCurrentMode = AudioManager.RINGER_MODE_SILENT;
                soundImageView.setImageResource(R.drawable.sound_silent);
                inWhichSoundIndex = SILENT_MODE;
                break;

            case AudioManager.RINGER_MODE_NORMAL:
                soundTextView.setText(R.string.soundText);
                soundImageView.setImageResource(R.drawable.sound);
                audioCurrentMode = AudioManager.RINGER_MODE_NORMAL;
                inWhichSoundIndex = RINGTONE_MODE;
                break;

            case AudioManager.RINGER_MODE_VIBRATE:
                soundTextView.setText(R.string.soundvibrate);
                audioCurrentMode = AudioManager.RINGER_MODE_VIBRATE;
                soundImageView.setImageResource(R.drawable.sound_vibrate);
                inWhichSoundIndex = VIBRATE_MODE;
                break;
        }
    }

    /**
     * See {@link com.receptix.batterybuddy.helper.Constants.LockScreenTimeout} for different range of values for screen timeout
     */
    private void getScreenTimeout() {

        int inWhichIndexSeconds;
        int inWhichIndexMinutes;

        lockScreenTimeOut = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, DELAY);
        Log.d(TAG_HOME_FRAGMENT, "Timeout=" + String.valueOf(lockScreenTimeOut));

        inWhichIndexSeconds = lockScreenTimeOut / 1000;

        // if lock screen timeout is less than (or equal to) 30 seconds
        if (inWhichIndexSeconds <= 30) {
            if (inWhichIndexSeconds < 1) {
                WhatisLockScreenTimeOut = TIMEOUT_AUTO_LOCK;
                lockscreenTextView.setText(R.string.autolock);
            } else if (inWhichIndexSeconds == 5) {
                WhatisLockScreenTimeOut = TIMEOUT_5_SECONDS;
                lockscreenTextView.setText(R.string.fivesecond);

            } else if (inWhichIndexSeconds == 15) {
                WhatisLockScreenTimeOut = TIMEOUT_15_SECONDS;
                lockscreenTextView.setText(R.string.fivesecond);

            } else if (inWhichIndexSeconds == 30) {
                WhatisLockScreenTimeOut = TIMEOUT_30_SECONDS;
                lockscreenTextView.setText(R.string.thirtysecond);
            }
        } else
        // if lock screen timeout is greater than 30 seconds
        {
            inWhichIndexMinutes = inWhichIndexSeconds / 60;
            if (inWhichIndexMinutes == 1) {
                WhatisLockScreenTimeOut = TIMEOUT_1_MINUTE;
                lockscreenTextView.setText(R.string.oneminute);

            } else if (inWhichIndexMinutes == 2) {
                WhatisLockScreenTimeOut = TIMEOUT_2_MINUTE;
                lockscreenTextView.setText(R.string.twominute);

            } else if (inWhichIndexMinutes == 5) {
                WhatisLockScreenTimeOut = TIMEOUT_5_MINUTES;
                lockscreenTextView.setText(R.string.fiveminute);

            } else if (inWhichIndexMinutes == 10) {
                WhatisLockScreenTimeOut = TIMEOUT_10_MINUTES;
                lockscreenTextView.setText(R.string.tenminute);

            } else if (inWhichIndexMinutes == 30) {
                WhatisLockScreenTimeOut = TIMEOUT_30_MINUTES;
                lockscreenTextView.setText(R.string.thirtyminute);

            }
        }

    }

    private void checkIfBluetoothOnOrOff() {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();



        if(bluetoothAdapter!=null){

            if (bluetoothAdapter.isEnabled()) {
                isBluetoothOn =0;
                bluetoothTextView.setText(getString(R.string.on));
            } else if(bluetoothAdapter.disable()) {
                isBluetoothOn=1;
                bluetoothTextView.setText(getString(R.string.off));
            }
        }

        Log.d("Bluetooth",String.valueOf(isBluetoothOn));

    }

    private void getDeviceBrightnessLevel() {

        int screenBrightnessLevel;

        screenBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, BRIGHTNESS_DEFAULT_VALUE);

        Log.d(TAG_HOME_FRAGMENT, "The brightness is: " + screenBrightness);

        screenBrightnessLevel = screenBrightness;

        if (screenBrightnessLevel <= BRIGHTNESS_LEVEL_0_UPPER_LIMIT) {
            inWhichBrightness = BRIGHTNESS_LEVEL_0;
            brightnessTextView.setText(R.string.zerobright);
        } else if (screenBrightnessLevel > BRIGHTNESS_LEVEL_0_UPPER_LIMIT && screenBrightnessLevel <= BRIGHTNESS_LEVEL_1_UPPER_LIMIT) {
            inWhichBrightness = BRIGHTNESS_LEVEL_1;
            brightnessTextView.setText(R.string.onebright);
        } else if (screenBrightnessLevel > BRIGHTNESS_LEVEL_1_UPPER_LIMIT && screenBrightnessLevel <= BRIGHTNESS_LEVEL_2_UPPER_LIMIT) {
            inWhichBrightness = BRIGHTNESS_LEVEL_2;
            brightnessTextView.setText(R.string.twobright);
        } else if (screenBrightnessLevel > BRIGHTNESS_LEVEL_2_UPPER_LIMIT && screenBrightnessLevel <= BRIGHTNESS_LEVEL_3_UPPER_LIMIT) {
            inWhichBrightness = BRIGHTNESS_LEVEL_3;
            brightnessTextView.setText(R.string.threebright);
        } else if (screenBrightnessLevel > BRIGHTNESS_LEVEL_3_UPPER_LIMIT && screenBrightnessLevel <= BRIGHTNESS_LEVEL_4_UPPER_LIMIT) {
            inWhichBrightness = BRIGHTNESS_LEVEL_4;
            brightnessTextView.setText(R.string.fourbright);
        } else if (screenBrightnessLevel > BRIGHTNESS_LEVEL_4_UPPER_LIMIT && screenBrightnessLevel <= BRIGHTNESS_LEVEL_5_UPPER_LIMIT) {
            inWhichBrightness = BRIGHTNESS_LEVEL_5;
            brightnessTextView.setText(R.string.fivebright);
        } else if (screenBrightnessLevel > BRIGHTNESS_LEVEL_5_UPPER_LIMIT && screenBrightnessLevel <= BRIGHTNESS_LEVEL_6_UPPER_LIMIT) {
            inWhichBrightness = BRIGHTNESS_LEVEL_6;
            brightnessTextView.setText(R.string.sixbright);
        } else if (screenBrightnessLevel > BRIGHTNESS_LEVEL_6_UPPER_LIMIT && screenBrightnessLevel <= BRIGHTNESS_LEVEL_7_UPPER_LIMIT) {
            inWhichBrightness = BRIGHTNESS_LEVEL_7;
            brightnessTextView.setText(R.string.sevenbright);
        } else if (screenBrightnessLevel > BRIGHTNESS_LEVEL_7_UPPER_LIMIT && screenBrightnessLevel <= BRIGHTNESS_LEVEL_8_UPPER_LIMIT) {
            inWhichBrightness = BRIGHTNESS_LEVEL_8;
            brightnessTextView.setText(R.string.eightbright);
        } else if (screenBrightnessLevel > BRIGHTNESS_LEVEL_8_UPPER_LIMIT && screenBrightnessLevel <= BRIGHTNESS_LEVEL_9_UPPER_LIMIT) {
            inWhichBrightness = BRIGHTNESS_LEVEL_9;
            brightnessTextView.setText(R.string.ninebright);
        }
    }

    private void registerBatteryInfoReceiver() {
        IntentFilter intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(battery_info_receiver, intentfilter);
    }

    private void initView(View view) {
        textView_batteryTechnology = (TextView) view.findViewById(R.id.technologyTextView);
        textView_batteryTemperature = (TextView) view.findViewById(R.id.temperatureTextView);
        textView_batteryVoltage = (TextView) view.findViewById(R.id.voltageTextView);
        batteryProgress = (DonutProgress) view.findViewById(R.id.homebatteryProgress);
        optimzerButton = (Button) view.findViewById(R.id.optimizerButton);
        optimzerButton.setOnClickListener(this);

        brightnessLinearLayout = (LinearLayout) view.findViewById(R.id.brightnessLinearLayout);
        brightnessLinearLayout.setOnClickListener(this);
        lockscreenLinearLayout = (LinearLayout) view.findViewById(R.id.lockscreenLinearLayout);
        lockscreenLinearLayout.setOnClickListener(this);
        bluetoothLinearLayout = (LinearLayout) view.findViewById(R.id.bluetoothLinearLayout);
        bluetoothLinearLayout.setOnClickListener(this);
        soundLinearLayout = (LinearLayout) view.findViewById(R.id.soundLinearLayout);
        soundLinearLayout.setOnClickListener(this);
        bluetoothTextView = (TextView) view.findViewById(R.id.bluetoothTextView);
        soundTextView = (TextView) view.findViewById(R.id.soundTextView);
        brightnessTextView = (TextView) view.findViewById(R.id.brightnessTextView);
        lockscreenTextView = (TextView) view.findViewById(R.id.lockscreenTextView);
        soundImageView = (ImageView) view.findViewById(R.id.soundImageView);

        textView_availableBattery_Hours = (TextView) view.findViewById(R.id.hourTextView);
        textView_availableBattery_Minutes = (TextView) view.findViewById(R.id.minutesTextView);
        textView_timeLeft_VoiceCall = (TextView) view.findViewById(R.id.voiceCallTextView);
        textview_timeLeft_Wifi = (TextView) view.findViewById(R.id.wifiCallTextView);
        textview_timeLeft_Video = (TextView) view.findViewById(R.id.videoCallTextView);


    }

    private void getValueInHoursMinutes(double remainingScreenTime, int statisticType) {

        int hours = (int) remainingScreenTime;
        //obtain actual number of minutes from double value of Hours
        String stringminutes = String.valueOf(String.format("%.2f", remainingScreenTime));
        String lasttwo = stringminutes.substring(stringminutes.length() - 2);
        int minute = Integer.parseInt(lasttwo);
        int realminutes = (minute * 60) / 100;

        String valueGreaterThanEqualToOneHour = String.valueOf(hours) + HOURS + String.valueOf(realminutes) + MINUTES;
        String valueLessThanOneHour = String.valueOf(0) + HOURS + String.valueOf(realminutes) + MINUTES;

        if (statisticType == OVERALL_BATTERY) {
            if (remainingScreenTime > 0.9) {
                textView_availableBattery_Hours.setText(String.valueOf(hours));
                textView_availableBattery_Minutes.setText(String.valueOf(realminutes));
            } else {
                textView_availableBattery_Hours.setText("0");
                textView_availableBattery_Minutes.setText(String.valueOf(realminutes));
            }
        } else if (statisticType == VOICE_CALL) {
            if (remainingScreenTime > 0.9) {
                textView_timeLeft_VoiceCall.setText(valueGreaterThanEqualToOneHour);
            } else {
                textView_timeLeft_VoiceCall.setText(valueLessThanOneHour);
            }
        } else if (statisticType == VIDEO) {
            if (remainingScreenTime > 0.9) {
                textview_timeLeft_Video.setText(valueGreaterThanEqualToOneHour);
            } else {
                textview_timeLeft_Video.setText(valueLessThanOneHour);
            }
        } else if (statisticType == WIFI) {
            if (remainingScreenTime > 0.9) {
                textview_timeLeft_Wifi.setText(valueGreaterThanEqualToOneHour);
            } else {
                textview_timeLeft_Wifi.setText(valueLessThanOneHour);
            }
        }
    }

    private void showMessage(String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.optimizerButton:
                Intent optintent = new Intent(context, OptimizerActivity.class);
                context.startActivity(optintent);

//                Intent BatteryAdsActivity for testing purpose
//
//                Intent bintent = new Intent(context, BatteryAdActivity.class);
//                context.startActivity(bintent);
                break;
            case R.id.brightnessLinearLayout:
                showMessage("I am brightness");
                SetScreenBrightness();
                break;
            case R.id.lockscreenLinearLayout:
                showMessage("I am Lock ");
                SetLockScreenTimer();
                break;
            case R.id.bluetoothLinearLayout:
                showMessage("I am Bluetooth");
                OnOffBluetooth();

                break;
            case R.id.soundLinearLayout:
                showMessage("I am Sound ");

                SoundStatusChange();

                break;

        }
    }

    private void SoundStatusChange() {

        new MaterialDialog.Builder(context)
                .titleColorRes(R.color.white)
                .contentColorRes(R.color.white)
                .positiveColorRes(R.color.white)
                .negativeColorRes(R.color.white)
                .title(R.string.soundtitle)
                .items(R.array.soundarray)
                .widgetColorRes(R.color.white)
                .backgroundColorRes(R.color.bgcolor)
                .itemsCallbackSingleChoice(inWhichSoundIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {


                        /**
                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected radio button to actually be selected.
                         **/
                        Toast.makeText(context, which + ": " + text + ", ID = " + view.getId(), Toast.LENGTH_SHORT).show();

                        switch (which) {

                            case 0:
                                audioManagerMode.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                soundTextView.setText(R.string.silentsound);
                                soundImageView.setImageResource(R.drawable.sound_silent);
                                break;
                            case 1:
                                audioManagerMode.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                soundTextView.setText(R.string.soundText);
                                soundImageView.setImageResource(R.drawable.sound);
                                break;
                            case 2:
                                audioManagerMode.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                soundTextView.setText(R.string.soundvibrate);
                                soundImageView.setImageResource(R.drawable.sound_vibrate);
                                break;


                        }


                        getDeviceRingerMode();

                        return true;
                    }
                })
                .positiveText(R.string.choose)
                .negativeText(R.string.cancel)
                .show();

    }

    private void SetScreenBrightness() {


        new MaterialDialog.Builder(context)
                .titleColorRes(R.color.white)
                .contentColorRes(R.color.white)
                .positiveColorRes(R.color.white)
                .negativeColorRes(R.color.white)
                .title(R.string.brightnesstitle)
                .items(R.array.brightness)
                .widgetColorRes(R.color.white)
                .backgroundColorRes(R.color.bgcolor)
                .itemsCallbackSingleChoice(inWhichBrightness, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        int setbrigtnesst = 0;

                        /**
                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected radio button to actually be selected.
                         **/
                        Toast.makeText(context, which + ": " + text + ", ID = " + view.getId(), Toast.LENGTH_SHORT).show();

                        switch (which) {

                            case 0:
                                setbrigtnesst = 24;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, setbrigtnesst);
                                brightnessTextView.setText(R.string.zerobright);
                                break;

                            case 1:
                                setbrigtnesst = 50;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, setbrigtnesst);
                                brightnessTextView.setText(R.string.onebright);
                                break;

                            case 2:
                                setbrigtnesst = 76;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, setbrigtnesst);
                                brightnessTextView.setText(R.string.twobright);
                                break;

                            case 3:
                                setbrigtnesst = 102;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, setbrigtnesst);
                                brightnessTextView.setText(R.string.threebright);
                                break;

                            case 4:
                                setbrigtnesst = 127;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, setbrigtnesst);
                                brightnessTextView.setText(R.string.fourbright);
                                break;

                            case 5:
                                setbrigtnesst = 152;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, setbrigtnesst);
                                brightnessTextView.setText(R.string.fivebright);
                                break;

                            case 6:
                                setbrigtnesst = 178;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, setbrigtnesst);
                                brightnessTextView.setText(R.string.sixbright);
                                break;

                            case 7:
                                setbrigtnesst = 203;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, setbrigtnesst);
                                brightnessTextView.setText(R.string.sevenbright);
                                break;

                            case 8:
                                setbrigtnesst = 230;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, setbrigtnesst);
                                brightnessTextView.setText(R.string.eightbright);
                                break;
                            case 9:
                                setbrigtnesst = 250;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, setbrigtnesst);
                                brightnessTextView.setText(R.string.ninebright);
                                break;
                        }

                        getDeviceBrightnessLevel();

                        return true;
                    }
                })
                .positiveText(R.string.choose)
                .negativeText(R.string.cancel)
                .show();

        //   Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,200);


    }

    private void SetLockScreenTimer() {

        new MaterialDialog.Builder(context)
                .titleColorRes(R.color.white)
                .contentColorRes(R.color.white)
                .positiveColorRes(R.color.white)
                .negativeColorRes(R.color.white)
                .title(R.string.lockScreenTimeouttitle)
                .items(R.array.sreenlocktimer)
                .widgetColorRes(R.color.white)
                .backgroundColorRes(R.color.bgcolor)
                .itemsCallbackSingleChoice(WhatisLockScreenTimeOut, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        int timeout = 0;

                        /**
                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected radio button to actually be selected.
                         **/
                        Toast.makeText(context, which + ": " + text + ", ID = " + view.getId(), Toast.LENGTH_SHORT).show();

                        switch (which) {

                            case 0:
                                timeout = 0;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeout);
                                lockscreenTextView.setText(R.string.autolock);
                                break;

                            case 1:
                                timeout = 5000;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeout);
                                lockscreenTextView.setText(R.string.fivesecond);
                                break;

                            case 2:
                                timeout = 15000;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeout);
                                lockscreenTextView.setText(R.string.fiveteensecond);
                                break;

                            case 3:
                                timeout = 30000;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeout);
                                lockscreenTextView.setText(R.string.thirtysecond);

                                break;

                            case 4:
                                timeout = 60000;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeout);
                                lockscreenTextView.setText(R.string.oneminute);

                                break;

                            case 5:
                                timeout = 120000;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeout);
                                lockscreenTextView.setText(R.string.twominute);

                                break;

                            case 6:
                                timeout = 300000;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeout);
                                lockscreenTextView.setText(R.string.fiveminute);

                                break;

                            case 7:
                                timeout = 600000;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeout);
                                lockscreenTextView.setText(R.string.tenminute);

                                break;

                            case 8:
                                timeout = 1800000;
                                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeout);
                                lockscreenTextView.setText(R.string.thirtyminute);

                                break;
                        }


                        getScreenTimeout();
                        return true;
                    }
                })
                .positiveText(R.string.choose)
                .negativeText(R.string.cancel)
                .show();


    }

    private void OnOffBluetooth() {

        String onoffArray[] = {"On", "Off"};

        new MaterialDialog.Builder(context)
                .titleColorRes(R.color.white)
                .contentColorRes(R.color.white)
                .positiveColorRes(R.color.white)
                .title(R.string.bluetoothtitle)
                .items(onoffArray)
                .widgetColorRes(R.color.white)
                .backgroundColorRes(R.color.bgcolor)
                .itemsCallbackSingleChoice(isBluetoothOn, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        /**
                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected radio button to actually be selected.
                         **/
                        Toast.makeText(context, which + ": " + text + ", ID = " + view.getId(), Toast.LENGTH_SHORT).show();

                        switch (which){
                            case 0:

                                 if(bluetoothAdapter.disable()){

                                     bluetoothAdapter.enable();
                                     bluetoothTextView.setText("On");
                                     isBluetoothOn =0;
                                 }


                                break;
                            case 1:

                                if(bluetoothAdapter.enable()){

                                    bluetoothAdapter.disable();
                                    bluetoothTextView.setText("Off");
                                    isBluetoothOn =1;
                                }

                                break;

                        }



                        return true;
                    }
                })
                .positiveText(R.string.choose)
                .show();
    }

    public double getBatteryCapacity() {
        Object mPowerProfile = null;
        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Method getAveragePower = Class.forName(POWER_PROFILE_CLASS).getMethod(METHOD_GET_AVERAGE_POWER, String.class);
            //Get total battery capacity in mAh.
            double batteryCapacity = (Double) getAveragePower.invoke(mPowerProfile, BATTERY_CAPACITY);
            return batteryCapacity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public int getPowerConsumption_radio() {

        Object mPowerProfile = null;
        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Method getAveragePower = Class.forName(POWER_PROFILE_CLASS).getMethod(METHOD_GET_AVERAGE_POWER, String.class);
            double radioActive = (Double) getAveragePower.invoke(mPowerProfile, RADIO_ACTIVE);
            double radioScanning = (Double) getAveragePower.invoke(mPowerProfile, RADIO_SCANNING);
            double radioOn = (Double) getAveragePower.invoke(mPowerProfile, RADIO_ON);

            int radioPowerConsumption = (int) radioActive + (int) radioScanning + (int) radioOn;
            return radioPowerConsumption;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // default value to return if radio power values not obtained
        return 0;

    }


    public double getPowerConsumption_Total() {

        Object mPowerProfile = null;
        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            Method getAveragePower = Class.forName(POWER_PROFILE_CLASS).getMethod(METHOD_GET_AVERAGE_POWER, String.class);

            double powerConsumption_wifiOn = (Double) getAveragePower.invoke(mPowerProfile, WIFI_ON);
            double powerConsumption_screenOn = (Double) getAveragePower.invoke(mPowerProfile, SCREEN_ON);
            double powerConsumption_gpsOn = (Double) getAveragePower.invoke(mPowerProfile, GPS_ON);
            double powerConsumption_cpuActive = (Double) getAveragePower.invoke(mPowerProfile, CPU_ACTIVE);
            double powerConsumption_cpuAwake = (Double) getAveragePower.invoke(mPowerProfile, CPU_AWAKE);

            double averageConsumption_Cpu = (powerConsumption_cpuActive + powerConsumption_cpuAwake) / 2;
            double powerConsumption_total = (averageConsumption_Cpu + powerConsumption_wifiOn + powerConsumption_screenOn);
            return powerConsumption_total;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0.0;


    }

    public double getPowerConsumption_Wifi() {

        Object mPowerProfile = null;
        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            Method getAveragePower = Class.forName(POWER_PROFILE_CLASS).getMethod(METHOD_GET_AVERAGE_POWER, String.class);

            double wifiOn = (Double) getAveragePower.invoke(mPowerProfile, WIFI_ON);
            double wifiActive = (Double) getAveragePower.invoke(mPowerProfile, WIFI_ACTIVE);
            double wifiScan = (Double) getAveragePower.invoke(mPowerProfile, WIFI_SCANNING);

            double screenOn = (Double) getAveragePower.invoke(mPowerProfile, SCREEN_ON);
            double cpuAwake = (Double) getAveragePower.invoke(mPowerProfile, CPU_AWAKE);
            double cpuIdle = (Double) getAveragePower.invoke(mPowerProfile, CPU_IDLE);
            double cpuActive = (Double) getAveragePower.invoke(mPowerProfile, CPU_ACTIVE);

            double averageConsumption_Cpu = (cpuActive + cpuAwake + cpuIdle) / 3;
            double totalConsumption_WifiOnly = (wifiActive + wifiOn + wifiScan) / 3;
            // to get a rough estimate for
            // ETA for wifi usage, we also consider CPU Usage and Screen On (since both CPU and Screen are used when Wifi is used (mostly))
            double totalConsumptionOnWifi = (totalConsumption_WifiOnly + averageConsumption_Cpu + screenOn);
            return totalConsumptionOnWifi;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0.0;


    }

    public double getPowerConsumption_Videos() {

        Object mPowerProfile = null;
        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            Method getAveragePower = Class.forName(POWER_PROFILE_CLASS).getMethod(METHOD_GET_AVERAGE_POWER, String.class);

            double cpuAwake = (Double) getAveragePower.invoke(mPowerProfile, CPU_AWAKE);
            double cpuActive = (Double) getAveragePower.invoke(mPowerProfile, CPU_ACTIVE);
            double wifiOn = (Double) getAveragePower.invoke(mPowerProfile, WIFI_ON);
            double screenOn = (Double) getAveragePower.invoke(mPowerProfile, SCREEN_ON);
            double dspAudio = (Double) getAveragePower.invoke(mPowerProfile, DSP_AUDIO);
            double dspVideo = (Double) getAveragePower.invoke(mPowerProfile, DSP_VIDEO);

            double averageConsumption_Cpu = (cpuActive + cpuAwake) / 2;

            // to get a rough estimate for
            // ETA for video usage, we also consider CPU Usage, Screen On
            // and Audio-Video Decoding Power Consumption.
            double totalConsumption_Videos = (averageConsumption_Cpu + wifiOn + screenOn + dspAudio + dspVideo);
            return totalConsumption_Videos;

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return 0.0;


    }

    public double getPowerConsumption_VoiceCalls() {

        Object mPowerProfile = null;
        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            Method getAveragePower = Class.forName(POWER_PROFILE_CLASS).getMethod(METHOD_GET_AVERAGE_POWER, String.class);

            double cpuAwake = (Double) getAveragePower.invoke(mPowerProfile, CPU_AWAKE);
            double cpuActive = (Double) getAveragePower.invoke(mPowerProfile, CPU_ACTIVE);
            double wifiOn = (Double) getAveragePower.invoke(mPowerProfile, WIFI_ON);
            double screenOn = (Double) getAveragePower.invoke(mPowerProfile, SCREEN_ON);
            double dspAudio = (Double) getAveragePower.invoke(mPowerProfile, DSP_AUDIO);

            double averageConsumption_Cpu = (cpuActive + cpuAwake) / 2;
            // to get a rough estimate for
            // ETA for video usage, we also consider CPU Usage, Wifi (for voice calls through wifi), Screen
            // and Audio Decoding Power Consumption.
            double totalConsumption = (averageConsumption_Cpu + wifiOn + screenOn + dspAudio);
            return totalConsumption;
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return 0.0;


    }


}
