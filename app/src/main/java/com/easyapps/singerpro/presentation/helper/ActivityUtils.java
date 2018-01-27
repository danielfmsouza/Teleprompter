package com.easyapps.singerpro.presentation.helper;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.easyapps.singerpro.presentation.MainActivity;
import com.easyapps.singerpro.presentation.MaintainLyricActivity;
import com.easyapps.singerpro.presentation.PrompterActivity;
import com.easyapps.singerpro.presentation.messages.Constants;
import com.easyapps.teleprompter.R;

/**
 * Created by daniel on 16/09/2016.
 * Helper that provides common methods to Activities.
 */
public class ActivityUtils {

    public static void setLyricFileNameParameter(String value, Intent intent) {
        setParameter(value, intent, Constants.FILE_NAME_PARAM);
    }

    private static void setHasFinishedAnimationParameter(boolean value, Intent intent) {
        setParameter(value, intent, Constants.HAS_FINISHED_ANIMATION);
    }

    public static boolean getHasFinishedAnimationParameter(Intent intent) {
        return getBooleanParameter(intent, Constants.HAS_FINISHED_ANIMATION);
    }

    public static String getFileNameParameter(Intent intent) {
        return getStringParameter(intent, Constants.FILE_NAME_PARAM);
    }

    public static void setCurrentPlaylistName(String playlistName, Context context) {
        setPreference(playlistName, context, R.string.pref_key_currentPlaylistName);
    }

    public static void setCurrentSelectedLyric(int position, Context context) {
        setPreference(position, context, R.string.pref_key_currentSelectedLyric);
    }

    public static void setIsNewLyric(boolean value, Context context) {
        setPreference(value, context, R.string.pref_key_isNewLyric);
    }

    public static void setClickedOnLyric(boolean clickedOnLyric, Context context) {
        setPreference(clickedOnLyric, context, R.string.pref_key_clickedOnLyric);
    }

    private static void setPreference(String value, Context context, int resource) {
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(context);

        String varName = context.getResources().getString(resource);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(varName, value);
        editor.apply();
    }

    private static void setPreference(int value, Context context, int resource) {
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(context);

        String varName = context.getResources().getString(resource);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(varName, value);
        editor.apply();
    }

    private static void setPreference(boolean value, Context context, int resource) {
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(context);

        String varName = context.getResources().getString(resource);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(varName, value);
        editor.apply();
    }

    public static String getCurrentPlaylistName(Context context) {
        return getStringPreference(R.string.pref_key_currentPlaylistName, context);
    }

    public static int getCurrentSelectedLyric(Context context) {
        return getIntPreference(R.string.pref_key_currentSelectedLyric, context);
    }

    public static boolean isNewLyric(Context context) {
        return getBoolPreference(R.string.pref_key_isNewLyric, context);
    }

    public static boolean isClickedOnLyric(Context context) {
        return getBoolPreference(R.string.pref_key_clickedOnLyric, context);
    }

    @NonNull
    private static String getStringPreference(int resource, Context context) {
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPref.getString(
                context.getResources().getString(resource), "");
    }

    private static boolean getBoolPreference(int resource, Context context) {
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPref.getBoolean(
                context.getResources().getString(resource), false);
    }

    private static int getIntPreference(int resource, Context context) {
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPref.getInt(
                context.getResources().getString(resource), 0);
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
        Intent i = new Intent(currentActivity, MainActivity.class);
        currentActivity.startActivity(i);
        currentActivity.finish();
    }

    public static void backToMaintainLyric(Activity currentActivity, String lyricName) {
        Intent i = new Intent(currentActivity, MaintainLyricActivity.class);
        setLyricFileNameParameter(lyricName, i);
        currentActivity.startActivity(i);
        currentActivity.finish();
    }

    public static void backToPrompter(Activity currentActivity, String setListName, String fileName) {
        Intent i = new Intent(currentActivity, PrompterActivity.class);
        setHasFinishedAnimationParameter(true, i);
        setLyricFileNameParameter(fileName, i);
        currentActivity.startActivity(i);
        currentActivity.finish();
    }
}
