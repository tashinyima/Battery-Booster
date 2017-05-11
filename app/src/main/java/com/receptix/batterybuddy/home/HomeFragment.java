package com.receptix.batterybuddy.home;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.DecimalFormat;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContentResolverCompat;
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
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.receptix.batterybuddy.BatteryAdActivity;
import com.receptix.batterybuddy.R;
import com.receptix.batterybuddy.optimizeractivity.OptimizerActivity;

import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "BatteryBuddy";
    View view;
    Context context;
    Toolbar toolbar;
    TextView temperaturTextView, volatageTextView, technologyTextView;
    String technology;
    int voltage, temperature, level, scale;
    DonutProgress batteryProgress;
    private int mProgressStatus = 0;
    Button optimzerButton;
    TextView bluetoothTextView, brightnessTextView, lockscreenTextView, soundTextView;

    LinearLayout brightnessLinearLayout, lockscreenLinearLayout, soundLinearLayout, bluetoothLinearLayout;
    private static final int DELAY = 3000;
    int lockScreenTimeOut = 0;
    private int brightess = 0;
    int inWhichBrightness = 0;
    int WhatisLockScreenTimeOut = 0;
    int isBluetoothOn = 1; // 1 = bluetooth is off by default....
    BluetoothAdapter bluetoothAdapter;
    AudioManager audioManagerMode = null;
    int audioCurrentMode = 0;
    int inWhichSoundIndex;
    BatteryManager myBatteryManger;
    ImageView soundImageView;


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
        registerBatteryInfoReceiver();
        initializeSystemDatas();

        return view;
    }

    private void initializeSystemDatas() {

        DeviceBrightness();
        isBluetoothOnOff();
        ScreenLockOutTime();
        DeviceSoundStatus();
        GetBatteryLevel();


    }

    private void GetBatteryLevel() {

        myBatteryManger = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        //  Long batteryLevel =myBatteryManger.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);

        //  Log.d(TAG,"Battery Remainign Info= "+ String.valueOf(batteryLevel));
    }

    private void DeviceSoundStatus() {

//        Constant values
//        SilentMode =0;
//        RingtonMode=2;
//        VibrateMode=1;

        audioCurrentMode = audioManagerMode.getRingerMode();
        Log.d(TAG, "Audio mode" + String.valueOf(audioCurrentMode));
        switch (audioCurrentMode) {

            case AudioManager.RINGER_MODE_NORMAL:
                soundTextView.setText(R.string.soundText);
                soundImageView.setImageResource(R.drawable.sound);
                audioCurrentMode = AudioManager.RINGER_MODE_NORMAL;
                inWhichSoundIndex = 1;
                break;
            case AudioManager.RINGER_MODE_SILENT:
                soundTextView.setText(R.string.silentsound);
                audioCurrentMode = AudioManager.RINGER_MODE_SILENT;
                soundImageView.setImageResource(R.drawable.sound_silent);
                inWhichSoundIndex = 0;
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                soundTextView.setText(R.string.soundvibrate);
                audioCurrentMode = AudioManager.RINGER_MODE_VIBRATE;
                soundImageView.setImageResource(R.drawable.sound_vibrate);
                inWhichSoundIndex = 2;
                break;

        }


    }

    private void ScreenLockOutTime() {

        int inWhichIndexSeconds;
        int inWhichIndexMinutes;

        lockScreenTimeOut = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, DELAY);
        Log.d(TAG, "Timeout=" + String.valueOf(lockScreenTimeOut));

        inWhichIndexSeconds = lockScreenTimeOut / 1000;
        if (inWhichIndexSeconds <= 30) {
            if (inWhichIndexSeconds < 1) {
                WhatisLockScreenTimeOut = 0;
                lockscreenTextView.setText(R.string.autolock);

            } else if (inWhichIndexSeconds == 5) {
                WhatisLockScreenTimeOut = 1;
                lockscreenTextView.setText(R.string.fivesecond);

            } else if (inWhichIndexSeconds == 15) {
                WhatisLockScreenTimeOut = 2;
                lockscreenTextView.setText(R.string.fivesecond);

            } else if (inWhichIndexSeconds == 30) {
                WhatisLockScreenTimeOut = 3;
                lockscreenTextView.setText(R.string.thirtysecond);

            }

        } else {
            inWhichIndexMinutes = inWhichIndexSeconds / 60;
            if (inWhichIndexMinutes == 1) {
                WhatisLockScreenTimeOut = 4;
                lockscreenTextView.setText(R.string.oneminute);

            } else if (inWhichIndexMinutes == 2) {
                WhatisLockScreenTimeOut = 5;
                lockscreenTextView.setText(R.string.twominute);

            } else if (inWhichIndexMinutes == 5) {
                WhatisLockScreenTimeOut = 6;
                lockscreenTextView.setText(R.string.fiveminute);

            } else if (inWhichIndexMinutes == 10) {
                WhatisLockScreenTimeOut = 7;
                lockscreenTextView.setText(R.string.tenminute);

            } else if (inWhichIndexMinutes == 30) {
                WhatisLockScreenTimeOut = 8;
                lockscreenTextView.setText(R.string.thirtyminute);

            }
        }


    }

    private void isBluetoothOnOff() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()) {
            isBluetoothOn = 0;
            bluetoothTextView.setText("On");

        } else {
            bluetoothTextView.setText("Off");
        }

    }

    private void DeviceBrightness() {

        int whatIsScreenBrightnessLevel;
        brightess = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 20);
        Log.d(TAG, "The brightnessis:" + String.valueOf(brightess));
        whatIsScreenBrightnessLevel = brightess;


        if (whatIsScreenBrightnessLevel <= 26) {
            inWhichBrightness = 0;
            brightnessTextView.setText(R.string.zerobright);
        } else if (whatIsScreenBrightnessLevel > 26 && whatIsScreenBrightnessLevel <= 51) {
            inWhichBrightness = 1;
            brightnessTextView.setText(R.string.onebright);

        } else if (whatIsScreenBrightnessLevel > 51 && whatIsScreenBrightnessLevel <= 77) {
            inWhichBrightness = 2;
            brightnessTextView.setText(R.string.twobright);

        } else if (whatIsScreenBrightnessLevel > 77 && whatIsScreenBrightnessLevel <= 102) {
            inWhichBrightness = 3;
            brightnessTextView.setText(R.string.threebright);

        } else if (whatIsScreenBrightnessLevel > 102 && whatIsScreenBrightnessLevel <= 128) {
            inWhichBrightness = 4;
            brightnessTextView.setText(R.string.fourbright);

        } else if (whatIsScreenBrightnessLevel > 128 && whatIsScreenBrightnessLevel <= 153) {
            inWhichBrightness = 5;
            brightnessTextView.setText(R.string.fivebright);

        } else if (whatIsScreenBrightnessLevel > 153 && whatIsScreenBrightnessLevel <= 179) {
            inWhichBrightness = 6;
            brightnessTextView.setText(R.string.sixbright);

        } else if (whatIsScreenBrightnessLevel > 179 && whatIsScreenBrightnessLevel <= 204) {
            inWhichBrightness = 7;
            brightnessTextView.setText(R.string.sevenbright);

        } else if (whatIsScreenBrightnessLevel > 204 && whatIsScreenBrightnessLevel <= 230) {
            inWhichBrightness = 8;
            brightnessTextView.setText(R.string.eightbright);

        } else if (whatIsScreenBrightnessLevel > 230 && whatIsScreenBrightnessLevel <= 255) {
            inWhichBrightness = 9;
            brightnessTextView.setText(R.string.ninebright);

        }


    }

    private void registerBatteryInfoReceiver() {

        IntentFilter intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(battery_info_receiver, intentfilter);

    }

    private void initView(View view) {
        technologyTextView = (TextView) view.findViewById(R.id.technologyTextView);
        temperaturTextView = (TextView) view.findViewById(R.id.temperatureTextView);
        volatageTextView = (TextView) view.findViewById(R.id.voltageTextView);
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


    }

    private BroadcastReceiver battery_info_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            boolean isPresent = intent.getBooleanExtra("present", false);
            if (isPresent) {

                technology = intent.getStringExtra("technology");
                voltage = intent.getIntExtra("voltage", 0);
                temperature = intent.getIntExtra("temperature", 0);
                level = intent.getIntExtra("level", 0);
                scale = intent.getIntExtra("scale", 0);

                double doublevolt = voltage * 0.001;
                double doubletemp = temperature * 0.1;

                // Calculate the battery charged percentage
                float percentage = level / (float) scale;
                // Update the progress bar to display current battery charged percentage
                mProgressStatus = (int) ((percentage) * 100);


                batteryProgress.setProgress(mProgressStatus);

                volatageTextView.setText(String.format("%.1f", doublevolt) + "V");
                temperaturTextView.setText(String.format("%.1f", doubletemp) + (char) 0x00b0 + "C");
                technologyTextView.setText(technology);
                Bundle bundle = intent.getExtras();
                Log.d(TAG, bundle.toString());

                Log.d(TAG, "Technology=" + technology + "voltage=" + voltage + "Temperature=" + temperature);


            } else {

                showMessage("There is no battery");
            }

        }
    };

    private void showMessage(String s) {

        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        context.unregisterReceiver(battery_info_receiver);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.optimizerButton:
//                Intent optintent = new Intent(context, OptimizerActivity.class);
//                context.startActivity(optintent);

//                Intent BatteryAdsActivity for testing purpose

                Intent bintent = new Intent(context, BatteryAdActivity.class);
                context.startActivity(bintent);
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
                        if (which == 0) {
                            bluetoothAdapter.enable();
                            bluetoothTextView.setText("On");


                        } else {
                            bluetoothAdapter.disable();
                            bluetoothTextView.setText("Off");

                        }


                        return true;
                    }
                })
                .positiveText(R.string.choose)
                .show();
    }
}
