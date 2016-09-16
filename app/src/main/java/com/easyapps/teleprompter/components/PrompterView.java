package com.easyapps.teleprompter.components;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danielfmsouza on 13/08/2016.
 */
public class PrompterView extends TextView {
    private int animationId;

    private int scrollSpeed;
    private int timeRunning;
    private int timeWaiting;
    private int totalTimers;

    private List<CountDownTimerPrompter> timers = new ArrayList<>();

    public PrompterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PrompterView(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed) {
            final Animation animation = PausablePrompterAnimation.loadAnimation(
                    getContext(), animationId, b, scrollSpeed);
            startAnimation(animation);

            // this is needed to make animation work
            new CountDownTimerPrompter(1, -2).start();

            createTimers();
            initializeTimers();
        }
    }

    private void createTimers() {
        final int SECONDS_TO_MILLISECONDS = 1000;

        if (totalTimers > 0) {
            if (timeRunning == 0)
                timers.add(new CountDownTimerPrompter(timeWaiting * SECONDS_TO_MILLISECONDS, 0));
            else
                for (int i = 0; i < totalTimers * 2; i++) {
                    timers.add(new CountDownTimerPrompter(timeWaiting * SECONDS_TO_MILLISECONDS, i));
                    timers.add(new CountDownTimerPrompter(timeRunning * SECONDS_TO_MILLISECONDS, ++i));
                }
        }
        else
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

    public boolean isRunning() {
        if (getAnimation() != null)
            return ((PausablePrompterAnimation) getAnimation()).isRunning();
        return false;
    }

    public void setScrollSpeed(int scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    public void setTimeRunning(int timeRunning) {
        this.timeRunning = timeRunning;
    }

    public void setTimeWaiting(int timeWaiting) {
        this.timeWaiting = timeWaiting;
    }

    public void setTotalTimers(int totalTimers) {
        this.totalTimers = totalTimers;
    }

    class CountDownTimerPrompter extends CountDownTimer {

        private int id;

        public CountDownTimerPrompter(long timeToCount, int id) {
            super(timeToCount, timeToCount);
            this.id = id;
        }

        @Override
        public void onTick(long countDownInterval) {
        }

        @Override
        public void onFinish() {
            startStop();
            startNextTimer(id + 1);
        }
    }

    private void startNextTimer(int id) {
        if (id >= 0 && id < timers.size())
            timers.get(id).start();
    }
}
