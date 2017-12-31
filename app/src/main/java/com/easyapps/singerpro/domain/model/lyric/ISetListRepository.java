package com.easyapps.singerpro.domain.model.lyric;

import com.easyapps.singerpro.infrastructure.persistence.lyric.FileSystemException;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;

/**
 * Repository for set lists.
 * Created by daniel on 11/05/2017.
 */

public interface ISetListRepository {
    void add(String name, List<String> lyricsNames) throws FileSystemException;
    void addLyricsToSetList(String setListName, List<String> lyricsNames)throws FileSystemException;
    void removeLyricsFromSetList(String setListName, List<String> lyricsName)throws FileSystemException;
    void remove(String setListName) throws FileSystemException, FileNotFoundException;
    List<String> load(String setListName)throws FileSystemException;
    void updateSetListName(String oldSetListName, String newSetListName) throws FileSystemException, FileNotFoundException;
}
