package ru.korsander.tedrss.utils;

import android.content.res.Resources;

/**
 * Created by korsander on 13.05.2015.
 */
public class Utils {
    public static float convertDpToPixel(float dp) {
        float dest = Resources.getSystem().getDisplayMetrics().densityDpi;
        float px = dp * (dest / 160f);
        return px;
    }

    public static float convertPixelsToDp(float px) {
        float dest = Resources.getSystem().getDisplayMetrics().densityDpi;
        float dp = px / (dest / 160f);
        return dp;
    }
}
