package com.easyapps.singerpro.domain.model.lyric;

import com.easyapps.singerpro.infrastructure.persistence.lyric.FileSystemException;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;

/**
 * Repository for set lists.
 * Created by daniel on 11/05/2017.
 */

public interface IPlaylistRepository {
    void add(String name, List<String> lyricsNames) throws FileSystemException;
    void addLyricsToPlaylist(String playlistName, List<String> lyricsNames)throws FileSystemException;
    void removeLyricsFromPlaylist(String playlistName, List<String> lyricsName)throws FileSystemException;
    void remove(String playlistName) throws FileSystemException, FileNotFoundException;
    List<String> load(String playlistName)throws FileSystemException;
    void updatePlaylistName(String oldPlaylistName, String newPlaylistName) throws FileSystemException, FileNotFoundException;
}
