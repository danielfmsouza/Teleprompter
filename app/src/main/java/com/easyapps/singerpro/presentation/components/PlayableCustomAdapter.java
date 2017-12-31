package com.easyapps.singerpro.presentation.components;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.easyapps.singerpro.presentation.PrompterActivity;
import com.easyapps.singerpro.presentation.SettingsActivity;
import com.easyapps.singerpro.presentation.helper.ActivityUtils;
import com.easyapps.singerpro.query.model.lyric.ConfigurationQueryModel;
import com.easyapps.singerpro.query.model.lyric.LyricQueryModel;
import com.easyapps.singerpro.query.model.lyric.LyricQueryModelComparator;
import com.easyapps.teleprompter.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by daniel on 12/09/2016.
 * Custom adapter that holds a play button, the song name, its configurations and one checkbox to
 * select it for deletion
 */
public class PlayableCustomAdapter extends ArrayAdapter<LyricQueryModel> {
    private Map<Integer, Holder> mHolders = new HashMap<>();
    private boolean mMultiSelectionEnabled = false;
    private Set<Integer> mSelection = new HashSet<>();
    private String playListName;

    public PlayableCustomAdapter(Context context, int resource, List<LyricQueryModel> lyrics,
                                 String setListName, int textViewResourceId) {
        super(context, resource, textViewResourceId, lyrics);
        this.playListName = setListName;

        sortItemsBySongNumber();
    }

    public void setNewSelection(int position) {
        mSelection.add(position);
        animatePlayButton(position);
        notifyDataSetChanged();
    }

    public Set<Integer> getCurrentCheckedPositions() {
        return mSelection;
    }

    public void removeSelection(int position) {
        mSelection.remove(position);
        animatePlayButton(position);
        notifyDataSetChanged();
    }

    private void animatePlayButton(int position) {
        Holder selected = mHolders.get(position);
        if (selected != null) {
            selected.playButton.startAnimation(getPlayButtonAnimation());
        }
    }

    public void clearSelection() {
        mSelection = new HashSet<Integer>();
        notifyDataSetChanged();
        mMultiSelectionEnabled = false;
    }

    public void enableMultiSelection() {
        mMultiSelectionEnabled = true;
    }

    private void sortItemsBySongNumber() {
        sort(new LyricQueryModelComparator());
    }

    @NonNull
    @Override
    public View getView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        View row = super.getView(position, convertView, parent);
        final Holder holder = new Holder();
        holder.text = row.findViewById(R.id.tvFileName);
        holder.configs = row.findViewById(R.id.tvFileConfiguration);
        holder.playButton = row.findViewById(R.id.btnPlay);
        holder.settingsButton = row.findViewById(R.id.btnSettings);

        final String lyricName = getLyricName(position);
        ConfigurationQueryModel config = getConfiguration(position);

        holder.text.setText(getLyricTitle(lyricName, config));
        holder.configs.setText(getConfigurationMessage(config));

        if (mMultiSelectionEnabled)
            holder.settingsButton.setVisibility(View.INVISIBLE);
        else
            holder.settingsButton.setVisibility(View.VISIBLE);

        if (mSelection.contains(position)) {
            row.setBackgroundColor(getContext().getResources().getColor(R.color.colorSelectedItem));
            holder.playButton.setSelected(true);
        } else {
            row.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
            holder.playButton.setSelected(false);
        }

        holder.settingsButton.setFocusable(false);
        holder.settingsButton.setFocusableInTouchMode(false);
        holder.playButton.setFocusable(false);
        holder.playButton.setFocusableInTouchMode(false);

        holder.playButton.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRowClickAction(position, (ListView) parent, convertView);
            }
        });
        holder.settingsButton.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSettings(position);
            }
        });

        mHolders.put(position, holder);
        row.setTag(holder);
        return row;
    }

    private void setRowClickAction(int position, ListView listView, View convertView) {
        if (mMultiSelectionEnabled) {
            listView.performItemClick(
                    this.getView(position, convertView, null),
                    position,
                    this.getItemId(position));
        } else {
            startPrompter(position);
        }
    }

    @NonNull
    private Animation getPlayButtonAnimation() {
        return AnimationUtils.loadAnimation(getContext(), R.anim.scale_in);
    }

    public void setSelectedItems(ArrayList<Integer> selectedItems) {
        if (selectedItems != null && !selectedItems.isEmpty()) {
            mSelection.addAll(selectedItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    private String getLyricTitle(String lyricName, ConfigurationQueryModel config) {
        String songNumberFormatted = String.format(Locale.getDefault(), "%02d", config.getSongNumber());

        return songNumberFormatted + " - " + lyricName;
    }

    @Nullable
    public String getLyricName(int position) {
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

    public void removeAllCheckedItems() {
        for (String lyricToDelete : getCurrentCheckedLyrics()) {
            super.remove(new LyricQueryModel(lyricToDelete, null));
        }
        mSelection.clear();
    }

    private void startPrompter(int position) {
        startActivity(PrompterActivity.class, position);
    }

    private void startSettings(int position) {
        startActivity(SettingsActivity.class, position);
    }

    private void startActivity(Class clazz, int position) {
        Intent i = new Intent(getContext(), clazz);

        ActivityUtils.setLyricFileNameParameter(getLyricName(position), i);
        ActivityUtils.setPlaylistNameParameter(playListName, i);
        getContext().startActivity(i);

        ((AppCompatActivity) getContext()).finish();
    }

    private String getConfigurationMessage(ConfigurationQueryModel config) {
        return getContext().getResources().getString(R.string.lyric_configuration,
                config.getScrollSpeed(), config.getFontSize(), config.getTimersCount());
    }

    public List<String> getCurrentCheckedLyrics() {
        List<String> result = new ArrayList<>();
        for (int position : mSelection) {
            result.add(getLyricName(position));
        }
        return result;
    }

    private static class Holder {
        TextView text;
        TextView configs;
        ImageView playButton;
        ImageView settingsButton;
    }
}
