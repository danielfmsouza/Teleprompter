package com.easyapps.singerpro.query.model.lyric;

/**
 * Query model for Configuration.
 * Created by daniel on 04/10/2016.
 */

public class ConfigurationQueryModel {

    private int scrollSpeed;
    private int fontSize;
    private int timersCount;
    private int songNumber;
    private int[] timerRunning;
    private int[] timerStopped;

    public ConfigurationQueryModel(int scrollSpeed, int[] timerRunning, int fontSize,
                                   int timersCount,  int[] timerStopped, int songNumber) {
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
}
