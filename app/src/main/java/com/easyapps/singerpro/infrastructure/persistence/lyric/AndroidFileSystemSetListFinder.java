package com.easyapps.singerpro.infrastructure.persistence.lyric;

import android.content.Context;

import com.easyapps.singerpro.query.model.lyric.ISetListFinder;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Android File System implementation for ISetListFinder.
 * Created by daniel on 04/10/2016.
 */

public class AndroidFileSystemSetListFinder implements ISetListFinder {

    private static final String FILE_EXTENSION = ".sl";

    private final Context androidApplicationContext;

    public AndroidFileSystemSetListFinder(Context androidApplicationContext) {
        this.androidApplicationContext = androidApplicationContext;
    }

    @Override
    public String[] getAllSetListsNames() {
        File[] files = androidApplicationContext.getFilesDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(FILE_EXTENSION);
            }
        });

        String[] result;
        if (files != null && files.length > 0) {
            result = new String[files.length];
            int pos = 0;
            for (File f : files) {
                int indexBeforeFileExtension = f.getName().length() - 3;
                String name = f.getName().substring(0, indexBeforeFileExtension);

                result[pos++] = name;
            }
        } else {
            result = new String[]{};
        }
        return result;
    }
}
