package com.easyapps.teleprompter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.easyapps.teleprompter.components.PrompterView;
import com.easyapps.teleprompter.helper.ActivityUtils;

import java.io.FileNotFoundException;
import java.io.IOException;

public class PrompterActivity extends AppCompatActivity {

    private PrompterView mPrompter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideUI();

        setContentView(R.layout.activity_prompter);
        mPrompter = (PrompterView) findViewById(R.id.fullscreen_content);

        String fileName = ActivityUtils.getFileNameParameter(getIntent());
        if (fileName != null) {
            loadFileIntoPrompter(fileName);
        } else
            ActivityUtils.showMessage(R.string.file_not_found, getBaseContext(),
                    Toast.LENGTH_LONG);
    }

    private void loadFileIntoPrompter(String fileName) {
        try {
            mPrompter.setText(ActivityUtils.getFileContent(fileName, this));
            setPrompterDefinitions(fileName);

        } catch (FileNotFoundException e) {
            ActivityUtils.showMessage(R.string.file_not_found, getBaseContext(),
                    Toast.LENGTH_LONG);
        } catch (IOException e) {
            ActivityUtils.showMessage(R.string.input_output_file_error, getBaseContext(),
                    Toast.LENGTH_LONG);
        }
    }

    private void setPrompterDefinitions(String fileName) {
        String scrollSpeedPrefKey = getResources().getString(R.string.pref_key_scrollSpeed);
        String timeRunningPrefKey = getResources().getString(R.string.pref_key_timeRunning);
        String timeWaitingPrefKey = getResources().getString(R.string.pref_key_timeWaiting);
        String totalTimersPrefKey = getResources().getString(R.string.pref_key_totalTimers);
        String textSizePrefKey = getResources().getString(R.string.pref_key_textSize);

        int scrollSpeedDefault = getResources().getInteger(R.integer.number_default_value_scroll_speed);
        int timeRunningDefault = getResources().getInteger(R.integer.number_min_value_timer);
        int timeWaitingDefault = getResources().getInteger(R.integer.number_min_value_timer);
        int totalTimersDefault = getResources().getInteger(R.integer.number_min_value_count_timers);
        int textSizeDefault = getResources().getInteger(R.integer.number_default_value_text_size);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int scrollSpeed = preferences.getInt(scrollSpeedPrefKey + fileName, scrollSpeedDefault);
        int totalTimers = preferences.getInt(totalTimersPrefKey + fileName, totalTimersDefault);
        int textSize = preferences.getInt(textSizePrefKey + fileName, textSizeDefault);

        int[] timeRunning = new int[totalTimers];
        int[] timeWaiting = new int[totalTimers];
        for (int i = 0; i < totalTimers; i++) {
            timeRunning[i] = preferences.getInt(timeRunningPrefKey + fileName + i, timeRunningDefault);
            timeWaiting[i] = preferences.getInt(timeWaitingPrefKey + fileName + i, timeWaitingDefault);
        }

        mPrompter.setAnimationId(R.anim.text_prompter);
        mPrompter.setTextSize(textSize);
        mPrompter.setScrollSpeed(scrollSpeed);
        mPrompter.setTimeRunning(timeRunning);
        mPrompter.setTimeWaiting(timeWaiting);
        mPrompter.setTotalTimers(totalTimers);
    }

    public void startStop(View view) {
        mPrompter.startStop();
    }

    private void hideUI() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    public void onBackPressed() {
        ActivityUtils.backToMain(this);
    }
}
