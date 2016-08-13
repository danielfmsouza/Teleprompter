package com.easyapps.teleprompter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.easyapps.teleprompter.messages.Constants;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class CreateFileActivity extends AppCompatActivity {

    private static final String TEXT_WRITTEN = "TEXT_WRITTEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_file);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            EditText etTextFile = (EditText) findViewById(R.id.etTextFile);

            // Restore value of members from saved state
            String savedText = savedInstanceState.getString(TEXT_WRITTEN);
            etTextFile.setText(savedText);
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
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);

        finish();
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

            //display file saved message
            Toast.makeText(getBaseContext(), "File saved successfully!",
                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Problem while saving file",
                    Toast.LENGTH_SHORT).show();
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
