package com.easyapps.singerpro.infrastructure.persistence.lyric;

import android.content.Context;
import androidx.annotation.NonNull;

import com.easyapps.singerpro.domain.model.lyric.Configuration;
import com.easyapps.singerpro.domain.model.lyric.IConfigurationRepository;
import com.easyapps.singerpro.domain.model.lyric.ILyricRepository;
import com.easyapps.singerpro.domain.model.lyric.IPlaylistRepository;
import com.easyapps.singerpro.query.model.lyric.ConfigurationQueryModel;
import com.easyapps.singerpro.query.model.lyric.ILyricFinder;
import com.easyapps.singerpro.query.model.lyric.LyricQueryModel;
import com.easyapps.singerpro.query.model.lyric.LyricQueryModelComparator;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Android File System implementation for ILyricFinder.
 * Created by daniel on 04/10/2016.
 */

public class AndroidFileSystemLyricFinder implements ILyricFinder {

    private static final String FILE_EXTENSION = ".mt";

    private final Context androidApplicationContext;
    private final IConfigurationRepository configurationRepository;
    private final IPlaylistRepository playlistRepository;

    @Inject
    public AndroidFileSystemLyricFinder(Context androidApplicationContext,
                                        IConfigurationRepository configurationRepository,
                                        IPlaylistRepository playlistRepository) {
        this.androidApplicationContext = androidApplicationContext;
        this.configurationRepository = configurationRepository;
        this.playlistRepository = playlistRepository;
    }

    @Override
    public List<LyricQueryModel> getAll() {
        File[] files = androidApplicationContext.getFilesDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(FILE_EXTENSION) && !name.contains(ILyricRepository.TEMP_LYRIC_NAME);
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
        final List<String> lyrics = playlistRepository.load(setListName);

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
    public List<String> getAllLyricNamesFromPlaylist(String playlistName, String currentLyricName) throws FileSystemException {
        List<LyricQueryModel> allLyricsFromSetList;
        if (playlistName == null || playlistName.isEmpty()) {
            allLyricsFromSetList = getAll();
        } else {
            allLyricsFromSetList = getFromSetList(playlistName);
        }

        Collections.sort(allLyricsFromSetList, new LyricQueryModelComparator());

        int currentIndex = allLyricsFromSetList.indexOf(new LyricQueryModel(currentLyricName, null));
        if (currentIndex > -1) {
            List<LyricQueryModel> subList =
                    allLyricsFromSetList.subList(currentIndex, allLyricsFromSetList.size());

            List<String> lyricNames = new ArrayList<>();
            for (LyricQueryModel lyric : subList) {
                lyricNames.add(lyric.getName());
            }
            return lyricNames;
        }
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