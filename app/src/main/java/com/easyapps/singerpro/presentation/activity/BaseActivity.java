package com.easyapps.singerpro.presentation.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.Html;

import com.easyapps.singerpro.R;

import java.text.MessageFormat;

public class BaseActivity extends AppCompatActivity {

    private int titleTextColor;
    private final String FORMATTED_TITLE = "<font color=\"{0}\"><b>{1}</b></font>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        titleTextColor = getResources().getColor(R.color.colorPrimaryDark);

        setPrimaryStatusBarColor(titleTextColor);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void setTitle(CharSequence title) {
        if (title == null) title = "";

        String formattedTitle = MessageFormat.format(
                FORMATTED_TITLE,
                String.valueOf(titleTextColor),
                getString(R.string.app_name) + " - " + title);

        super.setTitle(Html.fromHtml(formattedTitle));
    }

    private void setPrimaryStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (getWindow() != null)
                getWindow().setStatusBarColor(color);
        }
    }
}
