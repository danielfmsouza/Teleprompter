package com.easyapps.teleprompter.domain.model.lyric;

import com.easyapps.teleprompter.infrastructure.persistence.lyric.FileSystemException;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Repository for set lists.
 * Created by daniel on 11/05/2017.
 */

public interface ISetListRepository {
    void add(String name, List<String> lyricsNames) throws FileSystemException;
    void addLyricsToSetList(String setListName, List<String> lyricsNames)throws FileSystemException;
    void removeLyricFromSetList(String setListName, String lyricName)throws FileSystemException;
    void remove(String setListName) throws FileSystemException, FileNotFoundException;
    List<String> load(String setListName)throws FileSystemException;
}
