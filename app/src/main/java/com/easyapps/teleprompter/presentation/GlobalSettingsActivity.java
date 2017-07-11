package com.easyapps.teleprompter.presentation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.easyapps.teleprompter.R;
import com.easyapps.teleprompter.presentation.components.GlobalSettingsFragment;
import com.easyapps.teleprompter.presentation.components.TimerPreferenceFragment;
import com.easyapps.teleprompter.presentation.helper.ActivityUtils;
import com.easyapps.teleprompter.presentation.helper.PresentationConstants;

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
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case PresentationConstants.REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(getBaseContext(), R.string.bluetooth_enabled_successfully,
                            Toast.LENGTH_LONG).show();
                }
                else if (resultCode == RESULT_CANCELED){
                    Toast.makeText(getBaseContext(), R.string.bluetooth_enabled_denied,
                            Toast.LENGTH_LONG).show();
                    GlobalSettingsFragment fragment = (GlobalSettingsFragment)
                            getFragmentManager().findFragmentById(android.R.id.content);
                    fragment.clickOnBluetoothCheckbox();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        ActivityUtils.backToMain(this, setList);
    }
}
