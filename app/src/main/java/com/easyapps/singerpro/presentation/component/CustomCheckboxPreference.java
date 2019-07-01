package com.easyapps.singerpro.presentation.component;

import android.app.Activity;
import android.content.Context;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;

import com.easyapps.singerpro.presentation.helper.ActivityUtils;

public class CustomCheckboxPreference extends CheckBoxPreference {
    public CustomCheckboxPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCheckboxKey(context);
    }

    public CustomCheckboxPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCheckboxKey(context);
    }

    public CustomCheckboxPreference(Context context) {
        super(context);
        setCheckboxKey(context);
    }

    private void setCheckboxKey(Context context){
        Activity parent = ((Activity)context);
        String fileName = ActivityUtils.getLyricFileNameParameter(parent.getIntent());
        setKey(getKey() + (fileName == null ? "" : fileName));
    }
}
