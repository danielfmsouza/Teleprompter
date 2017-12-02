package com.easyapps.singerpro.presentation.components;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.easyapps.singerpro.presentation.ActivityCallback;
import com.easyapps.singerpro.presentation.CreateLyricActivity;
import com.easyapps.singerpro.presentation.PrompterActivity;
import com.easyapps.singerpro.presentation.SettingsActivity;
import com.easyapps.singerpro.presentation.helper.ActivityUtils;
import com.easyapps.singerpro.query.model.lyric.ConfigurationQueryModel;
import com.easyapps.singerpro.query.model.lyric.LyricQueryModel;
import com.easyapps.singerpro.query.model.lyric.LyricQueryModelComparator;
import com.easyapps.teleprompter.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by daniel on 12/09/2016.
 * Custom adapter that holds a play button, the song name, its configurations and one checkbox to
 * select it for deletion
 */
public class PlayableCustomAdapter extends ArrayAdapter<LyricQueryModel> {
    private final List<String> checkedItems;
    private boolean multipleSelectionEnabled = false;
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
        final Holder holder;

        if (row == null) {
            row = mInflater.inflate(R.layout.list_view_row_song, null);
        }

        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue,
                true);
        row.setBackgroundResource(outValue.resourceId);

        final String lyricName = getLyricName(position);
        ConfigurationQueryModel config = getConfiguration(position);

        holder = new Holder();
        holder.text = row.findViewById(R.id.tvFileName);
        holder.configs = row.findViewById(R.id.tvFileConfiguration);
        holder.playButton = row.findViewById(R.id.btnPlay);
        holder.playButton.setSelected(false);
        holder.settingsButton = row.findViewById(R.id.btnSettings);
        holder.removeFromSetListButton = row.findViewById(R.id.btnRemoveFromSetList);
        holder.text.setText(getLyricTitle(lyricName, config));
        holder.configs.setText(getConfigurationMessage(config));

        row.setTag(holder);

        final Animation animation = getPlayButtonAnimation(holder, lyricName);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRowClickAction(position, holder, animation, CreateLyricActivity.class);
            }
        });
        row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                holder.playButton.startAnimation(animation);
                return true;
            }
        });
        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRowClickAction(position, holder, animation, PrompterActivity.class);
            }
        });

        holder.playButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                holder.playButton.startAnimation(animation);
                return true;
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
        return row;
    }

    @NonNull
    private Animation getPlayButtonAnimation(final Holder holder, final String lyricName) {
        final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_out);

        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation animation2 = AnimationUtils.loadAnimation(getContext(), R.anim.scale_in);
                holder.playButton.startAnimation(animation2);
                checkOrUnCheckItem(lyricName, holder);
            }
        });
        return animation;
    }

    private void checkOrUnCheckItem(String lyricName, Holder holder) {
        if (checkedItems.contains(lyricName)) {
            checkedItems.remove(lyricName);
            holder.playButton.setSelected(false);
            verifySelectedItems();
        } else {
            activityCallback.showContent();
            holder.playButton.setSelected(true);
            checkedItems.add(lyricName);
            multipleSelectionEnabled = true;
        }
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
            multipleSelectionEnabled = false;
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
        ImageButton settingsButton;
        ImageButton removeFromSetListButton;
    }

    private void startSettings(int position) {
        startActivity(SettingsActivity.class, position);
    }

    private void setRowClickAction(int position, Holder holder, Animation animation, Class clazz) {
        if (multipleSelectionEnabled) {
            holder.playButton.startAnimation(animation);
        } else {
            startActivity(clazz, position);
        }
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
