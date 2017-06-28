package com.easyapps.teleprompter.presentation.components;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import static com.easyapps.teleprompter.R.xml.global_preferences;

/**
 * screen for global settings of the application
 * Created by daniel on 28/06/2017.
 */

public class GlobalSettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(global_preferences);
    }
}