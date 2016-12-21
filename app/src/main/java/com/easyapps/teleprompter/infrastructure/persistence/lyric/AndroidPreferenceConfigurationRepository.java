package com.easyapps.teleprompter.infrastructure.persistence.lyric;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.easyapps.teleprompter.R;
import com.easyapps.teleprompter.domain.model.lyric.Configuration;
import com.easyapps.teleprompter.domain.model.lyric.IConfigurationRepository;

/**
 * Implementation of IConfigurationRepository specific for an Android Shared Preference.
 * Created by daniel on 01/10/2016.
 */

class AndroidPreferenceConfigurationRepository implements IConfigurationRepository {

    private final Context androidApplicationContext;
    private final SharedPreferences preferences;

    private final String scrollSpeedPrefKey;
    private final String timeRunningPrefKey;
    private final String timeStoppedPrefKey;
    private final String totalTimersPrefKey;
    private final String textSizePrefKey;
    private final String songNumberPrefKey;

    private final int scrollSpeedDefault;
    private final int timeRunningDefault;
    private final int timeStoppedDefault;
    private final int totalTimersDefault;
    private final int fontSizeDefault;
    private final int songNumberDefault;

    AndroidPreferenceConfigurationRepository(Context androidApplicationContext) {
        this.androidApplicationContext = androidApplicationContext;
        this.preferences =
                PreferenceManager.getDefaultSharedPreferences(androidApplicationContext);

        scrollSpeedPrefKey = getResourcesString(R.string.pref_key_scrollSpeed);
        timeRunningPrefKey = getResourcesString(R.string.pref_key_timeRunning);
        timeStoppedPrefKey = getResourcesString(R.string.pref_key_timeWaiting);
        totalTimersPrefKey = getResourcesString(R.string.pref_key_totalTimers);
        textSizePrefKey = getResourcesString(R.string.pref_key_textSize);
        songNumberPrefKey = getResourcesString(R.string.pref_key_songNumber);

        scrollSpeedDefault = getResourcesInt(R.integer.number_default_value_scroll_speed);
        timeRunningDefault = getResourcesInt(R.integer.number_min_value_timer);
        timeStoppedDefault = getResourcesInt(R.integer.number_min_value_timer);
        totalTimersDefault = getResourcesInt(R.integer.number_min_value_count_timers);
        songNumberDefault = getResourcesInt(R.integer.number_song);
        fontSizeDefault = getResourcesInt(R.integer.number_default_value_text_size);
    }

    @Override
    public void updateId(String oldId, String newId) {
        renamePreferences(oldId, newId);
    }

    @Override
    public Configuration load(String id) {
        int scrollSpeed = preferences.getInt(scrollSpeedPrefKey + id, scrollSpeedDefault);
        int totalTimers = preferences.getInt(totalTimersPrefKey + id, totalTimersDefault);
        int fontSize = preferences.getInt(textSizePrefKey + id, fontSizeDefault);
        int songNumber = preferences.getInt(songNumberPrefKey + id, songNumberDefault);

        int[] timeRunning = new int[totalTimers];
        int[] timeStopped = new int[totalTimers];
        for (int i = 0; i < totalTimers; i++) {
            timeRunning[i] = preferences.getInt(timeRunningPrefKey + id + i, timeRunningDefault);
            timeStopped[i] = preferences.getInt(timeStoppedPrefKey + id + i, timeStoppedDefault);
        }

        return Configuration.newCompleteInstance(scrollSpeed, timeRunning,
                fontSize, timeStopped, totalTimers, songNumber);
    }

    private void renamePreferences(String oldFileName, String newFileName) {
        SharedPreferences.Editor editor = preferences.edit();

        int scrollSpeed = preferences.getInt(scrollSpeedPrefKey + oldFileName, scrollSpeedDefault);
        int totalTimers = preferences.getInt(totalTimersPrefKey + oldFileName, totalTimersDefault);
        int fontSize = preferences.getInt(textSizePrefKey + oldFileName, fontSizeDefault);
        int songNumber = preferences.getInt(songNumberPrefKey + oldFileName, songNumberDefault);

        editor.remove(scrollSpeedPrefKey);
        editor.remove(totalTimersPrefKey);
        editor.remove(textSizePrefKey);
        editor.remove(songNumberPrefKey);

        if (scrollSpeed != 0)
            editor.putInt(scrollSpeedPrefKey + newFileName, scrollSpeed);
        if (totalTimers != 0)
            editor.putInt(totalTimersPrefKey + newFileName, totalTimers);
        if (fontSize != 0)
            editor.putInt(textSizePrefKey + newFileName, fontSize);
        if (songNumber != 0)
            editor.putInt(songNumberPrefKey + newFileName, songNumber);

        for (int i = 0; i < totalTimers; i++) {
            int timeRunning = preferences.getInt(timeRunningPrefKey + oldFileName + i,
                    timeRunningDefault);
            int timeStopped = preferences.getInt(timeStoppedPrefKey + oldFileName + i,
                    timeStoppedDefault);

            editor.remove(timeRunningPrefKey + oldFileName + i);
            editor.remove(timeStoppedPrefKey + oldFileName + i);

            if (timeRunning != 0)
                editor.putInt(timeRunningPrefKey + newFileName + i, timeRunning);
            if (timeStopped != 0)
                editor.putInt(timeStoppedPrefKey + newFileName + i, timeStopped);
        }

        editor.apply();
    }

    @NonNull
    private String getResourcesString(int resource) {
        return androidApplicationContext.getResources().getString(resource);
    }

    private int getResourcesInt(int resource) {
        return androidApplicationContext.getResources().getInteger(resource);
    }
}
