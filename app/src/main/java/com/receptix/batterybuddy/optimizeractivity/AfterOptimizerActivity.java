package com.receptix.batterybuddy.optimizeractivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.receptix.batterybuddy.R;

public class AfterOptimizerActivity extends AppCompatActivity {
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_optimizer);

        initView();
        setupToolBar(getString(R.string.powerconsumptionToolbar));
    }

    private void setupToolBar(String title) {

        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView textViewTitle = (TextView) toolbar.findViewById(R.id.textViewTitle);
        textViewTitle.setText(title);


    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

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
