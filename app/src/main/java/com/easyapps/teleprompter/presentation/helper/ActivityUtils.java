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
        Bundle b = new Bundle();
        b.putString(Constants.FILE_NAME_PARAM, value);
        intent.putExtras(b);
    }

    public static String getFileNameParameter(Intent intent) {
        Bundle b = intent.getExtras();
        if (b != null)
            return b.getString(Constants.FILE_NAME_PARAM);
        return null;
    }

    public static Activity getActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }

    public static void backToMain(Activity currentActivity) {
        Intent i = new Intent(currentActivity, MainActivity.class);
        currentActivity.startActivity(i);
        currentActivity.finish();
    }
}
