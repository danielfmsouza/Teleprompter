package com.easyapps.singerpro.infrastructure.persistence.lyric;

import android.content.Context;
import android.support.annotation.NonNull;

import com.easyapps.singerpro.domain.model.lyric.Configuration;
import com.easyapps.singerpro.domain.model.lyric.IConfigurationRepository;
import com.easyapps.singerpro.domain.model.lyric.ISetListRepository;
import com.easyapps.singerpro.query.model.lyric.ConfigurationQueryModel;
import com.easyapps.singerpro.query.model.lyric.ILyricFinder;
import com.easyapps.singerpro.query.model.lyric.LyricQueryModel;
import com.easyapps.singerpro.query.model.lyric.LyricQueryModelComparator;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Android File System implementation for ILyricFinder.
 * Created by daniel on 04/10/2016.
 */

public class AndroidFileSystemLyricFinder implements ILyricFinder {

    private static final String FILE_EXTENSION = ".mt";

    private final Context androidApplicationContext;
    private final IConfigurationRepository configurationRepository;
    private final ISetListRepository setListRepository;

    public AndroidFileSystemLyricFinder(Context androidApplicationContext) {
        this.androidApplicationContext = androidApplicationContext;

        // TODO Do not instantiate it here. Use IoC and pass it as a parameter (improve testability).
        this.configurationRepository =
                new AndroidPreferenceConfigurationRepository(androidApplicationContext);
        this.setListRepository =
                new AndroidFileSystemSetListRepository(androidApplicationContext);
    }

    @Override
    public List<LyricQueryModel> getAll() {
        File[] files = androidApplicationContext.getFilesDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(FILE_EXTENSION);
            }
        });

        return buildLyrics(files);
    }

    @NonNull
    private List<LyricQueryModel> buildLyrics(File[] files) {
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

    @Override
    public List<LyricQueryModel> getFromSetList(String setListName) throws FileSystemException {
        final List<String> lyrics = setListRepository.load(setListName);

        File[] files = androidApplicationContext.getFilesDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                for (String lyricName : lyrics) {
                    if (name.equals(lyricName + FILE_EXTENSION))
                        return true;
                }
                return false;
            }
        });

        return buildLyrics(files);
    }

    @Override
    public String getNextLyricNameFromSetList(String setListName, String currentLyricName) throws FileSystemException {
        List<LyricQueryModel> allLyricsFromSetList;
        if (setListName == null || setListName.isEmpty()) {
            allLyricsFromSetList = getAll();
        } else {
            allLyricsFromSetList = getFromSetList(setListName);
        }

        Collections.sort(allLyricsFromSetList, new LyricQueryModelComparator());

        int currentIndex = allLyricsFromSetList.indexOf(new LyricQueryModel(currentLyricName, null));
        if (currentIndex != -1 && currentIndex != allLyricsFromSetList.size() - 1)
            return allLyricsFromSetList.get(currentIndex + 1).getName();

        return null;
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