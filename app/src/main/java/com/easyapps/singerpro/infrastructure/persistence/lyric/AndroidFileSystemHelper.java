package com.easyapps.singerpro.infrastructure.persistence.lyric;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import androidx.annotation.NonNull;

import com.easyapps.singerpro.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by Daniel on 2018-02-24.
 * Helper containing useful methods related to AndroidFileSystem.
 */

public class AndroidFileSystemHelper {
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
