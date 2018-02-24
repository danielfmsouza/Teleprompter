package com.easyapps.singerpro.ioc;

import com.easyapps.singerpro.presentation.fragment.MainListFragment;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by Daniel on 2018-02-14.
 * Sub component for MainListFragment
 */

@Subcomponent(modules = {LyricModule.class})
public interface MainListFragmentComponent extends AndroidInjector<MainListFragment> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<MainListFragment>{}
}
