package com.easyapps.teleprompter.presentation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.easyapps.teleprompter.R;
import com.easyapps.teleprompter.application.LyricApplicationService;
import com.easyapps.teleprompter.application.command.AddLyricCommand;
import com.easyapps.teleprompter.application.command.UpdateLyricCommand;
import com.easyapps.teleprompter.domain.model.lyric.ILyricRepository;
import com.easyapps.teleprompter.domain.model.lyric.Lyric;
import com.easyapps.teleprompter.infrastructure.persistence.lyric.AndroidFileSystemLyricRepository;
import com.easyapps.teleprompter.presentation.helper.ActivityUtils;

public class CreateLyricActivity extends AppCompatActivity {

    private static final String TEXT_WRITTEN = "TEXT_WRITTEN";
    private static final String FILE_NAME = "FILE_NAME";
    private String mFileName = null;
    private LyricApplicationService mAppService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_file);
        EditText etTextFile = (EditText) findViewById(R.id.etTextFile);
        EditText etFileName = (EditText) findViewById(R.id.etFileName);

        // TODO these have to be injected (IoC).
        ILyricRepository mLyricRepository = new AndroidFileSystemLyricRepository(getApplicationContext());
        mAppService = new LyricApplicationService(mLyricRepository, null);

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
                    Lyric lyric = mAppService.loadLyric(mFileName);
                    etTextFile.setText(lyric.getContent());
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
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
            try {
                if (mFileName != null) {
                    UpdateLyricCommand cmd =
                            new UpdateLyricCommand(fileName, getTextContent(), mFileName);
                    mAppService.updateLyric(cmd);
                } else {
                    AddLyricCommand cmd = new AddLyricCommand(fileName, getTextContent());
                    mAppService.addLyric(cmd);
                }

                String message = getResources().getString(R.string.file_saved);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                ActivityUtils.backToMain(this);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
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
