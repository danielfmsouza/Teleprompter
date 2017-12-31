package com.easyapps.singerpro.domain.model.lyric;

import android.support.annotation.NonNull;

/**
 * Represents each configuration that a Lyric holds.
 * Created by daniel on 01/10/2016.
 */

public class Configuration {
    private int scrollSpeed;
    private int fontSize;
    private int timersCount;
    private int songNumber;
    private int[] timerRunning;
    private int[] timerStopped;

    private Configuration(int songNumber) {
        this.songNumber = songNumber;
    }

    public static Configuration newCompleteInstance(int scrollSpeed, int[] timerRunning,
                                                    int fontSize, int[] timerStopped,
                                                    int timersCount, int songNumber) {
        return new Configuration(scrollSpeed, timerRunning, fontSize, timerStopped, timersCount,
                songNumber);
    }

    private Configuration(int scrollSpeed, int[] timerRunning, int fontSize, int[] timerStopped,
                          int timersCount, int songNumber) {
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
        this.songNumber = songNumber;
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

    public int getSongNumber() {
        return songNumber;
    }

    @NonNull
    public static Configuration newLightInstance(int songNumber) {
        return new Configuration(songNumber);
    }
}
