package com.easyapps.singerpro.ioc;

import com.easyapps.singerpro.presentation.PrompterActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by Daniel on 2018-02-14.
 * Sub component for PrompterActivity
 */
@Subcomponent(modules = {LyricModule.class})
public interface PrompterActivityComponent extends AndroidInjector<PrompterActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<PrompterActivity> {
    }
}