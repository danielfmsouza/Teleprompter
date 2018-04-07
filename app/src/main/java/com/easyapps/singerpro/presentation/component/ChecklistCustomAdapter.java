package com.easyapps.singerpro.presentation.component;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Daniel on 2018-03-04.
 * Custom adapter that holds a simple selectable list derived from ArrayAdapter
 */

public class ChecklistCustomAdapter extends ArrayAdapter<String> {
    public ChecklistCustomAdapter(Context context, int resource, List<String> names,
                                  int textViewResourceId) {
        super(context, resource, textViewResourceId, names);
    }

    @NonNull
    @Override
    public View getView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        View row = super.getView(position, convertView, parent);
        final Holder holder = new Holder();
        holder.text = new EditText(getContext());
        holder.selected = new CheckBox(getContext());

        holder.text.setText(getItem(position));
        holder.selected.setVisibility(View.VISIBLE);
        holder.selected.setChecked(false);

        row.setTag(holder);
        return row;
    }

    private static class Holder {
        TextView text;
        CheckBox selected;
    }
}

