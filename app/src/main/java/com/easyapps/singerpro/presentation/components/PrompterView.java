package com.easyapps.singerpro.presentation.components;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.widget.TextView;

import com.easyapps.teleprompter.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 13/08/2016.
 * View ready to hold a animated text on the screen. Besides that, it stills controls
 * CountDownTimerPrompter instances.
 */
public class PrompterView extends TextView {
    private int animationId;

    private int scrollSpeed;
    private int[] timeRunning;
    private int[] timeWaiting;
    private int totalTimers;
    private String setListName;
    private String fileName;
    private boolean animationPrepared = false;
    private boolean playNext = false;
    private int timeBeforeNextSong;
    private final CountDownTimerPrompter initialTimer = new CountDownTimerPrompter(1, -2);
    private final List<CountDownTimerPrompter> timers = new ArrayList<>();
    Animation animation;

    public PrompterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        formatPrompter(context);
    }

    private void formatPrompter(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        String backgroundColorDefault =
                getResources().getString(R.string.pref_backgroundColor_default);
        String textColorDefault = getResources().getString(R.string.pref_textColor_default);

        int backgroundColor = Integer.parseInt(sharedPref.getString(
                getResources().getString(R.string.pref_key_backgroundColor), backgroundColorDefault));
        int textColor = Integer.parseInt(sharedPref.getString(
                getResources().getString(R.string.pref_key_textColor), textColorDefault));


        setTextColor(textColor);
        setBackgroundColor(backgroundColor);
    }

    public void startAnimation() {
        if (animationPrepared) {
            startAnimation(animation);

            // this is needed to make animation work
            initialTimer.start();

            createTimers();
            initializeTimers();
        }
    }

    public PrompterView(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed && !animationPrepared) {
            animation = PausablePrompterAnimation.loadAnimation(
                    getContext(), animationId, b, scrollSpeed, setListName, fileName);

            // this is needed to make animation work

            animationPrepared = true;
        }
    }

    private void createTimers() {
        final int SECONDS_TO_MILLISECONDS = 1000;

        if (totalTimers > 0) {
            int pos = 0;
            for (int i = 0; i < totalTimers; i++, pos++) {
                if (timeRunning[i] == 0) {
                    timers.add(new CountDownTimerPrompter(timeWaiting[i] * SECONDS_TO_MILLISECONDS, pos));
                    break;
                }
                timers.add(new CountDownTimerPrompter(timeWaiting[i] * SECONDS_TO_MILLISECONDS, pos));
                timers.add(new CountDownTimerPrompter(timeRunning[i] * SECONDS_TO_MILLISECONDS, ++pos));
            }
        } else
            startStop();
    }

    private void initializeTimers() {
        if (timers.size() > 0)
            timers.get(0).start();
    }

    public void setAnimationId(int id) {
        animationId = id;
    }

    public void startStop() {
        if (getAnimation() != null)
            ((PausablePrompterAnimation) getAnimation()).startStop();
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

    public void setSetListName(String setListName) {
        this.setListName = setListName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private class CountDownTimerPrompter extends CountDownTimer {

        private boolean finished = false;
        private final int id;

        CountDownTimerPrompter(long timeToCount, int id) {
            super(timeToCount, timeToCount);
            this.id = id;
        }

        @Override
        public void onTick(long countDownInterval) {
        }

        @Override
        public void onFinish() {
            startStop();
            if (!finished)
                startNextTimer(id + 1);

            finished = true;
        }
    }

    private synchronized void startNextTimer(int id) {
        if (id >= 0 && id < timers.size()) {
            timers.get(id).start();
        }
    }
}

