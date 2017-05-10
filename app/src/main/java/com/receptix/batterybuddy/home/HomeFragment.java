package com.receptix.batterybuddy.home;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.DecimalFormat;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.receptix.batterybuddy.R;
import com.receptix.batterybuddy.optimizeractivity.OptimizerActivity;

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
    TextView bluetoothTextView;

    LinearLayout brightnessLinearLayout, lockscreenLinearLayout, soundLinearLayout, bluetoothLinearLayout;
    private static final int DELAY = 3000;
    int lockScreenTimeOut = 0;


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

        initView(view);
        registerBatteryInfoReceiver();

        return view;
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
                Intent optintent = new Intent(context, OptimizerActivity.class);
                context.startActivity(optintent);
                break;
            case R.id.brightnessLinearLayout:
                showMessage("I am brightness");
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

                break;

        }
    }

    private void SetLockScreenTimer() {
        int WhatisLockScreenTimeOut = 0;
        int inWhichIndexSeconds;
        int inWhichIndexMinutes;

        lockScreenTimeOut = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, DELAY);
        Log.d(TAG, "Timeout=" + String.valueOf(lockScreenTimeOut));

        inWhichIndexSeconds = lockScreenTimeOut / 1000;
        if (inWhichIndexSeconds <= 30) {
            if (inWhichIndexSeconds < 1) {
                WhatisLockScreenTimeOut = 0;

            } else if (inWhichIndexSeconds == 5) {
                WhatisLockScreenTimeOut = 1;
            } else if (inWhichIndexSeconds == 15) {
                WhatisLockScreenTimeOut = 2;
            } else if (inWhichIndexSeconds == 30) {
                WhatisLockScreenTimeOut = 3;
            }

        } else {
            inWhichIndexMinutes = inWhichIndexSeconds / 60;
            if (inWhichIndexMinutes == 1) {
                WhatisLockScreenTimeOut = 4;
            } else if (inWhichIndexMinutes == 2) {
                WhatisLockScreenTimeOut = 5;
            } else if (inWhichIndexMinutes == 5) {
                WhatisLockScreenTimeOut = 6;
            } else if (inWhichIndexMinutes == 10) {
                WhatisLockScreenTimeOut = 7;
            } else if (inWhichIndexMinutes == 30) {
                WhatisLockScreenTimeOut = 8;
            }
        }


        new MaterialDialog.Builder(context)
                .titleColorRes(R.color.white)
                .contentColorRes(R.color.white)
                .positiveColorRes(R.color.white)
                .title(R.string.lockScreenTimeouttitle)
                .items(R.array.sreenlocktimer)
                .widgetColorRes(R.color.white)
                .backgroundColorRes(R.color.bgcolor)
                .itemsCallbackSingleChoice(WhatisLockScreenTimeOut, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        int timeout=0;

                        /**
                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected radio button to actually be selected.
                         **/
                        Toast.makeText(context, which + ": " + text + ", ID = " + view.getId(), Toast.LENGTH_SHORT).show();
                        if(which==3){


                            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT,150000);
                        }
//                        switch (which){
//
//                            case 0:
//                                timeout=0;
//                                Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT, timeout);
//
//                            case 1:
//                                timeout=5000;
//                                Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT, timeout);
//
//                            case 2:
//                                timeout=15000;
//                                Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT, timeout);
//
//                            case 3:
//                                timeout=30000;
//                                Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT, timeout);
//
//                            case 4:
//                                timeout=60000;
//                                Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT, timeout);
//
//                            case 5:
//                                timeout=120000;
//                                Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT, timeout);
//
//                            case 6:
//                                timeout=300000;
//                                Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT, timeout);
//
//                            case 7:
//                                timeout=600000;
//                                Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT, timeout);
//
//                            case 8:
//                                timeout=1800000;
//                                Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT, timeout);
//                        }


                        return true;
                    }
                })
                .positiveText(R.string.choose)
                .show();


    }

    private void OnOffBluetooth() {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        int isBluetoothOn = 1; // 1 = bluetooth is off by default....
        if (bluetoothAdapter.isEnabled()) {
            isBluetoothOn = 0;

        }
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
