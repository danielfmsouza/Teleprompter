package com.easyapps.singerpro.ioc;

import android.content.Context;

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