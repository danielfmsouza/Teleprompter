package com.easyapps.singerpro.presentation.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.easyapps.singerpro.R;
import com.easyapps.singerpro.infrastructure.communication.bluetooth.BluetoothScreenShareServer;
import com.easyapps.singerpro.presentation.component.NumberPickerPreference;
import com.easyapps.singerpro.presentation.helper.PresentationConstants;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.easyapps.singerpro.R.xml.global_preferences;

/**
 * screen for global settings of the application
 * Created by daniel on 28/06/2017.
 */

public class GlobalSettingsFragment extends PreferenceFragment {

    private BluetoothScreenShareServer mBluetoothScreenShare;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(global_preferences);
        mBluetoothScreenShare = new BluetoothScreenShareServer();

        final PreferenceScreen preferenceScreen = this.getPreferenceScreen();
        setPlayNextCheckboxBehavior(preferenceScreen);
        verifyPlayNextActivated();

//        PreferenceCategory pcBluetooth = new PreferenceCategory(preferenceScreen.getContext());
//        pcBluetooth.setTitle(getResources().getString(R.string.pref_bluetooth));
//        preferenceScreen.addPreference(pcBluetooth);
//
//        final CheckBoxPreference cbpBluetoothEnabled =
//                new CheckBoxPreference(preferenceScreen.getContext());
//        cbpBluetoothEnabled.setChecked(mBluetoothScreenShare.isProtocolEnabled());
//        cbpBluetoothEnabled.setKey(getResources().getString(R.string.pref_isBluetoothEnabled));
//        cbpBluetoothEnabled.setTitle(R.string.pref_isBluetoothEnabled);
//        cbpBluetoothEnabled.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                if (cbpBluetoothEnabled.isChecked()) {
//                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(enableBtIntent, PresentationConstants.REQUEST_ENABLE_BT);
//                } else {
//                    mBluetoothScreenShare.disable();
//                    Toast.makeText(getActivity().getBaseContext(),
//                            R.string.bluetooth_disabled_successfully,
//                            Toast.LENGTH_LONG).show();
//                }
//                return true;
//            }
//        });
//        pcBluetooth.addPreference(cbpBluetoothEnabled);
//        setPairedDevices(preferenceScreen);
    }

    private void setPlayNextCheckboxBehavior(final PreferenceScreen preferenceScreen) {
        final CheckBoxPreference cpbPlayNext = (CheckBoxPreference)
                preferenceScreen.findPreference(
                        getResources().getString(R.string.pref_key_playNext));

        cpbPlayNext.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (cpbPlayNext.isChecked()) {
                    activateTimeBeforeNextOption(preferenceScreen);
                } else {
                    deactivateTimeBeforeNextOption(preferenceScreen);
                }
                return true;
            }
        });
    }

    private void setPairedDevices(final PreferenceScreen preferenceScreen) {
        if (mBluetoothScreenShare.isProtocolEnabled()) {
            for (BluetoothDevice pairedDevice : mBluetoothScreenShare.getPairedDevices()) {
                final Preference pPairedDevice =
                        new Preference(preferenceScreen.getContext());
                pPairedDevice.setTitle(pairedDevice.getName());

                preferenceScreen.addPreference(pPairedDevice);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PresentationConstants.REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(getActivity().getBaseContext(),
                            R.string.bluetooth_enabled_successfully,
                            Toast.LENGTH_LONG).show();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getActivity().getBaseContext(),
                            R.string.bluetooth_enabled_denied, Toast.LENGTH_LONG).show();
                    GlobalSettingsFragment fragment = (GlobalSettingsFragment)
                            getFragmentManager().findFragmentById(android.R.id.content);
                    fragment.clickOnBluetoothCheckbox();
                }
                break;
        }
    }

    public void clickOnBluetoothCheckbox() {
        PreferenceScreen preferenceScreen = this.getPreferenceScreen();
        CheckBoxPreference cbpBluetooth = (CheckBoxPreference)
                preferenceScreen.findPreference(
                        getResources().getString(R.string.pref_isBluetoothEnabled));

        if (cbpBluetooth != null) {
            cbpBluetooth.setChecked(!cbpBluetooth.isChecked());
        }
    }

    public void verifyPlayNextActivated() {
        PreferenceScreen preferenceScreen = this.getPreferenceScreen();
        CheckBoxPreference cpbPlayNext = (CheckBoxPreference)
                preferenceScreen.findPreference(
                        getResources().getString(R.string.pref_key_playNext));

        if (cpbPlayNext.isChecked()) {
            activateTimeBeforeNextOption(preferenceScreen);
        } else {
            deactivateTimeBeforeNextOption(preferenceScreen);
        }
    }

    private void activateTimeBeforeNextOption(PreferenceScreen preferenceScreen) {
        NumberPickerPreference nppTimeBeforeNext = (NumberPickerPreference)
                preferenceScreen.findPreference(
                        getResources().getString(R.string.pref_key_timeBeforeStart));

        nppTimeBeforeNext.setEnabled(true);
    }

    private void deactivateTimeBeforeNextOption(PreferenceScreen preferenceScreen) {
        NumberPickerPreference nppTimeBeforeNext = (NumberPickerPreference)
                preferenceScreen.findPreference(
                        getResources().getString(R.string.pref_key_timeBeforeStart));

        nppTimeBeforeNext.setEnabled(false);
    }
}