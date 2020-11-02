package com.easyapps.singerpro.presentation.component;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.easyapps.singerpro.R;
import com.easyapps.singerpro.presentation.helper.ActivityUtils;

public class CustomListView extends ListView {

    public CustomListView(Context context) {
        super(context);
    }

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public int getVerticalScrollOffset() {
        if (getFirstVisiblePosition() < 0) {
            return 0;
        }
        View firstElement = getChildAt(getFirstVisiblePosition());
        if (firstElement == null) {
            return 0;
        }
        return -firstElement.getTop() + getFirstVisiblePosition() * firstElement.getHeight();
    }


}