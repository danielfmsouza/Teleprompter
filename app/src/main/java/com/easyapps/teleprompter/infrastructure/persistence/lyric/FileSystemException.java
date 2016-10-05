package com.easyapps.teleprompter.infrastructure.persistence.lyric;

/**
 * Exception thrown when something happens with File System.
 * Created by daniel on 01/10/2016.
 */

public class FileSystemException extends Exception {
    FileSystemException(Exception cause){
        super(cause);
    }

    FileSystemException(String message){
        super(message);
    }
}
