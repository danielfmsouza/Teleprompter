package com.easyapps.teleprompter.infrastructure.persistence.lyric;

import android.content.Context;
import android.support.annotation.NonNull;

import com.easyapps.teleprompter.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by daniel on 27/02/2017.
 * Repository for Files on the Android file system environment.
 */

public class FileSystemRepository {

    @NonNull
    public static String readFile(File f, Context context) throws FileSystemException {
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
            throwNewFileSystemException(f.getName(), R.string.file_saving_error, context);
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return text.toString();
    }

    public static boolean fileExists(File dir, FilenameFilter filter) {
        File[] filesFiltered = dir.listFiles(filter);
        return filesFiltered != null && filesFiltered.length > 0;
    }

    public static void saveFile(String fileName, String extension, String content, Context context)
            throws FileSystemException {
        OutputStreamWriter outputWriter = null;
        try {
            FileOutputStream file = context.openFileOutput(fileName + extension, MODE_PRIVATE);
            outputWriter = new OutputStreamWriter(file);
            outputWriter.write(content);

        } catch (Exception e) {
            throwNewFileSystemException(fileName, R.string.file_saving_error, context);
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

    public static void throwNewFileSystemException(String fileName, int resource, Context context)
            throws FileSystemException {
        String message = context.getResources().getString(resource, fileName);
        throw new FileSystemException(message);
    }
}
