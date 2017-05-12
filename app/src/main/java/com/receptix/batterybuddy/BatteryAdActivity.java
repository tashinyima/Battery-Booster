package com.receptix.batterybuddy;

import android.app.ActivityManager;
import android.content.Context;
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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class BatteryAdActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG ="AdsActivity";
    LinearLayout adsLinearLayout, swipeLinearLayout;
    ActivityManager myActivityManager;
    Context context;
    ArcProgress ramprogress;
    TextView dateTextView;
    Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_battery_ad);
        myActivityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        context = getApplicationContext();
        initView();
        InitializeSystemData();

       // getApplicationDetails();

    }

    private void getApplicationDetails() {


        ActivityManager mgr = (ActivityManager)context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = mgr.getRunningAppProcesses();
        Log.e("DEBUG", "Running processes:");
        for(Iterator i = processes.iterator(); i.hasNext(); )
        {
            ActivityManager.RunningAppProcessInfo p = (ActivityManager.RunningAppProcessInfo)i.next();
            Log.e("DEBUG", "  process name: "+p.processName);
            Log.e("DEBUG", "     pid: "+p.pid);
            int[] pids = new int[1];
            pids[0] = p.pid;
            android.os.Debug.MemoryInfo[] MI = mgr.getProcessMemoryInfo(pids);
            Log.e("memory","     dalvik private: " + MI[0].dalvikPrivateDirty);
            Log.e("memory","     dalvik shared: " + MI[0].dalvikSharedDirty);
            Log.e("memory","     dalvik pss: " + MI[0].dalvikPss);
            Log.e("memory","     native private: " + MI[0].nativePrivateDirty);
            Log.e("memory","     native shared: " + MI[0].nativeSharedDirty);
            Log.e("memory","     native pss: " + MI[0].nativePss);
            Log.e("memory","     other private: " + MI[0].otherPrivateDirty);
            Log.e("memory","     other shared: " + MI[0].otherSharedDirty);
            Log.e("memory","     other pss: " + MI[0].otherPss);

            Log.e("memory","     total private dirty memory (KB): " + MI[0].getTotalPrivateDirty());
            Log.e("memory","     total shared (KB): " + MI[0].getTotalSharedDirty());
            Log.e("memory","     total pss: " + MI[0].getTotalPss());
        }
    }

    private void InitializeSystemData() {
        getRamInfo();
        getSystemDateNow();

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
        long blabla =memInfo.availMem;
        long total =memInfo.totalMem;

        long  usedram = total-blabla;



        double ratio = usedram / (double) total;
        double percentage =ratio*100;
        int rampercentage = (int) percentage;
        if(rampercentage>70){
            ramprogress.setProgress(rampercentage);
            ramprogress.setFinishedStrokeColor(ContextCompat.getColor(context,R.color.red));
            ramprogress.setTextColor(ContextCompat.getColor(context,R.color.red));
        }else {
            ramprogress.setProgress(rampercentage);
        }



        Log.d(TAG,"MEM="+String.valueOf(blabla)+"Total Ram="+String.valueOf(total)+"Percentage="+rampercentage);


    }

    private void initView() {

        adsLinearLayout = (LinearLayout) findViewById(R.id.adsLinearLayout);
        adsLinearLayout.setOnClickListener(this);
        swipeLinearLayout = (LinearLayout) findViewById(R.id.swipeLinearLayout);
        swipeLinearLayout.setOnClickListener(this);
        ramprogress = (ArcProgress) findViewById(R.id.ramArcProgress);
        dateTextView = (TextView) findViewById(R.id.datetv);


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
