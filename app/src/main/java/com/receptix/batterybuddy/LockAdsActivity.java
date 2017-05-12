package com.receptix.batterybuddy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class LockAdsActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout lockAdsLinearLayout;
    ImageView lockImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_lock_ads);
        initView();
    }

    private void initView() {
        lockImageView= (ImageView) findViewById(R.id.lockImageView);
        lockImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        showMessage("I am Clicked man");
    }

    private void showMessage(String s) {

        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }
}
