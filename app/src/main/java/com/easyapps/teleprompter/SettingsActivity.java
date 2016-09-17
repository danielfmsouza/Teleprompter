package com.easyapps.teleprompter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.easyapps.teleprompter.components.NumberPickerPreference;
import com.easyapps.teleprompter.helper.ActivityUtils;
import com.easyapps.teleprompter.messages.Constants;

/**
 * Created by danielfmsouza on 08/09/2016.
 */
public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
    }


    public static class PrefsFragment extends PreferenceFragment {

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);

        finish();
    }
}
