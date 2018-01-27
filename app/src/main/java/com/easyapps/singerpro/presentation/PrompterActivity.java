package com.easyapps.singerpro.presentation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.Toast;

import com.easyapps.singerpro.application.AutomaticPlayingApplicationService;
import com.easyapps.singerpro.application.LyricApplicationService;
import com.easyapps.singerpro.domain.model.lyric.Configuration;
import com.easyapps.singerpro.domain.model.lyric.ILyricRepository;
import com.easyapps.singerpro.domain.model.lyric.Lyric;
import com.easyapps.singerpro.infrastructure.persistence.lyric.AndroidFileSystemLyricFinder;
import com.easyapps.singerpro.infrastructure.persistence.lyric.AndroidFileSystemLyricRepository;
import com.easyapps.singerpro.presentation.components.PrompterView;
import com.easyapps.singerpro.presentation.helper.ActivityUtils;
import com.easyapps.singerpro.query.model.lyric.ILyricFinder;
import com.easyapps.teleprompter.R;

public class PrompterActivity extends AppCompatActivity {

    private PrompterView mPrompter;
    private boolean playNext;
    private int timeBeforeStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean hasFinishedAnimationParameter =
                ActivityUtils.getHasFinishedAnimationParameter(getIntent());

        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        int timeBeforeStartDefault = 0;
        playNext = sharedPref.getBoolean(
                getResources().getString(R.string.pref_key_playNext), false);
        timeBeforeStart = sharedPref.getInt(
                getResources().getString(R.string.pref_key_timeBeforeStart), timeBeforeStartDefault);
        String playlistName = ActivityUtils.getCurrentPlaylistName(this);

        boolean automaticPlaying = playNext && hasFinishedAnimationParameter;
        if (automaticPlaying) {
            setNextFileNameToPrompt(playlistName);
        }
        if (playNext || !hasFinishedAnimationParameter) {

            hideUI();

            setContentView(R.layout.activity_prompter);
            mPrompter = (PrompterView) findViewById(R.id.fullscreen_content);
            String fileName = ActivityUtils.getFileNameParameter(getIntent());
            if (fileName != null) {
                loadFileIntoPrompter(fileName, playlistName);
            } else {
                if (automaticPlaying) {
                    ActivityUtils.backToMain(this);
                    showToastMessage(R.string.prompting_finished);
                } else {
                    ActivityUtils.backToMain(this);
                    showToastMessage(R.string.file_not_found);
                }
            }
            setScrollViewBackgroundColor();
            VerifyTimeBeforeStartAnimation();
        } else {
            ActivityUtils.backToMain(this);
            showToastMessage(R.string.prompting_finished);
        }
    }

    private void showToastMessage(int resourceId) {
        String message = getResources().getString(resourceId);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void setNextFileNameToPrompt(String setListName) {
        ILyricFinder lyricFinder
                = new AndroidFileSystemLyricFinder(getApplicationContext());

        AutomaticPlayingApplicationService appService =
                new AutomaticPlayingApplicationService(lyricFinder);

        String fileName = ActivityUtils.getFileNameParameter(getIntent());
        String newLyricToPlay = null;
        try {
            newLyricToPlay = appService.loadNextLyricNameFromSetList(setListName, fileName);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        ActivityUtils.setLyricFileNameParameter(newLyricToPlay, getIntent());
    }

    private void VerifyTimeBeforeStartAnimation() {
        ViewTreeObserver vto = mPrompter.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                CountDownTimeBeforeStartAnimation(timeBeforeStart, playNext);
                mPrompter.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void CountDownTimeBeforeStartAnimation(final int timeBeforeStart, boolean playNext) {
        if (playNext) {
            final int leftover = 500; // I need to add a half second so the toast is shown correctly
            new CountDownTimer(timeBeforeStart * 1000 + leftover, 1000) {
                int aux = timeBeforeStart;
                Toast toast = Toast.makeText(getApplicationContext(), "...",
                        Toast.LENGTH_SHORT);

                public void onTick(long millisUntilFinished) {
                    toast.cancel();
                    toast = Toast.makeText(getApplicationContext(), aux-- + "...",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }

                public void onFinish() {
                    toast.cancel();
                    mPrompter.startAnimation();
                }
            }.start();

        } else
            mPrompter.startAnimation();
    }

    private void setScrollViewBackgroundColor() {
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        String backgroundColorDefault =
                getResources().getString(R.string.pref_backgroundColor_default);
        int backgroundColor = Integer.parseInt(sharedPref.getString(
                getResources().getString(R.string.pref_key_backgroundColor), backgroundColorDefault));

        ScrollView scrollView = findViewById(R.id.svText);
        scrollView.setBackgroundColor(backgroundColor);
    }

    private void loadFileIntoPrompter(String fileName, String setListName) {
        try {
            ILyricRepository lyricRepository =
                    new AndroidFileSystemLyricRepository(getApplicationContext());
            LyricApplicationService appService =
                    new LyricApplicationService(lyricRepository, null, null, null, null);

            Lyric lyric = appService.loadLyricWithConfiguration(fileName, false);
            mPrompter.setText(lyric.getContent());
            setPrompterDefinitions(lyric.getConfiguration(), setListName, fileName);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setPrompterDefinitions(Configuration config, String setListName, String fileName) {
        mPrompter.setAnimationId(R.anim.text_prompter);
        mPrompter.setTextSize(config.getFontSize());
        mPrompter.setScrollSpeed(config.getScrollSpeed());
        mPrompter.setTimeRunning(config.getTimerRunning());
        mPrompter.setTimeStopped(config.getTimerStopped());
        mPrompter.setTotalTimers(config.getTimersCount());
        mPrompter.setSetListName(setListName);
        mPrompter.setFileName(fileName);
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
        showToastMessage(R.string.prompting_canceled);
    }
}
