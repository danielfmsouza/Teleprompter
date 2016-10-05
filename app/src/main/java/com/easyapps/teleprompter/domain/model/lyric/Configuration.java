package com.easyapps.teleprompter.domain.model.lyric;

/**
 * Represents each configuration that a Lyric holds.
 * Created by daniel on 01/10/2016.
 */

public class Configuration {
    private int scrollSpeed;
    private int fontSize;
    private int timersCount;
    private int[] timerRunning;
    private int[] timerStopped;

    public static Configuration newCompleteInstance(int scrollSpeed, int[] timerRunning,
                                                    int fontSize, int[] timerStopped,
                                                    int timersCount) {
        return new Configuration(scrollSpeed, timerRunning, fontSize, timerStopped, timersCount);
    }

    private Configuration(int scrollSpeed, int[] timerRunning, int fontSize, int[] timerStopped,
                          int timersCount) {
        if (timerStopped == null)
            throw new IllegalArgumentException("timerStopped");
        if (timerRunning == null)
            throw new IllegalArgumentException("timerRunning");
        if (timersCount != timerRunning.length || timersCount != timerStopped.length)
            throw new IllegalArgumentException("timersCount");

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
