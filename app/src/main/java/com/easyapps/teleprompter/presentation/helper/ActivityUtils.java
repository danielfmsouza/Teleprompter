package com.easyapps.teleprompter.presentation.helper;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;

import com.easyapps.teleprompter.presentation.MainActivity;
import com.easyapps.teleprompter.presentation.messages.Constants;

/**
 * Created by daniel on 16/09/2016.
 * Helper that provides common methods to Activities.
 */
public class ActivityUtils {

    public static void setFileNameParameter(String value, Intent intent) {
        setParameter(value, intent, Constants.FILE_NAME_PARAM);
    }

    public static void setSetListNameParameter(String value, Intent intent) {
        setParameter(value, intent, Constants.SET_LIST_NAME_PARAM);
    }

    public static String getFileNameParameter(Intent intent) {
        return getParameter(intent, Constants.FILE_NAME_PARAM);
    }

    public static String getSetListNameParameter(Intent intent) {
        String setList = getParameter(intent, Constants.SET_LIST_NAME_PARAM);
        return setList == null ? "" : setList;
    }

    private static void setParameter(String value, Intent intent, String paramName) {
        Bundle b = new Bundle();
        b.putString(paramName, value);
        intent.putExtras(b);
    }

    private static String getParameter(Intent intent, String paramName) {
        Bundle b = intent.getExtras();
        if (b != null)
            return b.getString(paramName);
        return null;
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

    public static void backToMain(Activity currentActivity, String setListName) {
        Intent i = new Intent(currentActivity, MainActivity.class);
        setSetListNameParameter(setListName, i);
        currentActivity.startActivity(i);
        currentActivity.finish();
    }
}
