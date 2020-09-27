package com.easyapps.singerpro.infrastructure.persistence.lyric;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;

import com.easyapps.singerpro.R;
import com.easyapps.singerpro.domain.model.lyric.IPlaylistRepository;

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

import javax.inject.Inject;

/**
 * Implementation of the IPlaylistRepository specific for the Android File System.
 * Created by daniel on 11/05/2017.
 */

public class AndroidFileSystemPlaylistRepository extends FileSystemRepository implements IPlaylistRepository {

    private static final String FILE_EXTENSION = ".sl";

    @Inject
    public AndroidFileSystemPlaylistRepository(Context context) {
        super(context);
    }

    @Override
    public void add(String name, List<String> lyricsNames) throws FileSystemException {
        ObjectOutputStream outputWriter = null;
        String fileName = name + FILE_EXTENSION;

        try {
            FileOutputStream file = getContext().openFileOutput(
                    fileName, Context.MODE_PRIVATE);
            outputWriter = new ObjectOutputStream(file);
            outputWriter.writeObject(lyricsNames);

        } catch (FileNotFoundException e) {
            AndroidFileSystemHelper.throwNewFileSystemException(fileName,
                    R.string.file_not_found, getContext());
        } catch (IOException e) {
            AndroidFileSystemHelper.throwNewFileSystemException(fileName,
                    R.string.file_saving_error, getContext());
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
    public void addLyricsToPlaylist(String playlistName, List<String> lyricsNames)
            throws FileSystemException {
        List<String> setList = loadSetListFromFile(playlistName);

        Set<String> setListWithNewLyrics = new HashSet<>(setList);
        setListWithNewLyrics.addAll(lyricsNames);

        List<String> mergedSetList = new ArrayList<>(setListWithNewLyrics);

        add(playlistName, mergedSetList);
    }

    @Override
    public void removeLyricsFromPlaylist(String playlistName, List<String> lyricsNames) throws FileSystemException {
        List<String> setList = loadSetListFromFile(playlistName);
        setList.removeAll(lyricsNames);
        if (setList.isEmpty())
            try {
                remove(playlistName);
            } catch (FileNotFoundException e) {
                AndroidFileSystemHelper.throwNewFileSystemException(playlistName,
                        R.string.file_not_found, getContext());
            }
        else
            add(playlistName, setList);
    }

    @Override
    public void remove(final String playlistName) throws FileSystemException, FileNotFoundException {
        File fileToDelete = getFileByName(playlistName, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.equals(playlistName + FILE_EXTENSION);
            }
        });
        if (!fileToDelete.delete()) {
            AndroidFileSystemHelper.throwNewFileSystemException(playlistName,
                    R.string.delete_file_error, getContext());
        }
    }

    private File getFileByName(final String name, FilenameFilter filter) throws FileNotFoundException {
        File workDirectory = getContext().getFilesDir();
        File[] files = workDirectory.listFiles(filter);
        if (files != null && files.length > 0) {
            return files[0];
        }

        AndroidFileSystemHelper.throwNewFileNotFoundException(name, getContext());
        return new File(name);
    }

    @Override
    public List<String> load(String playlistName) throws FileSystemException {
        return loadSetListFromFile(playlistName);
    }

    @Override
    public void updatePlaylistName(String oldPlaylistName, final String newPlaylistName)
            throws FileSystemException, FileNotFoundException {
        if (oldPlaylistName.equals(newPlaylistName)) return;

        File dir = getContext().getFilesDir();
        File oldFile = new File(dir, oldPlaylistName + FILE_EXTENSION);

        List<String> fileContent = loadSetListFromFile(oldPlaylistName);

        if (fileExists(dir, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.equals(newPlaylistName + FILE_EXTENSION);
            }
        })) {
            AndroidFileSystemHelper.throwNewFileSystemException(newPlaylistName,
                    R.string.file_exists_error, getContext());
        } else if (oldFile.exists()) {
            add(newPlaylistName, fileContent);
            try {
                remove(oldPlaylistName);
            } catch (Exception ex) {
                remove(newPlaylistName);
            }
        } else {
            AndroidFileSystemHelper.throwNewFileSystemException(oldPlaylistName,
                    R.string.file_not_found, getContext());
        }
    }

    @Override
    public Uri[] exportAllPlaylists() {
        return getAllUris(FILE_EXTENSION);
    }

    @Override
    public void importPlaylistFile(Uri uri, String playlistName) throws FileSystemException {
        Object playlistFile = deserializeFromFile(uri);
        if (playlistFile instanceof ArrayList) {
            ArrayList<String> lyricsNames = (ArrayList<String>) playlistFile;
            String shortPlaylistName = getShortPlaylistName(playlistName);
            add(shortPlaylistName, lyricsNames);
        }
    }

    private String getShortPlaylistName(String playlistName) {
        if (playlistName != null) {
            int indexFileType = playlistName.indexOf(".");
            String shortPlaylistName = playlistName;
            if (indexFileType != -1)
                shortPlaylistName = playlistName.substring(0, indexFileType);
            return shortPlaylistName;
        }
        return null;
    }

    @Override
    public String getPlaylistExtension() {
        return FILE_EXTENSION;
    }

    @NonNull
    private List<String> loadSetListFromFile(String setListName) throws FileSystemException {
        File dir = getContext().getFilesDir();
        File file = new File(dir, setListName + FILE_EXTENSION);
        FileInputStream fis = null;
        List<String> setList = new ArrayList<>();

        try {
            fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object object = ois.readObject();

            if (object instanceof List) {
                setList = (List<String>) object;
            }

            ois.close();
        } catch (FileNotFoundException e) {
            AndroidFileSystemHelper.throwNewFileSystemException(setListName,
                    R.string.file_not_found, getContext());
        } catch (Exception e) {
            AndroidFileSystemHelper.throwNewFileSystemException(setListName,
                    R.string.input_output_file_error, getContext());
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