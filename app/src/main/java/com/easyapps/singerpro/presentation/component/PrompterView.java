package com.easyapps.singerpro.presentation.component;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.easyapps.singerpro.R;
import com.easyapps.singerpro.presentation.helper.CountDownTimer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 13/08/2016.
 * View ready to hold a animated text on the screen. Besides that, it stills controls
 * CountDownTimerPrompter instances.
 */
public class PrompterView extends androidx.appcompat.widget.AppCompatTextView {
    private int animationId;

    private int scrollSpeed;
    private int[] timeRunning;
    private int[] timeWaiting;
    private int totalTimers;
    private String fileName;
    private boolean isHtmlFormatted = false;
    private boolean animationPrepared = false;
    private final CountDownTimerPrompter initialTimer = new CountDownTimerPrompter(1, "", -2, null);
    private final List<CountDownTimerPrompter> timers = new ArrayList<>();
    Animation animation;
    PausablePrompterAnimation.OnFinishAnimationListener listener;

    public PrompterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startStop();
            }
        });
        setOnFinishAnimationListener(context);
        formatPrompter(context);
    }

    private void setOnFinishAnimationListener(Context context) {
        if (context instanceof PausablePrompterAnimation.OnFinishAnimationListener) {
            listener = (PausablePrompterAnimation.OnFinishAnimationListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFinishAnimationCallback");
        }
    }

    private void formatPrompter(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        String backgroundColorDefault =
                getResources().getString(R.string.pref_backgroundColor_default);
        String textColorDefault = getResources().getString(R.string.pref_textColor_default);
        String fontFamilyDefault = getResources().getString(R.string.pref_fontFamily_default);

        int backgroundColor = Integer.parseInt(sharedPref.getString(
                getResources().getString(R.string.pref_key_backgroundColor), backgroundColorDefault));
        int textColor = Integer.parseInt(sharedPref.getString(
                getResources().getString(R.string.pref_key_textColor), textColorDefault));
        String fontFamily = sharedPref.getString(
                getResources().getString(R.string.pref_key_fontFamily), fontFamilyDefault);

        setTypeface(Typeface.create(fontFamily, Typeface.BOLD));
        setTextColor(textColor);
        setBackgroundColor(backgroundColor);
    }

    public void startAnimation(TextView tvCountTimer) {
        if (animationPrepared) {
            startAnimation(animation);
            initialTimer.start();
            createTimers(tvCountTimer);
            initializeTimers();
        }
    }

    public PrompterView(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        loadAnimation(changed, b);
    }

    private void loadAnimation(boolean changed, int b) {
        if (changed && !animationPrepared && fileName != null) {
            animation = PausablePrompterAnimation.loadAnimation(
                    getContext(), animationId, b, scrollSpeed, fileName, listener);

            animationPrepared = true;
        }
    }

    private void createTimers(TextView tvCountTimer) {
        final int SECONDS_TO_MILLISECONDS = 1000;
        final String textTimerWaiting = getResources().getString(R.string.tv_timeWaiting);
        final String textTimerRunning = getResources().getString(R.string.tv_timeRunning);

        if (totalTimers > 0) {
            int pos = 0;
            for (int i = 0; i < totalTimers; i++, pos++) {
                if (timeRunning[i] == 0) {
                    timers.add(new CountDownTimerPrompter(
                            timeWaiting[i] * SECONDS_TO_MILLISECONDS,
                            textTimerWaiting, pos, tvCountTimer));
                    break;
                }
                timers.add(new CountDownTimerPrompter(
                        timeWaiting[i] * SECONDS_TO_MILLISECONDS,
                        textTimerWaiting, pos, tvCountTimer));
                timers.add(new CountDownTimerPrompter(
                        timeRunning[i] * SECONDS_TO_MILLISECONDS,
                        textTimerRunning, ++pos, tvCountTimer));
            }
        } else
            startStopAnimation();
    }

    private void initializeTimers() {
        if (timers.size() > 0) {
            timers.get(0).startTimer();
        }
    }

    public void setAnimationId(int id) {
        animationId = id;
    }

    public void startStop() {
        startStopAnimation();
        CountDownTimerPrompter current = getCurrentTimer();
        startStopCurrentTimer(current);
    }

    private void startStopAnimation() {
        if (getAnimation() != null) {
            ((PausablePrompterAnimation) getAnimation()).startStop();
        }
    }

    public void setScrollSpeed(int scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    public void setTimeRunning(int[] timeRunning) {
        this.timeRunning = timeRunning;
    }

    public void setTimeStopped(int[] timeWaiting) {
        this.timeWaiting = timeWaiting;
    }

    public void setTotalTimers(int totalTimers) {
        this.totalTimers = totalTimers;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setText(String text){
        if (isHtmlFormatted){
            setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
        }
        else{
            super.setText(text);
        }
    }

    public void setHtmlFormatted(boolean isHtmlFormatted) {
        this.isHtmlFormatted = isHtmlFormatted;
    }

    private class CountDownTimerPrompter extends CountDownTimer {
        private final String text;
        private boolean finished = false;
        private boolean isRunning = false;
        private final int id;
        private final TextView tvCountTimer;

        CountDownTimerPrompter(long timeToCount, String text, int id, TextView tvCountTimer) {
            super(timeToCount, 1000);
            this.id = id;
            this.text = text;
            this.tvCountTimer = tvCountTimer;
        }

        public void startTimer() {
            isRunning = true;
            start();
        }

        @Override
        public void onTick(long countDownInterval) {
            setCountTimerText(String.format(text, id + 1, countDownInterval / 1000));
        }

        @Override
        public void onFinish() {
            startStopAnimation();
            if (!finished)
                startNextTimer(id + 1);

            finished = true;
            isRunning = false;
            setCountTimerText("");
        }

        private void setCountTimerText(String text) {
            if (tvCountTimer != null) {
                tvCountTimer.setText(text);
            }
        }
    }

    private CountDownTimerPrompter getCurrentTimer() {
        for (CountDownTimerPrompter timer : timers) {
            if (timer.isRunning) {
                return timer;
            }
        }
        return null;
    }

    private synchronized void startNextTimer(int id) {
        if (id >= 0 && id < timers.size()) {
            timers.get(id).startTimer();
        }
    }

    private void startStopCurrentTimer(CountDownTimerPrompter currentTimer) {
        if (currentTimer == null) return;

        if (currentTimer.isPaused()) {
            currentTimer.resume();
        } else {
            currentTimer.pause();
        }
    }
}

