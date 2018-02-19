package com.easyapps.singerpro.ioc;

import com.easyapps.singerpro.presentation.fragments.MaintainLyricFragment;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by Daniel on 2018-02-14.
 * Sub component for MaintainLyricActivity
 */
@Subcomponent(modules = {LyricModule.class})
public interface MaintainLyricFragmentComponent extends AndroidInjector<MaintainLyricFragment> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<MaintainLyricFragment> {
    }
}
