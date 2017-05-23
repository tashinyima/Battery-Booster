package com.receptix.batterybuddy.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.receptix.batterybuddy.R;
import com.receptix.batterybuddy.helper.UserSessionManager;

public class TermsAndConditionsActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textViewTermsOfService, textViewPrivacyPolicy;
    UserSessionManager userSessionManager;
    Context context;
    Button agreeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tc);
         context = getApplicationContext();
         userSessionManager = new UserSessionManager(context);
        initView();
    }

    private void initView() {

        textViewTermsOfService = (TextView) findViewById(R.id.textview_terms_of_service);
        textViewPrivacyPolicy = (TextView) findViewById(R.id.textview_privacy_policy);
        agreeBtn = (Button) findViewById(R.id.useragreeBtn);
        agreeBtn.setOnClickListener(this);

        textViewTermsOfService.setOnClickListener(this);
        textViewPrivacyPolicy.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.textview_terms_of_service){
            startActivity(new Intent(TermsAndConditionsActivity.this, TermsPolicyActivity.class));
        }else if(v.getId()==R.id.textview_privacy_policy){
             startActivity(new Intent(TermsAndConditionsActivity.this, PrivacyActivity.class));
        }else if(v.getId()==R.id.useragreeBtn){
            startActivity(new Intent(TermsAndConditionsActivity.this,NavigationActivity.class));
            userSessionManager.setIsFirstTime(false);
            finish();
        }

    }
}
