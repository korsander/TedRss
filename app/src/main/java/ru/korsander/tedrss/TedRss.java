package ru.korsander.tedrss;

import android.app.Application;
import android.content.Context;

/**
 * Created by korsander on 06.05.2015.
 */
public class TedRss extends Application{
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
