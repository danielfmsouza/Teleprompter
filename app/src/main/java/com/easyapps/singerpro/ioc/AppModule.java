package com.easyapps.singerpro.ioc;

import android.content.Context;

import com.easyapps.singerpro.presentation.MaintainLyricActivity;
import com.easyapps.singerpro.presentation.SettingsActivity;
import com.easyapps.singerpro.presentation.fragments.MaintainLyricFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Daniel on 2018-02-11.
 * Holds the application methods for DI.
 */

@Module(subcomponents = {
        MainActivityComponent.class,
        PrompterActivityComponent.class,
        SettingsActivityComponent.class,
        MainListFragmentComponent.class,
        MaintainLyricActivityComponent.class,
        MaintainLyricFragmentComponent.class})
class AppModule {

    @Provides
    @Singleton
    Context provideContext(App application) {
        return application;
    }
}