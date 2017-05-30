package com.receptix.batterybuddy.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.receptix.batterybuddy.R;
import com.receptix.batterybuddy.helper.LogUtil;

import java.lang.reflect.Method;

import me.itangqi.waveloadingview.WaveLoadingView;

import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_LEVEL;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_SCALE;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.IS_BATTERY_PRESENT;
import static com.receptix.batterybuddy.helper.Constants.CurrentValues.CURRENT_RATE_AC;
import static com.receptix.batterybuddy.helper.Constants.CurrentValues.CURRENT_RATE_USB;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.BATTERY_CAPACITY;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.METHOD_GET_AVERAGE_POWER;
import static com.receptix.batterybuddy.helper.Constants.PowerProfileParams.POWER_PROFILE_CLASS;
import static com.receptix.batterybuddy.helper.Constants.ShortHandNotations.HOURS;
import static com.receptix.batterybuddy.helper.Constants.ShortHandNotations.MINUTES;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChargeFragment extends Fragment {

    // Custom View to show Charging Level as a Wave
    WaveLoadingView waveLoadingView;
    Context context;
    int batteryLevel, batteryScale, batteryChargedPercentage;
    private TextView textViewChargeTimeRemaining;
    private TextView textViewEtaHeading;
    private long mShortAnimationDuration = 300;
    private BroadcastReceiver battery_info_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent batteryStatus) {
            boolean isBatteryPresent = batteryStatus.getBooleanExtra(IS_BATTERY_PRESENT, false);
            if (isBatteryPresent) {
                batteryLevel = batteryStatus.getIntExtra(BATTERY_LEVEL, 0);
                batteryScale = batteryStatus.getIntExtra(BATTERY_SCALE, 0);
                // Calculate the battery charged percentage
                float percentage = batteryLevel / (float) batteryScale;

                // Update the progress bar to display current battery charged percentage
                batteryChargedPercentage = (int) ((percentage) * 100);

                waveLoadingView.setProgressValue(batteryChargedPercentage);

                String progressValue = String.valueOf(batteryChargedPercentage);
                //set title within the WaveView
                waveLoadingView.setCenterTitle(progressValue + "%");

                // pause charging animation and wave color, depending on charging status
                int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL;
                /*Toast.makeText(context, "isCharging = " + isCharging , Toast.LENGTH_SHORT).show();*/
                if(!isCharging)
                {
                    waveLoadingView.pauseAnimation();
                    textViewChargeTimeRemaining.setVisibility(View.GONE);
                    textViewEtaHeading.setVisibility(View.GONE);
//          waveLoadingView.setWaveColor(getContext().getResources().getColor(android.R.color.holo_green_dark));
              waveLoadingView.setWaveColor(ContextCompat.getColor(context,android.R.color.holo_green_dark));
                }
                else {
                    waveLoadingView.resumeAnimation();
                  //   waveLoadingView.setWaveColor(getContext().getResources().getColor(R.color.colorGreen));  Depreciated getcolor
                    waveLoadingView.setWaveColor(ContextCompat.getColor(context,R.color.colorGreen));

                    try {
                        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
                        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
                        int currentDelivered = 0;
                        if (usbCharge) {
                            currentDelivered = CURRENT_RATE_USB;
                        } else if (acCharge) {
                            currentDelivered = CURRENT_RATE_AC;
                        }
                        double totalBatteryCapacity = getBatteryCapacity();
                        int batteryCapacityCharged = batteryChargedPercentage;
                        int percentageRemaining = 100 - batteryCapacityCharged;
                        int batteryCapacityLeftToCharge = (int) (percentageRemaining * totalBatteryCapacity) / 100;
                        // time left to charge = capacity left to charge / current delivered
                        double timeLeftToCharge = (double) batteryCapacityLeftToCharge / currentDelivered;
                        LogUtil.e("timeLeftToCharge", timeLeftToCharge + "");
                        int hours = (int) timeLeftToCharge;
                        //obtain actual number of minutes from double value of Hours
                        String stringminutes = String.valueOf(String.format("%.2f", timeLeftToCharge));
                        String lasttwo = stringminutes.substring(stringminutes.length() - 2);
                        int minute = Integer.parseInt(lasttwo);
                        int realminutes = (minute * 60) / 100;
                        String valueGreaterThanEqualToOneHour = String.valueOf(hours) + HOURS + " " + String.valueOf(realminutes) + " " + MINUTES;
                        String valueLessThanOneHour = String.valueOf(realminutes) + " " + MINUTES;
                        textViewChargeTimeRemaining.setVisibility(View.VISIBLE);
                        textViewEtaHeading.setVisibility(View.VISIBLE);
                        if (timeLeftToCharge > 0.9) {
                            textViewChargeTimeRemaining.setText(valueGreaterThanEqualToOneHour);
                        } else {
                            textViewChargeTimeRemaining.setText(valueLessThanOneHour);
                        }

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }

            } else {
                showMessage(getString(R.string.no_battery_present));
            }
        }
    };


    public ChargeFragment() {
        // Required empty public constructor
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewFragmentCharge = inflater.inflate(R.layout.fragment_charge, container, false);
        context = getActivity();
        initView(viewFragmentCharge);
        textViewChargeTimeRemaining.setVisibility(View.GONE);
        textViewEtaHeading.setVisibility(View.GONE);

        //fade in the WaveLoadingView
        waveLoadingView.setAlpha(0f);
        waveLoadingView.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        waveLoadingView.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        return viewFragmentCharge;
    }

    /**
     * Subscribe to Battery Info Broadcast Receiver.
     */
    private void registerBatteryInfoReceiver() {
        IntentFilter intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(battery_info_receiver, intentfilter);
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

    private void showMessage(String messageToShow) {
        Toast.makeText(context, messageToShow, Toast.LENGTH_LONG).show();
    }

    private void initView(View view) {
        waveLoadingView = (WaveLoadingView) view.findViewById(R.id.waveLoadingView);
        textViewChargeTimeRemaining = (TextView) view.findViewById(R.id.chargeRemainingTextView);
        textViewEtaHeading = (TextView) view.findViewById(R.id.textview_eta_heading);
    }

}
