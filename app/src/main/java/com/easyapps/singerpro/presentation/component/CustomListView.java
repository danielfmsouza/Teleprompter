package com.easyapps.singerpro.presentation.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

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

    public View getViewByPosition(int pos) {
        final int firstListItemPosition = getFirstVisiblePosition();
        final int lastListItemPosition = getLastVisiblePosition();

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return getAdapter().getView(pos, null, this);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return getChildAt(childIndex);
        }
    }
}