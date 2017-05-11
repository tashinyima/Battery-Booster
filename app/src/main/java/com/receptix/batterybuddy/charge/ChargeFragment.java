package com.receptix.batterybuddy.charge;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.receptix.batterybuddy.R;

import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChargeFragment extends Fragment {

    WaveLoadingView waveLoadingView;
    Context context;
    int level, scale, mProgressStatus;


    public ChargeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_charge, container, false);
        context = getActivity();

        initView(view);

        registerBatteryInfoReceiver();

        return view;
    }

    private void registerBatteryInfoReceiver() {

        IntentFilter intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(battery_info_receiver, intentfilter);

    }

    private BroadcastReceiver battery_info_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            boolean isPresent = intent.getBooleanExtra("present", false);
            if (isPresent) {


                level = intent.getIntExtra("level", 0);
                scale = intent.getIntExtra("scale", 0);


                // Calculate the battery charged percentage
                float percentage = level / (float) scale;
                // Update the progress bar to display current battery charged percentage
                mProgressStatus = (int) ((percentage) * 100);

                waveLoadingView.setProgressValue(mProgressStatus);
                String progressValue = String.valueOf(mProgressStatus);
                waveLoadingView.setCenterTitle(progressValue+"%");


            } else {

                showMessage("There is no battery");
            }

        }
    };

    private void showMessage(String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

    private void initView(View view) {

        waveLoadingView = (WaveLoadingView) view.findViewById(R.id.waveLoadingView);


    }

}
