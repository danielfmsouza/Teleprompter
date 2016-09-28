package com.easyapps.teleprompter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.easyapps.teleprompter.helper.ActivityUtils;
import com.easyapps.teleprompter.messages.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class CreateFileActivity extends AppCompatActivity {

    private static final String TEXT_WRITTEN = "TEXT_WRITTEN";
    private static final String FILE_NAME = "FILE_NAME";
    private String mFileName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_file);
        EditText etTextFile = (EditText) findViewById(R.id.etTextFile);
        EditText etFileName = (EditText) findViewById(R.id.etFileName);

        mFileName = ActivityUtils.getFileNameParameter(getIntent());

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            String savedText = savedInstanceState.getString(TEXT_WRITTEN);
            etTextFile.setText(savedText);

            String savedFileName = savedInstanceState.getString(FILE_NAME);
            etFileName.setText(savedFileName);
        } else {
            if (mFileName != null) {
                try {
                    etTextFile.setText(ActivityUtils.getFileContent(mFileName, this));
                } catch (FileNotFoundException e) {
                    String message = getResources().getString(R.string.file_not_found);
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    String message = getResources().getString(R.string.input_output_file_error);
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                }
                etFileName.setText(mFileName);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        String textToSave = getTextContent();
        String fileNameToSave = getFileNameContent();

        savedInstanceState.putString(TEXT_WRITTEN, textToSave);
        savedInstanceState.putString(FILE_NAME, fileNameToSave);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        ActivityUtils.backToMain(this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            return true;
        } else
            return super.dispatchKeyEvent(event);
    }

    public void SaveFile(View v) {
        String fileName = getFileNameContent();
        if (fileName.trim().equals(""))
            setErrorFileNameRequired();
        else {
            boolean renameSuccess = true;
            if (mFileName != null && !mFileName.equals(fileName))
                renameSuccess = renameFile(fileName);

            if (renameSuccess && (isEditingSameFile(fileName) || newFileNotExists(fileName))) {
                String textToSave = getTextContent();
                try {
                    FileOutputStream file = openFileOutput(
                            fileName + Constants.FILE_EXTENSION, MODE_PRIVATE);
                    OutputStreamWriter outputWriter = new OutputStreamWriter(file);
                    outputWriter.write(textToSave);
                    outputWriter.close();

                    String message = getResources().getString(R.string.file_saved);
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    ActivityUtils.backToMain(this);
                } catch (Exception e) {
                    String message = getResources().getString(R.string.file_saving_error);
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private boolean isEditingSameFile(String fileName) {
        return (mFileName != null && fileName.equals(mFileName));
    }

    private boolean newFileNotExists(String fileName) {
        return !fileExists(fileName, getFilesDir());
    }

    private boolean fileExists(final String name, File dir) {
        File[] filesFiltered = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File directory, String fileName) {
                return fileName.equals(name + Constants.FILE_EXTENSION);
            }
        });

        if (filesFiltered != null && filesFiltered.length > 0) {
            String message = getResources().getString(R.string.file_exists_error, name);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    private boolean renameFile(String newFileName) {
        File dir = getFilesDir();
        File oldFile = new File(dir, mFileName + Constants.FILE_EXTENSION);
        File newFile = new File(dir, newFileName + Constants.FILE_EXTENSION);

        if (fileExists(newFileName, dir))
            return false;

        if (oldFile.exists()) {
            if (!oldFile.renameTo(newFile)) {
                String message = getResources().getString(R.string.file_rename_error, mFileName);
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                return false;
            } else {
                renamePreferences(mFileName, newFileName);
                mFileName = newFileName;
                return true;
            }
        } else {
            String message = getResources().getString(R.string.file_not_found);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void renamePreferences(String oldFileName, String newFileName) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        String scrollSpeedPrefKey = getResources().getString(R.string.pref_key_scrollSpeed);
        String totalTimersPrefKey = getResources().getString(R.string.pref_key_totalTimers);
        String textSizePrefKey = getResources().getString(R.string.pref_key_textSize);
        String timeRunningPrefKey = getResources().getString(R.string.pref_key_timeRunning);
        String timeWaitingPrefKey = getResources().getString(R.string.pref_key_timeWaiting);

        int scrollSpeed = preferences.getInt(scrollSpeedPrefKey + oldFileName, 0);
        int totalTimers = preferences.getInt(totalTimersPrefKey + oldFileName, 0);
        int textSize = preferences.getInt(textSizePrefKey + oldFileName, 0);

        editor.remove(scrollSpeedPrefKey);
        editor.remove(totalTimersPrefKey);
        editor.remove(textSizePrefKey);

        if (scrollSpeed != 0)
            editor.putInt(scrollSpeedPrefKey + newFileName, scrollSpeed);
        if (totalTimers != 0)
            editor.putInt(totalTimersPrefKey + newFileName, totalTimers);
        if (textSize != 0)
            editor.putInt(textSizePrefKey + newFileName, textSize);

        for (int i = 0; i < totalTimers; i++) {
            int timeRunning = preferences.getInt(timeRunningPrefKey + oldFileName + i, 0);
            int timeWaiting = preferences.getInt(timeWaitingPrefKey + oldFileName + i, 0);

            editor.remove(timeRunningPrefKey + oldFileName + i);
            editor.remove(timeWaitingPrefKey + oldFileName + i);

            if (timeRunning != 0)
                editor.putInt(timeRunningPrefKey + newFileName + i, timeRunning);
            if (timeWaiting != 0)
                editor.putInt(timeWaitingPrefKey + newFileName + i, timeWaiting);
        }

        editor.apply();
    }

    private void setErrorFileNameRequired() {
        EditText etFileName = (EditText) findViewById(R.id.etFileName);
        String error = getResources().getString(R.string.file_name_required);

        etFileName.setError(error);
    }

    private String getTextContent() {
        EditText etTextFile = (EditText) findViewById(R.id.etTextFile);
        return etTextFile.getText().toString();
    }

    private String getFileNameContent() {
        EditText etFileName = (EditText) findViewById(R.id.etFileName);
        return etFileName.getText().toString();
    }
}
