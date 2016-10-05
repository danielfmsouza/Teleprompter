package com.easyapps.teleprompter.domain.model.lyric;

/**
 * Interface for configurations in a Lyric object.
 * Created by daniel on 01/10/2016.
 */

public interface IConfigurationRepository {
    void updateId(String oldId, String newId);
    Configuration load(String id);
}
