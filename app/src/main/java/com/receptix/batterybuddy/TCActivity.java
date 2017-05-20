package com.receptix.batterybuddy;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.receptix.batterybuddy.activities.PrivacyActivity;
import com.receptix.batterybuddy.activities.TermsPolicyActivity;
import com.receptix.batterybuddy.general.UserSessionManager;

public class TCActivity extends AppCompatActivity implements View.OnClickListener {

    TextView secondtextView,fourthtextView;
    UserSessionManager userSessionManager;
    Context context;
    Button agreeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tc);
         context =getApplicationContext();
         userSessionManager = new UserSessionManager(context);
        initView();
    }

    private void initView() {

        secondtextView = (TextView) findViewById(R.id.secondtv);
        fourthtextView = (TextView) findViewById(R.id.fourthtv);
        agreeBtn = (Button) findViewById(R.id.useragreeBtn);
        agreeBtn.setOnClickListener(this);

        secondtextView.setOnClickListener(this);
        fourthtextView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.secondtv){

            startActivity(new Intent(TCActivity.this, TermsPolicyActivity.class));


        }else if(v.getId()==R.id.fourthtv){

             startActivity(new Intent(TCActivity.this, PrivacyActivity.class));

        }else if(v.getId()==R.id.useragreeBtn){


            startActivity(new Intent(TCActivity.this,NavigationActivity.class));
            userSessionManager.setIsFirstTime(false);
            finish();
        }

    }
}
