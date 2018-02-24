package com.easyapps.singerpro.ioc;

import android.app.Activity;
import android.app.Fragment;

import com.easyapps.singerpro.presentation.activity.MainActivity;
import com.easyapps.singerpro.presentation.activity.MaintainLyricActivity;
import com.easyapps.singerpro.presentation.activity.PrompterActivity;
import com.easyapps.singerpro.presentation.activity.SettingsActivity;
import com.easyapps.singerpro.presentation.fragment.MainListFragment;
import com.easyapps.singerpro.presentation.fragment.MaintainLyricFragment;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.android.FragmentKey;
import dagger.multibindings.IntoMap;

/**
 * Created by Daniel on 2018-02-11.
 * Holds all activity and fragments injectors
 */

@Module
abstract class ActivityBuilder {

    @Binds
    @IntoMap
    @ActivityKey(MainActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindMainActivity(MainActivityComponent.Builder builder);

    @Binds
    @IntoMap
    @ActivityKey(PrompterActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindPrompterActivity(PrompterActivityComponent.Builder builder);

    @Binds
    @IntoMap
    @ActivityKey(MaintainLyricActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindMaintainLyricActivity(MaintainLyricActivityComponent.Builder builder);

    @Binds
    @IntoMap
    @ActivityKey(SettingsActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindSettingsActivity(SettingsActivityComponent.Builder builder);

    @Binds
    @IntoMap
    @FragmentKey(MainListFragment.class)
    abstract AndroidInjector.Factory<? extends Fragment> bindMainListFragment(MainListFragmentComponent.Builder builder);

    @Binds
    @IntoMap
    @FragmentKey(MaintainLyricFragment.class)
    abstract AndroidInjector.Factory<? extends Fragment> bindMaintainLyricFragment(MaintainLyricFragmentComponent.Builder builder);
}