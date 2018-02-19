package com.easyapps.singerpro.domain.model.lyric;

import com.easyapps.singerpro.infrastructure.persistence.lyric.FileSystemException;

/**
 * Created by Daniel on 2018-02-11.
 * Defines all methods for the queue tha holds all lyrics to be played in a queue implementation
 */

public interface IQueueLyricRepository {
    boolean queueLyricForPlaying(String lyricName);

    String getNextLyricToPlay();

    void clearPlaylistQueue();

    void queueLyricsForPlaying(String lyricName, String playlistName) throws FileSystemException;
}
