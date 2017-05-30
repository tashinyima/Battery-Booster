package com.receptix.batterybuddy.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.appsflyer.AppsFlyerLib;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.JsonObject;
import com.receptix.batterybuddy.R;
import com.receptix.batterybuddy.helper.UserSessionManager;

public class TermsAndConditionsActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textViewPrivacyPolicy;
    UserSessionManager userSessionManager;
    Context context;
    Button agreeBtn;
    JsonObject jsonObject = new JsonObject();
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tc);
        context = getApplicationContext();
        userSessionManager = new UserSessionManager(context);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        AppsFlyerLib.getInstance().startTracking(this.getApplication(), getString(R.string.apps_flyer_dev_key));
        /*
         #AppsFlyer: registerConversionListener implements the collection of attribution (conversion) data
         Please refer to this documentation to view all the available attribution parameters:
         https://support.appsflyer.com/hc/en-us/articles/207032096-Accessing-AppsFlyer-Attribution-Conversion-Data-from-the-SDK-Deferred-Deeplinking
         */

        initView();


    }

    private void initView() {

        textViewPrivacyPolicy = (TextView) findViewById(R.id.textview_privacy_policy);
        agreeBtn = (Button) findViewById(R.id.useragreeBtn);
        agreeBtn.setOnClickListener(this);

        textViewPrivacyPolicy.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.textview_privacy_policy) {
            startActivity(new Intent(TermsAndConditionsActivity.this, PrivacyActivity.class));
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        } else if (v.getId() == R.id.useragreeBtn) {
            startActivity(new Intent(TermsAndConditionsActivity.this, NavigationActivity.class));
            userSessionManager.setIsFirstTime(false);
            finish();
        }

    }
}
