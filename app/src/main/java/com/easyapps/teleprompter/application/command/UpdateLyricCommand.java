package com.easyapps.teleprompter.application.command;

/**
 * Command for update Lyric.
 * Created by daniel on 03/10/2016.
 */

public class UpdateLyricCommand {
    private final String newName;
    private final String newContent;
    private final int newSongNumber;
    private final String oldName;

    public UpdateLyricCommand(String newName, String newContent, String newSongNumber, String oldName){
        this.newName = newName;
        this.newContent = newContent;
        this.oldName = oldName;
        this.newSongNumber = Integer.parseInt(newSongNumber);
    }

    public String getNewName() {
        return newName;
    }

    public String getNewContent() {
        return newContent;
    }

    public String getOldName() {
        return oldName;
    }

    public int getNewSongNumber() {
        return newSongNumber;
    }
}
