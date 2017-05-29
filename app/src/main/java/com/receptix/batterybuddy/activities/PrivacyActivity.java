package com.receptix.batterybuddy.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.receptix.batterybuddy.R;

public class PrivacyActivity extends AppCompatActivity {
    Toolbar toolbar;
    WebView webView;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        context =getApplicationContext();

        setupToolBar(getString(R.string.privacy_policy));

        webView = (WebView) findViewById(R.id.webview_privacy_policy);
        String privacyPolicyData = getString(R.string.privacynotice);
        webView.loadData(privacyPolicyData, "text/html", "UTF-8");

    }

    private void setupToolBar(String title) {
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView textViewTitle = (TextView) toolbar.findViewById(R.id.textViewTitle);
        textViewTitle.setText(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }



    public void getUserDetails(){

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name","tenzin");
        jsonObject.addProperty("email","tenzin@gm,ail");
        String url ="tenzin.com/user.php";

        Ion.with(context)
                .load(url)
                .setJsonObjectBody(jsonObject)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {



                    }
                });
    }
}
