package com.receptix.batterybuddy.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import com.receptix.batterybuddy.R;

public class PrivacyActivity extends AppCompatActivity {
    Toolbar toolbar;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

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
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
