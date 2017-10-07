package com.easyapps.singerpro.infrastructure.persistence.lyric;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;

import com.easyapps.teleprompter.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by daniel on 27/02/2017.
 * Repository for Files on the Android file system environment.
 */

public class FileSystemRepository {

    public static String getFileName(Uri uri, Context context) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @NonNull
    public static String readFile(Uri uri, Context context, String fileName)
            throws FileSystemException {
        InputStream is = null;
        String result = "";
        try {
            is = context.getContentResolver().openInputStream(uri);
            Scanner s = new Scanner(is).useDelimiter("\\A");
            result = s.hasNext() ? s.next() : "";
        } catch (Exception e) {
            throwNewFileSystemException(fileName, R.string.input_output_file_error, context);
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @NonNull
    static String readFile(File f, Context context) throws FileSystemException {
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
            throwNewFileSystemException(f.getName(), R.string.input_output_file_error, context);
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

    static boolean fileExists(File dir, FilenameFilter filter) {
        File[] filesFiltered = dir.listFiles(filter);
        return filesFiltered != null && filesFiltered.length > 0;
    }

    static void saveFile(String fileName, String extension, String content, Context context)
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

    static void throwNewFileSystemException(String fileName, int resource, Context context)
            throws FileSystemException {
        String message = context.getResources().getString(resource, fileName);
        throw new FileSystemException(message);
    }

    static void throwNewFileNotFoundException(String name, Context context)
            throws FileNotFoundException {
        String message = context.getResources().getString(R.string.file_not_found, name);
        throw new FileNotFoundException(message);
    }
}
