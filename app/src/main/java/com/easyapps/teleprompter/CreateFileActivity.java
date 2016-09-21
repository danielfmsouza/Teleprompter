package com.easyapps.teleprompter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.easyapps.teleprompter.helper.ActivityUtils;
import com.easyapps.teleprompter.messages.Constants;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class CreateFileActivity extends AppCompatActivity {

    private static final String TEXT_WRITTEN = "TEXT_WRITTEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_file);
        EditText etTextFile = (EditText) findViewById(R.id.etTextFile);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            String savedText = savedInstanceState.getString(TEXT_WRITTEN);
            etTextFile.setText(savedText);

        } else {
            String fileName = ActivityUtils.getFileNameParameter(getIntent());
            if (fileName != null) {
                try {
                    etTextFile.setText(ActivityUtils.getFileContent(fileName, this));
                } catch (FileNotFoundException e) {
                    ActivityUtils.showMessage(R.string.file_not_found, getBaseContext(),
                            Toast.LENGTH_LONG);
                } catch (IOException e) {
                    ActivityUtils.showMessage(R.string.input_output_file_error, getBaseContext(),
                            Toast.LENGTH_LONG);
                }
                EditText etFileName = (EditText) findViewById(R.id.etFileName);
                etFileName.setText(fileName);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        String textToSave = getTextContent();

        savedInstanceState.putString(TEXT_WRITTEN, textToSave);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        ActivityUtils.backToMain(this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            return true;
        } else
            return super.dispatchKeyEvent(event);
    }

    public void SaveFile(View v) {
        String textToSave = getTextContent();
        String fileName = getFileName();
        try {
            FileOutputStream file = openFileOutput(fileName + Constants.FILE_EXTENSION, MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(file);
            outputWriter.write(textToSave);
            outputWriter.close();

            ActivityUtils.showMessage(R.string.file_saved, getBaseContext(),
                    Toast.LENGTH_SHORT);
            ActivityUtils.backToMain(this);
        } catch (Exception e) {
            ActivityUtils.showMessage(R.string.file_saving_error, getBaseContext(),
                    Toast.LENGTH_SHORT);
        }
    }

    private String getTextContent() {
        EditText etTextFile = (EditText) findViewById(R.id.etTextFile);
        return etTextFile.getText().toString();
    }

    private String getFileName() {
        EditText etFileName = (EditText) findViewById(R.id.etFileName);
        return etFileName.getText().toString();
    }
}
