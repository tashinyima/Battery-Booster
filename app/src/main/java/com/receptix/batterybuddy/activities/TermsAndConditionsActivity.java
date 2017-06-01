package com.receptix.batterybuddy.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.JsonObject;
import com.receptix.batterybuddy.R;
import com.receptix.batterybuddy.helper.UserSessionManager;
import com.receptix.batterybuddy.receiver.AlarmReceiver;

import java.util.Calendar;

public class TermsAndConditionsActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textViewPrivacyPolicy;
    UserSessionManager userSessionManager;
    Context context;
    Button agreeBtn;
    JsonObject jsonObject = new JsonObject();
    PendingIntent pendingIntent;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tc);
        context = getApplicationContext();
        userSessionManager = new UserSessionManager(context);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        initView();
        startAlarm();


    }

    private void startAlarm() {
        try {
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(TermsAndConditionsActivity.this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(TermsAndConditionsActivity.this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HALF_DAY, pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
