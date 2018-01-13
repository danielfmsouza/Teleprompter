package com.easyapps.singerpro.application.command;

/**
 * Command for update Lyric.
 * Created by daniel on 03/10/2016.
 */

public class UpdateLyricCommand {
    private final String newName;
    private final String newContent;
    private final String newSongNumber;
    private final String oldName;

    public UpdateLyricCommand(String newName, String newContent, String newSongNumber, String oldName){
        this.newName = newName;
        this.newContent = newContent;
        this.oldName = oldName;
        this.newSongNumber = newSongNumber;
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

    public String getNewSongNumber() {
        return newSongNumber;
    }
}
