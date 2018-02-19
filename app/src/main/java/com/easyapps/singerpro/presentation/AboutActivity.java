package com.easyapps.singerpro.presentation;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

import com.easyapps.singerpro.R;
import com.easyapps.singerpro.presentation.helper.ActivityUtils;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView aboutText = (TextView) findViewById(R.id.tvAbout);
        Linkify.addLinks(aboutText, Linkify.WEB_URLS);
        hideActionBar();
    }

    private void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    public void onBackPressed() {
        ActivityUtils.backToMain(this);
    }
}
