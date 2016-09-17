package com.easyapps.teleprompter.helper;

import android.content.Intent;
import android.os.Bundle;

import com.easyapps.teleprompter.messages.Constants;

/**
 * Created by danielfmsouza on 16/09/2016.
 * Helper that provides common methods to Activities.
 */
public class ActivityUtils {

    public static void setStringParameter(String paramName, String value, Intent intent){
        Bundle b = new Bundle();
        b.putString(paramName, value);
        intent.putExtras(b);
    }

    public static String getStringParameter(String paramName, Intent intent){
        Bundle b = intent.getExtras();
        if (b != null)
            return b.getString(paramName);
        return null;
    }
}
