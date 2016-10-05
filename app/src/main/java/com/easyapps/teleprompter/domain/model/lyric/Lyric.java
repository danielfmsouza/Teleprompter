package com.easyapps.teleprompter.domain.model.lyric;

/**
 * Object that represents a Lyric from a song in the real world.
 * Created by daniel on 01/10/2016.
 */

public class Lyric {
    private String name;
    private String content;
    private Configuration configuration;

    public static Lyric newInstance(String name, String content) {
        return new Lyric(name, content);
    }

    public static Lyric newCompleteInstance(String name, String content,
                                            Configuration configuration) {
        return new Lyric(name, content, configuration);
    }

    private Lyric(String name, String content) {
        if (name == null || name.trim().equals(""))
            throw new IllegalArgumentException();

        this.name = name;
        this.content = content;
    }

    private Lyric(String name, String content, Configuration configuration) {
        this(name, content);
        this.configuration = configuration;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
