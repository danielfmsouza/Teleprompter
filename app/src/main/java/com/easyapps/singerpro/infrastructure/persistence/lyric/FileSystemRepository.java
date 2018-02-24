package com.easyapps.singerpro.infrastructure.persistence.lyric;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import com.easyapps.singerpro.BuildConfig;
import com.easyapps.singerpro.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;

import javax.inject.Inject;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by daniel on 27/02/2017.
 * Repository for Files on the Android file system environment.
 */

public class FileSystemRepository {
    private Context context;

    @Inject
    public FileSystemRepository(Context context) {
        this.context = context;
    }

    @NonNull
    String readFile(File f, Context context) throws FileSystemException {
        StringBuilder text = new StringBuilder();
        String line;
        BufferedReader br = null;

        try {
            br = new BufferedReader((new FileReader(f)));
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (Exception e) {
            AndroidFileSystemHelper.throwNewFileSystemException(f.getName(),
                    R.string.input_output_file_error, context);
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return text.toString();
    }

    public Object deserializeFromFile(Uri configFileUri) throws FileSystemException {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(getContext().getContentResolver().openInputStream(configFileUri));
            return ois.readObject();
        } catch (Exception ioe) {
            AndroidFileSystemHelper.throwNewFileSystemException(configFileUri.getLastPathSegment(),
                    R.string.input_output_file_error, getContext());
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    boolean fileExists(File dir, FilenameFilter filter) {
        File[] filesFiltered = dir.listFiles(filter);
        return filesFiltered != null && filesFiltered.length > 0;
    }

    void saveFile(String fileName, String extension, String content, Context context)
            throws FileSystemException {
        OutputStreamWriter outputWriter = null;
        try {
            FileOutputStream file = context.openFileOutput(fileName + extension, MODE_PRIVATE);
            outputWriter = new OutputStreamWriter(file);
            outputWriter.write(content);

        } catch (Exception e) {
            AndroidFileSystemHelper.throwNewFileSystemException(fileName, R.string.file_saving_error, context);
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

    Uri[] getAllUris(final String fileExtension) {
        File[] filesFiltered = context.getFilesDir().
                listFiles(new FilenameFilter() {
                              public boolean accept(File dir, String name) {
                                  return name.toLowerCase().endsWith(fileExtension);
                              }
                          }
                );
        if (filesFiltered != null && filesFiltered.length > 0) {
            Uri[] uris = new Uri[filesFiltered.length];

            for (int i = 0; i < filesFiltered.length; i++) {

                uris[i] = FileProvider.getUriForFile(context,
                        BuildConfig.APPLICATION_ID + ".provider", filesFiltered[i]);
            }
            return uris;
        }
        return null;
    }

    public Context getContext() {
        return context;
    }
}
