package com.easyapps.teleprompter.presentation;

import android.app.Activity;
import android.os.Bundle;

import com.easyapps.teleprompter.presentation.components.GlobalSettingsFragment;
import com.easyapps.teleprompter.presentation.components.TimerPreferenceFragment;
import com.easyapps.teleprompter.presentation.helper.ActivityUtils;

/**
 * Created by daniel on 28/06/2017.
 * Global Settings activity for entire application.
 */
public class GlobalSettingsActivity extends Activity {
    private String setList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setList = ActivityUtils.getSetListNameParameter(getIntent());

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new GlobalSettingsFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        ActivityUtils.backToMain(this, setList);
    }
}
