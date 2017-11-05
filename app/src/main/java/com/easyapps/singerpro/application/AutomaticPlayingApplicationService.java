package com.easyapps.singerpro.application;

import com.easyapps.singerpro.domain.model.lyric.Lyric;
import com.easyapps.singerpro.query.model.lyric.ILyricFinder;

/**
 * Created by Daniel on 2017-11-05.
 * AppService that manages all methods from automatic playing.
 */

public class AutomaticPlayingApplicationService {

    private final ILyricFinder lyricFinder;

    public AutomaticPlayingApplicationService(ILyricFinder lyricFinder) {
        this.lyricFinder = lyricFinder;
    }

    public String loadNextLyricNameFromSetList(String setListName, String currentLyricName)
            throws Exception {
        return lyricFinder.getNextLyricNameFromSetList(setListName, currentLyricName);
    }
}
