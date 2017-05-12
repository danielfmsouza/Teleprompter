package com.easyapps.teleprompter.infrastructure.persistence.lyric;

import android.content.Context;
import android.support.annotation.NonNull;

import com.easyapps.teleprompter.R;
import com.easyapps.teleprompter.domain.model.lyric.ISetListRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    public List<String> load(String setListName) throws FileSystemException {
        return loadSetListFromFile(setListName);
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