package com.easyapps.teleprompter.components;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Toast;

import com.easyapps.teleprompter.R;
import com.easyapps.teleprompter.helper.ActivityUtils;

/**
 * Created by daniel on 17/09/2016.
 * Preference that refreshes the timers accordingly the value set and captured on "onDialogClosed"
 * event.
 */
public class TimersCountPickerPreference extends NumberPickerPreference {
    private final Activity mContext;
    private String mFileName;

    public TimersCountPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = (Activity) context;

        String fileName = ActivityUtils.getFileNameParameter(
                mContext.getIntent());
        if (fileName != null) {
            mFileName = fileName;
        } else
            ActivityUtils.showMessage(R.string.file_not_found, mContext, Toast.LENGTH_SHORT);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            mContext.getFragmentManager().beginTransaction().replace(android.R.id.content,
                    TimerPreferenceFragment.newInstance(mFileName)).commit();
        }
    }
}