package com.easyapps.singerpro.domain.model.lyric;

import android.net.Uri;

import com.easyapps.singerpro.infrastructure.persistence.lyric.FileSystemException;

/**
 * Interface for configurations in a Lyric object.
 * Created by daniel on 01/10/2016.
 */

public interface IConfigurationRepository {

    void updateId(String oldId, String newId);

    void addOrUpdateSongNumber(String id, String songNumber);

    Configuration load(String id);

    Uri getURIFromConfiguration();

    String getConfigExtension();

    void importFromFileUri(Uri configFileUri) throws FileSystemException;
}
