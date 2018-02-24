package com.easyapps.singerpro.ioc;

import com.easyapps.singerpro.presentation.activity.MainActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by Daniel on 2018-02-11.
 * Sub component for MainActivity
 */

@Subcomponent(modules = {LyricModule.class})
public interface MainActivityComponent extends AndroidInjector<MainActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<MainActivity>{}
}