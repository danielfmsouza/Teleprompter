package com.easyapps.singerpro.ioc;

import com.easyapps.singerpro.domain.model.lyric.IConfigurationRepository;
import com.easyapps.singerpro.domain.model.lyric.ILyricRepository;
import com.easyapps.singerpro.domain.model.lyric.IPlaylistRepository;
import com.easyapps.singerpro.domain.model.lyric.IQueueLyricRepository;
import com.easyapps.singerpro.infrastructure.persistence.lyric.AndroidFileSystemLyricFinder;
import com.easyapps.singerpro.infrastructure.persistence.lyric.AndroidFileSystemLyricRepository;
import com.easyapps.singerpro.infrastructure.persistence.lyric.AndroidFileSystemPlaylistFinder;
import com.easyapps.singerpro.infrastructure.persistence.lyric.AndroidFileSystemPlaylistRepository;
import com.easyapps.singerpro.infrastructure.persistence.lyric.AndroidPreferenceConfigurationRepository;
import com.easyapps.singerpro.infrastructure.persistence.lyric.InMemoryQueueLyricRepository;
import com.easyapps.singerpro.query.model.lyric.ILyricFinder;
import com.easyapps.singerpro.query.model.lyric.IPlaylistFinder;

import dagger.Binds;
import dagger.Module;

/**
 * Created by Daniel on 2018-02-11.
 * Holds all binds for Lyric aggregate.
 */

@Module
public abstract class LyricModule {

    @Binds
    public abstract ILyricRepository bindLyricRepository(AndroidFileSystemLyricRepository lyricRepository);

    @Binds
    public abstract ILyricFinder bindLyricFinder(AndroidFileSystemLyricFinder lyricRepository);

    @Binds
    public abstract IPlaylistFinder bindPlaylistFinder(AndroidFileSystemPlaylistFinder lyricRepository);

    @Binds
    public abstract IPlaylistRepository bindPlaylistRepository(AndroidFileSystemPlaylistRepository lyricRepository);

    @Binds
    public abstract IConfigurationRepository bindConfigurationRepository(AndroidPreferenceConfigurationRepository lyricRepository);

    @Binds
    public abstract IQueueLyricRepository bindQueueLyricRepository(InMemoryQueueLyricRepository lyricQueue);
}