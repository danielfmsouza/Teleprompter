package com.easyapps.teleprompter.helper;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.easyapps.teleprompter.MainActivity;
import com.easyapps.teleprompter.R;
import com.easyapps.teleprompter.messages.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by daniel on 16/09/2016.
 * Helper that provides common methods to Activities.
 */
public class ActivityUtils {

    public static void setFileNameParameter(String value, Intent intent) {
        Bundle b = new Bundle();
        b.putString(Constants.FILE_NAME_PARAM, value);
        intent.putExtras(b);
    }

    public static String getFileNameParameter(Intent intent) {
        Bundle b = intent.getExtras();
        if (b != null)
            return b.getString(Constants.FILE_NAME_PARAM);
        return null;
    }

    public static void backToMain(Activity currentActivity) {
        Intent i = new Intent(currentActivity, MainActivity.class);
        currentActivity.startActivity(i);
        currentActivity.finish();
    }

    public static String getFileContent(String fileName, ContextWrapper context) throws IOException {
        String completeFileName = fileName + Constants.FILE_EXTENSION;
        File[] files = context.getFilesDir().listFiles();
        for (File f : files) {
            if (f.getName().equals(completeFileName))
                return readFile(f);
        }
        return null;
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
}
