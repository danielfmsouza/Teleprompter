package com.easyapps.singerpro.query.model.lyric;

import com.easyapps.singerpro.domain.model.lyric.Lyric;
import com.easyapps.singerpro.infrastructure.persistence.lyric.FileSystemException;

import java.util.List;

/**
 * Finder for Lyrics objects.
 * Created by daniel on 04/10/2016.
 */

public interface ILyricFinder {
    List<LyricQueryModel> getAll();

    List<LyricQueryModel> getFromSetList(String setListName) throws FileSystemException;

    String getNextLyricNameFromSetList(String setListName, String currentLyricName) throws FileSystemException;
}
