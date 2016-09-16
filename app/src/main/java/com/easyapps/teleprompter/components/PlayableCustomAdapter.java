package com.easyapps.teleprompter.components;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.easyapps.teleprompter.ActivityCallback;
import com.easyapps.teleprompter.PrompterActivity;
import com.easyapps.teleprompter.R;
import com.easyapps.teleprompter.messages.Constants;

import java.util.List;

/**
 * Created by danielfmsouza on 12/09/2016.
 * Custom adapter that holds a play button, the song name, it configurations and one checkbox to
 * select it for deletion
 */
public class PlayableCustomAdapter extends ArrayAdapter<String> {
    private boolean[] checkedItems;
    private LayoutInflater mInflater;
    private ActivityCallback activityCallback;

    public PlayableCustomAdapter(Context context, ActivityCallback activityCallback,
                                 List<String> files) {
        super(context, NO_SELECTION);

        this.activityCallback = activityCallback;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.checkedItems = new boolean[files.size()];

        addAll(files);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder;

        if (row == null) {
            row = mInflater.inflate(R.layout.list_view_row_song, null);

            holder = new Holder();
            holder.text = (TextView) row.findViewById(R.id.tvFiles);
            holder.checkBox = (CheckBox) row.findViewById(R.id.cbDelete);
            holder.playButton = (ImageButton) row.findViewById(R.id.btnPlay);
            holder.text.setText(getItem(position));

            row.setTag(holder);

        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPrompter(position);
            }
        });

        final Holder finalHolder = holder;
        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalHolder.checkBox.setChecked(!checkedItems[position]);
            }
        });
        holder.checkBox.setChecked(checkedItems[position]);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkedItems[position] = true;
                    activityCallback.showContent();
                } else {
                    verifySelectedItems(position);
                }
            }
        });

        return row;
    }

    private void verifySelectedItems(int position) {
        checkedItems[position] = false;
        for (boolean checked : checkedItems) {
            if (checked)
                return;
        }
        activityCallback.hideContent();
    }

    public boolean isChecked(int position) {
        return checkedItems[position];
    }

    @Override
    public void remove(String s) {
        verifySelectedItems(getPosition(s));
        super.remove(s);
        notifyDataSetChanged();
    }

    static class Holder {
        TextView text;
        ImageButton playButton;
        CheckBox checkBox;
    }

    private void startPrompter(int position) {
        Intent i = new Intent(getContext(), PrompterActivity.class);

        Bundle b = new Bundle();
        b.putString(Constants.FILE_NAME_PARAM, getItem(position));
        i.putExtras(b);
        getContext().startActivity(i);
    }
}
