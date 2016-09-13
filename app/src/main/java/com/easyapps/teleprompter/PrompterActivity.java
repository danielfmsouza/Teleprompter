package com.easyapps.teleprompter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.easyapps.teleprompter.components.PrompterView;
import com.easyapps.teleprompter.messages.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class PrompterActivity extends AppCompatActivity {

    private PrompterView mPrompter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideUI();

        setContentView(R.layout.activity_prompter);

        String scrollSpeedPrefKey = getResources().getString(R.string.pref_key_scrollSpeed);
        String timeRunningPrefKey = getResources().getString(R.string.pref_key_timeRunning);
        String timeWaitingPrefKey = getResources().getString(R.string.pref_key_timeWaiting);
        String totalTimersPrefKey = getResources().getString(R.string.pref_key_totalTimers);
        String textSizePrefKey = getResources().getString(R.string.pref_key_textSize);

        int scrollSpeedDefault = (getResources().getInteger(R.integer.number_default_value_scroll_speed));
        int timeRunningDefault = getResources().getInteger(R.integer.number_min_value_timer);
        int timeWaitingDefault = getResources().getInteger(R.integer.number_min_value_timer);
        int totalTimersDefault = getResources().getInteger(R.integer.number_min_value_count_timers);
        int textSizeDefault = getResources().getInteger(R.integer.number_default_value_text_size);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int scrollSpeed = preferences.getInt(scrollSpeedPrefKey, scrollSpeedDefault);
        int timeRunning = preferences.getInt(timeRunningPrefKey, timeRunningDefault);
        int timeWaiting = preferences.getInt(timeWaitingPrefKey, timeWaitingDefault);
        int totalTimers = preferences.getInt(totalTimersPrefKey, totalTimersDefault);
        int textSize = preferences.getInt(textSizePrefKey, textSizeDefault);

        mPrompter = (PrompterView) findViewById(R.id.fullscreen_content);
        mPrompter.setAnimationId(R.anim.text_prompter);
        mPrompter.setTextSize(textSize);
        mPrompter.setScrollSpeed(scrollSpeed);
        mPrompter.setTimeRunning(timeRunning);
        mPrompter.setTimeWaiting(timeWaiting);
        mPrompter.setTotalTimers(totalTimers);

        Bundle b = getIntent().getExtras();
        String fileName = null;
        if (b != null)
            fileName = b.getString(Constants.FILE_NAME_PARAM);

        if (fileName != null) {
            mPrompter.setText(getFileContent(fileName));
        }
    }

    private String getFileContent(String fileName) {
        String completeFileName = fileName + Constants.FILE_EXTENSION;
        File[] files = this.getFilesDir().listFiles();
        for (File f : files) {
            if (f.getName().equals(completeFileName))
                return readFile(f);
        }
        return null;
    }

    private String readFile(File f) {
        StringBuilder text = new StringBuilder();
        String line;
        try {
            BufferedReader br = new BufferedReader((new FileReader(f)));
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
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
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);

        finish();
    }
}
