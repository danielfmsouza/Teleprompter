package com.easyapps.teleprompter.components;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.easyapps.teleprompter.ActivityCallback;
import com.easyapps.teleprompter.CreateFileActivity;
import com.easyapps.teleprompter.PrompterActivity;
import com.easyapps.teleprompter.R;
import com.easyapps.teleprompter.SettingsActivity;
import com.easyapps.teleprompter.helper.ActivityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 12/09/2016.
 * Custom adapter that holds a play button, the song name, its configurations and one checkbox to
 * select it for deletion
 */
public class PlayableCustomAdapter extends ArrayAdapter<String> {
    private final List<Integer> checkedItems;
    private final LayoutInflater mInflater;
    private final ActivityCallback activityCallback;

    public PlayableCustomAdapter(Context context, ActivityCallback activityCallback,
                                 List<String> files) {
        super(context, NO_SELECTION);

        this.activityCallback = activityCallback;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.checkedItems = new ArrayList<>();

        addAll(files);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        Holder holder;

        if (row == null) {
            row = mInflater.inflate(R.layout.list_view_row_song, null);
        }

        holder = new Holder();
        holder.text = (TextView) row.findViewById(R.id.tvFileName);
        holder.configs = (TextView) row.findViewById(R.id.tvFileConfiguration);
        holder.checkBox = (CheckBox) row.findViewById(R.id.cbDelete);
        holder.playButton = (ImageButton) row.findViewById(R.id.btnPlay);
        holder.settingsButton = (ImageButton) row.findViewById(R.id.btnSettings);
        holder.text.setText(getItem(position));
        holder.configs.setText(getLyricConfiguration(getItem(position)));
        row.setTag(holder);

        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPrompter(position);
            }
        });

        holder.settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSettings(position);
            }
        });

        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEditFile(position);
            }
        });
        holder.checkBox.setChecked(checkedItems.contains(position));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkedItems.add(position);
                    activityCallback.showContent();
                } else {
                    checkedItems.remove(Integer.valueOf(position));
                    verifySelectedItems();
                }
            }
        });

        return row;
    }

    private void verifySelectedItems() {
        if (checkedItems.isEmpty()) {
            activityCallback.hideContent();
        }
    }

    public boolean isChecked(int position) {
        return checkedItems.contains(position);
    }

    @Override
    public void remove(String s) {
        checkedItems.remove(Integer.valueOf(getPosition(s)));
        super.remove(s);
        activityCallback.hideContent();
        notifyDataSetChanged();
    }

    private static class Holder {
        TextView text;
        TextView configs;
        ImageButton playButton;
        CheckBox checkBox;
        ImageButton settingsButton;
    }

    private void startPrompter(int position) {
        startActivity(PrompterActivity.class, position);
    }

    private void startSettings(int position) {
        startActivity(SettingsActivity.class, position);
    }

    private void startEditFile(int position) {
        startActivity(CreateFileActivity.class, position);
    }

    private void startActivity(Class clazz, int position) {
        Intent i = new Intent(getContext(), clazz);
        ActivityUtils.setFileNameParameter(getItem(position), i);
        getContext().startActivity(i);
        ((AppCompatActivity) getContext()).finish();
    }

    private String getLyricConfiguration(String fileName) {
        String scrollSpeedPrefKey = getStringFromResource(R.string.pref_key_scrollSpeed);
        String totalTimersPrefKey = getStringFromResource(R.string.pref_key_totalTimers);
        String textSizePrefKey = getStringFromResource(R.string.pref_key_textSize);

        int scrollSpeedDefault = getIntFromResource(R.integer.number_default_value_scroll_speed);
        int totalTimersDefault = getIntFromResource(R.integer.number_min_value_count_timers);
        int textSizeDefault = getIntFromResource(R.integer.number_default_value_text_size);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int scrollSpeed = preferences.getInt(scrollSpeedPrefKey + fileName, scrollSpeedDefault);
        int totalTimers = preferences.getInt(totalTimersPrefKey + fileName, totalTimersDefault);
        int textSize = preferences.getInt(textSizePrefKey + fileName, textSizeDefault);

        StringBuilder timersMessage = getTimersConfigMessage(fileName, preferences, totalTimers);

        return getContext().getResources().getString(R.string.lyric_configuration, scrollSpeed,
                textSize, totalTimers, timersMessage.toString());
    }

    @NonNull
    private StringBuilder getTimersConfigMessage(String fileName, SharedPreferences preferences,
                                                 int totalTimers) {
        String timeRunningPrefKey = getStringFromResource(R.string.pref_key_timeRunning);
        String timeWaitingPrefKey = getStringFromResource(R.string.pref_key_timeWaiting);
        int timeRunningDefault = getIntFromResource(R.integer.number_min_value_timer);
        int timeWaitingDefault = getIntFromResource(R.integer.number_min_value_timer);

        String timerMessage = getContext().getResources().getString(
                R.string.lyric_configuration_timers);
        StringBuilder timersMessage = new StringBuilder();

        for (int i = 0; i < totalTimers; i++) {
            int timeRunning = preferences.getInt(timeRunningPrefKey + fileName + i, timeRunningDefault);
            int timeWaiting = preferences.getInt(timeWaitingPrefKey + fileName + i, timeWaitingDefault);
            timersMessage.append(String.format(timerMessage, i + 1, timeWaiting, timeRunning));

            if (i + 1 < totalTimers)
                timersMessage.append("\n");
        }
        return timersMessage;
    }

    private int getIntFromResource(int resource) {
        return getContext().getResources().getInteger(resource);
    }

    @NonNull
    private String getStringFromResource(int resource) {
        return getContext().getResources().getString(resource);
    }
}
