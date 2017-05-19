package com.receptix.batterybuddy.optimizeractivity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.receptix.batterybuddy.R;

public class OptimalStateActivity extends AppCompatActivity {
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optimal_state);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setupToolBar(getString(R.string.optimalStateTextView));


    }

    private void setupToolBar(String title) {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView textViewTitle = (TextView) toolbar.findViewById(R.id.textViewTitle);
        textViewTitle.setText(title);


    }

    @Override
    protected void onStart() {
        super.onStart();
       /* final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                Intent backIntent = new Intent(OptimalStateActivity.this, AfterOptimizerActivity.class);
                startActivity(backIntent);
                finish();
            }
        }, 2000);*/
    }
}
