package com.easyapps.singerpro.ioc;

import com.easyapps.singerpro.domain.model.lyric.Lyric;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.Provides;
import dagger.android.AndroidInjectionModule;

/**
 * Created by Daniel on 2018-02-11.
 * Holds the application context injection
 */
@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        AppModule.class,
        LyricModule.class,
        ActivityBuilder.class})
public interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(App application);

        AppComponent build();
    }

    void inject(App app);
}