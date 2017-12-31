package com.easyapps.singerpro.application;

import android.net.Uri;

import com.easyapps.singerpro.application.command.AddLyricCommand;
import com.easyapps.singerpro.application.command.UpdateLyricCommand;
import com.easyapps.singerpro.domain.model.lyric.IConfigurationRepository;
import com.easyapps.singerpro.domain.model.lyric.ILyricRepository;
import com.easyapps.singerpro.domain.model.lyric.ISetListRepository;
import com.easyapps.singerpro.domain.model.lyric.Lyric;
import com.easyapps.singerpro.infrastructure.persistence.lyric.FileSystemException;
import com.easyapps.singerpro.query.model.lyric.ILyricFinder;
import com.easyapps.singerpro.query.model.lyric.ISetListFinder;
import com.easyapps.singerpro.query.model.lyric.LyricQueryModel;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Application service that manages all calls for Lyric aggregate.
 * Created by daniel on 03/10/2016.
 */

public class LyricApplicationService {

    private final ILyricRepository lyricRepository;
    private final ISetListFinder setListFinder;
    private final ISetListRepository setListRepository;
    private final ILyricFinder lyricFinder;
    private final IConfigurationRepository configurationRepository;

    public LyricApplicationService(ILyricRepository lyricRepository, ILyricFinder lyricFinder,
                                   IConfigurationRepository configurationRepository,
                                   ISetListFinder setListFinder,
                                   ISetListRepository setListRepository) {
        this.lyricRepository = lyricRepository;
        this.lyricFinder = lyricFinder;
        this.configurationRepository = configurationRepository;
        this.setListFinder = setListFinder;
        this.setListRepository = setListRepository;
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

    public Uri[] exportAllLyrics() {
        return lyricRepository.exportAllLyrics();
    }

    public Lyric loadLyricWithConfiguration(String lyricName) throws Exception {
        return lyricRepository.loadWithConfiguration(lyricName);
    }

    public List<LyricQueryModel> getAllLyrics() {
        return lyricFinder.getAll();
    }

    public void removeLyrics(List<String> idsLyrics) throws Exception {
        lyricRepository.remove(idsLyrics);
    }

    public String getConfigExtension() {
        return configurationRepository.getConfigExtension();
    }

    public void importAllConfigurationsFromFileUri(Uri configFileUri) throws FileSystemException {
        configurationRepository.importFromFileUri(configFileUri);
    }

    public String[] getAllPlaylistNames() {
        return setListFinder.getAllSetListsNames();
    }

    public void addSetList(String name, List<String> lyricsNames) throws FileSystemException {
        setListRepository.add(name, lyricsNames);
    }

    public void updateSetListName(String oldSetListName, String newSetListName) throws
            FileNotFoundException, FileSystemException {
        setListRepository.updateSetListName(oldSetListName, newSetListName);
    }

    public void addLyricToSetList(String setListName, List<String> lyricsNames) throws FileSystemException {
        setListRepository.addLyricsToSetList(setListName, lyricsNames);
    }

    public List<LyricQueryModel> loadLyricsFromSetList(String setListName) throws FileSystemException {
        return lyricFinder.getFromSetList(setListName);
    }

    public void removeLyricsFromSetList(String setListName, List<String> lyricsName) throws FileSystemException {
        setListRepository.removeLyricsFromSetList(setListName, lyricsName);
    }

    public void removeSetList(String setListName) throws FileNotFoundException, FileSystemException {
        setListRepository.remove(setListName);
    }
}
