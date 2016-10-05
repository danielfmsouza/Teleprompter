package com.easyapps.teleprompter.application.command;

/**
 * Command for update Lyric.
 * Created by daniel on 03/10/2016.
 */

public class UpdateLyricCommand {
    private final String newName;
    private final String newContent;
    private final String oldName;

    public UpdateLyricCommand(String newName, String newContent, String oldName){
        this.newName = newName;
        this.newContent = newContent;
        this.oldName = oldName;
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
}
