package com.easyapps.singerpro.infrastructure.persistence.lyric;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.easyapps.singerpro.BuildConfig;
import com.easyapps.singerpro.R;
import com.easyapps.singerpro.domain.model.lyric.IConfigurationRepository;
import com.easyapps.singerpro.domain.model.lyric.ILyricRepository;
import com.easyapps.singerpro.domain.model.lyric.Lyric;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.List;

import javax.inject.Inject;

/**
 * Implementation of the ILyricRepository specific for the Android File System.
 * Created by daniel on 01/10/2016.
 */
public class AndroidFileSystemLyricRepository extends FileSystemRepository implements ILyricRepository {

    private static final String FILE_EXTENSION = ".mt";
    private IConfigurationRepository configurationRepository;
    private Context androidContext;

    @Inject
    public AndroidFileSystemLyricRepository(Context context,
                                            IConfigurationRepository configurationRepository) {
        this.androidContext = context;
        this.configurationRepository = configurationRepository;
    }

    @Override
    public void add(Lyric lyric) throws FileSystemException {
        saveFile(lyric.getName(), FILE_EXTENSION, lyric.getContent(), androidContext);
    }

    @Override
    public void update(Lyric lyric, String oldName) throws FileSystemException {
        File dir = androidContext.getFilesDir();
        File oldFile = new File(dir, oldName + FILE_EXTENSION);
        File newFile = new File(dir, lyric.getName() + FILE_EXTENSION);

        if (oldName.equals(lyric.getName())) {
            saveFile(lyric.getName(), FILE_EXTENSION, lyric.getContent(), androidContext);
        } else if (fileExists(dir, new LyricFileNameFilter(lyric.getName()))) {
            throwNewFileSystemException(lyric.getName(), R.string.file_exists_error, androidContext);
        } else if (oldFile.exists()) {
            if (!oldFile.renameTo(newFile)) {
                throwNewFileSystemException(oldName, R.string.file_rename_error, androidContext);
            } else {
                saveFile(lyric.getName(), FILE_EXTENSION, lyric.getContent(), androidContext);
                configurationRepository.updateId(oldName, lyric.getName());
            }
        } else {
            throwNewFileSystemException(oldName, R.string.file_not_found, androidContext);
        }
    }

    @Override
    public void remove(List<String> ids) throws FileSystemException, FileNotFoundException {
        for (String id : ids) {
            File fileToDelete = getFileByName(id);
            if (!fileToDelete.delete()) {
                throwNewFileSystemException(id, R.string.delete_file_error,
                        androidContext);
            }
        }
    }

    @Override
    public Lyric loadWithConfiguration(String name) throws Exception {
        String content = getFileContent(name);
        return Lyric.newCompleteInstance(name, content, configurationRepository.load(name));
    }

    @Override
    public Lyric loadWithConfigurationPartialMatch(String name) throws Exception {
        String[] result = getFirstMatchFileContent(name);
        if (result == null) return null;

        return Lyric.newCompleteInstance(result[0], result[1], configurationRepository.load(result[0]));
    }

    @Override
    public Uri[] exportAllLyrics() {
        File[] filesFiltered = androidContext.getFilesDir().
                listFiles(new FilenameFilter() {
                              public boolean accept(File dir, String name) {
                                  return name.toLowerCase().endsWith(FILE_EXTENSION);
                              }
                          }
                );

        if (filesFiltered != null && filesFiltered.length > 0) {
            Uri[] uris = new Uri[filesFiltered.length];

            for (int i = 0; i < filesFiltered.length; i++) {

                uris[i] = FileProvider.getUriForFile(androidContext,
                        BuildConfig.APPLICATION_ID + ".provider", filesFiltered[i]);
            }
            return uris;
        }
        return null;
    }

    private File getFileByName(final String name) throws FileNotFoundException {
        File workDirectory = androidContext.getFilesDir();
        File[] files = workDirectory.listFiles(new LyricFileNameFilter(name));
        if (files != null && files.length > 0) {
            return files[0];
        }

        throwNewFileNotFoundException(name, androidContext);
        return new File(name);
    }

    private String getFileContent(String fileName) throws FileSystemException, FileNotFoundException {
        File[] filesFiltered = androidContext.getFilesDir().
                listFiles(new LyricFileNameFilter(fileName));

        if (filesFiltered != null && filesFiltered.length > 0) {
            return readFile(filesFiltered[0], androidContext);
        }
        throwNewFileNotFoundException(fileName, androidContext);

        return null;
    }

    private String[] getFirstMatchFileContent(String fileName) throws FileSystemException {
        File[] filesFiltered = androidContext.getFilesDir().
                listFiles(new LyricPartialFileNameFilter(fileName));

        if (filesFiltered != null && filesFiltered.length > 0) {
            String content = readFile(filesFiltered[0], androidContext);
            String name = filesFiltered[0].getName().replace(FILE_EXTENSION, "");
            return new String[]{name, content};
        }
        return null;
    }

    private class LyricFileNameFilter implements FilenameFilter {
        private final String filter;

        LyricFileNameFilter(String filter) {
            this.filter = filter + FILE_EXTENSION;
        }

        @Override
        public boolean accept(File dir, String name) {
            return filter.equals(name);
        }
    }

    private class LyricPartialFileNameFilter implements FilenameFilter {
        private final String filter;

        LyricPartialFileNameFilter(String filter) {
            this.filter = filter + FILE_EXTENSION;
        }

        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(filter);
        }
    }
}