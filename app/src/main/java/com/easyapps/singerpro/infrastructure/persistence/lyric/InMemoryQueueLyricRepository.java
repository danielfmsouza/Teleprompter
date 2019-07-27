package com.easyapps.singerpro.infrastructure.persistence.lyric;

import com.easyapps.singerpro.domain.model.lyric.IQueueLyricRepository;
import com.easyapps.singerpro.query.model.lyric.ILyricFinder;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by daniel on 2018-02-10.
 * Responsible for the queue that holds all lyrics to be played.
 */
@Singleton
public class InMemoryQueueLyricRepository implements IQueueLyricRepository {

    private final LinkedList<String> playlistQueue;
    private int currentLyricPosition;

    @Inject
    public InMemoryQueueLyricRepository() {
        this.playlistQueue = new LinkedList<>();
    }

    public boolean queueLyricForPlaying(String lyricName) {
        return lyricName != null && !lyricName.isEmpty() && playlistQueue.add(lyricName);
    }

    @Override
    public String getPreviousLyric() {
        currentLyricPosition--;

        return getCurrentLyric();
    }

    @Override
    public String getNextLyric() {
        currentLyricPosition =
                currentLyricPosition == playlistQueue.size() - 1 ? -1 : currentLyricPosition + 1;

        return getCurrentLyric();
    }

    public void clearPlaylistQueue() {
        playlistQueue.clear();
    }

    @Override
    public void queueLyricsForPlaying(Collection<String> lyrics, int firstLyric) {
        currentLyricPosition = firstLyric;
        playlistQueue.addAll(lyrics);
    }

    @Override
    public String getCurrentLyric() {
        if (currentLyricPosition < 0) {
            return null;
        }
        return playlistQueue.get(currentLyricPosition);
    }
}
