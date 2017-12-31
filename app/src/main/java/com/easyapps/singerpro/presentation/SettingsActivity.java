package com.easyapps.singerpro.presentation;

import android.app.Activity;
import android.os.Bundle;

import com.easyapps.singerpro.presentation.components.TimerPreferenceFragment;
import com.easyapps.singerpro.presentation.helper.ActivityUtils;

/**
 * Created by daniel on 08/09/2016.
 * Settings activity for each lyric. The file name is received by parameter.
 */
public class SettingsActivity extends Activity {
    private String setList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setList = ActivityUtils.getPlaylistNameParameter(getIntent());
        String fileName = ActivityUtils.getFileNameParameter(getIntent());
        if (fileName == null)
            throw new RuntimeException("File not found.");

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                TimerPreferenceFragment.newInstance(fileName)).commit();
    }

    @Override
    public void onBackPressed() {
        ActivityUtils.backToMain(this, setList);
    }
}
