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
    String TEMP_LYRIC_NAME = "71B03CC1-694F-469D-926E-22F28448A402";

    void add(Lyric lyric) throws Exception;

    void update(Lyric lyric, String oldName)throws Exception;

    void remove(List<String> ids)throws Exception;

    Lyric loadWithConfiguration(String name)throws Exception;

    Lyric loadWithConfigurationPartialMatch(String name) throws Exception;

    Uri[] exportAllLyrics();
}
