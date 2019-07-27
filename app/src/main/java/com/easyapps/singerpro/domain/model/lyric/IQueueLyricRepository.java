package com.easyapps.singerpro.domain.model.lyric;

import com.easyapps.singerpro.infrastructure.persistence.lyric.FileSystemException;

import java.util.Collection;

/**
 * Created by Daniel on 2018-02-11.
 * Defines all methods for the queue tha holds all lyrics to be played in a queue implementation
 */

public interface IQueueLyricRepository {
    boolean queueLyricForPlaying(String lyricName);

    String getCurrentLyric();

    String getPreviousLyric();

    String getNextLyric();

    void clearPlaylistQueue();

    void queueLyricsForPlaying(Collection<String> lyrics, int firstLyricToPlay);
}
