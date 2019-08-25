package com.easyapps.singerpro.presentation.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

import com.easyapps.singerpro.presentation.fragment.SettingsPreferenceFragment;
import com.easyapps.singerpro.presentation.helper.ActivityUtils;
import com.easyapps.singerpro.R;

import dagger.android.AndroidInjection;

/**
 * Created by daniel on 08/09/2016.
 * Settings activity for each lyric. The file name is received by parameter.
 */
public class SettingsActivity extends BaseActivity {
    private String mLyricName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        mLyricName = ActivityUtils.getLyricFileNameParameter(getIntent());
        if (mLyricName == null)
            throw new RuntimeException("File not found.");

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                SettingsPreferenceFragment.newInstance(mLyricName)).commit();

        setTitle(getString(R.string.title_activity_settings));
    }

    @Override
    public void onBackPressed() {
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        int orientation = getResources().getConfiguration().orientation;

        if (isTablet && orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ActivityUtils.backToMain(this);
        } else {
            ActivityUtils.backToCaller(this, mLyricName);
        }
    }
}
