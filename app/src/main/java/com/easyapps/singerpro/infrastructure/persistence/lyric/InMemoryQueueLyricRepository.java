package com.easyapps.singerpro.infrastructure.persistence.lyric;

import com.easyapps.singerpro.domain.model.lyric.IQueueLyricRepository;
import com.easyapps.singerpro.query.model.lyric.ILyricFinder;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by daniel on 2018-02-10.
 * Responsible for the queue that holds all lyrics to be played.
 */
@Singleton
public class InMemoryQueueLyricRepository implements IQueueLyricRepository {

    private final Queue<String> playlistQueue;
    private final ILyricFinder playlistFinder;

    @Inject
    public InMemoryQueueLyricRepository(ILyricFinder playlistFinder) {
        this.playlistQueue = new LinkedList<>();
        this.playlistFinder = playlistFinder;
    }

    public boolean queueLyricForPlaying(String lyricName) {
        return lyricName != null && !lyricName.isEmpty() && playlistQueue.add(lyricName);
    }

    public String getNextLyricToPlay() {
        return playlistQueue.poll();
    }

    public void clearPlaylistQueue(){
        playlistQueue.clear();
    }

    @Override
    public void queueLyricsForPlaying(String lyricName, String playlistName)throws FileSystemException {
        List<String> allLyricsToPlay = playlistFinder.getAllLyricNamesFromPlaylist(playlistName, lyricName);

        playlistQueue.addAll(allLyricsToPlay);
    }
}
