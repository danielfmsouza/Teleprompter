package com.easyapps.singerpro.presentation.component;

import android.widget.TextView;

import com.easyapps.singerpro.domain.model.lyric.Configuration;
import com.easyapps.singerpro.presentation.helper.CountDownTimer;
import com.easyapps.singerpro.query.model.lyric.ConfigurationQueryModel;

import java.util.ArrayList;
import java.util.List;

public class PrompterTimers {

    private final String textTimerStopped;
    private final String textTimerRunning;
    private final TimerListener timerListener;
    private final TextView tvCountTimer;
    private final List<CountDownTimerPrompter> countDownTimers = new ArrayList<>();
    private final Configuration timersConfig;

    public interface TimerListener {
        void onFinishTimer();
    }

    public PrompterTimers(Configuration timersConfig,
                          TimerListener timerListener,
                          TextView tvCountTimer,
                          String textTimerStopped,
                          String textTimerRunning) {
        if (timersConfig == null)
            throw new IllegalArgumentException("timersConfig cannot be null");
        if (timerListener == null)
            throw new IllegalArgumentException("timerListener cannot be null");
        if (tvCountTimer == null)
            throw new IllegalArgumentException("tvCountTimer cannot be null");
        if (textTimerStopped == null)
            throw new IllegalArgumentException("textTimerStopped cannot be null");
        if (textTimerRunning == null)
            throw new IllegalArgumentException("textTimerRunning cannot be null");

        this.timerListener = timerListener;
        this.tvCountTimer = tvCountTimer;
        this.timersConfig = timersConfig;
        this.textTimerRunning = textTimerRunning;
        this.textTimerStopped = textTimerStopped;

        createCountDownTimers();
    }

    public boolean initialize() {
        if (timersConfig.getTimersCount() > 0) {
            countDownTimers.get(0).start();
            return true;
        }
        return false;
    }

    public void startStopCurrentTimer() {
        CountDownTimerPrompter currentTimer = getCurrentTimer();
        if (currentTimer == null) {
            return;
        }

        if (currentTimer.isPaused()) {
            currentTimer.resume();
        } else {
            currentTimer.pause();
        }
    }

    private CountDownTimerPrompter getCurrentTimer() {
        for (CountDownTimerPrompter timer : countDownTimers) {
            if (timer.isRunning()) {
                return timer;
            }
        }
        return null;
    }

    private void createCountDownTimers() {
        final int SECONDS_TO_MILLISECONDS = 1000;

        int pos = 0;
        for (int i = 0; i < timersConfig.getTimersCount(); i++, pos++) {
            if (timersConfig.getTimerRunning()[i] == 0) {
                countDownTimers.add(new CountDownTimerPrompter(
                        timersConfig.getTimerStopped()[i] * SECONDS_TO_MILLISECONDS,
                        textTimerStopped, pos, tvCountTimer));
                break;
            }
            countDownTimers.add(new CountDownTimerPrompter(
                    timersConfig.getTimerStopped()[i] * SECONDS_TO_MILLISECONDS,
                    textTimerStopped, pos, tvCountTimer));
            countDownTimers.add(new CountDownTimerPrompter(
                    timersConfig.getTimerRunning()[i] * SECONDS_TO_MILLISECONDS,
                    textTimerRunning, ++pos, tvCountTimer));
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

    private class CountDownTimerPrompter extends CountDownTimer {
        private boolean finished = false;
        private final int id;
        private final String timerLabel;
        private final TextView tvCountTimer;

        CountDownTimerPrompter(long timeToCount,
                               String timerLabel,
                               int id,
                               TextView tvCountTimer) {
            super(timeToCount, 1000);
            this.id = id;
            this.timerLabel = timerLabel;
            this.tvCountTimer = tvCountTimer;
        }

        @Override
        public void onTick(long countDownInterval) {
            setCountTimerText(String.format(timerLabel, (id + 2)/2, countDownInterval / 1000));
        }

        @Override
        public void onFinish() {
            timerListener.onFinishTimer();
            if (!finished)
                startNextTimer(id + 1);

            finished = true;
            setCountTimerText("");
        }

        private void setCountTimerText(String text) {
            if (tvCountTimer != null) {
                tvCountTimer.setText(text);
            }
        }
    }

    private synchronized void startNextTimer(int id) {
        if (id >= 0 && id < countDownTimers.size()) {
            countDownTimers.get(id).start();
        }
    }
}
