package com.easyapps.singerpro.application;

import android.content.ClipData;
import android.content.Context;
import android.net.Uri;

import com.easyapps.singerpro.application.command.AddLyricCommand;
import com.easyapps.singerpro.application.command.UpdateLyricCommand;
import com.easyapps.singerpro.domain.model.lyric.IConfigurationRepository;
import com.easyapps.singerpro.domain.model.lyric.ILyricRepository;
import com.easyapps.singerpro.domain.model.lyric.IPlaylistRepository;
import com.easyapps.singerpro.domain.model.lyric.Lyric;
import com.easyapps.singerpro.infrastructure.persistence.lyric.AndroidFileSystemHelper;
import com.easyapps.singerpro.infrastructure.persistence.lyric.FileSystemException;
import com.easyapps.singerpro.query.model.lyric.ILyricFinder;
import com.easyapps.singerpro.query.model.lyric.IPlaylistFinder;
import com.easyapps.singerpro.query.model.lyric.LyricQueryModel;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * Application service that manages all calls for Lyric aggregate.
 * Created by daniel on 03/10/2016.
 */

public class LyricApplicationService {

    private ILyricRepository lyricRepository;
    private IPlaylistFinder playlistFinder;
    private IPlaylistRepository playlistRepository;
    private ILyricFinder lyricFinder;
    private IConfigurationRepository configurationRepository;

    @Inject
    public LyricApplicationService(ILyricRepository lyricRepository, ILyricFinder lyricFinder,
                                   IConfigurationRepository configurationRepository,
                                   IPlaylistFinder playlistFinder,
                                   IPlaylistRepository playlistRepository) {
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

    public ArrayList<Uri> exportAll() {
        Uri[] lyricUris = lyricRepository.exportAllLyrics();
        Uri[] playlistUris = playlistRepository.exportAllPlaylists();

        ArrayList<Uri> allUris = new ArrayList<>();

        if (lyricUris != null)
            allUris.addAll(Arrays.asList(lyricUris));
        if (playlistUris != null)
            allUris.addAll(Arrays.asList(playlistUris));

        Uri configUri = configurationRepository.getURIFromConfiguration();

        if (configUri != null)
            allUris.add(configUri);

        return allUris;
    }

    public void importFile(Uri uri, Context context) throws Exception {
        String fileName = AndroidFileSystemHelper.getFileName(uri, context);

        if (uri != null) {
            if (isConfigFile(fileName))
                configurationRepository.importFromFileUri(uri);
            else
                importLyricFile(1, uri, fileName, context);
        }
    }

    public void importAll(ClipData data, Context context) throws Exception {
        Uri configFileUri = null;

        for (int i = 0; i < data.getItemCount(); i++) {
            ClipData.Item item = data.getItemAt(i);

            if (item != null) {
                String fileName = AndroidFileSystemHelper.getFileName(item.getUri(), context);

                if (isConfigFile(fileName))
                    configFileUri = item.getUri();
                else if (isPlaylistFile(fileName))
                    playlistRepository.importPlaylistFile(item.getUri(), fileName);
                else
                    importLyricFile(i, item.getUri(), fileName, context);
            }
        }
        importConfigurationFile(configFileUri);
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

    private void importAllConfigurationsFromFileUri(Uri configFileUri) throws FileSystemException {
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

    private void importConfigurationFile(Uri configFileUri) throws FileSystemException {
        if (configFileUri != null) {
            importAllConfigurationsFromFileUri(configFileUri);
        }
    }

    private void importLyricFile(int i, Uri uri, String fileName, Context context)
            throws Exception {
        String content = AndroidFileSystemHelper.readFile(uri, context, fileName);

        if (fileName != null) {
            String shortFileName = fileName.substring(0, fileName.indexOf("."));
            AddLyricCommand cmd = new AddLyricCommand(shortFileName, content, String.valueOf(i));
            addLyric(cmd);
        }
    }

    private boolean isConfigFile(String fileName) {
        return fileName.endsWith(configurationRepository.getConfigExtension());
    }

    private boolean isPlaylistFile(String fileName) {
        return fileName.endsWith(playlistRepository.getPlaylistExtension());
    }
}
