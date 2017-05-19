package com.receptix.batterybuddy.optimizeractivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.receptix.batterybuddy.R;

public class SuccessOptimizerActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView imageView_successfulOptimization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_optimizer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

       setupToolBar(getString(R.string.poweroptimization));

        findViewsById();

        // show animation for 3 seconds
        YoYo.with(Techniques.Shake)
                .repeat(10)
                .playOn(findViewById(R.id.imageview_successful_optimization));


       /*     // open AdsActivity after animation completes
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // show activity only if user is currently on this activity
                    if(!isFinishing()) {
                        *//*Intent backIntent = new Intent(SuccessOptimizerActivity.this, AfterOptimizerActivity.class);
                        startActivity(backIntent);*//*
                        finish();
                    }
                }
            }, 3000);*/




    }

    private void findViewsById() {
        imageView_successfulOptimization = (ImageView) findViewById(R.id.imageview_successful_optimization);
    }

    private void setupToolBar(String title) {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView textViewTitle = (TextView) toolbar.findViewById(R.id.textViewTitle);
        textViewTitle.setText(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
