package com.easyapps.teleprompter.infrastructure.persistence.lyric;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.easyapps.teleprompter.BuildConfig;
import com.easyapps.teleprompter.R;
import com.easyapps.teleprompter.domain.model.lyric.IConfigurationRepository;
import com.easyapps.teleprompter.domain.model.lyric.ILyricRepository;
import com.easyapps.teleprompter.domain.model.lyric.Lyric;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Implementation of the ILyricRepository specific for the Android File System.
 * Created by daniel on 01/10/2016.
 */

public class AndroidFileSystemLyricRepository implements ILyricRepository {

    private static final String FILE_EXTENSION = ".mt";
    private final Context androidApplicationContext;
    private final IConfigurationRepository configurationRepository;

    public AndroidFileSystemLyricRepository(Context androidApplicationContext) {
        this.androidApplicationContext = androidApplicationContext;

        // TODO Do not instantiate it here. Use IoC and pass it as a parameter (improve testability).
        this.configurationRepository =
                new AndroidPreferenceConfigurationRepository(androidApplicationContext);
    }

    @Override
    public void add(Lyric lyric) throws FileSystemException {
        saveFile(lyric.getName(), lyric.getContent());
    }

    private void saveFile(String fileName, String content) throws FileSystemException {
        OutputStreamWriter outputWriter = null;
        try {
            FileOutputStream file = androidApplicationContext.openFileOutput(
                    fileName + FILE_EXTENSION, MODE_PRIVATE);
            outputWriter = new OutputStreamWriter(file);
            outputWriter.write(content);

        } catch (Exception e) {
            throwNewFileSystemException(fileName, R.string.file_saving_error);
        } finally {
            if (outputWriter != null) {
                try {
                    outputWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void update(Lyric lyric, String oldName) throws FileSystemException {
        File dir = androidApplicationContext.getFilesDir();
        File oldFile = new File(dir, oldName + FILE_EXTENSION);
        File newFile = new File(dir, lyric.getName() + FILE_EXTENSION);

        if (oldName.equals(lyric.getName())) {
            saveFile(lyric.getName(), lyric.getContent());
        } else if (fileExists(lyric.getName(), dir)) {
            throwNewFileSystemException(lyric.getName(), R.string.file_exists_error);
        } else if (oldFile.exists()) {
            if (!oldFile.renameTo(newFile)) {
                throwNewFileSystemException(oldName, R.string.file_rename_error);
            } else {
                saveFile(lyric.getName(), lyric.getContent());
                configurationRepository.updateId(oldName, lyric.getName());
            }
        } else {
            throwNewFileSystemException(oldName, R.string.file_not_found);
        }
    }

    private boolean fileExists(String name, File dir) {
        File[] filesFiltered = dir.listFiles(new LyricFileNameFilter(name));
        return filesFiltered != null && filesFiltered.length > 0;
    }

    @Override
    public void remove(List<String> ids) throws FileSystemException, FileNotFoundException {
        for (String id : ids) {
            File fileToDelete = getFileByName(id);
            if (!fileToDelete.delete()) {
                throwNewFileSystemException(id, R.string.delete_file_error);
            }
        }
    }

    @Override
    public Lyric load(String name) throws FileNotFoundException, FileSystemException {
        String content = getFileContent(name);
        return Lyric.newInstance(name, content);
    }

    @Override
    public Lyric loadWithConfiguration(String name) throws Exception {
        String content = getFileContent(name);
        return Lyric.newCompleteInstance(name, content, configurationRepository.load(name));
    }

    @Override
    public Uri[] exportAllLyrics() {
        File[] filesFiltered = androidApplicationContext.getFilesDir().
                listFiles(new FilenameFilter() {
                              public boolean accept(File dir, String name) {
                                  return name.toLowerCase().endsWith(FILE_EXTENSION);
                              }
                          }
                );

        if (filesFiltered != null && filesFiltered.length > 0) {
            Uri[] uris = new Uri[filesFiltered.length];

            for (int i = 0; i < filesFiltered.length; i++) {

                uris[i] = FileProvider.getUriForFile(androidApplicationContext,
                        BuildConfig.APPLICATION_ID + ".provider", filesFiltered[i]);
            }
            return uris;
        }
        return null;
    }

    @Override
    public void importLyric(Uri uri) {

    }

    private File getFileByName(final String name) throws FileNotFoundException {
        File workDirectory = androidApplicationContext.getFilesDir();
        File[] files = workDirectory.listFiles(new LyricFileNameFilter(name));
        if (files != null && files.length > 0) {
            return files[0];
        }

        throwNewFileNotFoundException(name);
        return new File(name);
    }

    private String getFileContent(String fileName) throws FileSystemException, FileNotFoundException {
        File[] filesFiltered = androidApplicationContext.getFilesDir().
                listFiles(new LyricFileNameFilter(fileName));

        if (filesFiltered != null && filesFiltered.length > 0) {
            try {
                return readFile(filesFiltered[0]);
            } catch (IOException e) {
                throwNewFileSystemException(fileName, R.string.input_output_file_error);
            }
        }
        throwNewFileNotFoundException(fileName);
        return null;
    }

    private void throwNewFileSystemException(String fileName, int resource)
            throws FileSystemException {
        String message = androidApplicationContext.getResources().
                getString(resource, fileName);

        throw new FileSystemException(message);
    }

    private void throwNewFileNotFoundException(String name) throws FileNotFoundException {
        String message = androidApplicationContext.getResources().
                getString(R.string.file_not_found, name);
        throw new FileNotFoundException(message);
    }

    private static String readFile(File f) throws IOException {
        StringBuilder text = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader((new FileReader(f)));
        while ((line = br.readLine()) != null) {
            text.append(line);
            text.append('\n');
        }
        return text.toString();
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
}