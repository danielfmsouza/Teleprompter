package com.easyapps.teleprompter.infrastructure.persistence.lyric;

import android.content.Context;

import com.easyapps.teleprompter.domain.model.lyric.Configuration;
import com.easyapps.teleprompter.domain.model.lyric.IConfigurationRepository;
import com.easyapps.teleprompter.query.model.lyric.ConfigurationQueryModel;
import com.easyapps.teleprompter.query.model.lyric.ILyricFinder;
import com.easyapps.teleprompter.query.model.lyric.LyricQueryModel;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Android File System implementation for ILyricFinder.
 * Created by daniel on 04/10/2016.
 */

public class AndroidFileSystemLyricFinder implements ILyricFinder {

    private static final String FILE_EXTENSION = ".mt";

    private final Context androidApplicationContext;
    private final IConfigurationRepository configurationRepository;

    public AndroidFileSystemLyricFinder(Context androidApplicationContext) {
        this.androidApplicationContext = androidApplicationContext;

        // TODO Do not instantiate it here. Use IoC and pass it as a parameter (improve testability).
        this.configurationRepository =
                new AndroidPreferenceConfigurationRepository(androidApplicationContext);
    }

    @Override
    public List<LyricQueryModel> getAll() {
        File[] files = androidApplicationContext.getFilesDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(FILE_EXTENSION);
            }
        });

        List<LyricQueryModel> result = new ArrayList<>();
        if (files != null) {
            for (File f : files) {
                int indexBeforeFileExtension = f.getName().length() - 3;
                String name = f.getName().substring(0, indexBeforeFileExtension);

                ConfigurationQueryModel config =
                        MappingConfigurationToQueryModel(configurationRepository.load(name));

                LyricQueryModel lyric = new LyricQueryModel(name, config);
                result.add(lyric);
            }
        }
        return result;
    }

    private ConfigurationQueryModel MappingConfigurationToQueryModel(Configuration config) {
        return new ConfigurationQueryModel(
                config.getScrollSpeed(),
                config.getTimerRunning(),
                config.getFontSize(),
                config.getTimersCount(),
                config.getTimerStopped(),
                config.getSongNumber());
    }
}
