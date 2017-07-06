package com.easyapps.teleprompter.presentation.components;

import android.os.Bundle;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.easyapps.teleprompter.R;

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

//        PreferenceScreen preferenceScreen = this.getPreferenceScreen();
//        PreferenceCategory bluetoothCategory =
//                (PreferenceCategory)preferenceScreen.getPreference(2);
//
//        // create preferences manually
//        PreferenceCategory preferenceCategory = new PreferenceCategory(preferenceScreen.getContext());
//        preferenceCategory.setTitle(getResources().getString(R.string.pref_bluetoothPairedDevices));
//        bluetoothCategory.addPreference(preferenceCategory);

//        Preference preference = new Preference(preferenceScreen.getContext());
//        preferencey.setTitle("yourTitle");
//        //do anything you want with the preferencey here
//        preferenceCategory.addPreference(preference);
    }
}