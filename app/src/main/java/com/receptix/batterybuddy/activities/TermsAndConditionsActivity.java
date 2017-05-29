package com.receptix.batterybuddy.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.receptix.batterybuddy.R;
import com.receptix.batterybuddy.helper.LogUtil;
import com.receptix.batterybuddy.helper.UserSessionManager;
import com.receptix.batterybuddy.helper.Utils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.receptix.batterybuddy.helper.Constants.APPS_FLYER_ATTRIBUTES.CLICK_ID;
import static com.receptix.batterybuddy.helper.Constants.APPS_FLYER_ATTRIBUTES.CLICK_TIME;
import static com.receptix.batterybuddy.helper.Constants.APPS_FLYER_ATTRIBUTES.INSTALL_TIME;
import static com.receptix.batterybuddy.helper.Constants.APPS_FLYER_ATTRIBUTES.INSTALL_TYPE;
import static com.receptix.batterybuddy.helper.Constants.APPS_FLYER_ATTRIBUTES.MEDIA_SOURCE;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.COUNTRY;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.DEFAULT_LAUNCHER;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.DEVICE_ID;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.DEVICE_INFO;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.EMAILS;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.ETHERNET;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.INSTALLED_APPS;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.IP_ADDRESS;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.LOCATION;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.MAC_ADDRESS;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.REQUEST_OBJECT;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.RESPONSE_OBJECT;
import static com.receptix.batterybuddy.helper.Constants.JsonProperties.WLAN;
import static com.receptix.batterybuddy.helper.Constants.Urls.URL_OZOCK;

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
       if(v.getId()==R.id.textview_privacy_policy){
             startActivity(new Intent(TermsAndConditionsActivity.this, PrivacyActivity.class));
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        }else if(v.getId()==R.id.useragreeBtn){
            startActivity(new Intent(TermsAndConditionsActivity.this,NavigationActivity.class));
            userSessionManager.setIsFirstTime(false);
            finish();
        }

    }
}
