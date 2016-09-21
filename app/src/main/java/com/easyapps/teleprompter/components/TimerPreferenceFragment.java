package com.easyapps.teleprompter.components;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.easyapps.teleprompter.R;
import com.easyapps.teleprompter.messages.Constants;

/**
 * Created by daniel on 20/09/2016.
 * Fragment that holds the set of waiting and running timers accordingly the total timers chose by
 * user.
 */
public class TimerPreferenceFragment extends PreferenceFragment {

    private Context mContext;

    public static TimerPreferenceFragment newInstance(String fileName) {

        TimerPreferenceFragment fragment = new TimerPreferenceFragment();

        Bundle b = new Bundle();
        b.putString(Constants.FILE_NAME_PARAM, fileName);
        fragment.setArguments(b);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            this.mContext = activity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getArguments();
        String fileName = b.getString(Constants.FILE_NAME_PARAM);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        PreferenceScreen pScreen = getPreferenceManager().createPreferenceScreen(mContext);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        int totalTimers = getTotalTimers(fileName, preferences);
        final int orderWaiting = 99;
        final int orderRunning = 100;

        // creating each timer configuration for each timer.
        for (int i = 0; i < totalTimers; i++) {
            setTimerStopped(pScreen, orderWaiting, fileName, i, preferences);
            setTimerRunning(pScreen, orderRunning, fileName, i, preferences);
            setPreferenceScreen(pScreen);
        }
        if (totalTimers > 0)
            addPreferencesFromResource(R.xml.preferences);
    }

    private int getTotalTimers(String fileName, SharedPreferences preferences) {
        int totalTimersDefault = getResources().getInteger(R.integer.number_min_value_count_timers);
        String totalTimersPrefKey = getResources().getString(R.string.pref_key_totalTimers);
        return preferences.getInt(totalTimersPrefKey + fileName, totalTimersDefault);
    }

    private void setTimerRunning(PreferenceScreen pScreen, int orderRunning,
                                 String fileName, int i, SharedPreferences preferences) {
        NumberPickerPreference npTimeRunning = new NumberPickerPreference(mContext);
        String keyRunning = getResources().getString(R.string.pref_key_timeRunning);
        String fullKey = keyRunning + fileName + i;

        npTimeRunning.setKey(fullKey);
        npTimeRunning.setTitle(getResources().getString(R.string.pref_title_timeRunning, i + 1));
        npTimeRunning.setSummary(R.string.pref_summary_timeRunning);
        npTimeRunning.setOrder(i + orderRunning);

        int timeRunningDefault = getResources().getInteger(R.integer.number_min_value_timer);
        int timeRunning = preferences.getInt(fullKey, timeRunningDefault);
        npTimeRunning.setValue(timeRunning);

        pScreen.addPreference(npTimeRunning);
    }

    private void setTimerStopped(PreferenceScreen pScreen, int orderWaiting,
                                 String fileName, int i, SharedPreferences preferences) {
        NumberPickerPreference npTimeWaiting = new NumberPickerPreference(mContext);
        String keyWaiting = getResources().getString(R.string.pref_key_timeWaiting);
        String fullKey = keyWaiting + fileName + i;

        npTimeWaiting.setKey(fullKey);
        npTimeWaiting.setTitle(getResources().getString(R.string.pref_title_timeWaiting, i + 1));
        npTimeWaiting.setSummary(R.string.pref_summary_timeWaiting);
        npTimeWaiting.setOrder(i + orderWaiting);

        int timeWaitingDefault = getResources().getInteger(R.integer.number_min_value_timer);
        int timeWaiting = preferences.getInt(fullKey, timeWaitingDefault);
        npTimeWaiting.setValue(timeWaiting);

        pScreen.addPreference(npTimeWaiting);
    }
}