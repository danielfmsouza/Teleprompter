package com.easyapps.teleprompter.application;

import com.easyapps.teleprompter.application.command.AddLyricCommand;
import com.easyapps.teleprompter.application.command.UpdateLyricCommand;
import com.easyapps.teleprompter.domain.model.lyric.ILyricRepository;
import com.easyapps.teleprompter.domain.model.lyric.Lyric;
import com.easyapps.teleprompter.query.model.lyric.ILyricFinder;
import com.easyapps.teleprompter.query.model.lyric.LyricQueryModel;

import java.util.List;

/**
 * Application service that manages all calls for Lyric aggregate.
 * Created by daniel on 03/10/2016.
 */

public class LyricApplicationService {

    private final ILyricRepository lyricRepository;
    private final ILyricFinder lyricFinder;

    public LyricApplicationService(ILyricRepository lyricRepository, ILyricFinder lyricFinder) {
        this.lyricRepository = lyricRepository;
        this.lyricFinder = lyricFinder;
    }

    public void addLyric(AddLyricCommand cmd) throws Exception {
        Lyric lyric = Lyric.newInstance(cmd.getName(), cmd.getContent());

        lyricRepository.add(lyric);
    }

    public void updateLyric(UpdateLyricCommand cmd) throws Exception {
        Lyric lyric = Lyric.newInstance(cmd.getNewName(), cmd.getNewContent());

        lyricRepository.update(lyric, cmd.getOldName());
    }

    public Lyric loadLyric(String name) throws Exception {
        return lyricRepository.load(name);
    }

    public Lyric loadLyricWithConfiguration(String lyricName) throws Exception{
        return lyricRepository.loadWithConfiguration(lyricName);
    }

    public List<LyricQueryModel> getAllLyrics(){
        return lyricFinder.getAll();
    }

    public void removeLyrics(List<String> idsLyrics)throws Exception{
        lyricRepository.remove(idsLyrics);
    }
}
