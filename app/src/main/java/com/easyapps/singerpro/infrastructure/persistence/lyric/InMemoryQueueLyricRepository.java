package com.easyapps.singerpro.infrastructure.persistence.lyric;

import com.easyapps.singerpro.domain.model.lyric.IQueueLyricRepository;
import com.easyapps.singerpro.query.model.lyric.ILyricFinder;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by daniel on 2018-02-10.
 * Responsible for the queue that holds all lyrics to be played.
 */
@Singleton
public class InMemoryQueueLyricRepository implements IQueueLyricRepository {

    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final LinkedList<String> playlistQueue;
    //todo RETHINK THIS APPROACH URGENTLY!!!!
    private int currentLyricPosition;

    @Inject
    public InMemoryQueueLyricRepository() {
        this.playlistQueue = new LinkedList<>();
    }

    public boolean queueLyricForPlaying(String lyricName) {
        lock.writeLock().lock();

        boolean result = lyricName != null && !lyricName.isEmpty() && playlistQueue.add(lyricName);

        lock.writeLock().unlock();

        return result;
    }

    @Override
    public String getPreviousLyric() {
        lock.writeLock().lock();

        currentLyricPosition--;

        String result = getCurrentLyric();

        lock.writeLock().unlock();

        return result;
    }

    @Override
    public String getNextLyric() {
        lock.writeLock().lock();

        currentLyricPosition =
                currentLyricPosition == playlistQueue.size() - 1 ? -1 : currentLyricPosition + 1;

        String result =  getCurrentLyric();

        lock.writeLock().unlock();

        return result;
    }

    public void clearPlaylistQueue() {
        lock.writeLock().lock();
        playlistQueue.clear();
        currentLyricPosition = 0;
        lock.writeLock().unlock();
    }

    @Override
    public void queueLyricsForPlaying(Collection<String> lyrics, int firstLyric) {
        lock.writeLock().lock();
        currentLyricPosition = firstLyric;
        playlistQueue.addAll(lyrics);
        lock.writeLock().unlock();
    }

    @Override
    public String getCurrentLyric() {
        lock.readLock().lock();
        if (currentLyricPosition < 0) {
            return null;
        }
        String result =  playlistQueue.get(currentLyricPosition);
        lock.readLock().unlock();

        return result;
    }
}
