package com.easyapps.singerpro.query.model.lyric;

import java.util.Comparator;

public class LyricQueryModelComparator implements Comparator<LyricQueryModel> {
    @Override
    public int compare(LyricQueryModel firstObj, LyricQueryModel secondObj) {
        if (firstObj.getOrder() > secondObj.getOrder())
            return 1;
        else if (firstObj.getOrder() < secondObj.getOrder())
            return -1;
        return 0;
    }
}
