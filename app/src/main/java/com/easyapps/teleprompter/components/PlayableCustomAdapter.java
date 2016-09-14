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
 * Custom adapter that holds a play button, the song name and one checkbox to select it for deletion
 */
public class PlayableCustomAdapter extends ArrayAdapter<String> {
    private List<String> files;
    private boolean[] positionArray;
    private LayoutInflater mInflater;
    private ActivityCallback activityCallback;

    public PlayableCustomAdapter(Context context, ActivityCallback activityCallback,  List<String> files) {
        super(context, NO_SELECTION);

        this.activityCallback = activityCallback;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.files = files;
        this.positionArray = new boolean[files.size()];
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        Holder holder;

        if(row==null){
            row = mInflater.inflate(R.layout.list_view_row_songs, null);

            holder = new Holder();
            holder.text = (TextView)row.findViewById(R.id.tvFiles);
            holder.checkBox =(CheckBox)row.findViewById(R.id.cbDelete);
            holder.playButton =(ImageButton)row.findViewById(R.id.btnPlay);

            holder.text.setText(files.get(position));
            row.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("test!! Position: " + position);
                startPrompter(position);
            }
        });

        final Holder finalHolder = holder;
        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalHolder.checkBox.setChecked(!positionArray[position]);
            }
        });
        holder.checkBox.setFocusable(false);
        holder.checkBox.setChecked(positionArray[position]);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    positionArray[position] = true;
                    activityCallback.showContent();
                }else {
                    positionArray[position] = false;
                    for (boolean checked: positionArray) {
                        if (checked)
                            return;
                    }
                    activityCallback.hideContent();
                }
            }
        });

        return row;
    }

    public boolean isChecked(int position){
        return positionArray[position];
    }

    private void startPrompter(int position) {
        Intent i = new Intent(getContext(), PrompterActivity.class);

        Bundle b = new Bundle();
        b.putString(Constants.FILE_NAME_PARAM, files.get(position));
        i.putExtras(b);
        getContext().startActivity(i);
    }

    static class Holder
    {
        TextView text;
        ImageButton playButton;
        CheckBox checkBox;
    }
}
