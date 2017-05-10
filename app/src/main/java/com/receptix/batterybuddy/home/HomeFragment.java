package com.receptix.batterybuddy.home;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.receptix.batterybuddy.R;
import com.receptix.batterybuddy.optimizeractivity.OptimizerActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG ="BatteryBuddy";
    View view;
    Context context;
    Toolbar toolbar;
    TextView temperaturTextView,volatageTextView,technologyTextView;
    String technology;
    int voltage,temperature,level,scale;
    DonutProgress batteryProgress;
    private int mProgressStatus = 0;
    Button optimzerButton;





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

        IntentFilter intentfilter= new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(battery_info_receiver,intentfilter);

    }

    private void initView(View view) {
        technologyTextView = (TextView) view.findViewById(R.id.technologyTextView);
        temperaturTextView = (TextView) view.findViewById(R.id.temperatureTextView);
        volatageTextView = (TextView) view.findViewById(R.id.voltageTextView);
        batteryProgress = (DonutProgress) view.findViewById(R.id.homebatteryProgress);
        optimzerButton = (Button) view.findViewById(R.id.optimizerButton);
        optimzerButton.setOnClickListener(this);


    }

    private BroadcastReceiver battery_info_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            boolean isPresent = intent.getBooleanExtra("present",false);
            if(isPresent){

                technology =intent.getStringExtra("technology");
                voltage = intent.getIntExtra("voltage",0);
                temperature = intent.getIntExtra("temperature",0);
                level = intent.getIntExtra("level",0);
                scale= intent.getIntExtra("scale",0);

                double doublevolt=voltage*0.001;
                double doubletemp=temperature*0.1;

                // Calculate the battery charged percentage
                float percentage = level/ (float) scale;
                // Update the progress bar to display current battery charged percentage
                mProgressStatus = (int)((percentage)*100);


                batteryProgress.setProgress(mProgressStatus);

                volatageTextView.setText(String.format("%.1f",doublevolt)+"V");
                temperaturTextView.setText(String.format("%.1f",doubletemp)+(char)0x00b0+"C");
                technologyTextView.setText(technology);
                Bundle bundle = intent.getExtras();
                Log.d(TAG,bundle.toString());

                Log.d(TAG,"Technology="+technology+"voltage="+voltage+"Temperature="+temperature);


            }else {

                showMessage("There is no battery");
            }

        }
    };

    private void showMessage(String s) {

        Toast.makeText(context,s,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        context.unregisterReceiver(battery_info_receiver);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.optimizerButton){

            Intent optintent = new Intent(context, OptimizerActivity.class);
            context.startActivity(optintent);

        }
    }
}
