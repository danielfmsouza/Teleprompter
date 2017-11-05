package com.easyapps.singerpro.presentation.components;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.easyapps.singerpro.presentation.ActivityCallback;
import com.easyapps.singerpro.presentation.PrompterActivity;
import com.easyapps.singerpro.presentation.SettingsActivity;
import com.easyapps.singerpro.query.model.lyric.ConfigurationQueryModel;
import com.easyapps.singerpro.query.model.lyric.LyricQueryModel;
import com.easyapps.singerpro.query.model.lyric.LyricQueryModelComparator;
import com.easyapps.teleprompter.R;
import com.easyapps.singerpro.presentation.CreateLyricActivity;
import com.easyapps.singerpro.presentation.helper.ActivityUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by daniel on 12/09/2016.
 * Custom adapter that holds a play button, the song name, its configurations and one checkbox to
 * select it for deletion
 */
public class PlayableCustomAdapter extends ArrayAdapter<LyricQueryModel> {
    private final List<String> checkedItems;
    private final LayoutInflater mInflater;
    private String setListName;
    private final ActivityCallback activityCallback;

    public PlayableCustomAdapter(Context context, ActivityCallback activityCallback,
                                 List<LyricQueryModel> lyrics, String setListName) {
        super(context, NO_SELECTION);

        this.activityCallback = activityCallback;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.setListName = setListName;
        this.checkedItems = new ArrayList<>();

        addAll(lyrics);
        sortItemsBySongNumber();
        activityCallback.hideContent();
    }

    private void sortItemsBySongNumber() {
        sort(new LyricQueryModelComparator());
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        Holder holder;

        if (row == null) {
            row = mInflater.inflate(R.layout.list_view_row_song, null);
        }

        String lyricName = getLyricName(position);
        ConfigurationQueryModel config = getConfiguration(position);

        holder = new Holder();
        holder.text = (TextView) row.findViewById(R.id.tvFileName);
        holder.configs = (TextView) row.findViewById(R.id.tvFileConfiguration);
        holder.checkBox = (CheckBox) row.findViewById(R.id.cbDelete);
        holder.playButton = (ImageButton) row.findViewById(R.id.btnPlay);
        holder.settingsButton = (ImageButton) row.findViewById(R.id.btnSettings);
        holder.removeFromSetListButton = (ImageButton) row.findViewById(R.id.btnRemoveFromSetList);
        holder.text.setText(getLyricTitle(lyricName, config));
        holder.configs.setText(getConfigurationMessage(config));
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

        holder.removeFromSetListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFromSetList(position);
            }
        });

        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEditFile(position);
            }
        });

        final String finalLyricName = lyricName;
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkedItems.add(finalLyricName);
                    activityCallback.showContent();
                } else {
                    checkedItems.remove(finalLyricName);
                    verifySelectedItems();
                }
            }
        });

        return row;
    }

    private void removeFromSetList(int position) {
        String lyricName = getLyricName(position);
        activityCallback.removeItem(lyricName);
    }

    @NonNull
    private String getLyricTitle(String lyricName, ConfigurationQueryModel config) {
        String songNumberFormatted = String.format(Locale.getDefault(), "%02d", config.getSongNumber());

        return songNumberFormatted + " - " + lyricName;
    }

    @Nullable
    private String getLyricName(int position) {
        LyricQueryModel lyric = getItem(position);
        String lyricName = "";
        if (lyric != null)
            lyricName = lyric.getName();
        return lyricName;
    }

    private ConfigurationQueryModel getConfiguration(int position) {
        LyricQueryModel lyric = getItem(position);
        ConfigurationQueryModel configuration = null;
        if (lyric != null)
            configuration = lyric.getConfiguration();
        return configuration;
    }

    private void verifySelectedItems() {
        if (checkedItems.isEmpty()) {
            activityCallback.hideContent();
        }
    }

    public List<String> getAllCheckedItems() {
        return checkedItems;
    }

    public void unCheckAllItems() {
        checkedItems.clear();
        activityCallback.hideContent();
        notifyDataSetChanged();
    }

    public void removeAllCheckedItems() {
        for (String lyricName : checkedItems) {
            LyricQueryModel lyricQueryModel = new LyricQueryModel(lyricName, null);
            super.remove(lyricQueryModel);
        }
        unCheckAllItems();
    }

    private static class Holder {
        TextView text;
        TextView configs;
        ImageButton playButton;
        CheckBox checkBox;
        ImageButton settingsButton;
        ImageButton removeFromSetListButton;
    }

    private void startPrompter(int position) {
        startActivity(PrompterActivity.class, position);
    }

    private void startSettings(int position) {
        startActivity(SettingsActivity.class, position);
    }

    private void startEditFile(int position) {
        startActivity(CreateLyricActivity.class, position);
    }

    private void startActivity(Class clazz, int position) {
        Intent i = new Intent(getContext(), clazz);

        ActivityUtils.setFileNameParameter(getLyricName(position), i);
        ActivityUtils.setSetListNameParameter(setListName, i);
        getContext().startActivity(i);

        ((AppCompatActivity) getContext()).finish();
    }

    private String getConfigurationMessage(ConfigurationQueryModel config) {
        StringBuilder timersMessage = new StringBuilder();
        String timerMessage = getContext().getResources().getString(
                R.string.lyric_configuration_timers);

        for (int i = 0; i < config.getTimersCount(); i++) {

            int timeRunning = config.getTimerRunning()[i];
            int timeStopped = config.getTimerStopped()[i];
            timersMessage.append(String.format(timerMessage, i + 1, timeStopped, timeRunning));

            if (i + 1 < config.getTimersCount())
                timersMessage.append("\n");
        }

        return getContext().getResources().getString(R.string.lyric_configuration,
                config.getScrollSpeed(), config.getFontSize(), config.getTimersCount(),
                timersMessage.toString());
    }
}
