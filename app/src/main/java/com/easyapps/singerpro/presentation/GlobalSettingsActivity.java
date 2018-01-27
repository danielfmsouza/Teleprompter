package com.easyapps.singerpro.presentation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.easyapps.singerpro.presentation.fragments.GlobalSettingsFragment;
import com.easyapps.singerpro.presentation.helper.ActivityUtils;

/**
 * Created by daniel on 28/06/2017.
 * Global Settings activity for entire application.
 */
public class GlobalSettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new GlobalSettingsFragment())
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

    }

    @Override
    public void onBackPressed() {
        ActivityUtils.backToMain(this);
    }
}
