package com.easyapps.teleprompter.domain.model.lyric;

import android.net.Uri;

import java.io.File;
import java.io.IOException;

/**
 * Interface for configurations in a Lyric object.
 * Created by daniel on 01/10/2016.
 */

public interface IConfigurationRepository {

    void updateId(String oldId, String newId);

    void addOrUpdateSongNumber(String id, int songNumber);

    Configuration load(String id);

    Uri getURIFromConfiguration();
}
