package com.easyapps.singerpro.presentation.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.easyapps.singerpro.R;
import com.easyapps.singerpro.presentation.MainActivity;

/**
 * Created by daniel on 16/09/2016.
 * Helper that provides common methods to Activities.
 */
public class ActivityUtils {
    private static final String FILE_NAME = "fileName";
    private static final String CALLER_ACTIVITY = "callerActivity";

    public static void setLyricFileNameParameter(String value, Intent intent) {
        setParameter(value, intent, FILE_NAME);
    }

    public static String getLyricFileNameParameter(Intent intent) {
        return getStringParameter(intent, FILE_NAME);
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

    private static String getStringParameter(Intent intent, String paramName) {
        Bundle b = intent.getExtras();
        if (b != null)
            return b.getString(paramName);
        return null;
    }

    public static void backToMain(Activity currentActivity) {
        Intent i = new Intent(currentActivity, MainActivity.class);
        currentActivity.startActivity(i);
        currentActivity.finish();
    }

    public static void startActivity(Activity currentActivity, String playlistName,
                                     Class activityToStart){
        Intent i = new Intent(currentActivity.getApplicationContext(), activityToStart);

        String lyricName = ActivityUtils.getLyricFileNameParameter(currentActivity.getIntent());

        setLyricFileNameParameter(lyricName, i);
        setCurrentPlaylistName(playlistName, currentActivity.getApplicationContext());
        setParameter(currentActivity.getClass().getName(), i, CALLER_ACTIVITY);

        currentActivity.startActivity(i);
        currentActivity.finish();
    }

    public static void backToCaller(Activity currentActivity, String lyricName) {
        String caller = getStringParameter(currentActivity.getIntent(), CALLER_ACTIVITY);

        if (caller == null || caller.isEmpty()) {
            backToMain(currentActivity);
        } else {
            Class callerActivityClass = getCallerActivityClass(currentActivity, caller);
            if (callerActivityClass == null) return;

            Intent i = new Intent(currentActivity, callerActivityClass);
            setLyricFileNameParameter(lyricName, i);

            currentActivity.startActivity(i);
            currentActivity.finish();
        }
    }

    @Nullable
    private static Class getCallerActivityClass(Activity currentActivity, String caller) {
        Class callerActivityClass = null;
        try {
            callerActivityClass = Class.forName(caller);
        } catch (ClassNotFoundException e) {
            Toast.makeText(currentActivity.getApplicationContext(),
                    R.string.internal_error, Toast.LENGTH_LONG).show();
        }
        return callerActivityClass;
    }
}