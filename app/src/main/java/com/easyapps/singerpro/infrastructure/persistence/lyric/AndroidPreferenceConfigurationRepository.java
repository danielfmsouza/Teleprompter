package com.easyapps.singerpro.infrastructure.persistence.lyric;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import com.easyapps.singerpro.BuildConfig;
import com.easyapps.singerpro.R;
import com.easyapps.singerpro.domain.model.lyric.Configuration;
import com.easyapps.singerpro.domain.model.lyric.IConfigurationRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import javax.inject.Inject;

/**
 * Implementation of IConfigurationRepository specific for an Android Shared Preference.
 * Created by daniel on 01/10/2016.
 */

public class AndroidPreferenceConfigurationRepository extends FileSystemRepository implements IConfigurationRepository {

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

    private static final String FILE_NAME = "settings";
    private static final String FILE_EXTENSION = ".cfg";

    @Inject
    public AndroidPreferenceConfigurationRepository(Context androidContext) {
        super(androidContext);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(androidContext);

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
    public void addOrUpdateSongNumber(String id, String songNumber) {
        SharedPreferences.Editor editor = preferences.edit();

        if (songNumber != null && !"".equals(songNumber) && songNumber.matches("\\d+"))
            editor.putInt(songNumberPrefKey + id, Integer.parseInt(songNumber));
        else
            editor.putInt(songNumberPrefKey + id, songNumberDefault);

        editor.apply();
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

    @Override
    public Uri getURIFromConfiguration() {
        ObjectOutputStream outputWriter = null;
        String fileName = FILE_NAME + FILE_EXTENSION;

        try {
            FileOutputStream file = getContext().openFileOutput(
                    fileName, Context.MODE_PRIVATE);
            outputWriter = new ObjectOutputStream(file);
            outputWriter.writeObject(preferences.getAll());

            return FileProvider.getUriForFile(getContext(),
                    BuildConfig.APPLICATION_ID + ".provider",
                    new File(getContext().getFilesDir(), fileName));

        } catch (Exception e) {
            return null;
        } finally {
            if (outputWriter != null) {
                try {
                    outputWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getConfigExtension() {
        return FILE_EXTENSION;
    }

    @Override
    public void importFromFileUri(Uri configFileUri) throws FileSystemException {
        Map configs;
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(getContext().getContentResolver().openInputStream(configFileUri));
            Object object = ois.readObject();

            if (object instanceof Map) {
                configs = (Map) object;
                addConfigurationsFromMap(configs);
            }
        } catch (Exception ioe) {
            AndroidFileSystemHelper.throwNewFileSystemException(FILE_NAME,
                    R.string.input_output_file_error, getContext());
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addConfigurationsFromMap(Map configs) {
        SharedPreferences.Editor editor = preferences.edit();

        for (Map.Entry<String, ?> config : ((Map<String, ?>) configs).entrySet()) {
            String value = config.getValue().toString();
            if (value.matches("\\d+")) {
                int parsedInt = Integer.parseInt(value);
                editor.putInt(config.getKey(), parsedInt);
            }
        }

        editor.apply();
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
        return getContext().getResources().getString(resource);
    }

    private int getResourcesInt(int resource) {
        return getContext().getResources().getInteger(resource);
    }
}
