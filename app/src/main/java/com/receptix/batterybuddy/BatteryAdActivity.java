package com.receptix.batterybuddy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class BatteryAdActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout adsLinearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_ad);
        initView();

    }
    private void initView() {

        adsLinearLayout = (LinearLayout) findViewById(R.id.adsLinearLayout);
        adsLinearLayout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "I am clicked ", Toast.LENGTH_SHORT).show();
    }
}
