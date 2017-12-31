package com.easyapps.singerpro.presentation.helper;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;

import com.easyapps.singerpro.presentation.MainActivity;
import com.easyapps.singerpro.presentation.PrompterActivity;
import com.easyapps.singerpro.presentation.messages.Constants;

/**
 * Created by daniel on 16/09/2016.
 * Helper that provides common methods to Activities.
 */
public class ActivityUtils {

    public static void setLyricFileNameParameter(String value, Intent intent) {
        setParameter(value, intent, Constants.FILE_NAME_PARAM);
    }

    public static void setPlaylistNameParameter(String value, Intent intent) {
        setParameter(value, intent, Constants.PLAYLIST_NAME_PARAM);
    }

    public static void setHasFinishedAnimationParameter(boolean value, Intent intent) {
        setParameter(value, intent, Constants.HAS_FINISHED_ANIMATION);
    }

    public static boolean getHasFinishedAnimationParameter(Intent intent) {
        return getBooleanParameter(intent, Constants.HAS_FINISHED_ANIMATION);
    }

    public static String getFileNameParameter(Intent intent) {
        return getStringParameter(intent, Constants.FILE_NAME_PARAM);
    }

    public static String getPlaylistNameParameter(Intent intent) {
        String setList = getStringParameter(intent, Constants.PLAYLIST_NAME_PARAM);
        return setList == null ? "" : setList;
    }

    private static void setParameter(String value, Intent intent, String paramName) {
        Bundle b = new Bundle();
        b.putString(paramName, value);
        intent.putExtras(b);
    }

    private static void setParameter(boolean value, Intent intent, String paramName) {
        Bundle b = new Bundle();
        b.putBoolean(paramName, value);
        intent.putExtras(b);
    }

    private static String getStringParameter(Intent intent, String paramName) {
        Bundle b = intent.getExtras();
        if (b != null)
            return b.getString(paramName);
        return null;
    }

    private static boolean getBooleanParameter(Intent intent, String paramName) {
        Bundle b = intent.getExtras();
        if (b != null)
            return b.getBoolean(paramName);
        return false;
    }

    public static Activity getActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    public static void backToMain(Activity currentActivity) {
        backToMain(currentActivity, null);
    }

    public static void backToMain(Activity currentActivity, String playlistName) {
        Intent i = new Intent(currentActivity, MainActivity.class);
        setPlaylistNameParameter(playlistName, i);
        currentActivity.startActivity(i);
        currentActivity.finish();
    }

    public static void backToPrompter(Activity currentActivity, String setListName, String fileName) {
        Intent i = new Intent(currentActivity, PrompterActivity.class);
        setHasFinishedAnimationParameter(true, i);
        setPlaylistNameParameter(setListName, i);
        setLyricFileNameParameter(fileName, i);
        currentActivity.startActivity(i);
        currentActivity.finish();
    }
}
