package com.easyapps.teleprompter.application;

import android.net.Uri;

import com.easyapps.teleprompter.application.command.AddLyricCommand;
import com.easyapps.teleprompter.application.command.UpdateLyricCommand;
import com.easyapps.teleprompter.domain.model.lyric.IConfigurationRepository;
import com.easyapps.teleprompter.domain.model.lyric.ILyricRepository;
import com.easyapps.teleprompter.domain.model.lyric.Lyric;
import com.easyapps.teleprompter.infrastructure.persistence.lyric.FileSystemException;
import com.easyapps.teleprompter.query.model.lyric.ILyricFinder;
import com.easyapps.teleprompter.query.model.lyric.LyricQueryModel;

import java.io.File;
import java.util.List;

/**
 * Application service that manages all calls for Lyric aggregate.
 * Created by daniel on 03/10/2016.
 */

public class LyricApplicationService {

    private final ILyricRepository lyricRepository;
    private final ILyricFinder lyricFinder;
    private final IConfigurationRepository configurationRepository;

    public LyricApplicationService(ILyricRepository lyricRepository, ILyricFinder lyricFinder,
                                   IConfigurationRepository configurationRepository) {
        this.lyricRepository = lyricRepository;
        this.lyricFinder = lyricFinder;
        this.configurationRepository = configurationRepository;
    }

    public void addLyric(AddLyricCommand cmd) throws Exception {
        Lyric lyric = Lyric.newInstance(cmd.getName(), cmd.getContent());

        configurationRepository.addOrUpdateSongNumber(cmd.getName(), cmd.getSongNumber());

        lyricRepository.add(lyric);
    }

    public void updateLyric(UpdateLyricCommand cmd) throws Exception {
        Lyric lyric = Lyric.newInstance(cmd.getNewName(), cmd.getNewContent());

        configurationRepository.addOrUpdateSongNumber(cmd.getOldName(), cmd.getNewSongNumber());

        lyricRepository.update(lyric, cmd.getOldName());
    }

    public Uri[] exportAllLyrics(){
        return lyricRepository.exportAllLyrics();
    }

    public Lyric loadLyric(String name) throws Exception {
        return lyricRepository.load(name);
    }

    public Lyric loadLyricWithConfiguration(String lyricName) throws Exception{
        return lyricRepository.loadWithConfiguration(lyricName);
    }

    public List<LyricQueryModel> getAllLyrics(){
        return lyricFinder.getAll();
    }

    public void removeLyrics(List<String> idsLyrics)throws Exception{
        lyricRepository.remove(idsLyrics);
    }

    public String getConfigExtension() {
        return configurationRepository.getConfigExtension();
    }

    public void importAllConfigurationsFromFileUri(Uri configFileUri) throws FileSystemException {
        configurationRepository.importFromFileUri(configFileUri);
    }
}
