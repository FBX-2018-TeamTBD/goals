package com.example.cassandrakane.goalz.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;

public class DisplayUtil {

    public static int dpToPx(int dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return  dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

}
