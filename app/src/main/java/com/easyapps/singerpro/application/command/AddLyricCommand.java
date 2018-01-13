package com.easyapps.singerpro.application.command;

/**
 * Command for Lyric object.
 * Created by daniel on 03/10/2016.
 */

public class AddLyricCommand {

    private final String name;
    private final String content;
    private final String songNumber;

    public AddLyricCommand(String name, String content, String songNumber) {
        this.name = name;
        this.content = content;
        this.songNumber = songNumber;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public String getSongNumber() {
        return songNumber;
    }
}
