package com.easyapps.singerpro.ioc;

import com.easyapps.singerpro.presentation.SettingsActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by Daniel on 2018-02-14.
 * Sub component for SettingsActivityComponent
 */
@Subcomponent(modules = {LyricModule.class})
public interface SettingsActivityComponent extends AndroidInjector<SettingsActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<SettingsActivity> {
    }
}