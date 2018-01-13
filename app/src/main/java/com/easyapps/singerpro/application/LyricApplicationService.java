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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Application service that manages all calls for Lyric aggregate.
 * Created by daniel on 03/10/2016.
 */

public class LyricApplicationService {

    private final ILyricRepository lyricRepository;
    private final ISetListFinder playlistFinder;
    private final ISetListRepository playlistRepository;
    private final ILyricFinder lyricFinder;
    private final IConfigurationRepository configurationRepository;

    public LyricApplicationService(ILyricRepository lyricRepository, ILyricFinder lyricFinder,
                                   IConfigurationRepository configurationRepository,
                                   ISetListFinder playlistFinder,
                                   ISetListRepository playlistRepository) {
        this.lyricRepository = lyricRepository;
        this.lyricFinder = lyricFinder;
        this.configurationRepository = configurationRepository;
        this.playlistFinder = playlistFinder;
        this.playlistRepository = playlistRepository;
    }

    public String getTempLyricName() {
        return ILyricRepository.TEMP_LYRIC_NAME;
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

    public ArrayList<Uri> exportAllLyrics() {
        Uri[] lyricsUris = lyricRepository.exportAllLyrics();

        ArrayList<Uri> allUris = new ArrayList<>();

        if (lyricsUris != null)
            allUris.addAll(Arrays.asList(lyricsUris));

        Uri configUri = configurationRepository.getURIFromConfiguration();

        if (configUri != null)
            allUris.add(configUri);

        return allUris;
    }

    public Lyric loadLyricWithConfiguration(String lyricName, boolean partialNameMatch) throws Exception {
        return partialNameMatch ? lyricRepository.loadWithConfigurationPartialMatch(lyricName) :
                lyricRepository.loadWithConfiguration(lyricName);
    }

    public List<LyricQueryModel> getAllLyrics() {
        return lyricFinder.getAll();
    }

    public void removeLyrics(List<String> idsLyrics) throws Exception {
        lyricRepository.remove(idsLyrics);
    }

    public boolean isConfigFile(String fileName) {
        return fileName.endsWith(configurationRepository.getConfigExtension());
    }

    public void importAllConfigurationsFromFileUri(Uri configFileUri) throws FileSystemException {
        configurationRepository.importFromFileUri(configFileUri);
    }

    public String[] getAllPlaylistNames() {
        return playlistFinder.getAllPlaylistNames();
    }

    public void addPlaylist(String name, List<String> lyricsNames) throws FileSystemException {
        playlistRepository.add(name, lyricsNames);
    }

    public void updatePlaylistName(String oldPlaylistName, String newPlaylistName) throws
            FileNotFoundException, FileSystemException {
        playlistRepository.updatePlaylistName(oldPlaylistName, newPlaylistName);
    }

    public void addLyricsToPlaylist(String playlistName, List<String> lyricsNames) throws FileSystemException {
        playlistRepository.addLyricsToPlaylist(playlistName, lyricsNames);
    }

    public List<LyricQueryModel> loadLyricsFromPlaylist(String playlistName) throws FileSystemException {
        return lyricFinder.getFromSetList(playlistName);
    }

    public void removeLyricsFromPlaylist(String playlistName, List<String> lyricsName) throws FileSystemException {
        playlistRepository.removeLyricsFromPlaylist(playlistName, lyricsName);
    }

    public void removeSetList(String setListName) throws FileNotFoundException, FileSystemException {
        playlistRepository.remove(setListName);
    }
}
