package com.easyapps.teleprompter.presentation;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.easyapps.teleprompter.R;
import com.easyapps.teleprompter.application.LyricApplicationService;
import com.easyapps.teleprompter.domain.model.lyric.Configuration;
import com.easyapps.teleprompter.domain.model.lyric.ILyricRepository;
import com.easyapps.teleprompter.domain.model.lyric.Lyric;
import com.easyapps.teleprompter.infrastructure.persistence.lyric.AndroidFileSystemLyricRepository;
import com.easyapps.teleprompter.presentation.components.PrompterView;
import com.easyapps.teleprompter.presentation.helper.ActivityUtils;

public class PrompterActivity extends AppCompatActivity {

    private PrompterView mPrompter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideUI();

        setContentView(R.layout.activity_prompter);
        mPrompter = (PrompterView) findViewById(R.id.fullscreen_content);

        String fileName = ActivityUtils.getFileNameParameter(getIntent());
        if (fileName != null) {
            loadFileIntoPrompter(fileName);
        } else {
            String message = getResources().getString(R.string.file_not_found);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    private void loadFileIntoPrompter(String fileName) {
        try {
            ILyricRepository lyricRepository =
                    new AndroidFileSystemLyricRepository(getApplicationContext());
            LyricApplicationService appService =
                    new LyricApplicationService(lyricRepository, null, null);

            Lyric lyric = appService.loadLyricWithConfiguration(fileName);
            mPrompter.setText(lyric.getContent());
            setPrompterDefinitions(lyric.getConfiguration());
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setPrompterDefinitions(Configuration config) {
        mPrompter.setAnimationId(R.anim.text_prompter);
        mPrompter.setTextSize(config.getFontSize());
        mPrompter.setScrollSpeed(config.getScrollSpeed());
        mPrompter.setTimeRunning(config.getTimerRunning());
        mPrompter.setTimeStopped(config.getTimerStopped());
        mPrompter.setTotalTimers(config.getTimersCount());
    }

    public void startStop(View view) {
        mPrompter.startStop();
    }

    private void hideUI() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    public void onBackPressed() {
        ActivityUtils.backToMain(this);
        String message = getResources().getString(R.string.prompting_canceled);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
