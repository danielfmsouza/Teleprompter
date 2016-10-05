package com.easyapps.teleprompter.query.model.lyric;

/**
 * Query model for Configuration.
 * Created by daniel on 04/10/2016.
 */

public class ConfigurationQueryModel {

    private int scrollSpeed;
    private int fontSize;
    private int timersCount;
    private int[] timerRunning;
    private int[] timerStopped;

    public ConfigurationQueryModel(int scrollSpeed, int[] timerRunning, int fontSize,
                                   int timersCount,  int[] timerStopped) {
        this.scrollSpeed = scrollSpeed;
        this.fontSize = fontSize;
        this.timersCount = timersCount;
        this.timerRunning = timerRunning;
        this.timerStopped = timerStopped;
    }

    public int getScrollSpeed() {
        return scrollSpeed;
    }

    public int getFontSize() {
        return fontSize;
    }

    public int getTimersCount() {
        return timersCount;
    }

    public int[] getTimerRunning() {
        return timerRunning;
    }

    public int[] getTimerStopped() {
        return timerStopped;
    }
}
