package com.easyapps.singerpro.domain.model.lyric;

import android.net.Uri;

import com.easyapps.singerpro.infrastructure.persistence.lyric.FileSystemException;

import java.util.List;
import java.util.Set;

/**
 * Repository for Lyric.
 * Created by daniel on 01/10/2016.
 */

public interface ILyricRepository {
    void add(Lyric lyric) throws Exception;

    void update(Lyric lyric, String oldName)throws Exception;

    void remove(List<String> ids)throws Exception;

    Lyric loadWithConfiguration(String name)throws Exception;

    Uri[] exportAllLyrics();
}
