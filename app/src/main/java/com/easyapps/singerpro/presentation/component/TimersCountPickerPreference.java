package com.easyapps.singerpro.presentation.component;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Toast;

import com.easyapps.singerpro.presentation.fragment.SettingsPreferenceFragment;
import com.easyapps.singerpro.R;
import com.easyapps.singerpro.presentation.helper.ActivityUtils;

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

        String fileName = ActivityUtils.getLyricFileNameParameter(
                mContext.getIntent());
        if (fileName != null) {
            mFileName = fileName;
        } else {
            String message = mContext.getResources().getString(R.string.file_not_found);
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            mContext.getFragmentManager().beginTransaction().replace(android.R.id.content,
                    SettingsPreferenceFragment.newInstance(mFileName)).commit();
        }
    }
}