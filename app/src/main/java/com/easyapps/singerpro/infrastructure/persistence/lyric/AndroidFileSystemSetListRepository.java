package com.easyapps.singerpro.infrastructure.persistence.lyric;

import android.content.Context;
import android.support.annotation.NonNull;

import com.easyapps.singerpro.domain.model.lyric.ISetListRepository;
import com.easyapps.teleprompter.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of the ISetListRepository specific for the Android File System.
 * Created by daniel on 11/05/2017.
 */

public class AndroidFileSystemSetListRepository extends FileSystemRepository implements ISetListRepository {

    private static final String FILE_EXTENSION = ".sl";
    private final Context androidContext;

    public AndroidFileSystemSetListRepository(Context androidContext) {
        this.androidContext = androidContext;
    }

    @Override
    public void add(String name, List<String> lyricsNames) throws FileSystemException {
        ObjectOutputStream outputWriter = null;
        String fileName = name + FILE_EXTENSION;

        try {
            FileOutputStream file = androidContext.openFileOutput(
                    fileName, Context.MODE_PRIVATE);
            outputWriter = new ObjectOutputStream(file);
            outputWriter.writeObject(lyricsNames);

        } catch (FileNotFoundException e) {
            throwNewFileSystemException(fileName, R.string.file_not_found, androidContext);
        } catch (IOException e) {
            throwNewFileSystemException(fileName, R.string.file_saving_error, androidContext);
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
    public void addLyricsToSetList(String setListName, List<String> lyricsNames)
            throws FileSystemException {
        List<String> setList = loadSetListFromFile(setListName);

        Set<String> setListWithNewLyrics = new HashSet<>(setList);
        setListWithNewLyrics.addAll(lyricsNames);

        List<String> mergedSetList = new ArrayList<>(setListWithNewLyrics);

        add(setListName, mergedSetList);
    }

    @Override
    public void removeLyricFromSetList(String setListName, String lyricName) throws FileSystemException {
        List<String> setList = loadSetListFromFile(setListName);
        setList.remove(lyricName);
        add(setListName, setList);
    }

    @Override
    public void remove(final String setListName) throws FileSystemException, FileNotFoundException {
        File fileToDelete = getFileByName(setListName, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.equals(setListName + FILE_EXTENSION);
            }
        });
        if (!fileToDelete.delete()) {
            throwNewFileSystemException(setListName, R.string.delete_file_error,
                    androidContext);
        }
    }

    private File getFileByName(final String name, FilenameFilter filter) throws FileNotFoundException {
        File workDirectory = androidContext.getFilesDir();
        File[] files = workDirectory.listFiles(filter);
        if (files != null && files.length > 0) {
            return files[0];
        }

        throwNewFileNotFoundException(name, androidContext);
        return new File(name);
    }

    @Override
    public List<String> load(String setListName) throws FileSystemException {
        return loadSetListFromFile(setListName);
    }

    @Override
    public void updateSetListName(String oldSetListName, final String newSetListName)
            throws FileSystemException, FileNotFoundException {
        if (oldSetListName.equals(newSetListName)) return;

        File dir = androidContext.getFilesDir();
        File oldFile = new File(dir, oldSetListName + FILE_EXTENSION);

        List<String> fileContent = loadSetListFromFile(oldSetListName);

        if (fileExists(dir, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.equals(newSetListName + FILE_EXTENSION);
            }
        })) {
            throwNewFileSystemException(newSetListName, R.string.file_exists_error, androidContext);
        } else if (oldFile.exists()) {
            add(newSetListName, fileContent);
            try {
                remove(oldSetListName);
            }catch (Exception ex){
                remove(newSetListName);
            }
        } else
        {
            throwNewFileSystemException(oldSetListName, R.string.file_not_found, androidContext);
        }

    }

    @NonNull
    private List<String> loadSetListFromFile(String setListName) throws FileSystemException {
        File dir = androidContext.getFilesDir();
        File file = new File(dir, setListName + FILE_EXTENSION);
        FileInputStream fis = null;
        List<String> setList = new ArrayList<>();

        try {
            fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object object = ois.readObject();

            if (object instanceof List) {
                setList = (List) object;
            }

            ois.close();
        } catch (FileNotFoundException e) {
            throwNewFileSystemException(setListName, R.string.file_not_found, androidContext);
        } catch (Exception e) {
            throwNewFileSystemException(setListName, R.string.input_output_file_error, androidContext);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return setList;
    }
}