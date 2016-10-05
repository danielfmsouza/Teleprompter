package com.easyapps.teleprompter.application.command;

/**
 * Command for Lyric object.
 * Created by daniel on 03/10/2016.
 */

public class AddLyricCommand {

    private final String name;
    private final String content;

    public AddLyricCommand(String name, String content){
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }
}
