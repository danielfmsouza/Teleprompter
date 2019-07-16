package com.easyapps.singerpro.presentation.activity;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.easyapps.singerpro.R;
import com.easyapps.singerpro.application.LyricApplicationService;
import com.easyapps.singerpro.domain.model.lyric.Configuration;
import com.easyapps.singerpro.domain.model.lyric.IQueueLyricRepository;
import com.easyapps.singerpro.domain.model.lyric.Lyric;
import com.easyapps.singerpro.presentation.component.CustomScrollView;
import com.easyapps.singerpro.presentation.component.PausablePrompterAnimation;
import com.easyapps.singerpro.presentation.component.PrompterView;
import com.easyapps.singerpro.presentation.helper.ActivityUtils;

import org.w3c.dom.Text;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class PrompterActivity extends AppCompatActivity
        implements CustomScrollView.OnFinishAnimationCallback {
    private CustomScrollView mPrompter;
    private String mLyricName;

    @Inject
    SharedPreferences sharedPref;

    @Inject
    IQueueLyricRepository lyricQueue;

    @Inject
    LyricApplicationService mLyricAppService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        hideUI();
        playQueuedLyric(null);
    }

    private void playQueuedLyric(String lyricPreviouslyPlayed) {
        mLyricName = lyricQueue.getNextLyricToPlay();

        if (mLyricName == null || mLyricName.isEmpty()) {
            backToCallerActivity(lyricPreviouslyPlayed);
            showToastMessage(R.string.prompting_finished);
        } else {
            loadFileIntoPrompter(mLyricName);
            setScrollViewBackgroundColor();
            verifyTimeBeforeStartAnimation();
        }
    }

    private void backToCallerActivity(String fileName) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        ActivityUtils.backToCaller(this, fileName);
    }

    private void showToastMessage(int resourceId) {
        String message = getResources().getString(resourceId);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void verifyTimeBeforeStartAnimation() {
        int timeBeforeStartDefault = 0;
        final boolean playNext = sharedPref.getBoolean(
                getResources().getString(R.string.pref_key_playNext), false);
        final int timeBeforeStart = sharedPref.getInt(
                getResources().getString(R.string.pref_key_timeBeforeStart), timeBeforeStartDefault);

        ViewTreeObserver vto = mPrompter.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                countDownTimeBeforeStartAnimation(timeBeforeStart, playNext);
                mPrompter.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void countDownTimeBeforeStartAnimation(final int timeBeforeStart, boolean playNext) {
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
                    mPrompter.startAnimation((TextView) findViewById(R.id.tvCountTimer));
                }
            }.start();

        } else {
            mPrompter.startAnimation((TextView) findViewById(R.id.tvCountTimer));
        }
    }

    private void setScrollViewBackgroundColor() {
        String backgroundColorDefault =
                getResources().getString(R.string.pref_backgroundColor_default);
        int backgroundColor = Integer.parseInt(sharedPref.getString(
                getResources().getString(R.string.pref_key_backgroundColor), backgroundColorDefault));

        mPrompter.setBackgroundColor(backgroundColor);
    }

    private void setTextViewPaddingBottom(TextView textView) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int heightToAdd = size.y;

        textView.setPadding(0, 0, 0, heightToAdd);
    }

    private void loadFileIntoPrompter(String fileName) {
        setContentView(R.layout.activity_prompter);
        mPrompter = findViewById(R.id.svText);
        TextView textView = findViewById(R.id.fullscreen_content);
        try {
            Lyric lyric = mLyricAppService.loadLyricWithConfiguration(fileName, false);
            setPrompterDefinitions(lyric.getConfiguration(), fileName);
            setTextViewDefinitions(textView, lyric);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        setTextViewPaddingBottom(textView);
    }

    private void setPrompterDefinitions(Configuration config, String fileName) {
//        mPrompter.setAnimationId(R.anim.text_prompter);
//        mPrompter.setTextSize(config.getFontSize());
        mPrompter.setScrollSpeed(config.getScrollSpeed());
        mPrompter.setTimeRunning(config.getTimerRunning());
        mPrompter.setTimeStopped(config.getTimerStopped());
        mPrompter.setTotalTimers(config.getTimersCount());
//        mPrompter.setHtmlFormatted(config.isHtmlFormatted());
        mPrompter.setFileName(fileName);
    }

    private void setTextViewDefinitions(TextView textView, Lyric lyric) {
        String textColorDefault = getResources().getString(R.string.pref_textColor_default);
        String fontFamilyDefault = getResources().getString(R.string.pref_fontFamily_default);
        String backgroundColorDefault = getResources().getString(R.string.pref_backgroundColor_default);

        int textColor = Integer.parseInt(sharedPref.getString(
                getResources().getString(R.string.pref_key_textColor), textColorDefault));
        String fontFamily = sharedPref.getString(
                getResources().getString(R.string.pref_key_fontFamily), fontFamilyDefault);
        int backgroundColor = Integer.parseInt(sharedPref.getString(
                getResources().getString(R.string.pref_key_backgroundColor), backgroundColorDefault));

        textView.setTypeface(Typeface.create(fontFamily, Typeface.BOLD));
        textView.setTextColor(textColor);
        textView.setTextSize(lyric.getConfiguration().getFontSize());
        textView.setBackgroundColor(backgroundColor);

        if (lyric.getConfiguration().isHtmlFormatted()) {
            textView.setText(Html.fromHtml(lyric.getContent()), TextView.BufferType.SPANNABLE);
        } else {
            textView.setText(lyric.getContent());
        }
    }

    private void hideUI() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    public void onBackPressed() {
        backToCallerActivity(mLyricName);
        showToastMessage(R.string.prompting_canceled);
        mPrompter.cancelAnimation();
    }

    @Override
    public void onFinishAnimation(String fileScrolled) {
//        mPrompter.setText("");
        playQueuedLyric(fileScrolled);
    }
}