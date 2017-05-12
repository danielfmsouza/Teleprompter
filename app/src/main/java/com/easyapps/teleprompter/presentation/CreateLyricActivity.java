package com.easyapps.teleprompter.presentation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.easyapps.teleprompter.R;
import com.easyapps.teleprompter.application.LyricApplicationService;
import com.easyapps.teleprompter.application.command.AddLyricCommand;
import com.easyapps.teleprompter.application.command.UpdateLyricCommand;
import com.easyapps.teleprompter.domain.model.lyric.IConfigurationRepository;
import com.easyapps.teleprompter.domain.model.lyric.ILyricRepository;
import com.easyapps.teleprompter.domain.model.lyric.Lyric;
import com.easyapps.teleprompter.infrastructure.persistence.lyric.AndroidFileSystemLyricRepository;
import com.easyapps.teleprompter.infrastructure.persistence.lyric.AndroidPreferenceConfigurationRepository;
import com.easyapps.teleprompter.presentation.helper.ActivityUtils;

public class CreateLyricActivity extends AppCompatActivity {

    private static final String TEXT_WRITTEN = "TEXT_WRITTEN";
    private static final String SONG_NUMBER = "SONG_NUMBER";
    private static final String FILE_NAME = "FILE_NAME";
    private String mFileName = null;
    private LyricApplicationService mAppService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_file);
        EditText etTextFile = (EditText) findViewById(R.id.etTextFile);
        EditText etFileName = (EditText) findViewById(R.id.etFileName);
        EditText etSongNumber = (EditText) findViewById(R.id.etSongNumber);

        // TODO these have to be injected (IoC).
        ILyricRepository mLyricRepository =
                new AndroidFileSystemLyricRepository(getApplicationContext());
        IConfigurationRepository mConfigRepository =
                new AndroidPreferenceConfigurationRepository(getApplicationContext());

        mAppService = new LyricApplicationService(mLyricRepository, null, mConfigRepository, null, null);

        mFileName = ActivityUtils.getFileNameParameter(getIntent());

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            String savedText = savedInstanceState.getString(TEXT_WRITTEN);
            etTextFile.setText(savedText);

            String savedFileName = savedInstanceState.getString(FILE_NAME);
            etFileName.setText(savedFileName);

            String savedSongNumber = savedInstanceState.getString(SONG_NUMBER);
            etSongNumber.setText(savedSongNumber);
        } else {
            if (mFileName != null) {
                try {
                    Lyric lyric = mAppService.loadLyricWithConfiguration(mFileName);
                    etTextFile.setText(lyric.getContent());

                    String songNumber = String.valueOf(lyric.getConfiguration().getSongNumber());
                    etSongNumber.setText(songNumber);
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

    public void SaveFile(View v) {
        String fileName = getFileNameContent();
        String songNumber = getSongNumberContent();

        if (fileName.trim().equals(""))
            setErrorFileNameRequired();
        if (songNumber.trim().equals(""))
            setErrorSongNumberRequired();
        if (!fileName.trim().equals("") && !songNumber.trim().equals("")) {
            try {
                if (mFileName != null) {
                    UpdateLyricCommand cmd = new UpdateLyricCommand(fileName, getTextContent(),
                            songNumber, mFileName);

                    mAppService.updateLyric(cmd);
                } else {
                    AddLyricCommand cmd = new AddLyricCommand(fileName, getTextContent(), songNumber);
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

    private void setErrorSongNumberRequired() {
        EditText etSongNumber = (EditText) findViewById(R.id.etSongNumber);
        String error = getResources().getString(R.string.song_number_required);

        etSongNumber.setError(error);
    }

    private String getTextContent() {
        EditText etTextFile = (EditText) findViewById(R.id.etTextFile);
        return etTextFile.getText().toString();
    }

    private String getFileNameContent() {
        EditText etFileName = (EditText) findViewById(R.id.etFileName);
        return etFileName.getText().toString();
    }

    private String getSongNumberContent() {
        EditText etSongNumber = (EditText) findViewById(R.id.etSongNumber);
        return etSongNumber.getText().toString();
    }
}
