package com.receptix.batterybuddy.charge;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.receptix.batterybuddy.R;

import me.itangqi.waveloadingview.WaveLoadingView;

import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_LEVEL;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_SCALE;
import static com.receptix.batterybuddy.helper.Constants.BatteryParams.IS_BATTERY_PRESENT;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChargeFragment extends Fragment {

    // Custom View to show Charging Level as a Wave
    WaveLoadingView waveLoadingView;
    Context context;
    int batteryLevel, batteryScale, mProgressStatus;
    private BroadcastReceiver battery_info_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isBatteryPresent = intent.getBooleanExtra(IS_BATTERY_PRESENT, false);
            if (isBatteryPresent) {
                batteryLevel = intent.getIntExtra(BATTERY_LEVEL, 0);
                batteryScale = intent.getIntExtra(BATTERY_SCALE, 0);
                // Calculate the battery charged percentage
                float percentage = batteryLevel / (float) batteryScale;

                // Update the progress bar to display current battery charged percentage
                mProgressStatus = (int) ((percentage) * 100);

                waveLoadingView.setProgressValue(mProgressStatus);

                String progressValue = String.valueOf(mProgressStatus);
                //set title within the WaveView
                waveLoadingView.setCenterTitle(progressValue + "%");

            } else {
                showMessage(getString(R.string.no_battery_present));
            }
        }
    };


    public ChargeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewFragmentCharge = inflater.inflate(R.layout.fragment_charge, container, false);
        context = getActivity();
        initView(viewFragmentCharge);
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
    }

}
